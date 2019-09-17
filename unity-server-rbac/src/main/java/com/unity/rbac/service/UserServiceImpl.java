
package com.unity.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.base.SessionHolder;
import com.unity.common.client.UcsClient;
import com.unity.common.client.vo.UcsUser;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constant.SafetyConstant;
import com.unity.common.constant.SysReminderConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.constants.Constants;
import com.unity.common.constants.RedisKeys;
import com.unity.common.enums.SysReminderDataSourceEnum;
import com.unity.common.enums.UserAccountLevelEnum;
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
import com.unity.rbac.enums.UcsSourceEnum;
import com.unity.rbac.enums.UserPerfectStatusEnum;
import com.unity.rbac.enums.UserSourceEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final UserDepartmentServiceImpl userDepartmentService;
    private final UserRoleServiceImpl userRoleService;
    private final ResourceServiceImpl resourceService;
    private final HashRedisUtils hashRedisUtils;
    private final UserHelpServiceImpl userHelpService;
    @javax.annotation.Resource
    private UcsClient ucsClient;
    private final DicUtils dicUtils;

    public UserServiceImpl(RedisUtils redisUtils, StringRedisTemplate stringRedisTemplate, UserResourceServiceImpl userResourceService,
                           UserIdentityServiceImpl userIdentityService, UserDepartmentServiceImpl userDepartmentService,
                           UserRoleServiceImpl userRoleService, ResourceServiceImpl resourceService, HashRedisUtils hashRedisUtils,
                           UserHelpServiceImpl userHelpService, DicUtils dicUtils) {
        this.redisUtils = redisUtils;
        this.stringRedisTemplate = stringRedisTemplate;
        this.userResourceService = userResourceService;
        this.userIdentityService = userIdentityService;
        this.userDepartmentService = userDepartmentService;
        this.userRoleService = userRoleService;
        this.resourceService = resourceService;
        this.hashRedisUtils = hashRedisUtils;
        this.userHelpService = userHelpService;
        this.dicUtils = dicUtils;
    }


    /**
     * 查询所有用户
     *
     * @return 用户信息列表
     * @author 郭振洋
     * @date 2019/7/18 9:29
     **/
    public List<Map<String, Object>> listUser() {
        List<User> userAndDepartmentList = baseMapper.findUserAndDepartmentList();
        Customer customer = LoginContextHolder.getRequestAttributes();
        List<User> collect = userAndDepartmentList.stream().filter(n -> !n.getId().equals(customer.getId())).collect(Collectors.toList());
        return JsonUtil.ObjectToList(collect, null, User::getId, User::getDepartment, User::getName, User::getLoginName);
    }


    /**
     * 查询属于某几个单位的用户，无论isDeleted是0还是1都返回
     *
     * @param idDepartments 单位id的集合，null时为查询所有的用户
     * @return java.util.List<java.lang.Long> 返回符合条件的用户的id，name集合
     * @author lifeihong
     * @date 2019/7/10 16:38
     */
    public Map<Long, String> listAllInDepartment(List<Long> idDepartments) {
        Map<String, Object> map = new HashMap<>(SafetyConstant.HASHMAP_DEFAULT_LENGTH);
        if (CollectionUtils.isNotEmpty(idDepartments)) {
            map.put("ids", idDepartments);
        }
        return baseMapper.listAllInDepartment(map).stream().collect(Collectors.toMap(User::getId, User::getName));
    }

    /**
     * 查询属于某几个单位的用户，得到{用户id，单位id}集合
     *
     * @param idDepartments 单位id的集合，null时为查询所有的用户
     * @return java.util.Map<java.lang.Long, java.lang.Long>返回符合条件的用户的{用户id，单位id}集合
     * @author lifeihong
     * @date 2019/7/10 17:19
     */
    public Map<Long, Long> listUserInDepartment(List<Long> idDepartments) {
        Map<String, Object> map = new HashMap<>(SafetyConstant.HASHMAP_DEFAULT_LENGTH);
        if (CollectionUtils.isNotEmpty(idDepartments)) {
            map.put("ids", idDepartments);
        }
        return baseMapper.listUserInDepartment(map).stream()
                .collect(Collectors.toMap(User::getId, User::getIdRbacDepartment));
    }

    /**
     * 用户统一注册（api、system）
     *
     * @param dto 包含用户注册必要信息
     * @author gengjiajia
     * @since 2018/12/05 15:11
     */
    @Transactional(rollbackFor = Exception.class)
    public Long unityUserRegister(User dto) {
        checkMainField(dto);
        //注册开始
        User user = new User();
        user.setNotes(dto.getNotes());
        user.setLoginName(dto.getLoginName());
        user.setPwd(Encryption.getEncryption(dto.getPwd(), user.getLoginName()));
        user.setPhone(dto.getPhone());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setNickName(dto.getNickName());
        user.setHeadPic(dto.getHeadPic());
        user.setWxOpenId(dto.getWxOpenId());
        user.setWxxOpenId(dto.getWxxOpenId());
        user.setIdRbacDepartment(dto.getIdRbacDepartment());
        user.setAccountLevel(dto.getAccountLevel());
        user.setPosition(dto.getPosition());
        super.save(user);
        //分配默认权限
        userHelpService.allocationPermission(user);
        return user.getId();
    }

    /**
     * 统一密码修改（api、system）
     *
     * @param pwd 密码
     * @author gengjiajia
     * @since 2018/12/07 08:59
     */
    @Transactional(rollbackFor = Exception.class)
    public void unityUpdatePwd(String oldPwd, String pwd) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        User user = super.getById(customer.getId());
        if (user == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("用户信息不存在")
                    .build();
        }
        //校验旧密码
        if (StringUtils.isNotEmpty(oldPwd) &&
                !Encryption.getEncryption(oldPwd, user.getLoginName()).equalsIgnoreCase(user.getPwd())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_ERROR)
                    .message("原密码错误")
                    .build();
        }
        pwd = Encryption.getEncryption(pwd, user.getLoginName());
        user.setPwd(pwd);
        //修改缓存中的密码
        customer.setPwd(pwd);
        redisUtils.putCurrentUserByToken(SessionHolder.getToken(), customer, Constants.APP_TOKEN_EXPIRE_DAY);
        super.updateById(user);
    }

    private void checkMainField(User dto) {
        UnityRuntimeException exception = UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS).build();
        if (dto.getAccountLevel() == null) {
            exception.setCode(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM);
            exception.setMessage("请选择项目级别");
            throw exception;
        }
        if (dto.getIdRbacDepartment() == null) {
            exception.setCode(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM);
            exception.setMessage("请选择单位");
            throw exception;
        }

        if (StringUtils.isEmpty(dto.getLoginName())) {
            exception.setMessage("请输入账号");
            throw exception;
        } else {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getLoginName, dto.getLoginName())
                    .eq(User::getIsDeleted, YesOrNoEnum.NO.getType());
            if (dto.getId() != null) {
                userLambdaQueryWrapper.ne(User::getId, dto.getId());
            }
            int loginNameCount = super.count(userLambdaQueryWrapper);
            if (loginNameCount > 0) {
                exception.setMessage("登录账号已存在");
                throw exception;
            }
        }
        if (StringUtils.isEmpty(dto.getName())) {
            exception.setMessage("请输入用户名称");
            throw exception;
        }
        String phone = dto.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, phone).eq(User::getIsDeleted, YesOrNoEnum.NO.getType());
            if (dto.getId() != null) {
                userLambdaQueryWrapper.ne(User::getId, dto.getId());
            }
            if (0 < super.count(userLambdaQueryWrapper)) {
                exception.setMessage("手机号已存在");
                throw exception;
            }
        }
        if (StringUtils.isNotEmpty(dto.getEmail())) {
            int emailCount = super.count(new QueryWrapper<User>().lambda()
                    .eq(User::getEmail, dto.getEmail())
                    .eq(User::getIsDeleted, YesOrNoEnum.NO.getType()));
            if (emailCount > 0) {
                exception.setMessage("邮箱已存在");
                throw exception;
            }
        }
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
        User user = pageEntity.getEntity();
        data.put("offset", (pageEntity.getPageable().getCurrent() - 1L) * pageEntity.getPageable().getSize());
        data.put("limit", pageEntity.getPageable().getSize());
        data.put("idRbacDepartment", user.getIdRbacDepartment());
        data.put("loginName", StringUtils.isNotBlank(user.getLoginName()) ? user.getLoginName().trim() : null);
        boolean isQueryByRoleId = false;
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
        if (customer.getIsSuperAdmin().equals(YesOrNoEnum.YES.getType()) || UserAccountLevelEnum.GROUP.getId().equals(customer.getAccountLevel())) {
            //超级管理员或一级管理员 放开数据权限
            data.put("dataPermissionIdList", null);
        } else if (customer.getAccountLevel().equals(UserAccountLevelEnum.PROJECT.getId())
                || CollectionUtils.isEmpty(customer.getDataPermissionIdList())) {
            grid.setTotal(0L);
            grid.setItems(Lists.newArrayList());
            return grid;
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
                User::getId, User::getDepartment, User::getLoginName, User::getNameInfoProject, User::getName, User::getIdRbacDepartment,
                User::getPhone, User::getPosition, User::getIsAdmin, User::getIsLock, User::getGroupConcatRoleName, User::getSource,
                User::getAccountLevel, User::getNotes
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
        m.put("accountLevelTitle", entity.getAccountLevel() != null ? UserAccountLevelEnum.of(entity.getAccountLevel()).getName() : "");
        m.put("sourceTitle", entity.getSource() != null ? UcsSourceEnum.of(entity.getSource()).getName() : "");
        m.put("groupConcatRoleName", entity.getGroupConcatRoleName() != null ? entity.getGroupConcatRoleName() : "");
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
        Map<String, Object> map = JsonUtil.ObjectToMap(user,
                this::adapterField,
                User::getId, User::getDepartment, User::getLoginName, User::getName, User::getPhone, User::getAccountLevel,
                User::getPosition,User::getGroupConcatRoleName, User::getSource, User::getNotes, User::getIsAdmin,
                User::getIdRbacDepartment
        );

        return map;
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
            if (user.getIdRbacDepartment() == null) {
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
                        "isAdmin"},
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
     * 调用用户中心接口校验用户信息
     *
     * @param user 用户信息
     * @return 用户信息
     * @author gengjiajia
     * @since 2019/07/27 16:19
     */
    private User goUcsInfoCheck(User user, String loginName, String pwd) {
        SystemResponse response = null;
        try {
            response = ucsClient.checkLoginInfo(UcsUser.newInstance()
                    .loginName(loginName)
                    .pwd(Encryption.getEncryption(pwd, loginName))
                    .source(UcsSourceEnum.SAFE.getId())
                    .build());
            log.info("===== 《unityLogin》 调用用户中心接口校验用户信息 ===== 接口出参 {}", GsonUtils.format(response));
        } catch (Exception e) {
            log.info("===== 《unityLogin》 用户中心服务不可用 ===== 异常 {}", e.getMessage());
        }
        if (response != null && response.getCode().equals(SystemResponse.FormalErrorCode.SUCCESS.getValue())) {
            //校验通过 本地用户如果为空则需要二次注册
            if (user == null) {
                Map<String, Object> body = GsonUtils.map(GsonUtils.format(response.getBody()));
                //二次注册
                user = callbackRegistration(loginName, Encryption.getEncryption(pwd, loginName),
                        Long.parseLong(body.get(UserConstants.ID).toString()),
                        Integer.parseInt(body.get(UserConstants.SOURCE).toString()),
                        body.get(UserConstants.DEPARTNAME).toString(),
                        body.get(UserConstants.NAME) != null ? body.get(UserConstants.NAME).toString() : ""
                );
            }
        } else {
            //用户中心异常
            throw UnityRuntimeException.newInstance()
                    .code(response != null ? SystemResponse.FormalErrorCode.of(response.getCode()) : SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message(response != null ? response.getMessage() : "远程服务不可用")
                    .build();
        }
        if (user == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.USERNAME_OR_PASSWORD_ERROR)
                    .message("用户名或密码错误")
                    .build();
        }
        return user;
    }

    /**
     * 二次注册
     * 用户登录时检测到用户中心存在而本地不存在
     *
     * @param loginName 账号
     * @return 用户信息
     * @author gengjiajia
     * @since 2019/07/27 16:21
     */
    private User callbackRegistration(String loginName, String pwd, Long idUcsUser, Integer source, String departName, String name) {
        User user = new User();
        user.setLoginName(loginName);
        user.setPwd(pwd);
        user.setSource(source);
        user.setName(name);
        user.setIsLock(YesOrNoEnum.NO.getType());
        this.save(user);
        //默认分配一个身份
        userHelpService.distributionDefaultIdentity(user.getId());
        return user;
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
//            throw UnityRuntimeException.newInstance()
//                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
//                    .message("暂未分配权限，请联系管理员")
//                    .build();
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
            //非本地账号 注册到用户中心
            if (!UserAccountLevelEnum.PROJECT.getId().equals(dto.getAccountLevel()) && !dto.getIsAdmin().equals(YesOrNoEnum.YES.getType())) {
                saveToUcs(dto);
            }
            //完善信息标识
            dto.setSource(UcsSourceEnum.SAFE.getId());
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
            if (!UcsSourceEnum.SAFE.getId().equals(user.getSource())) {
                user.setAccountLevel(dto.getAccountLevel());
                user.setIsAdmin(YesOrNoEnum.NO.getType());
                user.setIdRbacDepartment(dto.getIdRbacDepartment());
                user.setIsLock(YesOrNoEnum.NO.getType());
            }
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
     * 账号注册到用户中心
     *
     * @param dto 用户信息
     * @author gengjiajia
     * @since 2019/08/01 15:34
     */
    private void saveToUcs(User dto) {
        //调用UcsFeignClient向用户中心注册
        //获取当前用户单位名称及所有直属上层单位名称
        String departName = userDepartmentService.getImmediateSupervisorName(dto.getIdRbacDepartment());
        if (StringUtils.isEmpty(departName)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("单位信息不存在").build();
        }
        SystemResponse response = null;
        try {
            response = ucsClient.pushUserToUcs(UcsUser.newInstance()
                    .loginName(dto.getLoginName())
                    .name(dto.getName())
                    .pwd(dto.getPwd())
                    .departName(departName)
                    .source(UcsSourceEnum.SAFE.getId())
                    .build());
            log.info("===== 《后台新增用户注册到UCS》 ===== 返回数据 {}", GsonUtils.format(response));
        } catch (Exception e) {
            log.info("===== 《后台新增用户注册到UCS》 ===== 异常 {}", e.getMessage());
        }
        if (response == null) {
            //用户中心异常
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .message("远程服务不可用")
                    .build();
        } else if (response.getCode().equals(SystemResponse.FormalErrorCode.SUCCESS.getValue())
                || response.getCode().equals(SystemResponse.FormalErrorCode.MODIFY_DATA_REPEAT_OPERATION.getValue())) {
            //用户中心返回成功或用户中心存在该账号 本地保存信息
        } else {
            //用户中心异常
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.of(response.getCode()))
                    .message(response.getMessage())
                    .build();
        }
    }


    /**
     * 通过userId集获取对应数据
     *
     * @param userIds 用户id集
     * @return Map key:id value:user
     * @author gengjiajia
     * @since 2019/01/09 16:01
     */
    public Map<String, Map<String, Object>> findUserInfoByIdIn(List<Long> userIds) {
        Map<String, Map<String, Object>> map = Maps.newHashMap();
        List<Map<String, Object>> mapList = JsonUtil.ObjectToList(
                super.list(new QueryWrapper<User>().lambda().in(User::getId, userIds.toArray())),
                new String[]{"id", "loginName", "position", "phone", "email", "name", "nickName",
                        "headPic", "notes", "gmtCreate", "wxOpenId", "idRbacDepartment"},
                (m, u) -> m.put("gmtCreate", DateUtils.timeStamp2Date(u.getGmtCreate())));
        mapList.forEach(m -> map.put(m.get("id").toString(), m));
        return map;
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
        } else if (UserSourceEnum.OA.getId().equals(user.getSource())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("OA账号不允许重置密码")
                    .build();
        } else if (UserAccountLevelEnum.PROJECT.getId().equals(user.getAccountLevel()) || user.getIsAdmin().equals(YesOrNoEnum.YES.getType())) {
            //本地账号本地重置
            user.setPwd(Encryption.getEncryption(UserConstants.RESET_PWD, user.getLoginName()));
            this.updateById(user);
        } else {
            SystemResponse response = null;
            try {
                response = ucsClient.resetPassword(UcsUser.newInstance().loginName(user.getLoginName()).build());
            } catch (Exception e) {
                log.error("===== 《重置密码同步到UCS》 ===== 异常 {}", e.getMessage());
            }
            if (response != null && response.getCode().equals(SystemResponse.FormalErrorCode.SUCCESS.getValue())) {
                user.setPwd(Encryption.getEncryption(UserConstants.RESET_PWD, user.getLoginName()));
                this.updateById(user);
            } else {
                //用户中心异常
                throw UnityRuntimeException.newInstance()
                        .code(response != null ? SystemResponse.FormalErrorCode.of(response.getCode()) : SystemResponse.FormalErrorCode.SERVER_ERROR)
                        .message(response != null ? response.getMessage() : "远程服务不可用")
                        .build();
            }
        }
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
     * 返回当前登录账号，发送时可选择的账号
     *
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author JH
     * @date 2019/8/8 9:00
     */
    public List<Map<String, Object>> getUserList() {

        Customer customer = LoginContextHolder.getRequestAttributes();
        int level = customer.getAccountLevel();
        List<User> list;
        //集团可选系统中所有账号
        if (level == 1) {
            list = baseMapper.selectList(null);

            //本单位所有账号
        } else if (level == 4) {
            list = baseMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getId, customer.getId()).eq(User::getIdRbacDepartment, customer.getIdRbacDepartment()));
        } else {
            list = baseMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getIdRbacDepartment, customer.getIdRbacDepartment()));
        }
        return JsonUtil.ObjectToList(list, null, User::getId, User::getName);

    }

    /**
     * 根据角色、单位集合获取用户id集合
     *
     * @param map 参数map
     * @return java.util.Map<Long, List < Long>>
     * @author JH
     * @date 2019/8/8 18:11
     */
    public Map<Long, List<Long>> getUserIdsByRoleIdAndDepartmentIds(Map<String, Object> map) {
        return baseMapper.getUserIdsByRoleIdAndDepartmentIds(map).stream().collect(groupingBy(User::getIdRbacDepartment, Collectors.mapping(User::getId, Collectors.toList())));
    }

    /**
     * 获取某单位某角色的用户
     *
     * @param map 参数map
     * @return java.util.List<java.lang.Long>
     * @author JH
     * @date 2019/8/8 18:11
     */
    public List<Long> getUserIdsByRoleIdAndDepartmentId(Map<String, Object> map) {
        return baseMapper.getUserIdsByRoleIdAndDepartmentIds(map).stream().map(User::getId).collect(Collectors.toList());
    }


}
