
package com.unity.rbac.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.rbac.entity.AccountConflictRecord;
import com.unity.rbac.service.AccountConflictRecordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.util.Map;


/**
 * 账号冲突记录
 *
 * @author zhang
 * 生成时间 2019-07-25 18:51:37
 */
@Controller
@RequestMapping("/accountconflictrecord")
public class AccountConflictRecordController extends BaseWebController {
    @Autowired
    AccountConflictRecordServiceImpl service;

    /**
     * 获取账号冲突记录列表
     *
     * @param  pageEntity 查询条件
     * @return 账号冲突记录列表
     * @author gengjiajia
     * @since 2019/07/26 16:54
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<AccountConflictRecord> pageEntity) {
        return success(service.listByPage(pageEntity));
    }


    /**
     * 解决冲突
     *
     * @param record 冲突信息
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/07/26 17:26
     */
    @PostMapping("/conflictResolution")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> conflictResolution(@RequestBody AccountConflictRecord record) {
        if(record == null || record.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到冲突记录");
        } else if(record.getConflictFlag() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到冲突记录解决方式");
        }
        service.conflictResolution(record);
        return success("操作成功");
    }

}

