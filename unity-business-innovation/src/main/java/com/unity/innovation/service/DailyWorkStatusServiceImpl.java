
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.UserTypeEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.HashRedisUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.dao.DailyWorkStatusDao;
import com.unity.innovation.entity.*;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * ClassName: DailyWorkStatusService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:01
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class DailyWorkStatusServiceImpl extends BaseServiceImpl<DailyWorkStatusDao, DailyWorkStatus> {

    @Resource
    private DailyWorkKeywordServiceImpl keywordService;

    @Resource
    private AttachmentServiceImpl attachmentService;

    @Resource
    private SysCfgServiceImpl sysCfgService;

    @Resource
    private HashRedisUtils hashRedisUtils;

    @Resource
    private DailyWorkPackageServiceImpl workMPackageService;

    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 返回数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:43
     */
    public IPage<DailyWorkStatus> listByPage(PageEntity<DailyWorkStatus> search) {
        LambdaQueryWrapper<DailyWorkStatus> lqw = new LambdaQueryWrapper<>();
        // 管理员并且不是超级管理员  工作动态三个列表数据 返回空数据
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (UserTypeEnum.ADMIN.getId().equals(customer.getUserType()) && customer.getIsSuperAdmin() == YesOrNoEnum.NO.getType()) {
            lqw.eq(DailyWorkStatus::getId,YesOrNoEnum.NO.getType());
            IPage<DailyWorkStatus> list1 = page(search.getPageable(),  lqw);
            return list1;
        }
        //普通账号 只看自己单位的
        if (UserTypeEnum.ORDINARY.getId().equals(customer.getUserType())) {
            lqw.eq(DailyWorkStatus::getIdRbacDepartment, customer.getIdRbacDepartment());
        }
        //标题
        if (StringUtils.isNotBlank(search.getEntity().getTitle())) {
            lqw.like(DailyWorkStatus::getTitle, search.getEntity().getTitle());
        }
        //主题
        if (StringUtils.isNotBlank(search.getEntity().getTheme())) {
            lqw.like(DailyWorkStatus::getTheme, search.getEntity().getTheme());
        }
        //工作类别
        if (search.getEntity().getType() != null) {
            lqw.eq(DailyWorkStatus::getType, search.getEntity().getType());
        }
        //关键字
        if (search.getEntity().getKeyWord() != null) {
            //关键词中间表查询工作动态的数据
            List<DailyWorkKeyword> keyList = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                    .eq(DailyWorkKeyword::getIdKeyword, search.getEntity().getKeyWord()));
            List<Long> ids = keyList.stream().map(DailyWorkKeyword::getIdDailyWorkStatus).collect(Collectors.toList());
            lqw.in(DailyWorkStatus::getId, ids);
        }
        //状态
        if (search.getEntity().getState() != null) {
            lqw.eq(DailyWorkStatus::getState, search.getEntity().getState());
        }
        //创建时间
        if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
            //gt 大于 lt 小于
            lqw.lt(DailyWorkStatus::getGmtCreate, end);
            lqw.gt(DailyWorkStatus::getGmtCreate, begin);
        }
        //更新时间
        if (StringUtils.isNotBlank(search.getEntity().getModifiedTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getModifiedTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getModifiedTime(), false);
            //gt 大于 lt 小于
            lqw.gt(DailyWorkStatus::getGmtModified, begin);
            lqw.lt(DailyWorkStatus::getGmtModified, end);
        }
        //管理员 单位数据
        if (search.getEntity().getIdRbacDepartment() != null) {
            lqw.eq(DailyWorkStatus::getIdRbacDepartment, search.getEntity().getIdRbacDepartment());
        }
        //排序规则      未提请发布在前，已提请发布在后；未提请发布按创建时间倒序，已提请发布按提请时间倒序
        lqw.last(" ORDER BY state ASC , gmt_create desc ");
        IPage<DailyWorkStatus> list = page(search.getPageable(), lqw);
        if (CollectionUtils.isEmpty(list.getRecords())) {
            return list;
        }
        List<Long> ids = list.getRecords().stream().map(DailyWorkStatus::getId).collect(Collectors.toList());
        //工作类别
        List<SysCfg> typeList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, SysCfgEnum.ONE.getId()));
        Map<Long, String> typeNames = typeList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        //关键字
        List<DailyWorkKeyword> keys = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                .in(DailyWorkKeyword::getIdDailyWorkStatus, ids));
        List<SysCfg> keyList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, SysCfgEnum.TWO.getId()));
        Map<Long, String> keyNames = keyList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        keys.forEach(k->k.setKeyName(keyNames.get(k.getIdKeyword())));
        Map<Long, List<DailyWorkKeyword>> keyStr = keys.stream().collect(Collectors.groupingBy(DailyWorkKeyword::getIdDailyWorkStatus));
        list.getRecords().forEach(dwk -> {
            //工作类别
            dwk.setTypeName(typeNames.get(dwk.getType()));
            //关键字
            List<DailyWorkKeyword> keyList1 = keyStr.get((dwk.getId()));
            String keysStr=keyList1.stream().map(DailyWorkKeyword::getKeyName).collect(joining(" "));
            dwk.setKeyWordStr(keysStr);
            //redis获取单位名称
            if (hashRedisUtils.getFieldValueByFieldName
                    (RedisConstants.DEPARTMENT + dwk.getIdRbacDepartment(), "name") != null) {
                dwk.setDeptName(hashRedisUtils.getFieldValueByFieldName
                        (RedisConstants.DEPARTMENT + dwk.getIdRbacDepartment(), "name")
                );
            }
        });
        return list;
    }


    /**
     * 功能描述 需求包 添加基础数据 列表
     * @param search 查询条件
     * @return 分页信息
     * @author gengzhiqiang
     * @date 2019/9/20 14:50
     */
     IPage<DailyWorkStatus> listForContent(PageEntity<DailyWorkStatus> search) {
        LambdaQueryWrapper<DailyWorkStatus> lqw = new LambdaQueryWrapper<>();
        //标题
        if (StringUtils.isNotBlank(search.getEntity().getTitle())) {
            lqw.like(DailyWorkStatus::getTitle, search.getEntity().getTitle());
        }
        //主题
        if (StringUtils.isNotBlank(search.getEntity().getTheme())) {
            lqw.like(DailyWorkStatus::getTheme, search.getEntity().getTheme());
        }
        //工作类别
        if (search.getEntity().getType() != null) {
            lqw.eq(DailyWorkStatus::getType, search.getEntity().getType());
        }
        //关键字
        if (search.getEntity().getKeyWord() != null) {
            //关键词中间表查询工作动态的数据
            List<DailyWorkKeyword> keyList = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                    .eq(DailyWorkKeyword::getIdKeyword, search.getEntity().getKeyWord()));
            List<Long> ids = keyList.stream().map(DailyWorkKeyword::getIdDailyWorkStatus).collect(Collectors.toList());
            lqw.in(DailyWorkStatus::getId, ids);
        }
         //状态
         if (search.getEntity().getIdPackage() != null) {
             List<DailyWorkPackage> list = workMPackageService.list(new LambdaQueryWrapper<DailyWorkPackage>()
                     .eq(DailyWorkPackage::getIdPackage, search.getEntity().getIdPackage()));
             List<Long> ids = list.stream().map(DailyWorkPackage::getIdDailyWorkStatus).collect(Collectors.toList());
             lqw.and(w -> w
                     .in(DailyWorkStatus::getId, ids)
                     .or()
                     .eq(DailyWorkStatus::getState, YesOrNoEnum.NO.getType()));
         } else {
             lqw.eq(DailyWorkStatus::getState, YesOrNoEnum.NO.getType());
         }
        //创建时间
        if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
            //gt 大于 lt 小于
            lqw.gt(DailyWorkStatus::getGmtCreate, begin);
            lqw.lt(DailyWorkStatus::getGmtCreate, end);
        }
        //本单位数据 管理员 列表数据都要显示
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (customer.getIdRbacDepartment() != null ) {
            lqw.eq(DailyWorkStatus::getIdRbacDepartment, customer.getIdRbacDepartment());
        }
        //排序规则  创建时间倒序
        lqw.orderByDesc(DailyWorkStatus::getGmtCreate);
        IPage<DailyWorkStatus> list = page(search.getPageable(), lqw);
        if (CollectionUtils.isEmpty(list.getRecords())) {
            return list;
        }
        List<Long> ids = list.getRecords().stream().map(DailyWorkStatus::getId).collect(Collectors.toList());
        //工作类别
        List<SysCfg> typeList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, SysCfgEnum.ONE.getId()));
        Map<Long, String> typeNames = typeList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        //关键字
        List<DailyWorkKeyword> keys = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                .in(DailyWorkKeyword::getIdDailyWorkStatus, ids));
        List<SysCfg> keyList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, SysCfgEnum.TWO.getId()));
        Map<Long, String> keyNames = keyList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        keys.forEach(k->k.setKeyName(keyNames.get(k.getIdKeyword())));
        Map<Long, List<DailyWorkKeyword>> keyStr = keys.stream().collect(Collectors.groupingBy(DailyWorkKeyword::getIdDailyWorkStatus));
        list.getRecords().forEach(dwk -> {
            //关键字
            List<DailyWorkKeyword> keyList1 = keyStr.get((dwk.getId()));
            String keysStr=keyList1.stream().map(DailyWorkKeyword::getKeyName).collect(joining(" "));
            dwk.setKeyWordStr(keysStr);
            //工作类别
            dwk.setTypeName(typeNames.get(dwk.getType()));
            //redis获取单位名称
            if (hashRedisUtils.getFieldValueByFieldName
                    (RedisConstants.DEPARTMENT + dwk.getIdRbacDepartment(), "name") != null) {
                dwk.setDeptName(hashRedisUtils.getFieldValueByFieldName
                        (RedisConstants.DEPARTMENT + dwk.getIdRbacDepartment(), "name")
                );
            }
        });
        return list;
    }

    /**
     * 功能描述 新增编辑接口
     * @param entity 实体对象
     * @author gengzhiqiang
     * @date 2019/9/17 15:49
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEntity(DailyWorkStatus entity) {
        if (entity.getId() == null) {
            Customer customer = LoginContextHolder.getRequestAttributes();
            if (!UserTypeEnum.ORDINARY.getId().equals(customer.getUserType())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("该用户无操作数据权限").build();
            }
            entity.setIdRbacDepartment(customer.getIdRbacDepartment());
            //新增
            List<Long> keys = entity.getKeyWordList();
            entity.setAttachmentCode(UUIDUtil.getUUID().replace("-", ""));
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
            save(entity);
            //处理关键字
            keywordService.updateKeyWord(entity.getId(), keys);
        } else {
            //编辑
            DailyWorkStatus vo = getById(entity.getId());
            if (vo == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到对象").build();
            }
            if (YesOrNoEnum.YES.getType()==vo.getState()) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("已提请发布状态下数据不可编辑").build();
            }
            List<Long> keys = entity.getKeyWordList();
            keywordService.updateKeyWord(entity.getId(), keys);
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachmentList());
            updateById(entity);
        }
    }

    /**
     * 功能描述 详情接口
     * @param entity 对象
     * @return com.unity.innovation.entity.DailyWorkStatus 对象
     * @author gengzhiqiang
     * @date 2019/9/17 16:03
     */
    public DailyWorkStatus detailById(DailyWorkStatus entity) {
        DailyWorkStatus vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        List<Long> ids = Lists.newArrayList();
        ids.add(vo.getType());
        List<DailyWorkKeyword> list = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                .eq(DailyWorkKeyword::getIdDailyWorkStatus, vo.getId()));
        ids.addAll(list.stream().map(DailyWorkKeyword::getIdKeyword).collect(Collectors.toList()));
        List<SysCfg> allList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .in(SysCfg::getId, ids));
        List<SysCfg> one = allList.stream().filter(i -> SysCfgEnum.ONE.getId().equals(i.getCfgType())).collect(Collectors.toList());
        List<SysCfg> two = allList.stream().filter(i -> SysCfgEnum.TWO.getId().equals(i.getCfgType())).collect(Collectors.toList());
        String typeName = one.stream().map(SysCfg::getCfgVal).collect(joining());
        String keyName = two.stream().map(SysCfg::getCfgVal).collect(joining(" "));
        vo.setTypeName(typeName);
        vo.setKeyWordStr(keyName);
        vo.setKeyWordList(list.stream().map(DailyWorkKeyword::getIdKeyword).collect(Collectors.toList()));
        List<Map<String, Object>> val = JsonUtil.<SysCfg>ObjectToList(two,
                (m, key) -> {
                }, SysCfg::getId, SysCfg::getCfgVal);
        vo.setKeyCfgList(val);
        //附件
        List<Attachment> attachmentList=attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode,vo.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)){
            vo.setAttachmentList(attachmentList);
        }
        return vo;
    }

    /**
     * 功能描述 批量删除
     * @param ids id集合
     * @author gengzhiqiang
     * @date 2019/9/17 16:14
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeById(List<Long> ids) {
        List<DailyWorkStatus> list1 = list(new LambdaQueryWrapper<DailyWorkStatus>()
                .in(DailyWorkStatus::getId, ids));
        if (CollectionUtils.isEmpty(list1)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("存在已删除数据,请刷新页面后重新操作").build();
        }
        if (CollectionUtils.isNotEmpty(list1)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("已提请发布状态下数据不可删除").build();
        }
        Collection<DailyWorkStatus> list = listByIds(ids);
        List<String> codes = list.stream().map(DailyWorkStatus::getAttachmentCode).collect(Collectors.toList());
        //附件表
        attachmentService.remove(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codes));
        //关键字表
        keywordService.remove(new LambdaQueryWrapper<DailyWorkKeyword>().in(DailyWorkKeyword::getIdDailyWorkStatus, ids));
        removeByIds(ids);

    }

}
