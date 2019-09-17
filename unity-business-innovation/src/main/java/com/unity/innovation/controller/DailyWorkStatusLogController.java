
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.DailyWorkStatusLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;







/**
 * 创新日常工作管理-工作动态日志表
 * @author zhang
 * 生成时间 2019-09-17 11:17:01
 */
@Controller
@RequestMapping("/dailyworkstatuslog")
public class DailyWorkStatusLogController extends BaseWebController {
    @Autowired
    DailyWorkStatusLogServiceImpl service;

}

