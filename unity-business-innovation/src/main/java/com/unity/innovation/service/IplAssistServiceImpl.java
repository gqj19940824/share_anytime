package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.base.ContextHolder;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.JsonUtil;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.ReflectionUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.mSysCfg;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.dao.IplAssistDao;

import javax.annotation.Resource;
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
@Slf4j
public class IplAssistServiceImpl extends BaseServiceImpl<IplAssistDao, IplAssist> {
    @Autowired
    private IplLogServiceImpl iplLogService;

    @Autowired
    private IplAssistServiceImpl iplAssistService;

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Autowired
    private RedisSubscribeServiceImpl redisSubscribeService;

    @Autowired
    protected IplDarbMainServiceImpl iplDarbMainService;

    @Autowired
    private IplPdMainServiceImpl iplPdMainService;

    @Autowired
    private IplSatbMainServiceImpl iplSatbMainService;

    @Autowired
    private IplEsbMainServiceImpl iplEsbMainService;

    @Resource
    private RbacClient rbacClient;

    public PageElementGrid listAssistByPage(PageEntity<Map<String, Object>> pageEntity){
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Map<String, Object>> pageable = pageEntity.getPageable();
        Page<Map<String, Object>> page = PageHelper.startPage((int)pageable.getCurrent(), (int)pageable.getSize(), true);
        Map<String, Object> entity = pageEntity.getEntity();
        String gmtCreate = MapUtils.getString(entity, "gmtCreate");
        if (StringUtils.isNotBlank(gmtCreate)){
            entity.put("gmtCreateStart", InnovationUtil.getFirstTimeInMonth(gmtCreate, true));
            entity.put("gmtCreateEnd", InnovationUtil.getFirstTimeInMonth(gmtCreate, false));
        }
        Customer customer = LoginContextHolder.getRequestAttributes();
        entity.put("idRbacDepartmentAssist", customer.getIdRbacDepartment());
        List<Map<String, Object>> maps = baseMapper.assistDarbList(entity);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(page.getTotal())
                .items(convert(maps)).build();

        return result;
    }

    private List<Map<String,Object>> convert(List<Map<String,Object>> maps){
        if (CollectionUtils.isNotEmpty(maps)){
            maps.forEach(e->{
                e.put("idRbacDepartmentDutyName", InnovationUtil.getDeptNameById(MapUtils.getLong(e, "idRbacDepartmentDuty")));
            });
        }
        return maps;
    }

    /**
     * 新增协同单位
     *
     * @param   assists map
     *                idRbacDepartmentAssist 协同单位id
     *                inviteInfo 邀请事项
     * @param   entity 各对象（IplDarbMain、IplEsbMain、IplPdMain、IplSatbMain..）
     * @author qinhuan
     * @since 2019-09-25 18:52
     */
    @Transactional(rollbackFor = Exception.class)
    public <T>void addAssistant(List<IplAssist> assists, T entity){
        if (CollectionUtils.isEmpty(assists)){
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
        }
        try {
            // 主责单位id
            Long idRbacDepartmentDuty = (Long) ReflectionUtils.getDeclaredMethod(entity,"getIdRbacDepartmentDuty").invoke(entity);
            // 主表id
            Long idIplMain = (Long) ReflectionUtils.getDeclaredMethod(entity,"getId").invoke(entity);

            List<IplAssist> assists1 = getAssists(idRbacDepartmentDuty, idIplMain);
            List<Long> collect = assists1.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());

            // 遍历协同单位组装数据
            List<IplAssist> assistList = new ArrayList<>();
            StringBuilder deptName = new StringBuilder();
            assists.forEach(e->{
                if (collect.contains(e.getIdRbacDepartmentAssist())){
                    throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS).message("含重复添加数据").build();
                }
                Long idRbacDepartmentAssist = e.getIdRbacDepartmentAssist();
                IplAssist assist = IplAssist.newInstance()
                        .idRbacDepartmentDuty(idRbacDepartmentDuty)
                        .dealStatus(IplStatusEnum.UNDEAL.getId())
                        .processStatus(ProcessStatusEnum.NORMAL.getId())
                        .idIplMain(idIplMain)
                        .idRbacDepartmentAssist(idRbacDepartmentAssist)
                        .inviteInfo(e.getInviteInfo())
                        .build();
                assistList.add(assist);
                deptName.append(InnovationUtil.getDeptNameById(idRbacDepartmentAssist) + "、");
            });

            // 将状数据态置为"处理中"，将超时状态置为"进展正常"
            iplLogService.updateStatus(entity);

            // 拼接"处理进展"中的协同单位名称
            String nameStr = null;
            if(deptName.indexOf("、") > 0){
                nameStr = deptName.subSequence(0, deptName.lastIndexOf("、")).toString();
            }
            // 计算日志的状态
            Integer lastDealStatus = iplLogService.getLastDealStatus(idIplMain, idRbacDepartmentDuty);
            IplLog iplLog = IplLog.newInstance().idRbacDepartmentAssist(0L).processInfo("新增协同单位：" + nameStr).idIplMain(idIplMain).idRbacDepartmentDuty(idRbacDepartmentDuty).dealStatus(lastDealStatus).build();

            // 新增协同单位、保存处理日志、主表重设超时、设置协同单位超时
            iplAssistService.addAssist(iplLog, assistList);
        } catch (UnityRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("新增协同项出错" + e.getMessage(),e);
            throw UnityRuntimeException.newInstance().build();
        }
    }

    /**
     * 新增协同单位
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019-10-09 19:27
     */
    public void addAssist(IplLog iplLog, List<IplAssist> assistList){
        // 新增协同单位
        iplAssistService.saveBatch(assistList);
        // 保存处理日志
        iplLogService.save(iplLog);

        // 主表重设超时
        redisSubscribeService.saveSubscribeInfo(iplLog.getIdIplMain() + "-0", ListTypeConstants.UPDATE_OVER_TIME, iplLog.getIdRbacDepartmentDuty());

        // 设置协同单位超时
        assistList.forEach(e->{
            redisSubscribeService.saveSubscribeInfo(iplLog.getIdIplMain() + "-" + e.getIdRbacDepartmentAssist(), ListTypeConstants.DEAL_OVER_TIME, e.getIdRbacDepartmentDuty());
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

        // 清除主表定时任务
        mainIds.forEach(e->{
            redisSubscribeService.removeRecordInfo(e + "-0", idRbacDepartmentDuty);
        });
        // 清除协同单位定时任务
        List<IplAssist> iplAssists = iplAssistService.list(assistQw);
        iplAssists.forEach(e->{
            redisSubscribeService.removeRecordInfo(e.getIdIplMain() + "-" + e.getIdRbacDepartmentAssist(), e.getIdRbacDepartmentDuty());
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

            // 主责单位处理日志
            Map<String, Object> mapDuty = new HashMap<>();
            mapDuty.put("department", InnovationUtil.getDeptNameById(idRbacDepartmentDuty));
            mapDuty.put("processStatus", processStatus);
            mapDuty.put("logs", collect.get(0L)); // 在日志表的协同单位字段中，主责单位的日志记录在该字段中存为0
            processList.add(mapDuty);

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

        Customer customer = LoginContextHolder.getRequestAttributes();
        // 非主责单位协同列表只查自己
        if (!customer.getIdRbacDepartment().equals(idRbacDepartmentDuty)){
            Iterator<IplAssist> iterator = assists.iterator();
            while (iterator.hasNext()){
                IplAssist next = iterator.next();
                if (!next.getIdRbacDepartmentAssist().equals(customer.getIdRbacDepartment())){
                    iterator.remove();
                }
            }
        }
        resultMap.put("assists", assists);

        return resultMap;
    }

    public List<IplAssist> getAssists(Long idRbacDepartmentDuty, Long mainId){
        // 查询协同单位列表
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, mainId).orderByAsc(IplAssist::getGmtCreate);

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

    /**
     * 功能描述 获取协同单位下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    public List<Map<String, Object>> getAssistList(Long idIplMain,Long idDuty) {
        //主表id  数据集合
        List<DepartmentVO> departmentList = rbacClient.getAllDepartment();
        departmentList = departmentList.stream().filter(d -> !d.getId().equals(idDuty)).collect(Collectors.toList());
        List<IplAssist> assistList = iplAssistService.list(new LambdaQueryWrapper<IplAssist>()
                .eq(IplAssist::getIdIplMain, idIplMain)
                .eq(IplAssist::getIdRbacDepartmentDuty, idDuty));
        List<Long> ids = assistList.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
        departmentList = departmentList.stream().filter(d -> !ids.contains(d.getId())).collect(Collectors.toList());
        return JsonUtil.ObjectToList(departmentList, new String[]{"id", "name"}, null);
    }

}
