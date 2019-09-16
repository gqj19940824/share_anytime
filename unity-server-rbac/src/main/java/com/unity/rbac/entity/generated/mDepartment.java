package com.unity.rbac.entity.generated;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;

/**
 * 组织机构
 * @author creator
 * 生成时间 2018-12-24 19:43:58
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mDepartment extends BaseEntity{


        
        /**
        * 机构名称
        **/
        @TableField("name")
        private String name ;
        
        
        
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
         * 类型:status:1 公司 company,2 党委 partyCommittee,3 支部 branch
         */
        @TableField("dep_type")
        private Integer depType;

        /**
         *注册地址
         **/
        @TableField("address")
        private String address ;



        /**
         *企业生产经营范围
         **/
        @TableField("business_scope")
        private String businessScope ;



        /**
         *从业人数
         **/
        @TableField("employee_num")
        private Long employeeNum ;



        /**
         *主管部门
         **/
        @TableField("dept")
        private String dept ;



        /**
         *企业负责人
         **/
        @TableField("dept_director")
        private String deptDirector ;



        /**
         *企业负责人联系方式
         **/
        @TableField("dept_director_contact")
        private String deptDirectorContact ;



        /**
         *安全负责人
         **/
        @TableField("safe_director")
        private String safeDirector ;



        /**
         *安全负责人联系方式
         **/
        @TableField("safe_director_contact")
        private String safeDirectorContact ;

    public mDepartment(){}
}




