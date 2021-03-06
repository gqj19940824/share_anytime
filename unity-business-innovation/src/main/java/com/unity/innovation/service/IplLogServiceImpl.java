
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.ReflectionUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.controller.vo.PieVoByDoc;
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
import com.unity.innovation.enums.SysMessageDataSourceClassEnum;
import com.unity.innovation.enums.SysMessageFlowStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private SysMessageHelpService sysMessageHelpService;

    public List<PieVoByDoc.DataBean> satbDemandDone(Long start, Long end, Integer bizType){
        return baseMapper.satbDemandDone(start, end, bizType);
    }

    /**
     * 修改状态、插入日志
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 19:05
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void updateStatusByDuty(T entity, IplLog iplLog) {
        Long idRbacDepartmentDuty = (Long) ReflectionUtils.getFieldValue(entity, "idRbacDepartmentDuty");
        Integer bizType = (Integer) ReflectionUtils.getFieldValue(entity, "bizType");
        Long idIplMain = iplLog.getIdIplMain();
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getBizType, bizType).eq(IplAssist::getIdIplMain, idIplMain).eq(IplAssist::getIdRbacDepartmentAssist, iplLog.getIdRbacDepartmentAssist());
        IplAssist iplAssist = iplAssistService.getOne(qw);
        if (iplAssist == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName()).build();
        }
        Integer dealStatusNew = iplLog.getDealStatus();
        Integer dealStatusOld = iplAssist.getDealStatus();
        if (dealStatusOld.equals(dealStatusNew)){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message("数据状态已改变，请勿重复操作").build();
        }
        dealStatusOld = dealStatusOld == null ? IplStatusEnum.UNDEAL.getId() : dealStatusOld;
        //确认当前状态变更方向
        Integer flowStatus = null;

        if(dealStatusOld.equals(IplStatusEnum.UNDEAL.getId())
                && IplStatusEnum.DONE.getId().equals(dealStatusNew)){
            //待处理→处理完毕--清单协同处理--增加系统消息
            flowStatus = SysMessageFlowStatusEnum.SIX.getId();
        } else if (dealStatusOld.equals(IplStatusEnum.DEALING.getId())
                && IplStatusEnum.DONE.getId().equals(dealStatusNew)) {
            //处理中→处理完毕--清单协同处理--增加系统消息
            flowStatus = SysMessageFlowStatusEnum.SIX.getId();
        } else if (dealStatusOld.equals(IplStatusEnum.DONE.getId())
                && IplStatusEnum.DEALING.getId().equals(dealStatusNew)) {
            //处理完毕→处理中--清单协同处理--增加系统消息
            flowStatus = SysMessageFlowStatusEnum.SEVEN.getId();
        }
        // 修改状态、插入日志
        iplAssist.setDealStatus(dealStatusNew);
        iplAssist.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
        iplAssistService.updateById(iplAssist);

        // 主责单位改变协同单位的状态需要向协同单位和主责单位的操作日志中同时插入一条记录
        String switchType = IplStatusEnum.DEALING.getId().equals(dealStatusNew) ? "开启" : "关闭";
        IplLog assistDeptLog = IplLog.newInstance().dealStatus(dealStatusNew).bizType(bizType).idRbacDepartmentDuty(iplAssist.getIdRbacDepartmentDuty()).idIplMain(iplAssist.getIdIplMain()).processInfo(String.format("主责单位%s协同邀请", switchType)).idRbacDepartmentAssist(iplAssist.getIdRbacDepartmentAssist()).build();
        String processInfo = switchType + InnovationUtil.getDeptNameById(iplAssist.getIdRbacDepartmentAssist()) + "协同邀请";
        IplLog dutyDeptLog = IplLog.newInstance().dealStatus(dealStatusNew).bizType(bizType).idRbacDepartmentDuty(iplAssist.getIdRbacDepartmentDuty()).idIplMain(iplAssist.getIdIplMain()).processInfo(processInfo).idRbacDepartmentAssist(0L).build();

        iplLogService.save(assistDeptLog);
        iplLogService.save(dutyDeptLog);

        if (IplStatusEnum.DEALING.getId().equals(dealStatusNew)){ // 开启
            // 设置协同单位redis超时
            redisSubscribeService.saveSubscribeInfo(idIplMain + "-" + iplAssist.getIdRbacDepartmentAssist(), ListTypeConstants.UPDATE_OVER_TIME, idRbacDepartmentDuty, bizType);
        }else { // 关闭
            // 删除redis超时
            redisSubscribeService.removeRecordInfo(idIplMain + "-" + iplAssist.getIdRbacDepartmentAssist(), idRbacDepartmentDuty, bizType);
        }

        // 修改主责单位超时状态，重置redis超时
        ReflectionUtils.setFieldValue(entity, "processStatus", ProcessStatusEnum.NORMAL.getId());
        ReflectionUtils.setFieldValue(entity, "latestProcess", processInfo);
        updateMain(entity);
        redisSubscribeService.saveSubscribeInfo(idIplMain + "-0", ListTypeConstants.UPDATE_OVER_TIME, idRbacDepartmentDuty, bizType);
        //======处理中→处理完毕--清单协同处理--增加系统消息=======
        //======处理完毕→处理中--清单协同处理--增加系统消息=======
        if (flowStatus != null) {
            String enterpriseName = (String) ReflectionUtils.getFieldValue(entity, "enterpriseName");
            sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                    .sourceId(idIplMain)
                    .idRbacDepartment(idRbacDepartmentDuty)
                    .dataSourceClass(SysMessageDataSourceClassEnum.HELP.getId())
                    .flowStatus(flowStatus)
                    .title(enterpriseName)
                    .helpDepartmentIdList(Arrays.asList(iplAssist.getIdRbacDepartmentAssist()))
                    .bizType(bizType)
                    .build());
        }
    }

    /**
     * 校验完成情况是否超过融资/人才需求
     *
     * @param  idIplMain 主表id
     * @param  bizType 业务类型
     * @param  demandTotal 融资/人才需求
     * @param  type 1-成长目标投资的主责
     * @author qinhuan
     * @since 2019/11/15 5:38 下午
     */
    public void isTotalGeSum(Long idIplMain, Integer bizType, BigDecimal demandTotal,BigDecimal completeNum, Integer type){
        BigDecimal raisedTotal = getRaisedTotal(idIplMain, bizType);
        raisedTotal = raisedTotal.add(completeNum);
        //保留两位小数，四舍五入
        demandTotal = demandTotal.setScale(2,BigDecimal.ROUND_HALF_UP);
        raisedTotal = raisedTotal.setScale(2,BigDecimal.ROUND_HALF_UP);
        if (demandTotal.compareTo(raisedTotal) < 0){
            String errorMessage;
            switch (type){
                case 1:
                    errorMessage = "累计完成额度超出融资需求，请修改本次完成额度或修改企业融资需求！";
                    break;
                case 2:
                    errorMessage = "累计完成额度超出融资需求，请修改本次完成额度！";
                    break;
                case 3:
                    errorMessage = "累计完成情况超出人才需求，请修改本次完成情况或修改企业人才需求！";
                    break;
                case 4:
                    errorMessage = "累计完成情况超出人才需求，请修改本次完成情况！";
                    break;
                default:
                    errorMessage = "";
            }
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message(errorMessage).build();
        }
    }

    /**
     * 查询已完成额度
     *
     * @param idIplMain 主数据id
     * @return bizType 业务类型
     * @author qinhuan
     * @since 2019/11/27 9:50 上午
     */
    public BigDecimal getRaisedTotal(Long idIplMain, Integer bizType) {
        List<IplLog> list = list(new LambdaQueryWrapper<IplLog>().eq(IplLog::getIdIplMain, idIplMain).eq(IplLog::getBizType, bizType));
        return list.stream().filter(e -> e.getCompleteNum() != null).map(e -> BigDecimal.valueOf(e.getCompleteNum())).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * 协同单位实时更新接口
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 16:16
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void assistUpdateStatus(T entity, IplLog iplLog) {
        // 主责单位id
        Long idRbacDepartmentDuty = (Long) ReflectionUtils.getFieldValue(entity, "idRbacDepartmentDuty");
        Integer bizType = (Integer) ReflectionUtils.getFieldValue(entity, "bizType");
        Long idIplMain = (Long) ReflectionUtils.getFieldValue(entity, "id");
        Integer dealStatus = iplLog.getDealStatus();

        Customer customer = LoginContextHolder.getRequestAttributes();
        Long customerIdRbacDepartment = customer.getIdRbacDepartment();

        LambdaUpdateWrapper<IplAssist> qw = new LambdaUpdateWrapper<>();
        qw.eq(IplAssist::getBizType, bizType).eq(IplAssist::getIdIplMain, iplLog.getIdIplMain()).eq(IplAssist::getIdRbacDepartmentAssist, customerIdRbacDepartment);
        IplAssist one = iplAssistService.getOne(qw);
        if (IplStatusEnum.DONE.getId().equals(one.getDealStatus())){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION).message("数据状态错误").build();
        }

        if (IplStatusEnum.DONE.getId().equals(dealStatus)) {
            // 删除redis超时
            redisSubscribeService.removeRecordInfo(idIplMain + "-" + customerIdRbacDepartment, idRbacDepartmentDuty, bizType);
        } else {
            // 更新redis超时
            redisSubscribeService.saveSubscribeInfo(idIplMain + "-" + customerIdRbacDepartment, ListTypeConstants.UPDATE_OVER_TIME, idRbacDepartmentDuty, bizType);
        }

        iplAssistService.update(IplAssist.newInstance().dealStatus(dealStatus).processStatus(ProcessStatusEnum.NORMAL.getId()).build(), qw);

        // 记录日志
        iplLog.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
        iplLog.setIdRbacDepartmentAssist(customerIdRbacDepartment);
        iplLog.setBizType(bizType);
        iplLogService.save(iplLog);
    }

    /**
     * 主责单位实时更新接口
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 16:16
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void dutyUpdateStatus(T entity, IplLog iplLog) {

        Integer status = (Integer) ReflectionUtils.getFieldValue(entity, "status");
        if (IplStatusEnum.DONE.getId().equals(status)){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION).message("数据状态错误").build();
        }
        // 主责单位id
        Long idRbacDepartmentDuty = (Long) ReflectionUtils.getFieldValue(entity, "idRbacDepartmentDuty");
        Integer bizType = (Integer) ReflectionUtils.getFieldValue(entity, "bizType");
        Long idIplMain = (Long) ReflectionUtils.getFieldValue(entity, "id");
        Integer dealStatus = iplLog.getDealStatus();


        // 主责单位把主表完结
        if (IplStatusEnum.DONE.getId().equals(dealStatus)) {
            //主责单位修改清单状态为已完成，要提醒未完成处理的协同单位
            List<IplAssist> iplAssistList = iplAssistService.list(new LambdaQueryWrapper<IplAssist>().eq(IplAssist::getIdIplMain, idIplMain)
                    .eq(IplAssist::getBizType, bizType)
                    .ne(IplAssist::getDealStatus, IplStatusEnum.DONE.getId()));
            if(CollectionUtils.isNotEmpty(iplAssistList)){
                List<Long> idRbacDepartmentAssistList = iplAssistList.stream().map(IplAssist::getIdRbacDepartmentAssist)
                        .distinct()
                        .collect(Collectors.toList());
                String enterpriseName = (String) ReflectionUtils.getFieldValue(entity, "enterpriseName");
                sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                        .sourceId(idIplMain)
                        .idRbacDepartment(idRbacDepartmentDuty)
                        .dataSourceClass(SysMessageDataSourceClassEnum.HELP.getId())
                        .flowStatus(SysMessageFlowStatusEnum.SIX.getId())
                        .title(enterpriseName)
                        .helpDepartmentIdList(idRbacDepartmentAssistList)
                        .bizType(bizType)
                        .build());
            }
            // 休改主表状态 并休改协同表状态，各插入一个日志、各清除redis超时
            dutyDone(entity, idRbacDepartmentDuty, idIplMain, dealStatus, bizType, iplLog.getProcessInfo(),iplLog.getCompleteNum());
        } else {
            // 保存日志
            iplLog.setIdRbacDepartmentDuty(idRbacDepartmentDuty);
            iplLog.setIdRbacDepartmentAssist(0L);
            iplLog.setBizType(bizType);
            iplLogService.save(iplLog);

            // 将状数据态置为"处理中"，将超时状态置为"进展正常"
            ReflectionUtils.setFieldValue(entity, "status", IplStatusEnum.DEALING.getId());
            ReflectionUtils.setFieldValue(entity, "processStatus", ProcessStatusEnum.NORMAL.getId());
            ReflectionUtils.setFieldValue(entity, "latestProcess", iplLog.getProcessInfo());
            updateMain(entity);

            // 更新redis的超时
            redisSubscribeService.saveSubscribeInfo(idIplMain + "-0", ListTypeConstants.UPDATE_OVER_TIME, idRbacDepartmentDuty, bizType);
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
    private <T> void dutyDone(T entity, Long idRbacDepartmentDuty, Long idIplMain, Integer dealStatus, Integer bizType, String processInfo,Double completeNum) {

        List<IplLog> iplLogs = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        // 查询主表的协同单位
        List<IplAssist> assists = iplAssistService.getAssists(bizType, idIplMain);
        if (CollectionUtils.isNotEmpty(assists)) {
            // 过滤掉已关闭的协同单位
            assists.removeIf(e -> IplStatusEnum.DONE.getId().equals(e.getDealStatus()));

            if (CollectionUtils.isNotEmpty(assists)){
                // 更新协同单位状态、删除协同单位的redis超时设置
                assists.forEach(e -> {
                    builder.append(e.getNameRbacDepartmentAssist()).append("、");
                    e.setDealStatus(dealStatus);
                    e.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                    IplLog iplLogAssit = IplLog.newInstance().dealStatus(dealStatus).idRbacDepartmentDuty(idRbacDepartmentDuty).bizType(bizType).idRbacDepartmentAssist(e.getIdRbacDepartmentAssist()).idIplMain(idIplMain).processInfo("主责单位关闭协同邀请").build();
                    iplLogs.add(iplLogAssit);

                    // 删除协同单位的redis超时设置
                    redisSubscribeService.removeRecordInfo(idIplMain + "-" + e.getIdRbacDepartmentAssist(), idRbacDepartmentDuty, bizType);
                });

                // 批量更新协同单位状态
                iplAssistService.updateBatchById(assists);
            }
        }
        // 主责记录日志
        String deptName = StringUtils.stripEnd(builder.toString(), "、");
        String logInfo = processInfo;
        if (StringUtils.isNotBlank(deptName)){
            logInfo = logInfo + "\n" + "关闭了" + deptName + "的协同邀请";
        }
        IplLog iplLogDuty = IplLog.newInstance()
                .dealStatus(dealStatus)
                .idRbacDepartmentDuty(idRbacDepartmentDuty)
                .bizType(bizType)
                .idRbacDepartmentAssist(0L)
                .idIplMain(idIplMain)
                .processInfo(logInfo)
                .completeNum(completeNum)
                .build();
        iplLogs.add(iplLogDuty);
        iplLogService.saveBatch(iplLogs);

        // 删除主表的redis超时设置
        redisSubscribeService.removeRecordInfo(idIplMain + "-0", idRbacDepartmentDuty, bizType);

        // 更新主表状态、删除主表的redis超时设置
        ReflectionUtils.setFieldValue(entity, "processStatus", ProcessStatusEnum.NORMAL.getId());
        ReflectionUtils.setFieldValue(entity, "status", IplStatusEnum.DONE.getId());
        ReflectionUtils.setFieldValue(entity, "latestProcess", processInfo);
        updateMain(entity);
    }

    /**
     * 更新主表状态
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-10 16:28
     */
    public <C> void updateMain(C entity) {
        // 更新主表状态
        if (entity instanceof IplDarbMain) {
            IplDarbMain iplDarbMain = (IplDarbMain) entity;
            //判断是否为第一次更新
            IplDarbMain vo = iplDarbMainService.getById(iplDarbMain.getId());
            if (vo != null) {
                if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                    if (IplStatusEnum.DEALING.getId().equals(iplDarbMain.getStatus()) || IplStatusEnum.DONE.getId().equals(iplDarbMain.getStatus())) {
                        iplDarbMain.setGmtFirstDeal(System.currentTimeMillis());
                    }
                }
            }
            iplDarbMainService.updateById(iplDarbMain);
        } else if (entity instanceof IplEsbMain) {
            IplEsbMain iplEsbMain = (IplEsbMain) entity;
            //判断是否为第一次更新
            IplEsbMain vo = iplEsbMainService.getById(iplEsbMain.getId());
            if (vo != null) {
                if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                    if (IplStatusEnum.DEALING.getId().equals(iplEsbMain.getStatus()) || IplStatusEnum.DONE.getId().equals(iplEsbMain.getStatus())) {
                        iplEsbMain.setGmtFirstDeal(System.currentTimeMillis());
                    }
                }
            }
            iplEsbMainService.updateById(iplEsbMain);
        } else if (entity instanceof IplPdMain) {
            IplPdMain iplPdMain = (IplPdMain) entity;
            iplPdMainService.updateById(iplPdMain);
        } else if (entity instanceof IplSatbMain) {
            IplSatbMain iplSatbMain = (IplSatbMain) entity;
            //判断是否为第一次更新
            IplSatbMain vo = iplSatbMainService.getById(iplSatbMain.getId());
            if (vo != null) {
                if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                    if (IplStatusEnum.DEALING.getId().equals(iplSatbMain.getStatus()) || IplStatusEnum.DONE.getId().equals(iplSatbMain.getStatus())) {
                        iplSatbMain.setGmtFirstDeal(System.currentTimeMillis());
                    }
                }
            }
            iplSatbMainService.updateById(iplSatbMain);
        } else if (entity instanceof IplOdMain) {
            IplOdMain iplOdMain = (IplOdMain) entity;
            //判断是否为第一次更新
            IplOdMain vo = iplOdMainService.getById(iplOdMain.getId());
            if (vo != null) {
                if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                    if (IplStatusEnum.DEALING.getId().equals(iplOdMain.getStatus()) || IplStatusEnum.DONE.getId().equals(iplOdMain.getStatus())) {
                        iplOdMain.setGmtFirstDeal(System.currentTimeMillis());
                    }
                }
            }
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
    public Integer getLastDealStatus(Long idIplMain, Integer bizType) {
        LambdaQueryWrapper<IplLog> qw = new LambdaQueryWrapper();
        qw.eq(IplLog::getIdIplMain, idIplMain).eq(IplLog::getBizType, bizType).orderByDesc(IplLog::getGmtCreate);
        IplLog last = getOne(qw, false);
        // 处理中
        Integer dealStatus = IplStatusEnum.DEALING.getId();
        if (last != null) {
            dealStatus = last.getDealStatus();
        }
        return dealStatus;
    }

    /**
     * 功能描述 保存日志
     *
     * @param idIplMain   主表id
     * @param status      状态 2 处理中 3 处理完成
     * @param idDuty      主责单位id
     * @param idAssist    协同单位id
     * @param processInfo 记录信息
     * @author gengzhiqiang
     * @date 2019/9/27 17:12
     */
    public void saveLog(Long idIplMain, Integer status, Long idDuty, Long idAssist, String processInfo,Integer bizType) {
        IplLog dutyLog = IplLog.newInstance()
                .idIplMain(idIplMain)
                .dealStatus(status)
                .idRbacDepartmentDuty(idDuty)
                .idRbacDepartmentAssist(idAssist)
                .processInfo(processInfo)
                .bizType(bizType)
                .build();
        save(dutyLog);
    }

    /**
     * 根据主责单位id获取数据来源
     *
     * @param bizType 清单类型
     * @return 数据来源
     * @author gengjiajia
     * @since 2019/10/17 14:38
     */
    private Integer getDataSourceByBizType(Integer bizType) {
        switch (bizType) {
            case 10:
                return SysMessageDataSourceClassEnum.COOPERATION.getId();
            case 20:
                return SysMessageDataSourceClassEnum.DEVELOPING.getId();
            case 30:
                return SysMessageDataSourceClassEnum.TARGET.getId();
            case 40:
                return SysMessageDataSourceClassEnum.DEMAND.getId();
            default:
                return null;
        }
    }

    /**
     * 统计月度需求完成情况数量
     *
     * @param  startTime 开始统计时间
     * @param  endTime 结束统计时间
     * @param bizType 清单类型
     * @return 完成情况数量
     * @author gengjiajia
     * @since 2019/10/30 14:59
     */
    public List<Map<String,Object>> statisticsMonthlyDemandCompletionNum(Long startTime, Long endTime, Integer bizType){
        return baseMapper.statisticsMonthlyDemandCompletionNum(startTime,endTime,bizType);
    }

    /**
     * 某年某月指定行业人才需求完成情况统计
     *
     * @param  startTime 统计开始时间范围
     * @param endTime 统计截止时间范围
     * @return 需求完成情况统计
     * @author gengjiajia
     * @since 2019/10/30 19:38
     */
    public List<Map<String,Object>> statisticsIndustryDemandCompletionNum(Long startTime, Long endTime){
        return baseMapper.statisticsIndustryDemandCompletionNum(startTime,endTime);
    }
}
