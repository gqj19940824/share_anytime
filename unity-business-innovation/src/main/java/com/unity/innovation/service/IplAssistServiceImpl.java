package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.ReflectionUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.dao.IplAssistDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.*;
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

    @Resource
    private RbacClient rbacClient;

    @Resource
    private SysMessageHelpService sysMessageHelpService;

    public PageElementGrid listAssistByPage(PageEntity<Map<String, Object>> pageEntity){
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Map<String, Object>> pageable = pageEntity.getPageable();
        Map<String, Object> entity = pageEntity.getEntity();
        String gmtCreate = MapUtils.getString(entity, "gmtCreate");
        if (StringUtils.isNotBlank(gmtCreate)){
            entity.put("gmtCreateStart", InnovationUtil.getFirstTimeInMonth(gmtCreate, true));
            entity.put("gmtCreateEnd", InnovationUtil.getFirstTimeInMonth(gmtCreate, false));
        }
        Integer bizType = MapUtils.getInteger(entity, "bizType");
        Page<Map<String, Object>> page = PageHelper.startPage((int)pageable.getCurrent(), (int)pageable.getSize(), true);
        List<Map<String, Object>> maps;
        if (BizTypeEnum.ENTERPRISE.getType().equals(bizType)){
            maps = baseMapper.assistEsbList(entity);
        }else if (BizTypeEnum.CITY.getType().equals(bizType)){
            maps = baseMapper.assistDarbList(entity);
        }else if (BizTypeEnum.INTELLIGENCE.getType().equals(bizType)){
            maps = baseMapper.assistOdList(entity);
        }else if (BizTypeEnum.GROW.getType().equals(bizType)){
            maps = baseMapper.assistSatbList(entity);
        } else {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message("业务类型错误").build();
        }

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
            Long idRbacDepartmentDuty = (Long) ReflectionUtils.getFieldValue(entity,"idRbacDepartmentDuty");
            // 主责单位id
            Integer bizType = (Integer) ReflectionUtils.getFieldValue(entity,"bizType");
            // 主表id
            Long idIplMain = (Long) ReflectionUtils.getFieldValue(entity,"id");

            List<IplAssist> assists1 = getAssists(bizType, idIplMain);
            List<Long> collect = assists1.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList()); // TODO

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
                        .bizType(bizType)
                        .dealStatus(IplStatusEnum.UNDEAL.getId())
                        .processStatus(ProcessStatusEnum.NORMAL.getId())
                        .idIplMain(idIplMain)
                        .idRbacDepartmentAssist(idRbacDepartmentAssist)
                        .inviteInfo(e.getInviteInfo())
                        .build();
                assistList.add(assist);
                deptName.append(InnovationUtil.getDeptNameById(idRbacDepartmentAssist) + "、");
            });

            // 拼接"处理进展"中的协同单位名称
            String nameStr = null;
            if(deptName.indexOf("、") > -1){
                nameStr = deptName.subSequence(0, deptName.lastIndexOf("、")).toString();
            }
            // 计算日志的状态
            Integer lastDealStatus = iplLogService.getLastDealStatus(idIplMain, bizType);
            IplLog iplLog = IplLog.newInstance().idRbacDepartmentAssist(0L).processInfo("新增协同单位：" + nameStr)
                    .bizType(bizType).idIplMain(idIplMain).idRbacDepartmentDuty(idRbacDepartmentDuty).dealStatus(lastDealStatus).build(); // other TODO

            // 新增协同单位、保存处理日志、主表重设超时、设置协同单位超时
            iplAssistService.addAssist(iplLog, assistList);

            // 将状数据态置为"处理中"，将超时状态置为"进展正常"
            ReflectionUtils.setFieldValue(entity, "status", IplStatusEnum.DEALING.getId());
            ReflectionUtils.setFieldValue(entity, "processStatus", ProcessStatusEnum.NORMAL.getId());
            ReflectionUtils.setFieldValue(entity, "latestProcess", iplLog.getProcessInfo());
            iplLogService.updateMain(entity);

            //====新增协同单位====增加系统通知========
            String enterpriseName = ReflectionUtils.getDeclaredMethod(entity,"getEnterpriseName").invoke(entity).toString();
            List<Long> list = assistList.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
            sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                    .sourceId(idIplMain)
                    .idRbacDepartment(idRbacDepartmentDuty)
                    .dataSourceClass(SysMessageDataSourceClassEnum.HELP.getId())
                    .flowStatus(SysMessageFlowStatusEnum.ONE.getId())
                    .title(enterpriseName)
                    .helpDepartmentIdList(list)
                    .bizType(bizType)
                    .build());
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
        redisSubscribeService.saveSubscribeInfo(iplLog.getIdIplMain() + "-0", ListTypeConstants.UPDATE_OVER_TIME, iplLog.getIdRbacDepartmentDuty(), iplLog.getBizType());

        // 设置协同单位超时
        assistList.forEach(e->{
            redisSubscribeService.saveSubscribeInfo(iplLog.getIdIplMain() + "-" + e.getIdRbacDepartmentAssist(), ListTypeConstants.DEAL_OVER_TIME, e.getIdRbacDepartmentDuty(), e.getBizType());
        });
    }

    /**
     * 删除主表附带的日志、协同、附件，调用方法必须要有事物
     *
     * @param  iplSatbMain 主表
     * @return
     * @author qinhuan
     * @since 2019-10-09 14:42
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public void del(IplSatbMain iplSatbMain){
        batchDel(Collections.singletonList(iplSatbMain.getId()), Collections.singletonList(iplSatbMain), Collections.singletonList(iplSatbMain.getAttachmentCode()), iplSatbMain.getBizType());
    }

    /**
     * 批量删除主表附带的日志、协同、附件，调用方法必须要有事物
     *
     * @param  mainIds 主表id，
     * @param  bizType 业务类型
     * @return
     * @author qinhuan
     * @since 2019-10-09 14:42
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public <T> void batchDel(List<Long> mainIds, List<T> list, List<String> attachmentCodes, Integer bizType){

        // 删除日志
        LambdaQueryWrapper<IplLog> logQw = new LambdaQueryWrapper<>();
        logQw.eq(IplLog::getBizType, bizType).in(IplLog::getIdIplMain, mainIds);
        iplLogService.remove(logQw);

        LambdaQueryWrapper<IplAssist> assistQw = new LambdaQueryWrapper<>();
        assistQw.eq(IplAssist::getBizType, bizType).in(IplAssist::getIdIplMain, mainIds);
        // 清除协同单位定时任务
        List<IplAssist> iplAssists = iplAssistService.list(assistQw);
        iplAssists.forEach(e->{
            redisSubscribeService.removeRecordInfo(e.getIdIplMain() + "-" + e.getIdRbacDepartmentAssist(), e.getIdRbacDepartmentDuty(), bizType);
        });
        // 删除协同
        iplAssistService.remove(assistQw);

        // 删除附件
        LambdaQueryWrapper<Attachment> attachmentQw = new LambdaQueryWrapper<>();
        attachmentQw.in(Attachment::getAttachmentCode, attachmentCodes);
        attachmentService.remove(attachmentQw);

        // 清除主表定时任务
        list.forEach(e->{
            Long id = (Long) ReflectionUtils.getFieldValue(e, "id");
            Long idRbacDepartmentDuty = (Long) ReflectionUtils.getFieldValue(e, "idRbacDepartmentDuty");
            redisSubscribeService.removeRecordInfo(id + "-0", idRbacDepartmentDuty, bizType);
        });
    }

    /**
     * 总体进展
     *
     * @param mainId :主表id，idRbacDepartmentDuty:主表主责单位id，processStatus:主表状态
     * @return
     */
    public Map<String, Object> totalProcessAndAssists(Long mainId, Long idRbacDepartmentDuty, Integer processStatus, Integer bizType) {

        List<IplAssist> assists = getAssists(bizType, mainId);

        // 查询处理日志列表
        LambdaQueryWrapper<IplLog> logqw = new LambdaQueryWrapper<>();
        logqw.eq(IplLog::getBizType, bizType).eq(IplLog::getIdIplMain, mainId).orderByDesc(IplLog::getGmtCreate);
        List<IplLog> logs = iplLogService.list(logqw);

        // 日志定义返回值
        List<Map<String, Object>> processList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(logs)) {
            // 按照协同单位的id分成子logs  TODO  bizType
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
        if (!customer.getIdRbacDepartment().equals(idRbacDepartmentDuty)){ // TODO
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

    public List<IplAssist> getAssists(Integer bizType, Long mainId){
        // 查询协同单位列表
        LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
        qw.eq(IplAssist::getBizType, bizType).eq(IplAssist::getIdIplMain, mainId).orderByAsc(IplAssist::getGmtCreate);

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
    public List<Map<String, Object>> getAssistList(Long idIplMain, Integer bizType) {

        List<IplAssist> assistList = iplAssistService.list(new LambdaQueryWrapper<IplAssist>()
                .eq(IplAssist::getIdIplMain, idIplMain)
                .eq(IplAssist::getBizType, bizType));
        // 已有协同单位的单位id集合
        List<Long> assistDeptIds = assistList.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
        // 系统中所有单位的单位id集合
        List<DepartmentVO> departmentList = rbacClient.getAllDepartment();

        // 抛出已有协同单位、宣传部单位、主责单位
        departmentList = departmentList.stream().filter(d -> check(assistDeptIds, d.getId())).collect(Collectors.toList());

        return JsonUtil.ObjectToList(departmentList, new String[]{"id", "name"}, null);
    }

    private boolean check(List<Long> assistDeptIds, Long idDept) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        return !assistDeptIds.contains(idDept) && !InnovationConstant.DEPARTMENT_PD_ID.equals(idDept) && !idDept.equals(customer.getIdRbacDepartment());
    }

}
