package com.unity.rbac.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 用户
 *
 * @author creator
 * 生成时间 2018-12-24 19:44:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class mUser extends BaseEntity {

    /**
     * 登录名
     **/
    @TableField("login_name")
    private String loginName;

    /**
     * 密码
     **/
    @TableField("pwd")
    private String pwd;

    /**
     * 手机号
     **/
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     **/
    @TableField("email")
    private String email;

    /**
     * 昵称
     **/
    @TableField("nick_name")
    private String nickName;

    /**
     * 姓名
     **/
    @TableField("name")
    private String name;

    /**
     * 职位
     **/
    @TableField("position")
    private String position;

    /**
     * 头像
     **/
    @TableField("head_pic")
    private String headPic;

    /**
     * 微信openId
     **/
    @TableField("wx_open_id")
    private String wxOpenId;

    /**
     * 小程序openId
     **/
    @TableField("wxx_open_id")
    private String wxxOpenId;

    /**
     * 最后登录Ip
     **/
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 最后登录时间
     **/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @TableField("gmt_login_last")
    private java.util.Date gmtLoginLast;


    /**
     * 最后登录平台:status:1 web,2 android,3 ios,4 微信,5 小程序
     **/
    @TableField("last_login_platform")
    private Integer lastLoginPlatform;

    /**
     * 用户所属公司
     */
    @TableField(value = "id_rbac_department")
    private Long idRbacDepartment;

    /**
     * 是否锁定:flag:1 是,0 否
     */
    @TableField("is_lock")
    private Integer isLock;

    /**
     * 用户类型:
     */
    @TableField("user_type")
    private Integer userType;

    /**
     * 账号来源 1：系统，2：OA
     **/
    @TableField("source")
    private Integer source;

    /**
     * 是否接受短信
     */
    @TableField("receive_sms")
    private Integer receiveSms;

    /**
     * 是否接受系统消息
     */
    @TableField("receive_sys_msg")
    private Integer receiveSysMsg;

    public mUser() {
    }
}




