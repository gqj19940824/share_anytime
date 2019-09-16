
package com.unity.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.tree.TNode;
import com.unity.common.ui.tree.zTree;
import com.unity.common.ui.tree.zTreeStructure;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.XyDates;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.ResourceDao;
import com.unity.rbac.entity.*;
import com.unity.rbac.pojos.Relation;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 资源业务处理
 * ClassName: ResourceService
 * date: 2018-12-12 20:21:09
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
public class ResourceServiceImpl extends BaseServiceImpl<ResourceDao, Resource> implements IService<Resource> {

    private final UserResourceServiceImpl userResourceService;
    private final RoleResourceServiceImpl roleResourceService;
    private final RoleServiceImpl roleService;
    private final ResourceIdentityServiceImpl resourceIdentityService;
    private final RedisTemplate<String, Object> redisTemplate;
    /**
     * 绑定的资源id列表
     */
    private final static String BIND_RESOURCE_ID_LIST = "bindResourceIds";
    /**
     * 排除的资源id列表
     */
    private final static String EXCLUDE_RESOURCE_ID_LIST = "excludeResourceIds";

    public ResourceServiceImpl(UserResourceServiceImpl userResourceService, RoleResourceServiceImpl roleResourceService,
                               RoleServiceImpl roleService, ResourceIdentityServiceImpl resourceIdentityService, RedisTemplate<String, Object> redisTemplate) {
        this.userResourceService = userResourceService;
        this.roleResourceService = roleResourceService;
        this.roleService = roleService;
        this.resourceIdentityService = resourceIdentityService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取完整的资源树
     *
     * @return 完整的资源树
     * @author gengjiajia
     * @since 2019/07/03 10:04
     */
    public List<TNode> getTree(Map<String, List<Long>> listMap) {
        List<Long> bindResourceIds = MapUtils.isEmpty(listMap) ? Lists.newArrayList() : listMap.get(BIND_RESOURCE_ID_LIST);
        //通过redis获取资源树
        List<TNode> tree = getTreeByRedis();
        if (CollectionUtils.isNotEmpty(tree)) {
            return pocketTree(tree,bindResourceIds);
        }
        //防击穿策略
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            //二次查询，为避免多线程抢占锁对象后，后进入的线程直接查询数据库，因为此时第一个线程已经将数据存入redis
            List<TNode> treeByRedis = getTreeByRedis();
            if (CollectionUtils.isNotEmpty(treeByRedis)) {
                return pocketTree(treeByRedis,bindResourceIds);
            }
            //查询数据库
            List<Resource> list = this.list(new QueryWrapper<Resource>().lambda().orderByDesc(Resource::getSort));
            if (CollectionUtils.isEmpty(list)) {
                return Lists.newArrayList();
            }
            zTreeStructure structure = zTreeStructure.newInstance()
                    .idField("id")
                    .textField("name")
                    .parentField("idParent")
                    .gradationCodeField("gradationCode")
                    .kidField("resourceType,level,notes,resourceUrl")
                    .build();
            List<TNode> trees = zTree.getTree(list, structure);
            //存入redis
            redisTemplate.opsForValue().set(RedisConstants.RESOURCE_TREE, GsonUtils.format(trees));
            return pocketTree(trees,bindResourceIds);
        } finally {
            lock.unlock();
        }

    }


    /**
     * @desc: 组装树
     * @param: [trees, listMap]
     * @return: java.util.List<com.unity.common.ui.tree.TNode>
     * @author: vv
     * @date: 2019/9/9 15:29
     **/
    private List<TNode> pocketTree(List<TNode> trees,List<Long> listMap) {
        trees.forEach(
                item->{
                    item.setChecked(listMap.contains(Long.valueOf(item.getId())));
                    if (CollectionUtils.isNotEmpty(item.getChildren())){
                        pocketTree(item.getChildren(),listMap);
                    }
                }
        );
        return trees;
    }


    /**
     * 通过redis获取tree
     *
     * @return tree 资源树 数据列表
     * @author gengjiajia
     * @since 2019/07/02 16:44
     */
    private List<TNode> getTreeByRedis() {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object treeObj = ops.get(RedisConstants.RESOURCE_TREE);
        if (treeObj != null) {
            return GsonUtils.parse(treeObj.toString(), new TypeToken<List<TNode>>() {
            });
        }
        return null;
    }

    /**
     * 用户与资源关系绑定（包含解除关系）
     *
     * @param relation 包含用户id与资源id集
     * @author gengjiajia
     * @since 2019/07/02 19:40
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindUserAndResource(Relation relation) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        //实现方式，根据用户id删除绑定的关系，再新增关系
        /*//userResourceService.deleteByUserId(relation.getId());*/
        userResourceService.remove(new LambdaQueryWrapper<UserResource>().eq(UserResource::getIdRbacUser, relation.getId()));
        List<UserResource> bindUserResourceList = Lists.newArrayList();
        //先维护要分配的资源
        String creator = customer.getId().toString().concat(ConstString.SEPARATOR_POINT).concat(customer.getName());
        Long time = XyDates.getTime(new Date());
        if (ArrayUtils.isNotEmpty(relation.getBindResourceIds())) {
            List<Long> bindResourceIds = Arrays.asList(relation.getBindResourceIds());
            bindUserResourceList = bindResourceIds.parallelStream().map(resourceId ->
                    setUserResource(relation.getId(), creator, time, resourceId, YesOrNoEnum.YES.getType())
            ).collect(Collectors.toList());
        }
        //维护要排除的资源
        if (ArrayUtils.isNotEmpty(relation.getExcludeResourceIds())) {
            List<UserResource> excludeUserResourceList = Arrays.stream(relation.getExcludeResourceIds()).map(resourceId ->
                    setUserResource(relation.getId(), creator, time, resourceId, YesOrNoEnum.NO.getType())
            ).collect(Collectors.toList());
            bindUserResourceList.addAll(excludeUserResourceList);
        }
        //批量插入数据库
        if (CollectionUtils.isNotEmpty(bindUserResourceList)) {
            userResourceService.insertBatch(bindUserResourceList);
        }
    }

    /**
     * 组装用户与资源关系
     *
     * @param userId     用户id
     * @param creator    创建人
     * @param time       创建人
     * @param resourceId 创建人
     * @param flag       创建人
     * @return 用户与资源关系对象
     * @author gengjiajia
     * @since 2019/07/02 20:18
     */
    private UserResource setUserResource(Long userId, String creator, Long time, Long resourceId, int flag) {
        UserResource userResource = new UserResource();
        userResource.setIdRbacUser(userId);
        userResource.setIdRbacResource(resourceId);
        userResource.setAuthFlag(flag);
        userResource.setCreator(creator);
        userResource.setEditor(creator);
        userResource.setSort(time);
        userResource.setGmtCreate(time);
        userResource.setGmtModified(time);
        userResource.setIsDeleted(YesOrNoEnum.NO.getType());
        return userResource;
    }

    /**
     * 获取用户与资源关联的编码集
     *
     * @param userId 用户id
     * @return 关联的编码集
     * @author gengjiajia
     * @since 2019/07/02 20:52
     */
    public Map<String,List<Long>> getUserLinkedResourceIdListByUserId(Long userId) {
        //返回数据载体
        Map<String,List<Long>> data = Maps.newHashMap();
        List<UserResource> userResourceList = userResourceService.list(new LambdaQueryWrapper<UserResource>().eq(UserResource::getIdRbacUser, userId));
        if(CollectionUtils.isEmpty(userResourceList)){
            return data;
        }
        //获取拥有的资源id集 查询拥有的资源code
        Object[] bind = userResourceList.stream()
                .filter(userResource -> userResource.getAuthFlag().equals(YesOrNoEnum.YES.getType()))
                .map(UserResource::getIdRbacResource)
                .toArray();
        if(ArrayUtils.isNotEmpty(bind)){
            List<Resource> bindResources = this.list(new LambdaQueryWrapper<Resource>().in(Resource::getId, bind));
            data.put(BIND_RESOURCE_ID_LIST,bindResources.stream().map(Resource::getId).collect(Collectors.toList()));
        } else {
            data.put(BIND_RESOURCE_ID_LIST,Lists.newArrayList());
        }
        //查询排除的资源code
        Object[] exclude = userResourceList.stream()
                .filter(userResource -> userResource.getAuthFlag().equals(YesOrNoEnum.NO.getType()))
                .map(UserResource::getIdRbacResource)
                .toArray();
        if(ArrayUtils.isNotEmpty(exclude)){
            List<Resource> excludeResources = this.list(new LambdaQueryWrapper<Resource>().in(Resource::getId, exclude));
            data.put(EXCLUDE_RESOURCE_ID_LIST,excludeResources.stream().map(Resource::getId).collect(Collectors.toList()));
        } else {
            data.put(EXCLUDE_RESOURCE_ID_LIST,Lists.newArrayList());
        }
        return data;
    }


    /**
     * 角色与资源关系绑定（包含解除关系）
     *
     * @param relation 包含角色id与资源id集
     * @author gengjiajia
     * @since 2019/07/02 19:40
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindRoleAndResource(Relation relation) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        Role role = roleService.getById(relation.getId());
        if (role != null && YesOrNoEnum.YES.getType() == role.getIsDefault() && !customer.isSuperAdmin.equals(1)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("当前角色为系统默认角色，不允许授权")
                    .build();
        }
        //实现方式，根据角色id删除绑定的关系，再新增关系
        /*//roleResourceService.deleteByRoleId(relation.getId());*/
        roleResourceService.remove(new LambdaQueryWrapper<RoleResource>().eq(RoleResource::getIdRbacRole, relation.getId()));
        List<RoleResource> bindRoleResourceList = Lists.newArrayList();
        //先维护要分配的资源
        Long time = XyDates.getTime(new Date());
        String creator = customer.getId().toString().concat(ConstString.SEPARATOR_POINT).concat(customer.getName());
        if (ArrayUtils.isNotEmpty(relation.getBindResourceIds())) {
            List<Long> bindResourceIds = Arrays.asList(relation.getBindResourceIds());
            bindRoleResourceList = bindResourceIds.parallelStream().map(resourceId ->
                    setRoleResource(relation.getId(), creator, time, resourceId)
            ).collect(Collectors.toList());
        }
        //批量插入数据库
        if (CollectionUtils.isNotEmpty(bindRoleResourceList)) {
            roleResourceService.insertBatch(bindRoleResourceList);
        }
    }

    /**
     * 组装角色与资源关系
     *
     * @param roleId     角色id
     * @param creator    创建人
     * @param time       创建人
     * @param resourceId 创建人
     * @return 用户与资源关系对象
     * @author gengjiajia
     * @since 2019/07/02 20:18
     */
    private RoleResource setRoleResource(Long roleId, String creator, Long time, Long resourceId) {
        RoleResource roleResource = new RoleResource();
        roleResource.setIdRbacRole(roleId);
        roleResource.setIdRbacResource(resourceId);
        roleResource.setCreator(creator);
        roleResource.setEditor(creator);
        roleResource.setSort(time);
        roleResource.setGmtCreate(time);
        roleResource.setIsDeleted(YesOrNoEnum.NO.getType());
        roleResource.setGmtModified(time);
        return roleResource;
    }

    /**
     * 获取角色与资源关联的编码集
     *
     * @param roleId 角色id
     * @return 关联的编码集
     * @author gengjiajia
     * @since 2019/07/02 20:52
     */
    public Map<String, List<Long>> getRoleLinkedResourceIdListByRoleId(Long roleId) {
        //返回数据载体
        Map<String, List<Long>> data = Maps.newHashMap();
        List<Resource> resourceList = roleResourceService.getRoleLinkedResourceListByRoleId(roleId);
        if (CollectionUtils.isEmpty(resourceList)) {
            data.put(BIND_RESOURCE_ID_LIST, Lists.newArrayList());
            return data;
        }
        data.put(BIND_RESOURCE_ID_LIST, resourceList.stream().map(Resource::getId).collect(Collectors.toList()));
        return data;
    }

    /**
     * 身份与资源关系绑定（包含解除关系）
     *
     * @param relation 包含身份id与资源id集
     * @author gengjiajia
     * @since 2019/07/02 19:40
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindIdentityAndResource(Relation relation) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        String creator = customer.getId().toString().concat(ConstString.SEPARATOR_POINT).concat(customer.getName());
        //实现方式，根据身份id删除绑定的关系，再新增关系
        /*//resourceIdentityService.deleteByIdentityId(relation.getId());*/
        resourceIdentityService.remove(new LambdaQueryWrapper<ResourceIdentity>().eq(ResourceIdentity::getIdRbacIdentity, relation.getId()));
        List<ResourceIdentity> bindResourceIdentityList = Lists.newArrayList();
        //先维护要分配的资源
        Long time = XyDates.getTime(new Date());
        if (ArrayUtils.isNotEmpty(relation.getBindResourceIds())) {
            bindResourceIdentityList = Arrays.stream(relation.getBindResourceIds()).map(resourceId ->
                    setResourceIdentity(relation.getId(), creator, time, resourceId)
            ).collect(Collectors.toList());
        }
        //批量插入数据库
        if (CollectionUtils.isNotEmpty(bindResourceIdentityList)) {
            resourceIdentityService.insertBatch(bindResourceIdentityList);
        }
    }

    /**
     * 组装身份与资源关系
     *
     * @param identityId 角色id
     * @param creator    创建人
     * @param time       创建人
     * @param resourceId 创建人
     * @return 用户与资源关系对象
     * @author gengjiajia
     * @since 2019/07/02 20:18
     */
    private ResourceIdentity setResourceIdentity(Long identityId, String creator, Long time, Long resourceId) {
        ResourceIdentity resourceIdentity = new ResourceIdentity();
        resourceIdentity.setIdRbacIdentity(identityId);
        resourceIdentity.setIdRbacResource(resourceId);
        resourceIdentity.setGmtCreate(time);
        resourceIdentity.setCreator(creator);
        resourceIdentity.setEditor(creator);
        resourceIdentity.setGmtModified(time);
        resourceIdentity.setSort(time);
        resourceIdentity.setIsDeleted(YesOrNoEnum.NO.getType());
        return resourceIdentity;
    }

    /**
     * 获取身份与资源关联的编码集
     *
     * @param identityId 身份id
     * @return 关联的编码集
     * @author gengjiajia
     * @since 2019/07/02 20:52
     */
    public Map<String, List<Long>> getIdentityLinkedResourceIdListByIdentityId(Long identityId) {
        //返回数据载体
        Map<String, List<Long>> data = Maps.newHashMap();
        List<Resource> resourceList = resourceIdentityService.getIdentityLinkedResourceListByIdentityId(identityId);
        if (CollectionUtils.isNotEmpty(resourceList)) {
            data.put(BIND_RESOURCE_ID_LIST, resourceList.stream().map(Resource::getId).collect(Collectors.toList()));
        } else {
            data.put(BIND_RESOURCE_ID_LIST, Lists.newArrayList());
        }
        return data;
    }

    /**
     * 通过用户ID获取菜单及按钮资源级次编码
     *
     * @param userId     用户ID
     * @param identityId 身份ID
     * @return 菜单及按钮资源级次编码
     * @author gengjiajia
     * @since 2019/07/03 18:44
     */
    List<Resource> getUserAuthResourceListByUserId(Long userId, Long identityId) {
        /*整体思路
                    1、获取当前身份对应的资源，无对应资源时说明用户无权限
                    2、获取用户拥有的所有角色，再获取这些角色对应的资源
                    3、获取拥护本身拥有的资源（包含拥有的资源及排除的资源）
                    4、用角色资源与用户拥有的资源取并集
                    5、用身份资源与用户角色并集后的资源取交集
                    6、将取到的交集与用户要排除的资源取差集
         */
        // 1、获取当前身份对应的资源，无对应资源时说明用户无权限
        List<Resource> identityResourceList = resourceIdentityService.getIdentityLinkedResourceListByIdentityId(identityId);
        if (CollectionUtils.isEmpty(identityResourceList)) {
            return Lists.newArrayList();
        }
        List<String> identityResourceCodeList = identityResourceList.stream().map(Resource::getGradationCode).collect(Collectors.toList());
        // 2、获取用户拥有的所有角色，再获取这些角色对应的资源
        List<Resource> roleResourceList = roleResourceService.getRoleResourceCodeListByUserId(userId);
        List<String> roleResourceCodeList = roleResourceList.stream().map(Resource::getGradationCode).collect(Collectors.toList());
        // 3、获取拥护本身拥有的资源（包含拥有的资源及排除的资源）
        List<UserResource> userResourceList = userResourceService.list(new LambdaQueryWrapper<UserResource>().eq(UserResource::getIdRbacUser, userId));
        if (CollectionUtils.isNotEmpty(userResourceList)) {
            //获取用户本身拥有的资源 —> 拥有的资源
            Object[] bind = userResourceList.stream()
                    .filter(userResource -> userResource.getAuthFlag().equals(YesOrNoEnum.YES.getType()))
                    .map(UserResource::getIdRbacResource)
                    .toArray();
            if (ArrayUtils.isNotEmpty(bind)) {
                List<Resource> bindResources = this.list(new LambdaQueryWrapper<Resource>().in(Resource::getId, bind));
                List<String> bindResourceCodeList = bindResources.stream().map(Resource::getGradationCode).collect(Collectors.toList());
                // 4、用角色资源与用户拥有的资源取并集
                roleResourceCodeList.addAll(bindResourceCodeList);
            }
            // 5、用身份资源与用户角色并集后的资源取交集
            identityResourceCodeList.retainAll(roleResourceCodeList);
            //获取用户本身排除的资源 —> 排除的资源
            Object[] exclude = userResourceList.stream()
                    .filter(userResource -> userResource.getAuthFlag().equals(YesOrNoEnum.NO.getType()))
                    .map(UserResource::getIdRbacResource)
                    .toArray();
            if (ArrayUtils.isNotEmpty(exclude)) {
                List<Resource> excludeResources = this.list(new LambdaQueryWrapper<Resource>().in(Resource::getId, exclude));
                List<String> excludeResourceCodeList = excludeResources.stream().map(Resource::getGradationCode).collect(Collectors.toList());
                // 6、将取到的交集与用户要排除的资源取差集
                identityResourceCodeList.removeAll(excludeResourceCodeList);
            }
        } else {
            //用户本身未分配权限 直接用身份资源与角色的资源取交集
            identityResourceCodeList.retainAll(roleResourceCodeList);
        }
        return identityResourceList.parallelStream().filter(resource -> identityResourceCodeList.contains(resource.getGradationCode()))
                .collect(Collectors.toList());
    }

    /**
     * 获取左侧菜单栏（json字符串形式返回）
     *
     * @return 左侧菜单栏
     * @author gengjiajia
     * @since 2019/07/04 14:20
     */
    public String getLeftMenuTree() {
        Customer customer = LoginContextHolder.getRequestAttributes();
        //通过Customer获取权限内的菜单资源编码获取对应的完整资源数据
        if (CollectionUtils.isEmpty(customer.getMenuCodeList())) {
            //无权限 返回空
            return null;
        }
        List<Resource> resourceList = this.list(new LambdaQueryWrapper<Resource>().in(Resource::getGradationCode, customer.getMenuCodeList()
                .toArray()));
        //拼接资源 组装树 个人习惯先获取顶级节点
        List<Map<String, Object>> rootTree = resourceList.stream().filter(resource -> resource.getIdParent() == null || resource.getIdParent().equals(UserConstants.MENU_PARENT_ID))
                .map(this::setMenuNode)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rootTree)) {
            return null;
        }
        //递归获取子级节点
        recursiveAssemblyTree(resourceList, rootTree);
        return GsonUtils.format(rootTree);
    }

    /**
     * 递归获取子级菜单
     *
     * @param resourceList 数据源
     * @param rootTree     上级节点
     * @author gengjiajia
     * @since 2019/07/04 14:59
     */
    private void recursiveAssemblyTree(List<Resource> resourceList, List<Map<String, Object>> rootTree) {
        //遍历上级节点
        rootTree.forEach(map -> {
            //从源数据筛选当前遍历节点的子级节点集
            List<Map<String, Object>> childMenuTree = resourceList.parallelStream()
                    .filter(resource -> resource.getIdParent() != null && resource.getIdParent().toString().equals(map.get(UserConstants.MENU_ID).toString()))
                    .map(this::setMenuNode)
                    .collect(Collectors.toList());
            //将子级节点集设置到当前遍历节点中
            map.put(UserConstants.MENU_CHILDREN, childMenuTree);
            if (CollectionUtils.isNotEmpty(childMenuTree)) {
                //子级节点不为空，递归获取子级的下级节点
                recursiveAssemblyTree(resourceList, childMenuTree);
            }
        });
    }

    /**
     * 设置menu节点
     *
     * @param resource 源数据
     * @return menu节点
     * @author gengjiajia
     * @since 2019/07/04 15:25
     */
    private Map<String, Object> setMenuNode(Resource resource) {
        Map<String, Object> node = Maps.newHashMap();
        node.put(UserConstants.MENU_ID, resource.getId());
        node.put(UserConstants.MENU_ID_PARENT, resource.getIdParent());
        node.put(UserConstants.MENU_PATH, resource.getResourceUrl());
        node.put(UserConstants.MENU_ICONCLS, resource.getResourcePic());
        node.put(UserConstants.MENU_NAME, resource.getName());
        node.put(UserConstants.MENU_COMPONENT, resource.getComponent());
        return node;
    }
}