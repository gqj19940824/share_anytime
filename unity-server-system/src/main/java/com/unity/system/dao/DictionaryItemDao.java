
package com.unity.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.unity.common.base.BaseDao;
import com.unity.system.entity.DictionaryItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 数据字典项
 * @author creator
 * 生成时间 2018-12-21 13:25:58
 */
public interface DictionaryItemDao  extends BaseDao<DictionaryItem> {
    @Update({"update sys_dictionary_item set is_deleted ='1' where code like  concat(#{code},'%')"})
    int removeDictionaryItemBycode(String code);
    
    /**
     * 根据字典的ID查询字典项
     * @param
     * @return
     * @author  zhaozesheng
     * @since   2019/1/8 11:18
     */
    @Select("select * from sys_dictionary_item where id_sys_dictionary = #{id}")
   List<DictionaryItem> getDictionaryItemByDicId(@Param("id") Long id);
}

