package com.unity.innovation.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.service.IplAssistServiceImpl;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 创新发布清单-协同事项
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
@Controller
@RequestMapping("/iplassist")
public class IplAssistController extends BaseWebController {

    @Resource
    private IplAssistServiceImpl iplAssistService;

     /**
     * 获取一页数据
     * @param pageEntity 统一查询条件
     * @return
     */
    @PostMapping("/listAssistByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<Map<String, Object>> pageEntity) {
        Map<String, Object> entity = pageEntity.getEntity();
        Long idRbacDepartmentDuty = MapUtils.getLong(entity, "idRbacDepartmentDuty");
        if (idRbacDepartmentDuty == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }

        return success(iplAssistService.listAssistByPage(pageEntity));
    }
}

