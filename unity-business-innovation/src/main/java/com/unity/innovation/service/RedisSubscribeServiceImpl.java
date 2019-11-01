package com.unity.innovation.service;

import com.unity.common.constant.RedisConstants;
import com.unity.common.pojos.Dic;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.enums.ListCategoryEnum;
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



    /**
     * 保存清单超时处理信息
     *
     * @param id           清单id(主数据id+"-"+0(主责)/协同单位id)
     * @param overTimeType 选择超时DEAL_OVER_TIME/UPDATE_OVER_TIME
     * @param departmentId 主责单位id
     * @param bizType 主责单位类型
     * @author zhangxiaogang
     * @since 2019/10/8 14:05
     */
    public String saveSubscribeInfo(String id, String overTimeType, Long departmentId, Integer bizType) {
        String result = "error";
        try {
            Dic dicByCode = dicUtils.getDicByCode(ListTypeConstants.LIST_TIMEOUT, overTimeType);
            if (dicByCode != null) {
                String key = ListTypeConstants.LIST_CONTROL
                        .concat(departmentId + RedisConstants.KEY_JOINER)
                        .concat(overTimeType)
                        .concat(RedisConstants.KEY_JOINER)
                        .concat(id + RedisConstants.KEY_JOINER)
                        .concat(bizType.toString());
                log.info("【记录redis存储的key:】" + key);
                //String[] idArrays = id.split("-");
                //超时未处理
                if (ListTypeConstants.DEAL_OVER_TIME.equals(overTimeType)) {
                    //result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 3600));
                    result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 60));//1分钟
                    //超时未更新
                } else if (ListTypeConstants.UPDATE_OVER_TIME.equals(overTimeType)) {
                    removeRecordInfo(id, ListTypeConstants.DEAL_OVER_TIME, departmentId, bizType);
                    //result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 3600 * 24));
                    result = RedisPoolUtil.setEx(key, key, (Integer.valueOf(dicByCode.getDicValue()) * 60 * 5));//5分钟
                } else {
                    log.info("请确认参数");
                }
            }
        } catch (Exception e) {
            log.error("【操作异常:" + e.getMessage() + "】");
        }
        return result;
    }





    /**
     * 删除清单超时处理信息
     *
     * @param id           清单id(主数据id+"-"+0(主责)/协同单位id)
     * @param overTimeType 选择超时DEAL_OVER_TIME/UPDATE_OVER_TIME
     * @param departmentId 主责单位id
     * @param bizType 主责单位类型
     * @author zhangxiaogang
     * @since 2019/10/9 17:15
     */
    public void removeRecordInfo(String id, String overTimeType, Long departmentId, Integer bizType) {
            String key = ListTypeConstants.LIST_CONTROL
                    .concat(departmentId + RedisConstants.KEY_JOINER)
                    .concat("%s")
                    .concat(RedisConstants.KEY_JOINER)
                    .concat(id + RedisConstants.KEY_JOINER)
                    .concat(bizType.toString());
            log.info("【删除记录redis存储的key:】" + key);
            RedisPoolUtil.del(String.format(key, overTimeType));
    }

    /**
     * 删除清单超时处理信息
     *
     * @param id           清单id(主数据id+"-"+0(主责)/协同单位id)
     * @param departmentId 主责单位id
     * @param bizType 主责单位类型
     * @author zhangxiaogang
     * @since 2019/10/9 17:15
     */
    public void removeRecordInfo(String id, Long departmentId, Integer bizType) {
            String key = ListTypeConstants.LIST_CONTROL
                    .concat(departmentId + RedisConstants.KEY_JOINER)
                    .concat("%s")
                    .concat(RedisConstants.KEY_JOINER)
                    .concat(id + RedisConstants.KEY_JOINER)
                    .concat(bizType.toString());
            log.info("【删除记录redis存储的key:】" + key);
            RedisPoolUtil.del(String.format(key,ListTypeConstants.DEAL_OVER_TIME));
            RedisPoolUtil.del(String.format(key,ListTypeConstants.UPDATE_OVER_TIME));
    }

}
