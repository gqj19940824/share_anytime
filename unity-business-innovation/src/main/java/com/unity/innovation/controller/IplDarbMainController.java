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
import com.unity.innovation.entity.IplDarbMain;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.AttachmentServiceImpl;
import com.unity.innovation.service.IplDarbMainServiceImpl;
import com.unity.innovation.service.IplLogServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private DicUtils dicUtils;

    /**
     * 保存实时更新
     * @param iplLog
     * @return
     */
    @PostMapping("/updateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateStatus(@RequestBody IplLog iplLog) {
        Long idIplMain = iplLog.getIdIplMain();
        IplDarbMain byId = service.getById(idIplMain);
        Long idRbacDepartment = byId.getIdRbacDepartment();

        iplLog.setIdRbacDepartmentDuty(idRbacDepartment);
        Customer customer = LoginContextHolder.getRequestAttributes();
        Long idRbacDepartment1 = customer.getIdRbacDepartment();
        if (idRbacDepartment.equals(idRbacDepartment1)){
            iplLog.setIdRbacDepartmentAssist(0L);
        }else {
            iplLog.setIdRbacDepartmentAssist(idRbacDepartment1);
        }

        iplLogService.save(iplLog);
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
            entity.setIdRbacDepartment(100L); // TODO 写死了主责单位id
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

