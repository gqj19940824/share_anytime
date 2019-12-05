package com.unity.rbac.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.client.SystemClient;
import com.unity.common.constants.Constants;
import com.unity.common.enums.PlatformTypeEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.util.RedisUtils;
import com.unity.common.util.XyDates;
import com.unity.rbac.entity.User;
import com.unity.rbac.entity.UserToken;
import com.unity.rbac.service.UserTokenServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

/**
 * 用户登录辅助线程类
 * <p>
 * create by gengjiajia at 2019/03/12 14:34
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@Slf4j
public class UserLoginThreadUtil implements Runnable {

    private UserTokenServiceImpl userTokenService;
    private SystemClient systemClient;
    private RedisUtils redisUtils;
    private User user;
    private Map<String, Object> info;
    private Long identity;
    private Long idRbacDepartment;
    private Integer os;
    private String tokenStr;

    @Override
    public void run() {

        //token 过期时长 天数
        Integer day = PlatformTypeEnum.ANDROID.getType() == os || PlatformTypeEnum.IOS.getType() == os ? Constants.APP_TOKEN_EXPIRE_DAY : Constants.PC_TOKEN_EXPIRE_DAY;
        //维护信息到Redis -> customer
        try {
//            Customer customer = new Customer();
//            customer.setId(user.getId());
//            customer.setLoginName(user.getLoginName());
//            customer.setEmail(user.getEmail());
//            customer.setPhone(user.getPhone());
//            customer.setPwd(user.getPwd());
//            customer.setIdEntity(identity);
//            customer.setIdentityList((List<Long>) info.get("identityIds"));
//            customer.setModuleResource((List<String>) info.get("moduleResource"));
//            customer.setRoleList((List<Long>) info.get("roleIds"));
//            customer.setAuth((List<String>) info.get("apiResource"));
//            customer.setHeadPic(user.getHeadPic());
//            customer.setOs(os);
//            customer.setName(user.getName());
//            customer.setIdRbacDepartment(idRbacDepartment);
//
//
//            customer.setOutpsotList((List<Long>) info.get("outpostIds"));
//            customer.setDepartmentList((List<Long>) info.get("departmentIds"));
//            customer.setUserType(user.getUserType());
//
//
//            //redis存储新token
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===== 《UserLoginThreadUtil》登录辅助线程保存Customer异常 {}", e.getMessage());
        }

        //清除旧token
        String oldToken = userTokenService.findUserTokenByUserIdAndIdentity(user.getId(), identity);
        if (oldToken != null) {
            redisUtils.removeCurrentUserByToken(oldToken);
            /*if (identity.intValue() == PlatformTypeEnum.SYSTEM.getType()) {
                redisUtils.removeCurrentUserByToken(oldToken.concat("departs"));
            }*/
            LambdaQueryWrapper<UserToken> wrapper = new QueryWrapper<UserToken>().lambda()
                    .eq(UserToken::getIdRbacUser, user.getId());
            if(identity.intValue() ==PlatformTypeEnum.SYSTEM.getType()){
                wrapper.eq(UserToken::getIdRbacIdentity,identity);
            } else {
                wrapper.ne(UserToken::getIdRbacIdentity,(long)PlatformTypeEnum.SYSTEM.getType());
            }
            userTokenService.remove(wrapper);
        }

        Date now = new Date();
        //3.校验成功，维护token
        UserToken token = new UserToken();
        token.setIdRbacIdentity(identity);
        token.setIdRbacUser(user.getId());
        token.setToken(tokenStr);
        token.setLoginPlatform(os);
        token.setGmtCreate(XyDates.getTime(now));
        token.setIsDeleted(YesOrNoEnum.NO.getType());
        userTokenService.save(token);
        log.info("===== 《UserLoginThreadUtil》登录辅助线程保存信息成功。");
    }


   /* public static void main(String[] args) {
        String content = "[null,{\"name\":\"测试党委身份\",\"idParent\":3,\"gradationCode\":\".2.4\",\"depType\":2,\"id\":23,\"gmtCreate\":1552291679064,\"gmtModified\":1552291679064,\"isDeleted\":0,\"notes\":\"大师\",\"creator\":\"23.许利利\",\"editor\":\"1.系统管理员\"},{\"name\":\"测试第二支部\",\"idParent\":23,\"gradationCode\":\".2.4.2\",\"depType\":3,\"id\":31,\"gmtCreate\":1552370420691,\"gmtModified\":1552370420691,\"isDeleted\":0,\"notes\":\"测试第一支部\",\"creator\":\"1.系统管理员\",\"editor\":\"1.系统管理员\"},{\"name\":\"新航城党委\",\"idParent\":4,\"gradationCode\":\".2.1.3\",\"depType\":2,\"id\":15,\"gmtCreate\":1552269793337,\"gmtModified\":1552269793337,\"isDeleted\":0,\"notes\":\"的\",\"creator\":\"admin.管理员\",\"editor\":\"admin.管理员\"},{\"name\":\"北京新航城控股有限公司\",\"idParent\":3,\"gradationCode\":\".2.8\",\"depType\":1,\"id\":32,\"gmtCreate\":1552377466644,\"gmtModified\":1552377466644,\"isDeleted\":0,\"notes\":\"北京新航城控股有限公司\",\"creator\":\"1.系统管理员\",\"editor\":\"1.系统管理员\"},{\"name\":\"博大第一支部\",\"idParent\":11,\"gradationCode\":\".2.3.1.1\",\"depType\":3,\"id\":24,\"gmtCreate\":1552293347181,\"gmtModified\":1552293347181,\"isDeleted\":0,\"notes\":\"大\",\"creator\":\"1.系统管理员\",\"editor\":\"1.系统管理员\"},{\"name\":\"博大网信党组织\",\"idParent\":9,\"gradationCode\":\".2.3.1\",\"depType\":2,\"id\":11,\"gmtCreate\":1550627695994,\"gmtModified\":1550627695994,\"isDeleted\":0,\"notes\":\"博大网信党组织\",\"creator\":\"6.vv\",\"editor\":\"1.张三\"},{\"name\":\"新航城公司党委\",\"idParent\":32,\"gradationCode\":\".2.8.1\",\"depType\":2,\"id\":33,\"gmtCreate\":1552377479433,\"gmtModified\":1552377479433,\"isDeleted\":0,\"notes\":\"新航城公司党委\",\"creator\":\"1.系统管理员\",\"editor\":\"1.系统管理员\"},{\"name\":\"博大第二支部\",\"idParent\":11,\"gradationCode\":\".2.3.1.2\",\"depType\":3,\"id\":25,\"gmtCreate\":1552293369872,\"gmtModified\":1552293369872,\"isDeleted\":0,\"notes\":\"大\",\"creator\":\"1.系统管理员\",\"editor\":\"1.系统管理员\"},{\"name\":\"第三支部\",\"idParent\":9,\"gradationCode\":\".2.3.4\",\"depType\":3,\"id\":18,\"gmtCreate\":1552269858083,\"gmtModified\":1552269858083,\"isDeleted\":0,\"notes\":\"第三支部\",\"creator\":\"admin.管理员\",\"editor\":\"admin.管理员\"},{\"name\":\"新航城第一党支部\",\"idParent\":33,\"gradationCode\":\".2.8.1.1\",\"depType\":3,\"id\":34,\"gmtCreate\":1552377492566,\"gmtModified\":1552377492566,\"isDeleted\":0,\"notes\":\"新航城第一党支部\",\"creator\":\"1.系统管理员\",\"editor\":\"1.系统管理员\"},{\"name\":\"亦庄党委1\",\"idParent\":3,\"gradationCode\":\".2.5\",\"depType\":2,\"id\":26,\"gmtCreate\":1552295163336,\"gmtModified\":1552295163336,\"isDeleted\":0,\"notes\":\"的\",\"creator\":\"23.许利利\",\"editor\":\"23.许利利\"},{\"name\":\"股份第一党支部\",\"idParent\":4,\"gradationCode\":\".2.1.4\",\"depType\":3,\"id\":19,\"gmtCreate\":1552270658578,\"gmtModified\":1552270658578,\"isDeleted\":0,\"notes\":\"的\",\"creator\":\"admin.管理员\",\"editor\":\"admin.管理员\"},{\"name\":\"测试党委\",\"idParent\":4,\"gradationCode\":\".2.1.5\",\"depType\":2,\"id\":21,\"gmtCreate\":1552291074559,\"gmtModified\":1552291074559,\"isDeleted\":0,\"notes\":\"的\",\"creator\":\"23.许利利\",\"editor\":\"23.许利利\"},{\"name\":\"经开第一党支部\",\"idParent\":7,\"gradationCode\":\".2.1.2.1\",\"depType\":3,\"id\":29,\"gmtCreate\":1552302842168,\"gmtModified\":1552302842168,\"isDeleted\":0,\"notes\":\"嗯嗯\",\"creator\":\"15.zzs\",\"editor\":\"15.zzs\"},{\"name\":\"第一支部\",\"idParent\":5,\"gradationCode\":\".2.1.1.2\",\"depType\":3,\"id\":16,\"gmtCreate\":1552269815393,\"gmtModified\":1552269815393,\"isDeleted\":0,\"notes\":\"大\",\"creator\":\"admin.管理员\",\"editor\":\"admin.管理员\"},{\"name\":\"博大党委\",\"idParent\":9,\"gradationCode\":\".2.3.5\",\"depType\":2,\"id\":22,\"gmtCreate\":1552291378580,\"gmtModified\":1552291378580,\"isDeleted\":0,\"notes\":\"大\",\"creator\":\"23.许利利\",\"editor\":\"23.许利利\"},{\"name\":\"测试第一支部\",\"idParent\":23,\"gradationCode\":\".2.4.1\",\"depType\":3,\"id\":30,\"gmtCreate\":1552370402191,\"gmtModified\":1552370402191,\"isDeleted\":0,\"notes\":\"测试第一支部\",\"creator\":\"1.系统管理员\",\"editor\":\"1.系统管理员\"},{\"name\":\"第二支部\",\"idParent\":9,\"gradationCode\":\".2.3.3\",\"depType\":3,\"id\":17,\"gmtCreate\":1552269830904,\"gmtModified\":1552269830904,\"isDeleted\":0,\"notes\":\"的\",\"creator\":\"admin.管理员\",\"editor\":\"admin.管理员\"},{\"name\":\"党委身份\",\"idParent\":5,\"gradationCode\":\".2.1.1.3\",\"depType\":2,\"id\":20,\"gmtCreate\":1552290925289,\"gmtModified\":1552290925289,\"isDeleted\":0,\"notes\":\"是\",\"creator\":\"23.许利利\",\"editor\":\"23.许利利\"},{\"name\":\"党委2\",\"idParent\":3,\"gradationCode\":\".2.6\",\"depType\":2,\"id\":27,\"gmtCreate\":1552295171454,\"gmtModified\":1552295171454,\"isDeleted\":0,\"notes\":\"的\",\"creator\":\"23.许利利\",\"editor\":\"23.许利利\"},{\"name\":\"经开党支部\",\"idParent\":5,\"gradationCode\":\".2.1.1.1\",\"depType\":3,\"id\":6,\"gmtCreate\":1548226749823,\"gmtModified\":1548226749823,\"isDeleted\":0,\"notes\":\"经开党支部\",\"creator\":\"1.姓名\",\"editor\":\"1.姓名\"},{\"name\":\"党委三\",\"idParent\":3,\"gradationCode\":\".2.7\",\"depType\":2,\"id\":28,\"gmtCreate\":1552295182209,\"gmtModified\":1552295182209,\"isDeleted\":0,\"notes\":\"大\",\"creator\":\"23.许利利\",\"editor\":\"23.许利利\"},{\"name\":\"经开股份\",\"idParent\":3,\"gradationCode\":\".2.1\",\"depType\":1,\"id\":4,\"gmtCreate\":1548226707183,\"gmtModified\":1548226707183,\"isDeleted\":0,\"notes\":\"经开股份\",\"creator\":\"1.姓名\",\"editor\":\"1.姓名\"},{\"name\":\"经开党委\",\"idParent\":4,\"gradationCode\":\".2.1.2\",\"depType\":2,\"id\":7,\"gmtCreate\":1548226768157,\"gmtModified\":1548226768157,\"isDeleted\":0,\"notes\":\"经开党委\",\"creator\":\"1.姓名\",\"editor\":\"1.姓名\"},{\"name\":\"亦庄党委\",\"idParent\":3,\"gradationCode\":\".2.2\",\"depType\":2,\"id\":8,\"gmtCreate\":1550123866292,\"gmtModified\":1550123866292,\"isDeleted\":0,\"notes\":\"亦庄党委\",\"creator\":\"1.张三\",\"editor\":\"1.张三\"},{\"name\":\"北京经开互联科技有限公司\",\"idParent\":4,\"gradationCode\":\".2.1.1\",\"depType\":1,\"id\":5,\"gmtCreate\":1548226722714,\"gmtModified\":1548226722714,\"isDeleted\":0,\"notes\":\"经开互联\",\"creator\":\"1.姓名\",\"editor\":\"1.姓名\"},{\"name\":\"亦庄控股\",\"gradationCode\":\".2\",\"depType\":1,\"id\":3,\"gmtCreate\":1548226434938,\"gmtModified\":1548226434938,\"isDeleted\":0,\"notes\":\"亦庄控股\",\"creator\":\"1.姓名\",\"editor\":\"1.姓名\"},{\"name\":\"博大网信\",\"idParent\":3,\"gradationCode\":\".2.3\",\"depType\":1,\"id\":9,\"gmtCreate\":1550627647238,\"gmtModified\":1550627647238,\"isDeleted\":0,\"notes\":\"博大网信\",\"creator\":\"6.vv\",\"editor\":\"6.vv\"}]";
        List<DepartmentVO> parse = GsonUtils.parse(content, new TypeToken<List<DepartmentVO>>(){});
        System.out.println(parse.contains(null));
    }*/
}
