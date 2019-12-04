package com.unity.innovation.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.DicConstants;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.FileReaderUtil;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.ExcelExportByTemplate;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.*;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.*;
import com.unity.innovation.service.*;
import com.unity.innovation.util.InnovationUtil;
import com.unity.innovation.util.ZipUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * 创新发布活动-管理-主表
 *
 * @author zhang
 * 生成时间 2019-09-21 15:45:32
 */
@Controller
@RequestMapping("/ipaManageMain")
public class IpaManageMainController extends BaseWebController {

    @Resource
    private IpaManageMainServiceImpl ipaManageMainService;
    @Resource
    private IplManageMainServiceImpl iplManageMainService;
    @Resource
    private DailyWorkStatusPackageServiceImpl dailyWorkStatusPackageService;
    @Resource
    private PmInfoDeptServiceImpl pmInfoDeptService;
    @Resource
    private DicUtils dicUtils;
    @Resource
    private MediaManagerServiceImpl mediaManagerService;
    @Resource
    private SystemConfiguration systemConfiguration;

    /**
     * 入会一次包列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/22 7:40 下午
     */
    @PostMapping("/listPmpByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listPmpByPage(@RequestBody PageEntity<PmInfoDept> search) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        List<Long> roleList = customer.getRoleList();
        if (!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("无权查看").build();
        }
        PmInfoDept entity = search.getEntity();
        LambdaQueryWrapper<PmInfoDept> ew = new LambdaQueryWrapper<>();
        if (entity != null) {
            //提交时间
            if (StringUtils.isNotBlank(entity.getSubmitTime())) {
                //gt 大于 lt 小于
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
                ew.gt(PmInfoDept::getGmtSubmit, begin);
                //gt 大于 lt 小于
                long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
                ew.lt(PmInfoDept::getGmtSubmit, end);
            }

            ew.in(PmInfoDept::getStatus, WorkStatusAuditingStatusEnum.THIRTY.getId());
            if (entity.getIdRbacDepartment() != null) {
                ew.eq(PmInfoDept::getIdRbacDepartment, entity.getIdRbacDepartment());
            }
            if (entity.getId() != null) {
                ew.and(e -> e.isNull(PmInfoDept::getIdIpaMain).or().eq(PmInfoDept::getIdIpaMain, entity.getId()));
            } else {
                ew.isNull(PmInfoDept::getIdIpaMain);
            }
        }
        //排序
        ew.orderByDesc(PmInfoDept::getGmtSubmit, PmInfoDept::getGmtModified);
        IPage<PmInfoDept> page = pmInfoDeptService.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(page.getTotal()).items(convert2ListForPmp(page.getRecords())).build();
        return success(result);
    }

    /**
     * 工作动态一次包列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/22 7:41 下午
     */
    @PostMapping("/listDwspByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listDwspByPage(@RequestBody PageEntity<DailyWorkStatusPackage> search) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        List<Long> roleList = customer.getRoleList();
        if (!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("无权查看").build();
        }
        DailyWorkStatusPackage entity = search.getEntity();
        LambdaQueryWrapper<DailyWorkStatusPackage> ew = new LambdaQueryWrapper<>();
        if (entity != null) {
            //提交时间
            if (StringUtils.isNotBlank(entity.getSubmitTime())) {
                //gt 大于 lt 小于
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
                ew.gt(DailyWorkStatusPackage::getGmtSubmit, begin);
                //gt 大于 lt 小于
                long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
                ew.lt(DailyWorkStatusPackage::getGmtSubmit, end);
            }
        }
        //审核角色查看四种状态的数据
        ew.in(DailyWorkStatusPackage::getState, WorkStatusAuditingStatusEnum.THIRTY.getId());
        // 提交单位
        if (entity.getIdRbacDepartment() != null) {
            ew.eq(DailyWorkStatusPackage::getIdRbacDepartment, entity.getIdRbacDepartment());
        }
        if (entity.getId() != null) {
            ew.and(e -> e.isNull(DailyWorkStatusPackage::getIdIpaMain).or().eq(DailyWorkStatusPackage::getIdIpaMain, entity.getId()));
        } else {
            ew.isNull(DailyWorkStatusPackage::getIdIpaMain);
        }
        ew.orderByDesc(DailyWorkStatusPackage::getGmtSubmit);
        IPage<DailyWorkStatusPackage> page = dailyWorkStatusPackageService.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(page.getTotal()).items(convert2ListForDwsp(page.getRecords())).build();
        return success(result);
    }

    /**
     * 清单一次包列表
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/22 7:41 下午
     */
    @PostMapping("/listIplpByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listIplpByPage(@RequestBody PageEntity<IplManageMain> search) {
        Customer customer = LoginContextHolder.getRequestAttributes();
        List<Long> roleList = customer.getRoleList();
        if (!roleList.contains(Long.parseLong(dicUtils.getDicValueByCode(DicConstants.ROLE_GROUP, DicConstants.PD_B_ROLE)))) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("无权查看").build();
        }
        IplManageMain entity = search.getEntity();
        LambdaQueryWrapper<IplManageMain> ew = new LambdaQueryWrapper<>();
        if (entity != null) {
            //提交时间
            if (StringUtils.isNotBlank(entity.getSubmitTime())) {
                //gt 大于 lt 小于
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
                ew.gt(IplManageMain::getGmtSubmit, begin);
                //gt 大于 lt 小于
                long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
                ew.lt(IplManageMain::getGmtSubmit, end);
            }

            ew.in(IplManageMain::getStatus, WorkStatusAuditingStatusEnum.THIRTY.getId());
            if (entity.getIdRbacDepartmentDuty() != null) {
                ew.eq(IplManageMain::getIdRbacDepartmentDuty, entity.getIdRbacDepartmentDuty());
            }
        }
        if (entity.getId() != null) {
            ew.and(e -> e.isNull(IplManageMain::getIdIpaMain).or().eq(IplManageMain::getIdIpaMain, entity.getId()));
        } else {
            ew.isNull(IplManageMain::getIdIpaMain);
        }
        //排序
        ew.orderByDesc(IplManageMain::getGmtSubmit, IplManageMain::getGmtModified);
        IPage<IplManageMain> page = iplManageMainService.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(page.getTotal()).items(convert2ListForPkg(page.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return java.util.List 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2ListForPmp(List<PmInfoDept> list) {
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    m.put("infoTypeName", BizTypeEnum.ofName(entity.getBizType()));
                    m.put("departmentName", InnovationUtil.getDeptNameById(entity.getIdRbacDepartment()));
                }
                , PmInfoDept::getId, PmInfoDept::getTitle, PmInfoDept::getGmtSubmit
        );
    }

    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return java.util.List 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2ListForDwsp(List<DailyWorkStatusPackage> list) {
        return JsonUtil.<DailyWorkStatusPackage>ObjectToList(list,
                (m, entity) -> {
                    m.put("departmentName", InnovationUtil.getDeptNameById(entity.getIdRbacDepartment()));
                }, DailyWorkStatusPackage::getId, DailyWorkStatusPackage::getGmtSubmit,
                DailyWorkStatusPackage::getTitle);
    }

    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return java.util.List 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2ListForPkg(List<IplManageMain> list) {
        return JsonUtil.ObjectToList(list,
                this::adapterField, IplManageMain::getId, IplManageMain::getTitle, IplManageMain::getGmtSubmit, IplManageMain::getBizType);
    }

    private void adapterField(Map<String, Object> m, IplManageMain entity) {
        // 清单类型
        m.put("listType", BizTypeEnum.ofName(entity.getBizType()));
        // 单位名称
        m.put("idRbacDepartmentDutyName", InnovationUtil.getDeptNameById(entity.getIdRbacDepartmentDuty()));
    }

    /**
     * 二次打包一键下载
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/21 2:42 下午
     */
    @GetMapping("batchExport")
    public Mono<ResponseEntity<byte[]>> batchExport(@RequestParam("id") Long id) throws Exception {
        IpaManageMain entity = ipaManageMainService.getById(id);
        if (entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("数据不存在").build();
        }

        // 创建文件夹
        String basePath = systemConfiguration.getUploadPath() + "bachExport" + File.separator + UUIDUtil.getUUID() + File.separator ;
        logger.info("basePath:" + basePath);
        String filePaht = basePath + "创新发布/";
        ZipUtil.createFile(filePaht + "工作动态/");
        ZipUtil.createFile(filePaht + "创新发布清单/");
        ZipUtil.createFile(filePaht + "与会企业信息/");

        Long idIpaMain = entity.getId();

        // 创新发布清单的excel
        logger.info("生成创新发布清单excel");
        List<IplManageMain> iplList = iplManageMainService
                .list(new LambdaQueryWrapper<IplManageMain>().eq(IplManageMain::getIdIpaMain, idIpaMain).orderByDesc(IplManageMain::getGmtSubmit, IplManageMain::getGmtModified));
        if (CollectionUtils.isNotEmpty(iplList)) {
            iplExcel(iplList, filePaht);
        }
        // 工作动态的excel
        logger.info("生成工作动态excel");
        List<DailyWorkStatusPackage> dwspList = dailyWorkStatusPackageService
                .list(new LambdaQueryWrapper<DailyWorkStatusPackage>().eq(DailyWorkStatusPackage::getIdIpaMain, idIpaMain).orderByDesc(DailyWorkStatusPackage::getGmtSubmit, DailyWorkStatusPackage::getGmtModified));
        if (CollectionUtils.isNotEmpty(dwspList)) {
            dwsExcel(dwspList, filePaht);
        }
        // 与会信息的excel
        logger.info("生成与会信息excel");
        List<PmInfoDept> pmpList = pmInfoDeptService
                .list(new LambdaQueryWrapper<PmInfoDept>().eq(PmInfoDept::getIdIpaMain, idIpaMain).orderByDesc(PmInfoDept::getGmtSubmit, PmInfoDept::getGmtModified));
        if (CollectionUtils.isNotEmpty(pmpList)) {
            pmExcel(pmpList, filePaht);
        }

        // 发布活动详情页的excel
        logger.info("生成发布活动详情页的excel");
        glanceExcel(entity, filePaht, iplList, dwspList, pmpList);

        // 压缩
        ZipUtil.zip(basePath + "创新发布.zip", filePaht);

        // 给用户响应
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("", new String("创新发布".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] content = FileReaderUtil.getBytes(new File(basePath + "创新发布.zip"));

        //删除目录下所有的文件;
        ZipUtil.delFile(new File(basePath));

        return Mono.just(new ResponseEntity<>(content, headers, HttpStatus.CREATED));
    }

    private void glanceExcel(IpaManageMain entity, String filePaht, List<IplManageMain> iplList, List<DailyWorkStatusPackage> dwspList, List<PmInfoDept> pmpList) {
        int dwsStartRowIndex = 6;
        int iplStartRowIndex = 8;
        XSSFWorkbook wb = ExcelExportByTemplate.getWorkBook("template/ipa.xlsx");
        XSSFSheet sheet = wb.getSheetAt(0);

        // 标题
        sheet.getRow(0).getCell(0).setCellValue(entity.getTitle());

        // 发布人信息
        XSSFRow createrInfoRow = sheet.createRow(3);
        createrInfoRow.createCell(0).setCellValue(entity.getName());
        createrInfoRow.createCell(1).setCellValue(dicUtils.getDicValueByCode(DicConstants.IPA_LEVEL, entity.getLevel()));
        createrInfoRow.createCell(2).setCellValue(InnovationUtil.getDeptNameById(entity.getIdRbacDepartment()));

        // 日常工作动态
        if (CollectionUtils.isNotEmpty(dwspList)){
            sheet.shiftRows(dwsStartRowIndex, sheet.getLastRowNum(), dwspList.size(),true,false);
            for (DailyWorkStatusPackage dws : dwspList) {
                XSSFRow row = sheet.createRow(dwsStartRowIndex++);
                row.createCell(0).setCellValue(dws.getTitle());
                row.createCell(1).setCellValue(InnovationUtil.getDeptNameById(dws.getIdRbacDepartment()));
                row.createCell(2).setCellValue(DateUtils.timeStamp2Date(dws.getGmtSubmit()));
            }
        }
        // 清单
        if (CollectionUtils.isNotEmpty(iplList)){
            iplStartRowIndex = dwsStartRowIndex == 6?iplStartRowIndex:dwsStartRowIndex + 2;
            sheet.shiftRows(iplStartRowIndex, sheet.getLastRowNum(), iplList.size(),true, false);
            for (IplManageMain iplManageMain : iplList) {
                XSSFRow row = sheet.createRow(iplStartRowIndex++);
                row.createCell(0).setCellValue(iplManageMain.getTitle());
                row.createCell(1).setCellValue(BizTypeEnum.ofName(iplManageMain.getBizType()));
                row.createCell(2).setCellValue(InnovationUtil.getDeptNameById(iplManageMain.getIdRbacDepartmentDuty()));
                row.createCell(3).setCellValue(DateUtils.timeStamp2Date(iplManageMain.getGmtSubmit()));
            }
        }
        // 与会信息
        if (CollectionUtils.isNotEmpty(pmpList)){
            for (PmInfoDept pmInfoDept : pmpList) {
                XSSFRow row = sheet.createRow(iplStartRowIndex++ + 2);
                row.createCell(0).setCellValue(pmInfoDept.getTitle());
                row.createCell(1).setCellValue(BizTypeEnum.ofName(pmInfoDept.getBizType()));
                row.createCell(2).setCellValue(InnovationUtil.getDeptNameById(pmInfoDept.getIdRbacDepartment()));
                row.createCell(3).setCellValue(DateUtils.timeStamp2Date(pmInfoDept.getGmtSubmit()));
            }
        }
        ExcelExportByTemplate.downloadToPath(filePaht  + entity.getTitle() + ".xlsx", wb);
    }

    private void iplExcel(List<IplManageMain> list, String filePaht) {
        list.forEach(e -> {
            XSSFWorkbook wb;
            String snapshot = e.getSnapshot();
            switch (BizTypeEnum.of(e.getBizType())) {
                case INTELLIGENCE: // 组织部
                    wb = ExcelExportByTemplate.getWorkBook("template/od.xlsx");
                    ExcelExportByTemplate.setData(2, e.getTitle(), iplManageMainService.getOdData(e.getSnapshot()), e.getNotes(), wb);
                    break;
                case ENTERPRISE: // 企服局
                    wb = ExcelExportByTemplate.getWorkBook("template/esb.xlsx");
                    ExcelExportByTemplate.setData(2, e.getTitle(), iplManageMainService.getEbsData(e.getSnapshot()), e.getNotes(), wb);
                    break;
                case GROW: // 科技局
                    wb = ExcelExportByTemplate.getWorkBook("template/satb.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), iplManageMainService.getSatbData(snapshot), e.getNotes(), wb);
                    break;
                case CITY: // 发改局
                    wb = ExcelExportByTemplate.getWorkBook("template/darb.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), iplManageMainService.getDarbData(snapshot), e.getNotes(), wb);
                    break;
                case POLITICAL:
                    // 清新政商
                    wb = ExcelExportByTemplate.getWorkBook("template/suggestion.xlsx");
                    List<List<Object>> dataList = new ArrayList<>();
                    List<Integer> merge = new ArrayList<>();
                    if (StringUtils.isNotBlank(snapshot)) {
                        List<Map> parse = JSON.parseObject(snapshot, List.class);
                        parse.forEach(m -> {
                            dataList.add(
                                    Arrays.asList(
                                            IplCategoryEnum.ofName(MapUtils.getInteger(m, "category")),
                                            m.get("description"),
                                            DateUtils.timeStamp2Date(MapUtils.getLong(m, "gmtCreate")))
                            );
                        });

                        parse.stream().collect(Collectors.groupingBy(m -> m.get("category"), LinkedHashMap::new, Collectors.toList()))
                        .values().forEach(m->merge.add(m.size()));
                    }
                    ExcelExportByTemplate.setData(2, e.getTitle(), dataList, e.getNotes(), wb);
                    // 处理首列单元格合并
                    XSSFSheet sheet = wb.getSheetAt(0);
                    int mergeStartIndex = 2;
                    for (Integer integer : merge) {
                        if (integer > 1){
                            CellRangeAddress cellRangeAddress = new CellRangeAddress(mergeStartIndex, mergeStartIndex + integer-1, 0, 0);
                            sheet.addMergedRegion(cellRangeAddress);
                        }
                        mergeStartIndex += integer;
                    }
                    break;
                default:
                    logger.error("bizType错误:" + e.getBizType(), e);
                    throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("数据不存在").build();
            }

            ExcelExportByTemplate.downloadToPath(filePaht + "创新发布清单/" + e.getTitle() + "_" +System.currentTimeMillis() + ".xlsx", wb);
        });
    }

    private void dwsExcel(List<DailyWorkStatusPackage> list, String filePaht) {
        list.forEach(e -> {
            e.setDataList(dailyWorkStatusPackageService.addDataList(e));
            e.getDataList().forEach(d ->
                    {
                        if (CollectionUtils.isNotEmpty(d.getAttachmentList())) {
                            d.setAttachmentCode(d.getAttachmentList().stream().map(Attachment::getUrl).collect(joining("\n")));
                        } else {
                            d.setAttachmentCode(" ");
                        }
                    }
            );
            // 发改局导出
            List<List<Object>> data = iplManageMainService.getDwspData(e.getDataList());
            XSSFWorkbook wb = ExcelExportByTemplate.getWorkBook("template/dwsp.xlsx");
            ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
            ExcelExportByTemplate.downloadToPath(filePaht + "工作动态/" + e.getTitle() + "_" +System.currentTimeMillis()  + ".xlsx", wb);
        });
    }

    private void pmExcel(List<PmInfoDept> list, String filePaht) {
        list.forEach(e -> {
            XSSFWorkbook wb;
            // 入区
            PmInfoDept pmInfoDept = pmInfoDeptService.detailById(e.getId());
            if (BizTypeEnum.RQDEPTINFO.getType().equals(e.getBizType())) {
                List<InfoDeptYzgt> dataList = pmInfoDept.getDataList();
                dataList.forEach(d -> {
                    List<Attachment> attachmentList = d.getAttachmentList();
                    if (CollectionUtils.isNotEmpty(attachmentList)){
                        d.setAttachmentCode(attachmentList.stream().map(Attachment::getUrl).collect(joining("\n")));
                    }
                });
                List<List<Object>> data = pmInfoDeptService.getYzgtData(dataList);
                wb = ExcelExportByTemplate.getWorkBook("template/rq.xlsx");
                ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
                //  路演
            } else if (BizTypeEnum.LYDEPTINFO.getType().equals(e.getBizType())) {
                List<InfoDeptSatb> dataList = pmInfoDept.getDataList();
                List<List<Object>> data = pmInfoDeptService.getSatbData(dataList);
                wb = ExcelExportByTemplate.getWorkBook("template/ly.xlsx");
                ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
            } else if (BizTypeEnum.INVESTMENT.getType().equals(e.getBizType())) {
                List<List<Object>> data = pmInfoDeptService.getYzgtData(e.getSnapShot());
                wb = ExcelExportByTemplate.getWorkBook("template/invest.xlsx");
                ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
            } else {
                logger.error("bizType-错误：" + e.getBizType(), e);
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("数据不存在").build();
            }

            ExcelExportByTemplate.downloadToPath(filePaht + "与会企业信息/" + e.getTitle() + "_" + System.currentTimeMillis()  + ".xlsx", wb);
        });
    }

    /**
     * 更新发布效果
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 11:32 上午
     */
    @PostMapping("/updatePublishResult")
    public Mono<ResponseEntity<SystemResponse<Object>>> updatePublishResult(@RequestBody IpaManageMain entity) {
        if (StringUtils.isBlank(entity.getPublishResult())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        if (entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        IpaManageMain byId = ipaManageMainService.getById(entity.getId());
        if (byId == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        if (!IpaStatusEnum.UNUPDATE.getId().equals(byId.getStatus())){
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "数据状态已发生改变，请刷新页面");
        }
        logger.info("二次包更新发布效果：" + entity.getId());
        entity.setTitle(byId.getTitle());
        entity.setLevel(byId.getLevel());
        entity.setIdRbacDepartment(byId.getIdRbacDepartment());
        entity.setName(byId.getName());
        ipaManageMainService.updatePublishResult(entity);
        return success();
    }

    /**
     * 获取发布结果
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 11:32 上午
     */
    @PostMapping("/getPublisResult/{id}")
    public Mono<ResponseEntity<SystemResponse<Object>>> getPublisResult(@PathVariable("id") Long id) {
        IpaManageMain byId = ipaManageMainService.getById(id);
        if (byId == null) {
            return success();
        }
        String participateMedia = byId.getParticipateMedia();
        String publishMedia = byId.getPublishMedia();
        Set<Long> idMedias = new HashSet<>();
        if (StringUtils.isNotBlank(participateMedia)) {
            idMedias.addAll(Arrays.asList(participateMedia.split(",")).stream().map(s -> Long.parseLong(s)).collect(Collectors.toSet()));
        }
        if (StringUtils.isNotBlank(publishMedia)) {
            idMedias.addAll(Arrays.asList(publishMedia.split(",")).stream().map(s -> Long.parseLong(s)).collect(Collectors.toSet()));
        }
        StringBuilder participateMediaName = new StringBuilder();
        StringBuilder publishMediaName = new StringBuilder();
        if (CollectionUtils.isNotEmpty(idMedias)) {
            List<MediaManager> list = mediaManagerService.list(new LambdaQueryWrapper<MediaManager>().in(MediaManager::getId, idMedias));
            Map<Long, String> collect = list.stream().collect(Collectors.toMap(MediaManager::getId, MediaManager::getMediaName));

            if (StringUtils.isNotBlank(participateMedia)) {
                Arrays.stream(participateMedia.split(",")).forEach(e -> {
                    participateMediaName.append(collect.get(Long.parseLong(e)) + ",");
                });
            }
            if (StringUtils.isNotBlank(publishMedia)) {
                Arrays.stream(publishMedia.split(",")).forEach(e -> {
                    publishMediaName.append(collect.get(Long.parseLong(e)) + ",");
                });
            }
        }

        IpaManageMain build = IpaManageMain.newInstance().participateMedia(participateMedia).publishMedia(publishMedia)
                .publishResult(byId.getPublishResult()).publishMediaName(StringUtils.stripEnd(publishMediaName.toString(), ","))
                .participateMediaName(StringUtils.stripEnd(participateMediaName.toString(), ",")).build();

        return success(build);
    }

    /**
     * 发布
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 11:32 上午
     */
    @PostMapping("/publish")
    public Mono<ResponseEntity<SystemResponse<Object>>> publish(@RequestBody IpaManageMain entity) {
        if (entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        IpaManageMain byId = ipaManageMainService.getById(entity.getId());
        if (byId == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        if (!IpaStatusEnum.UNPUBLISH.getId().equals(byId.getStatus())){
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "数据状态已发生改变，请刷新页面");
        }
        logger.info("二次包发布：" + entity.getId());
        entity.setTitle(byId.getTitle());
        entity.setLevel(byId.getLevel());
        entity.setIdRbacDepartment(byId.getIdRbacDepartment());
        entity.setName(byId.getName());
        ipaManageMainService.publish(entity);
        return success();
    }

    /**
     * 批量删除
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 11:32 上午
     */
    @PostMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIds(@RequestBody Map<String, String> map) {
        String ids = map.get("ids");
        if (StringUtils.isBlank(ids)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        int count = ipaManageMainService.count(new LambdaQueryWrapper<IpaManageMain>().in(IpaManageMain::getId, ids).ne(IpaManageMain::getStatus, IpaStatusEnum.UNPUBLISH.getId()));
        if (count > 0) {
            return error(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION, "非待发布状态数据不允许删除");
        }
        ipaManageMainService.delByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success();
    }

    /**
     * 分页查询
     *
     * @param pageEntity 统一查询条件
     * @returns
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IpaManageMain> pageEntity) {

        LambdaQueryWrapper<IpaManageMain> ew = new LambdaQueryWrapper<>();
        IpaManageMain entity = pageEntity.getEntity();
        if (entity != null) {
            String createDate = entity.getCreateDate();
            if (StringUtils.isNotBlank(createDate)) {
                ew.gt(IpaManageMain::getGmtCreate, InnovationUtil.getFirstTimeInMonth(createDate, true));
                ew.lt(IpaManageMain::getGmtCreate, InnovationUtil.getFirstTimeInMonth(createDate, false));
            }
            Integer status = entity.getStatus();
            if (status != null) {
                ew.eq(IpaManageMain::getStatus, status);
            }
        }
        ew.orderByDesc(IpaManageMain::getGmtCreate);

        IPage<IpaManageMain> p = ipaManageMainService.page(pageEntity.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);
    }

    private List<Map<String, Object>> convert2List(List<IpaManageMain> list) {
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    m.put("statusName", IpaStatusEnum.getNameById(entity.getStatus()));
                }
                , IpaManageMain::getTitle, BaseEntity::getGmtCreate, BaseEntity::getId, IpaManageMain::getStatus
        );
    }

    /**
     * 新增或者编辑活动管理
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/17 10:17 上午
     */
    @PostMapping("saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IpaManageMain entity) {
        // 新增和编辑需要登录
        LoginContextHolder.getRequestAttributes();
        // 新增
        if (entity.getId() == null) {
            ipaManageMainService.add(entity);
            // 编辑
        } else {
            IpaManageMain byId = ipaManageMainService.getById(entity.getId());
            if (byId == null) {
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("数据不存在").build();
            }
            if (!IpaStatusEnum.UNPUBLISH.getId().equals(byId.getStatus())) {
                return error(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION, "非待发布状态数据不允许编辑");
            }
            ipaManageMainService.edit(entity);
        }
        return success();
    }

    /**
     * 详情
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 11:33 上午
     */
    @GetMapping("/detailById/{id}")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@PathVariable("id") Long id) {

        IpaManageMain entity = ipaManageMainService.getById(id);
        if (entity == null) {
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        entity.setIdRbacDepartmentName(InnovationUtil.getDeptNameById(entity.getIdRbacDepartment()));
        entity.setLevelName(dicUtils.getDicValueByCode(DicConstants.IPA_LEVEL, entity.getLevel()));

        // 清单一次包
        LambdaQueryWrapper<IplManageMain> iplQw = new LambdaQueryWrapper<>();
        iplQw.eq(IplManageMain::getIdIpaMain, entity.getId()).orderByDesc(IplManageMain::getGmtSubmit, IplManageMain::getGmtModified);
        List<IplManageMain> iplpList = iplManageMainService.list(iplQw);
        entity.setIplpList(convert2ListForPkg(iplpList));

        // 与会一次包
        LambdaQueryWrapper<PmInfoDept> pmQw = new LambdaQueryWrapper<>();
        pmQw.eq(PmInfoDept::getIdIpaMain, entity.getId()).orderByDesc(PmInfoDept::getGmtSubmit, PmInfoDept::getGmtModified);
        List<PmInfoDept> pmpList = pmInfoDeptService.list(pmQw);
        entity.setPmpList(convert2ListForPmp(pmpList));

        // 工作动态一次包
        LambdaQueryWrapper<DailyWorkStatusPackage> dwspQw = new LambdaQueryWrapper<>();
        dwspQw.eq(DailyWorkStatusPackage::getIdIpaMain, entity.getId()).orderByDesc(DailyWorkStatusPackage::getGmtSubmit, DailyWorkStatusPackage::getGmtModified);
        List<DailyWorkStatusPackage> dwspList = dailyWorkStatusPackageService.list(dwspQw);
        entity.setDwspList(convert2ListForDwsp(dwspList));

        return success(entity);
    }
}

