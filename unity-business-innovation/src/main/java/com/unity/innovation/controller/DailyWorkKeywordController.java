
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.DailyWorkKeywordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 创新日常工作管理-关键字中间表
 * @author zhang
 * 生成时间 2019-09-17 11:17:02
 */
@Controller
@RequestMapping("/dailyworkkeyword")
public class DailyWorkKeywordController extends BaseWebController {
    @Autowired
    DailyWorkKeywordServiceImpl service;

}

