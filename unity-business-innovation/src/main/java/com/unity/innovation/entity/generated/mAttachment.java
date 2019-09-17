package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author qinhuanhuan
 * 生成时间 2019-06-20 16:04:14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mAttachment extends BaseEntity {


        
        /**
        * 
        **/
        @TableField("attachment_code")
        private String attachmentCode ;
        
        
        
        /**
        * 
        **/
        @TableField("url")
        private String url ;
        
        
        
        /**
        * 
        **/
        @TableField("size")
        private String size ;

        /**
         *
         **/
        @TableField("size_long")
        private String sizeLong ;

        /**
         *
         **/
        @TableField("i_status")
        private String status ;

        /**
         *
         **/
        @TableField("i_type")
        private String type ;
        
        /**
        * 
        **/
        @TableField("name")
        private String name ;
        
        

}




