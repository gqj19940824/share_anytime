package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 创新发布活动-管理-主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "newInstance")
@EqualsAndHashCode(callSuper=false)
public class IpaManageMain extends BaseEntity{
        
        /**
        * 标题
        **/
        @CommentTarget("单位")
        @TableField("id_rbac_department")
        private Long idRbacDepartment ;

        /**
        * 标题
        **/
        @CommentTarget("单位名称")
        @TableField(exist = false)
        private String idRbacDepartmentName ;

        /**
        * 标题
        **/
        @CommentTarget("标题")
        @TableField("title")
        private String title ;

        
        
        /**
        * 状态
        **/
        @CommentTarget("状态")
        @TableField("status")
        private Integer status ;



        /**
        * 发布效果
        **/
        @CommentTarget("发布效果")
        @TableField("publish_result")
        private String publishResult ;

        /**
         * 发稿媒体
         **/
        @CommentTarget("发稿媒体")
        @TableField("publish_media")
        private String publishMedia ;

        /**
        * 与会企业信息一次包id
        **/
        @CommentTarget("与会企业信息一次包id")
        @TableField(exist = false)
        private List<Long> idPmpList ;

        /**
        * 级别
        **/
        @CommentTarget("级别")
        @TableField("level")
        private Integer level ;


        /**
        * 工作动态一次包id
        **/
        @CommentTarget("工作动态一次包id")
        @TableField(exist = false)
        private List<Long> idDwspList ;



        /**
        * 创新发布清单一次包
        **/
        @CommentTarget("创新发布清单一次包id")
        @TableField(exist = false)
        private List<Long> idIplpList ;

        /**
        * 与会企业信息一次包列表
        **/
        @CommentTarget("与会企业信息一次包列表")
        @TableField(exist = false)
        private List<Map<String, Object>> pmpList ;



        /**
        * 工作动态一次包列表
        **/
        @CommentTarget("工作动态一次包列表")
        @TableField(exist = false)
        private List<Map<String, Object>> dwspList ;



        /**
        * 创新发布清单一次包列表
        **/
        @CommentTarget("创新发布清单一次包列表")
        @TableField(exist = false)
        private List<Map<String, Object>> iplpList ;

        /**
         * 创建时间（查询使用，YYYY-MM）
         *
         * @param
         * @return
         * @author qinhuan
         * @since 2019/10/18 2:05 下午
         */
        @TableField(exist = false)
        private String createDate;
}




