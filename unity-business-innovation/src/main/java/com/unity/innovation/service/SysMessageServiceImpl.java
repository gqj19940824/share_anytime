
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.constant.RedisConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
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
import com.unity.innovation.enums.SysMessageDataSourceEnum;
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
    public PageElementGrid<Map<String,Object>> listByPage(PageEntity<SysMessage> pageEntity) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("userId",customer.getId());
        SysMessage entity = pageEntity.getEntity();
        if(entity != null){
            paramMap.put("dataSource",entity.getDataSource());
            paramMap.put("startTime",entity.getStartTime());
            paramMap.put("endTime",entity.getEndTime());
            paramMap.put("title",StringUtils.isBlank(entity.getTitle()) ? null : entity.getTitle().trim());
        }
        Page<SysMessage> page = pageEntity.getPageable();
        paramMap.put("offset",(page.getCurrent() - 1) * page.getSize());
        paramMap.put("limit", page.getSize());
        long total = baseMapper.countListTotalByParam(paramMap);
        if (total > 0){
            List<SysMessage> messageList = baseMapper.findPageListByParam(paramMap);
            return PageElementGrid.<Map<String,Object>>newInstance()
                    .total(total)
                    .items(convert2List(messageList))
                    .build();
        } else {
            return PageElementGrid.<Map<String,Object>>newInstance()
                    .total(total)
                    .items(Lists.newArrayList())
                    .build();
        }
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<SysMessage> list) {
        return JsonUtil.ObjectToList(list,
                this::adapterField
                , SysMessage::getId, SysMessage::getTitle, SysMessage::getSourceId, SysMessage::getDataSource, SysMessage::getSendType, SysMessage::getSort, SysMessage::getNotes, SysMessage::getIdRbacDepartment
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, SysMessage entity) {
        if (entity.getDataSource() != null) {
            m.put("dataSourceTitle", SysMessageDataSourceEnum.of(entity.getDataSource()).getName());
        }
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
    }

    /**
     * 设置消息已读状态
     *
     * @param  id 消息id
     * @author gengjiajia
     * @since 2019/09/23 11:27
     */
    @Transactional(rollbackFor = Exception.class)
    public void setMessageReadStatus(Long id) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        SysMessageReadLog log = sysMessageReadLogService.getOne(new LambdaQueryWrapper<SysMessageReadLog>()
                .eq(SysMessageReadLog::getMessageId, id)
                .eq(SysMessageReadLog::getTargetUserId, customer.getId()));
        if(log == null){
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("消息数据不存在")
                    .build();
        }
        if(log.getIsRead().equals(YesOrNoEnum.NO.getType())){
            log.setIsRead(YesOrNoEnum.YES.getType());
            sysMessageReadLogService.updateById(log);
        }
        //TODO webSocket 提醒数量 -1
    }

    /**
     * 新增 实时清单系统消息
     *
     * @param sourceId 源数量id
     * @param idRbacDepartment 单位id
     * @param  companyName 企业名称
     * @param inventoryType 数据归属单位类型 1 发改局,2 企服局,3 科技局,4 组织部,5 纪检组,6 宣传部,7 亦庄国投
     * @author gengjiajia
     * @since 2019/09/23 14:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void addNewRealTimeInventoryMessage(Long sourceId,Long idRbacDepartment,String companyName,Integer inventoryType){
        //保存系统消息
        String title = MessageConstants.addInventoryMessageMap.get(inventoryType);
        title = title.replace(MessageConstants.COMPANY_NAME,companyName);
        Long messageId = saveMessage(SysMessageDataSourceEnum.ADD1.getId(), sourceId, title, idRbacDepartment, inventoryType);
        //根据数据归属单位类型获取对应角色下人员并保存发送记录
        Dic dic = dicUtils.getDicByCode(MessageConstants.REALTIME_INVENTORY,inventoryType.toString());
        if(dic != null){
            String value = dic.getDicValue();
            String[] ids = value.split(ConstString.SPLIT_COMMA);
            Long[] idArr = (Long[]) ConvertUtils.convert(ids, Long.class);
            List<Long> idList = Arrays.asList(idArr);
            //通过角色id换取角色关联的 目标用户id
            List<Long> userIdList = rbacClient.getUserIdListByRoleIdList(idList);
            saveMessageLog(messageId,userIdList);
            //TODO webSocket 目标用户 提醒数量 +1

        }
    }

    /**
     * 新增 清单协同处理的系统消息
     *
     * @param sourceId 源数据
     * @param idRbacDepartment 主责单位id
     * @param assistanceDepId 协同单位id集
     * @param companyName 企业名称
     * @param inventoryType 数据归属单位类型 1 发改局,2 企服局,3 科技局,4 组织部,5 纪检组,6 宣传部,7 亦庄国投
     * @author gengjiajia
     * @since 2019/09/23 16:46
     */
    public void addInventoryAssistanceMessage(Long sourceId,Long idRbacDepartment,List<Long> assistanceDepId,String companyName,Integer inventoryType){
        //保存系统消息
        String title = MessageConstants.addInventoryAssistanceMap.get(inventoryType);
        String depName = hashRedisUtils.getFieldValueByFieldName(RedisConstants.DEPARTMENT.concat(idRbacDepartment.toString()), RedisConstants.NAME);
        title = title.replace(MessageConstants.DEP_NAME,depName).replace(MessageConstants.COMPANY_NAME,companyName);
        Long messageId = saveMessage(SysMessageDataSourceEnum.ADD2.getId(), sourceId, title, idRbacDepartment, inventoryType);
        //根据协同单位单位id集获取对应单位下人员并保存发送记录
        List<Long> userIdList = rbacClient.getUserIdListByDepIdList(assistanceDepId);
        saveMessageLog(messageId,userIdList);
        //TODO webSocket 目标用户 提醒数量 +1
    }

    /**
     * 保存消息
     *
     * @param dataSource 数据来源
     * @param sourceId 源数据id
     * @param title 消息内容
     * @param depId 单位id
     * @param inventoryType 数据所属单位类型
     * @return 消息id
     * @author gengjiajia
     * @since 2019/09/23 18:45
     */
    private Long saveMessage(Integer dataSource,Long sourceId,String title,Long depId,Integer inventoryType){
        SysMessage message = new SysMessage();
        message.setDataSource(dataSource);
        message.setSourceId(sourceId);
        message.setSendType(SysMessageSendTypeEnum.ONE.getId());
        message.setTitle(title);
        message.setIdRbacDepartment(depId);
        message.setDataSourceClass(inventoryType);
        this.save(message);
        return message.getId();
    }

    /**
     * 保存消息发送记录
     *
     * @param  messageId 消息id
     * @param userIdList 用户id列表
     * @author gengjiajia
     * @since 2019/09/23 20:11
     */
    private void saveMessageLog(Long messageId,List<Long> userIdList){
        List<SysMessageReadLog> logList = userIdList.stream().map(userId -> {
            SysMessageReadLog log = new SysMessageReadLog();
            log.setIsRead(YesOrNoEnum.NO.getType());
            log.setMessageId(messageId);
            log.setTargetUserId(userId);
            return log;
        }).collect(Collectors.toList());
        sysMessageReadLogService.saveBatch(logList);
    }
}
