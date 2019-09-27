package com.unity.innovation.entity.generated;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import com.baomidou.mybatisplus.annotation.TableField;

import com.unity.common.base.BaseEntity;
import com.unity.common.base.CommentTarget;

/**
 * 创新发布清单-纪检组-主表
 * @author zhang
 * 生成时间 2019-09-23 15:34:08
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mIplSupervisionMain extends BaseEntity{


        

        /**
        * 类型
        **/
        @CommentTarget("类型")
        @TableField("category")
        private Integer category ;
        
        
        
        /**
        * 描述
        **/
        @CommentTarget("描述")
        @TableField("description")
        private String description ;
        
        

}




