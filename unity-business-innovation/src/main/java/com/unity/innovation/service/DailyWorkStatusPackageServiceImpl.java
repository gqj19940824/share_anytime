
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.utils.ExcelStyleUtil;
import com.unity.common.utils.HashRedisUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.dao.DailyWorkStatusPackageDao;
import com.unity.innovation.entity.*;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.enums.WorkStatusAuditingProcessEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
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
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * ClassName: DailyWorkStatusPackageService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 11:17:02
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class DailyWorkStatusPackageServiceImpl extends BaseServiceImpl<DailyWorkStatusPackageDao, DailyWorkStatusPackage> {

    @Resource
    private AttachmentServiceImpl attachmentService;

    @Resource
    private DailyWorkPackageServiceImpl workMPackageService;

    @Resource
    private DailyWorkStatusServiceImpl workStatusService;

    @Resource
    private DailyWorkStatusLogServiceImpl logService;

    @Resource
    private SystemConfiguration systemConfiguration;

    @Resource
    private HashRedisUtils hashRedisUtils;

    @Resource
    private SysCfgServiceImpl sysCfgService;

    @Resource
    private DailyWorkKeywordServiceImpl keywordService;

    /**
     * 功能描述 分页接口
     * @param search 查询条件
     * @return  分页数据
     * @author gengzhiqiang
     * @date 2019/9/18 14:55
     */
    public IPage<DailyWorkStatusPackage> listByPageForBase(PageEntity<DailyWorkStatusPackage> search) {
        LambdaQueryWrapper<DailyWorkStatusPackage> lqw = new LambdaQueryWrapper<>();
        //提交时间
        if (StringUtils.isNotBlank(search.getEntity().getSubmitTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), false);
            //gt 大于 lt 小于
            lqw.gt(DailyWorkStatusPackage::getGmtSubmit, begin);
            lqw.lt(DailyWorkStatusPackage::getGmtSubmit, end);
        }
        //状态
        if (search.getEntity().getState() != null) {
            lqw.eq(DailyWorkStatusPackage::getState, search.getEntity().getState());
        }
        //本单位数据 管理员 列表数据都要显示
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (customer.getIdRbacDepartment() != null && customer.isAdmin != null) {
            if (YesOrNoEnum.NO.getType() == customer.isAdmin) {
                lqw.eq(DailyWorkStatusPackage::getIdRbacDepartment, customer.getIdRbacDepartment());
            }
        }
        //管理员 单位数据
        if (search.getEntity().getIdRbacDepartment() != null) {
            lqw.eq(DailyWorkStatusPackage::getIdRbacDepartment, search.getEntity().getIdRbacDepartment());
        }
        lqw.orderByDesc(DailyWorkStatusPackage::getGmtSubmit,DailyWorkStatusPackage::getGmtModified);
       // lqw.last(" ORDER BY gmt_modified desc , gmt_modified desc ");
        IPage<DailyWorkStatusPackage> list = page(search.getPageable(), lqw);
        if (CollectionUtils.isNotEmpty(list.getRecords())){
            list.getRecords().forEach(p -> {
                if (p.getState() != null) {
                    if (WorkStatusAuditingStatusEnum.exist(p.getState())) {
                        p.setStateName(WorkStatusAuditingStatusEnum.of(p.getState()).getName());
                    }
                }
                //redis获取单位名称
                if (hashRedisUtils.getFieldValueByFieldName
                        (RedisConstants.DEPARTMENT + p.getIdRbacDepartment(), "name") != null) {
                    p.setDeptName(hashRedisUtils.getFieldValueByFieldName
                            (RedisConstants.DEPARTMENT + p.getIdRbacDepartment(), "name")
                    );
                }
            });
        }
        return list;
    }

    /**
     * 功能描述 分页接口
     * @param search 查询条件
     * @return  分页数据
     * @author gengzhiqiang
     * @date 2019/9/18 14:55
     */
    public IPage<DailyWorkStatusPackage> listByPageForAll(PageEntity<DailyWorkStatusPackage> search) {
        LambdaQueryWrapper<DailyWorkStatusPackage> lqw = new LambdaQueryWrapper<>();
        //提交时间
        if (StringUtils.isNotBlank(search.getEntity().getSubmitTime())) {
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), false);
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getSubmitTime(), true);
            //gt 大于 lt 小于
            lqw.gt(DailyWorkStatusPackage::getGmtSubmit, begin);
            lqw.lt(DailyWorkStatusPackage::getGmtSubmit, end);
        }
        //状态
        if (search.getEntity().getState() != null) {
            lqw.eq(DailyWorkStatusPackage::getState, search.getEntity().getState());
        }
        //审核角色可按照单位查询 单位数据
        if (search.getEntity().getIdRbacDepartment() != null) {
            lqw.eq(DailyWorkStatusPackage::getIdRbacDepartment, search.getEntity().getIdRbacDepartment());
        }
        //审核角色查看四种状态的数据
        List<Integer> states=Lists.newArrayList();
        states.add(WorkStatusAuditingStatusEnum.TWENTY.getId());
        states.add(WorkStatusAuditingStatusEnum.THIRTY.getId());
        states.add(WorkStatusAuditingStatusEnum.FIFTY.getId());
        states.add(WorkStatusAuditingStatusEnum.SIXTY.getId());
        lqw.in(DailyWorkStatusPackage::getState,states);
        lqw.orderByDesc(DailyWorkStatusPackage::getGmtSubmit);
        IPage<DailyWorkStatusPackage> list = page(search.getPageable(), lqw);
        if (CollectionUtils.isNotEmpty(list.getRecords())){
            list.getRecords().forEach(p -> {
                //redis获取单位名称
                if (hashRedisUtils.getFieldValueByFieldName
                        (RedisConstants.DEPARTMENT + p.getIdRbacDepartment(), "name") != null) {
                    p.setDeptName(hashRedisUtils.getFieldValueByFieldName
                            (RedisConstants.DEPARTMENT + p.getIdRbacDepartment(), "name")
                    );
                }
                if (p.getState() != null) {
                    if (WorkStatusAuditingStatusEnum.exist(p.getState())) {
                        p.setStateName(WorkStatusAuditingStatusEnum.of(p.getState()).getName());
                    }
                }
            });
        }
        return list;
    }


    /**
     * 功能描述 新增编辑
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/9/18 15:54
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEntity(DailyWorkStatusPackage entity) {
        if (entity.getId() == null) {
            Customer customer = LoginContextHolder.getRequestAttributes();
            if (customer.getIdRbacDepartment() != null) {
                entity.setIdRbacDepartment(customer.getIdRbacDepartment());
            }
            //新增
            List<Long> works = entity.getWorkStatusList();
            entity.setAttachmentCode(UUIDUtil.getUUID().replace("-", ""));
            attachmentService.updateAttachments(entity.getAttachmentCode(), entity.getAttachmentList());
            entity.setState(WorkStatusAuditingStatusEnum.TEN.getId());
            //提交时间设置最大
            entity.setGmtSubmit(ParamConstants.GMT_SUBMIT);
            save(entity);
            //处理中间表
            workMPackageService.updateWorkPackage(entity.getId(), works);
        } else {
            //编辑
            DailyWorkStatusPackage vo = getById(entity.getId());
            if (vo == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到对象").build();
            }
            if (WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getState()) ||
                    WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getState())) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("只有待提交和已驳回状态下数据可编辑").build();
            }
            List<Long> works = entity.getWorkStatusList();
            long count = (long) workStatusService.list(new LambdaQueryWrapper<DailyWorkStatus>()
                    .in(DailyWorkStatus::getId, works)
                    .ne(DailyWorkStatus::getId,vo.getId())).size();
            if (count > 0) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("当前列表存在已提请数据").build();
            }
            workMPackageService.updateWorkPackage(entity.getId(), works);
            attachmentService.updateAttachments(vo.getAttachmentCode(), entity.getAttachmentList());
            updateById(entity);
        }
    }

    /**
     * 功能描述 详情接口
     * @param entity 带着id的对象
     * @return 详情对象
     * @author gengzhiqiang
     * @date 2019/9/18 18:42
     */
    public DailyWorkStatusPackage detailById(DailyWorkStatusPackage entity) {
        DailyWorkStatusPackage vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //数据集合
        List<DailyWorkPackage> mList = workMPackageService.list(new LambdaQueryWrapper<DailyWorkPackage>().eq(DailyWorkPackage::getIdPackage, entity.getId()));
        List<Long> ids = mList.stream().map(DailyWorkPackage::getIdDailyWorkStatus).collect(Collectors.toList());
        List<DailyWorkStatus> dateList = workStatusService.list(new LambdaQueryWrapper<DailyWorkStatus>().in(DailyWorkStatus::getId, ids));
        //工作类别
        List<SysCfg> typeList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, SysCfgEnum.ONE.getId()));
        Map<Long, String> typeNames = typeList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        //关键字
        List<DailyWorkKeyword> keys = keywordService.list(new LambdaQueryWrapper<DailyWorkKeyword>()
                .in(DailyWorkKeyword::getIdDailyWorkStatus, ids));
        List<SysCfg> keyList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, SysCfgEnum.TWO.getId()));
        Map<Long, String> keyNames = keyList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        keys.forEach(k->k.setKeyName(keyNames.get(k.getIdKeyword())));
        Map<Long, List<DailyWorkKeyword>> keyStr = keys.stream().collect(Collectors.groupingBy(DailyWorkKeyword::getIdDailyWorkStatus));
        dateList.forEach(dwk -> {
            //工作类别
            dwk.setTypeName(typeNames.get(dwk.getType()));
            //关键字
            List<DailyWorkKeyword> keyList1 = keyStr.get((dwk.getId()));
            String keysStr=keyList1.stream().map(DailyWorkKeyword::getKeyName).collect(joining(" "));
            dwk.setKeyWordStr(keysStr);
            //附件
            List<Attachment> attachmentList=attachmentService.list(new LambdaQueryWrapper<Attachment>()
                    .eq(Attachment::getAttachmentCode,dwk.getAttachmentCode()));
            if (CollectionUtils.isNotEmpty(attachmentList)){
                dwk.setAttachmentList(attachmentList);
            }
        });
        vo.setDataList(dateList);
        //日志集合
        List<DailyWorkStatusLog> logList = logService.list(new LambdaQueryWrapper<DailyWorkStatusLog>()
                .eq(DailyWorkStatusLog::getIdPackage, entity.getId())
                .orderByDesc(DailyWorkStatusLog::getGmtCreate));
        logList.forEach(log -> {
            if (log.getState() != null) {
                if (WorkStatusAuditingProcessEnum.exist(log.getState())) {
                    log.setLogName(WorkStatusAuditingProcessEnum.of(log.getState()).getName());
                }
            }
            //redis获取单位名称
            if (hashRedisUtils.getFieldValueByFieldName
                    (RedisConstants.DEPARTMENT + log.getIdRbacDepartment(), "name") != null) {
                log.setDeptName(hashRedisUtils.getFieldValueByFieldName
                        (RedisConstants.DEPARTMENT + log.getIdRbacDepartment(), "name")
                );
            }
        });
        vo.setLogList(logList);
        //流程节点
        Map<Integer, DailyWorkStatusLog> processNode = logList.stream().filter(log -> !WorkStatusAuditingStatusEnum.FORTY.getId().equals(log.getState()))
                .collect(Collectors.groupingBy(DailyWorkStatusLog::getState,
                        Collectors.collectingAndThen(Collectors
                                .minBy(Comparator.comparingLong(DailyWorkStatusLog::getGmtCreate)), Optional::get)));
        vo.setProcessNode(processNode);
        //附件
        List<Attachment> attachmentList=attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode,entity.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)){
            vo.setAttachmentList(attachmentList);
        }
        return vo;
    }


    /**
     * 功能描述 批量删除
     * @param ids id集合
     * @author gengzhiqiang
     * @date 2019/9/17 16:14
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeById(List<Long> ids) {
        List<Integer> stateList=Lists.newArrayList();
        stateList.add(WorkStatusAuditingStatusEnum.TEN.getId());
        stateList.add(WorkStatusAuditingStatusEnum.FORTY.getId());
        //判断状态是否可操作
        List<DailyWorkStatusPackage> list1 = list(new LambdaQueryWrapper<DailyWorkStatusPackage>()
                .notIn(DailyWorkStatusPackage::getState, stateList).in(DailyWorkStatusPackage::getId, ids));
        if (CollectionUtils.isNotEmpty(list1)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("该状态下数据不可删除").build();
        }
        Collection<DailyWorkStatusPackage> list = listByIds(ids);
        List<String> codes = list.stream().map(DailyWorkStatusPackage::getAttachmentCode).collect(Collectors.toList());
        //附件表
        attachmentService.remove(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codes));
        //工作动态表
        List<Long> works = workMPackageService.list(new LambdaQueryWrapper<DailyWorkPackage>().in(DailyWorkPackage::getIdPackage, ids))
                .stream().map(DailyWorkPackage::getIdDailyWorkStatus).collect(Collectors.toList());
        DailyWorkStatus dws = DailyWorkStatus.newInstance().build();
        dws.setState(YesOrNoEnum.NO.getType());
        workStatusService.update(dws, new LambdaUpdateWrapper<DailyWorkStatus>().in(DailyWorkStatus::getId, works));
        //中间表
        workMPackageService.remove(new LambdaQueryWrapper<DailyWorkPackage>().in(DailyWorkPackage::getIdPackage, ids));
        //日志表
        logService.remove(new LambdaQueryWrapper<DailyWorkStatusLog>().in(DailyWorkStatusLog::getIdPackage, ids));
        removeByIds(ids);
    }

    /**
     * 功能描述 提交接口
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/9/18 19:31
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(DailyWorkStatusPackage entity) {
        DailyWorkStatusPackage vo = getById(entity.getId());
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
        if (!(WorkStatusAuditingStatusEnum.TEN.getId().equals(vo.getState())
                || WorkStatusAuditingStatusEnum.FORTY.getId().equals(vo.getState()))) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("该状态下不可提交").build();
        }
        //待提交>>>>>待审核
        vo.setGmtSubmit(System.currentTimeMillis());
        vo.setState(WorkStatusAuditingStatusEnum.TWENTY.getId());
        updateById(vo);
        //日志记录
        DailyWorkStatusLog log=DailyWorkStatusLog.newInstance().build();
        log.setIdRbacDepartment(vo.getIdRbacDepartment());
        log.setIdPackage(vo.getId());
        log.setState(WorkStatusAuditingStatusEnum.TWENTY.getId());
        log.setActionDescribe("提交发布需求");
        logService.save(log);
    }

    /**
     * 功能描述 通过驳回接口 0否1是
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/9/18 19:31
     */
    @Transactional(rollbackFor = Exception.class)
    public void passOrReject(DailyWorkStatusPackage entity) {
        DailyWorkStatusPackage vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        if (!WorkStatusAuditingStatusEnum.TWENTY.getId().equals(vo.getState())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("该状态下不可操作").build();
        }
        //0：驳回 1：通过
        if ( entity.getFlag() == YesOrNoEnum.YES.getType()) {
            //待审核>>>>>已通过
            vo.setState(WorkStatusAuditingStatusEnum.THIRTY.getId());
            updateById(vo);
            //日志记录
            DailyWorkStatusLog log=DailyWorkStatusLog.newInstance().build();
            log.setIdRbacDepartment(vo.getIdRbacDepartment());
            log.setIdPackage(vo.getId());
            log.setState(WorkStatusAuditingStatusEnum.THIRTY.getId());
            log.setActionDescribe("审核发布需求");
            log.setComment(vo.getComment());
            logService.save(log);
        }else {
            //待审核>>>>>驳回
            vo.setState(WorkStatusAuditingStatusEnum.FORTY.getId());
            updateById(vo);
            //日志记录
            DailyWorkStatusLog log=DailyWorkStatusLog.newInstance().build();
            log.setIdRbacDepartment(vo.getIdRbacDepartment());
            log.setIdPackage(vo.getId());
            log.setState(WorkStatusAuditingStatusEnum.FORTY.getId());
            log.setActionDescribe("审核发布需求");
            log.setComment(vo.getComment());
            logService.save(log);
        }
    }

    /**
     * 功能描述 导出接口
     *
     * @param id 查询条件
     * @return byte[] 返回数据流
     * @author gengzhiqiang
     * @date 2019/7/8 10:15
     */
    public byte[] export(Long id) {
        //查询模板信息
        byte[] content;
        String templatePath = systemConfiguration.getUploadPath() + File.separator + "workStatus" + File.separator;
        String templateFile = templatePath + File.separator + "workStatus.xls";
        File dir = new File(templatePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream out = null;
        try {
            //定义表格对象
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            HSSFRow row;
            //表头
            DailyWorkStatusPackage entity = DailyWorkStatusPackage.newInstance().build();
            entity.setId(id);
            entity = detailById(entity);
            String top=entity.getTitle();
            Map<String, CellStyle> styleMap = ExcelStyleUtil.createProjectStyles(workbook);
            workbook.createCellStyle();
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styleMap.get("title"));
            titleCell.setCellValue(top);
            row = sheet.createRow(1);
            String[] title = { "标题", "工作类别", "关键字", "主题", "内容描述","备注","附件","创建时间"};
            sheet.addMergedRegion(new CellRangeAddress(0,0, 0, title.length-1));
            for (int j = 0; j < title.length; j++) {
                //创建每列
                Cell cell = row.createCell(j);
                //设置样式
                cell.setCellStyle(styleMap.get("title"));
                //列宽自适应
                sheet.autoSizeColumn(j, true);
                cell.setCellValue(title[j]);
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

    private void addData(HSSFSheet sheet, DailyWorkStatusPackage entity, Map<String,CellStyle> styleMap) {

        CellStyle sty = styleMap.get("data");
        int rowNum = 2;
        for (int j = 0; j < entity.getDataList().size(); j++) {
            HSSFRow row = sheet.createRow(rowNum++);
            HSSFCell cell0 = row.createCell(0);
            HSSFCell cell1 = row.createCell(1);
            HSSFCell cell2 = row.createCell(2);
            HSSFCell cell3 = row.createCell(3);
            HSSFCell cell4 = row.createCell(4);
            HSSFCell cell5 = row.createCell(5);
            HSSFCell cell6 = row.createCell(6);
            HSSFCell cell7 = row.createCell(7);
            cell0.setCellStyle(sty);
            sheet.setColumnWidth(0, 15*256);
            cell1.setCellStyle(sty);
            sheet.setColumnWidth(1, 15*256);
            cell2.setCellStyle(sty);
            sheet.setColumnWidth(2, 15*256);
            cell3.setCellStyle(sty);
            sheet.setColumnWidth(3, 15*256);
            cell4.setCellStyle(sty);
            sheet.setColumnWidth(4, 30*256);
            cell5.setCellStyle(sty);
            sheet.setColumnWidth(5, 30*256);
            cell6.setCellStyle(sty);
            sheet.setColumnWidth(6, 60*256);
            cell7.setCellStyle(sty);
            sheet.setColumnWidth(7, 18*256);
            cell0.setCellValue(entity.getDataList().get(j).getTitle());
            cell1.setCellValue(entity.getDataList().get(j).getTypeName());
            cell2.setCellValue(entity.getDataList().get(j).getKeyWordStr());
            cell3.setCellValue(entity.getDataList().get(j).getTheme());
            cell4.setCellValue(entity.getDataList().get(j).getDescription());
            if (StringUtils.isNotBlank(entity.getDataList().get(j).getNotes())){
                cell5.setCellValue(entity.getDataList().get(j).getNotes());
            }
            if (CollectionUtils.isNotEmpty(entity.getDataList().get(j).getAttachmentList())){
                String url = entity.getDataList().get(j).getAttachmentList().stream().map(Attachment::getUrl).collect(joining("\n"));
                cell6.setCellValue(url);

            }
            cell7.setCellValue(DateUtils.timeStamp2Date(entity.getDataList().get(j).getGmtCreate()));
        }
        Row titleRow = sheet.createRow(entity.getDataList().size() + 2);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(styleMap.get("data"));
        if (StringUtils.isNotBlank(entity.getNotes())) {
            titleCell.setCellValue("备注："+entity.getNotes());
        }
        CellRangeAddress range = new CellRangeAddress(entity.getDataList().size() + 2, entity.getDataList().size() + 2, 0, 7);
        sheet.addMergedRegion(range);
        RegionUtil.setBorderLeft(1, range, sheet);
        RegionUtil.setBorderBottom(1, range, sheet);
        RegionUtil.setBorderRight(1, range, sheet);
        RegionUtil.setBorderTop(1, range, sheet);
    }

    /**
     * 功能描述 基础数据列表
     * @param search 查询条件
     * @return 基础数据列表
     * @author gengzhiqiang
     * @date 2019/9/20 14:56
     */
    public IPage<DailyWorkStatus> listForContent(PageEntity<DailyWorkStatus> search) {
        return workStatusService.listForContent(search);
    }
}
