
package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.service.AttachmentServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 *
 * @author qinhuanhuan
 * 生成时间 2019-06-20 16:04:14
 */
@Controller
@RequestMapping("/attachment")
public class AttachmentController extends BaseWebController {

    @Resource
    AttachmentServiceImpl attachmentService;

    /**
     * @desc: 通过附件code获取附件
     * @param: [attachment]
     * @return: reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author: vv
     * @date: 2019/7/26 19:41
     **/
    @PostMapping("/getAttachmentByCode")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> getAttachmentByCode(@RequestBody Attachment attachment) {
        if( CollectionUtils.isNotEmpty(attachment.getAttachmentCodeList())){
            return success(JsonUtil.<Attachment>ObjectToList(attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode,attachment.getAttachmentCodeList())),null
                    ,Attachment::getId,Attachment::getName,Attachment::getIsDeleted,Attachment::getSize,Attachment::getUrl,Attachment::getSizeLong,Attachment::getStatus,Attachment::getType
            ));
        }
        return success(InnovationConstant.SUCCESS);
    }

}

