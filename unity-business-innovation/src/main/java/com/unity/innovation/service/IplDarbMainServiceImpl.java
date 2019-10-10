package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class IplDarbMainServiceImpl extends BaseServiceImpl<IplDarbMainDao, IplDarbMain> {

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Autowired
    private IplLogServiceImpl iplLogService;

    @Autowired
    private IplAssistServiceImpl iplAssistService;

    @Autowired
    private RedisSubscribeServiceImpl redisSubscribeService;

    @Transactional(rollbackFor = Exception.class)
    public Long add(IplDarbMain entity) {

        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachmentService.bachSave(entity.getAttachmentCode(), attachments);
        }

        save(entity);

        // 设置处理超时时间
        redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME, entity.getIdRbacDepartmentDuty());

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
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME, entity.getIdRbacDepartmentDuty());
        // 设置更新超时时间
        }else if (IplStatusEnum.DEALING.getId().equals(status)){
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.UPDATE_OVER_TIME, entity.getIdRbacDepartmentDuty());

            // 非"待处理"状态才记录日志
            Integer lastDealStatus = iplLogService.getLastDealStatus(id, entity.getIdRbacDepartmentDuty());
            IplLog iplLog = IplLog.newInstance().idIplMain(id).idRbacDepartmentAssist(0L)
                    .processInfo("更新基本信息").idRbacDepartmentDuty(entity.getIdRbacDepartmentDuty()).dealStatus(lastDealStatus).build();
            iplLogService.save(iplLog);
        }
    }

    /**
     * 批量删除发改局数据
     *
     * @param mainIds 发改局表ids
     * @param attachmentCodes 发改局表attachmentCodes
     *
     * @author qinhuan
     * @since 2019-10-09 16:27
     */
    @Transactional(rollbackFor = Exception.class)
    public void delByIds(List<Long> mainIds, List<String> attachmentCodes) {

        // 删除主表
        removeByIds(mainIds);

        // 批量删除主表附带的日志、协同、附件，调用方法必须要有事物
        iplAssistService.batchDel(mainIds, InnovationConstant.DEPARTMENT_DARB_ID, attachmentCodes);
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
    public void addAssistant(IplLog iplLog, List<IplAssist> assistList, IplDarbMain entity){

        // 更新主表两个状态
        entity.setStatus(IplStatusEnum.DEALING.getId());
        entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
        updateById(entity);

        // 新增协同单位、保存处理日志、主表重设超时、设置协同单位超时
        iplAssistService.addAssist(iplLog, assistList);
    }
}