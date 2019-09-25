
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.*;
import com.unity.innovation.entity.SysNoticeUser;
import com.unity.innovation.enums.IsSendEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;
import java.util.List;

import com.unity.innovation.service.SysNoticeServiceImpl;
import com.unity.innovation.entity.SysNotice;

import javax.annotation.Resource;


/**
 * 通知公告
 *
 * @author zhang
 * 生成时间 2019-09-23 15:00:35
 */
@RestController
@RequestMapping("/sysNotice")
public class SysNoticeController extends BaseWebController {
    @Resource
    SysNoticeServiceImpl service;


    /**
    * 列表查询 宣传部
    *
    * @param pageEntity 分页条件
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/24 11:20
    */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<SysNotice> pageEntity) {
        Page<SysNotice> pageable = pageEntity.getPageable();
        SysNotice entity = pageEntity.getEntity();
        LambdaQueryWrapper<SysNotice> ew = wrapper(entity);
        IPage<SysNotice> p = service.page(pageable, ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }

    @PostMapping("/listByPageOther")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPageOther(@RequestBody PageEntity<SysNotice> pageEntity) {

        Page<SysNotice> pageable = pageEntity.getPageable();
        SysNotice entity = pageEntity.getEntity();
        Customer customer = LoginContextHolder.getRequestAttributes();
        Long userId = customer.getId();
        entity.setUserId(userId);

        return success();
    }


    /**
    * 添加或修改
    *
    * @param entity 通知公告实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/24 11:19
    */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody SysNotice entity) {
        validate(entity);
        if (entity.getId() == null) {
            service.saveNotice(entity);
        } else {
            service.updateNotice(entity);
        }
        return success();
    }

    /**
     * 参数校验
     *
     * @param entity 参数
     * @author JH
     * @date 2019/9/23 18:07
     */
    private void validate(SysNotice entity) {
        if (entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("缺少参数").build();
        }
        //非空校验
        String result = ValidFieldUtil.checkEmptyStr(entity
                , ValidFieldFactory.emptyReg("标题不能为空! ", SysNotice::getTitle)
                , ValidFieldFactory.emptyReg("内容不能为空! ", SysNotice::getContent)
                , ValidFieldFactory.emptyReg("接收单位不能为空! ", SysNotice::getDepartmentIds));
        if (StringUtils.isNotEmpty(result)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message(result).build();
        }
        ValidFieldUtil.checkReg(entity, ValidFieldFactory.lengthReg(1, 20, "标题不能超过50字符!", SysNotice::getTitle));
        if (entity.getId() != null) {
            SysNotice old = service.getById(entity.getId());
            if (YesOrNoEnum.YES.getType() == old.getIsSend()) {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                        .message("已发送、无法修改！").build();
            }
        }
    }


    /**
     * 查询条件转换
     *
     * @param notice 统一查询对象
     * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.SysNotice>
     * @author JH
     * @date 2019/9/24 11:18
     */
    private LambdaQueryWrapper<SysNotice> wrapper(SysNotice notice) {

        LambdaQueryWrapper<SysNotice> ew = new LambdaQueryWrapper<>();

        if (notice.getIsSend() != null) {
            ew.eq(SysNotice::getIsSend, notice.getIsSend());
        }
        if (StringUtils.isNotEmpty(notice.getTitle())) {
            ew.like(SysNotice::getTitle, notice.getTitle());
        }
        if (notice.getGmtStart() != null) {
            ew.gt(SysNotice::getGmtSend, notice.getGmtStart());
        }
        if (notice.getGmtEnd() != null) {
            ew.lt(SysNotice::getGmtSend, notice.getGmtEnd());
        }
        ew.last(" order by is_send asc,gmt_send desc,gmt_modified desc");
        return ew;
    }


    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author JH
     * @date 2019/9/24 11:18
     */
    private List<Map<String, Object>> convert2List(List<SysNotice> list) {

        return JsonUtil.ObjectToList(list,
                (m, entity) ->
                    m.put("isSendName", IsSendEnum.of(entity.getIsSend()).getName())
                , SysNotice::getId, SysNotice::getTitle, SysNotice::getIsSend, SysNotice::getGmtSend
        );
    }




    /**
     * @param m      适配的结果
     * @param entity 需要适配的实体
     * @author JH
     * @date 2019/9/24 11:19
     */
    private void adapterField(Map<String, Object> m, SysNotice entity) {
        m.put("isSendName", IsSendEnum.of(entity.getIsSend()).getName());
    }


    /**
     * 批量删除
     *
     * @param ids id列表
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity < com.unity.common.pojos.SystemResponse < java.lang.Object>>>
     * @author JH
     * @date 2019/9/24 9:43
     */
    @PostMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIds(@RequestBody List<Long> ids) {
        if (CollectionUtils.isEmpty(ids) || CollectionUtils.isEmpty(service.list(new LambdaQueryWrapper<SysNotice>().in(SysNotice::getId, ids)))) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"要删除的数据不存在");
        }
        List<SysNotice> list = service.list(new LambdaQueryWrapper<SysNotice>().in(SysNotice::getId, ids).eq(SysNotice::getIsSend, YesOrNoEnum.YES.getType()));
        if (CollectionUtils.isNotEmpty(list)) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"存在已发送的数据、无法删除");
        }
        service.removeByIds(ids);
        return success();
    }


    /**
    * 详情接口
    *
    * @param entity 包含主键的实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/24 14:05
    */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody SysNotice entity) {
        if(entity == null || entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少id");
        }
        return success(service.detailById(entity.getId()));
    }

    @PostMapping("/getAttachmentListById")
    public Mono<ResponseEntity<SystemResponse<Object>>> getAttachmentListById(@RequestBody SysNotice entity) {
        if(entity == null || entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少id");
        }
        return success(service.getAttachmentListById(entity.getId()));
    }

    /**
    * 根据主表返回浏览情况
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/24 14:56
    */
    @PostMapping("/getReadInfo")
    public Mono<ResponseEntity<SystemResponse<Object>>> getReadInfo(@RequestBody SysNoticeUser entity) {
        if(entity == null || entity.getIdSysNotice() == null) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少主表id");
        }
        return success(service.getReadInfo(entity));
    }

}

