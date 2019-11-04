
package com.unity.innovation.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.UserVO;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constant.SmsConstants;
import com.unity.common.enums.MessageSaveFormEnum;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.*;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.AliSmsUtils;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.HashRedisUtils;
import com.unity.innovation.constants.MessageConstants;
import com.unity.innovation.dao.SysMessageDao;
import com.unity.innovation.entity.SysMessage;
import com.unity.innovation.entity.SysMessageReadLog;
import com.unity.innovation.entity.SysSendSmsLog;
import com.unity.innovation.enums.BizTypeEnum;
import com.unity.innovation.enums.SysMessageDataSourceClassEnum;
import com.unity.innovation.enums.SysMessageFlowStatusEnum;
import com.unity.innovation.enums.SysMessageSendTypeEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
@Slf4j
@Service
public class SysMessageServiceImpl extends BaseServiceImpl<SysMessageDao, SysMessage> {

    @Resource
    SysMessageReadLogServiceImpl sysMessageReadLogService;
    @Resource
    SysSendSmsLogServiceImpl sysSendSmsLogService;
    @Resource
    HashRedisUtils hashRedisUtils;
    @Resource
    AliSmsUtils aliSmsUtils;
    @Resource
    RbacClient rbacClient;
    @Resource
    DicUtils dicUtils;

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
        paramMap.put("userId", customer.getIsAdmin().equals(YesOrNoEnum.YES.getType()) ? null : customer.getId());
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
        m.put("dataSourceClassTitle", SysMessageDataSourceClassEnum.ofName(entity.getDataSourceClass()));
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
        int count = sysMessageReadLogService.count(new LambdaQueryWrapper<SysMessageReadLog>()
                .eq(SysMessageReadLog::getMessageId, messageId)
                .eq(SysMessageReadLog::getTargetUserId, customer.getId())
                .eq(SysMessageReadLog::getIsRead, YesOrNoEnum.NO.getType()));
        if(count > 0){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("操作错误")
                    .build();
        }
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
            // webSocket 提醒数量 -1
            sysMessageReadLogService.updateMessageNumToUserIdList(MessageSaveFormEnum.SYS_MSG.getId(), Arrays.asList(customer.getId()), YesOrNoEnum.NO.getType());
        }
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
        String title = MessageConstants.addInventoryMsgTitleMap.get(msg.getBizType().toString().concat(msg.getFlowStatus().toString()));
        if (StringUtils.isBlank(title)) {
            log.error("======《addInventoryMessage》--新增实时清单推送系统消息未获取到消息标题，bizType {}，flowStatus {}",msg.getBizType(),msg.getFlowStatus());
            return;
        }
        title = title.replace(MessageConstants.TITLE, StringUtils.isEmpty(msg.getTitle()) ? "未知企业" : msg.getTitle());
        if (StringUtils.isNotBlank(msg.getTime())) {
            title = title.replace(MessageConstants.TIME, msg.getTime());
        }
        //获取主责单位下用户进行系统消息及短信的发送
        List<UserVO> userList = rbacClient.getUserListByDepIdList(Arrays.asList(msg.getIdRbacDepartment()));
        if (CollectionUtils.isEmpty(userList)) {
            log.error("======《addInventoryMessage》--新增实时清单推送系统消息未获取到指定单位下用户，单位id {}",msg.getIdRbacDepartment());
            return;
        }
        //保存系统通知并推送
        saveAndSendMessage(userList, msg.getSourceId(), msg.getIdRbacDepartment(),
                msg.getDataSourceClass(), msg.getFlowStatus(), title);
        String smsTitle = MessageConstants.sendSmsContentMap.get(msg.getBizType().toString()
                .concat(msg.getFlowStatus().toString()));
        smsTitle = smsTitle.replace(MessageConstants.TITLE, StringUtils.isEmpty(msg.getTitle()) ? "未知企业" : msg.getTitle());
        //发送短信
        saveAndSendSms(msg, userList, smsTitle, "");
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
        String title = MessageConstants.addInventoryHelpMsgTitleMap.get(msg.getBizType().toString().concat(msg.getFlowStatus().toString()));
        if (StringUtils.isBlank(title)) {
            log.error("======《addInventoryHelpMessage》--新增清单协同处理推送系统消息未获取到消息标题，bizType {}，flowStatus {}",msg.getBizType(),msg.getFlowStatus());
            return;
        }
        String smsTitle = MessageConstants.sendHelpSmsContentMap.get(msg.getBizType().toString()
                .concat(msg.getFlowStatus().toString()));
        String depName = hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT.concat(msg.getIdRbacDepartment().toString()), RedisConstants.NAME);
        title = title.replace(MessageConstants.DEP_NAME, depName).replace(MessageConstants.TITLE, msg.getTitle());
        if (StringUtils.isNotBlank(msg.getTime())) {
            title = title.replace(MessageConstants.TIME, msg.getTime());
        }
        if (StringUtils.isNotBlank(smsTitle)) {
            smsTitle = smsTitle.replace(MessageConstants.DEP_NAME, depName).replace(MessageConstants.TITLE, msg.getTitle());
        }

        //获取主责单位下用户进行系统消息及短信的发送
        List<UserVO> userList = rbacClient.getUserListByDepIdList(msg.getHelpDepartmentIdList());
        if (CollectionUtils.isNotEmpty(userList)) {
            //保存系统通知并推送
            saveAndSendMessage(userList, msg.getSourceId(), msg.getIdRbacDepartment(),
                    msg.getDataSourceClass(), msg.getFlowStatus(), title);
            //发送短信
            saveAndSendSms(msg, userList, smsTitle, depName);
        } else {
            log.error("======《addInventoryHelpMessage》--实时清单新增清单协同处理推送系统消息未获取到指定单位下用户，单位id {}",msg.getIdRbacDepartment());
        }
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
        //根据数据归属单位类型获取对应角色下人员并保存发送记录
        List<UserVO> userList;
        if (msg.getFlowStatus().equals(YesOrNoEnum.YES.getType())) {
            //工作动态发布审核/清单发布审核/企业信息发布审核 这三种情况要发送短信到宣传部对应角色下
            Dic dic = dicUtils.getDicByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE);
            if(dic == null || StringUtils.isEmpty(dic.getDicValue())){
                log.error("======《addReviewMessage》--发布审核流程推送系统消息未获取到字典组配置的宣传部B角色======");
                return;
            }
            userList = rbacClient.getUserListByRoleIdList(Arrays.asList(Long.parseLong(dic.getDicValue())));
        } else {
            //发布流程 获取提交单位下属人员ID列表
            //获取主责单位下用户进行系统消息及短信的发送
            userList = rbacClient.getUserListByDepIdList(Arrays.asList(msg.getIdRbacDepartment()));

        }
        List<Long> userIdList = userList.stream()
                .filter(u -> u.getReceiveSysMsg().equals(YesOrNoEnum.YES.getType()))
                .map(UserVO::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            log.error("======《addReviewMessage》--发布审核流程推送系统消息未获取到指定单位下用户，单位id {}",msg.getIdRbacDepartment());
            return;
        }
        Long messageId = saveMessage(msg.getSourceId(), title, msg.getIdRbacDepartment(),
                msg.getDataSourceClass(), msg.getFlowStatus());
        saveMessageLog(messageId, userIdList);
        // webSocket 目标用户 提醒数量 +1
        sysMessageReadLogService.updateMessageNumToUserIdList(MessageSaveFormEnum.SYS_MSG.getId(), userIdList, YesOrNoEnum.YES.getType());
    }

    /**
     * 根据目标用户保存发送记录
     *
     * @param userList  目标用户
     * @param sourceId  源数据id
     * @param title     消息内容
     * @param depId     单位id
     * @param dataClass 数据所属单位类型
     * @param status    流程状态
     * @author gengjiajia
     * @since 2019/09/25 17:17
     */
    private void saveAndSendMessage(List<UserVO> userList, Long sourceId, Long depId, Integer dataClass, Integer status, String title) {
        List<Long> userIdList = userList.stream()
                .filter(u -> u.getReceiveSysMsg().equals(YesOrNoEnum.YES.getType()))
                .map(UserVO::getId)
                .collect(Collectors.toList());
        Long messageId = saveMessage(sourceId, title, depId, dataClass, status);
        saveMessageLog(messageId, userIdList);
        // webSocket 目标用户 提醒数量 +1
        sysMessageReadLogService.updateMessageNumToUserIdList(MessageSaveFormEnum.SYS_MSG.getId(), userIdList, YesOrNoEnum.YES.getType());

    }

    /**
     * 保存消息
     *
     * @param sourceId           源数据id
     * @param title              消息内容
     * @param depId              单位id
     * @param getDataSourceClass 数据所属单位类型
     * @param flowStatus         流程状态
     * @return 消息id
     * @author gengjiajia
     * @since 2019/09/23 18:45
     */
    private Long saveMessage(Long sourceId, String title, Long depId, Integer getDataSourceClass, Integer flowStatus) {
        SysMessage message = new SysMessage();
        message.setSourceId(sourceId);
        message.setSendType(SysMessageSendTypeEnum.ONE.getId());
        message.setTitle(title);
        message.setIdRbacDepartment(depId);
        message.setDataSourceClass(getDataSourceClass);
        message.setFlowStatus(flowStatus);
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
     * 获取当前用户的未读消息数
     *
     * @return 未读消息数
     * @author gengjiajia
     * @since 2019/09/30 09:10
     */
    public Map<String, Object> getMessageNumByCustomer() {
        Map<String, Object> numMap = Maps.newHashMap();
        Customer customer = LoginContextHolder.getRequestAttributes();
        //判断当前用户是否拥有宣传部a的角色
        Dic pdA = dicUtils.getDicByCode(DicConstants.ROLE_GROUP, DicConstants.PD_A_ROLE);
        Dic pdB = dicUtils.getDicByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE);
        if(customer.getRoleList().contains(Long.parseLong(pdA.getDicValue()))
                || customer.getRoleList().contains(Long.parseLong(pdB.getDicValue()))){
            numMap.put("isAdd", 0);
            numMap.put("sysMessageNum", 0);
            numMap.put("noticeNum", 0);
            return numMap;
        }
        Map<String, Object> sysMegNumMap = hashRedisUtils.getObj(MessageSaveFormEnum.SYS_MSG.getName());
        Map<String, Object> noticeMegNumMap = hashRedisUtils.getObj(MessageSaveFormEnum.NOTICE.getName());
        //获取所有消息数量，判断是否有属于当前人的消息
        int sysMessageNum = 0;
        int noticeNum = 0;
        if (MapUtils.isNotEmpty(sysMegNumMap)) {
            Object numBySysObj = sysMegNumMap.get(customer.getId().toString());
            int numBySys = numBySysObj != null ? Integer.parseInt(numBySysObj.toString()) : 0;
            sysMessageNum += numBySys;
        }

        if (MapUtils.isNotEmpty(noticeMegNumMap)) {
            Object numByNoticeObj = noticeMegNumMap.get(customer.getId().toString());
            int numByNotice = numByNoticeObj == null ? 0 : Integer.parseInt(numByNoticeObj.toString());
            noticeNum += numByNotice;
        }
        numMap.put("isAdd", sysMessageNum == 0 && noticeNum == 0 ? YesOrNoEnum.NO.getType() : YesOrNoEnum.YES.getType());
        numMap.put("sysMessageNum", sysMessageNum);
        numMap.put("noticeNum", noticeNum);
        return numMap;
    }

    /**
     * 保存并发送短信
     *
     * @param msg      包含消息数据
     * @param userList 包含目标用户
     * @author gengjiajia
     * @since 2019/10/17 20:56
     */
    private void saveAndSendSms(InventoryMessage msg, List<UserVO> userList, String title, String depName) {
        //判断 新增实时清单、新增协同单位、处理中→处理完毕、处理完毕→处理中 这四种状态下发送短信
        if (SysMessageFlowStatusEnum.ONE.getId().equals(msg.getFlowStatus())
                || SysMessageFlowStatusEnum.SIX.getId().equals(msg.getFlowStatus())
                || SysMessageFlowStatusEnum.SEVEN.getId().equals(msg.getFlowStatus())) {
            // 组装短信模板参数体（json格式）及短信内容  示例： {\"code\":\""+ code +"\"}
            String smsParem;
            if (SysMessageFlowStatusEnum.ONE.getId().equals(msg.getFlowStatus())
                    && !SysMessageDataSourceClassEnum.HELP.getId().equals(msg.getDataSourceClass())) {
                //说明是企业填报需求，短信模板需要企业名称和模块名称两个参数
                smsParem = "{\"enterpriseName\":\"【"+msg.getTitle()+"】\",\"menuName\":\"【"+SysMessageDataSourceClassEnum.ofName(msg.getDataSourceClass())+"】\"}";
            } else if (SysMessageFlowStatusEnum.SIX.getId().equals(msg.getFlowStatus())) {
                smsParem = "{\"mainCompanyName\":\"【"+depName+"】\",\"enterpriseName\":\"【"+msg.getTitle()+"】\"}";
            } else {
                BizTypeEnum typeEnum = BizTypeEnum.of(msg.getBizType());
                String menuName = typeEnum == null ? "" : typeEnum.getName();
                smsParem = "{\"mainCompanyName\":\"【"+depName+"】\",\"enterpriseName\":\"【"+msg.getTitle()+"】\",\"menuName\":\"【"+menuName+"】\"}";
            }

            //获取对应模板 清单类型+数据来源+流程状态组成dic_code
            Dic smsTemplateDic = dicUtils.getDicByCode(SmsConstants.ALI_SMS_GROUP, msg.getBizType().toString().concat(msg.getDataSourceClass().toString()
                    .concat(msg.getFlowStatus().toString())));
            if (smsTemplateDic == null || StringUtils.isEmpty(smsTemplateDic.getDicValue())) {
                //未获取到模板，不执行短信发送
                log.error("======《创新发布实时清单短信通知发送失败》---未获取到短信模板ID======");
                return;
            }
            //获取所有目标用户手机号批量发送
            String phoneStr = userList.stream()
                    .filter(u -> u.getReceiveSms().equals(YesOrNoEnum.YES.getType()))
                    .map(UserVO::getPhone)
                    .collect(Collectors.joining(","));
            //发送短信  批量发送
            SendSmsResponse response = aliSmsUtils.sendSms(phoneStr, smsParem, smsTemplateDic.getDicValue());
            int sendStatus = response.getCode() != null && "OK".equals(response.getCode())
                    ? YesOrNoEnum.YES.getType() : YesOrNoEnum.NO.getType();
            //筛选可接收短信的用户并遍历用户列表
            List<SysSendSmsLog> smsLogList = userList.stream()
                    .filter(u -> u.getReceiveSms().equals(YesOrNoEnum.YES.getType()))
                    .map(u -> {
                        SysSendSmsLog log = new SysSendSmsLog();
                        log.setUserId(u.getId());
                        log.setPhone(u.getPhone());
                        log.setFlowStatus(msg.getFlowStatus());
                        log.setDataSourceClass(msg.getDataSourceClass());
                        log.setSourceId(msg.getSourceId());
                        log.setIdRbacDepartment(msg.getIdRbacDepartment());
                        log.setContent(title);
                        log.setSendStatus(sendStatus);
                        log.setNotes(GsonUtils.format(response));
                        return log;
                    }).collect(Collectors.toList());
            sysSendSmsLogService.saveBatch(smsLogList);
        }
    }
}
