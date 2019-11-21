
package com.unity.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.constants.Constants;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.Dic;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.IpAdrressUtil;
import com.unity.common.util.RedisUtils;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.HashRedisUtils;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.dao.UserDao;
import com.unity.rbac.entity.User;
import com.unity.rbac.entity.UserIdentity;
import com.unity.rbac.entity.UserRole;
import com.unity.rbac.enums.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public class UserHelpServiceImpl extends BaseServiceImpl<UserDao, User> implements IService<User> {
    private final RedisUtils redisUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserIdentityServiceImpl userIdentityService;
    private final UserDepartmentServiceImpl userDepartmentService;
    private final UserRoleServiceImpl userRoleService;
    private final HashRedisUtils hashRedisUtils;
    private final DicUtils dicUtils;

    public UserHelpServiceImpl(RedisUtils redisUtils, StringRedisTemplate stringRedisTemplate,
                               UserIdentityServiceImpl userIdentityService, UserDepartmentServiceImpl userDepartmentService,
                               UserRoleServiceImpl userRoleService, HashRedisUtils hashRedisUtils, DicUtils dicUtils) {
        this.redisUtils = redisUtils;
        this.stringRedisTemplate = stringRedisTemplate;
        this.userIdentityService = userIdentityService;
        this.userDepartmentService = userDepartmentService;
        this.userRoleService = userRoleService;
        this.hashRedisUtils = hashRedisUtils;
        this.dicUtils = dicUtils;
    }


    /**
     * 维护用户最后一次登录信息
     *
     * @param user 用户信息
     * @param os   所属平台
     * @author gengjiajia
     * @since 2019/07/27 15:03
     */
    @Async
    void updateLoginInfo(User user, Integer os, Date now, HttpServletRequest request) {
        user.setLastLoginIp(IpAdrressUtil.getIpAdrress(request));
        user.setLastLoginPlatform(os);
        user.setSource(os);
        user.setGmtLoginLast(now);
        super.updateById(user);
    }

    /**
     * 保存用户信息到redis
     *
     * @param user     用户信息
     * @param os       操作终端
     * @param tokenStr token
     * @author gengjiajia
     * @since 2019/07/03 16:57
     */
    void saveCustomer(User user, Integer os, String tokenStr, Customer customer) {
        //token存入redis 生成key 规则 —> 固定头:登录终端:登录账号
        String key = RedisConstants.LOGINNAME2TOKEN.concat(RedisConstants.KEY_JOINER)
                .concat(os.toString())
                .concat(RedisConstants.KEY_JOINER)
                .concat(user.getLoginName());
        //用户信息有效期
        Integer day = os.equals(PlatformTypeEnum.ANDROID.getType()) || os.equals(PlatformTypeEnum.IOS.getType())
                ? Constants.APP_TOKEN_EXPIRE_DAY : Constants.PC_TOKEN_EXPIRE_DAY;
        //获取原token 用于清除登录信息
        String oldToken = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(oldToken)) {
            redisUtils.removeCurrentUserByToken(oldToken);
        }
        stringRedisTemplate.opsForValue().set(key, tokenStr, day, TimeUnit.DAYS);
        //查询用户数据权限
        List<Long> dataPermissionIdList = userDepartmentService.findDataPermissionIdListByUserId(user.getId());
        //查询用户关联的角色
        List<Long> roleIds = userRoleService.selectRoleIdsByUserId(user.getId());
        customer.setRoleList(roleIds);
        // redis缓存用户信息
        customer.setId(user.getId());
        customer.setLoginName(user.getLoginName());
        customer.setEmail(user.getEmail());
        customer.setPhone(user.getPhone());
        customer.setPwd(user.getPwd());
        customer.setHeadPic(user.getHeadPic());
        customer.setName(user.getName());
        customer.setIdRbacDepartment(user.getIdRbacDepartment());
        customer.setNameRbacDepartment(user.getDepartment());
        customer.setDataPermissionIdList(dataPermissionIdList);
        customer.setIsSuperAdmin(user.getSuperAdmin());
        customer.setIsAdmin(user.getSuperAdmin().equals(YesOrNoEnum.YES.getType()) ? YesOrNoEnum.YES.getType()
                : UserTypeEnum.ADMIN.getId().equals(user.getUserType()) ? YesOrNoEnum.YES.getType() : YesOrNoEnum.NO.getType());
        customer.setUserType(user.getUserType());
        customer.setOs(os);
        customer.setDepType(user.getDepType());
        //获取用户所在单位可处理数据范围
        if(user.getIdRbacDepartment() != null){
            List<Dic> dicList = dicUtils.getDicsByGroupCode(DicConstants.DEPART_HAVE_LIST_TYPE);
            List<Integer> bizTypeList = dicList.stream()
                    .filter(dic -> dic.getDicValue().equals(user.getIdRbacDepartment().toString()))
                    .map(dic -> Integer.parseInt(dic.getDicCode()))
                    .collect(Collectors.toList());
            customer.setTypeRangeList(bizTypeList);
        } else if(user.getSuperAdmin().equals(YesOrNoEnum.YES.getType())
                || user.getUserType().equals(UserTypeEnum.LEADER.getId())
                || user.getUserType().equals(UserTypeEnum.ADMIN.getId())){
            List<Dic> dicList = dicUtils.getDicsByGroupCode(DicConstants.DEPART_HAVE_LIST_TYPE);
            List<Integer> bizTypeList = dicList.stream()
                    .map(dic -> Integer.parseInt(dic.getDicCode()))
                    .collect(Collectors.toList());
            customer.setTypeRangeList(bizTypeList);
        }
        redisUtils.syncPutCurrentUserByToken(tokenStr, customer, day);
    }

    /**
     * 更新用户信息到redis
     *
     * @param user 用户信息
     * @author gengjiajia
     * @since 2019/07/11 09:24
     */
    @Async
    void saveOrUpdateUserToRedis(User user) {
        //先删后存
        String key = RedisConstants.USER.concat(user.getId().toString());
        hashRedisUtils.removeValueByKey(key);
        Map<String, Object> map = GsonUtils.map(GsonUtils.format(user));
        hashRedisUtils.putValueByKey(key, map);
    }

    /**
     * 分配默认身份
     *
     * @param id 用户id
     * @author gengjiajia
     * @since 2019/07/05 15:40
     */
    @Async
    void distributionDefaultIdentity(Long id) {
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setIdRbacUser(id);
        userIdentity.setIdRbacIdentity(UserConstants.PC_DEFAULT_IDENTITY_ID);
        userIdentityService.save(userIdentity);
        UserIdentity userIdentity2 = new UserIdentity();
        userIdentity2.setIdRbacUser(id);
        userIdentity2.setIdRbacIdentity(UserConstants.MOBILE_DEFAULT_IDENTITY_ID);
        userIdentityService.save(userIdentity2);
    }

    /**
     * 分配默认权限
     *
     * @param user 注册的新用户
     * @author gengjiajia
     * @since 2019/01/12 14:26
     */
    @Async
    void allocationPermission(User user) {
        // 用户和角色的绑定关系
        String roleIds = user.getRoleIds();
        if (StringUtils.isNotEmpty(roleIds)) {
            List<UserRole> userRoleList = new ArrayList<>();
            Arrays.stream(roleIds.split(ConstString.SPLIT_COMMA)).forEach(e -> {
                UserRole userRole = new UserRole();
                userRole.setIdRbacUser(user.getId());
                userRole.setIdRbacRole(Long.parseLong(e));
                userRoleList.add(userRole);
            });
            userRoleService.saveBatch(userRoleList);
        }
    }
}
