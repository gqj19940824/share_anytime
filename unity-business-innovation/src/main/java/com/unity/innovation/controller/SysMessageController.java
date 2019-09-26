
package com.unity.innovation.controller;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.entity.SysMessage;
import com.unity.innovation.enums.SysMessageDataSourceClassEnum;
import com.unity.innovation.enums.SysMessageDataSourceEnum;
import com.unity.innovation.service.SysMessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * sys_message
 *
 * @author G
 * 生成时间 2019-09-23 09:39:17
 */
@RestController
@RequestMapping("/sysMessage")
public class SysMessageController extends BaseWebController {
    @Autowired
    SysMessageServiceImpl service;

    /**
     * 获取一页数据
     *
     * @param  pageEntity 统一查询条件
     * @return 一页数据
     * @author gengjiajia
     * @since 2019/09/23 11:00
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<SysMessage> pageEntity) {
        PageElementGrid result = service.listByPage(pageEntity);
        return success(result);
    }

    /**
     * 获取系统消息下拉类别列表
     *
     * @return 类别列表
     * @author gengjiajia
     * @since 2019/09/23 11:00
     */
    @PostMapping("/getSysMessageClassList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getSysMessageClassList() {
        List<Map<String,Object>> list = Lists.newArrayList();
        SysMessageDataSourceClassEnum[] enums = SysMessageDataSourceClassEnum.values();
        for (SysMessageDataSourceClassEnum e : enums){
            Map<String,Object> map = Maps.newHashMap();
            map.put("id",e.getId());
            map.put("name",e.getName());
            list.add(map);
        }
        return success(list);
    }

    /**
     * 删除系统消息
     *
     * @param  sysMessage 包含系统id
     * @author gengjiajia
     * @since 2019/09/23 11:23
     */
    @PostMapping("/deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteById(@RequestBody SysMessage sysMessage) {
        if (sysMessage == null || sysMessage.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到数据ID");
        }
        service.deleteById(sysMessage.getId());
        return success("删除成功");
    }

    /**
     * 设置消息已读状态
     *
     * @param  sysMessage 包含系统id
     * @author gengjiajia
     * @since 2019/09/23 11:23
     */
    @PostMapping("/setMessageReadStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> setMessageReadStatus(@RequestBody SysMessage sysMessage) {
        if (sysMessage == null || sysMessage.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到数据ID");
        }
        service.setMessageReadStatus(sysMessage.getId());
        return success("设置成功");
    }

}

