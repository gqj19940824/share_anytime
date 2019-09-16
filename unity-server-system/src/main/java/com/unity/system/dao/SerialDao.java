
package com.unity.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.unity.common.base.BaseDao;
import com.unity.system.entity.Serial;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.util.Map;

/**
 * 流水号
 * @author creator
 * 生成时间 2018-12-21 13:25:59
 */
public interface SerialDao  extends BaseDao<Serial> {
    @Select({ "call generate_serial_val(#{serialPrefix,mode=IN,jdbcType=VARCHAR},"
            + "#{serialSuffixes,mode=IN,jdbcType=VARCHAR},"
            + "#{serialType,mode=IN,jdbcType=INTEGER},"
            + "#{servialNum,mode=OUT,jdbcType=VARCHAR})" })
    @Options(statementType= StatementType.CALLABLE)
    String  callGenerateSyserialVal(Map paraMap);
}

