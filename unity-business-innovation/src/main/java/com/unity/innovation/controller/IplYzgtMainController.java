
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
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
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2List(list.getRecords(), false)).build();
        return success(result);
    }


    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return java.util.List<java.util.Map               <               java.lang.String               ,               java.lang.Object>> 规范数据
     * @author zhangxiaogang
     * @date 2019/9/27 13:36
     */
    private List<Map<String, Object>> convert2List(List<IplYzgtMain> list, boolean flag) {
        Map<String, String> map;
        if (flag) {
            //批量获取附件
            List<String> codeList = list.stream().map(IplYzgtMain::getAttachmentCode).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(codeList)) {
                //无附件，随便加入一个元素，保证查询不报错
                codeList.add("0");
            }
            List<Attachment> allAttachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codeList.toArray()));
            map = allAttachmentList.stream()
                    .collect(groupingBy(Attachment::getAttachmentCode,
                            mapping(Attachment::getUrl, joining(","))));
        } else {
            map = Maps.newHashMap();
        }
        Map<Long, String> sysCfgMap = sysCfgService.getSysCfgMap(3);
        return JsonUtil.<IplYzgtMain>ObjectToList(list,
                (m, entity) -> {
                    if (SourceEnum.SELF.getId().equals(entity.getSource())) {
                        m.put("sourceTitle", InnovationConstant.DEPARTMENT_YZGT);
                    } else if (SourceEnum.SELF.getId().equals(entity.getSource())) {
                        m.put("sourceTitle", SourceEnum.ENTERPRISE.getName());
                    }
                    if (flag) {
                        m.put("attachmentCode", MapUtils.isEmpty(map) ? "" : map.get(entity.getAttachmentCode()));
                    }
                    m.put("industryCategoryTitle", sysCfgMap.get(entity.getIndustryCategory()));
                },
                IplYzgtMain::getId, IplYzgtMain::getContactPerson, IplYzgtMain::getContactWay, IplYzgtMain::getEnterpriseName,
                IplYzgtMain::getEnterpriseIntroduction, IplYzgtMain::getPost, IplYzgtMain::getSpecificCause, IplYzgtMain::getGmtCreate, IplYzgtMain::getAttachmentCode,
                IplYzgtMain::getGmtModified, IplYzgtMain::getNotes, IplYzgtMain::getIdCard, IplYzgtMain::getSource, IplYzgtMain::getEnterpriseIntroduction
        );
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
        Map<Long, String> sysCfgMap = sysCfgService.getSysCfgMap(3);
        return JsonUtil.<IplYzgtMain>ObjectToMap(iym,
                (m, entity) -> {
                    if (SourceEnum.SELF.getId().equals(entity.getSource())) {
                        m.put("sourceTitle", InnovationConstant.DEPARTMENT_YZGT);
                    } else if (SourceEnum.SELF.getId().equals(entity.getSource())) {
                        m.put("sourceTitle", SourceEnum.ENTERPRISE.getName());
                    }
                    m.put("industryCategoryTitle", sysCfgMap.get(entity.getIndustryCategory()));
                },
                IplYzgtMain::getId, IplYzgtMain::getContactPerson, IplYzgtMain::getContactWay, IplYzgtMain::getEnterpriseName,
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
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplYzgtMain::getEnterpriseName, IplYzgtMain::getIndustryCategory, IplYzgtMain::getIdCard,
                IplYzgtMain::getSpecificCause, IplYzgtMain::getEnterpriseIntroduction, IplYzgtMain::getContactPerson, IplYzgtMain::getContactWay, IplYzgtMain::getPost);
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
        if (entity.getPost().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "职位限制50字");
        }
        if (entity.getIdCard().length() >= ParamConstants.PARAM_MAX_LENGTH_18) {
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

    /**
     * excel导出
     *
     * @param res 响应对象
     * @param res 参数
     * @author 张晓刚
     * @since 2019/10/10 16:04
     */
    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletRequest req, HttpServletResponse res) {
        String fileName = "投资机构信息"+ DateUtil.getStringDate();
        ExportEntity<IplYzgtMain> excel = ExcelEntity.exportEntity(res);
        try {
            LambdaQueryWrapper<IplYzgtMain> ew = wrapper(req);
            List<IplYzgtMain> list = service.list(ew);
            excel
                    .addColumn(IplYzgtMain::getIndustryCategoryTitle, "行业类别")
                    .addColumn(IplYzgtMain::getEnterpriseName, "企业名称")
                    .addColumn(IplYzgtMain::getEnterpriseIntroduction, "企业简介")
                    .addColumn(IplYzgtMain::getSpecificCause, "投资意向")
                    .addColumn(IplYzgtMain::getContactPerson, "联系人")
                    .addColumn(IplYzgtMain::getContactWay, "联系方式")
                    .addColumn(IplYzgtMain::getIdCard, "身份证")
                    .addColumn(IplYzgtMain::getPost, "职务")
                    .addColumn(IplYzgtMain::getAttachmentCode, "附件")
                    .addColumn(IplYzgtMain::getNotes, "备注")
                    .addColumn(IplYzgtMain::getGmtCreate, "创建时间")
                    .addColumn(IplYzgtMain::getGmtModified, "更新时间")
                    .addColumn(IplYzgtMain::getSourceTitle, "来源")
                    .export(fileName, convert2List(list, true));
        } catch (Exception ex) {
            excel.exportError(fileName, ex);
        }
    }

    /**
     * 查询条件转换
     *
     * @param req 统一查询对象
     * @return 查询对象
     * @author zhangxiaogang
     * @since 2019/10/10 20:02
     */
    private LambdaQueryWrapper<IplYzgtMain> wrapper(HttpServletRequest req) {
        LambdaQueryWrapper<IplYzgtMain> ew = new LambdaQueryWrapper<>();
        String industryCategory = req.getParameter("industryCategory");
        String enterpriseName = req.getParameter("enterpriseName");
        String createDate = req.getParameter("createDate");
        String source = req.getParameter("source");
        String notes = req.getParameter("notes");
        if (StringUtils.isNotBlank(industryCategory)) {
            ew.eq(IplYzgtMain::getIndustryCategory, Long.parseLong(industryCategory));
        }
        if (StringUtils.isNotBlank(enterpriseName)) {
            ew.like(IplYzgtMain::getEnterpriseName, enterpriseName);
        }
        if (StringUtils.isNotBlank(createDate)) {
            String[] dateArr = createDate.split("-");
            int maxDay = JKDates.getMaxDay(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
            Date startDate = DateUtils.parseDate(createDate.concat("-01 00:00:00"));
            Date endDate = DateUtils.parseDate(createDate.concat("-").concat(String.valueOf(maxDay)).concat(" 23:59:59"));
            ew.between(IplYzgtMain::getGmtCreate, startDate.getTime(), endDate.getTime());
        }
        if (StringUtils.isNotBlank(source)) {
            ew.eq(IplYzgtMain::getSource, Integer.parseInt(source));
        }
        if (StringUtils.isNotBlank(notes)) {
            ew.like(IplYzgtMain::getNotes, notes);
        }
        ew.orderByAsc(IplYzgtMain::getSort);
        return ew;
    }

  /*  public static int foo(int a,int b){

        if(b == 0) {
            System.out.println("b=="+b);
            return 0;
        }
        if(b % 2 == 0) {
            System.out.println("b==="+b);
            return foo(a+a,b/2);
        }
        return foo(a+a,b/2)+a;
    }

    public static void main(String[] args) {
        int a= 1;
        System.out.println(foo(a,3));
        System.out.println(a);
        System.out.println(1/2);
    }
*/
}

