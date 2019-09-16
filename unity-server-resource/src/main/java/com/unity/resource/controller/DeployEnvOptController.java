
package com.unity.resource.controller;


import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.resource.entity.DeployEnvOpt;
import com.unity.resource.service.DeployEnvOptServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * 部署环境操作记录
 *
 * @author zhang
 * 生成时间 2019-09-03 10:12:42
 */
@RestController
@RequestMapping("/deployenvopt")
public class DeployEnvOptController extends BaseWebController {


    private final DeployEnvOptServiceImpl deployEnvOptService;

    public DeployEnvOptController(DeployEnvOptServiceImpl deployEnvOptService) {
        this.deployEnvOptService = deployEnvOptService;
    }


    /**
     * 获取一页数据
     *
     * @param pageEntity 统一查询条件
     * @return 条件查询结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:24
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<DeployEnvOpt> pageEntity) {
        LoginContextHolder.getRequestAttributes();
        return success(deployEnvOptService.findByPage(pageEntity));
    }

    /**
     * 添加部署环境操作
     *
     * @param entity 部署环境操作记录实体
     * @return code 0  操作结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:25
     */
    @PostMapping("/save")
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody DeployEnvOpt entity) {
        LoginContextHolder.getRequestAttributes();
        String valid = entity.valid();
        if (valid == null) {
            entity.setIsImpl(YesOrNoEnum.NO.getType());
            deployEnvOptService.saveOrUpdate(entity);
        } else {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, valid);
        }
        return success("操作成功");
    }


    /**
     * 查询详情信息
     *
     * @param id 部署环境操作记录实体id
     * @return code 0  操作结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:25
     */
    @GetMapping("/getDetailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> getDetailById(Long id) {
        Map<String, Object> detailMap = deployEnvOptService.getDetailById(id);
        if (MapUtils.isEmpty(detailMap)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未查询到详情信息");
        } else {
            return success(detailMap);
        }
    }

    /**
     * 执行脚本
     *
     * @param id 部署环境操作记录实体id
     * @return code 0  操作结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:25
     */
    @GetMapping("/doShellScript")
    public Mono<ResponseEntity<SystemResponse<Object>>> doShellScript(Long id) {
        int result = deployEnvOptService.doShellWork(id);
        if (result == 0) {
            return success("操作成功");
        } else {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "操作失败,请查询详情");
        }
    }


    /**
     * 批量删除
     *
     * @param idList id列表集合
     * @return code 0 操作结果
     * @author zhangxiaogang
     * @since 2019/9/3 10:26
     */
    @PostMapping("/deleteIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> del(@RequestBody List<Long> idList) {
        LoginContextHolder.getRequestAttributes();
        int resultInt = deployEnvOptService.deleteFiles(idList);
        if (resultInt == 0) {
            deployEnvOptService.removeByIds(idList);
        }
        return success("操作成功");
    }


}

