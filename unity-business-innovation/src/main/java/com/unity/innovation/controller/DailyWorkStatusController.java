
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.constant.ParamConstants;
import com.unity.innovation.entity.DailyWorkStatus;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.service.DailyWorkStatusServiceImpl;
import com.unity.innovation.service.SysCfgServiceImpl;
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
 * 创新日常工作管理-工作动态
 *
 * @author zhang
 * 生成时间 2019-09-17 11:17:01
 */
@Controller
@RequestMapping("/dailyWorkStatus")
public class DailyWorkStatusController extends BaseWebController {

    @Resource
    private DailyWorkStatusServiceImpl service;

    @Resource
    private SysCfgServiceImpl sysCfgService;

    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<DailyWorkStatus> search) {
        IPage<DailyWorkStatus> list = service.listByPage(search);
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
    private List<Map<String, Object>> convert2List(List<DailyWorkStatus> list) {
        return JsonUtil.<DailyWorkStatus>ObjectToList(list,
                (m, entity) -> {
                }, DailyWorkStatus::getId, DailyWorkStatus::getTypeName, DailyWorkStatus::getKeyWordStr, DailyWorkStatus::getTheme,
                DailyWorkStatus::getDescription, DailyWorkStatus::getNotes, DailyWorkStatus::getState, DailyWorkStatus::getGmtCreate,
                DailyWorkStatus::getGmtModified, DailyWorkStatus::getTitle,DailyWorkStatus::getDeptName);
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
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody DailyWorkStatus entity) {
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
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(DailyWorkStatus entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, DailyWorkStatus::getTitle, DailyWorkStatus::getTheme,
                DailyWorkStatus::getType, DailyWorkStatus::getKeyWordList, DailyWorkStatus::getDescription);
        if (StringUtils.isNotBlank(msg)){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getTitle().length()>ParamConstants.PARAM_MAX_LENGTH_50){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "标题限制50字");
        }
        if (entity.getTheme().length()>ParamConstants.PARAM_MAX_LENGTH_50){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "主题限制50字");
        }
        if (entity.getDescription().length()>ParamConstants.PARAM_MAX_LENGTH_500){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "内容描述限制500字");
        }
        if (StringUtils.isNotBlank(entity.getNotes()) && entity.getNotes().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "备注限制500字");
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
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody DailyWorkStatus entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,DailyWorkStatus::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        return success(service.detailById(entity));
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
     * 功能描述 获取工作类别下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    @PostMapping("/getTypeList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getTypeList() {
        return success(sysCfgService.getSysList1(SysCfgEnum.ONE.getId()));
    }

    /**
     * 功能描述 获取关键字下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    @PostMapping("/getKeyList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getKeyList() {
        return success(sysCfgService.getSysList1(SysCfgEnum.TWO.getId()));
    }

}

