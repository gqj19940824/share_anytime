package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.utils.ExcelExportByTemplate;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.*;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.*;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * darb->Development and Reform Bureau\r\n\r\n
 *
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
@RestController
@RequestMapping("/ipldarbmain")
public class IplDarbMainController extends BaseWebController {
    @Autowired
    IplDarbMainServiceImpl service;

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Autowired
    private IplLogServiceImpl iplLogService;

    @Autowired
    private IplAssistServiceImpl iplAssistService;

    @Autowired
    private SysCfgServiceImpl sysCfgService;

    @Autowired
    private IplManageMainServiceImpl iplManageMainService;

    /**
     * 导出excel
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 5:04 下午
     */
    @GetMapping("exportExcel")
    public void outputEXcel(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        IplManageMain iplManageMain = iplManageMainService.getById(id);
        // 组装excel需要的数据
        List<List<Object>> data = iplManageMainService.getDarbData(iplManageMain.getSnapshot());
        // 读取模板创建excel文件
        XSSFWorkbook wb = ExcelExportByTemplate.getWorkBook("template/darb.xlsx");
        // 从excel的第5行开始插入数据，并给excel的sheet和标题命名
        ExcelExportByTemplate.setData(4,iplManageMain.getTitle(), data, iplManageMain.getNotes(), wb);
        // 将生成好的excel响应给用户
        ExcelExportByTemplate.download(request, response, wb, iplManageMain.getTitle());
    }

    /**
     * 主责单位实时更新
     *
     * @param iplLog
     * @return
     */
    @PostMapping("/dutyUpdateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> dutyUpdateStatus(@RequestBody IplLog iplLog) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        IplDarbMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        Integer dealStatus = iplLog.getDealStatus();
        if (dealStatus == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        iplLogService.dutyUpdateStatus(entity, iplLog);

        return success();
    }

    /**
     * 协同单位实时更新
     *
     * @param iplLog
     * @return
     */
    @PostMapping("/assistUpdateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> assistUpdateStatus(@RequestBody IplLog iplLog) {

        IplDarbMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        Integer dealStatus = iplLog.getDealStatus();
        if (dealStatus == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        iplLogService.assistUpdateStatus(entity, iplLog);

        return success();
    }

    /**
     * 主责单位实时更新协同单位处理状态
     *
     * @param iplLog
     * @return
     */
    @PostMapping("/updateStatusByDuty")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateStatusByDuty(@RequestBody IplLog iplLog) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        // 协助单位id
        Long idRbacDepartmentAssist = iplLog.getIdRbacDepartmentAssist();
        if (idRbacDepartmentAssist == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        // 主表id
        Long idIplMain = iplLog.getIdIplMain();
        IplDarbMain entity = service.getById(idIplMain);
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }

        // 修改状态、插入日志
        iplLogService.updateStatusByDuty(entity, iplLog);

        return success();
    }

    /**
     * 新增协同事项
     *
     * @param iplDarbMain idIplMain：主表id
     *                    assists：协同单位map
     *                    idRbacDepartmentAssist 协同单位id
     *                    inviteInfo 邀请事项
     * @return
     */
    @PostMapping("/addAssistant")
    public Mono<ResponseEntity<SystemResponse<Object>>> addAssistant(@RequestBody IplDarbMain iplDarbMain) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        // 主表数据
        IplDarbMain entity = service.getById(iplDarbMain.getId());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }

        // 新增协同单位并记录日志
        iplAssistService.addAssistant(iplDarbMain.getIplAssists(), entity);

        return success();
    }

    /**
     * 获取一页数据
     *
     * @param pageEntity 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplDarbMain> pageEntity) {
        InnovationUtil.check(BizTypeEnum.CITY.getType()); // TODO 登陆接口判断领导
        LambdaQueryWrapper<IplDarbMain> ew = wrapper(pageEntity);
        IPage<IplDarbMain> p = service.page(pageEntity.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);
    }

    /**
     * 获取一页数据
     *
     * @param id 统一查询条件
     * @return
     */
    @GetMapping("/detailById/{id}")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@PathVariable("id") Long id) {
        //InnovationUtil.check(BizTypeEnum.CITY.getType());
        IplDarbMain entity = service.getById(id);
        entity.setIdRbacDepartmentName(InnovationUtil.getDeptNameById(entity.getIdRbacDepartmentDuty()));
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }

        Map<String, Object> resultMap = iplAssistService.totalProcessAndAssists(id, entity.getIdRbacDepartmentDuty(), entity.getProcessStatus(), BizTypeEnum.CITY.getType());
        resultMap.put("baseInfo", convert2Map(entity));
        return success(resultMap);
    }

    /**
     * 添加或修改
     *
     * @param entity darb->Development and Reform Bureau\r\n\r\n实体
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody IplDarbMain entity) {
        // TODO 校验

        if (entity.getId() == null) { // 新增
            Integer source = entity.getSource();
            if (SourceEnum.SELF.getId().equals(source)){
                InnovationUtil.check(BizTypeEnum.CITY.getType());
            }
            String uuid = UUIDUtil.getUUID();
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            entity.setAttachmentCode(uuid);
            entity.setIdRbacDepartmentDuty(InnovationUtil.getIdRbacDepartmentDuty(BizTypeEnum.CITY.getType()));
            service.add(entity);
        } else { // 编辑
            // 没有登录会抛异常
            LoginContextHolder.getRequestAttributes();

            service.edit(entity);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId());
        return success(result);
    }

    /**
     * 批量删除
     *
     * @param idsMap id列表用英文逗号分隔
     * @return
     */
    @PostMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIds(@RequestBody Map<String, String> idsMap) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        String ids = idsMap.get("ids");
        if (StringUtils.isBlank(ids)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        service.delByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success();
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplDarbMain> list) {

        Map<Long, Object> collect_;
        Set<Long> ids = new HashSet<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> {
                ids.add(e.getIndustryCategory());
                ids.add(e.getDemandCategory());
                ids.add(e.getDemandItem());
            });

            List<SysCfg> values = sysCfgService.getValues(ids);
            collect_ = values.stream().collect(Collectors.toMap(BaseEntity::getId, mSysCfg::getCfgVal, (k1, k2) -> k2));
        }else {
            collect_ = Maps.newHashMap();
        }
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity, collect_);
                }
                , IplDarbMain::getId, IplDarbMain::getEnterpriseName, IplDarbMain::getProjectName, IplDarbMain::getContent, IplDarbMain::getTotalInvestment, IplDarbMain::getProjectProgress, IplDarbMain::getTotalAmount, IplDarbMain::getBank, IplDarbMain::getBond, IplDarbMain::getSelfRaise, IplDarbMain::getIncreaseTrustType, IplDarbMain::getWhetherIntroduceSocialCapital, IplDarbMain::getConstructionCategory, IplDarbMain::getConstructionStage, IplDarbMain::getConstructionModel, IplDarbMain::getContactPerson, IplDarbMain::getContactWay, IplDarbMain::getAttachmentCode
        );
    }

    /**
     * 将实体 转换为 Map
     *
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplDarbMain ent) {
        Set<Long> ids = new HashSet<>(Arrays.asList(ent.getDemandCategory(), ent.getDemandItem(), ent.getIndustryCategory()));
        List<SysCfg> values = sysCfgService.getValues(ids);
        Map<Long, Object> collect = values.stream().collect(Collectors.toMap(BaseEntity::getId, mSysCfg::getCfgVal, (k1, k2) -> k2));


        return JsonUtil.<IplDarbMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m, entity, collect);
                }
                , IplDarbMain::getId, IplDarbMain::getEnterpriseName, IplDarbMain::getProjectName, IplDarbMain::getContent, IplDarbMain::getTotalInvestment, IplDarbMain::getProjectProgress, IplDarbMain::getTotalAmount, IplDarbMain::getBank, IplDarbMain::getBond, IplDarbMain::getSelfRaise, IplDarbMain::getIncreaseTrustType, IplDarbMain::getWhetherIntroduceSocialCapital, IplDarbMain::getConstructionCategory, IplDarbMain::getConstructionStage, IplDarbMain::getConstructionModel, IplDarbMain::getContactPerson, IplDarbMain::getContactWay, IplDarbMain::getAttachmentCode
                , IplDarbMain::getIndustryCategory, IplDarbMain::getDemandItem, IplDarbMain::getDemandCategory
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplDarbMain entity, Map<Long, Object> collect) {

        m.put("industryCategoryId", entity.getIndustryCategory());
        m.put("demandItemId", entity.getDemandItem());
        m.put("demandCategoryId", entity.getDemandCategory());
        m.put("industryCategory", collect.get(entity.getIndustryCategory()));
        m.put("demandItem", collect.get(entity.getDemandItem()));
        m.put("demandCategory", collect.get(entity.getDemandCategory()));
        m.put("source", SourceEnum.ENTERPRISE.getId().equals(entity.getSource()) ? "企业" : "发改局");
        m.put("status", IplStatusEnum.ofName(entity.getStatus()));
        m.put("processStatus", ProcessStatusEnum.ofName(entity.getProcessStatus()));
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));

        LambdaQueryWrapper<Attachment> qw = new LambdaQueryWrapper<>();
        qw.eq(Attachment::getAttachmentCode, entity.getAttachmentCode());
        m.put("attachments", attachmentService.list(qw));
    }

    /**
     * 查询条件转换
     *
     * @param pageEntity 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<IplDarbMain> wrapper(PageEntity<IplDarbMain> pageEntity) {
        LambdaQueryWrapper<IplDarbMain> ew = new LambdaQueryWrapper<>();
        if (pageEntity != null && pageEntity.getEntity() != null) {
            IplDarbMain entity = pageEntity.getEntity();

            // 行业类别
            Long industryCategory = entity.getIndustryCategory();
            if (industryCategory != null) {
                ew.eq(IplDarbMain::getIndustryCategory, industryCategory);
            }

            // 企业名称
            String enterpriseName = entity.getEnterpriseName();
            if (StringUtils.isNotBlank(enterpriseName)) {
                ew.like(IplDarbMain::getEnterpriseName, enterpriseName);
            }

            // 需求类别
            Long demandCategory = entity.getDemandCategory();
            if (demandCategory != null) {
                ew.eq(IplDarbMain::getDemandCategory, demandCategory);
            }

            // 项目名称
            String projectName = entity.getProjectName();
            if (StringUtils.isNotBlank(projectName)) {
                ew.like(IplDarbMain::getProjectName, projectName);
            }

            // 联系人
            String contactPerson = entity.getContactPerson();
            if (StringUtils.isNotBlank(contactPerson)) {
                ew.like(IplDarbMain::getContactPerson, contactPerson);
            }

            // 联系方式
            String contactWay = entity.getContactWay();
            if (StringUtils.isNotBlank(contactWay)) {
                ew.like(IplDarbMain::getContactWay, contactWay);
            }
            // 创建时间
            String creatTime = entity.getCreatTime();
            if (StringUtils.isNotBlank(creatTime)) {
                //gt 大于 lt 小于
                ew.ge(IplDarbMain::getGmtCreate, InnovationUtil.getFirstTimeInMonth(creatTime, true));
                ew.lt(IplDarbMain::getGmtCreate, InnovationUtil.getFirstTimeInMonth(creatTime, false));
            }
            // 更新时间
            String updateTime = entity.getUpdateTime();
            if (StringUtils.isNotBlank(updateTime)) {
                //gt 大于 lt 小于
                ew.ge(IplDarbMain::getGmtModified, InnovationUtil.getFirstTimeInMonth(updateTime, true));
                ew.lt(IplDarbMain::getGmtModified, InnovationUtil.getFirstTimeInMonth(updateTime, false));
            }

            // 来源
            Integer source = entity.getSource();
            if (source != null) {
                ew.eq(IplDarbMain::getSource, source);
            }

            // 状态
            Integer status = entity.getStatus();
            if (status != null) {
                ew.eq(IplDarbMain::getStatus, status);
            }

            // 备注
            Integer processStatus = entity.getProcessStatus();
            if (processStatus != null) {
                ew.eq(IplDarbMain::getProcessStatus, processStatus);
            }
        }
        ew.orderByDesc(IplDarbMain::getGmtCreate);
        return ew;
    }

    /**
     * 协同事项列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-09-25 15:26
     */
    @GetMapping("/assists/{mainId}")
    public Mono<ResponseEntity<SystemResponse<Object>>> assists(@PathVariable("mainId") Long mainId) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        // 查询基本信息
        IplDarbMain entity = service.getById(mainId);

        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 主责单位id
        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();

        // 查询协同单位列表
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getBizType, BizTypeEnum.CITY.getType()).eq(IplAssist::getIdIplMain, mainId).orderByDesc(IplAssist::getGmtCreate);
        List<IplAssist> assists = iplAssistService.list(qw);
        if (CollectionUtils.isNotEmpty(assists)) {
            assists.forEach(e -> {
                e.setNameRbacDepartmentAssist(InnovationUtil.getDeptNameById(e.getIdRbacDepartmentAssist()));
            });
        }

        return success(assists);
    }

    /**
     * 总体进展
     *
     * @param mainId
     * @return
     */
    @PostMapping("/totalProcess/{mainId}")
    public Mono<ResponseEntity<SystemResponse<Object>>> totalProcess(@PathVariable("mainId") Long mainId) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        // 查询基本信息
        IplDarbMain entity = service.getById(mainId);

        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 主责单位id
        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();
        return success(iplAssistService.totalProcessAndAssists(mainId, idRbacDepartmentDuty, entity.getProcessStatus(), BizTypeEnum.CITY.getType()).get("totalProcess"));
    }

    /**
     * 功能描述 获取协同单位下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    @PostMapping("/getAssistList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getAssistList(@RequestBody IplDarbMain entity) {
        InnovationUtil.check(BizTypeEnum.CITY.getType());
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplDarbMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        //主表id  数据集合
        IplDarbMain vo = service.getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        return success(iplAssistService.getAssistList(vo.getId(), BizTypeEnum.CITY.getType()));
    }
}

