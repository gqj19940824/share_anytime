package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.reflect.TypeToken;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.GsonUtils;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.dao.IplManageMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.DailyWorkStatusPackage;
import com.unity.innovation.entity.IplSupervisionMain;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.entity.generated.IplmManageLog;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingProcessEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplManageMainServiceImpl extends BaseServiceImpl<IplManageMainDao, IplManageMain> {


    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private IplmManageLogServiceImpl logService;

    @Resource
    private DicUtils dicUtils;

    /**
     * 组装发改局excel数据
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 4:38 下午
     */
    public List<List<Object>> getDarbData(String snapshot){
        List<List<Object>> dataList = new ArrayList<>();

        if (StringUtils.isNoneBlank(snapshot)) {
            List<Map> parse = JSON.parseObject(snapshot, List.class);
            parse.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.get("industryCategory"),
                        e.get("enterpriseName"),
                        e.get("demandItem"),
                        e.get("demandCategory"),
                        e.get("projectName"),
                        e.get("content"),
                        e.get("totalAmount"),
                        e.get("projectProgress"),
                        e.get("totalAmount"),
                        e.get("bank"),
                        e.get("bond"),
                        e.get("selfRaise"),
                        e.get("increaseTrustType"),
                        e.get("whetherIntroduceSocialCapital"),
                        e.get("constructionCategory"),
                        e.get("constructionStage"),
                        e.get("constructionModel"),
                        e.get("contactPerson"),
                        e.get("contactWay"),
                        e.get("gmtCreate"),
                        e.get("gmtModified"),
                        e.get("source"),
                        e.get("status"),
                        e.get("latestProcess"));
                dataList.add(list);
            });
        }
        return dataList;
    }

    /**
     * 组装企服局excel数据
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 4:38 下午
     */
    public List<List<Object>> getEbsData(String snapshot){
        List<List<Object>> dataList = new ArrayList<>();
        if (StringUtils.isNoneBlank(snapshot)) {
            List<Map> parse = JSON.parseObject(snapshot, List.class);
            parse.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.get("industryCategory"),
                        e.get("enterpriseName"),
                        e.get("enterpriseProfile"),
                        e.get("newProductAndTech"),
                        e.get("contactPerson"),
                        e.get("contactWay"),
                        e.get("gmtCreate"),
                        e.get("gmtModified"),
                        e.get("source"),
                        e.get("status"),
                        e.get("latestProcess"));
                dataList.add(list);
            });
        }
        return dataList;
    }

    /**
     * 组装企服局excel数据
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 4:38 下午
     */
    public List<List<Object>> getOdData(String snapshot){
        List<List<Object>> dataList = new ArrayList<>();
        if (StringUtils.isNoneBlank(snapshot)) {
            List<Map> parse = JSON.parseObject(snapshot, List.class);
            parse.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.get("industryCategory"),
                        e.get("enterpriseName"),
                        e.get("enterpriseIntroduction"),
                        e.get("jdName"),
                        e.get("jobDemandNum"),
                        e.get("majorDemand"),
                        e.get("duty"),
                        e.get("qualification"),
                        e.get("specificCause"),
                        e.get("contactPerson"),
                        e.get("contactWay"),
                        e.get("gmtCreate"),
                        e.get("gmtModified"),
                        e.get("source"),
                        e.get("status"),
                        e.get("latestProcess"));
                dataList.add(list);
            });
        }
        return dataList;
    }


    /**
     * 组装科技局excel数据
     *
     * @param snapshot 快照
     * @return excel数据
     * @author gengjiajia
     * @since 2019/10/19 4:38 下午
     */
    public List<List<Object>> getSatbData(String snapshot){
        List<List<Object>> dataList = new ArrayList<>();
        if (StringUtils.isNoneBlank(snapshot)) {
            List<Map> parse = JSON.parseObject(snapshot, List.class);
            parse.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.get("industryCategoryTitle"),
                        e.get("enterpriseName"),
                        e.get("demandCategoryTitle"),
                        e.get("projectName"),
                        e.get("projectAddress"),
                        e.get("projectIntroduce"),
                        e.get("totalAmount"),
                        e.get("bank"),
                        e.get("bond"),
                        e.get("raise"),
                        e.get("techDemondInfo"),
                        e.get("contactPerson"),
                        e.get("contactWay"),
                        e.get("gmtCreate"),
                        e.get("gmtModified"),
                        e.get("sourceTitle"),
                        e.get("statusTitle"),
                        e.get("latestProcess"));
                dataList.add(list);
            });
        }
        return dataList;
    }


    public List<List<Object>> getDwspData(List<DailyWorkStatusPackage> dwspList) {
        List<List<Object>> dataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dwspList)) {
            dwspList.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.get("title"),
                        e.get("typeName"),
                        e.get("keyWordStr"),
                        e.get("theme"),
                        e.get("description"),
                        e.get("notes"),
                        e.get("attachmentCode"),
                        e.get("gmtCreate"));
                dataList.add(list);
            });
        }
        return dataList;
    }

    /**
     * 从二次打包中删除
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/17 8:43 下午
     */
    public void updateIdIpaMain(List<Long> ids, List<Long> idIpaMains){
        baseMapper.updateIdIpaMain(ids, idIpaMains);
    }

    /**
     * 功能描述 公共分页接口
     *
     * @param search 查询条件
     * @return 分页集合
     * @author gengzhiqiang
     * @date 2019/10/9 16:47
     */
    public IPage<IplManageMain> listForPkg(PageEntity<IplManageMain> search) {
        LambdaQueryWrapper<IplManageMain> lqw = wrapper(search.getEntity());
        IPage<IplManageMain> list = page(search.getPageable(), lqw);
        if (CollectionUtils.isNotEmpty(list.getRecords())) {
            list.getRecords().forEach(p -> {
                if (p.getStatus() != null) {
                    WorkStatusAuditingStatusEnum aa = WorkStatusAuditingStatusEnum.of(p.getStatus());
                    p.setStatusName(aa != null ? aa.getName() : null);
                }
            });
        }
        return list;
    }

    /**
    * 查询条件封装
    *
    * @param entity 实体
    * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.generated.IplManageMain>
    * @author JH
    * @date 2019/10/14 10:10
    */
    private LambdaQueryWrapper<IplManageMain> wrapper(IplManageMain entity) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        List<Long> roleList = customer.getRoleList();
        LambdaQueryWrapper<IplManageMain> ew = new LambdaQueryWrapper<>();
        if (entity != null) {
            //提交时间
            if (StringUtils.isNotBlank(entity.getSubmitTime())) {
                //gt 大于 lt 小于
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
                ew.gt(IplManageMain::getGmtSubmit, begin);
                //gt 大于 lt 小于
                long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
                ew.lt(IplManageMain::getGmtSubmit, end);
            }
            //标识模块
            if(StringUtils.isNotBlank(entity.getCategory())) {
                ew.eq(IplManageMain::getIdRbacDepartmentDuty, getDepartmentId(entity));
            }else {
                //非宣传部审批角色必传category
                if(!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP,DicConstants.PD_B_ROLE)))) {
                    throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                            .message("提交单位不能为空").build();
                }
            }
            //宣传部审批角色不查看 待提交、已驳回
            if(roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP,DicConstants.PD_B_ROLE)))) {
                ew.notIn(IplManageMain::getStatus, Lists.newArrayList(WorkStatusAuditingStatusEnum.TEN.getId(),WorkStatusAuditingStatusEnum.FORTY.getId()));
            }
            //状态
            if (entity.getStatus() != null) {
                ew.eq(IplManageMain::getStatus, entity.getStatus());
            }
            //排序
            ew.orderByDesc(IplManageMain::getGmtSubmit, IplManageMain::getGmtModified);
        } else {
            //只有宣传部角色可以查询所有单位数据
            if(!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP,DicConstants.PD_B_ROLE)))) {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                        .message("提交单位不能为空").build();
            }
        }
        return ew;
    }






    /**
     * 功能描述 新增编辑
     *
     * @param entity     对象
     * @param department 四大单位
     * @author gengzhiqiang
     * @date 2019/10/9 16:48
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateForPkg(IplManageMain entity, Long department) {
        //快照数据 根据不同单位 切换不同vo
        String snapshot = GsonUtils.format(entity.getDataList());
        //纪检组需要进行排序
        if (ListCategoryEnum.DEPARTMENT_SUGGESTION_ID.getName().equals(entity.getCategory())) {
            List<IplSupervisionMain> iplSupervisionMainList = GsonUtils.parse(snapshot, new TypeToken<List<IplSupervisionMain>>() {
            });
            iplSupervisionMainList.sort(comparing(IplSupervisionMain::getCategory)
                    .reversed()
                    .thenComparing(IplSupervisionMain::getGmtCreate)
                    .reversed());
            snapshot = GsonUtils.format(iplSupervisionMainList);
        }
        //数据集合
        entity.setSnapshot(snapshot);
        if (entity.getId() == null) {
            //新增
            entity.setAttachmentCode(UUIDUtil.getUUID());
            //附件
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachments());
            //待提交
            entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
            //提交时间设置最大
            entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);

            //各局
            entity.setIdRbacDepartmentDuty(department);
            //保存
            save(entity);
            return entity.getId();
        } else {
            //编辑
            IplManageMain vo = getById(entity.getId());
            if (vo == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到对象").build();
            }
            if (!(WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getStatus()) ||
                    WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getStatus()))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("只有待提交和已驳回状态下数据可编辑").build();
            }

            //附件
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachments());
            //修改信息
            updateById(entity);
            return vo.getId();
        }
    }


    /**
     * 功能描述 删除包接口
     *
     * @param ids id集合
     * @author gengzhiqiang
     * @date 2019/10/10 13:47
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdsForPkg(List<Long> ids) {
        List<IplManageMain> list = list(new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, ids));
        //状态为处理完毕 不可删除
        List<Integer> stateList = com.google.common.collect.Lists.newArrayList();
        stateList.add(WorkStatusAuditingStatusEnum.TEN.getId());
        stateList.add(WorkStatusAuditingStatusEnum.FORTY.getId());
        List<IplManageMain> list1 = list.stream().filter(l -> !stateList.contains(l.getStatus())).collect(Collectors.toList());
        //判断状态是否可操作
        if (CollectionUtils.isNotEmpty(list1)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("该状态下数据不可删除").build();
        }
        List<String> codes = list.stream().map(IplManageMain::getAttachmentCode).collect(Collectors.toList());
        //附件表
        attachmentService.remove(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codes));
        //删除主表
        removeByIds(ids);
        //删除日志
        logService.remove(new LambdaQueryWrapper<IplmManageLog>()
                .in(IplmManageLog::getIdIplManageMain, ids));
    }

    /**
     * 功能描述 日志和日志节点
     *
     * @param iplManageMain 主表对象
     * @return com.unity.innovation.entity.generated.IplManageMain 填充了日志和日志节点对象
     * @author gengzhiqiang
     * @date 2019/10/10 15:10
     */
    @Transactional(rollbackFor = Exception.class)
    public IplManageMain setLogs(IplManageMain iplManageMain) {
        IplManageMain vo = getById(iplManageMain.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("数据不存在").build();
        }
        //操作记录
        List<IplmManageLog> logList = logService.list(new LambdaQueryWrapper<IplmManageLog>()
                .eq(IplmManageLog::getIdRbacDepartment, vo.getIdRbacDepartmentDuty())
                .eq(IplmManageLog::getIdIplManageMain, vo.getId())
                .orderByDesc(IplmManageLog::getGmtCreate));
        //日志名称
        logList.forEach(p -> {
            if (WorkStatusAuditingStatusEnum.exist(p.getStatus())) {
                p.setStatusName(WorkStatusAuditingStatusEnum.of(p.getStatus()).getName());
            }
        });

        return iplManageMain;
    }

    /**
     * 功能描述  提交公共方法
     *
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/10/10 15:30
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(IplManageMain entity) {
        IplManageMain vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        List<Attachment> attachment = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, vo.getAttachmentCode()));
        if (CollectionUtils.isEmpty(attachment)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未上传领导签字文件").build();
        }
        if (!(WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getStatus())
                || WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getStatus()))) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("该状态下不可提交").build();
        }
        //待提交>>>>>待审核
        vo.setGmtSubmit(System.currentTimeMillis());
        vo.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
        updateById(vo);
        //日志记录
        IplmManageLog log = IplmManageLog.newInstance().build();
        log.setIdRbacDepartment(vo.getIdRbacDepartmentDuty());
        log.setIdIplManageMain(vo.getId());
        log.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
        log.setContent("提交发布需求");
        logService.save(log);
    }


    /**
     * 详情接口
     *
     * @param id 主键
     * @return com.unity.innovation.entity.generated.IplManageMain
     * @author JH
     * @date 2019/10/10 10:56
     */
    public IplManageMain detailIplManageMainById(Long id) {
        IplManageMain iplManageMain = super.getById(id);
        if (iplManageMain == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("数据不存在").build();
        }
        String attachmentCode = iplManageMain.getAttachmentCode();
        iplManageMain.setAttachments(attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, attachmentCode)));
        iplManageMain.setSnapShotList(GsonUtils.parse(iplManageMain.getSnapshot(), new TypeToken<List>() {}));
        iplManageMain.setSnapshot("");
        //操作记录
        List<IplmManageLog> logList = logService.list(new LambdaQueryWrapper<IplmManageLog>()
                .eq(IplmManageLog::getIdIplManageMain, id)
                .orderByDesc(IplmManageLog::getGmtCreate));
        logList.forEach(n -> {
            n.setStatusName(Objects.requireNonNull(WorkStatusAuditingProcessEnum.of(n.getStatus())).getName());
            n.setDepartmentName(InnovationUtil.getDeptNameById(n.getIdRbacDepartment()));
        });
        iplManageMain.setLogList(logList);
        //按状态进行分组,同时只取时间最小的那一条数据
        Map<Integer, IplmManageLog> map = logList.stream()
                .filter(n -> !WorkStatusAuditingStatusEnum.FORTY.getId().equals(n.getStatus()))
                .collect(Collectors.toMap(IplmManageLog::getStatus, Function.identity(), BinaryOperator.minBy(Comparator.comparingLong(IplmManageLog::getGmtCreate))));
        Set<Integer> statusSet = map.keySet();
        List<IplmManageLog> processNodeList = Lists.newArrayList();
        for (int status : statusSet) {
            IplmManageLog log = map.get(status);
            processNodeList.add(log);
        }
        iplManageMain.setProcessNodeList(processNodeList);
        return iplManageMain;
    }


    /**
     * 通过/驳回
     *
     * @param entity 实体
     * @param old    原有数据
     * @author JH
     * @date 2019/10/12 17:27
     */
    @Transactional(rollbackFor = Exception.class)
    public void passOrReject(IplManageMain entity, IplManageMain old) {
        //通过
        if (YesOrNoEnum.YES.getType() == entity.getPassOrReject()) {
            old.setStatus(WorkStatusAuditingStatusEnum.THIRTY.getId());
            //驳回
        } else {
            old.setStatus(WorkStatusAuditingStatusEnum.FORTY.getId());
        }
        super.updateById(old);
        //记录日志
        logService.saveLog(old.getIdRbacDepartmentDuty(), old.getStatus(), entity.getContent(), entity.getId());

    }

    /**
    * 将枚举转为list 提交单位下拉框
    *
    * @return java.util.List
    * @author JH
    * @date 2019/10/14 14:14
    */
    public  List<Map<String, String>> submitDepartmentList() {
        List<Map<String, String>> list = Lists.newArrayList();
        for (ListCategoryEnum listCategoryEnum : ListCategoryEnum.values()) {
            Map<String, String> map = new HashMap<>(16);
            map.put("code", listCategoryEnum.getName());
            map.put("name", InnovationUtil.getDeptNameById(listCategoryEnum.getId()));
            list.add(map);
        }
        return list;
    }
    /**
     * 根据提交单位字符串返回单位id
     *
     * @param entity 实体
     * @return java.lang.Long
     * @author JH
     * @date 2019/10/14 10:13
     */
    public  Long getDepartmentId(IplManageMain entity) {
        if(entity == null || StringUtils.isBlank(entity.getCategory())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("提交单位不能为空").build();
        }
        ListCategoryEnum listCategoryEnum = ListCategoryEnum.valueOfName(entity.getCategory());
        if(listCategoryEnum != null) {
            return listCategoryEnum.getId();
        }else {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("提交单位错误").build();

        }
    }

}
