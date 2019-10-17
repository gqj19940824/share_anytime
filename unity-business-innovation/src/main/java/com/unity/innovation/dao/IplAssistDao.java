
package com.unity.innovation.dao;


import com.unity.common.base.BaseDao;
import com.unity.innovation.entity.generated.IplAssist;

import java.util.List;
import java.util.Map;

/**
 * 创新发布清单-协同事项
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
public interface IplAssistDao  extends BaseDao<IplAssist>{

    /**
     *
     *
     * @param  
     * @return 
     * @author qinhuan
     * @since 2019-10-14 13:58  
     */
    List<Map<String, Object>> assistDarbList(Map<String, Object> paramMap);
    List<Map<String, Object>> assistEsbList(Map<String, Object> paramMap);
    List<Map<String, Object>> assistSatbList(Map<String, Object> paramMap);
    List<Map<String, Object>> assistOdList(Map<String, Object> paramMap);
}

