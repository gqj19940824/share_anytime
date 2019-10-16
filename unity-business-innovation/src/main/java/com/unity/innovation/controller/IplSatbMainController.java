
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.IplSatbMainServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


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
     * 实时更新
     *
     * @param entity 包含状态及进展
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/10 11:23
     */
    @PostMapping("/realTimeUpdateStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> realTimeUpdateStatus(@RequestBody IplLog entity) {
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
     * 主责单位实时更新协同单位处理状态
     *
     * @param entity 包含状态及进展
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/10 11:23
     */
    @PostMapping("/realTimeUpdateStatusByDuty")
    public Mono<ResponseEntity<SystemResponse<Object>>> realTimeUpdateStatusByDuty(@RequestBody IplLog entity) {
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
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到成长目标投资清单发布ID")
                    .build();
        }
        return Mono.just(service.downloadIplSatbMainDataPkgToExcel(id));
    }
}

