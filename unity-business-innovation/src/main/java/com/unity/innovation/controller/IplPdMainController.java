
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JKDates;
import com.unity.common.util.ValidFieldUtil;
import com.unity.common.constant.ParamConstants;
import com.unity.innovation.entity.IplPdMain;
import com.unity.innovation.service.IplPdMainServiceImpl;
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


/**
 * 创新发布清单-宣传部-主表
 *
 * @author G
 * 生成时间 2019-09-29 15:50:28
 */
@RestController
@RequestMapping("/iplpdmain")
public class IplPdMainController extends BaseWebController {
    @Resource
    IplPdMainServiceImpl service;

    /**
     * 报名参加发布会 列表
     *
     * @param pageEntity 分页及查询参数
     * @return 列表数据
     * @author gengjiajia
     * @since 2019/09/29 16:00
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplPdMain> pageEntity) {
        return success(service.listByPage(pageEntity));
    }

    /**
     * 添加或修改
     *
     * @param entity 报名发布会信息
     * @return code 0 表示成功
     * @author gengjiajia
     * @since 2019/09/29 16:01
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IplPdMain entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, IplPdMain::getIndustryCategory, IplPdMain::getEnterpriseName,
                IplPdMain::getContactPerson, IplPdMain::getContactWay, IplPdMain::getEnterpriseIntroduction,
                IplPdMain::getIdCard, IplPdMain::getPost, IplPdMain::getSpecificCause,IplPdMain::getSource);
        if (StringUtils.isNotEmpty(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,msg);
        }
        //数据长度校验
        if(entity.getEnterpriseName().length() > ParamConstants.PARAM_MAX_LENGTH_50){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"企业名称仅支持最长50个字");
        }
        if(entity.getEnterpriseIntroduction().length() > ParamConstants.PARAM_MAX_LENGTH_500
                || entity.getSpecificCause().length() > ParamConstants.PARAM_MAX_LENGTH_500){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"企业简介及具体意向或事由仅支持最长500个字");
        }
        if(entity.getContactPerson().length() > ParamConstants.PARAM_MAX_LENGTH_20
                || entity.getContactWay().length() > ParamConstants.PARAM_MAX_LENGTH_20
                || entity.getPost().length() > ParamConstants.PARAM_MAX_LENGTH_20){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"联系人、联系电话及职务仅支持最长20个字符");
        }
        if(!ParamConstants.PARAM_MAX_LENGTH_18.equals(entity.getIdCard().length())
                && !ParamConstants.PARAM_MAX_LENGTH_15.equals(entity.getIdCard().length())){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"身份证长度固定为15位或18位");
        }
        service.saveOrUpdateIplPdMain(entity);
        return success("提交成功");
    }

    /**
     * excel导出
     *
     * @param req  参数
     * @param res 响应对象
     * @author gengjiajia
     * @since 2019/09/29 16:04
     */
    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletRequest req, HttpServletResponse res) {
        String fileName = "发布会报名信息";
        ExportEntity<IplPdMain> excel = ExcelEntity.exportEntity(res);
        try {
            LambdaQueryWrapper<IplPdMain> ew = wrapper(req);
            List<IplPdMain> list = service.list(ew);
            excel
                    .addColumn(IplPdMain::getIndustryCategory, "行业类别")
                    .addColumn(IplPdMain::getEnterpriseName, "企业名称")
                    .addColumn(IplPdMain::getEnterpriseIntroduction, "企业简介")
                    .addColumn(IplPdMain::getSpecificCause, "具体意向和事由")
                    .addColumn(IplPdMain::getContactPerson, "联系人")
                    .addColumn(IplPdMain::getContactWay, "联系方式")
                    .addColumn(IplPdMain::getIdCard, "身份证")
                    .addColumn(IplPdMain::getPost, "职务")
                    .addColumn(IplPdMain::getAttachmentCode, "附件")
                    .addColumn(IplPdMain::getNotes, "备注")
                    .addColumn(IplPdMain::getGmtCreate, "创建时间")
                    .addColumn(IplPdMain::getGmtModified, "更新时间")
                    .addColumn(IplPdMain::getSource, "来源")
                    .export(fileName, service.convert2ListByExport(list));
        } catch (Exception ex) {
            excel.exportError(fileName, ex);
        }
    }


    /**
     * 删除发布会活动
     *
     * @param entity 包含发布会id
     * @return code 0 表示删除
     * @author gengjiajia
     * @since 2019/09/29 16:15
     */
    @PostMapping("/deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteById(@RequestBody IplPdMain entity) {
        service.deleteById(entity.getId());
        return success("删除成功");
    }

    /**
     * 查看发布会详情
     *
     * @param entity 包含发布会id
     * @return 发布会详情
     * @author gengjiajia
     * @since 2019/10/08 09:26
     */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplPdMain entity) {
        return success(service.detailById(entity.getId()));
    }

    /**
     * 查询条件转换
     *
     * @param req 统一查询对象
     * @return 查询对象
     */
    private LambdaQueryWrapper<IplPdMain> wrapper(HttpServletRequest req) {
        LambdaQueryWrapper<IplPdMain> ew = new LambdaQueryWrapper<>();
        String industryCategory = req.getParameter("industryCategory");
        String enterpriseName = req.getParameter("enterpriseName");
        String createDate = req.getParameter("createDate");
        String source = req.getParameter("source");
        String notes = req.getParameter("notes");
        if (StringUtils.isNotBlank(industryCategory)) {
            ew.eq(IplPdMain::getIndustryCategory, Long.parseLong(industryCategory));
        }
        if (StringUtils.isNotBlank(enterpriseName)) {
            ew.like(IplPdMain::getEnterpriseName, enterpriseName);
        }
        if (StringUtils.isNotBlank(createDate)) {
            String[] dateArr = createDate.split("-");
            int maxDay = JKDates.getMaxDay(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
            Date startDate = DateUtils.parseDate(createDate.concat("-01 00:00:00"));
            Date endDate = DateUtils.parseDate(createDate.concat("-").concat(String.valueOf(maxDay)).concat(" 23:59:59"));
            ew.between(IplPdMain::getGmtCreate, startDate.getTime(), endDate.getTime());
        }
        if (StringUtils.isNotBlank(source)) {
            ew.eq(IplPdMain::getSource, Integer.parseInt(source));
        }
        if (StringUtils.isNotBlank(notes)) {
            ew.like(IplPdMain::getNotes, notes);
        }
        ew.orderByAsc(IplPdMain::getSort);
        return ew;
    }

    /**
     * 获取行业类别
     *
     * @return 行业类别
     * @author gengjiajia
     * @since 2019/10/08 15:03
     */
    @PostMapping("/getIndustryCategoryList")
    public Mono<ResponseEntity<SystemResponse<Object>>> getIndustryCategoryList() {
        return success(service.getIndustryCategoryList());
    }
}

