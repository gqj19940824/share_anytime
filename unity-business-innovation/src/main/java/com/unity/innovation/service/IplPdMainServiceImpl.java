
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.dao.IplPdMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplPdMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.enums.*;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * ClassName: IplPdMainService
 * date: 2019-09-29 15:50:28
 *
 * @author G
 * @since JDK 1.8
 */
@Service
public class IplPdMainServiceImpl extends BaseServiceImpl<IplPdMainDao, IplPdMain> {

    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private SysCfgServiceImpl sysCfgService;
    @Resource
    private SysMessageHelpService sysMessageHelpService;

    /**
     * 发布会 列表数据
     *
     * @param pageEntity 分页及参数
     * @return 列表数据
     * @author gengjiajia
     * @since 2019/09/29 16:24
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<IplPdMain> pageEntity) {
        LambdaQueryWrapper<IplPdMain> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(IplPdMain::getSort);
        IplPdMain entity = pageEntity.getEntity();
        if (entity != null) {
            if (entity.getIndustryCategory() != null) {
                wrapper.eq(IplPdMain::getIndustryCategory, entity.getIndustryCategory());
            }
            if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
                wrapper.like(IplPdMain::getEnterpriseName, entity.getEnterpriseName());
            }
            if (entity.getSource() != null) {
                wrapper.eq(IplPdMain::getSource, entity.getSource());
            }
            if (StringUtils.isNotBlank(entity.getNotes())) {
                wrapper.like(IplPdMain::getNotes, entity.getNotes());
            }
            if (StringUtils.isNotBlank(entity.getCreateDate())) {
                wrapper.between(IplPdMain::getGmtCreate,
                        InnovationUtil.getFirstTimeInMonth(entity.getCreateDate(), true),
                        InnovationUtil.getFirstTimeInMonth(entity.getCreateDate(), false));
            }
        }
        IPage<IplPdMain> iPage = this.page(pageEntity.getPageable(), wrapper);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(iPage.getTotal())
                .items(convert2List(iPage.getRecords()))
                .build();
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return List Map
     */
    private List<Map<String, Object>> convert2List(List<IplPdMain> list) {
        //获取行业类别
        Map<Long, String> industryCategoryMap = getSysCfgMap();
        //批量获取附件
        List<String> codeList = list.stream().map(IplPdMain::getAttachmentCode).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(codeList)){
            //无附件，随便加入一个元素，保证查询不报错
            codeList.add("0");
        }
        List<Attachment> allAttachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .in(Attachment::getAttachmentCode, codeList.toArray()));
        Map<String, List<Attachment>> listMap = allAttachmentList.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));
        return JsonUtil.<IplPdMain>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                    m.put("attachmentList", MapUtils.isEmpty(listMap) ? Lists.newArrayList() : convertList2MapByAttachment(listMap.get(entity.getAttachmentCode())));
                    m.put("industryCategoryTitle", entity.getIndustryCategory() == null
                            ? "" : industryCategoryMap.get(entity.getIndustryCategory()));
                }
                , IplPdMain::getId, IplPdMain::getIndustryCategory, IplPdMain::getEnterpriseName, IplPdMain::getEnterpriseIntroduction,
                IplPdMain::getSpecificCause, IplPdMain::getIdCard, IplPdMain::getContactPerson, IplPdMain::getContactWay,
                IplPdMain::getAttachmentCode, IplPdMain::getSource, IplPdMain::getStatus, IplPdMain::getPost, IplPdMain::getNotes
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplPdMain entity) {
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        m.put("sourceTitle", SourceEnum.ENTERPRISE.getId().equals(entity.getSource()) ? "企业" : "宣传部");
        String statusTitle = IplStatusEnum.ofName(entity.getStatus());
        m.put("statusTitle", statusTitle == null ? "" : statusTitle);
    }

    /**
     * 保存或更新发布会
     *
     * @param entity 包含发布会信息
     * @author gengjiajia
     * @since 2019/09/30 10:39
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIplPdMain(IplPdMain entity) {
        if (entity.getId() == null) {
            String uuid = UUIDUtil.getUUID();
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            entity.setAttachmentCode(uuid);
            Long idRbacDepartmentDuty = InnovationUtil.getIdRbacDepartmentDuty(BizTypeEnum.SIGNUP.getType());
            entity.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
            this.save(entity);
            //附件处理
            if(CollectionUtils.isNotEmpty(entity.getAttachmentList())){
                attachmentService.updateAttachments(uuid, entity.getAttachmentList());
            }
            //====宣传部====企业新增填报实时清单需求========
            if(entity.getSource().equals(SourceEnum.ENTERPRISE.getId())) {
                //企业需求填报才进行系统通知
                sysMessageHelpService.addInventoryMessage(InventoryMessage.newInstance()
                        .sourceId(entity.getId())
                        .idRbacDepartment(idRbacDepartmentDuty)
                        .dataSourceClass(SysMessageDataSourceClassEnum.PROPAGANDA.getId())
                        .flowStatus(SysMessageFlowStatusEnum.ONE.getId())
                        .title(entity.getEnterpriseName())
                        .bizType(BizTypeEnum.SIGNUP.getType())
                        .build());
            }
        } else {
            //编辑时必须登录
            LoginContextHolder.getRequestAttributes();
            IplPdMain main = this.getById(entity.getId());
            entity.setSource(main.getSource());
            entity.setAttachmentCode(main.getAttachmentCode());
            entity.setStatus(main.getStatus());
            entity.setGmtCreate(main.getGmtCreate());
            entity.setSort(main.getSort());
            this.updateById(entity);
            //附件处理
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
        }

    }

    /**
     * 删除发布会
     *
     * @param id 发布会id
     * @author gengjiajia
     * @since 2019/09/30 11:11
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        IplPdMain pdMain = this.getById(id);
        if (StringUtils.isNotEmpty(pdMain.getAttachmentCode())) {
            attachmentService.remove(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, pdMain.getAttachmentCode()));
        }
        this.removeById(id);
    }

    /**
     * 查询详情
     *
     * @param id 发布会id
     * @return 发布会详情信息
     * @author gengjiajia
     * @since 2019/09/30 11:32
     */
    public Map<String, Object> detailById(Long id) {
        IplPdMain pdMain = this.getById(id);
        if(pdMain == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("未获取到发布会报名信息").build();
        }
        return convert2Map(pdMain);
    }

    /**
     * 将实体列表 转换为Map
     *
     * @param ent 实体对象
     * @return Map
     */
    private Map<String, Object> convert2Map(IplPdMain ent) {
        //获取行业分类
        SysCfg sysCfg = sysCfgService.getById(ent.getIndustryCategory());
        //获取附件详情
        List<Attachment> attachmentList;
        if (StringUtils.isNotEmpty(ent.getAttachmentCode())) {
            attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, ent.getAttachmentCode()));
        } else {
            attachmentList = Lists.newArrayList();
        }
        return JsonUtil.ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m, entity);
                    m.put("attachmentList", CollectionUtils.isNotEmpty(attachmentList) ? convertList2MapByAttachment(attachmentList) : attachmentList);
                    m.put("industryCategoryTitle", sysCfg == null ? "" : sysCfg.getCfgVal());
                }
                , IplPdMain::getId, IplPdMain::getIdIplmMainIplMain, IplPdMain::getIndustryCategory, IplPdMain::getEnterpriseName,
                IplPdMain::getEnterpriseIntroduction, IplPdMain::getSpecificCause, IplPdMain::getIdCard, IplPdMain::getContactPerson,
                IplPdMain::getContactWay, IplPdMain::getAttachmentCode, IplPdMain::getSource, IplPdMain::getStatus, IplPdMain::getPost,
                IplPdMain::getNotes, IplPdMain::getAttachmentList
        );
    }

    /**
     * 将实体列表 转换为Map
     *
     * @param list 实体对象
     * @return Map
     */
    private List<Map<String, Object>> convertList2MapByAttachment(List<Attachment> list) {
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    // adapterField(m, entity);
                }
                , Attachment::getSize,Attachment::getUrl,Attachment::getName
        );
    }

    /**
     * 列表转换为map(excel导出专属)
     *
     * @param list 源数据
     * @return map
     * @author gengjiajia
     * @since 2019/10/08 09:52
     */
    public List<Map<String, Object>> convert2ListByExport(List<IplPdMain> list) {
        //批量获取附件
        List<String> codeList = list.stream().map(IplPdMain::getAttachmentCode).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(codeList)){
            //无附件，随便加入一个元素，保证查询不报错
            codeList.add("0");
        }
        //获取行业类别
        Map<Long, String> industryCategoryMap = getSysCfgMap();
        List<Attachment> allAttachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codeList.toArray()));
        Map<String, String> map = allAttachmentList.stream()
                .collect(groupingBy(Attachment::getAttachmentCode,
                        mapping(Attachment::getUrl, joining(","))));
        return JsonUtil.<IplPdMain>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                    m.put("source", SourceEnum.ENTERPRISE.getId().equals(entity.getSource()) ? "企业" : "宣传部");
                    m.put("attachmentCode", MapUtils.isEmpty(map) ? "" : map.get(entity.getAttachmentCode()));
                    m.put("industryCategory", industryCategoryMap.get(entity.getIndustryCategory()));
                }
                , IplPdMain::getIndustryCategory, IplPdMain::getEnterpriseName,
                IplPdMain::getEnterpriseIntroduction, IplPdMain::getSpecificCause,
                IplPdMain::getIdCard, IplPdMain::getContactPerson, IplPdMain::getContactWay,
                IplPdMain::getSource, IplPdMain::getPost, IplPdMain::getNotes
        );
    }

    /**
     * 获取行业类别
     *
     * @return 行业类别
     * @author gengjiajia
     * @since 2019/10/08 10:49
     */
    public Map<Long, String> getSysCfgMap() {
        List<SysCfg> cfgList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, SysCfgEnum.THREE.getId()));
        return cfgList.stream()
                .collect(groupingBy(SysCfg::getId, mapping(SysCfg::getCfgVal, joining())));
    }

    /**
     * 获取行业类别
     *
     * @return 行业类别
     * @author gengjiajia
     * @since 2019/10/08 10:49
     */
    public List<Map<String,Object>> getIndustryCategoryList() {
        return sysCfgService.getSysList1(SysCfgEnum.THREE.getId());
    }
}
