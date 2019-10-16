
package com.unity.innovation.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.client.RbacClient;
import com.unity.common.client.ReClient;
import com.unity.common.client.vo.DepartmentVO;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.FileDownload;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.FileDownloadUtil;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.dao.IplSatbMainDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.IplStatusEnum;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.util.InnovationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: IplSatbMainService
 * date: 2019-10-08 17:03:09
 *
 * @author G
 * @since JDK 1.8
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class IplSatbMainServiceImpl extends BaseServiceImpl<IplSatbMainDao, IplSatbMain> {

    @Resource
    AttachmentServiceImpl attachmentService;
    @Resource
    SysCfgServiceImpl sysCfgService;
    @Resource
    IplAssistServiceImpl iplAssistService;
    @Resource
    IplManageMainServiceImpl iplManageMainService;
    @Resource
    IplLogServiceImpl iplLogService;
    @Resource
    RbacClient rbacClient;
    @Resource
    SystemConfiguration systemConfiguration;
    @Resource
    ReClient reClient;

    /**
     * 获取清单列表
     *
     * @param pageEntity 包含分页及检索条件
     * @return 清单列表
     * @author gengjiajia
     * @since 2019/10/08 17:35
     */
    public PageElementGrid<Map<String, Object>> listByPage(PageEntity<IplSatbMain> pageEntity) {
        LambdaQueryWrapper<IplSatbMain> ew = new LambdaQueryWrapper<>();
        IplSatbMain entity = pageEntity.getEntity();
        if (entity != null) {
            wrapper(entity, ew);
        }
        IPage<IplSatbMain> page = this.page(pageEntity.getPageable(), ew);
        return PageElementGrid.<Map<String, Object>>newInstance()
                .total(page.getTotal())
                .items(convert2List(page.getRecords()))
                .build();
    }

    /**
     * 查询条件转换
     *
     * @param entity 检索条件
     * @param ew     检索条件组装器
     * @author gengjiajia
     * @since 2019/10/08 17:52
     */
    private void wrapper(IplSatbMain entity, LambdaQueryWrapper<IplSatbMain> ew) {
        ew.orderByAsc(IplSatbMain::getSort);
        if (entity.getIndustryCategory() != null) {
            ew.eq(IplSatbMain::getIndustryCategory, entity.getIndustryCategory());
        }
        if (entity.getDemandCategory() != null) {
            ew.eq(IplSatbMain::getDemandCategory, entity.getDemandCategory());
        }
        if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
            ew.like(IplSatbMain::getEnterpriseName, entity.getEnterpriseName());
        }
        if (StringUtils.isNotBlank(entity.getProjectName())) {
            ew.like(IplSatbMain::getProjectName, entity.getProjectName());
        }
        if (StringUtils.isNotEmpty(entity.getCreateDate())) {
            ew.between(IplSatbMain::getGmtCreate,
                    InnovationUtil.getFirstTimeInMonth(entity.getCreateDate(), true),
                    InnovationUtil.getFirstTimeInMonth(entity.getCreateDate(), false));
        }
        if (entity.getSource() != null) {
            ew.eq(IplSatbMain::getSource, entity.getSource());
        }
        if (entity.getStatus() != null) {
            ew.eq(IplSatbMain::getStatus, entity.getStatus());
        }
        if (StringUtils.isNotBlank(entity.getNotes())) {
            ew.like(IplSatbMain::getNotes, entity.getNotes());
        }
    }

    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return map列表
     * @author gengjiajia
     * @since 2019/10/08 17:58
     */
    private List<Map<String, Object>> convert2List(List<IplSatbMain> list) {
        //查询附件
        List<String> codeList = list.stream().map(IplSatbMain::getAttachmentCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(codeList)) {
            codeList.add("0");
        }
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codeList.toArray()));
        Map<String, List<Attachment>> attachmentMap = attachmentList.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));
        //获取行业类别
        Map<Long, String> industryCategoryMap = sysCfgService.getSysCfgMap(SysCfgEnum.THREE.getId());
        //需求类别
        Map<Long, String> demandCategoryMap = sysCfgService.getSysCfgMap(SysCfgEnum.FOUR.getId());
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                    List<Attachment> attachments = attachmentMap.get(entity.getAttachmentCode());
                    m.put("attachmentList", CollectionUtils.isEmpty(attachments) ? Lists.newArrayList() : convertList2MapByAttachment(attachments));
                    m.put("industryCategoryTitle", industryCategoryMap.get(entity.getIndustryCategory()));
                    m.put("demandCategoryTitle", demandCategoryMap.get(entity.getDemandCategory()));
                }
                , IplSatbMain::getId, IplSatbMain::getNotes, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName, IplSatbMain::getDemandCategory,
                IplSatbMain::getProjectName, IplSatbMain::getProjectAddress, IplSatbMain::getProjectIntroduce, IplSatbMain::getTotalAmount, IplSatbMain::getBank,
                IplSatbMain::getBond, IplSatbMain::getRaise, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay,
                IplSatbMain::getSource, IplSatbMain::getStatus
        );
    }

    /**
     * 字段适配
     *
     * @param m      适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m, IplSatbMain entity) {
        m.put("gmtCreate", entity.getGmtCreate());
        m.put("gmtModified", entity.getGmtModified());
        m.put("sourceTitle", entity.getSource().equals(SourceEnum.SELF.getId()) ? "科技局" : "企业");
        m.put("statusTitle", IplStatusEnum.ofName(entity.getStatus()));
    }

    /**
     * 将实体列表 转换为Map
     *
     * @param list 实体对象
     * @return Map
     */
    private List<Map<String, Object>> convertList2MapByAttachment(List<Attachment> list) {
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    // adapterField(m, entity);
                }
                , Attachment::getSize, Attachment::getUrl, Attachment::getName
        );
    }

    /**
     * 新增or修改清单
     *
     * @param entity 清单信息
     * @author gengjiajia
     * @since 2019/10/08 20:46
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateIplSatbMain(IplSatbMain entity) {
        if (entity.getId() == null) {
            String uuid = UUIDUtil.getUUID();
            entity.setAttachmentCode(uuid);
            entity.setStatus(IplStatusEnum.UNDEAL.getId());
            if (CollectionUtils.isNotEmpty(entity.getAttachmentList())) {
                attachmentService.updateAttachments(uuid, entity.getAttachmentList());
            }
            entity.setIdRbacDepartmentDuty(InnovationConstant.DEPARTMENT_SATB_ID);
            this.save(entity);
        } else {
            IplSatbMain main = this.getById(entity.getId());
            entity.setAttachmentCode(main.getAttachmentCode());
            entity.setSource(main.getSource());
            entity.setStatus(main.getStatus());
            entity.setGmtCreate(main.getGmtCreate());
            entity.setSort(main.getSort());
            if (CollectionUtils.isNotEmpty(entity.getAttachmentList())) {
                attachmentService.updateAttachments(main.getAttachmentCode(), entity.getAttachmentList());
            }
            this.updateById(entity);
        }

    }

    /**
     * 删除清单信息
     *
     * @param id 清单id
     * @author gengjiajia
     * @since 2019/10/08 20:47
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        //关联删除附件
        IplSatbMain main = this.getById(id);
        //关联删除协同信息
        iplAssistService.del(id, main.getIdRbacDepartmentDuty(), main.getAttachmentCode());
        this.removeById(id);
    }

    /**
     * 获取清单详情
     *
     * @param id 清单id
     * @return 清单详情
     * @author gengjiajia
     * @since 2019/10/08 20:48
     */
    public Map<String, Object> detailById(Long id) {
        return convert2Map(this.getById(id));
    }

    /**
     * 将实体 转换为 Map
     *
     * @param ent 实体
     * @return Map
     */
    private Map<String, Object> convert2Map(IplSatbMain ent) {
        //获取附件
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, ent.getAttachmentCode()));
        //行业类别
        SysCfg industryCategory = sysCfgService.getById(ent.getIndustryCategory());
        //需求类别
        SysCfg demandCategory = sysCfgService.getById(ent.getDemandCategory());
        //获取总体进展
        Map<String, Object> assists = iplAssistService.totalProcessAndAssists(ent.getId(), ent.getIdRbacDepartmentDuty(), ent.getStatus());
        Map<String, Object> detail = JsonUtil.ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m, entity);
                    m.put("attachmentList", convertList2MapByAttachment(attachmentList));
                    m.put("industryCategoryTitle", industryCategory.getCfgVal());
                    m.put("demandCategoryTitle", demandCategory.getCfgVal());
                }
                , IplSatbMain::getId, IplSatbMain::getIndustryCategory, IplSatbMain::getEnterpriseName
                , IplSatbMain::getDemandCategory, IplSatbMain::getProjectName, IplSatbMain::getProjectAddress
                , IplSatbMain::getProjectIntroduce, IplSatbMain::getTotalAmount, IplSatbMain::getBank, IplSatbMain::getBond
                , IplSatbMain::getRaise, IplSatbMain::getTechDemondInfo, IplSatbMain::getContactPerson, IplSatbMain::getContactWay
                , IplSatbMain::getSource, IplSatbMain::getStatus
        );
        assists.put("detail", detail);
        return assists;
    }

    /**
     * 获取系统类别
     *
     * @param cfgType 系统类型
     * @return 类别列表
     * @author gengjiajia
     * @since 2019/10/09 19:35
     */
    public List<Map<String, Object>> getCategoryBySysType(Integer cfgType) {
        return sysCfgService.getSysList1(cfgType);
    }

    /**
     * 获取协同单位列表
     *
     * @return 协同单位列表
     * @author gengjiajia
     * @since 2019/10/10 10:23
     */
    public List<Map<String, Object>> getAssistDepartmentList(Long id) {
        //主表id  数据集合
        IplSatbMain satbMain = this.getById(id);
        if (satbMain == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("未获取到成长目标投资信息").build();
        }
        List<DepartmentVO> departmentList = rbacClient.getAllDepartment();
        List<IplAssist> assistList = iplAssistService.list(new LambdaQueryWrapper<IplAssist>()
                .eq(IplAssist::getIdIplMain, id)
                .eq(IplAssist::getIdRbacDepartmentDuty, satbMain.getIdRbacDepartmentDuty()));
        List<Long> ids = assistList.stream()
                .map(IplAssist::getIdRbacDepartmentAssist)
                .collect(Collectors.toList());
        List<DepartmentVO> voList = departmentList.stream()
                .filter(d -> !ids.contains(d.getId()))
                .collect(Collectors.toList());
        return JsonUtil.ObjectToList(voList, null, DepartmentVO::getId, DepartmentVO::getName);
    }

    /**
     * 保存协同单位信息
     *
     * @param id         主业务id
     * @param assistList 协同单位信息列表
     * @author gengjiajia
     * @since 2019/10/10 10:59
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAssistDepartmentList(Long id, List<IplAssist> assistList) {
        IplSatbMain satbMain = this.getById(id);
        if (satbMain == null) {
            throw UnityRuntimeException.newInstance()
                    .message("未获取到成长目标投资信息")
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .build();
        }
        iplAssistService.addAssistant(assistList, satbMain);
    }

    /**
     * 实时更新
     *
     * @param entity 实时更新数据
     * @author gengjiajia
     * @since 2019/10/10 13:36
     */
    @Transactional(rollbackFor = Exception.class)
    public void realTimeUpdateStatus(IplLog entity) {
        IplSatbMain main = this.getById(entity.getIdIplMain());
        iplLogService.updateStatus(main, entity);
    }

    /**
     * 主责单位实时更新协同单位处理状态
     *
     * @param entity 包含状态及进展
     * @author gengjiajia
     * @since 2019/10/10 13:36
     */
    @Transactional(rollbackFor = Exception.class)
    public void realTimeUpdateStatusByDuty(IplLog entity) {
        IplSatbMain main = this.getById(entity.getIdIplMain());
        iplLogService.updateStatusByDuty(main, entity);
    }

    /**
     * 下载科技局实时清单资料到zip包
     *
     * @param id 主数据id
     * @return zip文件
     * @author gengjiajia
     * @since 2019/10/11 11:27
     */
    public ResponseEntity<byte[]> downloadIplSatbMainDataToZip(Long id) {
        IplSatbMain main = this.getById(id);
        if (main == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("企业创新发展信息实时清单数据不存在")
                    .build();
        }
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, main.getAttachmentCode()));
        if (CollectionUtils.isEmpty(attachmentList)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("企业创新发展信息实时清单无相关资料")
                    .build();
        }
        List<FileDownload> list = attachmentList.stream().map(attachment ->
                FileDownload.newInstance()
                        .url(attachment.getUrl())
                        .name(attachment.getName())
                        .build()
        ).collect(Collectors.toList());
        final String zipFileName = "企业创新发展信息实时清单-相关资料.zip";
        return FileDownloadUtil.downloadFileToZip(list, zipFileName);
    }

    /**
     * 导出科技局清单发布详情excel表格
     *
     * @param id 主数据id
     * @return excel表格
     * @author gengjiajia
     * @since 2019/10/11 11:27
     */
    public ResponseEntity<byte[]> downloadIplSatbMainDataPkgToExcel(Long id) {
        IplManageMain main = iplManageMainService.getById(id);
        if (main == null || StringUtils.isEmpty(main.getSnapshot())) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST)
                    .message("成长目标投资清单发布需求详情信息不存在")
                    .build();
        }
        //TODO 判断数据状态是否可导出
        InputStream inputStream = IplSatbMainServiceImpl.class.getClassLoader().getResourceAsStream("template" + File.separator + "iplsatbmain.xls");
        //查询模板信息
        byte[] content;
        String templatePath = systemConfiguration.getUploadPath() + File.separator + "iplsatbmain" + File.separator;
        String templateFile = templatePath + File.separator + "iplsatbmain.xls";
        File dir = new File(templatePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        HttpHeaders headers = new HttpHeaders();
        FileOutputStream out = null;
        try {
            // 创建excel文件对象
            HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = wb.getSheet("成长目标投资清单发布需求详情");
            /*// 创建excel文件对象
            HSSFWorkbook wb = new HSSFWorkbook();
            // 创建sheet
            Sheet sheet = wb.createSheet("成长目标投资清单发布需求详情");
            exportExcel(wb, sheet);*/
            //快照集合
            List<IplSatbMain> list = JSON.parseArray(main.getSnapshot(), IplSatbMain.class);
            //设置表格数据
            setTableData(sheet, list, main.getNotes());
            out = new FileOutputStream(templateFile);
            // 输出excel
            wb.write(out);
            out.close();
            File file = new File(templateFile);
            content = FileReaderUtil.getBytes(file);
            if (file.exists()) {
                file.delete();
            }
            headers.setContentDispositionFormData("attachment", new String("iplsatbmain.xls".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (Exception e) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.SERVER_ERROR, "下载失败");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new ResponseEntity<>(content, headers, HttpStatus.CREATED);
    }

    private void setTableData(Sheet sheet, List<IplSatbMain> list, String notes) {
        int rowNum = 4;
        for (IplSatbMain excelData : list) {
            Row tempRow = sheet.createRow(rowNum);
            tempRow.setHeight((short) 500);
            // 循环单元格填入数据
            for (int j = 0; j < 18; j++) {
                Cell tempCell = tempRow.createCell(j);
                String tempValue = getTableCellData(excelData, j);
                tempCell.setCellValue(tempValue);
            }
            rowNum += 1;
        }
        //表格最下方增加备注
        //合并最后一行所有单元格，填充备注信息
        sheet.getRow(sheet.getLastRowNum()).getCell(0).setCellValue("备注：".concat(notes));
    }

    private String getTableCellData(IplSatbMain excelData, int j) {
        switch (j) {
            case 0:
                return excelData.getIndustryCategoryTitle();
            case 1:
                return excelData.getEnterpriseName();
            case 3:
                return excelData.getDemandCategoryTitle();
            case 4:
                return excelData.getProjectName();
            case 5:
                return excelData.getProjectAddress();
            case 6:
                return excelData.getProjectIntroduce();
            case 7:
                return excelData.getTotalAmount().toString();
            case 8:
                return excelData.getBank().toString();
            case 9:
                return excelData.getBond().toString();
            case 10:
                return excelData.getRaise().toString();
            case 11:
                return excelData.getTechDemondInfo();
            case 12:
                return excelData.getContactPerson();
            case 13:
                return excelData.getContactWay();
            case 14:
                return DateUtils.timeStamp2Date(excelData.getGmtCreate());
            case 15:
                return DateUtils.timeStamp2Date(excelData.getGmtModified());
            case 16:
                return excelData.getSourceTitle();
            //TODO
            case 17:
                return "最新进展";
            default:
                return "";
        }
    }

    /*private void exportExcel(HSSFWorkbook wb, Sheet sheet,IplManageMain main) {
        // 行号
        int rowNum = 0;
        String[] row_first = {"行业类别", "企业名称", "需求类别", "项目名称", "项目地点", "项目介绍",
                "融资需求额度", "银行", "债券", "自筹",
                "技术需求情况", "联系人", "联系方式", "创建时间", "更新时间", "来源", "状态", "最新进展"};
        //设置列宽
        for (int i = 0; i < row_first.length; i++) {
            sheet.setColumnWidth(i, 2500);
        }
        Font headerFont = customerFont(wb, (short) 18);
        Font commonFont = customerFont(wb, (short) 12);
        CellStyle headerCellStyle = customerStyle(wb, headerFont);
        CellStyle commonCellStyle = customerStyle(wb, commonFont);
        //第一行
        Row r0 = sheet.createRow(rowNum++);
        r0.setHeight((short) 600);
        Cell henderCell = r0.createCell(0);
        henderCell.setCellValue("2019年9月成长目标投资清单发布需求详情");
        henderCell.setCellStyle(headerCellStyle);
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, row_first.length - 1));
        //第二、三、四行要进行合并，先创建出来
        for (int i = 1; i <= 3; i++) {
            Row r1 = sheet.createRow(rowNum++);
            r1.setHeight((short) 400);
            if (i == 1) {
                for (int n = 0; n < 18; n++) {
                    Cell cell = createCell(r1,n,commonCellStyle);
                    if (n < 6 || n > 9) {
                        cell.setCellValue(row_first[n]);
                    } else {
                        cell.setCellValue("融资需求情况（万元）");
                    }
                }
            } else if (i == 2) {
                Cell cell6 = createCell(r1,6,commonCellStyle);
                cell6.setCellValue("融资需求额度");
                cell6.setCellStyle(commonCellStyle);
                Cell cell7 = createCell(r1,7,commonCellStyle);
                cell7.setCellValue("其中");
                cell7.setCellStyle(commonCellStyle);
            } else {
                r1.createCell(7).setCellValue("银行");
                r1.createCell(8).setCellValue("债券");
                r1.createCell(9).setCellValue("自筹");
            }


            //tempCell.setCellStyle(alignLeftNoBorderStyle(wb));
        }
        //合并二、三、四行
        for (int i = 0; i < row_first.length; i++) {
            if (i < 6 || i > 9) {
                //上下单元格合并
                sheet.addMergedRegion(new CellRangeAddress(1, 3, i, i));
            } else if (i == 6) {
                //融资需求额度 合并第8列的第二、三行
                sheet.addMergedRegion(new CellRangeAddress(2, 3, i, 6));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 9));
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 9));
            }
        }
    }*/

    /*
     * 自定义字体
     *
     * @param  wb 工作簿对象
     * @param fontHeight 字体高度
     * @return 字体对象
     * @author gengjiajia
     * @since 2019/10/12 10:12
     *//*
    private Font customerFont(HSSFWorkbook wb,short fontHeight){
        //字体
        Font headerFont = wb.createFont();
        headerFont.setFontName("微软雅黑");
        headerFont.setFontHeightInPoints(fontHeight);
        headerFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        headerFont.setColor(HSSFColor.BLACK.index);
        return headerFont;
    }

    *//*
     * 自定义单元格样式
     *
     * @param  wb 工作簿对象
     * @param font 字体对象
     * @return 样式对象
     * @author gengjiajia
     * @since 2019/10/12 10:13
     *//*
    private CellStyle customerStyle(HSSFWorkbook wb,Font font){
        //表头样式，左右上下居中
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(font);
        // 左右居中
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        // 上下居中
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headerStyle.setLocked(true);
        // 自动换行
        headerStyle.setWrapText(false);
        //下边框
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //左边框
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //上边框
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //右边框
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        return headerStyle;
    }

    private Cell createCell(Row row,int index,CellStyle cellStyle){
        Cell cell = row.createCell(index);
        cell.setCellStyle(cellStyle);
        return cell;
    }*/
}
