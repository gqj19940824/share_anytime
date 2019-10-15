
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.innovation.service.InfoDeptSatbServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 路演企业信息管理-科技局-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
@Controller
@RequestMapping("/infoDeptSatb")
public class InfoDeptSatbController extends BaseWebController {
    @Autowired
    InfoDeptSatbServiceImpl service;
    



}

