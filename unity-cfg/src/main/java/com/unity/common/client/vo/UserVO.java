package com.unity.common.client.vo;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 用户
 *
 * @author creator
 * 生成时间 2018-12-24 19:44:04
 */
@Data
public class UserVO {

    private Long id;
    /**
     * 登录名
     **/
    private String loginName;

    /**
     * 密码
     **/
    private String pwd;

    /**
     * 手机号
     **/
    private String phone;

    /**
     * 邮箱
     **/
    private String email;

    /**
     * 昵称
     **/
    private String nickName;

    /**
     * 姓名
     **/
    private String name;

    /**
     * 职位
     **/
    private String position;

    /**
     * 头像
     **/
    private String headPic;

    /**
     * 微信openId
     **/
    private String wxOpenId;

    /**
     * 小程序openId
     **/
    private String wxxOpenId;

    /**
     * 最后登录Ip
     **/
    private String lastLoginIp;

    /**
     * 最后登录时间
     **/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private java.util.Date gmtLoginLast;


    /**
     * 最后登录平台:status:1 web,2 android,3 ios,4 微信,5 小程序
     **/
    private Integer lastLoginPlatform;

    /**
     * 用户所属公司
     */
    private Long idRbacDepartment;

    /**
     * 是否锁定:flag:1 是,0 否
     */
    private Integer isLock;

    /**
     * 用户类型:
     */
    private Integer userType;

    /**
     * 账号来源 1：系统，2：OA
     **/
    private Integer source;

    /**
     * 是否接受短信
     */
    private Integer receiveSms;

    /**
     * 是否接受系统消息
     */
    private Integer receiveSysMsg;

    public UserVO() {
    }
}




