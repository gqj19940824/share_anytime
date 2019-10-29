
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.reflect.TypeToken;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.InventoryMessage;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.util.GsonUtils;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.ExcelStyleUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ListTypeConstants;
import com.unity.innovation.dao.IplOdMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.*;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
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

    @Resource
    private SystemConfiguration systemConfiguration;

    @Resource
    private SysMessageHelpService sysMessageHelpService;

    @Resource
    private DicUtils dicUtils;

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
                lqw.eq(IplOdMain::getIndustryCategory, search.getEntity().getIndustryCategory());
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
        Long departmentId = Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.INTELLIGENCE.getType() + ""));
        if (entity.getId() == null) {
            //判断当前用户是否为操作单位
            if (SourceEnum.SELF.getId().equals((entity.getSource()))) {
                check();
            }
            //来源为当前局
            entity.setSource(entity.getSource());
            entity.setAttachmentCode(UUIDUtil.getUUID());
            // 状态设为处理中
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            //主责单位设置为组织部
            entity.setIdRbacDepartmentDuty(departmentId);
            //进展状态设为进展正常
            entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
            save(entity);
            redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME,departmentId,BizTypeEnum.INTELLIGENCE.getType());
            //========企业新增填报实时清单需求========
            if(entity.getSource().equals(SourceEnum.ENTERPRISE.getId())) {
                //企业需求填报才进行系统通知
                sysMessageHelpService.addInventoryMessage(InventoryMessage.newInstance()
                        .sourceId(entity.getId())
                        .idRbacDepartment(departmentId)
                        .dataSourceClass(SysMessageDataSourceClassEnum.DEMAND.getId())
                        .flowStatus(SysMessageFlowStatusEnum.ONE.getId())
                        .title(entity.getEnterpriseName())
                        .bizType(BizTypeEnum.INTELLIGENCE.getType())
                        .build());
            }
        } else {
            check();
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
                redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.DEAL_OVER_TIME, departmentId,BizTypeEnum.INTELLIGENCE.getType());
            } else if (IplStatusEnum.DEALING.getId().equals(vo.getStatus())) {
                //处理中 如果超时 则置为进展正常
                entity.setProcessStatus(ProcessStatusEnum.NORMAL.getId());
                iplLogService.saveLog(vo.getId(),
                        IplStatusEnum.DEALING.getId(),
                        departmentId,
                        0L,
                        "更新基本信息",BizTypeEnum.INTELLIGENCE.getType());
                entity.setLatestProcess("更新基本信息");
                redisSubscribeService.saveSubscribeInfo(entity.getId() + "-0", ListTypeConstants.UPDATE_OVER_TIME, departmentId,BizTypeEnum.INTELLIGENCE.getType());
                //======处理中的数据，主责单位再次编辑基本信息--清单协同处理--增加系统消息=======
                List<IplAssist> assists = iplAssistService.getAssists(BizTypeEnum.INTELLIGENCE.getType(), entity.getId());
                List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
                sysMessageHelpService.addInventoryMessage(InventoryMessage.newInstance()
                        .sourceId(entity.getId())
                        .idRbacDepartment(departmentId)
                        .dataSourceClass(SysMessageDataSourceClassEnum.DEMAND.getId())
                        .flowStatus(SysMessageFlowStatusEnum.FOUR.getId())
                        .title(entity.getEnterpriseName())
                        .helpDepartmentIdList(assistsIdList)
                        .bizType(BizTypeEnum.INTELLIGENCE.getType())
                        .build());
            }
            updateById(entity);
        }
    }

    /**
     * 功能描述 删除接口
     *
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
        //======处理中的数据，主责单位删除--清单协同处理--增加系统消息=======
        list.forEach(entity ->{
            List<IplAssist> assists = iplAssistService.getAssists(BizTypeEnum.INTELLIGENCE.getType(), entity.getId());
            List<Long> assistsIdList = assists.stream().map(IplAssist::getIdRbacDepartmentAssist).collect(Collectors.toList());
            sysMessageHelpService.addInventoryHelpMessage(InventoryMessage.newInstance()
                    .sourceId(entity.getId())
                    .idRbacDepartment(entity.getIdRbacDepartmentDuty())
                    .dataSourceClass(SysMessageDataSourceClassEnum.DEMAND.getId())
                    .flowStatus(SysMessageFlowStatusEnum.FIVES.getId())
                    .title(entity.getEnterpriseName())
                    .helpDepartmentIdList(assistsIdList)
                    .bizType(BizTypeEnum.INTELLIGENCE.getType())
                    .build());
        });
        // 删除主表
        removeByIds(ids);
        // 批量删除主表附带的日志、协同、附件，调用方法必须要有事物
        Long departmentId = Long.parseLong(dicUtils.getDicValueByCode(DicConstants.DEPART_HAVE_LIST_TYPE, BizTypeEnum.INTELLIGENCE.getType().toString()));
        iplAssistService.batchDel(ids, list, codes,BizTypeEnum.INTELLIGENCE.getType());
    }

    /**
     * 功能描述 详情接口
     *
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
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, vo.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            vo.setAttachmentList(attachmentList);
        }
        return vo;
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
        String templatePath = systemConfiguration.getUploadPath() + File.separator + "iplManageOd" + File.separator;
        String templateFile = templatePath + File.separator + "iplManageOd.xls";
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
            String[] title = {"行业类别", "企业名称", "企业简介", "岗位需求名称", "岗位需求数量", "需求人员专业领域",
                    "工作职责", "任职资格", "支持条件和福利待遇", "联系人", "联系方式", "创建时间", "更新时间", "来源", "状态", "最新进展"};
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, title.length - 1));
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

    private void addData(HSSFSheet sheet, IplManageMain entity, Map<String, CellStyle> styleMap) {
        List<IplOdMain> list = GsonUtils.parse(entity.getSnapshot(), new TypeToken<List<IplOdMain>>() {
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
            HSSFCell cell11 = row.createCell(11);
            HSSFCell cell12 = row.createCell(12);
            HSSFCell cell13 = row.createCell(13);
            HSSFCell cell14 = row.createCell(14);
            HSSFCell cell15 = row.createCell(15);
            cell0.setCellStyle(sty);
            sheet.setColumnWidth(0, 10 * 256);
            cell1.setCellStyle(sty);
            sheet.setColumnWidth(1, 30 * 256);
            cell2.setCellStyle(sty);
            sheet.setColumnWidth(2, 60 * 256);
            cell3.setCellStyle(sty);
            sheet.setColumnWidth(3, 30 * 256);
            cell4.setCellStyle(sty);
            sheet.setColumnWidth(4, 15 * 256);
            cell5.setCellStyle(sty);
            sheet.setColumnWidth(5, 20 * 256);
            cell6.setCellStyle(sty);
            sheet.setColumnWidth(6, 30 * 256);
            cell7.setCellStyle(sty);
            sheet.setColumnWidth(7, 30 * 256);
            cell8.setCellStyle(sty);
            sheet.setColumnWidth(8, 30 * 256);
            cell9.setCellStyle(sty);
            sheet.setColumnWidth(9, 15 * 256);
            cell10.setCellStyle(sty);
            sheet.setColumnWidth(10, 15 * 256);
            cell11.setCellStyle(sty);
            sheet.setColumnWidth(11, 18 * 256);
            cell12.setCellStyle(sty);
            sheet.setColumnWidth(12, 18 * 256);
            cell13.setCellStyle(sty);
            sheet.setColumnWidth(13, 10 * 256);
            cell14.setCellStyle(sty);
            sheet.setColumnWidth(14, 10 * 256);
            cell15.setCellStyle(sty);
            sheet.setColumnWidth(15, 30 * 256);
            cell0.setCellValue(list.get(j).getIndustryCategoryName());
            cell1.setCellValue(list.get(j).getEnterpriseName());
            cell2.setCellValue(list.get(j).getEnterpriseIntroduction());
            cell3.setCellValue(list.get(j).getJdName());
            cell4.setCellValue(list.get(j).getJobDemandNum());
            cell5.setCellValue(list.get(j).getMajorDemand());
            cell6.setCellValue(list.get(j).getDuty());
            cell7.setCellValue(list.get(j).getQualification());
            cell8.setCellValue(list.get(j).getSpecificCause());
            cell9.setCellValue(list.get(j).getContactPerson());
            cell10.setCellValue(list.get(j).getContactWay());
            cell11.setCellValue(DateUtils.timeStamp2Date(list.get(j).getGmtCreate()));
            cell12.setCellValue(DateUtils.timeStamp2Date(list.get(j).getGmtModified()));
            cell13.setCellValue(list.get(j).getSourceName());
            cell14.setCellValue(list.get(j).getStatusName());
            if (StringUtils.isNotBlank(list.get(j).getLatestProcess())) {
                cell15.setCellValue(list.get(j).getLatestProcess());
            }
        }
        Row titleRow = sheet.createRow(list.size() + 2);
        Cell titleCell = titleRow.createCell(0);
        CellStyle style = styleMap.get("note");
        titleCell.setCellStyle(style);
        if (StringUtils.isNotBlank(entity.getNotes())) {
            titleCell.setCellValue("备注：" + entity.getNotes());
        } else {
            titleCell.setCellValue("备注：");
        }
        CellRangeAddress range = new CellRangeAddress(list.size() + 2, list.size() + 2, 0, 15);
        sheet.addMergedRegion(range);
        RegionUtil.setBorderLeft(1, range, sheet);
        RegionUtil.setBorderBottom(1, range, sheet);
        RegionUtil.setBorderRight(1, range, sheet);
        RegionUtil.setBorderTop(1, range, sheet);
    }

    public void check(){
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (!customer.getTypeRangeList().contains(BizTypeEnum.INTELLIGENCE.getType())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("当前账号的单位不可操作数据").build();
        }
    }
}
