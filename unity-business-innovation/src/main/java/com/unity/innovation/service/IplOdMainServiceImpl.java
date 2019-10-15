
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.dao.IplOdMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.ProcessStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IplOdMainServiceImpl extends BaseServiceImpl<IplOdMainDao, IplOdMain> {

    @Resource
    private SysCfgServiceImpl sysCfgService;

    @Resource
    private AttachmentServiceImpl attachmentService;

    @Resource
    private IplLogServiceImpl iplLogService;

    @Resource
    private IplAssistServiceImpl iplAssistService;

    @Resource
    private RedisSubscribeServiceImpl redisSubscribeService;

    /**
     * 功能描述 分页接口
     *
     * @param search 查询条件
     * @return 分页集合
     * @author gengzhiqiang
     * @date 2019/9/25 16:26
     */
    public IPage<IplOdMain> listByPage(PageEntity<IplOdMain> search) {
        LambdaQueryWrapper<IplOdMain> lqw = new LambdaQueryWrapper<>();
        if (search != null && search.getEntity() != null) {
            //行业类型
            if (search.getEntity().getIndustryCategory() != null) {
                lqw.like(IplOdMain::getIndustryCategory, search.getEntity().getIndustryCategory());
            }
            //企业名称
            if (StringUtils.isNotBlank(search.getEntity().getEnterpriseName())) {
                lqw.like(IplOdMain::getEnterpriseName, search.getEntity().getEnterpriseName());
            }
            //岗位名称
            if (StringUtils.isNotBlank(search.getEntity().getJdName())) {
                lqw.like(IplOdMain::getJdName, search.getEntity().getJdName());
            }
            //创建时间
            if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
                long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
                lqw.lt(IplOdMain::getGmtCreate, end);
                long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
                //gt 大于 lt 小于
                lqw.gt(IplOdMain::getGmtCreate, begin);
            }
            //来源
            if (search.getEntity().getSource() != null) {
                lqw.like(IplOdMain::getSource, search.getEntity().getSource());
            }
            //状态
            if (search.getEntity().getStatus() != null) {
                lqw.like(IplOdMain::getStatus, search.getEntity().getStatus());
            }
            //备注状态
            if (search.getEntity().getProcessStatus() != null) {
                lqw.like(IplOdMain::getProcessStatus, search.getEntity().getProcessStatus());
            }
        }
        lqw.orderByDesc(IplOdMain::getGmtCreate);
        IPage<IplOdMain> list = null;
        if (search != null) {
            list = page(search.getPageable(), lqw);
            List<SysCfg> typeList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>().eq(SysCfg::getCfgType, SysCfgEnum.THREE.getId()));
            Map<Long, String> collect = typeList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
            list.getRecords().forEach(is -> {
                //来源名称
                if (is.getSource() != null) {
                    if (SourceEnum.ENTERPRISE.getId().equals(is.getSource())) {
                        is.setSourceName(SourceEnum.ENTERPRISE.getName());
                    } else if (SourceEnum.SELF.getId().equals(is.getSource())) {
                        is.setSourceName("组织部");
                    }
                }
                //备注名称
                if (is.getProcessStatus() != null) {
                    is.setProcessStatusName(ProcessStatusEnum.ofName(is.getProcessStatus()));
                }
                //行业类型
                if ((is.getIndustryCategory() != null) && (collect.get(is.getIndustryCategory()) != null)) {
                    is.setIndustryCategoryName(collect.get(is.getIndustryCategory()));
                }
                //状态名称
                if (is.getStatus() != null) {
                    is.setStatusName(IplStatusEnum.ofName(is.getStatus()));
                }
            });
        }
        return list;
    }

    /**
     * 功能描述 新增编辑
     *
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/9/25 16:26
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEntity(IplOdMain entity) {
        if (entity.getId() == null) {
            entity.setAttachmentCode(UUIDUtil.getUUID());
            //来源为当前局
            entity.setSource(entity.getSource());
            // 状态设为处理中
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            //主责单位设置为组织部
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_OD_ID);
            //进展状态设为进展正常
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
            save(entity);
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME, InnovationConstant.DEPARTMENT_OD_ID);
        } else {
            IplOdMain vo = getById(entity.getId());
            if (IplStatusEnum.DONE.getId().equals(vo.getStatus())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("处理完毕的数据不可编辑").build();
            }
            //处理附件
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachmentList());
            //待处理时
            if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.UPDATE_OVER_TIME,InnovationConstant.DEPARTMENT_OD_ID);
            } else if (IplStatusEnum.DEALING.getId().equals(vo.getStatus())) {
                //处理中 如果超时 则置为进展正常
                entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                iplLogService.saveLog(vo.getId(),
                        IplStatusEnum.DEALING.getId(),
                        InnovationConstant.DEPARTMENT_OD_ID,
                        0L,
                        "更新基本信息");
                entity.setLatestProcess("更新基本信息");
                redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.UPDATE_OVER_TIME,InnovationConstant.DEPARTMENT_OD_ID);
            }
            updateById(entity);
        }
    }

    /**
     * 功能描述 删除接口
     * @param ids ids
     * @author gengzhiqiang
     * @date 2019/9/25 16:53
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeById(List<Long> ids) {
        List<IplOdMain> list = list(new LambdaQueryWrapper<IplOdMain>().in(IplOdMain::getId, ids));
        //状态为处理完毕 不可删除
        List<IplOdMain> doneList = list.stream()
                .filter(i -> IplStatusEnum.DONE.getId().equals(i.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(doneList)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("处理完毕的数据不可删除").build();
        }
        List<String> codes = list.stream().map(IplOdMain::getAttachmentCode).collect(Collectors.toList());
        // 删除主表
        removeByIds(ids);
        // 批量删除主表附带的日志、协同、附件，调用方法必须要有事物
        iplAssistService.batchDel(ids, InnovationConstant.DEPARTMENT_DARB_ID, codes);
    }

    /**
     * 功能描述 详情接口
     * @param entity 对象
     * @return entity 对象
     * @author gengzhiqiang
     * @date 2019/9/25 18:46
     */
    public IplOdMain detailById(IplOdMain entity) {
        IplOdMain vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //来源名称
        if (vo.getSource() != null) {
            if (SourceEnum.SELF.getId().equals(vo.getSource())) {
                vo.setSourceName("组织部");
            } else if (SourceEnum.SELF.getId().equals(vo.getSource())) {
                vo.setSourceName(SourceEnum.ENTERPRISE.getName());
            }
        }
        //行业类型
        if (vo.getIndustryCategory() != null) {
            SysCfg industryCategory = sysCfgService.getById(vo.getIndustryCategory());
            vo.setIndustryCategoryName(industryCategory.getCfgVal());
        }
        //附件
        List<Attachment> attachmentList=attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode,vo.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)){
            vo.setAttachmentList(attachmentList);
        }
        return vo;
    }

}
