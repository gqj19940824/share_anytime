
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.dao.IplLogDao;
import com.unity.innovation.entity.generated.IplLog;
import org.springframework.stereotype.Service;

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

    /**
     * 功能描述 保存日志
     * @param idIplMain   主表id
     * @param status      状态 2 处理中 3 处理完成
     * @param idDuty      主责单位id
     * @param idAssist    协同单位id
     * @param processInfo 记录信息
     * @author gengzhiqiang
     * @date 2019/9/27 17:12
     */
    public void saveLog(Long idIplMain, Integer status, Long idDuty, Long idAssist, String processInfo) {
        IplLog dutyLog = IplLog.newInstance()
                .idIplMain(idIplMain)
                .dealStatus(status)
                .idRbacDepartmentDuty(idDuty)
                .idRbacDepartmentAssist(idAssist)
                .processInfo(processInfo)
                .build();
        save(dutyLog);
    }


}
