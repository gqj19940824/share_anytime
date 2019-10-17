
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.entity.*;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.*;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.dao.PmInfoDeptDao;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class PmInfoDeptServiceImpl extends BaseServiceImpl<PmInfoDeptDao, PmInfoDept> {

    @Resource
    private DicUtils dicUtils;
    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private PmInfoDeptLogServiceImpl logService;
    @Resource
    private InfoDeptSatbServiceImpl satbService;
    @Resource
    private InfoDeptYzgtServiceImpl yzgtService;



    /**
    * 查询条件封装
    *
    * @param entity 实体
    * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.PmInfoDept>
    * @author JH
    * @date 2019/10/17 11:14
    */
    public LambdaQueryWrapper<PmInfoDept> wrapper(PmInfoDept entity) {
        LambdaQueryWrapper<PmInfoDept> ew = new LambdaQueryWrapper<>();
        Customer customer = LoginContextHolder.getRequestAttributes();
        List<Long> roleList = customer.getRoleList();
        if (entity != null) {
            //提交时间
            if (StringUtils.isNotBlank(entity.getSubmitTime())) {
                long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
                ew.lt(PmInfoDept::getGmtSubmit, end);
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
                ew.gt(PmInfoDept::getGmtSubmit, begin);
            }
            //标识模块
            if (StringUtils.isNotBlank(entity.getCategory())) {
                ew.eq(PmInfoDept::getIdRbacDepartment, getDepartmentId(entity.getCategory()));
            } else {
                //非宣传部审批角色必传category
                if (!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
                    throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                            .message("提交单位不能为空").build();
                }
            }
            //状态
            if (entity.getStatus() != null) {
                ew.eq(PmInfoDept::getStatus, entity.getStatus());
            }

            //宣传部审批角色不查看 待提交、已驳回
            if (roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
                ew.notIn(PmInfoDept::getStatus, Lists.newArrayList(WorkStatusAuditingStatusEnum.TEN.getId(), WorkStatusAuditingStatusEnum.FORTY.getId()));
            }
            //排序
            ew.orderByDesc(PmInfoDept::getGmtSubmit, PmInfoDept::getGmtModified);
        } else {
            //只有宣传部角色可以查询所有单位数据
            if (!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                        .message("提交单位不能为空").build();
            }
        }
        return ew;
    }

    /**
    * 根据单位名称字符串获取单位id
    *
    * @param category 单位名称字符串
    * @return java.lang.Long
    * @author JH
    * @date 2019/10/17 11:11
    */
    private Long getDepartmentId(String category) {
        if (StringUtils.isBlank(category)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("提交单位不能为空").build();
        }
        ListCategoryEnum listCategoryEnum = ListCategoryEnum.valueOfName(category);
        if (listCategoryEnum != null) {
            return listCategoryEnum.getId();
        } else {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("提交单位错误").build();
        }
    }

    /**
     * 功能描述 新增编辑提交
     *
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/10/17 9:42
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEntity(PmInfoDept entity) {
        Long departmentId = getDepartmentId(entity.getCategory());
        List<Long> ids = entity.getDataIdList();
        attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
        if (entity.getId() == null) {
            //单位
            entity.setIdRbacDepartment(departmentId);
            //附件
            entity.setAttachmentCode(UUIDUtil.getUUID());
            entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
            //提交时间设置最大
            entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);
            save(entity);
            //处理集合数据
            updateIds(entity.getId(), ids, departmentId);
        } else {
            //编辑
            PmInfoDept vo = getById(entity.getId());
            if (vo == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到对象").build();
            }
            if (!(WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getStatus()) ||
                    WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getStatus()))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("只有待提交和已驳回状态下数据可编辑").build();
            }
            updateIds(vo.getId(), ids, departmentId);
            updateById(entity);
        }
    }

    /**
     * 功能描述 打包处理基础数据和主表的关系
     * @param id 主表id
     * @param ids 基础数据id集合
     * @param departmentId 单位id
     * @author gengzhiqiang
     * @date 2019/10/17 11:17
     */
    private void updateIds(Long id, List<Long> ids, Long departmentId) {
        if (ListCategoryEnum.DEPARTMENT_SATB.getId().equals(departmentId)) {
            //科技局
            //所添加数据中存在已提请发布的数据，请重新添加！
            List<InfoDeptSatb> history = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>()
                    .in(InfoDeptSatb::getId, ids)
                    .eq(InfoDeptSatb::getStatus, YesOrNoEnum.YES.getType()));
            if (CollectionUtils.isNotEmpty(history)) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("所添加数据中存在已提请发布的数据，请重新添加！").build();
            }
            //数据库里存的数据
            List<InfoDeptSatb> infoDeptSatbList = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>()
                    .eq(InfoDeptSatb::getId, id)
                    .eq(InfoDeptSatb::getStatus, YesOrNoEnum.YES.getType()));
            //数据库里没有数据 全部新增
            if (CollectionUtils.isEmpty(infoDeptSatbList)) {
                InfoDeptSatb infoDeptSatb = InfoDeptSatb.newInstance().build();
                infoDeptSatb.setIdPmInfoDept(id);
                infoDeptSatb.setStatus(YesOrNoEnum.YES.getType());
                satbService.update(infoDeptSatb, new LambdaQueryWrapper<InfoDeptSatb>()
                        .in(InfoDeptSatb::getId, ids));
                return;
            }
            //数据库里存的id集合
            List<Long> dbList = infoDeptSatbList.stream().map(InfoDeptSatb::getId).collect(Collectors.toList());
            //新增   前台传来的  数据库里没有
            List<Long> add = ids.stream().filter(i -> !dbList.contains(i)).collect(Collectors.toList());
            InfoDeptSatb addInfoDeptSatb = InfoDeptSatb.newInstance().build();
            addInfoDeptSatb.setIdPmInfoDept(id);
            addInfoDeptSatb.setStatus(YesOrNoEnum.YES.getType());
            satbService.update(addInfoDeptSatb, new LambdaQueryWrapper<InfoDeptSatb>()
                    .in(InfoDeptSatb::getId, add));
            //删除  数据库里有 但是前台没传的
            List<Long> delete = dbList.stream().filter(i -> !ids.contains(i)).collect(Collectors.toList());
            InfoDeptSatb deleteInfoDeptSatb = InfoDeptSatb.newInstance().build();
            deleteInfoDeptSatb.setIdPmInfoDept(0L);
            deleteInfoDeptSatb.setStatus(YesOrNoEnum.NO.getType());
            satbService.update(deleteInfoDeptSatb, new LambdaQueryWrapper<InfoDeptSatb>()
                    .in(InfoDeptSatb::getId, delete));
        } else if (ListCategoryEnum.DEPARTMENT_YZGT.getId().equals(departmentId)) {
            //亦庄国投
            //所添加数据中存在已提请发布的数据，请重新添加！
            List<InfoDeptYzgt> history = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>()
                    .in(InfoDeptYzgt::getId, ids)
                    .eq(InfoDeptYzgt::getStatus, YesOrNoEnum.YES.getType()));
            if (CollectionUtils.isNotEmpty(history)) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("所添加数据中存在已提请发布的数据，请重新添加！").build();
            }
            //数据库里存的数据
            List<InfoDeptYzgt> infoDeptYzgtList = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>()
                    .eq(InfoDeptYzgt::getId, id)
                    .eq(InfoDeptYzgt::getStatus, YesOrNoEnum.YES.getType()));
            //数据库里没有数据 全部新增
            if (CollectionUtils.isEmpty(infoDeptYzgtList)) {
                InfoDeptYzgt infoDeptYzgt = InfoDeptYzgt.newInstance().build();
                infoDeptYzgt.setIdPmInfoDept(id);
                infoDeptYzgt.setStatus(YesOrNoEnum.YES.getType());
                yzgtService.update(infoDeptYzgt, new LambdaQueryWrapper<InfoDeptYzgt>()
                        .in(InfoDeptYzgt::getId, ids));
                return;
            }
            //数据库里存的id集合
            List<Long> dbList = infoDeptYzgtList.stream().map(InfoDeptYzgt::getId).collect(Collectors.toList());
            //新增   前台传来的  数据库里没有
            List<Long> add = ids.stream().filter(i -> !dbList.contains(i)).collect(Collectors.toList());
            InfoDeptYzgt addInfoDeptYzgt = InfoDeptYzgt.newInstance().build();
            addInfoDeptYzgt.setIdPmInfoDept(id);
            addInfoDeptYzgt.setStatus(YesOrNoEnum.YES.getType());
            yzgtService.update(addInfoDeptYzgt, new LambdaQueryWrapper<InfoDeptYzgt>()
                    .in(InfoDeptYzgt::getId, add));
            //删除  数据库里有 但是前台没传的
            List<Long> delete = dbList.stream().filter(i -> !ids.contains(i)).collect(Collectors.toList());
            InfoDeptYzgt deleteInfoDeptYzgt = InfoDeptYzgt.newInstance().build();
            deleteInfoDeptYzgt.setIdPmInfoDept(0L);
            deleteInfoDeptYzgt.setStatus(YesOrNoEnum.NO.getType());
            yzgtService.update(deleteInfoDeptYzgt, new LambdaQueryWrapper<InfoDeptYzgt>()
                    .in(InfoDeptYzgt::getId, delete));
        }
    }


    /**
    * 批量删除
    *
    * @param ids 主键集合
    * @author JH
    * @date 2019/10/17 11:12
    */
    @Transactional(rollbackFor = Exception.class)
    public void removeDeptInfoByIds(List<Long> ids) {
        List<PmInfoDept> list = (List<PmInfoDept>) super.listByIds(ids);
        if(CollectionUtils.isEmpty(list)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("数据不存在").build();
        }
        List<PmInfoDept> collect = list.stream().filter(n -> !WorkStatusAuditingStatusEnum.FORTY.getId().equals(n.getStatus()) && !WorkStatusAuditingStatusEnum.TEN.getId().equals(n.getStatus())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collect)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("该状态无法删除").build();
        }
        //删除主表
        super.removeByIds(ids);
        //附件集合
        List<String> attachmentCodeList = list.stream().map(PmInfoDept::getAttachmentCode).collect(Collectors.toList());
        //删除附件表
        attachmentService.remove(new LambdaUpdateWrapper<Attachment>().in(Attachment::getAttachmentCode,attachmentCodeList));
        //删除日志表
        logService.remove(new LambdaUpdateWrapper<PmInfoDeptLog>().in(PmInfoDeptLog::getIdPmInfoDept,ids));
        //单位id
        Long departmentId = list.get(0).getIdRbacDepartment();
        //修改基础数据表状态
        if(InnovationConstant.DEPARTMENT_YZGT_ID.equals(departmentId)) {
            List<InfoDeptYzgt> yzgtList = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>().in(InfoDeptYzgt::getIdPmInfoDept, ids));
            yzgtList.forEach(n->n.setIdPmInfoDept(0L));
            yzgtService.updateBatchById(yzgtList);
        }else if(InnovationConstant.DEPARTMENT_SATB_ID.equals(departmentId)){
            List<InfoDeptSatb> satbList = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>().in(InfoDeptSatb::getIdPmInfoDept, ids));
            satbList.forEach(n->n.setIdPmInfoDept(0L));
            satbService.updateBatchById(satbList);

        }
    }

}
