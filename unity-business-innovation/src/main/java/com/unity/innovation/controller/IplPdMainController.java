
package com.unity.innovation.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.innovation.entity.IplPdMain;
import com.unity.innovation.service.IplPdMainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;


/**
 * 创新发布清单-宣传部-主表
 *
 * @author G
 * 生成时间 2019-09-29 15:50:28
 */
@RestController
@RequestMapping("/iplpdmain")
public class IplPdMainController extends BaseWebController {
    @Autowired
    IplPdMainServiceImpl service;

    /**
     * 报名参加发布会 列表
     *
     * @param  pageEntity 分页及查询参数
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
        service.saveOrUpdateIplPdMain(entity);
        return success("提交成功");
    }

    /**
     * excel导出
     *
     * @param  res 响应对象
     * @param cond 参数
     * @author gengjiajia
     * @since 2019/09/29 16:04  
     */
    @PostMapping({"/export/excel"})
    public void exportExcel(HttpServletResponse res, String cond) {
        String fileName = "创新发布清单-宣传部-主表";
        ExportEntity<IplPdMain> excel = ExcelEntity.exportEntity(res);
        /*try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<IplPdMain> ew = wrapper(search);
            List<IplPdMain> list = service.list(ew);
            excel
                    .addColumn(IplPdMain::getId, "")
                    .addColumn(IplPdMain::getIdIplmMainIplMain, "")
                    .addColumn(IplPdMain::getIndustryCategory, "行业类别")
                    .addColumn(IplPdMain::getEnterpriseName, "企业名称")
                    .addColumn(IplPdMain::getEnterpriseIntroduction, "企业简介")
                    .addColumn(IplPdMain::getSpecificCause, "具体意向和事由")
                    .addColumn(IplPdMain::getIdCard, "身份证")
                    .addColumn(IplPdMain::getContactPerson, "联系人")
                    .addColumn(IplPdMain::getContactWay, "联系方式")
                    .addColumn(IplPdMain::getAttachmentCode, "附件")
                    .addColumn(IplPdMain::getSource, "来源")
                    .addColumn(IplPdMain::getStatus, "状态")
                    .addColumn(IplPdMain::getPost, "职务")
                    .addColumn(IplPdMain::getNotes, "")
                    .addColumn(IplPdMain::getEditor, "")
                    .export(fileName, convert2List(list));
        } catch (Exception ex) {
            excel.exportError(fileName, ex);
        }*/
    }



    /**
     * 删除发布会活动
     *
     * @param  entity 包含发布会id
     * @return code 0 表示删除
     * @author gengjiajia
     * @since 2019/09/29 16:15  
     */
    @PostMapping("/removeById")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeById(@RequestBody IplPdMain entity) {
        service.deleteById(entity.getId());
        return success("删除成功");
    }
}

