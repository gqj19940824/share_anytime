package com.unity.common.client.vo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Builder(builderMethodName = "newInstance")
public class UcsUser {
  /**
   * 登录名
   **/
  private String loginName ;

  /**
   * 原密码
   **/
  private String oldPwd ;

  /**
   * 密码
   **/
  private String pwd ;


  /**
   * 来源类型:status:10 OA系统 oa,20 资产系统 assets,30 安全系统 safe,30 工程系统 project
   **/
  private Integer source ;

  /**
   * 组织名称
   */
  private String departName;

  /**
   * 名称
   */
  private String name;

  /**
   * 用户中心id
   */
  private Long idUcsUser;
}

