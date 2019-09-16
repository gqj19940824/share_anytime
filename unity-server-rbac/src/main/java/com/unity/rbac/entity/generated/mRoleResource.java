package com.unity.rbac.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色资源
 * @author creator
 * 生成时间 2018-12-24 19:44:00
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class mRoleResource extends BaseEntity{

        /**
        * 编号_资源
        **/
        @TableField("id_rbac_resource")
        private Long idRbacResource ;

        /**
        * 编号_角色
        **/
        @TableField("id_rbac_role")
        private Long idRbacRole ;

    public mRoleResource(){}
}




