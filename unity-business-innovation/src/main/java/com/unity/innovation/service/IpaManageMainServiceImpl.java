package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.ReviewMessage;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.dao.IpaManageMainDao;
import com.unity.innovation.entity.DailyWorkStatusPackage;
import com.unity.innovation.entity.PmInfoDept;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: IpaManageMainService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-21 15:45:33
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IpaManageMainServiceImpl extends BaseServiceImpl<IpaManageMainDao, IpaManageMain> {

    @Resource
    private IplManageMainServiceImpl iplManageMainService;
    @Resource
    private DailyWorkStatusPackageServiceImpl dailyWorkStatusPackageService;
    @Resource
    private PmInfoDeptServiceImpl pmInfoDeptService;
    @Resource
    private SysMessageHelpService sysMessageHelpService;

    @Transactional(rollbackFor = Exception.class)
    public void updateIpaMain(IpaManageMain entity) {
        // 更新二次包数据
        Integer status = entity.getStatus();
        LambdaUpdateWrapper<IpaManageMain> wrapper = new LambdaUpdateWrapper<IpaManageMain>().eq(IpaManageMain::getId, entity.getId()).set(IpaManageMain::getStatus, status);
        if (StringUtils.isNotBlank(entity.getPublishResult())) {
            wrapper.set(IpaManageMain::getPublishResult, entity.getPublishResult());
        }
        wrapper.set(IpaManageMain::getPublishMedia, entity.getPublishMedia()).set(IpaManageMain::getParticipateMedia, entity.getParticipateMedia());
        update(wrapper);
        updateFirstPackStatus(entity.getId(), status);
        /*=========工作动态发布管理/5个xx清单发布管理/2个企业信息发布管理=======系统通知======================*/
        sendSysMessage(entity);
    }

    private void updateFirstPackStatus(Long idIpaMain, Integer status) {
        // 更新一次包状态
        dailyWorkStatusPackageService
                .update(new LambdaUpdateWrapper<DailyWorkStatusPackage>().eq(DailyWorkStatusPackage::getIdIpaMain, idIpaMain).set(DailyWorkStatusPackage::getState, status));
        pmInfoDeptService
                .update(new LambdaUpdateWrapper<PmInfoDept>().eq(PmInfoDept::getIdIpaMain, idIpaMain).set(PmInfoDept::getStatus, status));
        iplManageMainService
                .update(new LambdaUpdateWrapper<IplManageMain>().eq(IplManageMain::getIdIpaMain, idIpaMain).set(IplManageMain::getStatus, status));
    }

    /**
     * 工作动态发布管理/5个xx清单发布管理/2个企业信息发布管理 发布/更新发布效果
     *
     * @param entity 包含消息数据
     * @author gengjiajia
     * @since 2019/10/29 11:13
     */
    private void sendSysMessage(IpaManageMain entity) {
        //通过发布管理id分别获取工作动态、5个清单、两个企业信息列表
        if (WorkStatusAuditingStatusEnum.FIFTY.getId().equals(entity.getStatus())
                || WorkStatusAuditingStatusEnum.SIXTY.getId().equals(entity.getStatus())) {
            List<DailyWorkStatusPackage> packageList = dailyWorkStatusPackageService.list(
                    new LambdaQueryWrapper<DailyWorkStatusPackage>().eq(DailyWorkStatusPackage::getIdIpaMain, entity.getId())
                            .eq(DailyWorkStatusPackage::getState, entity.getStatus()));
            List<IplManageMain> mainList = iplManageMainService.list(new LambdaQueryWrapper<IplManageMain>()
                    .eq(IplManageMain::getIdIpaMain, entity.getId())
                    .eq(IplManageMain::getStatus, entity.getStatus()));
            List<PmInfoDept> deptList = pmInfoDeptService.list(
                    new LambdaQueryWrapper<PmInfoDept>().eq(PmInfoDept::getIdIpaMain, entity.getId())
                            .eq(PmInfoDept::getStatus, entity.getStatus()));
            int flowStatus = entity.getStatus().equals(WorkStatusAuditingStatusEnum.FIFTY.getId())
                    ? SysMsgFlowStatusEnum.FOUR.getId() : SysMsgFlowStatusEnum.FIVES.getId();
            //发布 获取所有需要通知的单位id
            packageList.forEach(work -> {
                pushSysMessage(SysMessageDataSourceClassEnum.WORK_RELEASE_MANAGE.getId(),
                        flowStatus,
                        work.getIdRbacDepartment(),
                        work.getId(),
                        work.getTitle());
            });
            deptList.forEach(dep -> {
                int dataSourceClass = dep.getBizType().equals(BizTypeEnum.RQDEPTINFO.getType())
                        ? SysMessageDataSourceClassEnum.YZGT_RELEASE_REVIEW.getId() :
                        dep.getBizType().equals(BizTypeEnum.LYDEPTINFO.getType()) ?
                                SysMessageDataSourceClassEnum.SATB_RELEASE_REVIEW.getId() : SysMessageDataSourceClassEnum.INVESTMENT_RELEASE_REVIEW.getId();
                pushSysMessage(dataSourceClass,
                        flowStatus,
                        dep.getIdRbacDepartment(),
                        dep.getId(),
                        dep.getTitle());
            });
            mainList.forEach(main -> {
                Integer dataSourceClass = SysMessageDataSourceClassEnum.getDataSourceClassByBizType(main.getBizType());
                pushSysMessage(dataSourceClass,
                        flowStatus,
                        main.getIdRbacDepartmentDuty(),
                        main.getId(),
                        main.getTitle());
            });
        }
    }

    /**
     * 推送系统消息
     *
     * @param dataSourceClass  数据来源
     * @param flowStatus       流程状态
     * @param idRbacDepartment 提交单位
     * @param sourceId         源id
     * @param title            标题
     * @author gengjiajia
     * @since 2019/10/29 13:57
     */
    private void pushSysMessage(int dataSourceClass, int flowStatus, Long idRbacDepartment, Long sourceId, String title) {
        sysMessageHelpService.addReviewMessage(ReviewMessage.newInstance()
                .dataSourceClass(dataSourceClass)
                .flowStatus(flowStatus)
                .idRbacDepartment(idRbacDepartment)
                .sourceId(sourceId)
                .title(title)
                .build());
    }


    @Transactional(rollbackFor = Exception.class)
    public void delByIds(List<Long> ids) {
        // 删除二次打包表
        removeByIds(ids);

        // 删除各个一次打包表
        dailyWorkStatusPackageService.updateIdIpaMain(null, ids);
        pmInfoDeptService.updateIdIpaMain(null, ids);
        iplManageMainService.updateIdIpaMain(null, ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(IpaManageMain entity) {
        List<Long> idDwspList = entity.getIdDwspList();
        List<Long> idIplpList = entity.getIdIplpList();
        List<Long> idPmpList = entity.getIdPmpList();

        if (CollectionUtils.isEmpty(idDwspList) && CollectionUtils.isEmpty(idIplpList) && CollectionUtils.isEmpty(idPmpList)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message(
                    SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
        }

        updateById(entity);
        Long ipaId = entity.getId();
        // 工作动态
        if (CollectionUtils.isNotEmpty(idDwspList)) {
            editDwsp(ipaId, idDwspList);
        }
        // 与会企业信息
        if (CollectionUtils.isNotEmpty(idPmpList)) {
            editPmp(ipaId, idPmpList);
        }
        // 创新发布清单
        if (CollectionUtils.isNotEmpty(idIplpList)) {
            editIplp(ipaId, idIplpList);
        }
    }

    /**
     * 新增活动管理
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/17 2:37 下午
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(IpaManageMain entity) {
        List<Long> idDwspList = entity.getIdDwspList();
        List<Long> idIplpList = entity.getIdIplpList();
        List<Long> idPmpList = entity.getIdPmpList();

        if (CollectionUtils.isEmpty(idDwspList) && CollectionUtils.isEmpty(idIplpList) && CollectionUtils.isEmpty(idPmpList)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM).message(
                    SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName()).build();
        }

        entity.setStatus(IpaStatusEnum.UNPUBLISH.getId());
        save(entity);

        Long ipaId = entity.getId();
        // 工作动态
        if (CollectionUtils.isNotEmpty(idDwspList)) {
            checkUnique(countDwsp(idDwspList));
            saveDwsp(ipaId, idDwspList);
        }
        // 与会企业信息
        if (CollectionUtils.isNotEmpty(idPmpList)) {
            checkUnique(countPmp(idPmpList));
            savePmp(ipaId, idPmpList);
        }
        // 创新发布清单
        if (CollectionUtils.isNotEmpty(idIplpList)) {
            checkUnique(countIplp(idIplpList));
            saveIplp(ipaId, idIplpList);
        }
    }

    private void checkUnique(int count) {
        if (count > 0) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                    .message("所添加的数据中存在已添加至其他创新发布活动的数据，请重新添加！").build();
        }
    }

    private void saveIplp(Long idIpaMain, List<Long> toSaveIdList) { // TODO 是否缺少一种状态
        iplManageMainService.update(IplManageMain.newInstance().idIpaMain(idIpaMain).build()
                , new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, toSaveIdList));
    }

    private void savePmp(Long idIpaMain, List<Long> toSaveIdList) {
        PmInfoDept pmInfoDept = new PmInfoDept();
        pmInfoDept.setIdIpaMain(idIpaMain);
//        pmInfoDept.setStatus(IpaStatusEnum.UNPUBLISH.getId()); // TODO 是否缺少一种状态
        pmInfoDeptService.update(pmInfoDept
                , new LambdaQueryWrapper<PmInfoDept>().in(PmInfoDept::getId, toSaveIdList));
    }

    private void saveDwsp(Long idIpaMain, List<Long> toSaveIdList) {
        DailyWorkStatusPackage dailyWorkStatusPackage = new DailyWorkStatusPackage();
        dailyWorkStatusPackage.setIdIpaMain(idIpaMain);
//        dailyWorkStatusPackage.setState(IpaStatusEnum.UNPUBLISH.getId()); // TODO 是否缺少一种状态
        dailyWorkStatusPackageService.update(dailyWorkStatusPackage
                , new LambdaQueryWrapper<DailyWorkStatusPackage>().in(DailyWorkStatusPackage::getId, toSaveIdList));
    }

    private Integer countDwsp(List<Long> toSaveIdList) {
        return dailyWorkStatusPackageService.count(new LambdaQueryWrapper<DailyWorkStatusPackage>().in(DailyWorkStatusPackage::getId, toSaveIdList).isNotNull(DailyWorkStatusPackage::getIdIpaMain));
    }

    private Integer countIplp(List<Long> toSaveIdList) {
        return iplManageMainService.count(new LambdaQueryWrapper<IplManageMain>().in(IplManageMain::getId, toSaveIdList).isNotNull(IplManageMain::getIdIpaMain));
    }

    private Integer countPmp(List<Long> toSaveIdList) {
        return pmInfoDeptService.count(new LambdaQueryWrapper<PmInfoDept>().in(PmInfoDept::getId, toSaveIdList).isNotNull(PmInfoDept::getIdIpaMain));
    }

    private void editIplp(Long ipaId, List<Long> idIplpList) {
        List<IplManageMain> list = iplManageMainService
                .list(new LambdaQueryWrapper<IplManageMain>().eq(IplManageMain::getIdIpaMain, ipaId));
        // 已存在的
        List<Long> savedIdList = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
        // 需要新增的
        List<Long> toSaveIdList = idIplpList.stream().filter(e -> !savedIdList.contains(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toSaveIdList)) {
            checkUnique(countIplp(toSaveIdList));
            saveIplp(ipaId, toSaveIdList);
        }
        // 需要删除的
        List<Long> toDelIdList = savedIdList.stream().filter(e -> !idIplpList.contains(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toDelIdList)) {
            iplManageMainService.updateIdIpaMain(toDelIdList, null);
        }
    }

    private void editPmp(Long ipaId, List<Long> idPmpList) {
        List<PmInfoDept> list = pmInfoDeptService
                .list(new LambdaQueryWrapper<PmInfoDept>().eq(PmInfoDept::getIdIpaMain, ipaId));
        // 已存在的
        List<Long> savedIdList = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
        // 需要新增的
        List<Long> toSaveIdList = idPmpList.stream().filter(e -> !savedIdList.contains(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toSaveIdList)) {
            checkUnique(countPmp(toSaveIdList));
            savePmp(ipaId, toSaveIdList);
        }
        // 需要删除的
        List<Long> toDelIdList = savedIdList.stream().filter(e -> !idPmpList.contains(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toDelIdList)) {
            pmInfoDeptService.updateIdIpaMain(toDelIdList, null);
        }
    }

    private void editDwsp(Long ipaId, List<Long> idDwspList) {
        List<DailyWorkStatusPackage> list = dailyWorkStatusPackageService
                .list(new LambdaQueryWrapper<DailyWorkStatusPackage>().eq(DailyWorkStatusPackage::getIdIpaMain, ipaId));
        // 已存在的
        List<Long> savedIdList = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
        // 需要新增的
        List<Long> toSaveIdList = idDwspList.stream().filter(e -> !savedIdList.contains(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toSaveIdList)) {
            checkUnique(countDwsp(toSaveIdList));
            saveDwsp(ipaId, toSaveIdList);
        }
        // 需要删除的
        List<Long> toDelIdList = savedIdList.stream().filter(e -> !idDwspList.contains(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toDelIdList)) {
            dailyWorkStatusPackageService.updateIdIpaMain(toDelIdList, null);
        }
    }
}
