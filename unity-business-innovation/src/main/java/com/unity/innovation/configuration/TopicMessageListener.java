package com.unity.innovation.configuration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.constant.RedisConstants;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.*;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.enums.*;
import com.unity.innovation.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 监听redis发布事件
 * <p>
 * create by zhangxiaogang at 2019/9/23 11:01
 */
@Component
@Slf4j
public class TopicMessageListener implements MessageListener {

    @Resource
    private IplTimeOutLogServiceImpl iplTimeOutLogService;
    @Resource
    private IplAssistServiceImpl iplAssistService;
    @Resource
    protected IplDarbMainServiceImpl iplDarbMainService;
    @Resource
    private IplSatbMainServiceImpl iplSatbMainService;
    @Resource
    private IplEsbMainServiceImpl iplEsbMainService;
    @Resource
    private IplSuggestionServiceImpl iplSuggestionService;
    @Resource
    private IplOdMainServiceImpl iplOdMainService;
    @Resource
    private SysMessageHelpService sysMessageHelpService;
    @Resource
    private DicUtils dicUtils;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 请使用valueSerializer
        byte[] body = message.getBody();
        String itemValue = new String(body);
        // 请参考配置文件，本例中key，value的序列化方式均为string。
        log.info("itemValue====:" + itemValue);
        //itemValue:listControl:DEPARTMENT_DARB_CONTROL:DEAL_OVER_TIME:12-0:10
        String[] itemValueArrays = itemValue.split(RedisConstants.KEY_JOINER);
        if (itemValueArrays.length == 5) {
                String[] idArr = itemValueArrays[3].split("-");
                //日志记录
                recordTimeOutLog(itemValueArrays[4],itemValueArrays[1], itemValueArrays[2], idArr);
                //更新清单
                updateProcessStatus(itemValueArrays);
                //增加系统消息
                addSysMessage(itemValueArrays);
        }

    }

    /**
     * 清单超时未处理 增加系统消息提醒
     *
     * @param itemValueArrays 包含超时信息
     * @author gengjiajia
     * @since 2019/10/17 10:01
     */
    private void addSysMessage(String[] itemValueArrays) {
        String departmentType = itemValueArrays[1];
        ListCategoryEnum listCategoryEnum = ListCategoryEnum.valueOfName(departmentType);
        //主责单位id
        Long idRbacDepartmentDuty = listCategoryEnum == null ? null : listCategoryEnum.getId();
        // 主表id
        String[] idStrArr = itemValueArrays[3].split("-");
        Integer bizType = Integer.parseInt(itemValueArrays[4]);
        Long idIplMain = Long.parseLong(idStrArr[0]);
        Dic dicByCode = dicUtils.getDicByCode(ListTypeConstants.LIST_TIMEOUT, itemValueArrays[2]);
        //配置项为小时，超过24小时换算为天
        int timeout = Integer.parseInt(dicByCode.getDicValue());
        String time = timeout > 24 ? (timeout / 24) + "D" : timeout + "H";
        InventoryMessage inventoryMessage = InventoryMessage.newInstance()
                .sourceId(idIplMain)
                .time(time)
                .flowStatus(itemValueArrays[2].equals(ListTypeConstants.DEAL_OVER_TIME)
                        //超时未处理
                        ? SysMessageFlowStatusEnum.TWO.getId()
                        //超时未更新
                        : SysMessageFlowStatusEnum.THREE.getId())
                .build();
        if (BizTypeEnum.CITY.getType().equals(bizType)) {
            IplDarbMain main = iplDarbMainService.getById(idIplMain);
            inventoryMessage.setTitle(main.getEnterpriseName());
            inventoryMessage.setIdRbacDepartment(main.getIdRbacDepartmentDuty());
            inventoryMessage.setDataSourceClass(SysMessageDataSourceClassEnum.COOPERATION.getId());
        } else if (BizTypeEnum.ENTERPRISE.getType().equals(bizType)) {
            IplEsbMain main = iplEsbMainService.getById(idIplMain);
            inventoryMessage.setTitle(main.getEnterpriseName());
            inventoryMessage.setIdRbacDepartment(main.getIdRbacDepartmentDuty());
            inventoryMessage.setDataSourceClass(SysMessageDataSourceClassEnum.DEVELOPING.getId());
        } else if (BizTypeEnum.POLITICAL.getType().equals(bizType)) {
            IplSuggestion main = iplSuggestionService.getById(idIplMain);
            inventoryMessage.setTitle(main.getEnterpriseName());
            inventoryMessage.setIdRbacDepartment(idRbacDepartmentDuty);
            inventoryMessage.setDataSourceClass(SysMessageDataSourceClassEnum.SUGGEST.getId());
        } else if (BizTypeEnum.INTELLIGENCE.getType().equals(bizType)) {
            IplOdMain main = iplOdMainService.getById(idIplMain);
            inventoryMessage.setTitle(main.getEnterpriseName());
            inventoryMessage.setIdRbacDepartment(idRbacDepartmentDuty);
            inventoryMessage.setDataSourceClass(SysMessageDataSourceClassEnum.DEMAND.getId());
        } else if (BizTypeEnum.GROW.getType().equals(bizType)) {
            IplSatbMain main = iplSatbMainService.getById(idIplMain);
            inventoryMessage.setTitle(main.getEnterpriseName());
            inventoryMessage.setIdRbacDepartment(idRbacDepartmentDuty);
            inventoryMessage.setDataSourceClass(SysMessageDataSourceClassEnum.TARGET.getId());
        }
        if (!"0".equals(idStrArr[1])) {
            //说明是协同单位超时
            inventoryMessage.setBizType(bizType);
            inventoryMessage.setHelpDepartmentIdList(Arrays.asList(Long.parseLong(idStrArr[1])));
            sysMessageHelpService.addInventoryHelpMessage(inventoryMessage);
        } else {
            sysMessageHelpService.addInventoryMessage(inventoryMessage);
        }

    }

    /**
     * 更新清单
     *
     * @param itemValueArrays redis的key
     * @author qinhuan
     * @since 2019-10-11 10:29
     */
    private void updateProcessStatus(String[] itemValueArrays) {
        // 主表id
        Long idIplMain = Long.parseLong(itemValueArrays[3].split("-")[0]);
        // 业务类型
        Integer bizType = Integer.parseInt(itemValueArrays[4]);
        // 超时类型
        Integer processStatus = itemValueArrays[2].equals(ListTypeConstants.DEAL_OVER_TIME) ? 1 : 2;

        // 更新主表
        if ("0".equals(itemValueArrays[3].split("-")[1])) {
            // 发改局
            if (BizTypeEnum.CITY.getType().equals(bizType)) {
                iplDarbMainService.update(IplDarbMain.newInstance().processStatus(processStatus).build(), new LambdaQueryWrapper<IplDarbMain>().eq(IplDarbMain::getId, idIplMain));
            } else if (BizTypeEnum.ENTERPRISE.getType().equals(bizType)) {
                IplEsbMain iplEsbMain = IplEsbMain.newInstance().build();
                iplEsbMain.setProcessStatus(processStatus);
                iplEsbMainService.update(iplEsbMain, new LambdaQueryWrapper<IplEsbMain>().eq(IplEsbMain::getId, idIplMain));
            } else if (BizTypeEnum.INTELLIGENCE.getType().equals(bizType)) {
                IplOdMain iplOdMain = new IplOdMain();
                iplOdMain.setProcessStatus(processStatus);
                iplOdMainService.update(iplOdMain, new LambdaQueryWrapper<IplOdMain>().eq(IplOdMain::getId, idIplMain));
            } else if (BizTypeEnum.GROW.getType().equals(bizType)) {
                IplSatbMain iplSatbMain = new IplSatbMain();
                iplSatbMain.setProcessStatus(processStatus);
                iplSatbMainService.update(iplSatbMain, new LambdaQueryWrapper<IplSatbMain>().eq(IplSatbMain::getId, idIplMain));
            }
            // 更新协同表
        } else {
            Long idRbacDepartmentAssit = Long.parseLong(itemValueArrays[3].split("-")[1]);
            LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
            qw.eq(IplAssist::getBizType, bizType).eq(IplAssist::getIdIplMain, idIplMain).eq(IplAssist::getIdRbacDepartmentAssist, idRbacDepartmentAssit);
            iplAssistService.update(IplAssist.newInstance().processStatus(processStatus).build(), qw);
        }
    }

    /**
     * 记录超时日志
     *
     * @param overTimeType 超时类别
     * @param idArrays     id数组
     * @param departmentId 主责部门id
     * @author zhangxiaogang
     * @since 2019/10/8 18:37
     */
    private void recordTimeOutLog(String type, String departmentId, String overTimeType, String... idArrays) {
        IplTimeOutLog iplTimeOutLog = new IplTimeOutLog();
        iplTimeOutLog.setMainId(Long.valueOf(idArrays[0]));
        Long aLong = Long.valueOf(idArrays[1]);
        //主责
        if (aLong.intValue() == 0) {
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.MAIN.getId());
            iplTimeOutLog.setDepartmentId(Long.valueOf(departmentId));
        } else {
            //协同
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.COORDINATION.getId());
            iplTimeOutLog.setDepartmentId(aLong);
        }
        iplTimeOutLog.setBizType(type);
        iplTimeOutLog.setTimeType(overTimeType);
        iplTimeOutLogService.save(iplTimeOutLog);
    }
}
