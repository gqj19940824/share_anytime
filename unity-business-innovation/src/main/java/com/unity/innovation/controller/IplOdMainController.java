
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
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.mIplOdMain;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
 * od->organization department
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
     * @param list 集合
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2List(List<IplOdMain> list) {
        return JsonUtil.<IplOdMain>ObjectToList(list,
                (m, entity) -> {
                },
                IplOdMain::getId, IplOdMain::getIndustryCategory, IplOdMain::getIndustryCategoryName, IplOdMain::getEnterpriseName,
                IplOdMain::getJdName,IplOdMain::getJobDemandNum, IplOdMain::getContactPerson, IplOdMain::getContactWay,
                IplOdMain::getGmtCreate, IplOdMain::getGmtModified, IplOdMain::getSource, IplOdMain::getSourceName,
                IplOdMain::getStatus, IplOdMain::getStatusName, IplOdMain::getProcessStatus, IplOdMain::getProcessStatusName, IplOdMain::getLatestProcess);
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
        if (obj!=null){
            return obj;
        }
        service.saveEntity(entity);
        return success("操作成功");
    }
    /**
     * 功能描述 数据校验
     * @param entity 实体
     * @return com.unity.common.exception.UnityRuntimeException
     * @author gengzhiqiang
     * @date 2019/9/17 15:49
     */
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(IplOdMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,  IplOdMain::getSource, IplOdMain::getIndustryCategory, IplOdMain::getEnterpriseName, IplOdMain::getEnterpriseIntroduction,
                IplOdMain::getJdName, IplOdMain::getDuty, IplOdMain::getMajorDemand, IplOdMain::getSpecificCause,
                IplOdMain::getJobDemandNum, IplOdMain::getMajorDemand, IplOdMain::getContactPerson, IplOdMain::getContactWay,mIplOdMain::getEmail);

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
        if (entity.getJobDemandNum().toString().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "岗位需求数量限制20字");
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
        resultMap = iplAssistService.totalProcessAndAssists(entity.getId(), entity.getIdRbacDepartmentDuty(), entity.getProcessStatus());
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
    public Mono<ResponseEntity<SystemResponse<Object>>> getAssistList(@RequestBody IplOdMain entity) {
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
        return success(iplAssistService.getAssistList(vo.getId(),vo.getIdRbacDepartmentDuty()));
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
        // 主表数据
        IplOdMain entity = service.getById(iplDarbMain.getId());
        if(entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST,SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        List<IplAssist> assists = iplDarbMain.getAssistList();
        if (CollectionUtils.isEmpty(assists)){
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
        // 协助单位id
        Long idRbacDepartmentAssist = iplLog.getIdRbacDepartmentAssist();
        if (idRbacDepartmentAssist == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        // 主表id
        Long idIplMain = iplLog.getIdIplMain();
        IplOdMain entity = service.getById(idIplMain);
        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        // 修改状态、插入日志
        iplLogService.updateStatusByDuty(entity.getIdRbacDepartmentDuty(), entity.getId(), iplLog);
        return success(InnovationConstant.SUCCESS);
    }

    /**
     * 功能描述 主责单位 实时更新 接口
     *
     * @param iplLog 对象
     * @return 返回信息
     * @author gengzhiqiang
     * @date 2019/9/17 15:51
     */
    @PostMapping("/updateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateStatus(@RequestBody IplLog iplLog) {
        IplOdMain entity = service.getById(iplLog.getIdIplMain());
        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        iplLogService.updateStatus(entity, iplLog);
        return success(InnovationConstant.SUCCESS);
    }

}

