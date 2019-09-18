
package com.unity.innovation.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.AttachmentServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

}

