package com.unity.common.client.vo;


import com.unity.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织机构
 * @author creator
 * 生成时间 2018-12-24 19:43:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "newInstance")
public class DepartmentVO extends BaseEntity{



        /**
        * 机构名称
        **/
        private String name ;



        /**
        * 父Id
        **/
        private Long idParent ;



        /**
        * 树层次
        **/
        private Integer level ;



        /**
        * 级次编码
        **/
        private String gradationCode ;

        /**
         * 类型:status:1 公司 company,2 党委 partyCommittee,3 支部 branch
         */
        private Integer depType;

}




