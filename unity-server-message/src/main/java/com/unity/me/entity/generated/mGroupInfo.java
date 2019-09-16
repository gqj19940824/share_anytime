package com.unity.me.entity.generated;


import com.baomidou.mybatisplus.annotation.TableField;
import com.unity.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 分组信息
 * @author creator
 * 生成时间 2019-02-12 12:47:34
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class mGroupInfo extends BaseEntity{


        
        /**
        * 组名
        **/
        @TableField("group_name")
        private String groupName ;
        
        

}




