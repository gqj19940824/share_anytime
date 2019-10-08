
package com.unity.innovation.controller.api;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.entity.IplPdMain;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.IplPdMainServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


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
        if(CollectionUtils.isNotEmpty(entity.getAttachmentList()) && entity.getAttachmentList().size() > 9){
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH,"照片数量不得大于9张");
        }
        entity.setSource(SourceEnum.ENTERPRISE.getId());
        service.saveOrUpdateIplPdMain(entity);
        return success("提交成功");
    }
}

