package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.dao.IplAssistDao;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: IplAssistService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:35
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplAssistServiceImpl extends BaseServiceImpl<IplAssistDao, IplAssist> {
    @Autowired
    private IplLogServiceImpl iplLogService;

    @Autowired
    private IplAssistServiceImpl iplAssistService;

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Autowired
    private RedisSubscribeServiceImpl redisSubscribeService;

    /**
     * 新增协同单位
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-09 19:27
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public void addAssist(IplLog iplLog, List<IplAssist> assistList){
        // 新增协同单位
        iplAssistService.saveBatch(assistList);
        // 保存处理日志
        iplLogService.save(iplLog);

        // 主表重设超时
        redisSubscribeService.saveSubscribeInfo(iplLog.getIdIplMain() + "-0", ListTypeConstants.UPDATE_OVER_TIME, iplLog.getIdRbacDepartmentDuty());

        // 设置协同单位超时
        assistList.forEach(e->{
            redisSubscribeService.saveSubscribeInfo(e.getId() + "-" + e.getIdRbacDepartmentAssist(), ListTypeConstants.DEAL_OVER_TIME, e.getIdRbacDepartmentDuty());
        });
    }

    /**
     * 删除主表附带的日志、协同、附件，调用方法必须要有事物
     *
     * @param  mainId 主表id，
     * @param  idRbacDepartmentDuty 主责单位id
     * @return
     * @author qinhuan
     * @since 2019-10-09 14:42
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public void del(Long mainId, Long idRbacDepartmentDuty, String attachmentCode){
        batchDel(Collections.singletonList(mainId), idRbacDepartmentDuty, Collections.singletonList(attachmentCode));
    }

    /**
     * 批量删除主表附带的日志、协同、附件，调用方法必须要有事物
     *
     * @param  mainIds 主表id，
     * @param  idRbacDepartmentDuty 主责单位id
     * @return
     * @author qinhuan
     * @since 2019-10-09 14:42
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public void batchDel(List<Long> mainIds, Long idRbacDepartmentDuty, List<String> attachmentCodes){

        // 删除日志
        LambdaQueryWrapper<IplLog> logQw = new LambdaQueryWrapper<>();
        logQw.eq(IplLog::getIdRbacDepartmentDuty, idRbacDepartmentDuty).in(IplLog::getIdIplMain, mainIds);
        iplLogService.remove(logQw);

        // 删除协同
        LambdaQueryWrapper<IplAssist> assistQw = new LambdaQueryWrapper<>();
        assistQw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).in(IplAssist::getIdIplMain, mainIds);
        iplAssistService.remove(assistQw);

        // 删除附件
        LambdaQueryWrapper<Attachment> attachmentQw = new LambdaQueryWrapper<>();
        attachmentQw.in(Attachment::getAttachmentCode, attachmentCodes);
        attachmentService.remove(attachmentQw);

        // 删除redis定时任务
        mainIds.forEach(e->{
            redisSubscribeService.removeRecordInfo(e + "-0", idRbacDepartmentDuty);
        });

    }

    /**
     * 总体进展
     *
     * @param mainId :主表id，idRbacDepartmentDuty:主表主责单位id，processStatus:主表状态
     * @return
     */
    public Map<String, Object> totalProcessAndAssists(Long mainId, Long idRbacDepartmentDuty, Integer processStatus) {

        List<IplAssist> assists = getAssists(idRbacDepartmentDuty, mainId);

        // 查询处理日志列表
        LambdaQueryWrapper<IplLog> logqw = new LambdaQueryWrapper<>();
        logqw.eq(IplLog::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplLog::getIdIplMain, mainId).orderByDesc(IplLog::getGmtCreate);
        List<IplLog> logs = iplLogService.list(logqw);

        // 日志定义返回值
        List<Map<String, Object>> processList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logs)) {
            // 按照协同单位的id分成子logs
            LinkedHashMap<Long, List<IplLog>> collect = logs.stream()
                    .collect(Collectors.groupingBy(IplLog::getIdRbacDepartmentAssist, LinkedHashMap::new, Collectors.toList()));

            // 协同单位处理日志
            if (CollectionUtils.isNotEmpty(assists)) {
                assists.forEach(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("department", e.getNameRbacDepartmentAssist());
                    map.put("processStatus", e.getProcessStatus());
                    map.put("logs", collect.get(e.getIdRbacDepartmentAssist()));
                    processList.add(map);
                });
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalProcess", processList);
        resultMap.put("assists", assists);

        return resultMap;
    }

    public List<IplAssist> getAssists(Long idRbacDepartmentDuty, Long mainId){
        // 查询协同单位列表
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, mainId).orderByDesc(IplAssist::getGmtCreate);

        List<IplAssist> assists = list(qw);

        // 协同单位名称
        if (CollectionUtils.isNotEmpty(assists)) {
            assists.forEach(e -> {
                String nameDeptAssist = null;
                if (new Long(0L).equals(e.getIdRbacDepartmentAssist())){
                    nameDeptAssist = InnovationUtil.getDeptNameById(e.getIdRbacDepartmentDuty());
                }else {
                    try {
                        nameDeptAssist = InnovationUtil.getDeptNameById(e.getIdRbacDepartmentAssist());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                e.setNameRbacDepartmentAssist(nameDeptAssist);
            });
        }
        return assists;
    }
}
