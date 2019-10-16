
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.utils.UUIDUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unity.innovation.entity.InfoDeptYzgt;
import com.unity.innovation.dao.InfoDeptYzgtDao;

import javax.annotation.Resource;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class InfoDeptYzgtServiceImpl extends BaseServiceImpl<InfoDeptYzgtDao, InfoDeptYzgt> {


    @Resource
    private AttachmentServiceImpl attachmentService;

    /**
    * 新增/修改
    *
    * @param entity 实体
    * @author JH
    * @date 2019/10/15 16:09
    */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateYzgt(InfoDeptYzgt entity) {
        if(entity.getId() == null) {
            //默认为提请发布
            entity.setStatus(YesOrNoEnum.NO.getType());
            String attachmentCode = UUIDUtil.getUUID();
            entity.setAttachmentCode(attachmentCode);
            super.save(entity);
            //保存附件
            attachmentService.updateAttachments(attachmentCode,entity.getAttachmentList());
        }else {
            InfoDeptYzgt old = super.getById(entity.getId());
            String attachmentCode = old.getAttachmentCode();
            super.updateById(entity);
            //保存附件
            attachmentService.updateAttachments(attachmentCode,entity.getAttachmentList());
        }
    }
}
