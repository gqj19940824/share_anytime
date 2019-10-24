
package com.unity.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.base.SessionHolder;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.constants.RedisKeys;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.*;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.HashRedisUtils;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.UserDao;
import com.unity.rbac.entity.*;
import com.unity.rbac.enums.ResourceTypeEnum;
import com.unity.rbac.enums.UserTypeEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * 用户信息业务处理
 * <p>
 * ClassName: UserService
 * date: 2018-12-12 20:21:11
 *
 * @author creator
 * @since JDK 1.8
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<UserDao, User> implements IService<User> {

    private final RedisUtils redisUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserResourceServiceImpl userResourceService;
    private final UserIdentityServiceImpl userIdentityService;
    private final UserRoleServiceImpl userRoleService;
    private final ResourceServiceImpl resourceService;
    private final HashRedisUtils hashRedisUtils;
    private final UserHelpServiceImpl userHelpService;
    private final DicUtils dicUtils;

    public UserServiceImpl(RedisUtils redisUtils, StringRedisTemplate stringRedisTemplate, UserResourceServiceImpl userResourceService,
                           UserIdentityServiceImpl userIdentityService,
                           UserRoleServiceImpl userRoleService, ResourceServiceImpl resourceService, HashRedisUtils hashRedisUtils,
                           UserHelpServiceImpl userHelpService, DicUtils dicUtils) {
        this.redisUtils = redisUtils;
        this.stringRedisTemplate = stringRedisTemplate;
        this.userResourceService = userResourceService;
        this.userIdentityService = userIdentityService;
        this.userRoleService = userRoleService;
        this.resourceService = resourceService;
        this.hashRedisUtils = hashRedisUtils;
        this.userHelpService = userHelpService;
        this.dicUtils = dicUtils;
    }

    /**
     * 查询账号管理列表
     *
     * @param pageEntity 包含查询条件
     * @return 后台用户列表
     * @author gengjiajia
     * @since 2018/12/08 13:37
     */
    public PageElementGrid<Map<String, Object>> findUserListBySystem(PageEntity<User> pageEntity) {
        PageElementGrid<Map<String, Object>> grid = new PageElementGrid<>();
        Customer customer = LoginContextHolder.getRequestAttributes();
        Map<String, Object> data = Maps.newHashMap();
        data.put("offset", (pageEntity.getPageable().getCurrent() - 1L) * pageEntity.getPageable().getSize());
        data.put("limit", pageEntity.getPageable().getSize());
        User user = pageEntity.getEntity();
        boolean isQueryByRoleId = false;
        if(user != null){
            data.put("idRbacDepartment", user.getIdRbacDepartment());
            data.put("depType", user.getDepType());
            data.put("isLock", user.getIsLock());
            data.put("name", StringUtils.isBlank(user.getName()) ? null : user.getName().trim());
            data.put("loginName", StringUtils.isNotBlank(user.getLoginName()) ? user.getLoginName().trim() : null);
            if (user.getRoleId() == null) {
                //不查询
                data.put("roleId", null);
            } else if (user.getRoleId().equals(0L)) {
                //查询无角色的用户
                data.put("roleId", 0);
            } else {
                //查询指定角色的用户
                data.put("roleId", user.getRoleId());
                isQueryByRoleId = true;
            }
        }
        if (customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType())) {
            //超级管理员或一级管理员 放开数据权限
            data.put("dataPermissionIdList", null);
        } else {
            data.put("dataPermissionIdList", customer.getDataPermissionIdList());
        }
        long total = baseMapper.countUserTotalNum(data);
        if (total > 0) {
            List<User> userList = baseMapper.findUserListByPage(data);
            grid.setItems(convert2List(userList, customer, isQueryByRoleId));
        } else {
            grid.setItems(Lists.newArrayList());
        }
        grid.setTotal(total);
        return grid;
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return List Map
     */
    private List<Map<String, Object>> convert2List(List<User> list, Customer customer, boolean isQueryByRoleId) {
        //查询所有用户关联的角色
        List<Long> userIdList = list.stream().map(User::getId).collect(Collectors.toList());
        List<UserRole> userRoleList = userRoleService.list(new LambdaQueryWrapper<UserRole>().in(UserRole::getIdRbacUser, userIdList.toArray()));
        Map<Long, List<Long>> roleIdListOfUserId = userRoleList.parallelStream()
                .collect(groupingBy(UserRole::getIdRbacUser, mapping(UserRole::getIdRbacRole, toList())));
        //判断页面是否使用角色进行查询
        Map<Long, String> groupConcatRoleNameMap = Maps.newHashMap();
        if (isQueryByRoleId && CollectionUtils.isNotEmpty(userIdList)) {
            //批量获取用户所关联的角色名称串
            List<User> groupConcatRoleNameList = userRoleService.getGroupConcatRoleNameListByUserIdIn(userIdList);
            Map<Long, String> map = groupConcatRoleNameList.stream().collect(groupingBy(User::getId, mapping(User::getGroupConcatRoleName, joining())));
            groupConcatRoleNameMap.putAll(map);
        }
        return JsonUtil.ObjectToList(list,
                (m, u) -> adapterFieldByList(m, u, customer, roleIdListOfUserId, isQueryByRoleId, groupConcatRoleNameMap),
                User::getId, User::getDepartment, User::getLoginName, User::getName, User::getIdRbacDepartment,User::getReceiveSms,User::getDepType,
                User::getPhone, User::getPosition, User::getUserType, User::getIsLock, User::getGroupConcatRoleName, User::getNotes, User::getSource
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterFieldByList(Map<String, Object> m, User entity, Customer customer, Map<Long,
            List<Long>> roleIdListOfUserIdMap, boolean isQueryByRoleId, Map<Long, String> groupConcatRoleNameMap) {
        adapterField(m, entity);
        m.put("receiveSmsName", entity.getReceiveSms() != null ? entity.getReceiveSms() == 0 ? "是" : "否" : "");
        m.put("isLockName", entity.getIsLock() != null  ? entity.getIsLock() == 0 ? "已启用" : "已禁用" : "");
        List<Long> roleIdListOfUserId = roleIdListOfUserIdMap.get(entity.getId());
        if (customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType())) {
            //超管可分配所有角色
            m.put("isDist", YesOrNoEnum.YES.getType());
        } else if (customer.getIsAdmin().equals(YesOrNoEnum.NO.getType()) || CollectionUtils.isEmpty(customer.getRoleList())) {
            //当前用户非管理员 或管理员没有可分配的角色
            m.put("isDist", YesOrNoEnum.NO.getType());
        } else if (CollectionUtils.isEmpty(roleIdListOfUserId)) {
            //当前指定的账号未分配角色
            m.put("isDist", YesOrNoEnum.YES.getType());
        } else if (customer.getRoleList().containsAll(roleIdListOfUserId)) {
            //当前管理员拥有的角色完全包含指定账号的角色
            m.put("isDist", YesOrNoEnum.YES.getType());
        } else {
            //其他情况不可分配
            m.put("isDist", YesOrNoEnum.NO.getType());
        }
        if (isQueryByRoleId && MapUtils.isNotEmpty(groupConcatRoleNameMap)) {
            //组装当前用户所有角色名称
            String groupConcatRoleName = groupConcatRoleNameMap.get(entity.getId());
            m.put("groupConcatRoleName", groupConcatRoleName == null ? "" : groupConcatRoleName);
        }
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, User entity) {
        m.put("creator", entity.getCreator().contains(ConstString.SEPARATOR_POINT) ? entity.getCreator().split(ConstString.SPLIT_POINT)[1] : entity.getCreator());
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("groupConcatRoleName", StringUtils.isNotEmpty(entity.getGroupConcatRoleName()) ? entity.getGroupConcatRoleName() : "");
        m.put("userTypeTitle", entity.getUserType() != null ? UserTypeEnum.of(entity.getUserType()).getName() : "");
        if(entity.getDepType() != null){
            Dic dic = dicUtils.getDicByCode(UserConstants.DEP_TYPE, entity.getDepType().toString());
            m.put("depTypeTitle",dic == null ? "" : dic.getDicValue());
        }
    }

    /**
     * 获取指定用户信息
     *
     * @param userId 指定用户id
     * @return 指定用户信息
     * @author jiaww
     * @since 2019/1/11 13:37
     */
    public Map<String, Object> getUserById(Long userId) {
        User user = baseMapper.getUserInfoById(userId);
        if (user == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("用户信息不存在")
                    .build();
        }
        return JsonUtil.ObjectToMap(user,
                this::adapterField,
                User::getId, User::getDepartment, User::getLoginName, User::getName, User::getPhone,User::getReceiveSms,
                User::getPosition, User::getGroupConcatRoleName, User::getSource, User::getNotes, User::getUserType,
                User::getIdRbacDepartment, User::getDepType
        );
    }

    /**
     * 删除指定用户（逻辑删除）
     *
     * @param id 指定用户id
     * @author gengjiajia
     * @since 2018/12/11 13:39
     */
    @Transactional(rollbackFor = Exception.class)
    public void delUser(Long id) {
        //删除用户与角色的关联关系
        userRoleService.remove(new QueryWrapper<UserRole>().lambda()
                .eq(UserRole::getIdRbacUser, id));
        //删除用户与身份的关联关系
        userIdentityService.remove(new QueryWrapper<UserIdentity>().lambda()
                .eq(UserIdentity::getIdRbacUser, id));
        //删除用户与资源的关联关系
        userResourceService.remove(new QueryWrapper<UserResource>().lambda()
                .eq(UserResource::getIdRbacUser, id));
        super.removeById(id);
        //从redis中删除该用户
        hashRedisUtils.removeValueByKey(RedisConstants.USER.concat(id.toString()));
    }

    /**
     * 全局统一登录
     *
     * @param loginName 登录账号
     * @param pwd       登录密码
     * @param os        登录平台
     * @return 用户信息及权限信息
     * @author gengjiajia
     * @since 2018/12/17 09:40
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unityLogin(String loginName, String pwd, Integer os) {
        Date now = new Date();
        //1.通过账号获取用户信息
        User user = baseMapper.getUserInfoByLoginName(loginName);
        if (user != null) {
            if (user.getIsLock().equals(YesOrNoEnum.YES.getType())) {
                //账号已锁定
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LOGIN_DATA_SATUS_ERR)
                        .message("账号已锁定，如有疑问请联系管理员")
                        .build();
            }
            if (user.getIdRbacDepartment() == null && UserTypeEnum.ORDINARY.getId().equals(user.getUserType())) {

                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LOGIN_DATA_SATUS_ERR)
                        .message("暂未分配所属单位，请联系管理员")
                        .build();
            }
            user.setSuperAdmin(isSuperAdmin(user.getId()));
            //密码校验
            if (!Encryption.getEncryption(pwd, user.getLoginName()).equalsIgnoreCase(user.getPwd())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_ERROR)
                        .message("用户名或密码错误")
                        .build();
            }
        } else {
            //用户为空 调用用户中心接口校验账号是否存在
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_ERROR)
                    .message("用户不存在")
                    .build();
        }
        //返回数据总载体
        Map<String, Object> info = Maps.newHashMap();
        //生成token
        String tokenStr = EncryptUtil.generateToken(RedisKeys.CUSTOMER);
        info.put("token", tokenStr);
        // 用户拥有的资源权限
        Customer customer = new Customer();
        getUserAuthResource(user.getId(), os.longValue(), customer, info);
        //用户信息存入redis
        userHelpService.saveCustomer(user, os, tokenStr, customer);
        Map userMap = JsonUtil.ObjectToMap(user,
                new String[]{"id", "loginName", "phone", "name", "notes", "idRbacDepartment", "department",
                        "isAdmin","userType"},
                (m, u) -> {
                    m.put("gmtCreate", DateUtils.timeStamp2Date(u.getGmtCreate()));
                    m.put("roleList", userRoleService.selectRoleIdsByUserId(u.getId()));
                    m.put("isSuperAdmin", u.getSuperAdmin());
                });
        info.put("user", userMap);
        //维护登录信息
        userHelpService.updateLoginInfo(user, os, now, SessionHolder.getRequest());
        return info;
    }




    /**
     * 获取用户授权的资源
     *
     * @param userId   用户id
     * @param os       终端
     * @param customer redis用户对象
     * @param info     接口返回载体
     * @author gengjiajia
     * @since 2019/07/04 10:55
     */
    private void getUserAuthResource(Long userId, long os, Customer customer, Map<String, Object> info) {
        List<Resource> userAuthResourceList = resourceService.getUserAuthResourceListByUserId(userId, os);
        if (CollectionUtils.isEmpty(userAuthResourceList)) {
            return;
        }
        //获取用户拥有的接口列表
        List<String> authApiList = userAuthResourceList.stream().filter(resource -> resource.getResourceType().equals(ResourceTypeEnum.API.getType()))
                .map(Resource::getResourceUrl)
                .collect(Collectors.toList());
        //获取用户拥有的按钮编码列表
        List<String> buttonCodeList = userAuthResourceList.stream().filter(resource -> resource.getResourceType().equals(ResourceTypeEnum.BUTTON.getType()))
                .map(Resource::getGradationCode)
                .collect(Collectors.toList());
        //获取用户拥有的菜单编码列表
        List<String> menuCodeList = userAuthResourceList.stream().filter(resource -> resource.getResourceType().equals(ResourceTypeEnum.MENU.getType()))
                .map(Resource::getGradationCode)
                .collect(Collectors.toList());
        info.put(UserConstants.BUTTON_CODE_LIST, buttonCodeList);
        info.put(UserConstants.MENU_CODE_LIST, menuCodeList);
        customer.setAuth(authApiList);
        customer.setButtonCodeList(buttonCodeList);
        customer.setMenuCodeList(menuCodeList);
    }

    /**
     * 判断当前用户是否超级管理员
     *
     * @param id 用户id
     * @return 0 否 1 是
     * @author gengjiajia
     * @since 2019/08/01 16:26
     */
    private Integer isSuperAdmin(Long id) {
        Dic dicByCode = dicUtils.getDicByCode(DicConstants.ACCOUNT, DicConstants.SUPER_ADMIN);
        if (dicByCode == null || StringUtils.isEmpty(dicByCode.getDicValue())) {
            return 0;
        }
        String dicValue = dicByCode.getDicValue();
        String[] split = dicValue.split(ConstString.SPLIT_COMMA);
        return ArrayUtils.contains(split, id.toString()) ? 1 : 0;
    }

    /**
     * 统一退出登录
     *
     * @author gengjiajia
     * @since 2018/12/17 15:47
     */
    public void unityLogout(String os) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        String key = RedisConstants.LOGINNAME2TOKEN.concat(RedisConstants.KEY_JOINER)
                .concat(os)
                .concat(RedisConstants.KEY_JOINER)
                .concat(customer.getLoginName());
        String token = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(token)) {
            redisUtils.removeCurrentUserByToken(token);
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * 后台添加 or 修改用户信息
     *
     * @param dto 包含用户信息
     *            -1005 数据已存在
     *            -1011 数据不存在
     *            -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/05 17:03
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateUserInfo(User dto) {
        // 新增
        if (dto.getId() == null) {
            dto.setPwd(Encryption.getEncryption(UserConstants.RESET_PWD, dto.getLoginName()));
            //完善信息标识
            dto.setIsLock(YesOrNoEnum.NO.getType());
            super.save(dto);
            //默认分配一个身份
            userHelpService.distributionDefaultIdentity(dto.getId());
            //用户信息保存到redis
            userHelpService.saveOrUpdateUserToRedis(dto);
        } else {
            //后台修改用户
            User user = super.getById(dto.getId());
            if (user == null) {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("未查询到对应的用户信息").build();
            }
            user.setNotes(dto.getNotes());
            user.setPhone(dto.getPhone());
            user.setName(dto.getName());
            user.setPosition(dto.getPosition());
            if(!dto.getLoginName().equals(user.getLoginName())){
                //修改账号 密码重置
                user.setLoginName(dto.getLoginName());
                user.setPwd(Encryption.getEncryption(UserConstants.RESET_PWD,dto.getLoginName()));
            }
            user.setReceiveSms(dto.getReceiveSms());
            user.setReceiveSysMsg(dto.getReceiveSysMsg());
            super.updateById(user);
            //如果用户登录过 //生成保存token的key
            if (user.getLastLoginPlatform() != null) {
                String key = RedisConstants.LOGINNAME2TOKEN.concat(RedisConstants.KEY_JOINER)
                        .concat(user.getLastLoginPlatform().toString())
                        .concat(RedisConstants.KEY_JOINER)
                        .concat(user.getLoginName());
                String token = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.isNotEmpty(token)) {
                    //修改用户信息后应强制该用户下线
                    redisUtils.removeCurrentUserByToken(token);
                    stringRedisTemplate.delete(key);
                }
            }
            //用户信息保存到redis
            userHelpService.saveOrUpdateUserToRedis(user);
        }
    }

    /**
     * 忘记密码
     *
     * @param dto 包含用户信息
     * @author gengjiajia
     * @since 2018/12/15 15:49
     */
    public void unityForgetPwd(User dto) {
        User user = super.getOne(new QueryWrapper<User>().lambda()
                .eq(User::getPhone, dto.getPhone()));
        if (user == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("手机号不存在")
                    .build();
        }
        user.setPwd(Encryption.getEncryption(dto.getPwd(), user.getLoginName()));
        super.updateById(user);
    }

    /**
     * 获取用户及关联的单位信息列表
     *
     * @return 用户及关联的单位信息列表
     * @author gengjiajia
     * @since 2019/07/12 18:58
     */
    public List<User> findUserAndDepartmentList() {
        return baseMapper.findUserAndDepartmentList();
    }

    /**
     * 重置密码
     *
     * @param id 用户id
     * @author gengjiajia
     * @since 2019/07/29 14:10
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("用户信息不存在")
                    .build();
        }
        user.setPwd(Encryption.getEncryption(UserConstants.RESET_PWD, user.getLoginName()));
        this.updateById(user);
    }

    /**
     * 修改密码
     *
     * @param user 包含用户密码
     * @author gengjiajia
     * @since 2019/01/18 16:51
     */
    public void updateUserPwd(User user) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        User byUser = this.getById(customer.getId());
        if (byUser != null) {
            //本地账户本地创建
            if (!byUser.getPwd().equals(Encryption.getEncryption(user.getOldPwd(), byUser.getLoginName()))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_ERROR)
                        .message("原密码错误")
                        .build();
            }
            byUser.setPwd(Encryption.getEncryption(user.getPwd(), byUser.getLoginName()));
            this.updateById(byUser);
        }
    }

    /**
     * 账号锁定/解锁
     *
     * @param dto 包含锁定信息
     * @author gengjiajia
     * @since 2019/08/06 09:55
     */
    public void lock(User dto) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getIsAdmin().equals(YesOrNoEnum.YES.getType())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("非管理员不允许该操作")
                    .build();
        }
        User user = this.getById(dto.getId());
        if (user == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("用户信息不存在")
                    .build();
        }
        if (!dto.getIsLock().equals(user.getIsLock())) {
            user.setIsLock(dto.getIsLock());
            this.updateById(user);
        }
        //else 则是 无效操作 不理会
    }

    /**
     * 根据单位id修改对应用户状态
     *
     * @param  isLock 是否启用 0 是 1 否（主要是考虑到用户这边用的字段叫 isLck）
     * @param idRbacDepartment 单位id
     * @author gengjiajia
     * @since 2019/09/24 10:51
     */
    public void updateIsLockByIdRbacDepartment(Integer isLock,Long idRbacDepartment){
        baseMapper.updateIsLockByIdRbacDepartment(isLock,idRbacDepartment);
    }



    /**
    * 查询属于某几个单位的用户，得到{单位id，员工id集合}
    *
    * @param departmentIds 单位id的集合
    * @return Map<Long, List<Long>>
    * @author JH
    * @date 2019/9/23 16:23
    */
    public Map<Long,List<Long>> listUserInDepartment(List<Long> departmentIds) {

        if (CollectionUtils.isNotEmpty(departmentIds)) {
            List<User> userList = baseMapper.selectList(new LambdaQueryWrapper<User>().in(User::getIdRbacDepartment, departmentIds).orderByDesc(User::getGmtCreate));
           return userList.stream().collect(groupingBy(User::getIdRbacDepartment, mapping(User::getId, toList())));
        }
        return new HashMap<>(InnovationConstant.HASHMAP_DEFAULT_LENGTH);
    }

    /**
     * 身份认证
     *
     * @param phone 手机号
     * @param secret 秘钥 MD5(secretKey+Md5(phone))
     * @return 用户信息
     * @author gengjiajia
     * @since 2019/10/23 15:57
     */
    public Map<String,Object> authentication(String phone, String secret) {
        //通过秘钥与手机号做加密，然后对比判断是否是约定数据
        String localSecret = Encryption.getEncryption(UserConstants.SECRET_KEY, phone);
        if(!localSecret.equalsIgnoreCase(secret)){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message("登录信息错误")
                    .build();
        }
        //返回数据总载体
        Map<String, Object> info = Maps.newHashMap();
        //查询用户基本信息，并生成token
        User user = getUserAuthInfo(phone);
        Map userMap = JsonUtil.ObjectToMap(user,
                new String[]{"loginName", "phone", "name", "notes", "email", "position",
                        "nickName","userType"},
                null);
        info.put("user", userMap);
        //生成token
        String tokenStr = EncryptUtil.generateToken(RedisKeys.CUSTOMER);
        info.put("token", tokenStr);
        // 用户拥有的资源权限
        Customer customer = new Customer();
        getUserAuthResource(user.getId(), PlatformTypeEnum.WEB.getType(), customer, info);
        //用户信息存入redis
        userHelpService.saveCustomer(user, PlatformTypeEnum.WEB.getType(), tokenStr, customer);
        //维护登录信息
        userHelpService.updateLoginInfo(user, PlatformTypeEnum.WEB.getType(), new Date(), SessionHolder.getRequest());
        return info;
    }

    /**
     * 获取用户基本信息
     *
     * @param  phone 手机号
     * @return 用户基本信息
     * @author gengjiajia
     * @since 2019/10/23 16:14
     */
    private User getUserAuthInfo(String phone) {
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getLoginName, phone));
        //判断账号可用性
        if(user == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("登录账号存在异常")
                    .build();
        } else if (user.getIsLock().equals(YesOrNoEnum.YES.getType())){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LOGIN_DATA_SATUS_ERR)
                    .message("登录账号已被锁定")
                    .build();
        }
        return user;
    }



    public Map<String,Object> getLoginInfo(String secret) {
        //通过秘钥与token做加密，然后对比判断是否是约定数据
        String token = SessionHolder.getToken();
        String localSecret = Encryption.getEncryption(UserConstants.SECRET_KEY, token);
        if(!localSecret.equalsIgnoreCase(secret)){
            throw UnityRuntimeException.newInstance()
                    .message("登录信息错误")
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .build();
        }
        Map<String,Object> info = Maps.newHashMap();
        Customer customer = LoginContextHolder.getRequestAttributes();
        info.put(UserConstants.BUTTON_CODE_LIST, customer.getButtonCodeList());
        info.put(UserConstants.MENU_CODE_LIST, customer.getMenuCodeList());
        //TODO
        return null;
    }
}
