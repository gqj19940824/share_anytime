
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.RedisConstants;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.FileDownload;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.*;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.dao.IplSatbMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.*;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@Slf4j
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
    @Resource
    DicUtils dicUtils;
    @Resource
    RedisSubscribeServiceImpl redisSubscribeService;
    @Resource
    SysMessageHelpService sysMessageHelpService;
    @Resource
    HashRedisUtils hashRedisUtils;

    public List<PieVoByDoc.DataBean> demandNew(Long start, Long end){
        return baseMapper.demandNew(start, end);
    }

    public Map<String, Double> demandNewCatagory(Long start, Long end){
        
        return baseMapper.demandNewCatagory(start, end);
    }

    public List<Map<String, Object>> satbDemandTrend(Long start, Long end){

        return baseMapper.satbDemandTrend(start, end);
    }

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
        ew.orderByDesc(IplSatbMain::getSort);
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
            ew.between(IplSatbMain::getGmtCreate,
                    InnovationUtil.getFirstTimeInMonth(entity.getCreateDate(), true),
                    InnovationUtil.getFirstTimeInMonth(entity.getCreateDate(), false));
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
                IplSatbMain::getSource, IplSatbMain::getStatus,IplSatbMain::getProcessStatus,
                IplSatbMain::getLatestProcess
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
        m.put("processStatusTitle", ProcessStatusEnum.ofName(entity.getProcessStatus()));
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
            //校验当前用户是否可操作
            if(SourceEnum.SELF.getId().equals(entity.getSource())){
                InnovationUtil.check(BizTypeEnum.GROW.getType());
            }
            String uuid = UUIDUtil.getUUID();
            entity.setAttachmentCode(uuid);
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            if (CollectionUtils.isNotEmpty(entity.getAttachmentList())) {
                attachmentService.updateAttachments(uuid, entity.getAttachmentList());
            }

            Long idRbacDepartmentDuty = InnovationUtil.getIdRbacDepartmentDuty(BizTypeEnum.GROW.getType());
            entity.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            this.save(entity);
            redisSubscribeService.saveSubscribeInfo(entity.getId().toString().concat("-0"),
                    ListTypeConstants.DEAL_OVER_TIME,entity.getIdRbacDepartmentDuty(),BizTypeEnum.GROW.getType());
            //====科技局====企业新增填报实时清单需求========
            if (entity.getSource().equals(SourceEnum.ENTERPRISE.getId())) {
                //企业需求填报才进行系统通知
                sysMessageHelpService.addInventoryMessage(InventoryMessage.newInstance()
                        .sourceId(entity.getId())
                        .idRbacDepartment(entity.getIdRbacDepartmentDuty())
                        .dataSourceClass(SysMessageDataSourceClassEnum.TARGET.getId())
                        .flowStatus(SysMessageFlowStatusEnum.ONE.getId())
                        .title(entity.getEnterpriseName())
                        .bizType(BizTypeEnum.GROW.getType())
                        .build());
            }
        } else {
            Customer customer = LoginContextHolder.getRequestAttributes();
            if (!customer.getTypeRangeList().contains(BizTypeEnum.GROW.getType())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("当前账号的单位不可操作数据").build();
            }
            //编辑时必须登录
            LoginContextHolder.getRequestAttributes();
            //校验当前用户是否可操作
            InnovationUtil.check(BizTypeEnum.GROW.getType());
            IplSatbMain main = this.getById(entity.getId());
            entity.setAttachmentCode(main.getAttachmentCode());
            entity.setSource(main.getSource());
            entity.setStatus(main.getStatus());
            entity.setGmtCreate(main.getGmtCreate());
            entity.setSort(main.getSort());
            if (CollectionUtils.isNotEmpty(entity.getAttachmentList())) {
                attachmentService.updateAttachments(main.getAttachmentCode(), entity.getAttachmentList());
            }
            // 保存修改
            Integer status = entity.getStatus();
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            if (IplStatusEnum.DEALING.getId().equals(status)) {
                // 非"待处理"状态才记录日志，该字段与日志处理相同
                entity.setLatestProcess("更新基本信息");
            }
            this.updateById(entity);

            // 更新超时时间
            // 设置处理超时时间
            if (IplStatusEnum.UNDEAL.getId().equals(status)) {
                redisSubscribeService.saveSubscribeInfo(entity.getId().toString().concat("-0"),
                        ListTypeConstants.DEAL_OVER_TIME,entity.getIdRbacDepartmentDuty(),BizTypeEnum.GROW.getType());
                // 设置更新超时时间
            } else if (IplStatusEnum.DEALING.getId().equals(status)) {
                redisSubscribeService.saveSubscribeInfo(entity.getId().toString().concat("-0"),
                        ListTypeConstants.UPDATE_OVER_TIME,entity.getIdRbacDepartmentDuty(),BizTypeEnum.GROW.getType());
                // 非"待处理"状态才记录日志
                Integer lastDealStatus = iplLogService.getLastDealStatus(entity.getId(), BizTypeEnum.GROW.getType());
                iplLogService.save(IplLog.newInstance()
                        .idIplMain(entity.getId())
                        .idRbacDepartmentAssist(0L)
                        .processInfo("更新基本信息")
                        .idRbacDepartmentDuty(entity.getIdRbacDepartmentDuty())
                        .dealStatus(lastDealStatus)
                        .build());
                //======处理中的数据，主责单位再次编辑基本信息--清单协同处理--增加系统消息=======
                List<IplAssist> assists = iplAssistService.getAssists(BizTypeEnum.GROW.getType(), entity.getId());
                List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
                sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                        .sourceId(entity.getId())
                        .idRbacDepartment(entity.getIdRbacDepartmentDuty())
                        .dataSourceClass(SysMessageDataSourceClassEnum.TARGET.getId())
                        .flowStatus(SysMessageFlowStatusEnum.FOUR.getId())
                        .title(entity.getEnterpriseName())
                        .helpDepartmentIdList(assistsIdList)
                        .bizType(BizTypeEnum.GROW.getType())
                        .build());
            }
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
        if (main.getStatus().equals(IplStatusEnum.DONE.getId())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("处理完毕的实时清单不可删除")
                    .build();
        }
        //======处理中的数据，主责单位删除--清单协同处理--增加系统消息=======
        List<IplAssist> assists = iplAssistService.getAssists(BizTypeEnum.GROW.getType(), main.getId());
        List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist)
                .collect(Collectors.toList());
        sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                .sourceId(main.getId())
                .idRbacDepartment(main.getIdRbacDepartmentDuty())
                .dataSourceClass(SysMessageDataSourceClassEnum.TARGET.getId())
                .flowStatus(SysMessageFlowStatusEnum.FIVES.getId())
                .title(main.getEnterpriseName())
                .helpDepartmentIdList(assistsIdList)
                .bizType(BizTypeEnum.GROW.getType())
                .build());
        //关联删除协同信息
        iplAssistService.del(main);
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
        if(ent == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("未获取到成长目标投资信息").build();
        }
        //获取附件
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, ent.getAttachmentCode()));
        //行业类别
        SysCfg industryCategory = sysCfgService.getById(ent.getIndustryCategory());
        //需求类别
        SysCfg demandCategory = sysCfgService.getById(ent.getDemandCategory());
        //获取总体进展
        Map<String, Object> assists = iplAssistService.totalProcessAndAssists(ent.getId(), ent.getIdRbacDepartmentDuty(), ent.getStatus(), BizTypeEnum.GROW.getType());
        String depName = hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT
                .concat(ent.getIdRbacDepartmentDuty().toString()), RedisConstants.NAME);
        Map<String, Object> detail = JsonUtil.ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m, entity);
                    m.put("attachmentList", convertList2MapByAttachment(attachmentList));
                    m.put("industryCategoryTitle", industryCategory.getCfgVal());
                    m.put("demandCategoryTitle", demandCategory.getCfgVal());
                    m.put("nameRbacDepartmentDuty",depName);
                }
                , IplSatbMain::getId, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName
                , IplSatbMain::getDemandCategory, IplSatbMain::getProjectName, IplSatbMain::getProjectAddress
                , IplSatbMain::getProjectIntroduce, IplSatbMain::getTotalAmount, IplSatbMain::getBank, IplSatbMain::getBond
                , IplSatbMain::getRaise, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay
                , IplSatbMain::getSource, IplSatbMain::getStatus
        );
        assists.put("baseInfo", detail);
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
        //校验当前用户是否可操作
        InnovationUtil.check(BizTypeEnum.GROW.getType());
        IplSatbMain main = this.getById(entity.getIdIplMain());
        iplLogService.dutyUpdateStatus(main, entity);
    }

    /**
     * 主责单位实时更新协同单位处理状态
     *
     * @param entity 包含状态及进展
     * @author gengjiajia
     * @since 2019/10/10 13:36
     */
    @Transactional(rollbackFor = Exception.class)
    public void realTimeUpdateStatusByDuty(IplLog entity) {
        //校验当前用户是否可操作
        InnovationUtil.check(BizTypeEnum.GROW.getType());
        IplSatbMain main = this.getById(entity.getIdIplMain());
        iplLogService.updateStatusByDuty(main, entity);
    }

    /**
     * 下载科技局实时清单资料到zip包
     *
     * @param id 主数据id
     * @return zip文件
     * @author gengjiajia
     * @since 2019/10/11 11:27
     */
    public ResponseEntity<byte[]> downloadIplSatbMainDataToZip(Long id) {
        IplSatbMain main = this.getById(id);
        if (main == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("企业创新发展信息实时清单数据不存在")
                    .build();
        }
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, main.getAttachmentCode()));
        if (CollectionUtils.isEmpty(attachmentList)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("企业创新发展信息实时清单无相关资料")
                    .build();
        }
        List<FileDownload> list = attachmentList.stream().map(attachment ->
                FileDownload.newInstance()
                        .url(attachment.getUrl())
                        .name(attachment.getName())
                        .build()
        ).collect(Collectors.toList());
        final String zipFileName = "企业创新发展信息实时清单-相关资料.zip";
        return FileDownloadUtil.downloadFileToZip(list, zipFileName);
    }

    /**
     * 导出科技局清单发布详情excel表格
     *
     * @param id 主数据id
     * @return excel表格
     * @author gengjiajia
     * @since 2019/10/11 11:27
     */
    public void downloadIplSatbMainDataPkgToExcel(Long id, HttpServletRequest request,
                                                  HttpServletResponse response) {
        IplManageMain main = iplManageMainService.getById(id);
        if (main == null || StringUtils.isEmpty(main.getSnapshot())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("成长目标投资清单发布需求详情信息不存在")
                    .build();
        }
        List<List<Object>> satbData = iplManageMainService.getSatbData(main.getSnapshot());
        //判断状态，是否可以下载
        XSSFWorkbook wb = ExcelExportByTemplate.getWorkBook("template/satb.xlsx");
        ExcelExportByTemplate.setData(4,main.getTitle(), satbData, main.getNotes(), wb);
        ExcelExportByTemplate.download(request, response, wb, main.getTitle());
    }

    /**
     * 协同单位更新状态
     *
     * @param  iplLog 包含状态信息
     * @author gengjiajia
     * @since 2019/10/24 16:15
     */
    public void assistRealTimeUpdateStatus(IplLog iplLog) {
        IplSatbMain main = this.getById(iplLog.getIdIplMain());
        if(main == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("未获取到成长目标投资清单信息")
                    .build();
        }
        iplLogService.assistUpdateStatus(main,iplLog);
    }
}
