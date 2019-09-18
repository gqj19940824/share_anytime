
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.DailyWorkStatusPackageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;







/**
 * 创新日常工作管理-工作动态需求表
 * @author zhang
 * 生成时间 2019-09-17 11:17:01
 */
@Controller
@RequestMapping("/dailyworkstatuspackage")
public class DailyWorkStatusPackageController extends BaseWebController {
    @Autowired
    DailyWorkStatusPackageServiceImpl service;

}

