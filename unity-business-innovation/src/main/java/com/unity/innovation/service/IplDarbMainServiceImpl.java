package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplLog;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.IplDarbMain;
import com.unity.innovation.dao.IplDarbMainDao;

import java.awt.print.Pageable;
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
                .eq(IplLog::getIdRbacDepartmentDuty, entity.getIdRbacDepartment())
                .orderByDesc(IplLog::getGmtCreate);
        IplLog last = iplLogService.getOne(qw, false);

        IplLog iplLog = IplLog.newInstance().idIplMain(entity.getId()).idRbacDepartmentAssist(0L)
                .processInfo("更新基本信息").processStatus(last.getProcessStatus()).idRbacDepartmentDuty(entity.getIdRbacDepartment()).build();
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
}