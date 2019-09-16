package com.unity.resource.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 文件资源
 * @author creator
 * 生成时间 2019-01-25 13:46:28
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mFileResource extends BaseEntity{


        
        /**
        * 编号_资源类型
        **/
        @TableField("id_re_resource_type")
        private Long idReResourceType ;
        
        
        
        /**
        * 资源路径
        **/
        @TableField("url")
        private String url ;
        
        
        
        /**
        * 存储组名
        **/
        @TableField("file_group")
        private String fileGroup ;
        
        
        
        /**
        * 资源后缀
        **/
        @TableField("ext")
        private String ext ;
        
        
        
        /**
        * 类型:status:1 公司 company,2 党委 partyCommittee,3 支部 branch
        **/
        @TableField("dep_type")
        private Integer depType ;
        
        
        
        /**
        * 单位（kb）
        **/
        @TableField("file_size")
        private Long fileSize ;
        
        
        
        /**
        * 简介
        **/
        @TableField("remark")
        private String remark ;
        
        
        
        /**
        * md5
        **/
        @TableField("md5")
        private String md5 ;
        
        

}




