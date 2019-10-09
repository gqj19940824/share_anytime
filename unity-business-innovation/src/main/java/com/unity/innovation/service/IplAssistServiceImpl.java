package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    /**
     * 删除主表附带的日志、协同、附件
     *
     * @param  mainId 主表id，
     *         businessType 业务类型，参见innovationConst.DEPARTMENT_DARB_ID
     * @return
     * @author qinhuan
     * @since 2019-10-09 14:42
     */
    public void del(Long mainId, String businessType){

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
