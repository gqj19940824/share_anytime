
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.ParamConstants;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.service.IplAssistServiceImpl;
import com.unity.innovation.service.IplSatbMainServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;


/**
 * 创新发布清单-科技局-主表
 *
 * @author G
 * 生成时间 2019-10-08 17:03:09
 */
@RestController
@RequestMapping("/iplsatbmain")
public class IplSatbMainController extends BaseWebController {
    @Autowired
    IplSatbMainServiceImpl service;
    @Resource
    private IplAssistServiceImpl iplAssistService;
    /**
     * 成长目标投资实时清单-科技局
     *
     * @param pageEntity 包含分页及检索条件
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/08 17:23
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplSatbMain> pageEntity) {
        PageElementGrid result = service.listByPage(pageEntity);
        return success(result);
    }

    /**
     * 新增or修改成长目标投资实时清单
     *
     * @param entity 实时清单信息
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/08 17:26
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplSatbMain entity) {
        //校验
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName
                , IplSatbMain::getDemandCategory, IplSatbMain::getProjectName, IplSatbMain::getProjectAddress, IplSatbMain::getProjectIntroduce
                , IplSatbMain::getTotalAmount, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay,IplSatbMain::getSource);
        if (StringUtils.isNotEmpty(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        //数据长度校验
        if(entity.getEnterpriseName().length() > ParamConstants.PARAM_MAX_LENGTH_50
                || entity.getProjectName().length() > ParamConstants.PARAM_MAX_LENGTH_50
                || entity.getTechDemondInfo().length() > ParamConstants.PARAM_MAX_LENGTH_50){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"企业名称、项目名称及技术需求情况仅支持最长50个字");
        }
        if(entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20
                || entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"联系人、联系电话仅支持最长20个字");
        }
        if(entity.getProjectAddress().length() > ParamConstants.PARAM_MAX_LENGTH_100){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"项目地址仅支持最长100个字");
        }
        if(entity.getProjectIntroduce().length() > ParamConstants.PARAM_MAX_LENGTH_500){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"项目介绍支持最长500个字");
        }
        if(entity.getTotalAmount() != null){
            if(entity.getBank() == null && entity.getBond() == null && entity.getRaise() == null){
                return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"银行、债券、自筹至少填写一项");
            } else {
                InnovationUtil.checkAmont(entity.getTotalAmount(),entity.getBank(),entity.getBond(),entity.getRaise());
            }
        } else if(entity.getBank() != null || entity.getBond() != null || entity.getRaise() != null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"填写银行、债券、自筹任意一项后，还需填写需求总额");
        }
        service.saveOrUpdateIplSatbMain(entity);
        return success("更新成功");
    }

    /**
     * 删除实时清单
     *
     * @param entity 包含清单id
     * @return code -> 0 表示删除成功
     * @author gengjiajia
     * @since 2019/10/08 17:30
     */
    @PostMapping("/deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteById(@RequestBody IplSatbMain entity) {
        check();
        service.deleteById(entity.getId());
        return success("删除成功");
    }

    /**
     * 实时清单详情
     *
     * @param entity 包含清单id
     * @return 清单详情
     * @author gengjiajia
     * @since 2019/10/08 17:32
     */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplSatbMain entity) {
        return success(service.detailById(entity.getId()));
    }

    /**
     * 根据类型获取对应系统类别
     *
     * @param sysCfg 类型
     * @return 对应系统类别
     * @author gengjiajia
     * @since 2019/10/09 19:32
     */
    @PostMapping("/getCategoryByCfgType")
    public Mono<ResponseEntity<SystemResponse<Object>>> getCategoryBySysType(@RequestBody SysCfg sysCfg) {
        if (sysCfg.getCfgType() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到系统类型");
        }
        return success(service.getCategoryBySysType(sysCfg.getCfgType()));
    }

    /**
     * 获取协同单位列表
     *
     * @param entity 包含主业务id
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/10 10:21
     */
    @PostMapping("/getAssistDepartmentList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getAssistDepartmentList(@RequestBody IplSatbMain entity) {
        check();
        if (entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到业务ID");
        }
        return success(service.getAssistDepartmentList(entity.getId()));
    }

    /**
     * 保存协同事项单位列表
     *
     * @param entity 包含主业务id 协同单位信息
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/10 10:21
     */
    @PostMapping("/saveAssistDepartmentList")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveAssistDepartmentList(@RequestBody IplSatbMain entity) {
        check();
        if (entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到业务ID");
        }
        if (CollectionUtils.isEmpty(entity.getAssistList())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到协同单位信息");
        }
        service.saveAssistDepartmentList(entity.getId(), entity.getAssistList());
        return success("提交成功");
    }

    /**
     * 主责单位实时更新
     *
     * @param entity 包含状态及进展
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/10 11:23
     */
    @PostMapping("/dutyUpdateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> realTimeUpdateStatus(@RequestBody IplLog entity) {
        check();
        if (entity.getIdIplMain() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到业务ID");
        }
        if (StringUtils.isEmpty(entity.getProcessInfo())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到处理进展");
        }
        if (entity.getDealStatus() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到处理状态");
        }
        service.realTimeUpdateStatus(entity);
        return success("更新成功");
    }

    /**
     * 协同单位实时更新
     *
     * @param  iplLog 更新日志信息
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/24 16:13
     */
    @PostMapping("/assistUpdateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> assistRealTimeUpdateStatus(@RequestBody IplLog iplLog) {
        if (iplLog.getDealStatus() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到处理状态");
        }
        if(iplLog.getIdIplMain() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到业务ID");
        }
        service.assistRealTimeUpdateStatus(iplLog);
        return success("更新成功");
    }

    /**
     * 主责单位实时更新协同单位处理状态
     *
     * @param entity 包含状态及进展
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/10 11:23
     */
    @PostMapping("/updateStatusByDuty")
    public Mono<ResponseEntity<SystemResponse<Object>>> realTimeUpdateStatusByDuty(@RequestBody IplLog entity) {
        check();
        if (entity.getIdIplMain() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到业务ID");
        }
        if (entity.getIdRbacDepartmentAssist() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到协同单位ID");
        }
        if (entity.getDealStatus() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到处理状态");
        }
        service.realTimeUpdateStatusByDuty(entity);
        return success("更新成功");
    }

    // =====================================================清单发布管理===================================================

    /**
     * 下载科技局实时清单资料到zip包
     *
     * @param  id 主数据id
     * @return zip文件
     * @author gengjiajia
     * @since 2019/10/11 11:27  
     */
    @GetMapping("/downloadIplSatbMainDataToZip/{id}")
    public Mono<ResponseEntity<byte[]>> downloadIplSatbMainDataToZip(@PathVariable("id") Long id) {
        if(id == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到成长目标投资清单ID")
                    .build();
        }
        return Mono.just(service.downloadIplSatbMainDataToZip(id));
    }

    /**
     * 导出科技局清单发布详情excel表格
     *
     * @param  id 主数据id
     * @return excel表格
     * @author gengjiajia
     * @since 2019/10/11 11:27
     */
    @GetMapping("/downloadIplSatbMainDataPkgToExcel/{id}")
    public Mono<ResponseEntity<byte[]>> downloadIplSatbMainDataPkgToExcel(@PathVariable("id") Long id) {
        if(id == null){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("未获取到成长目标投资清单发布ID").build();
        }
        return service.downloadIplSatbMainDataPkgToExcel(id);
    }

    /**
     * 功能描述 获取协同单位下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    @PostMapping("/getAssistList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getAssistList(@RequestBody IplSatbMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplSatbMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        //主表id  数据集合
        IplSatbMain vo = service.getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        return success(iplAssistService.getAssistList(vo.getId(), BizTypeEnum.GROW.getType()));
    }

    public void check(){
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getTypeRangeList().contains(BizTypeEnum.GROW.getType())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("当前账号的单位不可操作数据").build();
        }
    }
}

