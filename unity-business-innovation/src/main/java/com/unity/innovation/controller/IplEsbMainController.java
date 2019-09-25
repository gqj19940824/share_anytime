
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.IplEsbMainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;


/**
 * EnterpriseServiceBreau->esb;\r\nInnovationPublishList->ip
 * @author zhang
 * 生成时间 2019-09-25 14:51:39
 */
@Controller
@RequestMapping("/iplesbmain")
public class IplEsbMainController extends BaseWebController {
    @Resource
    IplEsbMainServiceImpl service;
    

}

