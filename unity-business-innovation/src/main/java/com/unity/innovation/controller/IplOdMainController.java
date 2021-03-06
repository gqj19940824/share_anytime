
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
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.entity.generated.mIplOdMain;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * od->organization department
 *
 * @author zhang
 * 生成时间 2019-10-14 09:47:49
 */
@Controller
@RequestMapping("/iplOdMain")
public class IplOdMainController extends BaseWebController {

    @Resource
    IplOdMainServiceImpl service;

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
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplOdMain> search) {
        IPage<IplOdMain> list = service.listByPage(search);
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
    private List<Map<String, Object>> convert2List(List<IplOdMain> list) {
        return JsonUtil.<IplOdMain>ObjectToList(list,
                (m, entity) -> {
                },
                IplOdMain::getId, IplOdMain::getIndustryCategory, IplOdMain::getIndustryCategoryName, IplOdMain::getEnterpriseName,
                IplOdMain::getJdName, IplOdMain::getJobDemandNum, IplOdMain::getMajorDemand, IplOdMain::getEnterpriseIntroduction,
                IplOdMain::getDuty, IplOdMain::getQualification, IplOdMain::getSpecificCause,
                IplOdMain::getContactPerson, IplOdMain::getContactWay, IplOdMain::getEmail,
                IplOdMain::getGmtCreate, IplOdMain::getGmtModified, IplOdMain::getSource, IplOdMain::getSourceName,
                IplOdMain::getStatus, IplOdMain::getStatusName, IplOdMain::getProcessStatus, IplOdMain::getProcessStatusName, IplOdMain::getLatestProcess,
                IplOdMain::getBizType, IplOdMain::getIdRbacDepartmentDuty);
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
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplOdMain entity) {
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
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(IplOdMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplOdMain::getIndustryCategory, IplOdMain::getEnterpriseName, IplOdMain::getEnterpriseIntroduction,
                IplOdMain::getJdName, IplOdMain::getDuty, IplOdMain::getMajorDemand, IplOdMain::getSpecificCause,
                IplOdMain::getJobDemandNum, IplOdMain::getMajorDemand, IplOdMain::getContactPerson, IplOdMain::getContactWay, mIplOdMain::getEmail);

        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getEnterpriseName().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业名称字数限制50字");
        }
        if (entity.getEnterpriseIntroduction().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业简介字数限制500字");
        }
        if (entity.getJdName().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "岗位需求名称字数限制20字");
        }
        if (entity.getMajorDemand().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "需求人员专业领域字数限制20字");
        }
        if (entity.getJobDemandNum().toString().length() > ParamConstants.PARAM_MAX_LENGTH_15) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "岗位需求数量限制15字");
        }
        if (entity.getDuty().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "工作职责字数限制500字");
        }
        if (entity.getQualification().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "任职资格字数限制500字");
        }
        if (entity.getSpecificCause().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "支持条件和福利待遇字数限制500字");
        }
        if (entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系人字数限制20字");
        }
        if (entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系方式字数限制20字");
        }
        if (entity.getEmail().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "邮箱字数限制50字");
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
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplOdMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplOdMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        entity = service.detailById(entity);
        //日志公共方法
        Map<String, Object> resultMap;
        resultMap = iplAssistService.totalProcessAndAssists(entity.getId(), entity.getIdRbacDepartmentDuty(), entity.getProcessStatus(), entity.getStatus(), BizTypeEnum.INTELLIGENCE.getType());
        resultMap.put("baseInfo", entity);

        // 用于前端判断是否允许用户更新完成情况时输入本次完成额度，如果缺口大于0则用户可以输入本次完成额度
        if (entity.getJobDemandNum() == null){
            resultMap.put("gap", 0);
        }else {
            BigDecimal raisedTotal = iplLogService.getRaisedTotal(entity.getId(), BizTypeEnum.INTELLIGENCE.getType());
            resultMap.put("gap", BigDecimal.valueOf(entity.getJobDemandNum()).subtract(raisedTotal));
        }

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
    public Mono<ResponseEntity<SystemResponse<Object>>> getAssistList(@RequestBody IplOdMain entity) {
        service.check();
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplOdMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        //主表id  数据集合
        IplOdMain vo = service.getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        return success(iplAssistService.getAssistList(vo.getId(), BizTypeEnum.INTELLIGENCE.getType()));
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
    public Mono<ResponseEntity<SystemResponse<Object>>> addAssistant(@RequestBody IplOdMain iplDarbMain) {
        service.check();
        // 主表数据
        IplOdMain entity = service.getById(iplDarbMain.getId());
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
        IplOdMain entity = service.getById(idIplMain);
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
        IplOdMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        Integer dealStatus = iplLog.getDealStatus();
        if (dealStatus == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 校验完成额度是否超标 超出过直接抛异常
        iplLogService.isTotalGeSum(entity.getId(),BizTypeEnum.INTELLIGENCE.getType(),
                new BigDecimal(entity.getJobDemandNum()),
                iplLog.getCompleteNum() == null ? BigDecimal.ZERO : new BigDecimal(iplLog.getCompleteNum()),
                3);
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
        IplOdMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        Integer dealStatus = iplLog.getDealStatus();
        if (dealStatus == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 校验完成额度是否超标 超出过直接抛异常
        iplLogService.isTotalGeSum(entity.getId(),BizTypeEnum.INTELLIGENCE.getType(),
                new BigDecimal(entity.getJobDemandNum()),
                iplLog.getCompleteNum() == null ? BigDecimal.ZERO : new BigDecimal(iplLog.getCompleteNum()),
                4);
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
        if (id == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到要导出的id").build();
        }
        IplManageMain entity = new IplManageMain();
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
            headers.setContentDispositionFormData("高端才智需求实时清单", new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xls");
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

