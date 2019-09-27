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
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.service.*;
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
    private SysCfgServiceImpl sysCfgService;

    @Autowired
    private DicUtils dicUtils;



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
    public Mono<ResponseEntity<SystemResponse<Object>>> addAssistant(@RequestBody Map map) {
        // 主表id
        Long idIplMain = MapUtils.getLong(map, "idIplMain");
        IplDarbMain entity = service.getById(idIplMain);
        if(entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST,SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        List<Map> assists = (List<Map>) map.get("assists");
        if (CollectionUtils.isEmpty(assists)){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();
        List<IplAssist> assistList = new ArrayList<>();
        StringBuilder deptName = new StringBuilder();

        // 遍历协同单位组装数据
        assists.forEach(e->{
            Long idRbacDepartmentAssist = MapUtils.getLong(e, "idRbacDepartmentAssist");
            IplAssist assist = IplAssist.newInstance()
                    .idRbacDepartmentDuty(idRbacDepartmentDuty)
                    .dealStatus(IplStatusEnum.DEALING.getId())
                    .idIplMain(idIplMain)
                    .idRbacDepartmentAssist(idRbacDepartmentAssist)
                    .inviteInfo(MapUtils.getString(e, "inviteInfo"))
                    .build();
            assistList.add(assist);
            deptName.append(InnovationUtil.getUserNameById(idRbacDepartmentAssist) + "、");
        });

        // 拼接"处理进展"中的协同单位名称
        String nameStr = null;
        if(deptName.indexOf("、") > 0){
            nameStr = deptName.subSequence(0, deptName.lastIndexOf("、")).toString();
        }

        // 计算日志的状态
        Integer lastDealStatus = iplLogService.getLastDealStatus(idIplMain, idRbacDepartmentDuty);

        IplLog iplLog = IplLog.newInstance().idRbacDepartmentAssist(0L).processInfo("新增协同单位：" + nameStr).idIplMain(idIplMain).idRbacDepartmentDuty(idRbacDepartmentDuty).dealStatus(lastDealStatus).build();

        // 新增协同单位并记录日志
        service.addAssistant(iplLog, assistList);
        
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
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@PathVariable("id") Long id) {
        IplDarbMain entity = service.getById(id);

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
            entity.setIdRbacDepartmentDuty(10L);
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
        Set<Long> ids = new HashSet<>();
        if (CollectionUtils.isNotEmpty(list)){
            list.forEach(e->{
                ids.add(e.getIndustryCategory());
                ids.add(e.getDemandCategory());
                ids.add(e.getDemandItem());
            });
        }

        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity, ids);
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
        Set<Long> ids = new HashSet<>(Arrays.asList(ent.getDemandCategory(), ent.getDemandItem(), ent.getIndustryCategory()));
        return JsonUtil.<IplDarbMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity, ids);
                }
                ,IplDarbMain::getId,IplDarbMain::getEnterpriseName,IplDarbMain::getProjectName,IplDarbMain::getContent,IplDarbMain::getTotalInvestment,IplDarbMain::getProjectProgress,IplDarbMain::getTotalAmount,IplDarbMain::getBank,IplDarbMain::getBond,IplDarbMain::getSelfRaise,IplDarbMain::getIncreaseTrustType,IplDarbMain::getWhetherIntroduceSocialCapital,IplDarbMain::getConstructionCategory,IplDarbMain::getConstructionStage,IplDarbMain::getConstructionModel,IplDarbMain::getContactPerson,IplDarbMain::getContactWay
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplDarbMain entity, Set<Long> ids){

        List<Map<String, Object>> values = sysCfgService.getValues(ids);

        Map<Long, Object> collect = values.stream().collect(Collectors.toMap(e -> MapUtils.getLong(e, "id"), e -> MapUtils.getString(e, "cfg_val"),(k1,k2)->k2));

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

