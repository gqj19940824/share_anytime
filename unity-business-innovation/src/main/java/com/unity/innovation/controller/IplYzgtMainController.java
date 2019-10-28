
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JKDates;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.utils.DateUtil;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplYzgtMain;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.AttachmentServiceImpl;
import com.unity.innovation.service.IplYzgtMainServiceImpl;
import com.unity.innovation.service.SysCfgServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


/**
 * od->yi zhuang guo tou
 *
 * @author zhang
 * 生成时间 2019-09-25 14:51:40
 */
@RestController
@RequestMapping("/iplyzgtmain")
public class IplYzgtMainController extends BaseWebController {
    @Resource
    private IplYzgtMainServiceImpl service;
    @Resource
    private SysCfgServiceImpl sysCfgService;
    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private DicUtils dicUtils;


    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 分页数据
     * @author zhangxiaogang
     * @date 2019/9/27 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplYzgtMain> search) {
        IPage<IplYzgtMain> list = service.listByPage(search);
        List<IplYzgtMain> records = list.getRecords();
        service.convert2List(records);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(JsonUtil.ObjectToList(records,null, IplYzgtMain::getId, IplYzgtMain::getContactPerson, IplYzgtMain::getContactWay, IplYzgtMain::getEnterpriseName,IplYzgtMain::getIndustryCategory,
                        IplYzgtMain::getEnterpriseIntroduction, IplYzgtMain::getPost, IplYzgtMain::getSpecificCause, IplYzgtMain::getGmtCreate, IplYzgtMain::getAttachmentCode,
                        IplYzgtMain::getGmtModified, IplYzgtMain::getNotes, IplYzgtMain::getIdCard, IplYzgtMain::getSource, IplYzgtMain::getEnterpriseIntroduction,
                        IplYzgtMain::getIndustryCategoryTitle,IplYzgtMain::getEnterpriseScaleTitle,IplYzgtMain::getEnterpriseNatureTitle,IplYzgtMain::getEnterpriseLocationTitle,IplYzgtMain::getSourceTitle)).build();
        return success(result);
    }




    /**
     * 功能描述 数据整理
     *
     * @param iym 对象
     * @return java.util.List<java.util.Map               <               java.lang.String               ,               java.lang.Object>> 规范数据
     * @author zhangxiaogang
     * @date 2019/9/27 13:36
     */
    private Map<String, Object> convert2Map(IplYzgtMain iym) {
        Map<Long, String> industryCategoryTitleMap = sysCfgService.getSysCfgMap(3);
        Map<Long, String> enterpriseNatureTitleMap = sysCfgService.getSysCfgMap(6);

        return JsonUtil.ObjectToMap(iym,
                (m, entity) -> {
                    if (SourceEnum.SELF.getId().equals(entity.getSource())) {
                        m.put("sourceTitle", InnovationConstant.DEPARTMENT_YZGT);
                    } else if (SourceEnum.ENTERPRISE.getId().equals(entity.getSource())) {
                        m.put("sourceTitle", SourceEnum.ENTERPRISE.getName());
                    }
                    m.put("industryCategoryTitle", industryCategoryTitleMap.get(entity.getIndustryCategory()));
                    //企业性质
                    m.put("enterpriseNatureTitle", enterpriseNatureTitleMap.get(entity.getEnterpriseNature()));
                    //企业规模
                    Dic enterpriseScale = dicUtils.getDicByCode(DicConstants.ENTERPRISE_SCALE, entity.getEnterpriseScale().toString());
                    if (enterpriseScale != null && StringUtils.isNotBlank(enterpriseScale.getDicValue())) {
                        m.put("enterpriseScaleTitle", enterpriseScale.getDicValue());
                    }
                    //企业属地
                    Dic enterpriseLocation = dicUtils.getDicByCode(DicConstants.ENTERPRISE_LOCATION, entity.getEnterpriseLocation().toString());
                    if (enterpriseLocation != null && StringUtils.isNotBlank(enterpriseLocation.getDicValue())) {
                        m.put("enterpriseLocationTitle", enterpriseLocation.getDicValue());
                    }
                },
                IplYzgtMain::getId, IplYzgtMain::getContactPerson, IplYzgtMain::getContactWay, IplYzgtMain::getEnterpriseName,IplYzgtMain::getIndustryCategory,
                IplYzgtMain::getEnterpriseIntroduction, IplYzgtMain::getPost, IplYzgtMain::getSpecificCause, IplYzgtMain::getGmtCreate, IplYzgtMain::getAttachmentCode,
                IplYzgtMain::getGmtModified, IplYzgtMain::getNotes, IplYzgtMain::getIdCard, IplYzgtMain::getSource, IplYzgtMain::getEnterpriseIntroduction
        );
    }


    /**
     * 功能描述
     *
     * @param entity 保存计划
     * @return 成功返回成功信息
     * @author zhangxiaogang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplYzgtMain entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParam(entity);
        if (obj != null) {
            return obj;
        }
        service.saveOrUpdateIplYzgtMain(entity);
        return success("操作成功");
    }


    /**
     * 功能描述 数据校验
     *
     * @param entity 实体
     * @return com.unity.common.exception.UnityRuntimeException
     * @author zhangxiaogang
     * @date 2019/9/27 15:49
     */
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(IplYzgtMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplYzgtMain::getEnterpriseName, IplYzgtMain::getIndustryCategory, IplYzgtMain::getIdCard, IplYzgtMain::getSource,
                IplYzgtMain::getSpecificCause, IplYzgtMain::getEnterpriseIntroduction, IplYzgtMain::getContactPerson, IplYzgtMain::getContactWay, IplYzgtMain::getPost,
                IplYzgtMain::getEnterpriseScale,IplYzgtMain::getEnterpriseNature,IplYzgtMain::getEnterpriseLocation);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (entity.getEnterpriseIntroduction().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业简介限制500字");
        }
        if (entity.getSpecificCause().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "投资意向限制500字");
        }
        if (entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系人限制20字");
        }
        if (entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "联系方式限制20字");
        }
        if (StringUtils.isNotBlank(entity.getEnterpriseName()) && entity.getEnterpriseName().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "企业名称限制50字");
        }
        if (entity.getPost().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "职位限制50字");
        }
        if (entity.getIdCard().length() > ParamConstants.PARAM_MAX_LENGTH_18) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "身份证限制18位");
        }
        return null;
    }

    /**
     * 功能描述 批量删除
     *
     * @param ids id集合
     * @return 成功返回成功信息
     * @author zhangxiaogang
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
     * @author zhangxiaogang
     * @date 2019/9/27 15:51
     */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplYzgtMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplYzgtMain::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        return success(convert2Map(service.detailById(entity)));
    }


}

