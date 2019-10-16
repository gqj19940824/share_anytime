
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.service.InfoDeptSatbServiceImpl;
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
 * 路演企业信息管理-科技局-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
@Controller
@RequestMapping("/infoDeptSatb")
public class InfoDeptSatbController extends BaseWebController {

    @Resource
    InfoDeptSatbServiceImpl service;

    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<InfoDeptSatb> search) {
        IPage<InfoDeptSatb> list = service.listByPage(search);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2List(list.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 数据整理
     * @param list 集合
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2List(List<InfoDeptSatb> list) {
        return JsonUtil.<InfoDeptSatb>ObjectToList(list,
                (m, entity) -> {
                },
                InfoDeptSatb::getId, InfoDeptSatb::getIndustryCategory, InfoDeptSatb::getIndustryCategoryName);
    }

}

