package com.unity.me.controller.feign;

import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.GsonUtils;
import com.unity.me.service.example.api.impl.EasemobIMUsers;
import io.swagger.client.model.RegisterUsers;
import io.swagger.client.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 环信注册<br>
 *
 * @author wangbin
 * @create 2019年2月14日16:22:51
 * @since 1.0.0
 */
@RestController
@RequestMapping("/feign/chatLog")
@Slf4j
public class ChatLogFeignController  extends BaseWebController {



    /**
     * 单个用户注册
     * @param map 用户ID
     * @return body  0 为成功 1 已注册 2 失败
     */
    @PostMapping("/singleRegisterUser")
    public Mono<ResponseEntity<SystemResponse<Object>>>  singleRegisterUser(@RequestBody Map<String , Object> map ) {
        String password = "PBK@2019";
        Object userId = map.get("userId");
        if (userId == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST,"用户ID为空");
        }
        int body = 0 ;
        EasemobIMUsers ease = new EasemobIMUsers();
        RegisterUsers users = new RegisterUsers();
        User payLoad = new User().username(userId.toString()).password(password);
        users.add(payLoad);
        Object objectNode =  ease.getIMUserByUserName(userId.toString());
        if (objectNode != null){
            //log.info("=====获取环信用户信息==== {}", GsonUtils.format(objectNode));
            body = 1;
        }else {
            //注册用户
            Object createNewUserSingleNode =  ease.createNewIMUserSingle(users);
            if (createNewUserSingleNode == null){
                body = 2;
            } /*else {
                log.info("=====注册环信用户信息==== {}", GsonUtils.format(createNewUserSingleNode));
            }*/
        }
        return success(body);
    }

    /**
     * 用户批量注册
     *
     * @param map 用户账号
     * @return body  0 为成功 1:失败或已注册
     */
    @PostMapping("/batchRegisterUser")
    public Mono<ResponseEntity<SystemResponse<Object>>> batchRegisterUser(@RequestBody Map<String, List<String>> map) {
        String password = "PBK@2019";
        List<String> userIds = map.get("userId");
        if (CollectionUtils.isEmpty(userIds)) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, "用户ID为空");
        }
        int body = 0;
        EasemobIMUsers ease = new EasemobIMUsers();
        RegisterUsers users = new RegisterUsers();
        userIds.forEach(userId -> {
            User payLoad = new User().username(userId).password(password);
            users.add(payLoad);
        });
        Object createNewUserSingleNode = ease.createNewIMUserBatch(users);
        if (createNewUserSingleNode == null){
            body = 1;
        }
        return success(body);
    }

    /**
     * 用户退出登录
     *
     * @param id 用户账号
     * @return body  0 为成功
     */
    @GetMapping("/disconnectIMUser/{id}")
    public void disconnectIMUser(@PathVariable("id") String id) {
        EasemobIMUsers ease = new EasemobIMUsers();
        Object body = ease.disconnectIMUser(id);
        log.info("===== 环信--用户退出登录--接口返回数据 {}",GsonUtils.format(body));
    }
}
