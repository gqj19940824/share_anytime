
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.IplCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unity.innovation.entity.IplSupervisionMain;
import com.unity.innovation.dao.IplSupervisionMainDao;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplSupervisionMainServiceImpl extends BaseServiceImpl<IplSupervisionMainDao, IplSupervisionMain> {

    @Resource
    private IplManageMainServiceImpl iplManageMainService;

    @Resource
    private AttachmentServiceImpl attachmentService;

    @Resource
    private IplmManageLogServiceImpl logService;


    /**
     * 新增
     *
     * @param entity 实体
     * @author JH
     * @date 2019/10/8 16:30
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIplManageMain(IplManageMain entity) {
        String attachmentCode;
        //提交
        if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
            entity.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
        } else {
            entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
        }
        //保存快照数据
        setSnapShot(entity);
        //新增
        if (entity.getId() == null) {
            attachmentCode = UUIDUtil.getUUID();
            entity.setAttachmentCode(attachmentCode);
            entity.setIdRbacDepartmentDuty(8L);
            //保存主表
            iplManageMainService.save(entity);
        } else {
            IplManageMain old = iplManageMainService.getById(entity.getId());
            attachmentCode = old.getAttachmentCode();
            //修改主表
            iplManageMainService.updateById(entity);
        }
        //保存附件表
        attachmentService.updateAttachments(attachmentCode, entity.getAttachments());
        //提交、记录日志
        if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
            logService.saveLog(8L, WorkStatusAuditingStatusEnum.TEN.getId(), "", entity.getId());
        }
    }


    /**
     * 保存快照数据
     *
     * @param entity 实体
     * @author JH
     * @date 2019/10/9 10:14
     */
    private void setSnapShot(IplManageMain entity) {
        //保存快照数据
        List<IplSupervisionMain> snapShotList = entity.getSupervisionMainList();
        snapShotList.forEach(n -> n.setCategoryName(IplCategoryEnum.ofName(n.getCategory())));
        String snapshot = JSON.toJSONString(snapShotList);
        entity.setSnapshot(snapshot);
    }

    /**
     * 一次打包删除一条数据
     *
     * @param entity 实体
     * @author JH
     * @date 2019/10/8 16:30
     */
    public void deleteIplManageMain(IplManageMain entity) {
        //原有发布清单管理
        IplManageMain old = iplManageMainService.getById(entity.getId());
        //原有快照
        String snapshot = old.getSnapshot();
        List<IplSupervisionMain> list = JSON.parseArray(snapshot, IplSupervisionMain.class);
        //删除一条数据
        List<IplSupervisionMain> collect = list.stream().filter(n -> !n.getId().equals(entity.getIdIplSupervisionMain())).collect(Collectors.toList());
        old.setSnapshot(JSON.toJSONString(collect));
        iplManageMainService.updateById(old);
    }

    public IPage<IplManageMain> pageIplManageMain(IPage<IplManageMain> pageable, Wrapper<IplManageMain> ew) {
        return iplManageMainService.page(pageable,ew);
    }

}
