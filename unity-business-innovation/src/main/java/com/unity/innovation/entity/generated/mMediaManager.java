package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 媒体管理表
 * @author zhang
 * 生成时间 2019-10-28 13:41:56
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mMediaManager extends BaseEntity{


        
        /**
        * 媒体类别
        **/
        @CommentTarget("媒体类别")
        @TableField("media_type")
        private Long mediaType ;
        
        
        
        /**
        * 媒体名称
        **/
        @CommentTarget("媒体名称")
        @TableField("media_name")
        private String mediaName ;
        
        
        
        /**
        * 联系人
        **/
        @CommentTarget("联系人")
        @TableField("contact_person")
        private String contactPerson ;
        
        
        
        /**
        * 联系方式
        **/
        @CommentTarget("联系方式")
        @TableField("contact_way")
        private String contactWay ;
        
        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;
        
        

}




