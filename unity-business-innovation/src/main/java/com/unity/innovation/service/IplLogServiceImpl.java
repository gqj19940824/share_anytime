
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.dao.IplLogDao;

/**
 * ClassName: IplLogService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:36
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplLogServiceImpl extends BaseServiceImpl<IplLogDao, IplLog> {

    /**
     * 获取最近一条日志的状态
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-09-25 20:14
     */
    public Integer getLastDealStatus(Long idIplMain, Long idRbacDepartmentDuty) {
        LambdaQueryWrapper<IplLog> qw = new LambdaQueryWrapper();
        qw.eq(IplLog::getIdIplMain, idIplMain)
                .eq(IplLog::getIdRbacDepartmentDuty, idRbacDepartmentDuty)
                .orderByDesc(IplLog::getGmtCreate);
        IplLog last = getOne(qw, false);
        // 处理中
        Integer dealStatus = 2;
        if (last != null) {
            dealStatus = last.getDealStatus();
        }
        return dealStatus;
    }
}
