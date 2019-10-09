
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.dao.IplSuggestionDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplSuggestion;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IplSuggestionService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-23 15:38:10
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplSuggestionServiceImpl extends BaseServiceImpl<IplSuggestionDao, IplSuggestion> {

    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private IplLogServiceImpl iplLogService;

    /**
     * 功能描述 分页接口
     *
     * @param search 查询条件
     * @return 分页集合
     * @author gengzhiqiang
     * @date 2019/9/23 15:54
     */
    public IPage<IplSuggestion> listByPage(PageEntity<IplSuggestion> search) {
        LambdaQueryWrapper<IplSuggestion> lqw = new LambdaQueryWrapper<>();
        //标题
        if (StringUtils.isNotBlank(search.getEntity().getTitle())) {
            lqw.like(IplSuggestion::getTitle, search.getEntity().getTitle());
        }
        //企业名称
        if (StringUtils.isNotBlank(search.getEntity().getEnterpriseName())) {
            lqw.like(IplSuggestion::getEnterpriseName, search.getEntity().getEnterpriseName());
        }
        //创建时间
        if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
            //gt 大于 lt 小于
            lqw.lt(IplSuggestion::getGmtCreate, end);
            lqw.gt(IplSuggestion::getGmtCreate, begin);
        }
        //来源
        if (search.getEntity().getSource() != null) {
            lqw.eq(IplSuggestion::getSource, search.getEntity().getSource());
        }
        //状态
        if (search.getEntity().getStatus() != null) {
            lqw.eq(IplSuggestion::getStatus, search.getEntity().getStatus());
        }
        //备注
        if (search.getEntity().getProcessStatus() != null) {
            lqw.eq(IplSuggestion::getProcessStatus, search.getEntity().getProcessStatus());
        }
        lqw.orderByDesc(IplSuggestion::getGmtModified);
        IPage<IplSuggestion> list = page(search.getPageable(), lqw);
        list.getRecords().forEach(is -> {
            //来源名称
            if (is.getSource() != null) {
                if (SourceEnum.ENTERPRISE.getId().equals(is.getSource())) {
                    is.setSourceName(SourceEnum.ENTERPRISE.getName());
                } else if (SourceEnum.SELF.getId().equals(is.getSource())) {
                    is.setSourceName("纪检组");
                }
            }
            //状态名称
            if (is.getStatus() != null) {
                is.setStatusName(IplStatusEnum.ofName(is.getStatus()));
            }
            //备注名称
            if (is.getProcessStatus() != null) {
                is.setProcessStatusName(ProcessStatusEnum.ofName(is.getProcessStatus()));
            }
        });
        return list;
    }

    /**
     * 功能描述 新增编辑接口
     *
     * @param entity 对象
     * @author gengzhiqiang
     * @date 2019/9/23 18:52
     */
    @Transactional(rollbackFor = Exception.class)
    public Long  saveEntity(IplSuggestion entity) {
        if (entity.getId() == null) {
            entity.setAttachmentCode(UUIDUtil.getUUID());
            // 状态设为处理中
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            //进展状态设为进展正常
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            //来源为纪检组
            entity.setSource(SourceEnum.SELF.getId());
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
            save(entity);
            return entity.getId();
        } else {
            IplSuggestion vo = getById(entity.getId());
            if (IplStatusEnum.DONE.getId().equals(vo.getStatus())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("处理完毕的数据不可编辑").build();
            }
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachmentList());
            //待处理时
            if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                if (ProcessStatusEnum.DEAL_OVERTIME.getId().equals(vo.getProcessStatus())){
                    vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                }
            }else if (IplStatusEnum.DEALING.getId().equals(vo.getStatus())) {
                //处理中 如果超时 则置为进展正常
                if (ProcessStatusEnum.UPDATE_OVERTIME.getId().equals(vo.getProcessStatus())){
                    vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                }
                IplLog iplLog = IplLog.newInstance().build();
                //纪检组 意见建议id
                iplLog.setIdIplMain(vo.getId());
                //处理中
                iplLog.setDealStatus(IplStatusEnum.DEALING.getId());
                //更新基本信息
                iplLog.setProcessInfo("更新基本信息");
                //主责单位
                Customer customer = LoginContextHolder.getRequestAttributes();
                if (customer.getIdRbacDepartment() != null) {
                    iplLog.setIdRbacDepartmentDuty(customer.getIdRbacDepartment());
                }
                //协同单位
                iplLog.setIdRbacDepartmentAssist(0L);
                //处理中的状态下 每次更新都记录日志
                iplLogService.save(iplLog);
            }
            updateById(entity);
            return entity.getId();
        }
    }

    /**
     * 功能描述 批量删除
     *
     * @param ids ids
     * @author gengzhiqiang
     * @date 2019/9/24 13:44
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeById(List<Long> ids) {
        List<IplSuggestion> list = list(new LambdaQueryWrapper<IplSuggestion>().in(IplSuggestion::getId, ids));
        //状态为处理完毕 不可删除
        List<IplSuggestion> doneList = list.stream()
                .filter(i -> IplStatusEnum.DONE.getId().equals(i.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(doneList)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("处理完毕的数据不可删除").build();
        }
        List<String> codes = list.stream().map(IplSuggestion::getAttachmentCode).collect(Collectors.toList());
        //附件表
        attachmentService.remove(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codes));
        //删除日志表
        Customer customer = LoginContextHolder.getRequestAttributes();
        iplLogService.remove(new LambdaQueryWrapper<IplLog>()
                .in(IplLog::getIdIplMain,ids)
                .eq(IplLog::getIdRbacDepartmentDuty,customer.getIdRbacDepartment()));
        //主表
        removeByIds(ids);
    }

    /**
     * 功能描述 详情接口
     *
     * @param entity 对象
     * @return IplSuggestion 对象
     * @author gengzhiqiang
     * @date 2019/9/17 16:03
     */
    public IplSuggestion detailById(IplSuggestion entity) {
        IplSuggestion vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //来源名称
        if (vo.getSource() != null) {
            if (SourceEnum.SELF.getId().equals(vo.getSource())) {
                vo.setSourceName("纪检组");
            } else if (SourceEnum.SELF.getId().equals(vo.getSource())) {
                vo.setSourceName(SourceEnum.ENTERPRISE.getName());
            }
        }
        if (StringUtils.isNotBlank(vo.getEnterpriseName())) {
            vo.setLogEnterpriseName(vo.getEnterpriseName());
        } else {
            vo.setLogEnterpriseName("未知单位");
        }
        Customer customer = LoginContextHolder.getRequestAttributes();
        //根据主表id和单位id 查询纪检组的所有日志信息
        List<IplLog> logList = iplLogService.list(new LambdaQueryWrapper<IplLog>()
                .eq(IplLog::getIdIplMain, vo.getId())
                .eq(IplLog::getIdRbacDepartmentDuty, customer.getIdRbacDepartment())
                .orderByDesc(IplLog::getGmtCreate));
        logList.forEach(log -> {
            if (log.getDealStatus() != null) {
                log.setStatusName(IplStatusEnum.ofName(log.getDealStatus()));
            }
        });
        //封装对象
        Map<String, Object> iplLogMap1 = new HashMap<>(16);
        iplLogMap1.put("department", vo.getLogEnterpriseName());
        iplLogMap1.put("processStatus", 3);
        IplLog l1 = IplLog.newInstance().build();
        l1.setGmtCreate(vo.getGmtCreate());
        List<IplLog> l1List = Lists.newArrayList();
        l1List.add(l1);
        iplLogMap1.put("logs", l1List);
        Map<String, Object> iplLogMap2 = new HashMap<>(16);
        iplLogMap2.put("department", "主责单位：纪检组");
        iplLogMap2.put("processStatus", vo.getStatus());
        l1.setGmtCreate(vo.getGmtCreate());
        iplLogMap2.put("logs", logList);
        List<Map<String, Object>> logListAll = Lists.newArrayList();
        logListAll.add(iplLogMap1);
        logListAll.add(iplLogMap2);
        vo.setTotalProcess(logListAll);
        //附件
        List<Attachment> attachmentList=attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode,vo.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)){
            vo.setAttachmentList(attachmentList);
        }
        return vo;
    }

    /**
     * 功能描述 处理接口
     * @param entity 对象
     * @author gengzhiqiang
     * @date 2019/9/24 16:06
     */
    public void dealById(IplSuggestion entity) {
        IplSuggestion vo = getById(entity.getId());
        if (IplStatusEnum.DONE.getId().equals(vo.getStatus())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("处理完毕的数据不可处理").build();
        }
        Customer customer = LoginContextHolder.getRequestAttributes();
        //保存日志信息 主表id 单位id 流程状态 处理进展
        //待处理 或者 处理中 时
        if (IplStatusEnum.UNDEAL.getId().equals(entity.getStatus()) || IplStatusEnum.DEALING.getId().equals(entity.getStatus())) {
            IplLog iplLog = IplLog.newInstance()
                    .idIplMain(entity.getId())
                    .idRbacDepartmentDuty(customer.getIdRbacDepartment())
                    .idRbacDepartmentAssist(0L)
                    .dealStatus(IplStatusEnum.DEALING.getId())
                    .processInfo(entity.getProcessMessage())
                    .build();
            iplLogService.save(iplLog);
            //更新主表状态
            vo.setStatus(IplStatusEnum.DEALING.getId());
            vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            updateById(vo);
            //处理完成
        } else if (IplStatusEnum.DONE.getId().equals(entity.getStatus())) {
            IplLog iplLog = IplLog.newInstance()
                    .idIplMain(entity.getId())
                    .idRbacDepartmentDuty(customer.getIdRbacDepartment())
                    .idRbacDepartmentAssist(0L)
                    .dealStatus(IplStatusEnum.DONE.getId())
                    .processInfo(entity.getProcessMessage())
                    .build();
            iplLogService.save(iplLog);
            //更新主表状态
            vo.setStatus(IplStatusEnum.DONE.getId());
            vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            updateById(vo);
        }

    }


}
