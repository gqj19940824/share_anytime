
package com.unity.rbac.controller;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.tree.TNode;
import com.unity.rbac.entity.Resource;
import com.unity.rbac.pojos.Relation;
import com.unity.rbac.service.ResourceServiceImpl;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资源信息后台控制类
 * <p>
 * create by gengjiajia at 2018-12-05 14:47:53
 * @author gengjiajia
 */
@Slf4j
@RestController
@RequestMapping("resource")
public class ResourceController extends BaseWebController {

    private final ResourceServiceImpl resourceService;

    public ResourceController(ResourceServiceImpl resourceService){
        this.resourceService = resourceService;
    }

   /**
    * 获取完整的资源树
    *
    * @return 完整的资源树
    * @author gengjiajia
    * @since 2019/07/02 14:07
    */
    @PostMapping("/getTree")
    public Mono<ResponseEntity<SystemResponse<Object>>> getTree(@RequestBody Relation relation) {
        if(relation == null || relation.getId() == null || relation.getType() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"参数不正确");
        }
        Map<String, List<Long>> listMap = relation.getType() == 1 ?
                resourceService.getRoleLinkedResourceIdListByRoleId(relation.getId()) :
                resourceService.getUserLinkedResourceIdListByUserId(relation.getId());
        List<TNode> tree = resourceService.getTree(listMap);
        return success(tree);
    }

    /**
     * 获取用户绑定的资源ID列表
     *
     * @param relation 包含用户id
     * @return code -> 0 表示成功
     *                 -1013 缺少必要参数
     * @author gengjiajia
     * @since 2019/07/02 14:07
     */
    @PostMapping("/getUserLinkedResourceIdListByUserId")
    public Mono<ResponseEntity<SystemResponse<Object>>> getUserLinkedResourceIdListByUserId(@RequestBody Relation relation) {
        if(relation == null || relation.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要查询的用户ID");
        }
        return success(resourceService.getUserLinkedResourceIdListByUserId(relation.getId()));
    }

    /**
     * 获取角色绑定的资源ID列表
     *
     * @param relation 包含角色id
     * @return code -> 0 表示成功
     *                 -1013 缺少必要参数
     * @author gengjiajia
     * @since 2019/07/02 14:07
     */
    @PostMapping("/getRoleLinkedResourceIdListByRoleId")
    public Mono<ResponseEntity<SystemResponse<Object>>> getRoleLinkedResourceIdListByRoleId(@RequestBody Relation relation) {
        if(relation == null || relation.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要查询的角色ID");
        }
        return success(resourceService.getRoleLinkedResourceIdListByRoleId(relation.getId()));
    }

    /**
     * 用户与资源关系绑定（包含解除关系）
     *
     * @param relation 包含用户id与资源id集
     * @return code -> 0 表示成功
     *                 -1013 缺少必要参数
     * @author gengjiajia
     * @since 2019/07/02 14:07
     */
    @PostMapping("/bindUserAndResource")
    public Mono<ResponseEntity<SystemResponse<Object>>> bindUserAndResource(@RequestBody Relation relation) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType())){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                    .message("非超级管理员不允许执行此操作")
                    .build();
        }
        if(relation == null || relation.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要分配的用户ID");
        }
        resourceService.bindUserAndResource(relation);
        return success("操作成功");
    }



    /**
     *  角色与资源关系绑定（包含解除关系）
     *
     * @param relation 包含角色id与资源id集
     * @return code -> 0 表示成功
     *                 -1013 缺少必要参数
     * @author gengjiajia
     * @since 2019/07/02 14:07
     */
    @PostMapping("/bindRoleAndResource")
    public Mono<ResponseEntity<SystemResponse<Object>>> bindRoleAndResource(@RequestBody Relation relation) {
        if(relation == null || relation.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要分配的角色ID");
        }

        resourceService.bindRoleAndResource(relation);
        return success("操作成功");
    }

    /**
     * 维护pc角色全部资源权限
     * @return
     */
    @PostMapping("/saveRoleResource")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveRoleResource() {
        Relation relation = new Relation();
        relation.setId(1L);
        List<Resource> resourceList = resourceService.list();
        List<Long> list = resourceList.parallelStream().map(Resource::getId).collect(Collectors.toList());
        Long[] ts = list.toArray(new Long[list.size()]);
        relation.setBindResourceIds(ts);
        resourceService.bindRoleAndResource(relation);
        return success("操作成功");
    }



    /**
     *  身份与资源关系绑定（包含解除关系）
     *
     * @param relation 包含身份id与资源id集
     * @return code -> 0 表示成功
     *                 -1013 缺少必要参数
     * @author gengjiajia
     * @since 2019/07/02 14:07
     */
    @PostMapping("/bindIdentityAndResource")
    public Mono<ResponseEntity<SystemResponse<Object>>> bindIdentityAndResource(@RequestBody Relation relation) {
        if(relation == null || relation.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要分配的身份ID");
        }
        resourceService.bindIdentityAndResource(relation);
        return success("操作成功");
    }

    /**
     * 获取身份绑定的资源ID列表
     *
     * @param relation 包含身份id
     * @return code -> 0 表示成功
     *                 -1013 缺少必要参数
     * @author gengjiajia
     * @since 2019/07/02 14:07
     */
    @PostMapping("/getIdentityLinkedResourceIdListByIdentityId")
    public Mono<ResponseEntity<SystemResponse<Object>>> getIdentityLinkedResourceIdListByIdentityId(@RequestBody Relation relation) {
        if(relation == null || relation.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要查询的身份ID");
        }
        return success(resourceService.getIdentityLinkedResourceIdListByIdentityId(relation.getId()));
    }

    /**
     * 获取左侧菜单栏（json字符串形式返回）
     *
     * @return 左侧菜单栏
     * @author gengjiajia
     * @since 2019/07/04 14:20
     */
    @PostMapping("/getLeftMenuTree")
    public Mono<ResponseEntity<SystemResponse<Object>>> getLeftMenuTree() {
        String leftMenuTree = resourceService.getLeftMenuTree();
        log.info("======《getLeftMenuTree》左侧菜单栏======{}",leftMenuTree);
        return success("获取成功",leftMenuTree);
    }

}