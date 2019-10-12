
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.service.IplManageMainServiceImpl;
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
 * 创新发布活动-管理-主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:32
 */
@Controller
@RequestMapping("/ipamManageMain")
public class IpaManageMainController extends BaseWebController {

    @Resource
    IplManageMainServiceImpl service;

    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplManageMain> search) {
        Long category = 0L;
        if (search != null && search.getEntity() != null) {
             category = getCategory(search.getEntity());
        }
        IPage<IplManageMain> list= service.listForPkg(search,category);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2ListForPkg(list.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 数据整理
     * @param list 集合
     * @return java.util.List 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2ListForPkg(List<IplManageMain> list) {
        return JsonUtil.<IplManageMain>ObjectToList(list,
                (m, entity) -> {
                }, IplManageMain::getId, IplManageMain::getTitle, IplManageMain::getGmtSubmit, IplManageMain::getStatus,IplManageMain::getStatusName);
    }



    /**
     * 功能描述 包的新增编辑
     *
     * @param entity 保存计划
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplManageMain entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParam(entity);
        if (obj != null) {
            return obj;
        }
        Long category = getCategory(entity);
        service.saveOrUpdateForPkg(entity,category);
        return success("操作成功");
    }

    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(IplManageMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplManageMain::getTitle, IplManageMain::getDataList);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (StringUtils.isNotBlank(entity.getNotes()) && entity.getNotes().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "备注字数限制500字");
        }
        if (entity.getTitle().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "标题字数限制50字");
        }
        return null;
    }

    /**
     * 功能描述 发改局包详情接口
     *
     * @param entity 对象
     * @return 返回信息
     * @author gengzhiqiang
     * @date 2019/9/17 15:51
     */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplManageMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplManageMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        return success(service.detailIplManageMainById(entity.getId()));
    }

    /**
     * 功能描述 批量删除包
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
        //todo 删除单位
        service.removeByIdsForPkg(ids,InnovationConstant.DEPARTMENT_ESB_ID);
        return success("删除成功");
    }

    /**
     * 功能描述 提交接口
     *
     * @param entity 实体
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/submit")
    public Mono<ResponseEntity<SystemResponse<Object>>> submit(@RequestBody IplManageMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,IplManageMain::getId,IplManageMain::getCategory);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        Long category = getCategory(entity);
        entity.setIdRbacDepartmentDuty(category);
        service.submit(entity);
        return success("操作成功");
    }


    private Long getCategory(IplManageMain entity) {
        Long category = 0L;
        if (entity != null) {
            ListCategoryEnum categoryEnum = ListCategoryEnum.valueOfName(entity.getCategory());
            if (categoryEnum != null) {
                category = categoryEnum.getId();
            }
        }
        return category;
    }

}

