package com.unity.innovation.entity.generated;


import com.unity.common.base.CommentTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;


/**
 * 系统配置
 * @author zhang
 * 生成时间 2019-09-17 14:53:55
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mSysCfg extends BaseEntity{


        
        /**
        * 类型 1：工作类别 2：关键字 3：行业类别 4：需求类型 5：需求名目
        **/
        @CommentTarget("模块类别")
        @TableField("cfg_type")
        private Integer cfgType ;
        
        
        
        /**
        * 值
        **/
        @CommentTarget("类别名称")
        @TableField("cfg_val")
        private String cfgVal ;


        /**
         * 启用状态 0 未启用  已启用
         **/
        @CommentTarget("启用状态")
        @TableField("use_status")
        private Integer useStatus ;
        
        

}




