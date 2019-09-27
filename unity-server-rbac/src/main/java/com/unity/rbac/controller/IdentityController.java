package com.unity.rbac.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.SafetyConstant;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.rbac.entity.Identity;
import com.unity.rbac.service.IdentityServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 身份信息后台控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 * @author gengjiajia
 */
@Controller
@RequestMapping("identity")
public class IdentityController extends BaseWebController {

    private final IdentityServiceImpl identityService;

    public IdentityController(IdentityServiceImpl identityService){
        this.identityService = identityService;
    }


    /**
     * 新增 or 修改 --> 身份
     *
     * @param  dto 包含身份信息
     * @return code 0 表示成功
     * -1005 数据已存在
     * -1011 数据不存在
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/12 13:46
     */
    @PostMapping("save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody Identity dto) {
        identityService.saveOrUpdateIdentity(dto);
        return success("操作成功");
    }

    /**
     * 后台指定用户已有的身份列表
     *
     * @param  search 包含身份列表查询条件
     * @return code 0 表示成功
     * 500 表示缺少查询条件
     * @author gengjiajia
     * @since 2018/12/12 13:51
     */
    @PostMapping("userIdentityListByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> userIdentityListByPage(@RequestBody SearchElementGrid search) {
        return success(identityService.userIdentityListByPage(search));
    }

    /**
     * 批量更新用户身份id和所有资源绑定
     *
     * @param id 身份id
     * @author zhangxiaogang
     * @since 2019/7/25 15:45
     */
    @GetMapping("saveUserIdentiryList/{id}")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> saveUserIdentiryList(@PathVariable Long id) {
        identityService.saveUserIdentityResourceList(id);
        return success("操作成功");
    }


    /**
     * 获取指定的身份信息
     *
     * @param  id 包含角色id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/11 14:52
     */
    @GetMapping("getInentityInfo/{id}")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> getInentityInfo(@PathVariable Long id) {
        if(id == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到指定身份ID");
        }
        return success(identityService.getInentity(id));
    }

    /**
     * 删除指定角色
     *
     * @param  id 包含指定角色id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/11 15:00
     */
    @DeleteMapping("delIdentity/{id}")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> delIdentity(@PathVariable Long id) {
        if(id == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到指定身份ID");
        }
        identityService.delIdentity(id);
        return success("删除成功");
    }

    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return code 0 表示成功
     */
    @DeleteMapping("del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        identityService.removeByIds(ConvertUtil.arrString2Long(ids.split(",")));
        return success(SafetyConstant.SUCCESS);
    }

    /**
     * 批量删除
     * @param ids 身份id集
     * @return code 0 表示成功
     */
    @PostMapping("deleteIdentity")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  deleteIdentity(@RequestBody List<Long> ids) {
        for (Long id: ids){
            if (id == null) {
                return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到指定资源ID");
            }
            identityService.delIdentity(id);
        }
        return success("删除成功");
    }
}