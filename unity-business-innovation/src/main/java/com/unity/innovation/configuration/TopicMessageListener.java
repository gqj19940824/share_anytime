package com.unity.innovation.configuration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constant.RedisConstants;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.IplTimeOutLog;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.UnitCategoryEnum;
import com.unity.innovation.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听redis发布事件
 *<p>
 *create by zhangxiaogang at 2019/9/23 11:01
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
    private SysMessageReadLogServiceImpl sysMessageReadLogService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 请使用valueSerializer
        byte[] body = message.getBody();
        String itemValue = new String(body);
        // 请参考配置文件，本例中key，value的序列化方式均为string。
        log.info("itemValue:"+itemValue);
        //itemValue:listControl:DEPARTMENT_DARB_CONTROL:DEAL_OVER_TIME:12-0
        String[] itemValueArrays = itemValue.split(RedisConstants.KEY_JOINER);
        if(itemValueArrays.length == 4) {
            String departmentType = itemValueArrays[1];
            ListCategoryEnum listCategoryEnum = ListCategoryEnum.valueOfName(departmentType);
            if (listCategoryEnum != null) {
                //日志记录
                recordTimeOutLog(listCategoryEnum.getId(), itemValueArrays[2], itemValueArrays[3].split("-"));

                //更新清单
                updateProcessStatus(itemValueArrays);
                //todo 发送给那些人消息
                //sysMessageReadLogService.updateMessageNumToUserIdList();
            }
        }

    }

    /**
     * 更新清单
     *
     * @param  itemValueArrays redis的key
     * @author qinhuan
     * @since 2019-10-11 10:29
     */
    private void updateProcessStatus(String[] itemValueArrays) {
        // 主表id
        Long idIplMain = Long.parseLong(itemValueArrays[3].split("-")[0]);
        // 主责单位id
        Long idRbacDepartmentDuty = ListCategoryEnum.valueOfName(itemValueArrays[1]).getId();
        // 超时类型
        Integer processStatus = itemValueArrays[2].equals(ListTypeConstants.DEAL_OVER_TIME)?1:2;

        // 更新主表
        if(new Integer(0).equals(itemValueArrays[3].split("-")[1])){
            if (InnovationConstant.DEPARTMENT_DARB_ID.equals(idRbacDepartmentDuty)){
                iplDarbMainService.update(IplDarbMain.newInstance().processStatus(processStatus).build(), new LambdaQueryWrapper<IplDarbMain>().eq(IplDarbMain::getId, idIplMain));
            }else if (InnovationConstant.DEPARTMENT_ESB_ID.equals(idRbacDepartmentDuty)){
                IplEsbMain iplEsbMain = IplEsbMain.newInstance().build();
                iplEsbMain.setProcessStatus(processStatus);
                iplEsbMainService.update(iplEsbMain, new LambdaQueryWrapper<IplEsbMain>().eq(IplEsbMain::getId, idIplMain));
            }else if (InnovationConstant.DEPARTMENT_SUGGESTION_ID.equals(idRbacDepartmentDuty)){
                // TODO
            }else if (InnovationConstant.DEPARTMENT_OD_ID.equals(idRbacDepartmentDuty)){
                // TODO
            }else if (InnovationConstant.DEPARTMENT_PD_ID.equals(idRbacDepartmentDuty)){
                // TODO
            }else if (InnovationConstant.DEPARTMENT_SATB_ID.equals(idRbacDepartmentDuty)){
                IplSatbMain iplSatbMain = new IplSatbMain();
                iplSatbMain.setProcessStatus(processStatus);
                iplSatbMainService.update(iplSatbMain, new LambdaQueryWrapper<IplSatbMain>().eq(IplSatbMain::getId, idIplMain));
            }
        // 更新协同表
        }else {
            Long idRbacDepartmentAssit = Long.parseLong(itemValueArrays[3].split("-")[1]);
            LambdaQueryWrapper<IplAssist> qw = new LambdaQueryWrapper<>();
            qw.eq(IplAssist::getIdRbacDepartmentDuty, idRbacDepartmentDuty).eq(IplAssist::getIdIplMain, idIplMain).eq(IplAssist::getIdRbacDepartmentAssist, idRbacDepartmentAssit);
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
    private void recordTimeOutLog(Long departmentId, String overTimeType, String... idArrays) {
        IplTimeOutLog iplTimeOutLog = new IplTimeOutLog();
        iplTimeOutLog.setMainId(Long.valueOf(idArrays[0]));
        Long aLong = Long.valueOf(idArrays[1]);
        //主责
        if (aLong.intValue() == 0) {
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.MAIN.getId());
            iplTimeOutLog.setDepartmentId(departmentId);
            //协同
        } else {
            iplTimeOutLog.setUnitCategory(UnitCategoryEnum.COORDINATION.getId());
            iplTimeOutLog.setDepartmentId(aLong);
        }
        iplTimeOutLog.setTimeType(overTimeType);
        iplTimeOutLogService.save(iplTimeOutLog);
    }
}
