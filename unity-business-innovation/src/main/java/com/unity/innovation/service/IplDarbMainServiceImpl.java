package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.dao.IplDarbMainDao;

import java.util.*;

/**
 * ClassName: IplDarbMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:36
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplDarbMainServiceImpl extends BaseServiceImpl<IplDarbMainDao, IplDarbMain> {

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Autowired
    private IplLogServiceImpl iplLogService;

    @Autowired
    private IplAssistServiceImpl iplAssistService;

    @Autowired
    private RedisSubscribeServiceImpl redisSubscribeService;

    public void updateStatus(IplDarbMain entity, IplLog iplLog){
        // 主责单位id
        Long idRbacDepartmentDuty = entity.getIdRbacDepartmentDuty();
        Long id = entity.getId();

        Customer customer = LoginContextHolder.getRequestAttributes();
        Long customerIdRbacDepartment = customer.getIdRbacDepartment();
        // 主责单位
        if (idRbacDepartmentDuty.equals(customerIdRbacDepartment)){
//            iplLog.setIdRbacDepartmentAssist(0L);

            // 判断状态，如果主责单位把主表完结，需要改主表状态 TODO 并且改协同表状态，各插入一个日志和协同表的redis
            Integer dealStatus = iplLog.getDealStatus();
            if (IplStatusEnum.DONE.getId().equals(dealStatus)){
                // 更新主表状态
                entity.setStatus(IplStatusEnum.DONE.getId());
                updateById(entity);

                StringBuilder builder = new StringBuilder("关闭");

                List<IplAssist> assists = iplAssistService.getAssists(idRbacDepartmentDuty, id);
                assists.forEach(e->{
                    builder.append(e.getNameRbacDepartmentAssist()).append("、");
                    e.setDealStatus(IplStatusEnum.DONE.getId());
                });
                if (builder.indexOf("、")>0){
                    builder.deleteCharAt(builder.length()-1);
                }
                builder.append("协同邀请");

                // 批量更新协同单位状态
                iplAssistService.updateBatchById(assists);

                // 主责记录日志
                IplLog.newInstance().dealStatus(dealStatus).idRbacDepartmentDuty(idRbacDepartmentDuty).idRbacDepartmentAssist(0L).idIplMain(id).processInfo(builder.toString());


            }
        }else {
            iplLog.setIdRbacDepartmentAssist(customerIdRbacDepartment);
        }

        iplLog.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
        iplLogService.save(iplLog); // TODO 更改redis
    }

    @Transactional(rollbackFor = Exception.class)
    public Long add(IplDarbMain entity) {

        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachmentService.bachSave(entity.getAttachmentCode(), attachments);
        }

        save(entity);

        // 设置处理超时时间
        redisSubscribeService.saveSubscribeInfo(entity.getId() + "", ListTypeConstants.DEAL_OVER_TIME, ListTypeConstants.IPL_DARB);

        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(IplDarbMain entity) {
        // 保存附件
        Long id = entity.getId();
        IplDarbMain byId = getById(id);
        if (byId == null){
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        List<Attachment> attachments = entity.getAttachments();
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachmentService.updateAttachments(byId.getAttachmentCode(), attachments);
        }

        // 保存修改
        updateById(entity);

        // 更新超时时间
        Integer status = entity.getStatus();
        // 设置处理超时时间
        if (IplStatusEnum.UNDEAL.getId().equals(status)){
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "", ListTypeConstants.DEAL_OVER_TIME, ListTypeConstants.IPL_DARB);
        // 设置更新超时时间
        }else if (IplStatusEnum.DEALING.getId().equals(status)){
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "", ListTypeConstants.UPDATE_OVER_TIME, ListTypeConstants.IPL_DARB);

            // 非"待处理"状态才记录日志
            Integer lastDealStatus = iplLogService.getLastDealStatus(id, entity.getIdRbacDepartmentDuty());
            IplLog iplLog = IplLog.newInstance().idIplMain(id).idRbacDepartmentAssist(0L)
                    .processInfo("更新基本信息").idRbacDepartmentDuty(entity.getIdRbacDepartmentDuty()).dealStatus(lastDealStatus).build();
            iplLogService.save(iplLog);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delByIds(List<Long> mainIds, List<String> attachmentCodes) {

        // 删除主表
        removeByIds(mainIds);

        // 批量删除主表附带的日志、协同、附件，调用方法必须要有事物
        iplAssistService.batchDel(mainIds, InnovationConstant.DEPARTMENT_DARB_ID, attachmentCodes);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByDuty(IplAssist iplAssist, IplLog iplLog, Long idRbacDepartmentDuty, Long idRbacDepartmentAssist, Long idIplMain) {
        iplAssist.setDealStatus(iplLog.getDealStatus());
        iplAssistService.updateById(iplAssist);

        // 主责单位改变协同单位的状态需要向协同单位和主责单位的操作日志中同时插入一条记录
        IplLog assistDeptLog = IplLog.newInstance().dealStatus(iplLog.getDealStatus()).idRbacDepartmentDuty(idRbacDepartmentDuty).idIplMain(idIplMain).processInfo("主责单位改变状态").idRbacDepartmentAssist(idRbacDepartmentAssist).build();
        IplLog dutyDeptLog = IplLog.newInstance().dealStatus(iplLog.getDealStatus()).idRbacDepartmentDuty(idRbacDepartmentDuty).idIplMain(idIplMain).processInfo("主责单位改变状态").idRbacDepartmentAssist(0L).build();

        iplLogService.save(assistDeptLog);
        iplLogService.save(dutyDeptLog);

        // TODO 更新redis
    }

    /**
     * 新增协同单位
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-09-25 18:52
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAssistant(IplLog iplLog, List<IplAssist> assistList){
        iplAssistService.saveBatch(assistList);
        iplLogService.save(iplLog);

        // 更新主表两个状态、更新redis
    }
}