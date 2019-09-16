package com.unity.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.unity.rbac.entity.generated.mResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@Data
@TableName(value = "rbac_resource")
@EqualsAndHashCode(callSuper=false)
public class Resource extends mResource{

    /*
     * 重写equals方法
     */
    /*public boolean equals(Resource resource){
        if(resource == null || resource.getId() == null){
            return false;
        }
        return  this.getId().equals(resource.getId());
    }*/

}

