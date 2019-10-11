package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.dao.IplManageMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.*;
import com.unity.innovation.enums.IplmStatusEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ClassName: IplManageMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:37
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplManageMainServiceImpl extends BaseServiceImpl<IplManageMainDao, IplManageMain> {

    @Autowired
    private IplDarbMainSnapshotServiceImpl iplDarbMainSnapshotService;
    @Autowired
    private IplDarbMainServiceImpl iplDarbMainService;
    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Resource
    private IplmManageLogServiceImpl logService;

    @Transactional(rollbackFor = Exception.class)
    public void add(IplManageMain entity) {

        String uuid = UUIDUtil.getUUID();
        // 保存发改局
        List<Long> idiplDarbMains = entity.getIdiplDarbMains();
        if (CollectionUtils.isNotEmpty(idiplDarbMains)){
            LambdaQueryWrapper<IplDarbMain> lq = new LambdaQueryWrapper();
            lq.in(IplDarbMain::getId, idiplDarbMains);
            List<IplDarbMain> iplDarbMains = iplDarbMainService.list(lq);
            List<IplDarbMainSnapshot> iplDarbMainSnapshots = new ArrayList<>();
            if (CollectionUtils.isEmpty(iplDarbMains)){
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
            }
            // 复制snapshot   TODOset主表id
            iplDarbMains.forEach(e->iplDarbMainSnapshots.add(InnovationUtil.Copy(e, IplDarbMainSnapshot.newInstance().build())));
            iplDarbMainSnapshotService.saveBatch(iplDarbMainSnapshots);

            // 保存管理主表
            entity.setAttachmentCode(uuid);
            entity.setStatus(IplmStatusEnum.UNCOMMIT.getId());
            save(entity);

            // 保存关联表
            iplDarbMainSnapshots.forEach(e->{
                IplmMainIplMain.newInstance().idIplMain(e.getId()).idIplmMain(entity.getId()).idRbacDepartmentDuty(e.getIdRbacDepartmentDuty());
            });
        }

        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if(CollectionUtils.isNotEmpty(attachments)){
            attachmentService.bachSave(uuid, attachments);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(IplManageMain entity) {

        String uuid = entity.getAttachmentCode();
        // 保存发改局
        List<Long> idiplDarbMains = entity.getIdiplDarbMains();
        if (CollectionUtils.isNotEmpty(idiplDarbMains)){
            LambdaQueryWrapper<IplDarbMain> lq = new LambdaQueryWrapper();
            lq.in(IplDarbMain::getId, idiplDarbMains);
            List<IplDarbMain> iplDarbMains = iplDarbMainService.list(lq);
            List<IplDarbMainSnapshot> iplDarbMainSnapshots = new ArrayList<>();
            if (CollectionUtils.isEmpty(iplDarbMains)){
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
            }
            // 复制snapshot
            iplDarbMains.forEach(e->iplDarbMainSnapshots.add(InnovationUtil.Copy(e, IplDarbMainSnapshot.newInstance().build())));
            iplDarbMainSnapshotService.saveBatch(iplDarbMainSnapshots);

            // 保存管理主表
            entity.setAttachmentCode(uuid);
            entity.setStatus(IplmStatusEnum.UNCOMMIT.getId());
            save(entity);

            // 保存关联表
            iplDarbMainSnapshots.forEach(e->{
                IplmMainIplMain.newInstance().idIplMain(e.getId()).idIplmMain(entity.getId()).idRbacDepartmentDuty(e.getIdRbacDepartmentDuty());
            });
        }

        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if(CollectionUtils.isNotEmpty(attachments)){
            attachmentService.bachSave(uuid, attachments);
        }
    }

    /**
     * 功能描述 公共分页接口
     * @param search 查询条件
     * @return  分页集合
     * @author gengzhiqiang
     * @date 2019/10/9 16:47
     */
    public IPage<IplManageMain> listForPkg(PageEntity<IplManageMain> search,Long department) {
        LambdaQueryWrapper<IplManageMain> lqw = new LambdaQueryWrapper<>();
        if (search != null) {
            //提交时间
            if (StringUtils.isNotBlank(search.getEntity().getSubmitTime())) {
                //gt 大于 lt 小于
                long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), true);
                lqw.gt(IplManageMain::getGmtSubmit, begin);
                //gt 大于 lt 小于
                long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), false);
                lqw.lt(IplManageMain::getGmtSubmit, end);
            }
            //状态
            if (search.getEntity().getStatus() != null) {
                lqw.eq(IplManageMain::getStatus, search.getEntity().getStatus());
            }
        }
        //各局
        lqw.eq(IplManageMain::getIdRbacDepartmentDuty, department);
        //排序
        lqw.orderByDesc(IplManageMain::getGmtSubmit, IplManageMain::getGmtModified);
        IPage<IplManageMain> list = page(search.getPageable(), lqw);
        if (CollectionUtils.isNotEmpty(list.getRecords())) {
            list.getRecords().forEach(p -> {
                if (p.getStatus() != null) {
                    if (WorkStatusAuditingStatusEnum.exist(p.getStatus())) {
                        p.setStatusName(WorkStatusAuditingStatusEnum.of(p.getStatus()).getName());
                    }
                }
            });
        }
        return list;
    }


    /**
     * 功能描述 新增编辑
     * @param entity 对象
     * @param department 四大单位
     * @author gengzhiqiang
     * @date 2019/10/9 16:48
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateForPkg(IplManageMain entity, Long department) {
        if (entity.getId() == null) {
            //新增
            entity.setAttachmentCode(UUIDUtil.getUUID().replace("-", ""));
            //附件
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachments());
            //待提交
            entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
            //提交时间设置最大
            entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);
            //快照数据 根据不同单位 切换不同vo
            if (InnovationConstant.DEPARTMENT_ESB_ID.equals(department)) {
                entity.setSnapshot(JSON.toJSONString(entity.getIplEsbMainList()));
            }
            entity.setSnapshot(JSON.toJSONString(entity.getIplEsbMainList()));
            //各局
            entity.setIdRbacDepartmentDuty(department);
            //保存
            save(entity);
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
            //快照数据 根据不同单位 切换不同vo
            if (InnovationConstant.DEPARTMENT_ESB_ID.equals(department)) {
                entity.setSnapshot(JSON.toJSONString(entity.getIplEsbMainList()));
            }
            //附件
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachments());
            //修改信息
            updateById(entity);
        }
    }


    /**
     * 功能描述 删除包接口
     * @param ids id集合
     * @author gengzhiqiang
     * @date 2019/10/10 13:47
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdsForPkg(List<Long> ids,Long department) {
        List<IplManageMain> list = list(new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, ids));
        //状态为处理完毕 不可删除
        List<Integer> stateList = com.google.common.collect.Lists.newArrayList();
        stateList.add(WorkStatusAuditingStatusEnum.TEN.getId());
        stateList.add(WorkStatusAuditingStatusEnum.FORTY.getId());
        List<IplManageMain> list1 = list.stream().filter(l -> stateList.contains(l.getStatus())).collect(Collectors.toList());
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
                .eq(IplmManageLog::getIdRbacDepartment, department)
                .in(IplmManageLog::getIdIplManageMain, ids));
    }

    /**
     * 功能描述 日志和日志节点
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
     * 功能描述  提交公共方法
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/10/10 15:30
     */
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
}
