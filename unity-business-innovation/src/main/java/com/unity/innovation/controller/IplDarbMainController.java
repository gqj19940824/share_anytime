package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.DicConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.IplDarbMain;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.AttachmentServiceImpl;
import com.unity.innovation.service.IplAssistServiceImpl;
import com.unity.innovation.service.IplDarbMainServiceImpl;
import com.unity.innovation.service.IplLogServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    private DicUtils dicUtils;

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

        // 查询协同单位列表
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, mainId).orderByDesc(IplAssist::getGmtCreate);
        List<IplAssist> assists = iplAssistService.list(qw);

        // 查询处理日志列表
        LambdaQueryWrapper<IplLog> logqw = new LambdaQueryWrapper<>();
        logqw.eq(IplLog::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplLog::getIdIplMain, mainId).orderByDesc(IplLog::getGmtCreate);
        List<IplLog> logs = iplLogService.list(logqw);

        // 定义返回值
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logs)){
            // 按照协同单位的id分成子logs
            LinkedHashMap<Long, List<IplLog>> collect = logs.stream().collect(Collectors.groupingBy(IplLog::getIdRbacDepartmentAssist, LinkedHashMap::new, Collectors.toList()));

            // 主责单位处理日志
            Map<String, Object> mapDuty = new HashMap<>();
            mapDuty.put("department", InnovationUtil.getDeptNameById(idRbacDepartmentDuty));
            mapDuty.put("processStatus", entity.getProcessStatus());
            mapDuty.put("logs", collect.get(0L)); // 在日志表的协同单位字段中，主责单位的日志记录在该字段中存为0
            resultList.add(mapDuty);
            // 协同单位处理日志
            if (CollectionUtils.isNotEmpty(assists)){
                assists.forEach(e->{
                    Map<String, Object> map = new HashMap<>();
                    map.put("department", InnovationUtil.getDeptNameById(e.getIdRbacDepartmentAssist()));
                    map.put("processStatus", e.getProcessStatus());
                    map.put("logs", collect.get(e.getIdRbacDepartmentAssist()));
                    resultList.add(map);
                });
            }
        }

        return success(resultList);
    }

    /**
     * 实时更新
     * @param iplLog
     * @return
     */
    @PostMapping("/updateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateStatus(@RequestBody IplLog iplLog) {
        Long idIplMain = iplLog.getIdIplMain();
        IplDarbMain entity = service.getById(idIplMain);
        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 主责单位id
        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();

        iplLog.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
        Customer customer = LoginContextHolder.getRequestAttributes();
        Long customerIdRbacDepartment = customer.getIdRbacDepartment();
        if (idRbacDepartmentDuty.equals(customerIdRbacDepartment)){
            iplLog.setIdRbacDepartmentAssist(0L);
        }else {
            iplLog.setIdRbacDepartmentAssist(customerIdRbacDepartment);
        }

        iplLogService.save(iplLog);
        return success(null);
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
        // 主责单位id
        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();

        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, idIplMain).eq(IplAssist::getIdRbacDepartmentAssist, idRbacDepartmentAssist);
        IplAssist iplAssist = iplAssistService.getOne(qw);

        // 修改状态、插入日志
        service.updateStatusByDuty(iplAssist, iplLog, idRbacDepartmentDuty, idRbacDepartmentAssist, idIplMain);
        return success(null);
    }

    /**
     * 新增协同事项
     * @param map 统一查询条件
     * @return
     */
    @PostMapping("/addAssistant")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody Map map) {
        Long idIplMain = MapUtils.getLong(map, "idIplMain");
        IplDarbMain byId = service.getById(idIplMain);
        List<Map> assists = (List<Map>) map.get("assists");
        List<IplAssist> assistList = new ArrayList<>();
        assists.forEach(e->{
            IplAssist build = IplAssist.newInstance()
                    .idRbacDepartmentDuty(byId.getIdRbacDepartmentDuty())
                    .dealStatus(IplStatusEnum.DEALING.getId())
                    .idIplMain(idIplMain)
                    .idRbacDepartmentAssist(MapUtils.getLong(e, "idRbacDepartmentAssist"))
                    .inviteInfo(MapUtils.getString(e, "inviteInfo"))
                    .build();
            assistList.add(build);
        });
        iplAssistService.saveBatch(assistList);
        // TODO 插入日志
        return success(null);
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
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@PathVariable("id") String id) {
        IplDarbMain byId = service.getById(id);
        Map<String, Object> stringObjectMap = convert2Map(byId);
        return success(stringObjectMap);
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
            entity.setIdRbacDepartmentDuty(100L); // TODO 写死了主责单位id
            service.add(entity);
        }else { // 编辑
            // 没有登录会抛异常
            LoginContextHolder.getRequestAttributes();

            service.edit(entity);
        }

        return success(null);
    }

    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/removeByIds/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  removeByIds(@PathVariable("ids") String ids) {
        service.delByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(null);
    }

    /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplDarbMain> list){

        return JsonUtil.<IplDarbMain>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,IplDarbMain::getId,IplDarbMain::getEnterpriseName,IplDarbMain::getProjectName,IplDarbMain::getContent,IplDarbMain::getTotalInvestment,IplDarbMain::getProjectProgress,IplDarbMain::getTotalAmount,IplDarbMain::getBank,IplDarbMain::getBond,IplDarbMain::getSelfRaise,IplDarbMain::getIncreaseTrustType,IplDarbMain::getWhetherIntroduceSocialCapital,IplDarbMain::getConstructionCategory,IplDarbMain::getConstructionStage,IplDarbMain::getConstructionModel,IplDarbMain::getContactPerson,IplDarbMain::getContactWay
        );
    }

     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplDarbMain ent){
        return JsonUtil.<IplDarbMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,IplDarbMain::getId,IplDarbMain::getEnterpriseName,IplDarbMain::getProjectName,IplDarbMain::getContent,IplDarbMain::getTotalInvestment,IplDarbMain::getProjectProgress,IplDarbMain::getTotalAmount,IplDarbMain::getBank,IplDarbMain::getBond,IplDarbMain::getSelfRaise,IplDarbMain::getIncreaseTrustType,IplDarbMain::getWhetherIntroduceSocialCapital,IplDarbMain::getConstructionCategory,IplDarbMain::getConstructionStage,IplDarbMain::getConstructionModel,IplDarbMain::getContactPerson,IplDarbMain::getContactWay
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplDarbMain entity){

        m.put("industryCategory", dicUtils.getDicValueByCode(DicConstants.INDUSTRY_CATEGORY, entity.getIndustryCategory() + ""));
        m.put("demandItem", dicUtils.getDicValueByCode(DicConstants.DEMAND_ITEM, entity.getDemandItem() + ""));
        m.put("demandCategory", dicUtils.getDicValueByCode(DicConstants.DEMAND_CATEGORY, entity.getDemandCategory() + ""));
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
            // 行业类别
            IplDarbMain entity = pageEntity.getEntity();
            Integer industryCategory = entity.getIndustryCategory();
            if (industryCategory != null){
                ew.eq(IplDarbMain::getIndustryCategory, industryCategory);
            }

            // 企业名称
            String enterpriseName = entity.getEnterpriseName();
            if (StringUtils.isNotBlank(enterpriseName)){
                ew.like(IplDarbMain::getEnterpriseName, enterpriseName);
            }

            // 需求类别
            Integer demandCategory = entity.getDemandCategory();
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

            SimpleDateFormat df = new SimpleDateFormat("yyyyMM01");
            Calendar c = Calendar.getInstance();
            // 创建时间
            String creatTime = entity.getCreatTime();
            if (StringUtils.isNotBlank(creatTime)){
                String[] split = creatTime.split("-");

                c.set(Calendar.YEAR, Integer.parseInt(split[0]));
                c.set(Calendar.MONTH, Integer.parseInt(split[1]));

                String start = df.format(c.getTime());
                try {
                    Date startDate = df.parse(start);
                    c.add(Calendar.MONTH, 1);
                    String end = df.format(c.getTime());
                    Date endDate = df.parse(end);
                    ew.ge(IplDarbMain::getGmtCreate, startDate);
                    ew.lt(IplDarbMain::getGmtCreate, endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // 更新时间
            String updateTime = entity.getUpdateTime();
            if (StringUtils.isNotBlank(updateTime)){
                String[] split = updateTime.split("-");

                c.set(Calendar.YEAR, Integer.parseInt(split[0]));
                c.set(Calendar.MONTH, Integer.parseInt(split[1]));

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

        return ew;
    }
}

