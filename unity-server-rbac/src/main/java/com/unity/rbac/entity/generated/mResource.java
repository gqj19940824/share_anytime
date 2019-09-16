package com.unity.rbac.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源
 * @author creator
 * 生成时间 2018-12-24 19:44:03
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mResource extends BaseEntity{


        
        /**
        * 资源名称
        **/
        @TableField("name")
        private String name ;
        
        
        
        /**
        * 资源地址
        **/
        @TableField("resource_url")
        private String resourceUrl ;
        
        
        
        /**
        * 资源类型:status:10 层级菜单 levelMenu,20 视图菜单 viewMenu,30 按钮 button,40 接口 api
        **/
        @TableField("resource_type")
        private Integer resourceType ;
        
        
        
       /**
        * 资源图标
        **/
        @TableField("resource_pic")
        private String resourcePic ;
        
        
        
        /**
        * 父Id
        **/
        @TableField("id_parent")
        private Long idParent ;
        
        
        
        /**
        * 树层次
        **/
        @TableField("i_level")
        private Integer level ;
        
        
        
        /**
        * 级次编码
        **/
        @TableField("gradation_code")
        private String gradationCode ;

        /**
         * 路由地址
         **/
        @TableField("component")
        private String component ;
        
        
        
        /*
        * 平台:status:1 web,2 android,3 ios,4 微信,5 小程序
        @TableField("platform")
        private Integer platform ;
        **/
        
        
        /*
        * 功能列表
        @TableField("function_list")
        private String functionList ;
         **/
        

    public mResource(){}
}




