package com.unity.configuration;

import lombok.*;
import org.springframework.util.StringUtils;

/**
 * 系统token综合返回类
 */
@Data
@NoArgsConstructor
public class SystemResponse<T extends Object> {
    private Integer code;
    private T body;
    private String message;

    public void success() {
        code = FormalErrorCode.SUCCESS.getValue();
        message = FormalErrorCode.SUCCESS.getName();
    }

    public SystemResponse<T> success(T body) {
        success();
        this.body = body;
        return this;
    }


    /**
     * 全局接口Token异常
     * Created by Jung on 2017/7/27.
     */
    @AllArgsConstructor
    public enum FormalErrorCode {
        SUCCESS(0,"操作成功"),
        //操作错误
        ILLEGAL_OPERATION(-1000,"操作错误"),
        //用户名或密码为空
        USERNAME_OR_PASSWORD_EMPTY(-1001,"用户名或密码为空"),
        //用户名或密码错误
        USERNAME_OR_PASSWORD_ERROR(-1002,"用户名或密码错误"),
        //修改的数据和原始数据相同
        MODIFY_DATA_EQUALS_ORIGIN(-1003,"修改的数据和原始数据相同"),
        //数据超长
        MODIFY_DATA_OVER_LENTTH(-1004,"数据超长"),
        //修改数据已存在
        MODIFY_DATA_ALREADY_EXISTS(-1005,"修改数据已存在"),
        //重复操作
        MODIFY_DATA_REPEAT_OPERATION(-1006,"重复操作"),
        //超时
        GET_DATA_OUT_TIME(-1007,"超时"),
        //原数据错误
        ORIGINAL_DATA_ERR(-1008,"原数据错误"),
        //登录数据错误
        LOGIN_DATA_ERR(-1009,"登录数据错误"),
        //登录数据状态错误
        LOGIN_DATA_SATUS_ERR(-1010,"登录数据状态错误"),
        //数据不存在
        DATA_DOES_NOT_EXIST(-1011,"数据不存在"),
        //权限校验不通过
        OPERATION_NO_AUTHORITY(-1012,"权限校验不通过"),
        //缺少必要参数
        LACK_REQUIRED_PARAM(-1013,"缺少必要参数"),
        //缺少必要参数
        NOT_FIND_SERVER(-1014,"服务不可用，请稍后再试"),
        //未知异常
        SERVER_ERROR(-9999,"系统正忙,请稍后在试......"),
        ;

        @Getter
        @Setter
        private Integer value;
        @Getter
        @Setter
        private String name;

        public static FormalErrorCode of(Integer value){
            switch (value){
                case 0:
                    return SUCCESS;
                case -1000:
                    return ILLEGAL_OPERATION;
                case -1001:
                    return USERNAME_OR_PASSWORD_EMPTY;
                case -1002:
                    return USERNAME_OR_PASSWORD_ERROR;
                case -1003:
                    return MODIFY_DATA_EQUALS_ORIGIN;
                case -1004:
                    return MODIFY_DATA_OVER_LENTTH;
                case -1005:
                    return MODIFY_DATA_ALREADY_EXISTS;
                case -1006:
                    return MODIFY_DATA_REPEAT_OPERATION;
                case -1007:
                    return GET_DATA_OUT_TIME;
                case -1008:
                    return ORIGINAL_DATA_ERR;
                case -1009:
                    return LOGIN_DATA_ERR;
                case -1010:
                    return LOGIN_DATA_SATUS_ERR;
                case -1011:
                    return DATA_DOES_NOT_EXIST;
                case -1012:
                    return OPERATION_NO_AUTHORITY;
                case -1013:
                    return LACK_REQUIRED_PARAM;
                case -9999:
                    return SERVER_ERROR;
                default:
                    return SERVER_ERROR;
            }
        }
    }

    public SystemResponse<T> formalSuccess() {
        code = FormalErrorCode.SUCCESS.getValue();
        message = FormalErrorCode.SUCCESS.getName();
        return this;
    }


    public SystemResponse<T> formalSuccess(String message) {
        code = FormalErrorCode.SUCCESS.getValue();
        this.message = message;
        return this;
    }

    public SystemResponse<T> formalSuccess(String message, T obj) {
        code = FormalErrorCode.SUCCESS.getValue();
        this.message = message;
        this.body = obj;
        return this;
    }


    /**
     * request的错误请求方法
     *
     * @param errorCode 错误指
     * @param message   错误的信息，如果没有信息，则用errorCode自带的名称
     * @return 创建好的对象
     * @author Jung
     * @since 2018-02-18 22:17:00
     */
    public SystemResponse<T> error(FormalErrorCode errorCode, String message) {
        code = errorCode.getValue();
        this.message = StringUtils.isEmpty(message) ? errorCode.getName() : message;
        return this;
    }

    /**
     * 只有错误信息的方法，调用的是重载方法
     *
     * @param errorCode 错误代码
     * @return 创建好的对象
     * @author Jung
     * @since 2018-02-18 22:17:55
     */
    public SystemResponse<T> error(FormalErrorCode errorCode) {
        return error(errorCode, null);
    }

}
