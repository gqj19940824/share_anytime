
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.RedisConstants;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.HashRedisUtils;
import com.unity.innovation.entity.SysSendSmsLog;
import com.unity.innovation.enums.SysMessageDataSourceClassEnum;
import com.unity.innovation.enums.SysMessageFlowStatusEnum;
import com.unity.innovation.service.SysSendSmsLogServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * sys_send_sms_log
 *
 * @author G
 * 生成时间 2019-10-17 21:24:33
 */
@RestController
@RequestMapping("/syssendsmslog")
public class SysSendSmsLogController extends BaseWebController {
    @Autowired
    SysSendSmsLogServiceImpl service;
    @Autowired
    HashRedisUtils hashRedisUtils;

    /**
     * 获取一页数据
     *
     * @param  pageEntity 统一查询条件
     * @return 一页数据
     * @author gengjiajia
     * @since 2019/10/18 11:33
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<SysSendSmsLog> pageEntity) {
        LambdaQueryWrapper<SysSendSmsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysSendSmsLog::getSort);
        SysSendSmsLog entity = pageEntity.getEntity();
        if(entity != null){
            if(StringUtils.isNotBlank(entity.getPhone())){
                wrapper.like(SysSendSmsLog::getPhone,entity.getPhone());
            }
            if(entity.getDataSourceClass() != null){
                wrapper.eq(SysSendSmsLog::getDataSourceClass,entity.getDataSourceClass());
            }
            if(StringUtils.isNotBlank(entity.getContent())){
                wrapper.like(SysSendSmsLog::getContent,entity.getContent());
            }
            if(entity.getIdRbacDepartment() != null){
                wrapper.eq(SysSendSmsLog::getIdRbacDepartment,entity.getIdRbacDepartment());
            }
            if(entity.getGmtCreate() != null && entity.getGmtModified()!= null){
                wrapper.between(SysSendSmsLog::getGmtCreate,entity.getGmtCreate(),entity.getGmtModified());
            }else {
                if(entity.getGmtCreate() != null){
                    wrapper.ge(SysSendSmsLog::getGmtCreate,entity.getGmtCreate());
                }
                if(entity.getGmtModified() != null){
                    wrapper.le(SysSendSmsLog::getGmtModified,entity.getGmtModified());
                }
            }
        }
        IPage p = service.page(pageEntity.getPageable(), wrapper);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<SysSendSmsLog> list) {

        return JsonUtil.<SysSendSmsLog>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                , SysSendSmsLog::getId, SysSendSmsLog::getUserId, SysSendSmsLog::getPhone, SysSendSmsLog::getContent, SysSendSmsLog::getIdRbacDepartment, SysSendSmsLog::getDataSourceClass, SysSendSmsLog::getFlowStatus,SysSendSmsLog::getGmtCreate,SysSendSmsLog::getGmtModified,SysSendSmsLog::getSendStatus,SysSendSmsLog::getSourceId
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, SysSendSmsLog entity) {
        if (entity.getFlowStatus() != null) {
            m.put("flowStatusTitle", SysMessageFlowStatusEnum.ofName(entity.getFlowStatus()));
        }
        m.put("dataSourceClassTitle", SysMessageDataSourceClassEnum.ofName(entity.getDataSourceClass()));
        String name = hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT.concat(entity.getIdRbacDepartment().toString()), RedisConstants.NAME);
        m.put("nameRbacDepartment",name);
    }

    /**
     * 删除
     *
     * @param  log 包含短信id
     * @return  code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/11/07 19:31
     */
    @PostMapping("/deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteById(@RequestBody SysSendSmsLog log) {
        if(log == null || log.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到短信id");
        }
        service.removeById(log.getId());
        return success("删除成功");
    }

    /**
     * 获取短信管理类型下拉列表
     *
     * @return code 0 获取成功
     * @author gengjiajia
     * @since 2019/11/07 19:35
     */
    @PostMapping("/getDataSourceClassList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getDataSourceClassList() {
        return success(Arrays.stream(SysMessageDataSourceClassEnum.values()).filter(e -> e.getId() <= SysMessageDataSourceClassEnum.HELP.getId())
                .map(e -> {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", e.getId());
                    map.put("name", e.getName());
                    return map;
                }).collect(Collectors.toList()));
    }
}

