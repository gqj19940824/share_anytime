package com.unity.innovation.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;
import lombok.*;

import java.util.List;

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
        * 与会企业信息一次包id
        **/
        @CommentTarget("与会企业信息一次包id")
        @TableField(exist = false)
        private List<Long> idPmpList ;



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

        

}




