package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.IplDarbMain;
import com.unity.innovation.dao.IplDarbMainDao;

import java.util.List;

/**
 * 
 * ClassName: IplDarbMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:36
 * 
 * @author zhang 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplDarbMainServiceImpl extends BaseServiceImpl<IplDarbMainDao,IplDarbMain> {

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Autowired
    private IplLogServiceImpl iplLogService;

    @Autowired
    private IplAssistServiceImpl iplAssistService;

    @Transactional(rollbackFor = Exception.class)
    public Long add(IplDarbMain entity) {
        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachmentService.bachSave(UUIDUtil.getUUID(), attachments);
        }

        save(entity);

        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(IplDarbMain entity) {
        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachmentService.updateAttachments(entity.getAttachmentCode(), attachments);
        }

        // 保存修改
        updateById(entity);
        LambdaQueryWrapper<IplLog> qw = new LambdaQueryWrapper();
        qw.eq(IplLog::getIdIplMain, entity.getId())
                .eq(IplLog::getIdRbacDepartmentDuty, entity.getIdRbacDepartmentDuty())
                .orderByDesc(IplLog::getGmtCreate);
        IplLog last = iplLogService.getOne(qw, false);
        // 处理中
        Integer dealStatus = 2;
        if (last != null){
            dealStatus = last.getDealStatus();
        }
        IplLog iplLog = IplLog.newInstance().idIplMain(entity.getId()).idRbacDepartmentAssist(0L)
                .processInfo("更新基本信息").idRbacDepartmentDuty(entity.getIdRbacDepartmentDuty()).dealStatus(dealStatus).build();
        iplLogService.save(iplLog);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)){
            ids.forEach(e->{
                IplDarbMain byId = getById(e);
                if (byId != null){
                    String attachmentCode = byId.getAttachmentCode();
                    attachmentService.remove(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, attachmentCode));

                    removeById(e);
                }
            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByDuty(IplAssist iplAssist, IplLog iplLog, Long idRbacDepartmentDuty, Long idRbacDepartmentAssist, Long idIplMain){
        iplAssist.setDealStatus(iplLog.getDealStatus());
        iplAssistService.updateById(iplAssist);

        // 主责单位改变协同单位的状态需要向协同单位和主责单位的操作日志中同时插入一条记录
        IplLog assistDeptLog = IplLog.newInstance().dealStatus(iplLog.getDealStatus()).idRbacDepartmentDuty(idRbacDepartmentDuty).idIplMain(idIplMain).processInfo("主责单位改变状态").idRbacDepartmentAssist(idRbacDepartmentAssist).build();
        IplLog dutyDeptLog = IplLog.newInstance().dealStatus(iplLog.getDealStatus()).idRbacDepartmentDuty(idRbacDepartmentDuty).idIplMain(idIplMain).processInfo("主责单位改变状态").idRbacDepartmentAssist(0L).build();

        iplLogService.save(assistDeptLog);
        iplLogService.save(dutyDeptLog);
    }
}