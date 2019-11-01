
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.constant.ParamConstants;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * EnterpriseServiceBreau->esb;\r\nInnovationPublishList->ip
 *
 * @author zhang
 * 生成时间 2019-09-25 14:51:39
 */
@Controller
@RequestMapping("/iplEsbMain")
public class IplEsbMainController extends BaseWebController {

    @Resource
    IplEsbMainServiceImpl service;

    @Resource
    private SysCfgServiceImpl sysCfgService;

    @Resource
    private IplAssistServiceImpl iplAssistService;

    @Resource
    private IplManageMainServiceImpl iplManageMainService;

    @Resource
    private IplLogServiceImpl iplLogService;

    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplEsbMain> search) {
        service.check();
        IPage<IplEsbMain> list = service.listByPage(search);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2List(list.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return java.util.List<java.util.Map   <   java.lang.String   ,   java.lang.Object>> 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2List(List<IplEsbMain> list) {
        return JsonUtil.<IplEsbMain>ObjectToList(list,
                (m, entity) -> {
                }, IplEsbMain::getId, IplEsbMain::getIndustryCategory, IplEsbMain::getIndustryCategoryName, IplEsbMain::getEnterpriseName
                , IplEsbMain::getSummary, IplEsbMain::getContactPerson, IplEsbMain::getContactWay, IplEsbMain::getGmtCreate
                , IplEsbMain::getGmtModified, IplEsbMain::getSource, IplEsbMain::getSourceName, IplEsbMain::getStatus
                , IplEsbMain::getStatusName, IplEsbMain::getProcessStatus, IplEsbMain::getProcessStatusName, IplEsbMain::getLatestProcess, IplEsbMain::getNewProductAndTech);
    }

    /**
     * 功能描述 获取行业类别下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    @PostMapping("/getTypeList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getKeyList() {
        service.check();
        return success(sysCfgService.getSysList1(SysCfgEnum.THREE.getId()));
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
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplEsbMain entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParam(entity);
        if (obj != null) {
            return obj;
        }
        service.saveEntity(entity);
        return success("操作成功");
    }

    /**
     * 功能描述 数据校验
     *
     * @param entity 实体
     * @return com.unity.common.exception.UnityRuntimeException
     * @author gengzhiqiang
     * @date 2019/9/17 15:49
     */
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(IplEsbMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplEsbMain::getIndustryCategory, IplEsbMain::getEnterpriseName
                , IplEsbMain::getEnterpriseProfile, IplEsbMain::getSummary, IplEsbMain::getContactPerson, IplEsbMain::getContactWay);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (StringUtils.isBlank(entity.getNewTech()) && StringUtils.isBlank(entity.getNewProduct())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "新产品/新技术必填其一");
        }
        if (entity.getEnterpriseName().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业名称字数限制50字");
        }
        if (entity.getEnterpriseProfile().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业简介字数限制500字");
        }
        if (entity.getSummary().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "概述字数限制20字");
        }
        if (StringUtils.isNotBlank(entity.getNewProduct()) && entity.getNewProduct().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "新产品字数限制500字");
        }
        if (StringUtils.isNotBlank(entity.getNewTech()) && entity.getNewTech().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "新技术字数限制500字");
        }
        if (entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系人字数限制20字");
        }
        if (entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系方式字数限制20字");
        }
        return null;
    }

    /**
     * 功能描述 批量删除
     *
     * @param ids id集合
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:17
     */
    @PostMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIds(@RequestBody List<Long> ids) {
        service.check();
        if (ids == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要删除的ID");
        }
        service.removeById(ids);
        return success("删除成功");
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
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplEsbMain entity) {
        service.check();
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplEsbMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        entity = service.detailById(entity);
        //日志公共方法
        Map<String, Object> resultMap = iplAssistService.totalProcessAndAssists(entity.getId(), entity.getIdRbacDepartmentDuty(), entity.getProcessStatus(), BizTypeEnum.ENTERPRISE.getType());
        resultMap.put("baseInfo", entity);
        return success(resultMap);
    }

    /**
     * 功能描述 获取协同单位下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    @PostMapping("/getAssistList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getAssistList(@RequestBody IplEsbMain entity) {
        service.check();
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplEsbMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        //主表id  数据集合
        IplEsbMain vo = service.getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        return success(iplAssistService.getAssistList(vo.getId(), BizTypeEnum.ENTERPRISE.getType()));
    }

    /**
     * 功能描述 添加 协同事项 接口
     *
     * @param iplDarbMain 对象
     * @return 返回信息
     * @author gengzhiqiang
     * @date 2019/9/17 15:51
     */
    @PostMapping("/addAssist")
    public Mono<ResponseEntity<SystemResponse<Object>>> addAssistant(@RequestBody IplEsbMain iplDarbMain) {
        service.check();
        // 主表数据
        IplEsbMain entity = service.getById(iplDarbMain.getId());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        List<IplAssist> assists = iplDarbMain.getAssistList();
        if (CollectionUtils.isEmpty(assists)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        // 新增协同单位并记录日志
        iplAssistService.addAssistant(assists, entity);

        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 功能描述 主责单位开启关闭协同单位接口
     *
     * @param iplLog 对象
     * @return 返回信息
     * @author gengzhiqiang
     * @date 2019/9/17 15:51
     */
    @PostMapping("/dealAssist")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateStatusByDuty(@RequestBody IplLog iplLog) {
        service.check();
        // 协助单位id
        Long idRbacDepartmentAssist = iplLog.getIdRbacDepartmentAssist();
        if (idRbacDepartmentAssist == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        // 主表id
        Long idIplMain = iplLog.getIdIplMain();
        IplEsbMain entity = service.getById(idIplMain);
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 修改状态、插入日志
        iplLogService.updateStatusByDuty(entity, iplLog);
        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 主责单位实时更新
     *
     * @param iplLog
     * @return
     */
    @PostMapping("/dutyUpdateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> dutyUpdateStatus(@RequestBody IplLog iplLog) {
        service.check();
        IplEsbMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        Integer dealStatus = iplLog.getDealStatus();
        if (dealStatus == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        iplLogService.dutyUpdateStatus(entity, iplLog);

        return success();
    }

    /**
     * 实时更新
     *
     * @param iplLog
     * @return
     */
    @PostMapping("/assistUpdateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> assistUpdateStatus(@RequestBody IplLog iplLog) {
        service.check();
        IplEsbMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        Integer dealStatus = iplLog.getDealStatus();
        if (dealStatus == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        iplLogService.assistUpdateStatus(entity, iplLog);

        return success();
    }

    /**
     * 功能描述  导出接口
     *
     * @param id 数据id
     * @return 数据流
     * @author gengzhiqiang
     * @date 2019/10/11 11:07
     */
    @GetMapping({"/export/excel"})
    public Mono<ResponseEntity<byte[]>> exportExcel(@RequestParam("id") Long id) {
        service.check();
        if (id == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到要导出的id").build();
        }
        IplManageMain entity = IplManageMain.newInstance().build();
        entity.setId(id);
        entity = iplManageMainService.getById(entity.getId());
        if (entity == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        String filename = entity.getTitle();
        byte[] content;
        HttpHeaders headers = new HttpHeaders();
        try {
            content = service.export(entity);
            //处理乱码
            headers.setContentDispositionFormData("企业创新发展实时清单", new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xls");
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

