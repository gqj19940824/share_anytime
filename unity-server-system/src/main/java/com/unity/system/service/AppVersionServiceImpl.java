
package com.unity.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.system.dao.AppVersionDao;
import com.unity.system.entity.AppVersion;
import org.springframework.stereotype.Service;

/**
 * ClassName: AppVersionService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2018-12-21 13:31:11
 *
 * @author creator
 * @since JDK 1.8
 */
@Service
public class AppVersionServiceImpl extends BaseServiceImpl<AppVersionDao, AppVersion> {

    /**
     * 根据类型查询最新的一条版本信息
     *
     * @param systemType 查询类型
     * @return 查询版本信息
     * @author zhangxiaogang
     * @since 2019/3/20 19:11
     */
    public AppVersion findAppVersionBySystemType(Integer systemType) {
        QueryWrapper<AppVersion> qw = new QueryWrapper<>();
        qw.lambda().eq(AppVersion::getSystemType, systemType)
                .orderByDesc(AppVersion::getId);
        return this.baseMapper.selectOne(qw);
    }


}
