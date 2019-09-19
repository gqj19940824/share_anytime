
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.DailyWorkPackageServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;


/**
 * 创新日常工作管理-工作动态与需求中间表
 * @author zhang
 * 生成时间 2019-09-17 11:17:02
 */
@Controller
@RequestMapping("/dailyWorkPackage")
public class DailyWorkPackageController extends BaseWebController {

    @Resource
    DailyWorkPackageServiceImpl service;


}

