
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.IplSuggestion;
import com.unity.innovation.service.IplSuggestionServiceImpl;
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

import static com.unity.innovation.util.InnovationUtil.isEmail;


/**
 * 意见建议-纪检组
 * @author zhang
 * 生成时间 2019-09-23 15:38:10
 */
@Controller
@RequestMapping("/iplSuggestion")
public class IplSuggestionController extends BaseWebController {
    @Resource
    IplSuggestionServiceImpl service;

    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplSuggestion> search) {
        IPage<IplSuggestion> list = service.listByPage(search);
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
    private List<Map<String, Object>> convert2List(List<IplSuggestion> list) {
        return JsonUtil.<IplSuggestion>ObjectToList(list,
                (m, entity) -> {
                }, IplSuggestion::getId,IplSuggestion::getTitle,IplSuggestion::getTitle,IplSuggestion::getEnterpriseName
                ,IplSuggestion::getContactPerson,IplSuggestion::getContactWay,IplSuggestion::getEmail,IplSuggestion::getGmtCreate
                ,IplSuggestion::getGmtModified,IplSuggestion::getSource,IplSuggestion::getStatus,IplSuggestion::getProcessStatus
                ,IplSuggestion::getSourceName,IplSuggestion::getStatusName,IplSuggestion::getProcessStatusName);
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
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplSuggestion entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParam(entity);
        if (obj!=null){
            return obj;
        }
        return success(service.saveEntity(entity));
    }

    /**
     * 功能描述 数据校验
     * @param entity 实体
     * @return com.unity.common.exception.UnityRuntimeException
     * @author gengzhiqiang
     * @date 2019/9/17 15:49
     */
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(IplSuggestion entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplSuggestion::getTitle, IplSuggestion::getSuggestion, IplSuggestion::getSource);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getTitle().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "标题限制50字");
        }
        if (entity.getSuggestion().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "意见和建议限制500字");
        }
        if (StringUtils.isNotBlank(entity.getContactPerson()) && entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系人限制20字");
        }
        if (StringUtils.isNotBlank(entity.getContactWay()) && entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系方式限制20字");
        }
        if (StringUtils.isNotBlank(entity.getEnterpriseName()) && entity.getEnterpriseName().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业名称限制50字");
        }
        if (StringUtils.isNotBlank(entity.getEmail()) && entity.getEmail().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系邮箱限制50字");
        }
        if ( StringUtils.isNotBlank(entity.getEmail()) && !isEmail(entity.getEmail())) {
            return error(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION, "邮箱格式有误");
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
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplSuggestion entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,IplSuggestion::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        return success(service.detailById(entity));
    }

    /**
     * 功能描述 处理接口
     *
     * @param entity 对象
     * @return 返回信息
     * @author gengzhiqiang
     * @date 2019/9/17 15:51
     */
    @PostMapping("/dealById")
    public Mono<ResponseEntity<SystemResponse<Object>>> dealById(@RequestBody IplSuggestion entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplSuggestion::getId, IplSuggestion::getStatus, IplSuggestion::getProcessMessage);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        service.dealById(entity);
        return success("操作成功");
    }

}

