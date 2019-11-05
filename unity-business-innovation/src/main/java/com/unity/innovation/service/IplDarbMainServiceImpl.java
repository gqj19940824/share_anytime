package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.dao.IplDarbMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: IplDarbMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:36
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Slf4j
public class IplDarbMainServiceImpl extends BaseServiceImpl<IplDarbMainDao, IplDarbMain> {

    @Autowired
    private AttachmentServiceImpl attachmentService;
    @Autowired
    private IplLogServiceImpl iplLogService;
    @Autowired
    private IplAssistServiceImpl iplAssistService;
    @Autowired
    private RedisSubscribeServiceImpl redisSubscribeService;
    @Autowired
    private IplManageMainServiceImpl iplManageMainService;
    @Autowired
    private SysMessageHelpService sysMessageHelpService;
    @Autowired
    private DicUtils dicUtils;

    /**
     * 功能描述 详情接口
     *
     * @param id 管理表id
     * @return com.unity.innovation.entity.generated.IplManageMain 对象
     * @author gengzhiqiang
     * @date 2019/10/9 19:50
     */
    public IplManageMain detailByIdForPkg(Long id) {
        IplManageMain entity = iplManageMainService.getById(id);
        if (entity == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //快照集合
        List<IplDarbMain> list = JSON.parseArray(entity.getSnapshot(), IplDarbMain.class);
        entity.setSnapshot("");
        entity.setIplDarbMainList(list);
        //附件
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, entity.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            entity.setAttachments(attachmentList);
        }
        //日志集合 日志节点集合
        entity = iplManageMainService.setLogs(entity);
        return entity;
    }


    @Transactional(rollbackFor = Exception.class)
    public Long add(IplDarbMain entity) {

        // 保存附件
        List<Attachment> attachments = entity.getAttachments();
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachmentService.bachSave(entity.getAttachmentCode(), attachments);
        }
        save(entity);

        // 设置处理超时时间
        redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME, entity.getIdRbacDepartmentDuty(), entity.getBizType());

        //====发改局====企业新增填报实时清单需求========
        if(entity.getSource().equals(SourceEnum.ENTERPRISE.getId())){
            //企业需求填报才进行系统通知
            sysMessageHelpService.addInventoryMessage(InventoryMessage.newInstance()
                    .sourceId(entity.getId())
                    .idRbacDepartment(entity.getIdRbacDepartmentDuty())
                    .dataSourceClass(SysMessageDataSourceClassEnum.COOPERATION.getId())
                    .flowStatus(SysMessageFlowStatusEnum.ONE.getId())
                    .title(entity.getEnterpriseName())
                    .bizType(BizTypeEnum.CITY.getType())
                    .build());
        }
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(IplDarbMain entity) {
        // 保存附件
        Long idIplMain = entity.getId();
        IplDarbMain byId = getById(idIplMain);
        if (byId == null) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        List<Attachment> attachments = entity.getAttachments();
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachmentService.updateAttachments(byId.getAttachmentCode(), attachments);
        }

        // 保存修改
        entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
        Integer status = byId.getStatus();
        if (IplStatusEnum.DEALING.getId().equals(status)) {
            // 非"待处理"状态才记录日志，该字段与日志处理相同
            entity.setLatestProcess("更新基本信息");
        }
        updateById(entity);

        // 设置处理超时时间
        if (IplStatusEnum.UNDEAL.getId().equals(status)) {
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME, entity.getIdRbacDepartmentDuty(), entity.getBizType());
            // 设置更新超时时间
        } else if (IplStatusEnum.DEALING.getId().equals(status)) {
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.UPDATE_OVER_TIME, entity.getIdRbacDepartmentDuty(), entity.getBizType());

            // 非"待处理"状态才记录日志
            Integer lastDealStatus = iplLogService.getLastDealStatus(idIplMain, BizTypeEnum.CITY.getType());
            IplLog iplLog = IplLog.newInstance().idIplMain(idIplMain).idRbacDepartmentAssist(0L).bizType(BizTypeEnum.CITY.getType())
                    .processInfo("更新基本信息").idRbacDepartmentDuty(entity.getIdRbacDepartmentDuty()).dealStatus(lastDealStatus).build();
            iplLogService.save(iplLog);
            //======处理中的数据，主责单位再次编辑基本信息--清单协同处理--增加系统消息=======
            List<IplAssist> assists = iplAssistService.getAssists(entity.getBizType(), entity.getId());
            List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
            sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                    .sourceId(entity.getId())
                    .idRbacDepartment(byId.getIdRbacDepartmentDuty())
                    .dataSourceClass(SysMessageDataSourceClassEnum.HELP.getId())
                    .flowStatus(SysMessageFlowStatusEnum.FOUR.getId())
                    .title(entity.getEnterpriseName())
                    .helpDepartmentIdList(assistsIdList)
                    .bizType(BizTypeEnum.CITY.getType())
                    .build());
        }
    }

    /**
     * 批量删除发改局数据
     *
     * @param mainIds 发改局表ids
     * @author qinhuan
     * @since 2019-10-09 16:27
     */
    @Transactional(rollbackFor = Exception.class)
    public void delByIds(List<Long> mainIds) {
        List<IplDarbMain> list = this.list(new LambdaQueryWrapper<IplDarbMain>().in(IplDarbMain::getId, mainIds));
        if (CollectionUtils.isNotEmpty(list)) {
            //======处理中的数据，主责单位删除--清单协同处理--增加系统消息=======
            list.forEach(entity -> {
                List<IplAssist> assists = iplAssistService.getAssists(entity.getBizType(), entity.getId());
                List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
                sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                        .sourceId(entity.getId())
                        .idRbacDepartment(entity.getIdRbacDepartmentDuty())
                        .dataSourceClass(SysMessageDataSourceClassEnum.HELP.getId())
                        .flowStatus(SysMessageFlowStatusEnum.FIVES.getId())
                        .title(entity.getEnterpriseName())
                        .helpDepartmentIdList(assistsIdList)
                        .bizType(BizTypeEnum.CITY.getType())
                        .build());
            });
            //附件处理
            List<String> attachmentCodes = new ArrayList<>();
            list.forEach(e -> {
                attachmentCodes.add(e.getAttachmentCode());
            });
            // 删除主表
            removeByIds(mainIds);
            // 批量删除主表附带的日志、协同、附件，调用方法必须要有事物
            iplAssistService.batchDel(mainIds, list, attachmentCodes, BizTypeEnum.CITY.getType());
        }
    }
}