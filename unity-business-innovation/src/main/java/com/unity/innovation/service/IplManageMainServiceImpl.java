package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplDarbMainSnapshot;
import com.unity.innovation.entity.generated.IplmMainIplMain;
import com.unity.innovation.enums.IplmStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.dao.IplManageMainDao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ClassName: IplManageMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:37
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplManageMainServiceImpl extends BaseServiceImpl<IplManageMainDao, IplManageMain> {

    @Autowired
    private IplDarbMainSnapshotServiceImpl iplDarbMainSnapshotService;
    @Autowired
    private IplDarbMainServiceImpl iplDarbMainService;
    @Autowired
    private AttachmentServiceImpl attachmentService;


    @Transactional(rollbackFor = Exception.class)
    public void add(IplManageMain entity) {

        String uuid = UUIDUtil.getUUID();
        // 保存发改局
        List<Long> idiplDarbMains = entity.getIdiplDarbMains();
        if (CollectionUtils.isNotEmpty(idiplDarbMains)){
            LambdaQueryWrapper<IplDarbMain> lq = new LambdaQueryWrapper();
            lq.in(IplDarbMain::getId, idiplDarbMains);
            List<IplDarbMain> iplDarbMains = iplDarbMainService.list(lq);
            List<IplDarbMainSnapshot> iplDarbMainSnapshots = new ArrayList<>();
            if (CollectionUtils.isEmpty(iplDarbMains)){
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
            }
            // 复制snapshot
            iplDarbMains.forEach(e->iplDarbMainSnapshots.add(InnovationUtil.Copy(e, IplDarbMainSnapshot.newInstance().build())));
            iplDarbMainSnapshotService.saveBatch(iplDarbMainSnapshots);

            // 保存管理主表
            entity.setAttachmentCode(uuid);
            entity.setStatus(IplmStatusEnum.UNCOMMIT.getId());
            save(entity);

            // 保存关联表
            iplDarbMainSnapshots.forEach(e->{
                IplmMainIplMain.newInstance().idIplMain(e.getId()).idIplmMain(entity.getId()).idRbacDepartmentDuty(e.getIdRbacDepartmentDuty());
            });
        }

        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if(CollectionUtils.isNotEmpty(attachments)){
            attachmentService.bachSave(uuid, attachments);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(IplManageMain entity) {

//        String uuid = entity.getAttachmentCode();
//        // 保存发改局
//        List<Long> idiplDarbMains = entity.getIdiplDarbMains();
//        if (CollectionUtils.isNotEmpty(idiplDarbMains)){
//            LambdaQueryWrapper<IplDarbMain> lq = new LambdaQueryWrapper();
//            lq.in(IplDarbMain::getId, idiplDarbMains);
//            List<IplDarbMain> iplDarbMains = iplDarbMainService.list(lq);
//            List<IplDarbMainSnapshot> iplDarbMainSnapshots = new ArrayList<>();
//            if (CollectionUtils.isEmpty(iplDarbMains)){
//                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
//                        .message(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
//            }
//            // 复制snapshot
//            iplDarbMains.forEach(e->iplDarbMainSnapshots.add(InnovationUtil.Copy(e, IplDarbMainSnapshot.newInstance().build())));
//            iplDarbMainSnapshotService.saveBatch(iplDarbMainSnapshots);
//
//            // 保存管理主表
//            entity.setAttachmentCode(uuid);
//            entity.setStatus(IplmStatusEnum.UNCOMMIT.getId());
//            save(entity);
//
//            // 保存关联表
//            iplDarbMainSnapshots.forEach(e->{
//                IplmMainIplMain.newInstance().idIplMain(e.getId()).idIplmMain(entity.getId()).idRbacDepartmentDuty(e.getIdRbacDepartmentDuty());
//            });
//        }
//
//        // 保存附件
//        List<Attachment> attachments = entity.getAttachments();
//        if(CollectionUtils.isNotEmpty(attachments)){
//            attachmentService.bachSave(uuid, attachments);
//        }
    }
}
