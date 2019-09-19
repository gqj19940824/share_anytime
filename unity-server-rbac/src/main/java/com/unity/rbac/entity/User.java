package com.unity.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.common.util.annotation.ExcelField;
import com.unity.rbac.entity.generated.mUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@TableName(value = "rbac_user")
@EqualsAndHashCode(callSuper=false)
public class User extends mUser{

  @TableField(exist=false)
  private String oldPwd;

  /**
   * 公司名称
   **/
  @TableField(exist=false)
  private String department;


  /**
   * 角色
   */
  @TableField(exist=false)
  private String roleIds;

  /**
   * 角色id
   */
  @TableField(exist=false)
  private Long roleId;

  /**
   * 操作终端
   */
  @TableField(exist=false)
  private Integer os;

  /**
   * 用户拥有的角色名称（多个以逗号拼接）
   */
  @TableField(exist=false)
  private String groupConcatRoleName;

  /**
   * 是否超管
   */
  @TableField(exist=false)
  private Integer superAdmin;

  public User(){}
}

