
package com.unity.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.unity.common.base.BaseDao;
import com.unity.system.entity.Dictionary;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据字典
 * @author creator
 * 生成时间 2018-12-21 13:25:58
 */
public interface DictionaryDao  extends BaseDao<Dictionary> {
	/**
	 * 查询所有的业务性质的字典
	 * @return List<Dictionary>
	 * @author  zhaozesheng
	 * @since   2019/1/8 11:10
	 */
	@Select("select * from sys_dictionary where name = 'business_nature'")
    List<Dictionary> getDictionaryByName();
}

