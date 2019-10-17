
package com.unity.innovation.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.ReviewMessage;
import com.unity.innovation.dao.SysMessageDao;
import com.unity.innovation.entity.SysMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * ClassName: SysMessageService
 * date: 2019-09-23 09:39:17
 *
 * @author G
 * @since JDK 1.8
 */
@Service
public class SysMessageHelpService extends BaseServiceImpl<SysMessageDao, SysMessage> {

    @Resource
    SysMessageServiceImpl sysMessageService;

    /**
     * 新增 实时清单系统消息
     *
     * @param msg 包含 源数量id、单位id、企业名称、数据归属单位类型、流程状态
     * @author gengjiajia
     * @since 2019/09/23 14:46
     */
    @Async
    public void addInventoryMessage(InventoryMessage msg) {
        sysMessageService.addInventoryMessage(msg);
    }

    /**
     * 新增 清单协同处理的系统消息
     *
     * @param msg 包含 源数量id、单位id、企业名称、数据归属单位类型、流程状态、协同单位id集
     * @author gengjiajia
     * @since 2019/09/23 16:46
     */
    @Async
    public void addInventoryHelpMessage(InventoryMessage msg) {
        sysMessageService.addInventoryHelpMessage(msg);
    }

    /**
     * 新增 发布审核流程的系统消息
     *
     * @param msg 包含消息数据
     * @author gengjiajia
     * @since 2019/09/25 16:20
     */
    @Async
    public void addReviewMessage(ReviewMessage msg) {
        sysMessageService.addReviewMessage(msg);
    }

}
