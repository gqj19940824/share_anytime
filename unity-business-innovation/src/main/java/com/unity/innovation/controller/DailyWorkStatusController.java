
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.DailyWorkStatus;
import com.unity.innovation.service.DailyWorkStatusServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 创新日常工作管理-工作动态
 *
 * @author zhang
 * 生成时间 2019-09-17 11:17:01
 */
@Controller
@RequestMapping("/dailyWorkStatus")
public class DailyWorkStatusController extends BaseWebController {
    @Resource
    DailyWorkStatusServiceImpl service;

    /**
     * 功能描述 分页列表查询
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<DailyWorkStatus> search) {
        IPage<DailyWorkStatus> list = service.listByPage(search);
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
    private List<Map<String, Object>> convert2List(List<DailyWorkStatus> list) {
        return JsonUtil.<DailyWorkStatus>ObjectToList(list,
                (m, entity) -> {
                    Customer customer = LoginContextHolder.getRequestAttributes();
                    Long currentDepartmentId = customer.getIdRbacDepartment();
                    m.put("operationButton", YesOrNoEnum.NO.getType());
                    m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified(), "yyyy-MM-dd"));
                }, DailyWorkStatus::getId);
    }

}

