package com.unity.resource.enums;

import lombok.AllArgsConstructor;

/**
 * 类型
 * @author zhang
 * 生成时间 2019-09-03 10:12:42
 */
@AllArgsConstructor
public enum OptTypeEnum {

    JAR_TYPE(10, "JAR上传"),
        SH_TYPE(20, "执行脚本"),
        HTML_TYPE(30, "HTML脚本"),
    ;
    
    
    public static OptTypeEnum of(Integer id) {
        if (id.equals(JAR_TYPE.getId())) {
           return JAR_TYPE;
        }
        if (id.equals(SH_TYPE.getId())) {
           return SH_TYPE;
        }
        if (id.equals(HTML_TYPE.getId())) {
           return SH_TYPE;
        }
    ;
       return null;
    }
    
    /**
     * 判断值是否在枚举中存在
     * @param id
     * @return
     */
    public static boolean exist(int id){
        boolean flag = false;
        for (OptTypeEnum e: OptTypeEnum.values()){
            if(e.getId()==id){
                flag = true;
                break;
            }
        }
        return flag;
    }
    
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



