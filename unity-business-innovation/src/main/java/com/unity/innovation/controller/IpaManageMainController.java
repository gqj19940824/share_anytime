package com.unity.innovation.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.IpaManageMainServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 创新发布活动-管理-主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:32
 */
@Controller
@RequestMapping("/ipamManageMain")
public class IpaManageMainController extends BaseWebController {

    @Resource
    private IpaManageMainServiceImpl ipaManageMainService;


}

