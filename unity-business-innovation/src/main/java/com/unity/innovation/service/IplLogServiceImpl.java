
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.ReflectionUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.dao.IplLogDao;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.IplPdMain;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private RedisSubscribeServiceImpl redisSubscribeService;

    @Autowired
    private IplDarbMainServiceImpl iplDarbMainService;

    @Autowired
    private IplAssistServiceImpl iplAssistService;

    @Autowired
    private IplLogServiceImpl iplLogService;

    @Autowired
    private IplEsbMainServiceImpl iplEsbMainService;

    @Autowired
    private IplPdMainServiceImpl iplPdMainService;

    @Autowired
    private IplSatbMainServiceImpl iplSatbMainService;
    @Autowired
    private IplOdMainServiceImpl iplOdMainService;
    /**
     * 修改状态、插入日志
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 19:05
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByDuty(Long idRbacDepartmentDuty, Long idIplMain, IplLog iplLog) {
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, idIplMain).eq(IplAssist::getIdRbacDepartmentAssist, iplLog.getIdRbacDepartmentAssist());
        IplAssist iplAssist = iplAssistService.getOne(qw);

        if(iplAssist == null){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName()).build();
        }
        // 修改状态、插入日志
        iplAssist.setDealStatus(iplLog.getDealStatus());
        iplAssistService.updateById(iplAssist);

        // 主责单位改变协同单位的状态需要向协同单位和主责单位的操作日志中同时插入一条记录 TODO processInfo修改
        IplLog assistDeptLog = IplLog.newInstance().dealStatus(iplAssist.getDealStatus()).idRbacDepartmentDuty(iplAssist.getIdRbacDepartmentDuty()).idIplMain(iplAssist.getIdIplMain()).processInfo("主责单位改变状态").idRbacDepartmentAssist(iplAssist.getIdRbacDepartmentAssist()).build();
        IplLog dutyDeptLog = IplLog.newInstance().dealStatus(iplAssist.getDealStatus()).idRbacDepartmentDuty(iplAssist.getIdRbacDepartmentDuty()).idIplMain(iplAssist.getIdIplMain()).processInfo("主责单位改变状态").idRbacDepartmentAssist(0L).build();

        iplLogService.save(assistDeptLog);
        iplLogService.save(dutyDeptLog);

        // 更新redis
        redisSubscribeService.saveSubscribeInfo(idIplMain + "-" + iplAssist.getIdRbacDepartmentAssist(), ListTypeConstants.UPDATE_OVER_TIME, idRbacDepartmentDuty);

        // TODO 修改主责单位超时redis和数据状态
    }


    /**
     * 实时更新接口
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 16:16
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void updateStatus(T entity, IplLog iplLog){
        // 主责单位id
        Long idRbacDepartmentDuty = (Long) ReflectionUtils.getFieldValue(entity, "idRbacDepartmentDuty");
        Long id = (Long) ReflectionUtils.getFieldValue(entity, "id");
        Integer dealStatus = iplLog.getDealStatus();

        Customer customer = LoginContextHolder.getRequestAttributes();
        Long customerIdRbacDepartment = customer.getIdRbacDepartment();
        // 主责单位
        if (idRbacDepartmentDuty.equals(customerIdRbacDepartment)){
            // 主责单位把主表完结
            if (IplStatusEnum.DONE.getId().equals(dealStatus)){
                // 休改主表状态 并休改协同表状态，各插入一个日志、各清除redis超时
                dutyDone(entity, idRbacDepartmentDuty, id, dealStatus);
            }else {
                // 保存日志
                iplLog.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
                iplLog.setIdRbacDepartmentAssist(0L);
                iplLogService.save(iplLog);

                // 将状数据态置为"处理中"，将超时状态置为"进展正常"
                updateStatus(entity);

                // 更新redis的超时
                redisSubscribeService.saveSubscribeInfo(id+"-0", ListTypeConstants.UPDATE_OVER_TIME, idRbacDepartmentDuty);
            }
        // 协同单位
        }else {
            if (IplStatusEnum.DONE.getId().equals(dealStatus)){
                // 如果协同单位关闭了协同则修改协同状态
                LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
                qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, iplLog.getIdIplMain()).eq(IplAssist::getIdRbacDepartmentAssist, customerIdRbacDepartment);
                iplAssistService.update(IplAssist.newInstance().dealStatus(IplStatusEnum.DONE.getId()).build(), qw);
                // 删除redis超时
                redisSubscribeService.removeRecordInfo(id + "-" + customerIdRbacDepartment, idRbacDepartmentDuty);
            }else {
                // 更新redis超时
                redisSubscribeService.saveSubscribeInfo(id + "-" + customerIdRbacDepartment, ListTypeConstants.UPDATE_OVER_TIME, idRbacDepartmentDuty);
            }

            // 记录日志
            iplLog.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
            iplLog.setIdRbacDepartmentAssist(customerIdRbacDepartment);
            iplLogService.save(iplLog);
        }
    }

    /**
     * 将状数据态置为"处理中"，将超时状态置为"进展正常"
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-11 14:34
     */
    public <T> void updateStatus(T entity) {
        Integer status = (Integer) ReflectionUtils.getFieldValue(entity, "status");
        Integer processStatus = (Integer) ReflectionUtils.getFieldValue(entity, "processStatus");
        boolean flag = false;
        if (IplStatusEnum.UNDEAL.getId().equals(status)){
            ReflectionUtils.setFieldValue(entity, "status", IplStatusEnum.DEALING.getId());
            flag = true;
        }
        if (!ProcessStatusEnum.NORMAL.getId().equals(processStatus)){
            ReflectionUtils.setFieldValue(entity, "processStatus", ProcessStatusEnum.NORMAL.getId());
            flag = true;
        }

        if (flag){
            updateMain(entity);
        }
    }

    /**
     * 休改主表状态 并休改协同表状态，各插入一个日志、各清除redis超时
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 17:10
     */
    private <T> void dutyDone(T entity, Long idRbacDepartmentDuty, Long id, Integer dealStatus) {
        // 更新主表状态、删除主表的redis超时设置
        ReflectionUtils.setFieldValue(entity, "status", IplStatusEnum.DONE.getId());
        updateMain(entity);
        // 删除主表的redis超时设置
        redisSubscribeService.removeRecordInfo(id+"-0", idRbacDepartmentDuty);

        List<IplLog> iplLogs = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        // TODO 过滤已关闭的协同单位
        List<IplAssist> assists = iplAssistService.getAssists(idRbacDepartmentDuty, id);
        // 更新协同单位状态、删除协同单位的redis超时设置
        if (CollectionUtils.isNotEmpty(assists)){
            assists.forEach(e->{
                builder.append(e.getNameRbacDepartmentAssist()).append("、");
                e.setDealStatus(dealStatus);
                IplLog iplLogAssit = IplLog.newInstance().dealStatus(dealStatus).idRbacDepartmentDuty(idRbacDepartmentDuty).idRbacDepartmentAssist(e.getId()).idIplMain(id).processInfo("主责单位关闭协同邀请").build();
                iplLogs.add(iplLogAssit);

                // 删除协同单位的redis超时设置
                redisSubscribeService.removeRecordInfo(id+"-"+e.getIdRbacDepartmentAssist(), idRbacDepartmentDuty);
            });

            builder.deleteCharAt(builder.length()-1);

            // 批量更新协同单位状态
            iplAssistService.updateBatchById(assists);
        }

        // 主责记录日志
        IplLog iplLogDuty = IplLog.newInstance().dealStatus(dealStatus).idRbacDepartmentDuty(idRbacDepartmentDuty)
                .idRbacDepartmentAssist(0L).idIplMain(id).processInfo(String.format("关闭%s协同邀请", builder.toString())).build();
        iplLogs.add(iplLogDuty);
        iplLogService.saveBatch(iplLogs);
    }

    /**
     * 更新主表状态
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 16:28
     */
    public <T> void updateMain(T entity) {
        // 更新主表状态
        if (entity instanceof IplDarbMain){
            IplDarbMain iplDarbMain = (IplDarbMain)entity;
            iplDarbMainService.updateById(iplDarbMain);
        }else if (entity instanceof IplEsbMain){
            IplEsbMain iplEsbMain = (IplEsbMain) entity;
            iplEsbMainService.updateById(iplEsbMain);
        }else if (entity instanceof IplPdMain){
            IplPdMain iplPdMain = (IplPdMain)entity;
            iplPdMainService.updateById(iplPdMain);
        }else if (entity instanceof IplSatbMain){
            IplSatbMain iplSatbMain = (IplSatbMain) entity;
            iplSatbMainService.updateById(iplSatbMain);
        }else if (entity instanceof IplOdMain){
            IplOdMain iplOdMain = (IplOdMain) entity;
            iplOdMainService.updateById(iplOdMain);
        }// TODO 完善每个模块的更新
    }

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
