
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.IplYzgtMainServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;







/**
 * od->yi zhuang guo tou
 * @author zhang
 * 生成时间 2019-09-25 14:51:40
 */
@Controller
@RequestMapping("/iplyzgtmain")
public class IplYzgtMainController extends BaseWebController {
    @Resource
    IplYzgtMainServiceImpl service;
    

}

