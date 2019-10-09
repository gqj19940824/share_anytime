
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.DailyWorkStatus;
import com.unity.innovation.entity.IplYzgtMain;
import com.unity.innovation.service.IplYzgtMainServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * od->yi zhuang guo tou
 * @author zhang
 * 生成时间 2019-09-25 14:51:40
 */
@RestController
@RequestMapping("/iplyzgtmain")
public class IplYzgtMainController extends BaseWebController {
    @Resource
    private IplYzgtMainServiceImpl service;



    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplYzgtMain> search) {
        IPage<IplYzgtMain> list = service.listByPage(search);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2List(list.getRecords())).build();
        return success(result);
    }



    /**
     * 功能描述 数据整理
     * @param list 集合
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 规范数据
     * @author zhangxiaogang
     * @date 2019/9/27 13:36
     */
    private List<Map<String, Object>> convert2List(List<IplYzgtMain> list) {
        return JsonUtil.<IplYzgtMain>ObjectToList(list,
                (m, entity) -> {
                }, IplYzgtMain::getId, IplYzgtMain::getContactPerson, IplYzgtMain::getContactWay, IplYzgtMain::getEnterpriseName);
    }
    

}

