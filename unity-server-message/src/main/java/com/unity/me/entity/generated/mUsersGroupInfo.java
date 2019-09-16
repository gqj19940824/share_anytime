package com.unity.me.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 人员分组关系表
 * @author creator
 * 生成时间 2019-02-12 12:47:36
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mUsersGroupInfo extends BaseEntity{


        
        /**
        * 编号_分组信息
        **/
        @TableField("id_me_group_info")
        private Long idMeGroupInfo ;
        
        
        
        /**
        * 人员
        **/
        @TableField("rbac_user_id")
        private Long rbacUserId ;
        
        

}




