
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JKDates;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.dao.IplSatbMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IplSatbMainService
 * date: 2019-10-08 17:03:09
 *
 * @author G
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplSatbMainServiceImpl extends BaseServiceImpl<IplSatbMainDao, IplSatbMain> {

    @Resource
    AttachmentServiceImpl attachmentService;
    @Resource
    SysCfgServiceImpl sysCfgService;
    @Resource
    IplAssistServiceImpl iplAssistService;
    @Resource
    IplManageMainServiceImpl iplManageMainService;
    @Resource
    IplLogServiceImpl iplLogService;
    @Resource
    RbacClient rbacClient;

    /**
     * 获取清单列表
     *
     * @param pageEntity 包含分页及检索条件
     * @return 清单列表
     * @author gengjiajia
     * @since 2019/10/08 17:35
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<IplSatbMain> pageEntity) {
        LambdaQueryWrapper<IplSatbMain> ew = new LambdaQueryWrapper<>();
        IplSatbMain entity = pageEntity.getEntity();
        if (entity != null) {
            wrapper(entity, ew);
        }
        IPage<IplSatbMain> page = this.page(pageEntity.getPageable(), ew);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(page.getTotal())
                .items(convert2List(page.getRecords()))
                .build();
    }

    /**
     * 查询条件转换
     *
     * @param entity 检索条件
     * @param ew     检索条件组装器
     * @author gengjiajia
     * @since 2019/10/08 17:52
     */
    private void wrapper(IplSatbMain entity, LambdaQueryWrapper<IplSatbMain> ew) {
        ew.orderByAsc(IplSatbMain::getSort);
        if (entity.getIndustryCategory() != null) {
            ew.eq(IplSatbMain::getIndustryCategory, entity.getIndustryCategory());
        }
        if (entity.getDemandCategory() != null) {
            ew.eq(IplSatbMain::getDemandCategory, entity.getDemandCategory());
        }
        if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
            ew.like(IplSatbMain::getEnterpriseName, entity.getEnterpriseName());
        }
        if (StringUtils.isNotBlank(entity.getProjectName())) {
            ew.like(IplSatbMain::getProjectName, entity.getProjectName());
        }
        if (StringUtils.isNotEmpty(entity.getCreateDate())) {
            String createTime = entity.getCreateDate();
            String[] dateArr = createTime.split("-");
            int maxDay = JKDates.getMaxDay(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
            ew.between(IplSatbMain::getGmtCreate,
                    DateUtils.parseDate(createTime.concat("-01 00:00:00")).getTime(),
                    DateUtils.parseDate(createTime.concat("-").concat(String.valueOf(maxDay)).concat(" 23:59:59")).getTime());
        }
        if (entity.getSource() != null) {
            ew.eq(IplSatbMain::getSource, entity.getSource());
        }
        if (entity.getStatus() != null) {
            ew.eq(IplSatbMain::getStatus, entity.getStatus());
        }
        if (StringUtils.isNotBlank(entity.getNotes())) {
            ew.like(IplSatbMain::getNotes, entity.getNotes());
        }
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return map列表
     * @author gengjiajia
     * @since 2019/10/08 17:58
     */
    private List<Map<String, Object>> convert2List(List<IplSatbMain> list) {
        //查询附件
        List<String> codeList = list.stream().map(IplSatbMain::getAttachmentCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(codeList)) {
            codeList.add("0");
        }
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codeList.toArray()));
        Map<String, List<Attachment>> attachmentMap = attachmentList.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));
        //获取行业类别
        Map<Long, String> industryCategoryMap = sysCfgService.getSysCfgMap(SysCfgEnum.THREE.getId());
        //需求类别
        Map<Long, String> demandCategoryMap = sysCfgService.getSysCfgMap(SysCfgEnum.FOUR.getId());
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                    List<Attachment> attachments = attachmentMap.get(entity.getAttachmentCode());
                    m.put("attachmentList", CollectionUtils.isEmpty(attachments) ? Lists.newArrayList() : convertList2MapByAttachment(attachments));
                    m.put("industryCategoryTitle", industryCategoryMap.get(entity.getIndustryCategory()));
                    m.put("demandCategoryTitle", demandCategoryMap.get(entity.getDemandCategory()));
                }
                , IplSatbMain::getId, IplSatbMain::getNotes, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName, IplSatbMain::getDemandCategory,
                IplSatbMain::getProjectName, IplSatbMain::getProjectAddress, IplSatbMain::getProjectIntroduce, IplSatbMain::getTotalAmount, IplSatbMain::getBank,
                IplSatbMain::getBond, IplSatbMain::getRaise, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay,
                IplSatbMain::getSource, IplSatbMain::getStatus
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplSatbMain entity) {
        m.put("gmtCreate", entity.getGmtCreate());
        m.put("gmtModified", entity.getGmtModified());
        m.put("sourceTitle", entity.getSource().equals(SourceEnum.SELF.getId()) ? "科技局" : "企业");
        m.put("statusTitle", IplStatusEnum.ofName(entity.getStatus()));
    }

    /**
     * 将实体列表 转换为Map
     *
     * @param list 实体对象
     * @return Map
     */
    private List<Map<String, Object>> convertList2MapByAttachment(List<Attachment> list) {
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    // adapterField(m, entity);
                }
                , Attachment::getSize, Attachment::getUrl, Attachment::getName
        );
    }

    /**
     * 新增or修改清单
     *
     * @param entity 清单信息
     * @author gengjiajia
     * @since 2019/10/08 20:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIplSatbMain(IplSatbMain entity) {
        if (entity.getId() == null) {
            String uuid = UUIDUtil.getUUID();
            entity.setAttachmentCode(uuid);
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            if (CollectionUtils.isNotEmpty(entity.getAttachmentList())) {
                attachmentService.updateAttachments(uuid, entity.getAttachmentList());
            }
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_SATB_ID);
            this.save(entity);
        } else {
            IplSatbMain main = this.getById(entity.getId());
            entity.setAttachmentCode(main.getAttachmentCode());
            entity.setSource(main.getSource());
            entity.setStatus(main.getStatus());
            entity.setGmtCreate(main.getGmtCreate());
            entity.setSort(main.getSort());
            if (CollectionUtils.isNotEmpty(entity.getAttachmentList())) {
                attachmentService.updateAttachments(main.getAttachmentCode(), entity.getAttachmentList());
            }
            this.updateById(entity);
        }

    }

    /**
     * 删除清单信息
     *
     * @param id 清单id
     * @author gengjiajia
     * @since 2019/10/08 20:47
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        //关联删除附件
        IplSatbMain main = this.getById(id);
        //关联删除协同信息
        iplAssistService.del(id, main.getIdRbacDepartmentDuty(), main.getAttachmentCode());
        this.removeById(id);
    }

    /**
     * 获取清单详情
     *
     * @param id 清单id
     * @return 清单详情
     * @author gengjiajia
     * @since 2019/10/08 20:48
     */
    public Map<String, Object> detailById(Long id) {
        return convert2Map(this.getById(id));
    }

    /**
     * 将实体 转换为 Map
     *
     * @param ent 实体
     * @return Map
     */
    private Map<String, Object> convert2Map(IplSatbMain ent) {
        //获取附件
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, ent.getAttachmentCode()));
        //行业类别
        SysCfg industryCategory = sysCfgService.getById(ent.getIndustryCategory());
        //需求类别
        SysCfg demandCategory = sysCfgService.getById(ent.getDemandCategory());
        //获取总体进展
        Map<String, Object> assists = iplAssistService.totalProcessAndAssists(ent.getId(), ent.getIdRbacDepartmentDuty(), ent.getStatus());
        Map<String, Object> detail = JsonUtil.ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m, entity);
                    m.put("attachmentList", convertList2MapByAttachment(attachmentList));
                    m.put("industryCategoryTitle", industryCategory.getCfgVal());
                    m.put("demandCategoryTitle", demandCategory.getCfgVal());
                }
                , IplSatbMain::getId, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName
                , IplSatbMain::getDemandCategory, IplSatbMain::getProjectName, IplSatbMain::getProjectAddress
                , IplSatbMain::getProjectIntroduce, IplSatbMain::getTotalAmount, IplSatbMain::getBank, IplSatbMain::getBond
                , IplSatbMain::getRaise, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay
                , IplSatbMain::getSource, IplSatbMain::getStatus
        );
        assists.put("detail", detail);
        return assists;
    }

    /**
     * 获取系统类别
     *
     * @param cfgType 系统类型
     * @return 类别列表
     * @author gengjiajia
     * @since 2019/10/09 19:35
     */
    public List<Map<String, Object>> getCategoryBySysType(Integer cfgType) {
        return sysCfgService.getSysList1(cfgType);
    }

    /**
     * 获取协同单位列表
     *
     * @return 协同单位列表
     * @author gengjiajia
     * @since 2019/10/10 10:23
     */
    public List<Map<String, Object>> getAssistDepartmentList(Long id) {
        //主表id  数据集合
        IplSatbMain satbMain = this.getById(id);
        if (satbMain == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("未获取到成长目标投资信息").build();
        }
        List<DepartmentVO> departmentList = rbacClient.getAllDepartment();
        List<IplAssist> assistList = iplAssistService.list(new LambdaQueryWrapper<IplAssist>()
                .eq(IplAssist::getIdIplMain, id)
                .eq(IplAssist::getIdRbacDepartmentDuty, satbMain.getIdRbacDepartmentDuty()));
        List<Long> ids = assistList.stream()
                .map(IplAssist::getIdRbacDepartmentAssist)
                .collect(Collectors.toList());
        List<DepartmentVO> voList = departmentList.stream()
                .filter(d -> !ids.contains(d.getId()))
                .collect(Collectors.toList());
        return JsonUtil.ObjectToList(voList, null, DepartmentVO::getId, DepartmentVO::getName);
    }

    /**
     * 保存协同单位信息
     *
     * @param id         主业务id
     * @param assistList 协同单位信息列表
     * @author gengjiajia
     * @since 2019/10/10 10:59
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAssistDepartmentList(Long id, List<IplAssist> assistList) {
        IplSatbMain satbMain = this.getById(id);
        if (satbMain == null) {
            throw UnityRuntimeException.newInstance()
                    .message("未获取到成长目标投资信息")
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .build();
        }
        iplAssistService.addAssistant(assistList, satbMain);
    }

    /**
     * 实时更新
     *
     * @param entity 实时更新数据
     * @author gengjiajia
     * @since 2019/10/10 13:36
     */
    @Transactional(rollbackFor = Exception.class)
    public void realTimeUpdateStatus(IplLog entity) {
        IplSatbMain main = this.getById(entity.getIdIplMain());
        iplLogService.updateStatus(main, entity);
    }

    /**
     * 主责单位实时更新协同单位处理状态
     *
     * @param entity 包含状态及进展
     * @author gengjiajia
     * @since 2019/10/10 13:36
     */
    public void realTimeUpdateStatusByDuty(IplLog entity) {
        IplSatbMain main = this.getById(entity.getIdIplMain());
        iplLogService.updateStatusByDuty(main.getIdRbacDepartmentDuty(), main.getId(), entity);
    }

    /**
     * 清单发布管理列表
     *
     * @param search           查询条件
     * @param departmentSatbId 主责单位id
     * @return 分页列表
     * @author gengjiajia
     * @since 2019/10/10 16:56
     */
    public PageElementGrid listForPkg(PageEntity<IplManageMain> search, Long departmentSatbId) {
        IPage<IplManageMain> list = iplManageMainService.listForPkg(search, departmentSatbId);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2ListForPkg(list.getRecords())).build();

    }

    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return java.util.List 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2ListForPkg(List<IplManageMain> list) {
        return JsonUtil.<IplManageMain>ObjectToList(list,
                (m, entity) -> {
                }, IplManageMain::getId, IplManageMain::getTitle, IplManageMain::getGmtSubmit, IplManageMain::getStatus, IplManageMain::getStatusName);
    }

    /**
     * 新增or修改清单发布
     *
     * @param entity 清单发布信息
     * @author gengjiajia
     * @since 2019/10/10 16:00
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateForPkg(IplManageMain entity) {
        if (entity.getId() == null) {
            //新增
            entity.setAttachmentCode(UUIDUtil.getUUID());
            //附件
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachments());
            //待提交
            entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
            //提交时间设置最大
            entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);
            //快照数据
            entity.setSnapshot(JSON.toJSONString(entity.getIplSatbMainList()));
            //企服局
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_SATB_ID);
            //保存
            iplManageMainService.save(entity);
        } else {
            //编辑
            IplManageMain vo = iplManageMainService.getById(entity.getId());
            if (vo == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到发布清单信息").build();
            }
            if (!(WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getStatus())) ||
                    WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getStatus())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("只有待提交和已驳回状态下数据可编辑")
                        .build();
            }
            //快照数据
            entity.setSnapshot(JSON.toJSONString(entity.getIplEsbMainList()));
            //附件
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachments());
            //修改信息
            iplManageMainService.updateById(entity);
        }
    }

    /**
     * 获取发布清单详情
     *
     * @param id 发布详情
     * @return 发布清单详情
     * @author gengjiajia
     * @since 2019/10/10 17:10
     */
    public IplManageMain detailByIdForPkg(Long id) {
        IplManageMain entity = iplManageMainService.getById(id);
        if (entity == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到发布清单信息")
                    .build();
        }
        //快照集合
        List<IplSatbMain> list = JSON.parseArray(entity.getSnapshot(), IplSatbMain.class);
        entity.setSnapshot("");
        entity.setIplSatbMainList(list);
        //附件
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, entity.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            entity.setAttachments(attachmentList);
        }
        //日志集合 日志节点集合
        entity = iplManageMainService.setLogs(entity);
        return entity;
    }

    /**
     * 删除发布信息
     *
     * @param ids              批量删除id集合
     * @param departmentSatbId 科技局主责id
     * @author gengjiajia
     * @since 2019/10/10 17:12
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdsForPkg(List<Long> ids, Long departmentSatbId) {
        iplManageMainService.removeByIdsForPkg(ids, departmentSatbId);
    }

    /**
     * 发布清单提交
     *
     * @param entity 发布清单信息
     * @author gengjiajia
     * @since 2019/10/10 17:14
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(IplManageMain entity) {
        iplManageMainService.submit(entity);
    }


}
