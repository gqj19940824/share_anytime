
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.MessageSaveFormEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.*;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.HashRedisUtils;
import com.unity.innovation.constants.MessageConstants;
import com.unity.innovation.dao.SysMessageDao;
import com.unity.innovation.entity.SysMessage;
import com.unity.innovation.entity.SysMessageReadLog;
import com.unity.innovation.enums.SysMessageDataSourceClassEnum;
import com.unity.innovation.enums.SysMessageSendTypeEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * ClassName: SysMessageService
 * date: 2019-09-23 09:39:17
 *
 * @author G
 * @since JDK 1.8
 */
@Service
public class SysMessageServiceImpl extends BaseServiceImpl<SysMessageDao, SysMessage> {

    @Resource
    SysMessageReadLogServiceImpl sysMessageReadLogService;
    @Resource
    DicUtils dicUtils;
    @Resource
    RbacClient rbacClient;
    @Resource
    HashRedisUtils hashRedisUtils;

    /**
     * 分页列表
     *
     * @param pageEntity 分页参数
     * @return 列表数据
     * @author gengjiajia
     * @since 2019/09/23 09:56
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<SysMessage> pageEntity) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("userId", customer.getId());
        SysMessage entity = pageEntity.getEntity();
        if (entity != null) {
            paramMap.put("dataSourceClass", entity.getDataSourceClass());
            paramMap.put("startTime", entity.getStartTime());
            paramMap.put("endTime", entity.getEndTime());
            paramMap.put("title", StringUtils.isBlank(entity.getTitle()) ? null : entity.getTitle().trim());
        }
        Page<SysMessage> page = pageEntity.getPageable();
        paramMap.put("offset", (page.getCurrent() - 1) * page.getSize());
        paramMap.put("limit", page.getSize());
        long total = baseMapper.countListTotalByParam(paramMap);
        if (total > 0) {
            List<SysMessage> messageList = baseMapper.findPageListByParam(paramMap);
            return PageElementGrid.<Map<String, Object>>newInstance()
                    .total(total)
                    .items(convert2List(messageList))
                    .build();
        } else {
            return PageElementGrid.<Map<String, Object>>newInstance()
                    .total(total)
                    .items(Lists.newArrayList())
                    .build();
        }
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return List Map
     */
    private List<Map<String, Object>> convert2List(List<SysMessage> list) {
        return JsonUtil.ObjectToList(list,
                this::adapterField
                , SysMessage::getId, SysMessage::getTitle, SysMessage::getSourceId, SysMessage::getSendType, SysMessage::getNotes,
                SysMessage::getIdRbacDepartment, SysMessage::getIsRead, SysMessage::getDataSourceClass
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, SysMessage entity) {
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
        SysMessageDataSourceClassEnum sourceClassEnum = SysMessageDataSourceClassEnum.of(entity.getDataSourceClass());
        m.put("dataSourceClassTitle", sourceClassEnum != null ? sourceClassEnum.getName() : "");
    }

    /**
     * 删除消息
     *
     * @param messageId 消息id
     * @author gengjiajia
     * @since 2019/09/24 19:01
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long messageId) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        this.removeById(messageId);
        sysMessageReadLogService.remove(new LambdaQueryWrapper<SysMessageReadLog>()
                .eq(SysMessageReadLog::getMessageId, messageId)
                .eq(SysMessageReadLog::getTargetUserId, customer.getId()));
    }

    /**
     * 设置消息已读状态
     *
     * @param id 消息id
     * @author gengjiajia
     * @since 2019/09/23 11:27
     */
    @Transactional(rollbackFor = Exception.class)
    public void setMessageReadStatus(Long id) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        SysMessageReadLog log = sysMessageReadLogService.getOne(new LambdaQueryWrapper<SysMessageReadLog>()
                .eq(SysMessageReadLog::getMessageId, id)
                .eq(SysMessageReadLog::getTargetUserId, customer.getId()));
        if (log == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("消息数据不存在")
                    .build();
        }
        if (log.getIsRead().equals(YesOrNoEnum.NO.getType())) {
            log.setIsRead(YesOrNoEnum.YES.getType());
            sysMessageReadLogService.updateById(log);
        }
        // webSocket 提醒数量 -1
        sysMessageReadLogService.updateMessageNumToUserIdList(MessageSaveFormEnum.SYS_MSG.getId(), Arrays.asList(customer.getId()), YesOrNoEnum.NO.getType());
    }

    /**
     * 新增 实时清单系统消息
     *
     * @param msg 包含 源数量id、单位id、企业名称、数据归属单位类型、流程状态
     * @author gengjiajia
     * @since 2019/09/23 14:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void addInventoryMessage(InventoryMessage msg) {
        //保存系统消息
        String title = MessageConstants.addInventoryMsgTitleMap.get(msg.getDataSourceClass().toString().concat(msg.getFlowStatus().toString()));
        if (StringUtils.isBlank(title)) {
            return;
        }
        title = title.replace(MessageConstants.TITLE, msg.getTitle());
        if (StringUtils.isNotBlank(msg.getTime())) {
            title = title.replace(MessageConstants.TIME, msg.getTime());
        }
        Long messageId = saveMessage(msg.getSourceId(), title, msg.getIdRbacDepartment(), msg.getDataSourceClass());
        //根据数据归属单位类型获取对应角色下人员并保存发送记录
        saveMessageLogByRole(MessageConstants.REALTIME_INVENTORY_ROLES,
                MessageConstants.inventoryDataSourceClassToRoleMap.get(msg.getDataSourceClass()), messageId);
    }

    /**
     * 新增 清单协同处理的系统消息
     *
     * @param msg 包含 源数量id、单位id、企业名称、数据归属单位类型、流程状态、协同单位id集
     * @author gengjiajia
     * @since 2019/09/23 16:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void addInventoryHelpMessage(InventoryMessage msg) {
        //保存系统消息
        String title = MessageConstants.addInventoryHelpMsgTitleMap.get(msg.getDataSourceClass().toString().concat(msg.getFlowStatus().toString()));
        if (StringUtils.isBlank(title)) {
            return;
        }
        String depName = hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT.concat(msg.getIdRbacDepartment().toString()), RedisConstants.NAME);
        title = title.replace(MessageConstants.DEP_NAME, depName).replace(MessageConstants.TITLE, msg.getTitle());
        if (StringUtils.isNotBlank(msg.getTime())) {
            title = title.replace(MessageConstants.TIME, msg.getTime());
        }
        Long idRbacDepartment = msg.getIdRbacDepartment();
        Long messageId = saveMessage(msg.getSourceId(), title, idRbacDepartment, msg.getDataSourceClass());
        //根据协同单位单位id集获取对应单位下人员并保存发送记录
        List<Long> userIdList = rbacClient.getUserIdListByDepIdList(msg.getHelpDepartmentIdList());
        saveMessageLog(messageId, userIdList);
        // webSocket 目标用户 提醒数量 +1
        sysMessageReadLogService.updateMessageNumToUserIdList(MessageSaveFormEnum.SYS_MSG.getId(), userIdList, YesOrNoEnum.YES.getType());
    }

    /**
     * 新增 发布审核流程的系统消息
     *
     * @param msg 包含消息数据
     * @author gengjiajia
     * @since 2019/09/25 16:20
     */
    @Transactional(rollbackFor = Exception.class)
    public void addReviewMessage(ReviewMessage msg) {
        String title = MessageConstants.reviewMsgTitleMap.get(msg.getFlowStatus().toString());
        //提交审核 标题包含单位名称
        if (msg.getFlowStatus().equals(YesOrNoEnum.YES.getType())) {
            String depName = hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT.concat(msg.getIdRbacDepartment().toString()), RedisConstants.NAME);
            title = title.replace(MessageConstants.DEP_NAME, depName);
        }
        title = title.replace(MessageConstants.TITLE, msg.getTitle());
        Long messageId = saveMessage(msg.getSourceId(), title, msg.getIdRbacDepartment(), msg.getDataSourceClass());
        //根据数据归属单位类型获取对应角色下人员并保存发送记录
        if (msg.getFlowStatus().equals(YesOrNoEnum.YES.getType())) {
            saveMessageLogByRole(MessageConstants.REALTIME_INVENTORY_ROLES,
                    MessageConstants.reviewDataSourceClassToRoleMap.get(msg.getFlowStatus()), messageId);
        } else {
            //发布流程
            //获取提交单位下属人员ID列表
            List<Long> depIdList = Arrays.asList(msg.getIdRbacDepartment());
            List<Long> userIdList = rbacClient.getUserIdListByDepIdList(depIdList);
            saveMessageLog(messageId, userIdList);
            // webSocket 目标用户 提醒数量 +1
            sysMessageReadLogService.updateMessageNumToUserIdList(MessageSaveFormEnum.SYS_MSG.getId(), userIdList, YesOrNoEnum.YES.getType());
        }
    }

    /**
     * 保存消息
     *
     * @param sourceId           源数据id
     * @param title              消息内容
     * @param depId              单位id
     * @param getDataSourceClass 数据所属单位类型
     * @return 消息id
     * @author gengjiajia
     * @since 2019/09/23 18:45
     */
    private Long saveMessage(Long sourceId, String title, Long depId, Integer getDataSourceClass) {
        SysMessage message = new SysMessage();
        message.setSourceId(sourceId);
        message.setSendType(SysMessageSendTypeEnum.ONE.getId());
        message.setTitle(title);
        message.setIdRbacDepartment(depId);
        message.setDataSourceClass(getDataSourceClass);
        this.save(message);
        return message.getId();
    }

    /**
     * 保存消息发送记录
     *
     * @param messageId  消息id
     * @param userIdList 用户id列表
     * @author gengjiajia
     * @since 2019/09/23 20:11
     */
    private void saveMessageLog(Long messageId, List<Long> userIdList) {
        List<SysMessageReadLog> logList = userIdList.stream().map(userId -> {
            SysMessageReadLog log = new SysMessageReadLog();
            log.setIsRead(YesOrNoEnum.NO.getType());
            log.setMessageId(messageId);
            log.setTargetUserId(userId);
            return log;
        }).collect(Collectors.toList());
        sysMessageReadLogService.saveBatch(logList);
    }

    /**
     * 根据角色获取对应用户来保存发送记录
     *
     * @param groupCode 角色字典组名
     * @param itemCode  角色字典项名
     * @param messageId 消息id
     * @author gengjiajia
     * @since 2019/09/25 17:17
     */
    private void saveMessageLogByRole(String groupCode, String itemCode, Long messageId) {
        Dic dic = dicUtils.getDicByCode(groupCode, itemCode);
        if (dic != null) {
            String value = dic.getDicValue();
            String[] ids = value.split(ConstString.SPLIT_COMMA);
            Long[] idArr = (Long[]) ConvertUtils.convert(ids, Long.class);
            List<Long> idList = Arrays.asList(idArr);
            //通过角色id换取角色关联的 目标用户id
            List<Long> userIdList = rbacClient.getUserIdListByRoleIdList(idList);
            saveMessageLog(messageId, userIdList);
            // webSocket 目标用户 提醒数量 +1
            sysMessageReadLogService.updateMessageNumToUserIdList(MessageSaveFormEnum.SYS_MSG.getId(), userIdList, YesOrNoEnum.YES.getType());
        }
    }
}
