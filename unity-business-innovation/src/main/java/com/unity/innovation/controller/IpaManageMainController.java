package com.unity.innovation.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.service.IpaManageMainServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

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

    /**
     * 新增或者编辑活动管理
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/17 10:17 上午
     */
    @PostMapping("saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IpaManageMain entity){
        // 新增和编辑需要登录
        Customer customer = LoginContextHolder.getRequestAttributes();
        // 新增
        if (entity.getId() == null){
            ipaManageMainService.add(entity);
        // 编辑
        }else {
            ipaManageMainService.edit(entity);
        }
        return success();
    }
}

