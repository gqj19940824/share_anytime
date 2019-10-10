
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.dao.AttachmentDao;
import com.unity.innovation.entity.Attachment;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: AttachmentService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-06-20 16:04:14
 *
 * @author qinhuanhuan
 * @since JDK 1.8
 */
@SuppressWarnings("unchecked")
@Service
public class AttachmentServiceImpl extends BaseServiceImpl<AttachmentDao, Attachment> {

    /**
     * 批量保存附件
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-09-23 11:08
     */
    public void bachSave(String attachmentCode, List<Attachment> attachments){
        if (CollectionUtils.isNotEmpty(attachments)){
            attachments.forEach(e->e.setAttachmentCode(attachmentCode));

            saveBatch(attachments);
        }
    }

    /**
     * 功能描述 附件方法重写
     * @param attachmentCode 附件code
     * @param attachments    附件列表
     * @return java.lang.String
     * @author gengzhiqiang
     * @date 2019/7/5 16:24
     */
    //@Transactional(rollbackFor = Exception.class)
    public String updateAttachments(String attachmentCode, List<Attachment> attachments) {
        if (attachments == null){ attachments = Lists.newArrayList();}
        List<Attachment> attachmentList = this.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, attachmentCode));
        if(CollectionUtils.isEmpty(attachmentList) ){
            attachments.forEach(i->i.setAttachmentCode(attachmentCode));
            if (CollectionUtils.isNotEmpty(attachments)){
                this.saveBatch(attachments);
            }
            return attachmentCode;
        }

        Set<Long> collect = attachments.stream().filter(a->a.getId() != null).map(Attachment::getId).collect(Collectors.toSet());
        //新增的附件列表
        List<Attachment> attachmentAdds = attachments.stream().filter(i -> (i.getId() == null || i.getId() < 0) && StringUtils.isNotEmpty(i.getUrl())).collect(Collectors.toList());
        attachmentAdds.forEach(i->i.setAttachmentCode(attachmentCode));
        if (CollectionUtils.isNotEmpty(attachmentAdds)){
            this.saveBatch(attachmentAdds);
        }
        //删除的附件列表
        //Set<Long> finalCollect = collect;
        Set<Long> set = attachmentList.stream().filter(i -> !collect.contains(i.getId())).map(Attachment::getId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(set)){
            this.removeByIds(set);
        }
        return attachmentCode;
    }

}
