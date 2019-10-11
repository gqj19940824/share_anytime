
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.entity.generated.IplmManageLog;
import com.unity.innovation.enums.IplCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingProcessEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unity.innovation.entity.IplSupervisionMain;
import com.unity.innovation.dao.IplSupervisionMainDao;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


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
        //保存快照数据
        setSnapShot(entity);
        //新增
        if (entity.getId() == null) {
            //提交
            if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
                entity.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
                entity.setGmtSubmit(System.currentTimeMillis());
            } else {
                entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
                entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);
            }
            attachmentCode = UUIDUtil.getUUID();
            entity.setAttachmentCode(attachmentCode);
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_SUGGESTION_ID);
            //保存主表
            iplManageMainService.save(entity);
        } else {
            IplManageMain old = iplManageMainService.getById(entity.getId());
            attachmentCode = old.getAttachmentCode();
            if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
                if (ParamConstants.GMT_SUBMIT.equals(old.getGmtSubmit())) {
                    entity.setGmtSubmit(System.currentTimeMillis());
                }
                entity.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
            }
            //修改主表
            iplManageMainService.updateById(entity);
        }
        //保存附件表
        attachmentService.updateAttachments(attachmentCode, entity.getAttachments());
        //提交、记录日志
        if (YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
            logService.saveLog(InnovationConstant.DEPARTMENT_SUGGESTION_ID, WorkStatusAuditingStatusEnum.TWENTY.getId(), "", entity.getId());
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
        snapShotList.sort(comparing(IplSupervisionMain::getCategory)
                    .reversed()
                    .thenComparing(IplSupervisionMain::getGmtCreate)
                    .reversed());
        String snapshot = JSON.toJSONString(snapShotList);
        entity.setSnapshot(snapshot);
    }


    /**
     * 分页查询
     *
     * @param pageable 分页参数
     * @param ew       查询条件
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.unity.innovation.entity.generated.IplManageMain>
     * @author JH
     * @date 2019/10/9 16:26
     */
    public IPage<IplManageMain> pageIplManageMain(IPage<IplManageMain> pageable, Wrapper<IplManageMain> ew) {
        return iplManageMainService.page(pageable, ew);
    }


    /**
     * 通过/驳回
     *
     * @param idIplManageMain 发布清单主表id
     * @param yesOrNo         1：通过 0:驳回
     * @param content         意见
     * @return 错误信息 成功返回success
     * @author JH
     * @date 2019/10/9 16:47
     */
    public String passOrReject(Long idIplManageMain, Integer yesOrNo, String content) {
        IplManageMain old = iplManageMainService.getById(idIplManageMain);
        //待审核才能审核
        if (WorkStatusAuditingStatusEnum.TWENTY.getId().equals(old.getStatus())) {
            //通过
            if (YesOrNoEnum.YES.getType() == yesOrNo) {
                old.setStatus(WorkStatusAuditingStatusEnum.THIRTY.getId());
                //驳回
            } else {
                old.setStatus(WorkStatusAuditingStatusEnum.FORTY.getId());
            }
            iplManageMainService.updateById(old);
            //记录日志
            logService.saveLog(InnovationConstant.DEPARTMENT_SUGGESTION_ID, old.getStatus(), content, idIplManageMain);
        } else {
            return "此状态不能不能审核";
        }

        return "success";
    }

    /**
    * 详情接口
    *
    * @param id 主键
    * @return com.unity.innovation.entity.generated.IplManageMain
    * @author JH
    * @date 2019/10/10 10:56
    */
    public IplManageMain detailIplManageMainById(Long id) {
        IplManageMain iplManageMain = iplManageMainService.getById(id);
        if(iplManageMain == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("数据不存在").build();
        }
        iplManageMain.setSupervisionMainList(JSON.parseArray(iplManageMain.getSnapshot(),IplSupervisionMain.class));
        //操作记录
        List<IplmManageLog> logList = logService.list(new LambdaQueryWrapper<IplmManageLog>()
                .eq(IplmManageLog::getIdRbacDepartment, InnovationConstant.DEPARTMENT_SUGGESTION_ID)
                .eq(IplmManageLog::getIdIplManageMain, id)
                .orderByDesc(IplmManageLog::getGmtCreate));
        logList.forEach(n ->{
            n.setStatusName(WorkStatusAuditingProcessEnum.of(n.getStatus()).getName());
            n.setDepartmentName(InnovationUtil.getDeptNameById(n.getIdRbacDepartment()));
        });
        iplManageMain.setLogList(logList);
        //按状态进行分组,同时只取时间最小的那一条数据
         Map<Integer,IplmManageLog> map = logList.stream()
                .filter(n -> !WorkStatusAuditingStatusEnum.FORTY.getId().equals(n.getStatus()))
                .collect(Collectors.toMap(IplmManageLog::getStatus, Function.identity(), BinaryOperator.minBy(Comparator.comparingLong(IplmManageLog::getGmtCreate))));
        Set<Integer> statusSet = map.keySet();
        List<IplmManageLog> processNodeList = Lists.newArrayList();
        for (int status : statusSet) {
           IplmManageLog log = map.get(status);
            processNodeList.add(log);
        }
        iplManageMain.setProcessNodeList(processNodeList);
        return iplManageMain;
    }

    /**
     * 查询条件转换
     *
     * @param entity 统一查询对象
     * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.IplSupervisionMain>
     * @author JH
     * @date 2019/9/26 13:54
     */
    public LambdaQueryWrapper<IplSupervisionMain> wrapper(IplSupervisionMain entity){
        LambdaQueryWrapper<IplSupervisionMain> ew = new LambdaQueryWrapper<>();
        if(entity.getCategory() != null) {
            ew.eq(IplSupervisionMain::getCategory,entity.getCategory());
        }
        //创建时间
        if (StringUtils.isNotBlank(entity.getCreateTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), false);
            //gt 大于 lt 小于
            ew.lt(IplSupervisionMain::getGmtCreate, end);
            ew.gt(IplSupervisionMain::getGmtCreate, begin);
        }
        if(entity.getDescription() != null) {
            ew.like(IplSupervisionMain::getDescription,entity.getDescription());
        }
        return ew;
    }
    
    /**
    * 返回可选择的基础数据以及已选择的数据
    *
    * @param entity 查询条件
    * @return java.util.List<com.unity.innovation.entity.IplSupervisionMain>
    * @author JH
    * @date 2019/10/10 11:35
    */
    public  Map<String,List<IplSupervisionMain>> listSupervisionToAdd(IplSupervisionMain entity) {
        Map<String,List<IplSupervisionMain>> res = new HashMap<>(16);
        LambdaQueryWrapper<IplSupervisionMain> ew = wrapper(entity);
        //基础数据
        List<IplSupervisionMain> base = super.list(ew);
        res.put("base",base);
        IplManageMain iplManageMain = iplManageMainService.getById(entity.getId());
        String s = iplManageMain.getSnapshot();
        //快照数据
        List<IplSupervisionMain> snapshot = JSON.parseArray(s, IplSupervisionMain.class);
        res.put("snapshot",snapshot);
        return res;
    }
    /**
    * 清亲政商关系清单发布管理-纪检组 删除接口
    *
    * @param id 主键
    * @author JH
    * @date 2019/10/10 14:12
    */
    @Transactional(rollbackFor = Exception.class)
    public void removeIplManageMainById(Long id) {
        IplManageMain iplManageMain = iplManageMainService.getById(id);
        String attachmentCode = iplManageMain.getAttachmentCode();
        //删除附件
        attachmentService.updateAttachments(attachmentCode,null);
        //删除日志
        logService.remove(new LambdaUpdateWrapper<IplmManageLog>().eq(IplmManageLog::getIdIplManageMain,id).eq(IplmManageLog::getIdRbacDepartment,InnovationConstant.DEPARTMENT_SUGGESTION_ID));
        iplManageMainService.removeById(id);
    }


}
