
package com.unity.innovation.controller.api;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.entity.IplPdMain;
import com.unity.innovation.service.IplPdMainServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/api/iplpdmain")
public class IplPdMainApiController extends BaseWebController {
    @Autowired
    IplPdMainServiceImpl service;

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
                 IplPdMain::getContactPerson, IplPdMain::getContactWay,IplPdMain::getEnterpriseIntroduction,
                IplPdMain::getIdCard, IplPdMain::getPost,IplPdMain::getSpecificCause);
        if(StringUtils.isNotEmpty(msg)){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,msg);
        }
        if(CollectionUtils.isNotEmpty(entity.getAttachments()) && entity.getAttachments().size() > 9){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"照片数量不得大于9张");
        }
        service.saveOrUpdateIplPdMain(entity);
        return success("提交成功");
    }
}

