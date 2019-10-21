
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.SysSendSmsLog;
import com.unity.innovation.enums.SysMessageFlowStatusEnum;
import com.unity.innovation.service.SysSendSmsLogServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * sys_send_sms_log
 *
 * @author G
 * 生成时间 2019-10-17 21:24:33
 */
@Controller
@RequestMapping("/syssendsmslog")
public class SysSendSmsLogController extends BaseWebController {
    @Autowired
    SysSendSmsLogServiceImpl service;

    /**
     * 获取一页数据
     *
     * @param  pageEntity 统一查询条件
     * @return 一页数据
     * @author gengjiajia
     * @since 2019/10/18 11:33
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<SysSendSmsLog> pageEntity) {
        LambdaQueryWrapper<SysSendSmsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysSendSmsLog::getSort);
        SysSendSmsLog entity = pageEntity.getEntity();
        if(entity != null && StringUtils.isNotBlank(entity.getPhone())){
            wrapper.like(SysSendSmsLog::getPhone,entity.getPhone());
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
                , SysSendSmsLog::getId, SysSendSmsLog::getUserId, SysSendSmsLog::getPhone, SysSendSmsLog::getContent, SysSendSmsLog::getIdRbacDepartment, SysSendSmsLog::getDataSourceClass, SysSendSmsLog::getFlowStatus, SysSendSmsLog::getSort, SysSendSmsLog::getNotes
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
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
    }

    /**
     * 批量删除
     *
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>> del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(null);
    }
}

