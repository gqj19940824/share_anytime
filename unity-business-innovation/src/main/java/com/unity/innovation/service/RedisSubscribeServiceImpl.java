package com.unity.innovation.service;

import com.unity.common.constant.RedisConstants;
import com.unity.common.pojos.Dic;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.entity.IplTimeOutLog;
import com.unity.innovation.enums.ListCategoryEnum;
import com.unity.innovation.enums.UnitCategoryEnum;
import com.unity.innovation.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 测试redis的订阅与发布
 * <p>
 * create by zhangxiaogang at 2019/9/23 10:07
 */
@Service
@Slf4j
public class RedisSubscribeServiceImpl {

    @Resource
    private DicUtils dicUtils;
    @Resource
    private IplTimeOutLogServiceImpl iplTimeOutLogService;


    /**
     * 保存清单超时处理信息
     *
     * @param id           清单id(主数据id+"-"+0(主责)/协同单位id)
     * @param overTimeType 选择超时DEAL_OVER_TIME/UPDATE_OVER_TIME
     * @param departmentId 主责单位id
     * @author zhangxiaogang
     * @since 2019/10/8 14:05
     */
    public String saveSubscribeInfo(String id, String overTimeType, Long departmentId) {
        String result = "error";
        try {
            Dic dicByCode = dicUtils.getDicByCode(ListTypeConstants.LIST_TIMEOUT, overTimeType);
            ListCategoryEnum listCategoryEnum = ListCategoryEnum.of(departmentId);
            if (listCategoryEnum != null && dicByCode != null) {
                String key = ListTypeConstants.LIST_CONTROL
                        .concat(listCategoryEnum.getName() + RedisConstants.KEY_JOINER)
                        .concat(overTimeType)
                        .concat(RedisConstants.KEY_JOINER)
                        .concat(id);
                log.info("【记录redis存储的key:】" + key);
                String[] idArrays = id.split("-");
                //超时未处理
                if (ListTypeConstants.DEAL_OVER_TIME.equals(overTimeType)) {
                    result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 3600));
                    //超时未更新
                } else if (ListTypeConstants.UPDATE_OVER_TIME.equals(overTimeType)) {
                    removeRecordInfo(id, ListTypeConstants.DEAL_OVER_TIME, departmentId);
                    result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 3600 * 24));
                } else {
                    log.info("请确认参数");
                }
                if ("OK".equals(result)) {
                    recordTimeOutLog(departmentId, overTimeType, idArrays);
                }
            }
        } catch (Exception e) {
            log.error("【操作异常:" + e.getMessage() + "】");
        }
        return result;
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

    /**
     * 删除清单超时处理信息
     *
     * @param id           清单id(主数据id+"-"+0(主责)/协同单位id)
     * @param overTimeType 选择超时DEAL_OVER_TIME/UPDATE_OVER_TIME
     * @param departmentId 主责单位id
     * @author zhangxiaogang
     * @since 2019/10/9 17:15
     */
    public void removeRecordInfo(String id, String overTimeType, Long departmentId) {
        Dic dicByCode = dicUtils.getDicByCode(ListTypeConstants.LIST_TIMEOUT, overTimeType);
        ListCategoryEnum listCategoryEnum = ListCategoryEnum.of(departmentId);
        if (listCategoryEnum != null && dicByCode != null) {
            String key = ListTypeConstants.LIST_CONTROL
                    .concat(listCategoryEnum.getName() + RedisConstants.KEY_JOINER)
                    .concat("%s")
                    .concat(RedisConstants.KEY_JOINER)
                    .concat(id);
            log.info("【删除记录redis存储的key:】" + key);
            RedisPoolUtil.del(String.format(key, overTimeType));
        }
    }
    /**
     * 删除清单超时处理信息
     *
     * @param id           清单id(主数据id+"-"+0(主责)/协同单位id)
     * @param departmentId 主责单位id
     * @author zhangxiaogang
     * @since 2019/10/9 17:15
     */
    public void removeRecordInfo(String id, Long departmentId) {
        ListCategoryEnum listCategoryEnum = ListCategoryEnum.of(departmentId);
        if (listCategoryEnum != null) {
            String key = ListTypeConstants.LIST_CONTROL
                    .concat(listCategoryEnum.getName() + RedisConstants.KEY_JOINER)
                    .concat("%s")
                    .concat(RedisConstants.KEY_JOINER)
                    .concat(id);
            log.info("【删除记录redis存储的key:】" + key);
            RedisPoolUtil.del(String.format(key,ListTypeConstants.DEAL_OVER_TIME));
            RedisPoolUtil.del(String.format(key,ListTypeConstants.UPDATE_OVER_TIME));
        }
    }
}
