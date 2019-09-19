
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.DailyWorkStatusPackage;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.service.DailyWorkStatusPackageServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * 创新日常工作管理-工作动态需求表
 * @author zhang
 * 生成时间 2019-09-17 11:17:01
 */
@Controller
@RequestMapping("/dailyWorkStatusPackage")
public class DailyWorkStatusPackageController extends BaseWebController {
    @Autowired
    DailyWorkStatusPackageServiceImpl service;

    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPageForBase")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPageForBase(@RequestBody PageEntity<DailyWorkStatusPackage> search) {
        IPage<DailyWorkStatusPackage> list = service.listByPageForBase(search);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2List(list.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 审核角色分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPageForAll")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPageForAll(@RequestBody PageEntity<DailyWorkStatusPackage> search) {
        IPage<DailyWorkStatusPackage> list = service.listByPageForAll(search);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2List(list.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2List(List<DailyWorkStatusPackage> list) {
        return JsonUtil.<DailyWorkStatusPackage>ObjectToList(list,
                (m, entity) -> {
                    if (WorkStatusAuditingStatusEnum.exist(entity.getState())) {
                        entity.setStateName(WorkStatusAuditingStatusEnum.of(entity.getState()).getName());
                    }
                }, DailyWorkStatusPackage::getId, DailyWorkStatusPackage::getGmtSubmit,DailyWorkStatusPackage::getDeptName,
                DailyWorkStatusPackage::getTitle, DailyWorkStatusPackage::getState,DailyWorkStatusPackage::getStateName);
    }

    /**
     * 功能描述
     *
     * @param entity 保存计划
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody DailyWorkStatusPackage entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParam(entity);
        if (obj!=null){
            return obj;
        }
        service.saveEntity(entity);
        return success("操作成功");
    }

    /**
     * 功能描述 数据校验
     * @param entity 实体
     * @return 异常信息
     * @author gengzhiqiang
     * @date 2019/9/18 18:36
     */
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(DailyWorkStatusPackage entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, DailyWorkStatusPackage::getTitle, DailyWorkStatusPackage::getWorkStatusList);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getTitle().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "标题限制50字");
        }
        if (entity.getNotes().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "备注限制500字");
        }
        return null;
    }

    /**
     * 功能描述 详情接口
     *
     * @param entity 对象
     * @return 返回信息
     * @author gengzhiqiang
     * @date 2019/9/17 15:51
     */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody DailyWorkStatusPackage entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,DailyWorkStatusPackage::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        return success(service.detailById(entity));
    }

    /**
     * 功能描述 批量删除
     *
     * @param ids
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:17
     */
    @PostMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIds(@RequestBody List<Long> ids) {
        if (ids == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要删除的ID");
        }
        service.removeById(ids);
        return success("删除成功");
    }

    /**
     * 功能描述 提交接口
     *
     * @param entity
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/submit")
    public Mono<ResponseEntity<SystemResponse<Object>>> submit(@RequestBody DailyWorkStatusPackage entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,DailyWorkStatusPackage::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        service.submit(entity);
        return success("操作成功");
    }

    /**
     * 功能描述 通过驳回接口
     *
     * @param entity
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/passOrReject")
    public Mono<ResponseEntity<SystemResponse<Object>>> passOrReject(@RequestBody DailyWorkStatusPackage entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, DailyWorkStatusPackage::getComment,DailyWorkStatusPackage::getFlag,DailyWorkStatusPackage::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getComment().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "审核意见限制500字");
        }
        service.passOrReject(entity);
        return success("操作成功");
    }

    /**
     * 功能描述  导出接口
     * @param id 数据id
     * @return 数据流
     * @author gengzhiqiang
     * @date 2019/9/20 11:07
     */
    @GetMapping({"/export/excel"})
    public Mono<ResponseEntity<byte[]>> exportExcel(@RequestParam("id") Long id) {

        String filename="工作动态详情"+DateUtils.timeStamp2Date(System.currentTimeMillis(),"yyyy-MM-dd");
        byte[] content;
        HttpHeaders headers = new HttpHeaders();
        try {
            //根据flag 判断导出类型
            content = service.export(id);
            //处理乱码
            headers.setContentDispositionFormData("工作动态详情", new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xls");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            throw UnityRuntimeException.newInstance()
                    .message(e.getMessage())
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .build();
        }
        return Mono.just(new ResponseEntity<>(content, headers, HttpStatus.CREATED));

    }




}

