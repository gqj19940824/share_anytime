
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.DailyWorkPackage;
import com.unity.innovation.entity.DailyWorkStatus;
import com.unity.innovation.service.DailyWorkPackageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


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

