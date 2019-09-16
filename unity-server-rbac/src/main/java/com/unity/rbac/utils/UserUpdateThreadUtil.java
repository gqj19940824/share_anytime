package com.unity.rbac.utils;

import com.unity.common.constants.Constants;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.pojos.Customer;
import com.unity.common.util.RedisUtils;
import com.unity.rbac.constants.UserConstants;
import com.unity.rbac.entity.User;
import com.unity.rbac.service.UserTokenServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户修改信息辅助线程类
 * <p>
 * create by gengjiajia at 2019/03/12 14:34
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@Slf4j
public class UserUpdateThreadUtil implements Runnable {

    private UserTokenServiceImpl userTokenService;
    private RedisUtils redisUtils;
    private StringRedisTemplate redisTemplate;
    private User user;
    private Integer os;

    @Override
    public void run() {
        //通过os判断调用接口的是移动端还是后端
//        String token = userTokenService.getLastUserTokenByUserIdAndIdentity/user.getId(), Long.parseLong(os.toString()));
//        Object token = redisTemplate.opsForValue().get(UserConstants.LOGINNAME2TOKEN + user.getIdentity() + ":" + user.getLoginName());
//
//        //用户自己修改信息 更新redis
//        try {
//            Customer customer;
//            if (token != null) {
//                customer = redisUtils.getCurrentUserByToken((String) token);
//                if (customer != null) {
//                    Integer day = PlatformTypeEnum.ANDROID.getType() == os || PlatformTypeEnum.IOS.getType() == os ? constants.APP_TOKEN_EXPIRE_DAY : constants.PC_TOKEN_EXPIRE_DAY;
//                    customer.setId(user.getId());
//                    customer.setLoginName(user.getLoginName());
//                    customer.setEmail(user.getEmail());
//                    customer.setPhone(user.getPhone());
//                    customer.setPwd(user.getPwd());
//                    customer.setIdEntity(Long.parseLong(os.toString()));
//                    customer.setHeadPic(user.getHeadPic());
//                    customer.setOs(os);
//                    customer.setName(user.getName());
//                    customer.setNickName(user.getNickName());
//                    customer.setIdRbacDepartment(user.getIdRbacDepartment());
//                    //redis存储新token     TODO
//                    //redisUtils.putCurrentUserByToken(token, customer, day);
//                }
//            }
    }
}
