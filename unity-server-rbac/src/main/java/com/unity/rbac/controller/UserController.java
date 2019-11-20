package com.unity.rbac.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.ParamConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.GsonUtils;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.entity.User;
import com.unity.rbac.enums.UserTypeEnum;
import com.unity.rbac.service.UserServiceImpl;
import com.unity.rbac.utils.RegExpValidatorUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 用户信息后台控制类
 * <p>
 *
 * @author gengjiajia
 * @since 2018/12/21 13:45
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController extends BaseWebController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 查询账号管理列表
     *
     * @param pageEntity 包含用户列表查询条件
     * @return code : 0 表示成功
     * @author gengjiajia
     * @since 2018/12/11 10:04
     */
    @PostMapping("listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<User> pageEntity) {
        return success(userService.findUserListBySystem(pageEntity));
    }

    /**
     * 新增或修改用户信息
     *
     * @param user 包含用户信息
     * @return code 0 表示成功
     * -1005 数据已存在
     * -1011 数据不存在
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/21 13:45
     */
    @PostMapping("saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdateUserInfo(@RequestBody User user) {
        log.info("===== 《saveOrUpdate》 新增用户 入参 {}",GsonUtils.format(user));
        UnityRuntimeException exception = checksSaveOrUpdateUserInfo(user);
        if (exception != null) {
            return error(exception.getCode(), exception.getMessage());
        }
        userService.saveOrUpdateUserInfo(user);
        return success("操作成功");
    }

    /**
     * 校验用户新增或修改的信息
     *
     * @param user 用户信息
     * @author gengjiajia
     * @since 2019/07/05 10:30
     */
    private UnityRuntimeException checksSaveOrUpdateUserInfo(User user) {
        //校验必填字段
        UnityRuntimeException exception = checkNonEmptyUserInfo(user);
        if(exception != null){
            return exception;
        }
        //校验非必填字段
        return checkCanEmptyUserInfo(user);
    }


    /**
     * 删除用户
     *
     * @param user 包含要删除的用户id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * @author gengjiajia
     * @since 2018/12/17 17:12
     */
    @PostMapping("deleteById")
    public Mono<ResponseEntity<SystemResponse<Object>>> deleteById(@RequestBody User user) {
        if (user == null || user.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "未获取到要删除的用户ID");
        }
        userService.delUser(user.getId());
        return success("删除成功");
    }

    /**
     * 重置密码
     *
     * @param user 包含用户id
     * @return code: 0表示成功
     * -1013 数据不存在
     * @author gengjiajia
     * @since 2019/01/18 15:09
     */
    @PostMapping("resetPwd")
    public Mono<ResponseEntity<SystemResponse<Object>>> resetPwd(@RequestBody User user) {
        // 非超级管理员不允许重置密码
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getIsAdmin().equals(YesOrNoEnum.YES.getType())){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY)
                    .message("非管理员不允许重置密码")
                    .build();
        }
        if (user == null || user.getId() == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "用户信息不存在");
        }
        userService.resetPwd(user.getId());
        return success("密码重置成功");
    }

    /**
     * 修改密码
     *
     * @param user 包含用户密码
     * @return code: 0 成功
     * -1011 用户信息不存在
     * @author gengjiajia
     * @since 2019/01/18 16:51
     */
    @PostMapping("updateUserPwd")
    public Mono<ResponseEntity<SystemResponse<Object>>> updateUserPwd(@RequestBody User user) {
        if(user == null || StringUtils.isEmpty(user.getOldPwd()) || StringUtils.isEmpty(user.getPwd())){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到密码");
        }
        userService.updateUserPwd(user);
        return success("修改成功");
    }

    /**
     * 忘记密码
     *
     * @param dto 包含用户信息
     * @return code 0 表示成功
     * @author gengjiajia
     * @since 2018/12/15 15:49
     */
    @PostMapping("forgetPwd")
    public Mono<ResponseEntity<SystemResponse<Object>>> unityForgetPwd(@RequestBody User dto) {
        userService.unityForgetPwd(dto);
        return success("更新成功");
    }

    /**
     * 账号锁定/解锁
     *
     * @param dto 包含用户信息
     * @return code 0 表示成功
     * @author gengjiajia
     * @since 2018/12/15 15:49
     */
    @PostMapping("lock")
    public Mono<ResponseEntity<SystemResponse<Object>>> lock(@RequestBody User dto) {
        if(dto == null || dto.getId() == null || dto.getIsLock() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到要锁定的信息");
        }
        userService.lock(dto);
        return success("操作成功");
    }

    /**
     * 检验tokan是否过期
     *
     * @return code : 0 正常
     * -1009 tokan失效
     * @author gengjiajia
     * @since 2019/05/15 20:33
     */
    @PostMapping("checkToken")
    public Mono<ResponseEntity<SystemResponse<Object>>> checkToken() {
        LoginContextHolder.getRequestAttributes();
        return success("token正常");
    }

    /**
     * 账号列表获取指定用户信息
     *
     * @param user 包含指定用户的id
     * @return code 0 表示成功
     * -1013 缺少必要参数
     * -1011 用户不存在
     * @author gengjiajia
     * @since 2018/12/11 13:55
     */
    @PostMapping("getUserInfoToUserListById")
    public Mono<ResponseEntity<SystemResponse<Object>>> getUserInfoToUserListById(@RequestBody User user) {
        if(user == null || user.getId() == null){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到指定用户ID");
        }
        return success(userService.getUserById(user.getId()));
    }

    /**
     * 获取登录信息
     *
     * @param  param 认证参数
     * @return code -> 0 表示成功
     * @author gengjiajia
     * @since 2019/10/24 09:29  
     */
    @PostMapping("getLoginInfo")
    public Mono<ResponseEntity<SystemResponse<Object>>> getLoginInfo(@RequestBody Map<String,String> param) {
        if(MapUtils.isEmpty(param) || StringUtils.isEmpty(param.get("appToken"))){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"未获取到认证信息");
        }
        return success(userService.getLoginInfo(param.get(UserConstants.PHONE),param.get("appToken")));
    }

    /**
     * 校验用户必填字段的合法性
     *
     * @param  user 用户信息
     * @return 错误信息及错误码
     * @author gengjiajia
     * @since 2019/07/26 09:18  
     */
    private UnityRuntimeException checkNonEmptyUserInfo(User user){
        if(user.getLoginName() != null){
            user.setLoginName(user.getLoginName().replaceAll(" ",""));
        }
        Customer customer = LoginContextHolder.getRequestAttributes();
        if(!customer.getIsAdmin().equals(YesOrNoEnum.YES.getType())){
            //当前账号非管理员
            return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.OPERATION_NO_AUTHORITY).message("非管理员不能创建账号！").build();
        }
        if(user.getUserType() == null){
            return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("请选择账号类别").build();
        }
        if(UserTypeEnum.ORDINARY.getId().equals(user.getUserType())){
            if(user.getReceiveSms() == null){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("请选择是否接收短信").build();
            } else if(user.getReceiveSysMsg() == null){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("请选择是否接收系统消息").build();
            } else if(user.getReceiveSms().equals(YesOrNoEnum.YES.getType())
                    && !RegExpValidatorUtil.checkPhone(user.getPhone())){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("请以手机号格式填写电话").build();
            }
        }
        //普通账号需选择单位
        if (UserTypeEnum.ORDINARY.getId().equals(user.getUserType()) && user.getIdRbacDepartment() == null) {
            return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("请选择所属单位").build();
        }
        if(!UserTypeEnum.ORDINARY.getId().equals(user.getUserType())){
            user.setIdRbacDepartment(null);
        }
        if(!UserTypeEnum.ADMIN.getId().equals(user.getUserType())){
            if(!RegExpValidatorUtil.checkPhone(user.getLoginName().trim())){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("非管理员账号请使用手机号！").build();
            }
        } else {
            if(!RegExpValidatorUtil.checkLoginName(user.getLoginName().trim())){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("管理员账号由字母或数字组成，限制20字符！").build();
            }
        }
        //校验账号唯一性
        if (user.getId() == null && userService.count(new LambdaQueryWrapper<User>().eq(User::getLoginName, user.getLoginName())) > 0){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS).message("登录账号已存在！").build();

        } else {
            if(userService.count(new LambdaQueryWrapper<User>().ne(User::getId,user.getId()).eq(User::getLoginName, user.getLoginName())) > 0){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS).message("登录账号已存在！").build();
            }
        }
        if(StringUtils.isBlank(user.getName())){
            return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message("请填写用户名称").build();
        } else if(user.getName().trim().length() > ParamConstants.PARAM_MAX_LENGTH_20){
            return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("用户名称限制在20个字符内！").build();
        }
        return null;
    }

    /**
     * 校验用户非必填字段的合法性
     *
     * @param  user 用户信息
     * @return 错误信息及错误码
     * @author gengjiajia
     * @since 2019/07/26 09:18
     */
    private UnityRuntimeException checkCanEmptyUserInfo(User user){
        if (StringUtils.isNotBlank(user.getPhone())) {
            user.setPhone(user.getPhone().replaceAll(" ",""));
            if(user.getPhone().length() > ParamConstants.PARAM_MAX_LENGTH_20){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message("联系电话最长支持输入20个字符！").build();
            }
            //校验手机号唯一
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,user.getPhone().trim());
            if(user.getId() != null){
                wrapper.ne(User::getId,user.getId());
            }
            if(userService.count(wrapper) > 0){
                return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS).message("联系电话已存在！").build();
            }
        }
        if (StringUtils.isNotBlank(user.getPosition())
                && user.getPosition().length() > ParamConstants.PARAM_MAX_LENGTH_20) {
            return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message("职位限制长度不得超过20字！").build();
        }
        if (StringUtils.isNotBlank(user.getNotes())
                && user.getNotes().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message("备注长度不得超过500字！").build();
        }
        return null;
    }
}