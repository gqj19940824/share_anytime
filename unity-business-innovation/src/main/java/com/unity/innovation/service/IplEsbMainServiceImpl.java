
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.util.GsonUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.ExcelStyleUtil;
import com.unity.common.utils.HashRedisUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.dao.IplEsbMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.*;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IplEsbMainService
 * date: 2019-09-25 14:51:39
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class IplEsbMainServiceImpl extends BaseServiceImpl<IplEsbMainDao, IplEsbMain> {

    @Resource
    private SysCfgServiceImpl sysCfgService;

    @Resource
    private AttachmentServiceImpl attachmentService;

    @Resource
    private IplLogServiceImpl iplLogService;

    @Resource
    private IplAssistServiceImpl iplAssistService;

    @Resource
    private RbacClient rbacClient;

    @Resource
    private HashRedisUtils hashRedisUtils;

    @Resource
    private IplManageMainServiceImpl iplManageMainService;

    @Resource
    private SystemConfiguration systemConfiguration;

    @Resource
    private RedisSubscribeServiceImpl redisSubscribeService;

    @Resource
    private SysMessageHelpService sysMessageHelpService;

    /**
     * 功能描述 分页接口
     * @param search 查询条件
     * @return 分页集合
     * @author gengzhiqiang
     * @date 2019/9/25 16:26
     */
    public IPage<IplEsbMain> listByPage(PageEntity<IplEsbMain> search) {
        LambdaQueryWrapper<IplEsbMain> lqw = new LambdaQueryWrapper<>();
        //行业类型
        if (search.getEntity().getIndustryCategory() != null) {
            lqw.like(IplEsbMain::getIndustryCategory, search.getEntity().getIndustryCategory());
        }
        //企业名称
        if (StringUtils.isNotBlank(search.getEntity().getEnterpriseName())) {
            lqw.like(IplEsbMain::getEnterpriseName, search.getEntity().getEnterpriseName());
        }
        //创建时间
        if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
            //gt 大于 lt 小于
            lqw.gt(IplEsbMain::getGmtCreate, begin);
            lqw.lt(IplEsbMain::getGmtCreate, end);
        }
        //来源
        if (search.getEntity().getSource() != null) {
            lqw.like(IplEsbMain::getSource, search.getEntity().getSource());
        }
        //状态
        if (search.getEntity().getStatus() != null) {
            lqw.like(IplEsbMain::getStatus, search.getEntity().getStatus());
        }
        //备注状态
        if (search.getEntity().getProcessStatus() != null) {
            lqw.like(IplEsbMain::getProcessStatus, search.getEntity().getProcessStatus());
        }
        //创新内容：概述
        if (search.getEntity().getSummary() != null) {
            lqw.like(IplEsbMain::getSummary, search.getEntity().getSummary());
        }
        lqw.orderByDesc(IplEsbMain::getGmtCreate);
        IPage<IplEsbMain> list = page(search.getPageable(), lqw);
        List<SysCfg> typeList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>().eq(SysCfg::getCfgType, SysCfgEnum.THREE.getId()));
        Map<Long, String> collect = typeList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        list.getRecords().forEach(is -> {
            //来源名称
            if (is.getSource() != null) {
                if (SourceEnum.ENTERPRISE.getId().equals(is.getSource())) {
                    is.setSourceName(SourceEnum.ENTERPRISE.getName());
                } else if (SourceEnum.SELF.getId().equals(is.getSource())) {
                    is.setSourceName("企业服务局");
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
            StringBuilder stringBuilder = new StringBuilder();
            if (StringUtils.isNotBlank(is.getNewProduct())) {
                stringBuilder.append("新产品：").append(System.getProperty(InnovationConstant.LINE_SEPARATOR)).append(is.getNewProduct()).append(System.getProperty(InnovationConstant.LINE_SEPARATOR));
            }
            if (StringUtils.isNotBlank(is.getNewTech())) {
                stringBuilder.append("新技术：").append(System.getProperty(InnovationConstant.LINE_SEPARATOR)).append(is.getNewTech());
            }
            is.setNewProductAndTech(stringBuilder.toString());
        });
        return list;
    }

    /**
     * 功能描述 新增编辑
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/9/25 16:26
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEntity(IplEsbMain entity) {
        if (entity.getId() == null) {
            entity.setAttachmentCode(UUIDUtil.getUUID());
            //来源为当前局
            entity.setSource(entity.getSource());
            // 状态设为处理中
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            //主责单位设置为企服局  12
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_ESB_ID);
            //进展状态设为进展正常
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
            save(entity);
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME, InnovationConstant.DEPARTMENT_ESB_ID);
            //====企服局====企业新增填报实时清单需求========
            sysMessageHelpService.addInventoryMessage(InventoryMessage.newInstance()
                    .sourceId(entity.getId())
                    .idRbacDepartment(InnovationConstant.DEPARTMENT_ESB_ID)
                    .dataSourceClass(SysMessageDataSourceClassEnum.COOPERATION.getId())
                    .flowStatus(SysMessageFlowStatusEnum.ONE.getId())
                    .title(entity.getEnterpriseName())
                    .build());
        } else {
            IplEsbMain vo = getById(entity.getId());
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
                redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.UPDATE_OVER_TIME,InnovationConstant.DEPARTMENT_ESB_ID);
            }else if (IplStatusEnum.DEALING.getId().equals(vo.getStatus())) {
                //处理中 如果超时 则置为进展正常
                entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                iplLogService.saveLog(vo.getId(),
                        IplStatusEnum.DEALING.getId(),
                        InnovationConstant.DEPARTMENT_ESB_ID,
                        0L,
                        "更新基本信息");
                entity.setLatestProcess("更新基本信息");
                redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.UPDATE_OVER_TIME,InnovationConstant.DEPARTMENT_ESB_ID);
                //======处理中的数据，主责单位再次编辑基本信息--清单协同处理--增加系统消息=======
                List<IplAssist> assists = iplAssistService.getAssists(entity.getIdRbacDepartmentDuty(), entity.getId());
                List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
                sysMessageHelpService.addInventoryMessage(InventoryMessage.newInstance()
                        .sourceId(entity.getId())
                        .idRbacDepartment(InnovationConstant.DEPARTMENT_ESB_ID)
                        .dataSourceClass(SysMessageDataSourceClassEnum.DEVELOPING.getId())
                        .flowStatus(SysMessageFlowStatusEnum.FOUR.getId())
                        .title(entity.getEnterpriseName())
                        .helpDepartmentIdList(assistsIdList)
                        .build());
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
        List<IplEsbMain> list = list(new LambdaQueryWrapper<IplEsbMain>().in(IplEsbMain::getId, ids));
        //状态为处理完毕 不可删除
        List<IplEsbMain> doneList = list.stream()
                .filter(i -> IplStatusEnum.DONE.getId().equals(i.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(doneList)) {
            throw UnityRuntimeException.newInstance()
                    .message("处理完毕的数据不可删除")
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .build();
        }
        List<String> codes = list.stream().map(IplEsbMain::getAttachmentCode).collect(Collectors.toList());

        //======处理中的数据，主责单位删除--清单协同处理--增加系统消息=======
        list.forEach(entity ->{
            List<IplAssist> assists = iplAssistService.getAssists(entity.getIdRbacDepartmentDuty(), entity.getId());
            List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
            sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                    .sourceId(entity.getId())
                    .idRbacDepartment(entity.getIdRbacDepartmentDuty())
                    .dataSourceClass(SysMessageDataSourceClassEnum.DEVELOPING.getId())
                    .flowStatus(SysMessageFlowStatusEnum.FIVES.getId())
                    .title(entity.getEnterpriseName())
                    .helpDepartmentIdList(assistsIdList)
                    .build());
        });

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
    public IplEsbMain detailById(IplEsbMain entity) {
        IplEsbMain vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //来源名称
        if (vo.getSource() != null) {
            if (SourceEnum.SELF.getId().equals(vo.getSource())) {
                vo.setSourceName("企业服务局");
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

    /**
     * 功能描述  添加协同事项接口
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/9/25 18:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAssist(IplEsbMain entity) {
        //主表id  数据集合
        IplEsbMain vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        List<IplAssist> assistList = entity.getAssistList();
        StringBuffer sb=new StringBuffer();
        sb.append("新增协同单位：");
        assistList.forEach(a -> {
            if (a.getIdRbacDepartmentAssist() == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到协同单位id").build();
            }
            //处理状态 正常
            a.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            //主表id
            a.setIdIplMain(vo.getId());
            //主责单位
            a.setIdRbacDepartmentDuty(vo.getIdRbacDepartmentDuty());
            //状态为 正在处理
            a.setDealStatus(IplStatusEnum.UNDEAL.getId());
            a.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            sb.append(hashRedisUtils.getFieldValueByFieldName
                    (RedisConstants.DEPARTMENT + a.getIdRbacDepartmentAssist(), RedisConstants.NAME).toString());
            sb.append("、");
        });
        sb.deleteCharAt(sb.length() - 1);
        //保存协同单位
        iplAssistService.saveBatch(assistList);
        //保存 主责日志
        iplLogService.saveLog(entity.getId(), IplStatusEnum.DEALING.getId(),
                vo.getIdRbacDepartmentDuty(), 0L, sb.toString());
        vo.setLatestProcess(sb.toString());
        //更新主表状态
        if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
            vo.setStatus(IplStatusEnum.DEALING.getId());
        }
        vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
        updateById(vo);
    }



    /**
     * 功能描述 获取协同单位下拉列表
     *
     * @return 单位id及其集合
     * @author gengzhiqiang
     * @date 2019/7/26 16:03
     */
    public List<Map<String, Object>> getAssistList(IplEsbMain entity) {
        //主表id  数据集合
        IplEsbMain vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        List<DepartmentVO> departmentList = rbacClient.getAllDepartment();
        List<IplAssist> assistList = iplAssistService.list(new LambdaQueryWrapper<IplAssist>()
                .eq(IplAssist::getIdIplMain, vo.getId())
                .eq(IplAssist::getIdRbacDepartmentDuty, vo.getIdRbacDepartmentDuty()));
        List<Long> ids = assistList.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
        departmentList = departmentList.stream().filter(d -> !ids.contains(d.getId())).collect(Collectors.toList());
        return JsonUtil.ObjectToList(departmentList, new String[]{"id", "name"}, null);
    }



    /**
     * 功能描述 主责单位处理协同单位接口
     * @param entity  协同单位实体
     * @author gengzhiqiang
     * @date 2019/9/25 18:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void dealAssist(IplAssist entity) {
        //主表id
        IplEsbMain vo = getById(entity.getIdIplMain());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //获取协同单位名称
        String assistDeptName = hashRedisUtils.getFieldValueByFieldName
                (RedisConstants.DEPARTMENT + entity.getIdRbacDepartmentAssist(), RedisConstants.NAME);
        //处理中
        if (IplStatusEnum.DEALING.getId().equals(entity.getDealStatus())) {
            //保存 协同日志
            iplLogService.saveLog(vo.getId(),IplStatusEnum.DEALING.getId(),vo.getIdRbacDepartmentDuty(), entity.getIdRbacDepartmentAssist(),
                    "主责单位开启协同邀请");
            //保存 主责日志
            iplLogService.saveLog(vo.getId(),IplStatusEnum.DEALING.getId(),vo.getIdRbacDepartmentDuty(), 0L,
                    "开启"+assistDeptName+"协同邀请");
            //更新主表状态
            if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                vo.setStatus(IplStatusEnum.DEALING.getId());
            }
            vo.setLatestProcess("开启"+assistDeptName+"协同邀请");
            vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            updateById(vo);
            //更新协同单位状态
            entity.setDealStatus(IplStatusEnum.DEALING.getId());
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            iplAssistService.update(entity, new LambdaUpdateWrapper<IplAssist>()
                    .eq(IplAssist::getIdRbacDepartmentDuty, InnovationConstant.DEPARTMENT_ESB_ID)
                    .eq(IplAssist::getIdIplMain, vo.getId())
                    .eq(IplAssist::getIdRbacDepartmentAssist, entity.getIdRbacDepartmentAssist()));
        } else if (IplStatusEnum.DONE.getId().equals(entity.getDealStatus())) {
            //保存 协同日志
            iplLogService.saveLog(vo.getId(),IplStatusEnum.DONE.getId(),vo.getIdRbacDepartmentDuty(), entity.getIdRbacDepartmentAssist(),
                    "主责单位关闭协同邀请");
            //保存 主责日志
            iplLogService.saveLog(vo.getId(),IplStatusEnum.DONE.getId(),vo.getIdRbacDepartmentDuty(), 0L,
                    "关闭" + assistDeptName + "协同邀请");
            //主表状态
            vo.setStatus(IplStatusEnum.DONE.getId());
            vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            vo.setLatestProcess("关闭"+assistDeptName+"协同邀请");
            updateById(vo);
            //日志表状态
            entity.setDealStatus(IplStatusEnum.DONE.getId());
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            iplAssistService.update(entity, new LambdaUpdateWrapper<IplAssist>()
                    .eq(IplAssist::getIdRbacDepartmentDuty, InnovationConstant.DEPARTMENT_ESB_ID)
                    .eq(IplAssist::getIdIplMain, vo.getId())
                    .eq(IplAssist::getIdRbacDepartmentAssist, entity.getIdRbacDepartmentAssist()));
        }

    }

    /**
     * 功能描述 实时更新处理接口
     * @param entity  实体
     * @author gengzhiqiang
     * @date 2019/9/25 18:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(IplAssist entity) {
        //主表id  数据集合
        IplEsbMain vo = getById(entity.getIdIplMain());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //判断是主责和协同处理 0指的是主责单位
        if (YesOrNoEnum.NO.getType() == entity.getIdRbacDepartmentAssist()) {
            //处理中
            if (IplStatusEnum.DEALING.getId().equals(entity.getDealStatus())) {
                //保存 主责日志
                iplLogService.saveLog(entity.getIdIplMain(), IplStatusEnum.DEALING.getId(),
                        vo.getIdRbacDepartmentDuty(), 0L, entity.getDealMessage());
                //更新主表状态
                if (IplStatusEnum.UNDEAL.getId().equals(vo.getStatus())) {
                    vo.setStatus(IplStatusEnum.DEALING.getId());
                }
                vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                vo.setLatestProcess(entity.getDealMessage());
                updateById(vo);
            } else if (IplStatusEnum.DONE.getId().equals(entity.getDealStatus())) {
                //处理 未完成的协同单位数据
                List<IplAssist> dealData = iplAssistService.list(new LambdaQueryWrapper<IplAssist>()
                        .eq(IplAssist::getIdIplMain, entity.getIdIplMain())
                        .eq(IplAssist::getIdRbacDepartmentDuty, vo.getIdRbacDepartmentDuty())
                        .ne(IplAssist::getIdRbacDepartmentAssist, 0L)
                        .ne(IplAssist::getDealStatus, IplStatusEnum.DONE.getId()));
                if (CollectionUtils.isNotEmpty(dealData)) {
                    List<IplLog> logs = Lists.newArrayList();
                    StringBuffer sb = new StringBuffer();
                    sb.append(entity.getDealMessage());
                    sb.append(System.getProperty(InnovationConstant.LINE_SEPARATOR));
                    sb.append("关闭");
                    dealData.forEach(d -> {
                        //遍历单位名称，拼接日志记录
                        sb.append(hashRedisUtils.getFieldValueByFieldName
                                (RedisConstants.DEPARTMENT + d.getIdRbacDepartmentAssist(), RedisConstants.NAME).toString());
                        sb.append("、");
                        IplLog log = IplLog.newInstance()
                                .dealStatus(IplStatusEnum.DONE.getId())
                                .idIplMain(vo.getId())
                                .idRbacDepartmentDuty(vo.getIdRbacDepartmentDuty())
                                .idRbacDepartmentAssist(d.getIdRbacDepartmentAssist())
                                .processInfo("主责单位关闭协同邀请")
                                .build();
                        logs.add(log);
                    });
                    //保存协同日志
                    iplLogService.saveBatch(logs);
                    //修改协同单位状态
                    IplAssist assist=IplAssist.newInstance()
                            .dealStatus(IplStatusEnum.DONE.getId())
                            .processStatus(ProcessStatusEnum.NORMAL.getId())
                            .build();
                    iplAssistService.update(assist, new LambdaUpdateWrapper<IplAssist>()
                            .eq(IplAssist::getIdRbacDepartmentDuty, InnovationConstant.DEPARTMENT_ESB_ID)
                            .eq(IplAssist::getIdIplMain, vo.getId()));
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append("协同邀请");
                    entity.setDealMessage(sb.toString());
                }
                //保存 主责日志
                iplLogService.saveLog(entity.getIdIplMain(), IplStatusEnum.DONE.getId(),
                        vo.getIdRbacDepartmentDuty(), 0L, entity.getDealMessage());
                vo.setLatestProcess(entity.getDealMessage());
                //更新主表状态
                vo.setStatus(IplStatusEnum.DONE.getId());
                vo.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                updateById(vo);
            }
        }
        //协同处理单位id不为 0 else {}

    }

    /**
     * 功能描述 详情接口
     * @param entity 实体
     * @return com.unity.innovation.entity.generated.IplManageMain 对象
     * @author gengzhiqiang
     * @date 2019/10/9 19:50
     */
    public IplManageMain detailByIdForPkg(IplManageMain entity) {
        entity = iplManageMainService.getById(entity.getId());
        if (entity == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //快照集合
        List<IplEsbMain> list = JSON.parseArray(entity.getSnapshot(), IplEsbMain.class);
        entity.setSnapshot("");
       // entity.setIplEsbMainList(list);
        //附件
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, entity.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            entity.setAttachments(attachmentList);
        }
        //日志集合 日志节点集合
        entity = iplManageMainService.setLogs(entity);
        return entity;
    }



    /**
     * 功能描述 导出接口
     *
     * @param entity 对象
     * @return byte[] 返回数据流
     * @author gengzhiqiang
     * @date 2019/7/8 10:15
     */
    public byte[] export(IplManageMain entity) {
        byte[] content;
        String templatePath = systemConfiguration.getUploadPath() + File.separator + "iplManageEsb" + File.separator;
        String templateFile = templatePath + File.separator + "iplManageEsb.xls";
        File dir = new File(templatePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream out = null;
        try {
            //定义表格对象
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(entity.getTitle());
            HSSFRow row;
            //表头
            Map<String, CellStyle> styleMap = ExcelStyleUtil.createProjectStyles(workbook);
            workbook.createCellStyle();
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styleMap.get("title"));
            titleCell.setCellValue(entity.getTitle());
            row = sheet.createRow(1);
            String[] title = { "行业类别", "企业名称", "企业简介", "创新内容", "联系人","联系方式","创建时间","更新时间","来源","状态","最新进展"};
            sheet.addMergedRegion(new CellRangeAddress(0,0, 0, title.length-1));
            for (int j = 0; j < title.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(title[j]);
                cell.setCellStyle(styleMap.get("title"));
                sheet.autoSizeColumn(j, true);
            }
            //填充数据
            addData(sheet, entity, styleMap);
            out = new FileOutputStream(templateFile);
            // 输出excel
            workbook.write(out);
            out.close();
            File file = new File(templateFile);
            content = FileReaderUtil.getBytes(file);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.SERVER_ERROR, "导出失败");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    private void addData(HSSFSheet sheet, IplManageMain entity, Map<String,CellStyle> styleMap) {
        List<IplEsbMain> list = GsonUtils.parse(entity.getSnapshot(), new TypeToken<List<IplEsbMain>>() {
        });
        CellStyle sty = styleMap.get("data");
        int rowNum = 2;
        for (int j = 0; j < list.size(); j++) {
            HSSFRow row = sheet.createRow(rowNum++);
            HSSFCell cell0 = row.createCell(0);
            HSSFCell cell1 = row.createCell(1);
            HSSFCell cell2 = row.createCell(2);
            HSSFCell cell3 = row.createCell(3);
            HSSFCell cell4 = row.createCell(4);
            HSSFCell cell5 = row.createCell(5);
            HSSFCell cell6 = row.createCell(6);
            HSSFCell cell7 = row.createCell(7);
            HSSFCell cell8 = row.createCell(8);
            HSSFCell cell9 = row.createCell(9);
            HSSFCell cell10 = row.createCell(10);
            cell0.setCellStyle(sty);
            sheet.setColumnWidth(0, 10*256);
            cell1.setCellStyle(sty);
            sheet.setColumnWidth(1, 30*256);
            cell2.setCellStyle(sty);
            sheet.setColumnWidth(2, 60*256);
            cell3.setCellStyle(sty);
            sheet.setColumnWidth(3, 60*256);
            cell4.setCellStyle(sty);
            sheet.setColumnWidth(4, 15*256);
            cell5.setCellStyle(sty);
            sheet.setColumnWidth(5, 15*256);
            cell6.setCellStyle(sty);
            sheet.setColumnWidth(6, 18*256);
            cell7.setCellStyle(sty);
            sheet.setColumnWidth(7, 18*256);
            cell8.setCellStyle(sty);
            sheet.setColumnWidth(8, 10*256);
            cell9.setCellStyle(sty);
            sheet.setColumnWidth(9, 10*256);
            cell10.setCellStyle(sty);
            sheet.setColumnWidth(10, 30*256);
            cell0.setCellValue(list.get(j).getIndustryCategoryName());
            cell1.setCellValue(list.get(j).getEnterpriseName());
            cell2.setCellValue(list.get(j).getEnterpriseProfile());
            if (StringUtils.isNotBlank(list.get(j).getNewProductAndTech())) {
                cell3.setCellValue(list.get(j).getNewProductAndTech());
            }
            cell4.setCellValue(list.get(j).getContactPerson());
            cell5.setCellValue(list.get(j).getContactWay());
            cell6.setCellValue(DateUtils.timeStamp2Date(list.get(j).getGmtCreate()));
            cell7.setCellValue(DateUtils.timeStamp2Date(list.get(j).getGmtModified()));
            cell8.setCellValue(list.get(j).getSourceName());
            cell9.setCellValue(list.get(j).getStatusName());
            if (StringUtils.isNotBlank(list.get(j).getLatestProcess())) {
                cell10.setCellValue(list.get(j).getLatestProcess());
            }
        }
        Row titleRow = sheet.createRow(list.size() + 2);
        Cell titleCell = titleRow.createCell(0);
        CellStyle style = styleMap.get("note");
        titleCell.setCellStyle(style);
        if (StringUtils.isNotBlank(entity.getNotes())) {
            titleCell.setCellValue("备注："+entity.getNotes());
        }else{
            titleCell.setCellValue("备注：");
        }
        CellRangeAddress range = new CellRangeAddress(list.size() + 2, list.size() + 2, 0, 10);
        sheet.addMergedRegion(range);
        RegionUtil.setBorderLeft(1, range, sheet);
        RegionUtil.setBorderBottom(1, range, sheet);
        RegionUtil.setBorderRight(1, range, sheet);
        RegionUtil.setBorderTop(1, range, sheet);
    }


}
