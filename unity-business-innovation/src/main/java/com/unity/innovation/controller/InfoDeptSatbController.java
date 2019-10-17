
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.service.InfoDeptSatbServiceImpl;
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
 * 路演企业信息管理-科技局-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
@Controller
@RequestMapping("/infoDeptSatb")
public class InfoDeptSatbController extends BaseWebController {

    @Resource
    InfoDeptSatbServiceImpl service;

    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<InfoDeptSatb> search) {
        IPage<InfoDeptSatb> list = service.listByPage(search);
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
    private List<Map<String, Object>> convert2List(List<InfoDeptSatb> list) {
        return JsonUtil.<InfoDeptSatb>ObjectToList(list,
                (m, entity) -> {
                },
                InfoDeptSatb::getId, InfoDeptSatb::getEnterpriseName,
                InfoDeptSatb::getIndustryCategory, InfoDeptSatb::getIndustryCategoryName,
                InfoDeptSatb::getEnterpriseScale, InfoDeptSatb::getEnterpriseScaleName,
                InfoDeptSatb::getEnterpriseNature, InfoDeptSatb::getEnterpriseNatureName,
                InfoDeptSatb::getInGeneralSituation, InfoDeptSatb::getInDetail,
                InfoDeptSatb::getAchievementLevel, InfoDeptSatb::getAchievementLevelName,
                InfoDeptSatb::getIsPublishFirst, InfoDeptSatb::getContactPerson, InfoDeptSatb::getContactWay,
                InfoDeptSatb::getGmtCreate, InfoDeptSatb::getGmtModified, InfoDeptSatb::getStatus
        );
    }


    /**
     * 添加或修改
     *
     * @param entity 亦庄国投
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/10/15 16:10
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody InfoDeptSatb entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParam(entity);
        if (obj!=null){
            return obj;
        }
        service.saveEntity(entity);
        return success("success");
    }
    /**
     * 功能描述 数据校验
     * @param entity 实体
     * @return com.unity.common.exception.UnityRuntimeException
     * @author gengzhiqiang
     * @date 2019/9/17 15:49
     */
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(InfoDeptSatb entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, InfoDeptSatb::getEnterpriseName, InfoDeptSatb::getIndustryCategory,
                InfoDeptSatb::getEnterpriseScale, InfoDeptSatb::getEnterpriseNature, InfoDeptSatb::getEnterpriseIntroduction
                , InfoDeptSatb::getInGeneralSituation, InfoDeptSatb::getAchievementLevel, InfoDeptSatb::getIsPublishFirst
                , InfoDeptSatb::getContactPerson, InfoDeptSatb::getContactWay);
        if (StringUtils.isNotBlank(msg)){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getEnterpriseName().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业名称限制50字");
        }
        if (entity.getEnterpriseIntroduction().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业简介限制500字");
        }
        if (entity.getInGeneralSituation().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "概况限制20字");
        }
        if (StringUtils.isNotBlank(entity.getInDetail()) && entity.getInDetail().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "详情限制500字");
        }
        if (StringUtils.isNotBlank(entity.getNotes()) && entity.getNotes().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "备注限制500字");
        }
        if (StringUtils.isNotBlank(entity.getContactPerson()) && entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系人限制20字");
        }
        if (StringUtils.isNotBlank(entity.getContactWay()) && entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系方式限制20字");
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
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody InfoDeptSatb entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,InfoDeptSatb::getId);
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

}

