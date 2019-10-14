package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.*;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.*;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * darb->Development and Reform Bureau\r\n\r\n
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
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listForPkg")
    public Mono<ResponseEntity<SystemResponse<Object>>> listForPkg(@RequestBody PageEntity<IplManageMain> search) {
        IPage<IplManageMain> list= iplManageMainService.listForPkg(search,InnovationConstant.DEPARTMENT_DARB_ID);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2ListForPkg(list.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 数据整理
     * @param list 集合
     * @return java.util.List 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2ListForPkg(List<IplManageMain> list) {
        return JsonUtil.<IplManageMain>ObjectToList(list,
                (m, entity) -> {
                }, IplManageMain::getId, IplManageMain::getTitle, IplManageMain::getGmtSubmit, IplManageMain::getStatus,IplManageMain::getStatusName);
    }

    /**
     * 功能描述 包的新增编辑
     *
     * @param entity 保存计划
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/saveOrUpdateForPkg")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdateForPkg(@RequestBody IplManageMain entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParamForPkg(entity);
        if (obj != null) {
            return obj;
        }
        iplManageMainService.saveOrUpdateForPkg(entity,InnovationConstant.DEPARTMENT_ESB_ID);
        return success("操作成功");
    }

    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParamForPkg(IplManageMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplManageMain::getTitle, IplManageMain::getIplEsbMainList);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getTitle().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "标题字数限制50字");
        }
        if (StringUtils.isNotBlank(entity.getNotes()) && entity.getNotes().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "备注字数限制500字");
        }
        return null;
    }

    /**
     * 功能描述 发改局包详情接口
     *
     * @param entity 对象
     * @return 返回信息
     * @author gengzhiqiang
     * @date 2019/9/17 15:51
     */
    @PostMapping("/detailByIdForPkg")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailByIdForPkg(@RequestBody IplManageMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplManageMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        return success(service.detailByIdForPkg(entity.getId()));
    }

    /**
     * 功能描述 批量删除包
     *
     * @param ids id集合
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:17
     */
    @PostMapping("/removeByIdsForPkg")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIdsForPkg(@RequestBody List<Long> ids) {
        if (ids == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要删除的ID");
        }
        iplManageMainService.removeByIdsForPkg(ids,InnovationConstant.DEPARTMENT_ESB_ID);
        return success("删除成功");
    }
    /**
     * 功能描述 提交接口
     *
     * @param entity 实体
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/submit")
    public Mono<ResponseEntity<SystemResponse<Object>>> submit(@RequestBody IplManageMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,IplManageMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_ESB_ID);
        iplManageMainService.submit(entity);
        return success("操作成功");
    }

    /**
     * 功能描述  导出接口
     * @param id 数据id
     * @return 数据流
     * @author gengzhiqiang
     * @date 2019/10/11 11:07
     */
//    @GetMapping({"/export/excel"})
//    public Mono<ResponseEntity<byte[]>> exportExcel(@RequestParam("id") Long id) {
//        if (id == null) {
//            throw UnityRuntimeException.newInstance()
//                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
//                    .message("未获取到要导出的id").build();
//        }
//        IplManageMain entity = IplManageMain.newInstance().build();
//        entity.setId(id);
//        entity = service.detailByIdForPkg(entity.getId());
//        String filename = entity.getTitle();
//        byte[] content;
//        HttpHeaders headers = new HttpHeaders();
//        try {
//            content = service.export(entity);
//            //处理乱码
//            headers.setContentDispositionFormData("企业创新发展实时清单", new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xls");
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        } catch (Exception e) {
//            throw UnityRuntimeException.newInstance()
//                    .message(e.getMessage())
//                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
//                    .build();
//        }
//        return Mono.just(new ResponseEntity<>(content, headers, HttpStatus.CREATED));
//
//    }

    /////////////////////////////////////////////////基础数据/////////////////////////////////////////////////////////////////

    /**
     * 实时更新
     * @param iplLog
     * @return
     */
    @PostMapping("/updateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateStatus(@RequestBody IplLog iplLog) {
        IplDarbMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }

        iplLogService.updateStatus(entity, iplLog);

        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 主责单位实时更新协同单位处理状态
     * @param iplLog
     * @return
     */
    @PostMapping("/updateStatusByDuty")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateStatusByDuty(@RequestBody IplLog iplLog) {

        // 协助单位id
        Long idRbacDepartmentAssist = iplLog.getIdRbacDepartmentAssist();
        if (idRbacDepartmentAssist == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        // 主表id
        Long idIplMain = iplLog.getIdIplMain();
        IplDarbMain entity = service.getById(idIplMain);
        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }

        // 修改状态、插入日志
        iplLogService.updateStatusByDuty(entity.getIdRbacDepartmentDuty(), entity.getId(), iplLog);

        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 新增协同事项
     * @param iplDarbMain
     *          idIplMain：主表id
     *          assists：协同单位map
     *              idRbacDepartmentAssist 协同单位id
     *              inviteInfo 邀请事项
     * @return
     */
    @PostMapping("/addAssistant")
    public Mono<ResponseEntity<SystemResponse<Object>>> addAssistant(@RequestBody IplDarbMain iplDarbMain) {
        // 主表数据
        IplDarbMain entity = service.getById(iplDarbMain.getId());
        if(entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST,SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        List<IplAssist> assists = iplDarbMain.getIplAssists();
        if (CollectionUtils.isEmpty(assists)){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        // 新增协同单位并记录日志
        iplAssistService.addAssistant(assists, entity);
        
        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 获取一页数据
     * @param pageEntity 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplDarbMain> pageEntity) {

        LambdaQueryWrapper<IplDarbMain> ew = wrapper(pageEntity);
        IPage<IplDarbMain> p = service.page(pageEntity.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);
    }

    /**
     * 获取一页数据
     * @param id 统一查询条件
     * @return
     */
    @GetMapping("/detailById/{id}")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@PathVariable("id") Long id) {
        IplDarbMain entity = service.getById(id);
        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }

        Map<String, Object> resultMap = iplAssistService.totalProcessAndAssists(id, entity.getIdRbacDepartmentDuty(), entity.getProcessStatus());
        resultMap.put("baseInfo", convert2Map(entity));
        return success(resultMap);
    }
    
    /**
     * 添加或修改
     * @param entity darb->Development and Reform Bureau\r\n\r\n实体
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IplDarbMain entity) {

        // TODO 校验

        if (entity.getId() == null){ // 新增
            String uuid = UUIDUtil.getUUID();
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            entity.setAttachmentCode(uuid);
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_DARB_ID);
            service.add(entity);
        }else { // 编辑
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
     * @param idsMap id列表用英文逗号分隔
     * @return
     */
    @PostMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>>  removeByIds(@RequestBody Map<String, String> idsMap) {
        String ids = idsMap.get("ids");
        if (StringUtils.isBlank(ids)){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        List<IplDarbMain> list = service.list(new LambdaQueryWrapper<IplDarbMain>().in(IplDarbMain::getId, ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA))));
        if (CollectionUtils.isNotEmpty(list)){
            List<Long> mainIds = new ArrayList<>();
            List<String> attachmentCodes = new ArrayList<>();
            list.forEach(e->{
                mainIds.add(e.getId());
                attachmentCodes.add(e.getAttachmentCode());
            });

            service.delByIds(mainIds, attachmentCodes);
        }

        return success();
    }

    /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplDarbMain> list){

        Map<Long, Object> collect_ = null;
        Set<Long> ids = new HashSet<>();
        if (CollectionUtils.isNotEmpty(list)){
            list.forEach(e->{
                ids.add(e.getIndustryCategory());
                ids.add(e.getDemandCategory());
                ids.add(e.getDemandItem());
            });

            List<SysCfg> values = sysCfgService.getValues(ids);
            collect_ = values.stream().collect(Collectors.toMap(BaseEntity::getId, mSysCfg::getCfgVal,(k1, k2)->k2));
        }

        Map<Long, Object> collect = collect_;

        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity, collect);
                }
                ,IplDarbMain::getId,IplDarbMain::getEnterpriseName,IplDarbMain::getProjectName,IplDarbMain::getContent,IplDarbMain::getTotalInvestment,IplDarbMain::getProjectProgress,IplDarbMain::getTotalAmount,IplDarbMain::getBank,IplDarbMain::getBond,IplDarbMain::getSelfRaise,IplDarbMain::getIncreaseTrustType,IplDarbMain::getWhetherIntroduceSocialCapital,IplDarbMain::getConstructionCategory,IplDarbMain::getConstructionStage,IplDarbMain::getConstructionModel,IplDarbMain::getContactPerson,IplDarbMain::getContactWay,IplDarbMain::getAttachmentCode
        );
    }

     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplDarbMain ent){
        Set<Long> ids = new HashSet<>(Arrays.asList(ent.getDemandCategory(), ent.getDemandItem(), ent.getIndustryCategory()));
        List<SysCfg> values = sysCfgService.getValues(ids);
        Map<Long, Object> collect = values.stream().collect(Collectors.toMap(BaseEntity::getId, mSysCfg::getCfgVal,(k1, k2)->k2));

        return JsonUtil.<IplDarbMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity, collect);
                }
                ,IplDarbMain::getId,IplDarbMain::getEnterpriseName,IplDarbMain::getProjectName,IplDarbMain::getContent,IplDarbMain::getTotalInvestment,IplDarbMain::getProjectProgress,IplDarbMain::getTotalAmount,IplDarbMain::getBank,IplDarbMain::getBond,IplDarbMain::getSelfRaise,IplDarbMain::getIncreaseTrustType,IplDarbMain::getWhetherIntroduceSocialCapital,IplDarbMain::getConstructionCategory,IplDarbMain::getConstructionStage,IplDarbMain::getConstructionModel,IplDarbMain::getContactPerson,IplDarbMain::getContactWay,IplDarbMain::getAttachmentCode
                ,IplDarbMain::getIndustryCategory,IplDarbMain::getDemandItem,IplDarbMain::getDemandCategory
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplDarbMain entity, Map<Long, Object> collect){

        m.put("industryCategoryId", entity.getIndustryCategory());
        m.put("demandItemId", entity.getDemandItem());
        m.put("demandCategoryId", entity.getDemandCategory());
        m.put("industryCategory", collect.get(entity.getIndustryCategory()));
        m.put("demandItem", collect.get(entity.getDemandItem()));
        m.put("demandCategory", collect.get(entity.getDemandCategory()));
        m.put("source", SourceEnum.ENTERPRISE.getId().equals(entity.getSource())?"企业":"发改局");
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
     * @param pageEntity 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<IplDarbMain> wrapper(PageEntity<IplDarbMain> pageEntity){
        LambdaQueryWrapper<IplDarbMain> ew = new LambdaQueryWrapper<>();
        if(pageEntity != null && pageEntity.getEntity()!= null){
            IplDarbMain entity = pageEntity.getEntity();

            // 行业类别
            Long industryCategory = entity.getIndustryCategory();
            if (industryCategory != null){
                ew.eq(IplDarbMain::getIndustryCategory, industryCategory);
            }

            // 企业名称
            String enterpriseName = entity.getEnterpriseName();
            if (StringUtils.isNotBlank(enterpriseName)){
                ew.like(IplDarbMain::getEnterpriseName, enterpriseName);
            }

            // 需求类别
            Long demandCategory = entity.getDemandCategory();
            if (demandCategory != null){
                ew.eq(IplDarbMain::getDemandCategory, demandCategory);
            }

            // 项目名称
            String projectName = entity.getProjectName();
            if(StringUtils.isNotBlank(projectName)){
                ew.like(IplDarbMain::getProjectName, projectName);
            }

            // 联系人
            String contactPerson = entity.getContactPerson();
            if (StringUtils.isNotBlank(contactPerson)){
                ew.like(IplDarbMain::getContactPerson, contactPerson);
            }

            // 联系方式
            String contactWay = entity.getContactWay();
            if (StringUtils.isNotBlank(contactWay)){
                ew.like(IplDarbMain::getContactWay, contactWay);
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-01");
            Calendar c = Calendar.getInstance();
            // 创建时间
            String creatTime = entity.getCreatTime();
            if (StringUtils.isNotBlank(creatTime)){
                String[] split = creatTime.split("-");

                c.set(Calendar.YEAR, Integer.parseInt(split[0]));
                c.set(Calendar.MONTH, Integer.parseInt(split[1])-1);

                String start = df.format(c.getTime());
                try {
                    Date startDate = df.parse(start);
                    c.add(Calendar.MONTH, 1);
                    String end = df.format(c.getTime());
                    Date endDate = df.parse(end);
                    ew.ge(IplDarbMain::getGmtCreate, startDate.getTime());
                    ew.lt(IplDarbMain::getGmtCreate, endDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // 更新时间
            String updateTime = entity.getUpdateTime();
            if (StringUtils.isNotBlank(updateTime)){
                String[] split = updateTime.split("-");

                c.set(Calendar.YEAR, Integer.parseInt(split[0]));
                c.set(Calendar.MONTH, Integer.parseInt(split[1])-1);

                String start = df.format(c.getTime());
                try {
                    Date startDate = df.parse(start);
                    c.add(Calendar.MONTH, 1);
                    String end = df.format(c.getTime());
                    Date endDate = df.parse(end);
                    ew.ge(IplDarbMain::getGmtModified, startDate.getTime());
                    ew.lt(IplDarbMain::getGmtModified, endDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // 来源
            Integer source = entity.getSource();
            if (source != null){
                ew.eq(IplDarbMain::getSource, source);
            }

            // 状态
            Integer status = entity.getStatus();
            if (status != null){
                ew.eq(IplDarbMain::getStatus, status);
            }

            // 备注
            Integer processStatus = entity.getProcessStatus();
            if (processStatus != null){
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
    public Mono<ResponseEntity<SystemResponse<Object>>> assists(@PathVariable("mainId") Long mainId){
        // 查询基本信息
        IplDarbMain entity = service.getById(mainId);

        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 主责单位id
        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();

        // 查询协同单位列表
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, mainId).orderByDesc(IplAssist::getGmtCreate);
        List<IplAssist> assists = iplAssistService.list(qw);
        if (CollectionUtils.isNotEmpty(assists)){
            assists.forEach(e->{
                e.setNameRbacDepartmentAssist(InnovationUtil.getDeptNameById(e.getIdRbacDepartmentAssist()));
            });
        }

        return success(assists);
    }

    /**
     * 总体进展
     * @param mainId
     * @return
     */
    @PostMapping("/totalProcess/{mainId}")
    public Mono<ResponseEntity<SystemResponse<Object>>> totalProcess(@PathVariable("mainId") Long mainId) {
        // 查询基本信息
        IplDarbMain entity = service.getById(mainId);

        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 主责单位id
        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();
        return success(iplAssistService.totalProcessAndAssists(mainId,idRbacDepartmentDuty, entity.getProcessStatus()).get("totalProcess"));
    }
}

