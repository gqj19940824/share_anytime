
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.reflect.TypeToken;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.ReviewMessage;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.util.GsonUtils;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.ExcelStyleUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.common.constant.ParamConstants;
import com.unity.innovation.dao.PmInfoDeptDao;
import com.unity.innovation.entity.*;
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
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
public class PmInfoDeptServiceImpl extends BaseServiceImpl<PmInfoDeptDao, PmInfoDept> {

    @Resource
    private DicUtils dicUtils;
    @Resource
    private AttachmentServiceImpl attachmentService;
    @Resource
    private PmInfoDeptLogServiceImpl logService;
    @Resource
    private InfoDeptSatbServiceImpl satbService;
    @Resource
    private InfoDeptYzgtServiceImpl yzgtService;
    @Resource
    private SystemConfiguration systemConfiguration;
    @Resource
    private IplYzgtMainServiceImpl iplYzgtMainService;
    @Resource
    private SysMessageHelpService sysMessageHelpService;

    /**
     * 从二次打包中删除
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/17 8:43 下午
     */
    public void updateIdIpaMain(List<Long> ids, List<Long> idIpaMains) {
        baseMapper.updateIdIpaMain(ids, idIpaMains);
    }


    /**
     * 查询条件封装
     *
     * @param entity 实体
     * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.PmInfoDept>
     * @author JH
     * @date 2019/10/17 11:14
     */
    public LambdaQueryWrapper<PmInfoDept> wrapper(PmInfoDept entity) {
        LambdaQueryWrapper<PmInfoDept> ew = new LambdaQueryWrapper<>();
        if (entity != null) {
            //提交时间
            if (StringUtils.isNotBlank(entity.getSubmitTime())) {
                long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
                ew.lt(PmInfoDept::getGmtSubmit, end);
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
                ew.gt(PmInfoDept::getGmtSubmit, begin);
            }

            if (entity.getBizType() != null) {
                ew.eq(PmInfoDept::getBizType, entity.getBizType());
            } else {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                        .message("提交单位不能为空").build();
            }


            //状态
            if (entity.getStatus() != null) {
                ew.eq(PmInfoDept::getStatus, entity.getStatus());
            }

            //排序
            ew.orderByDesc(PmInfoDept::getGmtSubmit, PmInfoDept::getGmtModified);
        } else {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("信息类型不能为空").build();
        }
        return ew;
    }

    /**
     * 根据单位名称字符串获取单位id
     *
     * @param category 单位名称字符串
     * @return java.lang.Long
     * @author JH
     * @date 2019/10/17 11:11
     */
    private Long getDepartmentId(String category) {
        if (StringUtils.isBlank(category)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("提交单位不能为空").build();
        }
        ListCategoryEnum listCategoryEnum = ListCategoryEnum.valueOfName(category);
        if (listCategoryEnum != null) {
            return listCategoryEnum.getId();
        } else {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("提交单位错误").build();
        }
    }

    /**
     * 功能描述 新增编辑提交
     *
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/10/17 9:42
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveEntity(PmInfoDept entity) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        Long departmentId = customer.getIdRbacDepartment();
        List<Long> ids = entity.getDataIdList();
        if (BizTypeEnum.INVESTMENT.getType().equals(entity.getBizType())) {
            List<IplYzgtMain> investList = entity.getInvestList();
            investList.sort(comparing(IplYzgtMain::getGmtCreate).reversed());
            investList.forEach(n ->{
                n.setCreateTime(DateUtils.timeStamp2Date(n.getGmtCreate()));
                n.setModifiedTime(DateUtils.timeStamp2Date(n.getGmtModified()));
            });
            String snapshot = GsonUtils.format(investList);
            entity.setSnapShot(snapshot);
        }
        if (entity.getId() == null) {
            //单位
            entity.setIdRbacDepartment(departmentId);
            //附件
            entity.setAttachmentCode(UUIDUtil.getUUID());
            entity.setStatus(WorkStatusAuditingStatusEnum.TEN.getId());
            //提交时间设置最大
            entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);
            save(entity);
            //处理集合数据
            updateIds(entity.getId(), ids, entity.getBizType());
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
        } else {
            //编辑
            PmInfoDept vo = getById(entity.getId());
            if (vo == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到对象").build();
            }
            if (!(WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getStatus()) ||
                    WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getStatus()))) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("只有待提交和已驳回状态下数据可编辑").build();
            }
            updateIds(vo.getId(), ids, entity.getBizType());
            updateById(entity);
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachmentList());
        }
        return entity.getId();
    }

    /**
     * 功能描述 打包处理基础数据和主表的关系
     *
     * @param id      主表id
     * @param ids     基础数据id集合
     * @param bizType 信息类型
     * @author gengzhiqiang
     * @date 2019/10/17 11:17
     */
    private void updateIds(Long id, List<Long> ids, Integer bizType) {
        if (BizTypeEnum.LYDEPTINFO.getType().equals(bizType)) {
            //科技局
            //数据库里存的数据
            List<InfoDeptSatb> infoDeptSatbList = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>()
                    .eq(InfoDeptSatb::getIdPmInfoDept, id)
                    .eq(InfoDeptSatb::getStatus, YesOrNoEnum.YES.getType()));
            //数据库里没有数据 全部新增
            if (CollectionUtils.isEmpty(infoDeptSatbList)) {
                //所添加数据中存在已提请发布的数据，请重新添加！
                List<InfoDeptSatb> history = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>()
                        .in(InfoDeptSatb::getId, ids)
                        .eq(InfoDeptSatb::getStatus, YesOrNoEnum.YES.getType()));
                if (CollectionUtils.isNotEmpty(history)) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                            .message("所添加数据中存在已提请发布的数据，请重新添加！").build();
                }
                InfoDeptSatb infoDeptSatb = InfoDeptSatb.newInstance().build();
                infoDeptSatb.setIdPmInfoDept(id);
                infoDeptSatb.setStatus(YesOrNoEnum.YES.getType());
                satbService.update(infoDeptSatb, new LambdaQueryWrapper<InfoDeptSatb>()
                        .in(InfoDeptSatb::getId, ids));
                return;
            }
            //数据库里存的id集合
            List<Long> dbList = infoDeptSatbList.stream().map(InfoDeptSatb::getId).collect(Collectors.toList());
            //新增   前台传来的  数据库里没有
            List<Long> add = ids.stream().filter(i -> !dbList.contains(i)).collect(Collectors.toList());
            //所添加数据中存在已提请发布的数据，请重新添加！
            if (CollectionUtils.isNotEmpty(add)) {
                List<InfoDeptSatb> history = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>()
                        .in(InfoDeptSatb::getId, add)
                        .eq(InfoDeptSatb::getStatus, YesOrNoEnum.YES.getType()));
                if (CollectionUtils.isNotEmpty(history)) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                            .message("所添加数据中存在已提请发布的数据，请重新添加！").build();
                }
                InfoDeptSatb addInfoDeptSatb = InfoDeptSatb.newInstance().build();
                addInfoDeptSatb.setIdPmInfoDept(id);
                addInfoDeptSatb.setStatus(YesOrNoEnum.YES.getType());
                satbService.update(addInfoDeptSatb, new LambdaQueryWrapper<InfoDeptSatb>()
                        .in(InfoDeptSatb::getId, add));
            }
            //删除  数据库里有 但是前台没传的
            List<Long> delete = dbList.stream().filter(i -> !ids.contains(i)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(delete)) {
                InfoDeptSatb deleteInfoDeptSatb = InfoDeptSatb.newInstance().build();
                deleteInfoDeptSatb.setIdPmInfoDept(0L);
                deleteInfoDeptSatb.setStatus(YesOrNoEnum.NO.getType());
                satbService.update(deleteInfoDeptSatb, new LambdaQueryWrapper<InfoDeptSatb>()
                        .in(InfoDeptSatb::getId, delete));
            }
        } else if (BizTypeEnum.RQDEPTINFO.getType().equals(bizType)) {
            //亦庄国投
            //数据库里存的数据
            List<InfoDeptYzgt> infoDeptYzgtList = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>()
                    .eq(InfoDeptYzgt::getIdPmInfoDept, id)
                    .eq(InfoDeptYzgt::getStatus, YesOrNoEnum.YES.getType()));
            //数据库里没有数据 全部新增
            if (CollectionUtils.isEmpty(infoDeptYzgtList)) {
                //所添加数据中存在已提请发布的数据，请重新添加！
                List<InfoDeptYzgt> history = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>()
                        .in(InfoDeptYzgt::getId, ids)
                        .eq(InfoDeptYzgt::getStatus, YesOrNoEnum.YES.getType()));
                if (CollectionUtils.isNotEmpty(history)) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                            .message("所添加数据中存在已提请发布的数据，请重新添加！").build();
                }
                InfoDeptYzgt infoDeptYzgt = InfoDeptYzgt.newInstance().build();
                infoDeptYzgt.setIdPmInfoDept(id);
                infoDeptYzgt.setStatus(YesOrNoEnum.YES.getType());
                yzgtService.update(infoDeptYzgt, new LambdaQueryWrapper<InfoDeptYzgt>()
                        .in(InfoDeptYzgt::getId, ids));
                return;
            }
            //数据库里存的id集合
            List<Long> dbList = infoDeptYzgtList.stream().map(InfoDeptYzgt::getId).collect(Collectors.toList());
            //新增   前台传来的  数据库里没有
            List<Long> add = ids.stream().filter(i -> !dbList.contains(i)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(add)) {
                //所添加数据中存在已提请发布的数据，请重新添加！
                List<InfoDeptYzgt> history = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>()
                        .in(InfoDeptYzgt::getId, add)
                        .eq(InfoDeptYzgt::getStatus, YesOrNoEnum.YES.getType()));
                if (CollectionUtils.isNotEmpty(history)) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                            .message("所添加数据中存在已提请发布的数据，请重新添加！").build();
                }
                InfoDeptYzgt addInfoDeptYzgt = InfoDeptYzgt.newInstance().build();
                addInfoDeptYzgt.setIdPmInfoDept(id);
                addInfoDeptYzgt.setStatus(YesOrNoEnum.YES.getType());
                yzgtService.update(addInfoDeptYzgt, new LambdaQueryWrapper<InfoDeptYzgt>()
                        .in(InfoDeptYzgt::getId, add));
            }
            //删除  数据库里有 但是前台没传的
            List<Long> delete = dbList.stream().filter(i -> !ids.contains(i)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(delete)) {
                InfoDeptYzgt deleteInfoDeptYzgt = InfoDeptYzgt.newInstance().build();
                deleteInfoDeptYzgt.setIdPmInfoDept(0L);
                deleteInfoDeptYzgt.setStatus(YesOrNoEnum.NO.getType());
                yzgtService.update(deleteInfoDeptYzgt, new LambdaQueryWrapper<InfoDeptYzgt>()
                        .in(InfoDeptYzgt::getId, delete));
            }
        }
    }


    /**
     * 批量删除
     *
     * @param ids 主键集合
     * @author JH
     * @date 2019/10/17 11:12
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDeptInfoByIds(List<Long> ids) {
        List<PmInfoDept> list = (List<PmInfoDept>) super.listByIds(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("数据不存在").build();
        }
        List<PmInfoDept> collect = list.stream().filter(n -> !WorkStatusAuditingStatusEnum.FORTY.getId().equals(n.getStatus()) && !WorkStatusAuditingStatusEnum.TEN.getId().equals(n.getStatus())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("该状态无法删除").build();
        }
        //删除主表
        super.removeByIds(ids);
        //附件集合
        List<String> attachmentCodeList = list.stream().map(PmInfoDept::getAttachmentCode).collect(Collectors.toList());
        //删除附件表
        attachmentService.remove(new LambdaUpdateWrapper<Attachment>().in(Attachment::getAttachmentCode, attachmentCodeList));
        //删除日志表
        logService.remove(new LambdaUpdateWrapper<PmInfoDeptLog>().in(PmInfoDeptLog::getIdPmInfoDept, ids));
        //单位id
        Integer bizType = list.get(0).getBizType();
        //修改基础数据表状态
        if (BizTypeEnum.RQDEPTINFO.getType().equals(bizType)) {
            List<InfoDeptYzgt> yzgtList = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>().in(InfoDeptYzgt::getIdPmInfoDept, ids));
            yzgtList.forEach(n -> {
                n.setIdPmInfoDept(0L);
                n.setStatus(IsCommitEnum.NO.getId());
            });
            yzgtService.updateBatchById(yzgtList);
        } else if (BizTypeEnum.LYDEPTINFO.getType().equals(bizType)) {
            List<InfoDeptSatb> satbList = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>().in(InfoDeptSatb::getIdPmInfoDept, ids));
            satbList.forEach(n -> {
                n.setIdPmInfoDept(0L);
                n.setStatus(IsCommitEnum.NO.getId());
            });
            satbService.updateBatchById(satbList);

        }
    }

    /**
     * 通过/驳回
     *
     * @param entity 实体
     * @param old    原有数据
     * @author JH
     * @date 2019/10/12 17:27
     */
    @Transactional(rollbackFor = Exception.class)
    public void passOrReject(PmInfoDeptLog entity, PmInfoDept old) {
        //通过
        if (YesOrNoEnum.NO.getType() == entity.getPassOrReject()) {
            old.setStatus(WorkStatusAuditingStatusEnum.FORTY.getId());
            //驳回
        } else {
            old.setStatus(WorkStatusAuditingStatusEnum.THIRTY.getId());
        }
        super.updateById(old);
        //记录日志
        entity.setStatus(old.getStatus());
        entity.setIdRbacDepartment(old.getIdRbacDepartment());
        entity.setIdPmInfoDept(old.getId());
        entity.setId(null);
        logService.save(entity);
        /*======================2个企业信息发布管理--通过/驳回======================系统通知======================*/
        //通过清单类型获取数据分类
        sysMessageHelpService.addReviewMessage(ReviewMessage.newInstance()
                .dataSourceClass(old.getBizType().equals(BizTypeEnum.RQDEPTINFO.getType())
                        ? SysMessageDataSourceClassEnum.YZGT_RELEASE_REVIEW.getId() : SysMessageDataSourceClassEnum.SATB_RELEASE_REVIEW.getId())
                .flowStatus(YesOrNoEnum.YES.getType() == entity.getPassOrReject()
                        ? SysMsgFlowStatusEnum.THREE.getId() : SysMsgFlowStatusEnum.TWO.getId())
                .idRbacDepartment(old.getIdRbacDepartment())
                .sourceId(old.getId())
                .title(old.getTitle())
                .build());
    }


    /**
     * 功能描述 导出接口
     *
     * @param entity 对象
     * @return byte[] 返回数据流
     * @author gengzhiqiang
     * @date 2019/7/8 10:15
     */
    public byte[] export(PmInfoDept entity) {
        byte[] content;
        String templatePath = systemConfiguration.getUploadPath() + File.separator + "PmInfoDept" + File.separator;
        String templateFile = templatePath + File.separator + "PmInfoDept.xls";
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
            Integer bizType = entity.getBizType();
            String[] title = getTitle(bizType);
            CellRangeAddress range = new CellRangeAddress(0,0, 0, title.length-1);
            sheet.addMergedRegion(range);
            RegionUtil.setBorderRight(1, range, sheet);
            for (int j = 0; j < title.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(title[j]);
                cell.setCellStyle(styleMap.get("title"));
                sheet.autoSizeColumn(j, true);
            }
            //填充数据
            if (BizTypeEnum.LYDEPTINFO.getType().equals(bizType)) {
                addSatb(sheet, entity, styleMap);
            } else if (BizTypeEnum.RQDEPTINFO.getType().equals(bizType)) {
                addYzgt(sheet, entity, styleMap);
            } else {

            }
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

    private String[] getTitle(Integer bizType) {
        String[] ly = { "行业类别", "企业规模", "企业性质","企业名称", "企业简介", "创新成果",
                "成果创新水平", "是否首次对外发布", "备注", "联系人", "联系方式", "附件", "创建时间"};
        String[] rq = { "行业类别", "企业规模", "企业性质","企业名称", "企业简介",
                "备注", "联系人", "联系方式", "附件", "创建时间"};
        if (BizTypeEnum.LYDEPTINFO.getType().equals(bizType)) {
            return ly;
        } else if (BizTypeEnum.RQDEPTINFO.getType().equals(bizType)) {
            return rq;
        } else {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("单位数据").build();
        }
    }

    private void addSatb(HSSFSheet sheet, PmInfoDept entity, Map<String, CellStyle> styleMap) {
        List<InfoDeptSatb> list = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>()
                .eq(InfoDeptSatb::getIdPmInfoDept, entity.getId()));
        if (CollectionUtils.isNotEmpty(list)) {
            satbService.dealData(list);
        }
        List<String> collect = list.stream().map(InfoDeptSatb::getAttachmentCode).collect(Collectors.toList());
        List<Attachment> attachments = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, collect));
        Map<String, List<Attachment>> collect1 = attachments.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));
        list.forEach(s -> s.setAttachmentList(collect1.get(s.getAttachmentCode())));
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
            cell0.setCellStyle(sty);
            sheet.setColumnWidth(0, 10 * 256);
            cell1.setCellStyle(sty);
            sheet.setColumnWidth(1, 10 * 256);
            cell2.setCellStyle(sty);
            sheet.setColumnWidth(2, 10 * 256);
            cell3.setCellStyle(sty);
            sheet.setColumnWidth(3, 30 * 256);
            cell4.setCellStyle(sty);
            sheet.setColumnWidth(4, 30 * 256);
            cell5.setCellStyle(sty);
            sheet.setColumnWidth(5, 30 * 256);
            cell6.setCellStyle(sty);
            sheet.setColumnWidth(6, 15 * 256);
            cell7.setCellStyle(sty);
            sheet.setColumnWidth(7, 18 * 256);
            cell8.setCellStyle(sty);
            sheet.setColumnWidth(8, 30 * 256);
            cell9.setCellStyle(sty);
            sheet.setColumnWidth(9, 15 * 256);
            cell10.setCellStyle(sty);
            sheet.setColumnWidth(10, 15 * 256);
            cell11.setCellStyle(sty);
            sheet.setColumnWidth(11, 90 * 256);
            cell12.setCellStyle(sty);
            sheet.setColumnWidth(12, 18 * 256);
            cell0.setCellValue(list.get(j).getIndustryCategoryName());
            cell1.setCellValue(list.get(j).getEnterpriseScaleName());
            cell2.setCellValue(list.get(j).getEnterpriseNatureName());
            cell3.setCellValue(list.get(j).getEnterpriseName());
            cell4.setCellValue(list.get(j).getEnterpriseIntroduction());
            if (StringUtils.isNotBlank(list.get(j).getInDetail())) {
                cell5.setCellValue(list.get(j).getInDetail());
            }
            cell6.setCellValue(list.get(j).getAchievementLevelName());
            cell7.setCellValue(list.get(j).getIsPublishFirst() == YesOrNoEnum.YES.getType() ? "是" : "否");
            if (StringUtils.isNotBlank(list.get(j).getNotes())) {
                cell8.setCellValue(list.get(j).getNotes());
            }
            cell9.setCellValue(list.get(j).getContactPerson());
            cell10.setCellValue(list.get(j).getContactWay());
            if (CollectionUtils.isNotEmpty(list.get(j).getAttachmentList())) {
                String u = list.get(j).getAttachmentList().stream().map(Attachment::getUrl).collect(Collectors.joining(System.getProperty(InnovationConstant.LINE_SEPARATOR)));
                cell11.setCellValue(u);
            }
            cell12.setCellValue(DateUtils.timeStamp2Date(list.get(j).getGmtCreate()));
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
        CellRangeAddress range = new CellRangeAddress(list.size() + 2, list.size() + 2, 0, 12);
        sheet.addMergedRegion(range);
        RegionUtil.setBorderLeft(1, range, sheet);
        RegionUtil.setBorderBottom(1, range, sheet);
        RegionUtil.setBorderRight(1, range, sheet);
        RegionUtil.setBorderTop(1, range, sheet);
    }

    private void addYzgt(HSSFSheet sheet, PmInfoDept entity, Map<String, CellStyle> styleMap) {
        List<InfoDeptYzgt> list = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>()
                .eq(InfoDeptYzgt::getIdPmInfoDept, entity.getId()));
        yzgtService.convert2List(list);
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
            cell0.setCellStyle(sty);
            sheet.setColumnWidth(0, 10 * 256);
            cell1.setCellStyle(sty);
            sheet.setColumnWidth(1, 10 * 256);
            cell2.setCellStyle(sty);
            sheet.setColumnWidth(2, 10 * 256);
            cell3.setCellStyle(sty);
            sheet.setColumnWidth(3, 30 * 256);
            cell4.setCellStyle(sty);
            sheet.setColumnWidth(4, 30 * 256);
            cell5.setCellStyle(sty);
            sheet.setColumnWidth(5, 30 * 256);
            cell6.setCellStyle(sty);
            sheet.setColumnWidth(6, 15 * 256);
            cell7.setCellStyle(sty);
            sheet.setColumnWidth(7, 15 * 256);
            cell8.setCellStyle(sty);
            sheet.setColumnWidth(8, 90 * 256);
            cell9.setCellStyle(sty);
            sheet.setColumnWidth(9, 18 * 256);
            cell0.setCellValue(list.get(j).getIndustryCategoryName());
            cell1.setCellValue(list.get(j).getEnterpriseScaleName());
            cell2.setCellValue(list.get(j).getEnterpriseNatureName());
            cell3.setCellValue(list.get(j).getEnterpriseName());
            cell4.setCellValue(list.get(j).getEnterpriseIntroduction());
            if (StringUtils.isNotBlank(list.get(j).getNotes())) {
                cell5.setCellValue(list.get(j).getNotes());
            }
            cell6.setCellValue(list.get(j).getContactPerson());
            cell7.setCellValue(list.get(j).getContactWay());
            if (CollectionUtils.isNotEmpty(list.get(j).getAttachmentList())) {
                String u = list.get(j).getAttachmentList().stream().map(Attachment::getUrl).collect(Collectors.joining(System.getProperty(InnovationConstant.LINE_SEPARATOR)));
                cell8.setCellValue(u);
            }
            cell9.setCellValue(DateUtils.timeStamp2Date(list.get(j).getGmtCreate()));
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
        CellRangeAddress range = new CellRangeAddress(list.size() + 2, list.size() + 2, 0, 9);
        sheet.addMergedRegion(range);
        RegionUtil.setBorderLeft(1, range, sheet);
        RegionUtil.setBorderBottom(1, range, sheet);
        RegionUtil.setBorderRight(1, range, sheet);
        RegionUtil.setBorderTop(1, range, sheet);
    }

    /**
     * 功能描述  提交公共方法
     *
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/10/10 15:30
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(PmInfoDept entity) {
        PmInfoDept vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        List<Attachment> attachment = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, vo.getAttachmentCode()));
        if (CollectionUtils.isEmpty(attachment)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未上传领导签字文件").build();
        }
        if (!(WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getStatus())
                || WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getStatus()))) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("该状态下不可提交").build();
        }
        //待提交>>>>>待审核
        vo.setGmtSubmit(System.currentTimeMillis());
        vo.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
        updateById(vo);
        //日志记录
        PmInfoDeptLog log = new PmInfoDeptLog();
        log.setIdRbacDepartment(vo.getIdRbacDepartment());
        log.setIdPmInfoDept(vo.getId());
        log.setStatus(WorkStatusAuditingStatusEnum.TWENTY.getId());
        log.setContent("提交发布需求");
        logService.save(log);
        /*======================企业信息发布审核======================系统通知======================*/
        sysMessageHelpService.addReviewMessage(ReviewMessage.newInstance()
                .dataSourceClass(SysMessageDataSourceClassEnum.ENTERPRISE_RELEASE_REVIEW.getId())
                .flowStatus(SysMsgFlowStatusEnum.ONE.getId())
                .idRbacDepartment(vo.getIdRbacDepartment())
                .sourceId(vo.getId())
                .title(vo.getTitle())
                .build());
    }

    /**
     * 详情
     *
     * @param id 主键
     * @return com.unity.innovation.entity.PmInfoDept
     * @author JH
     * @date 2019/10/17 15:34
     */
    public PmInfoDept detailById(Long id) {
        PmInfoDept entity = super.getById(id);
        Integer bizType = entity.getBizType();
        if (BizTypeEnum.RQDEPTINFO.getType().equals(bizType)) {
            List<InfoDeptYzgt> yzgtList = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>().eq(InfoDeptYzgt::getIdPmInfoDept, id));
            yzgtService.convert2List(yzgtList);
            entity.setDataList(yzgtList);
        } else if (BizTypeEnum.LYDEPTINFO.getType().equals(bizType)) {
            List<InfoDeptSatb> satbList = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>().eq(InfoDeptSatb::getIdPmInfoDept, id));
            if (CollectionUtils.isNotEmpty(satbList)) {
                satbService.dealData(satbList);
            }
            entity.setDataList(satbList);
        } else if(BizTypeEnum.INVESTMENT.getType().equals(bizType)) {
            String snapShot = entity.getSnapShot();
            List<IplYzgtMain> list = GsonUtils.parse(snapShot, new TypeToken<List<IplYzgtMain>>() {
            });
            entity.setSnapShot("");
            entity.setDataList(list);
        }
        List<Attachment> list = attachmentService.list(new LambdaQueryWrapper<Attachment>().eq(Attachment::getAttachmentCode, entity.getAttachmentCode()));
        entity.setAttachmentList(list);
        //操作记录
        List<PmInfoDeptLog> logList = logService.list(new LambdaQueryWrapper<PmInfoDeptLog>()
                .eq(PmInfoDeptLog::getIdPmInfoDept, id)
                .orderByDesc(PmInfoDeptLog::getGmtCreate));
        logList.forEach(n -> {
            n.setDepartmentName(InnovationUtil.getDeptNameById(n.getIdRbacDepartment()));
            n.setStatusName(Objects.requireNonNull(WorkStatusAuditingProcessEnum.of(n.getStatus())).getName());
        });
        entity.setLogList(logList);
        //按状态进行分组,同时只取时间最小的那一条数据
        Map<Integer, PmInfoDeptLog> map = logList.stream()
                .filter(n -> !WorkStatusAuditingStatusEnum.FORTY.getId().equals(n.getStatus()))
                .collect(Collectors.toMap(PmInfoDeptLog::getStatus, Function.identity(), BinaryOperator.minBy(Comparator.comparingLong(PmInfoDeptLog::getGmtCreate))));
        Set<Integer> statusSet = map.keySet();
        List<PmInfoDeptLog> processNodeList = Lists.newArrayList();
        for (int status : statusSet) {
            PmInfoDeptLog log = map.get(status);
            processNodeList.add(log);
        }
        entity.setProcessNodeList(processNodeList);
        return entity;
    }


    /**
     * 封装下载数据包
     *
     * @param yzgtList 亦庄国投数据
     * @return java.util.List<java.util.List < java.lang.Object>>
     * @author JH
     * @date 2019/10/22 11:18
     */
    public List<List<Object>> getYzgtData(List<InfoDeptYzgt> yzgtList) {
        List<List<Object>> dataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(yzgtList)) {
            yzgtList.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.getIndustryCategoryName(),
                        e.getEnterpriseScaleName(),
                        e.getEnterpriseNatureName(),
                        e.getEnterpriseName(),
                        e.getEnterpriseIntroduction(),
                        e.getNotes(),
                        e.getContactPerson(),
                        e.getContactWay(),
                        e.getAttachmentCode(),
                        DateUtils.timeStamp2Date(e.getGmtCreate()));
                dataList.add(list);
            });
        }
        return dataList;
    }


    /**
     * 封装下载数据包
     *
     * @param satbList 科技局数据
     * @return java.util.List<java.util.List < java.lang.Object>>
     * @author JH
     * @date 2019/10/22 11:28
     */
    public List<List<Object>> getSatbData(List<InfoDeptSatb> satbList) {
        List<List<Object>> dataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(satbList)) {
            satbList.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.getIndustryCategoryName(),
                        e.getEnterpriseScaleName(),
                        e.getEnterpriseNatureName(),
                        e.getEnterpriseName(),
                        e.getEnterpriseIntroduction(),
                        e.getInDetail(),
                        e.getAchievementLevelName(),
                        e.getIsPublishFirst() == YesOrNoEnum.YES.getType() ? "是" : "否",
                        e.getNotes(),
                        e.getContactPerson(),
                        e.getContactWay(),
                        e.getAttachmentCode(),
                        DateUtils.timeStamp2Date(e.getGmtCreate()));
                dataList.add(list);
            });
        }
        return dataList;
    }

    /**
     * 投资机构下载数据拼装
     *
     * @param snapshot 快照数据
     * @return java.util.List<java.util.List<java.lang.Object>>
     * @author JH
     * @date 2019/10/25 16:53
     */
    public  List<List<Object>> getYzgtData(String snapshot) {
        List<List<Object>> dataList = new ArrayList<>();

        if (StringUtils.isNoneBlank(snapshot)) {
            List<Map> parse = JSON.parseObject(snapshot, List.class);
            parse.forEach(e -> {
                List<Object> list = Arrays.asList(
                        e.get("industryCategoryTitle"),
                        e.get("enterpriseScaleTitle"),
                        e.get("enterpriseNatureTitle"),
                        e.get("enterpriseLocationTitle"),
                        e.get("enterpriseName"),
                        e.get("enterpriseIntroduction"),
                        e.get("specificCause"),
                        e.get("notes"),
                        e.get("contactPerson"),
                        e.get("contactWay"),
                        e.get("idCard"),
                        e.get("post"),
                        e.get("attachmentCode"),
                        e.get("createTime"),
                        e.get("modifiedTime"),
                        e.get("sourceTitle"));
                dataList.add(list);
            });
        }
        return dataList;
    }


}
