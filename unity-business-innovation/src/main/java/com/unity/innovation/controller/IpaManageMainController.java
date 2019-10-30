package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.DicConstants;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.ExcelExportByTemplate;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.*;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.*;
import com.unity.innovation.service.*;
import com.unity.innovation.util.DownloadUtil;
import com.unity.innovation.util.InnovationUtil;
import com.unity.innovation.util.ZipUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.util.Lists;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
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

            ew.notIn(PmInfoDept::getStatus, Lists.newArrayList(WorkStatusAuditingStatusEnum.TEN.getId(), WorkStatusAuditingStatusEnum.FORTY.getId()));
            if (entity.getIdRbacDepartment() != null) {
                ew.eq(PmInfoDept::getIdRbacDepartment, entity.getIdRbacDepartment());
            }
            if (entity.getId() != null){
                ew.and(e->e.isNull(PmInfoDept::getIdIpaMain).or().eq(PmInfoDept::getIdIpaMain, entity.getId()));
            }else {
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
        ew.notIn(DailyWorkStatusPackage::getState, Lists.newArrayList(WorkStatusAuditingStatusEnum.TEN.getId(), WorkStatusAuditingStatusEnum.FORTY.getId()));
        // 提交单位
        if (entity.getIdRbacDepartment() != null) {
            ew.eq(DailyWorkStatusPackage::getIdRbacDepartment, entity.getIdRbacDepartment());
        }
        if (entity.getId() != null){
            ew.and(e->e.isNull(DailyWorkStatusPackage::getIdIpaMain).or().eq(DailyWorkStatusPackage::getIdIpaMain, entity.getId()));
        }else {
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

            ew.notIn(IplManageMain::getStatus, Lists.newArrayList(WorkStatusAuditingStatusEnum.TEN.getId(), WorkStatusAuditingStatusEnum.FORTY.getId()));
            if (entity.getIdRbacDepartmentDuty() != null) {
                ew.eq(IplManageMain::getIdRbacDepartmentDuty, entity.getIdRbacDepartmentDuty());
            }
        }
        if (entity.getId() != null){
            ew.and(e->e.isNull(IplManageMain::getIdIpaMain).or().eq(IplManageMain::getIdIpaMain, entity.getId()));
        }else {
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
                    m.put("infoTypeName", InfoTypeEnum.of(entity.getIdRbacDepartment()).getName());
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
                this::adapterField, IplManageMain::getId, IplManageMain::getTitle, IplManageMain::getGmtSubmit);
    }

    private void adapterField(Map<String, Object> m, IplManageMain entity) {
        // 清单类型
        ListCategoryEnum of = ListCategoryEnum.of(entity.getIdRbacDepartmentDuty());
        m.put("listType", of == null ? "" : of.getListType());
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
    public void batchExport(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
        IpaManageMain entity = ipaManageMainService.getById(id);
        if (entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("数据不存在").build();
        }

        // 创建文件夹
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        String basePath = resource.getPath() + UUIDUtil.getUUID() + "/";
        String filePaht = basePath + "创新发布/";
        //创建文件夹;
        ZipUtil.createFile(filePaht + "工作动态/");
        ZipUtil.createFile(filePaht + "创新发布清单/");
        ZipUtil.createFile(filePaht + "与会企业信息/");

        Long idIpaMain = entity.getId();
        // 创新发布清单的excel
        iplExcel(idIpaMain, filePaht);
        // 工作动态的excel
        List<DailyWorkStatusPackage> dwspList = dailyWorkStatusPackageService
                .list(new LambdaQueryWrapper<DailyWorkStatusPackage>().eq(DailyWorkStatusPackage::getIdIpaMain, idIpaMain));
        if (CollectionUtils.isNotEmpty(dwspList)) {
            dwspList.forEach(e -> {
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
                ExcelExportByTemplate.downloadToPath(filePaht + "工作动态/" + e.getTitle() + ".xlsx", wb);
            });
        }
        // 与会信息的excel
        List<PmInfoDept> pmpList = pmInfoDeptService
                .list(new LambdaQueryWrapper<PmInfoDept>().eq(PmInfoDept::getIdIpaMain, idIpaMain));
        if (CollectionUtils.isNotEmpty(pmpList)) {
            pmpList.forEach(e -> {
                XSSFWorkbook wb;
                // 入区
                PmInfoDept pmInfoDept = pmInfoDeptService.detailById(e.getId());
                if (BizTypeEnum.RQDEPTINFO.equals(e.getBizType())) {
                    List<InfoDeptYzgt> dataList = pmInfoDept.getDataList();
                    dataList.forEach(d -> d.setAttachmentCode(
                            d.getAttachmentList().stream().map(Attachment::getUrl).collect(joining("\n"))));
                    List<List<Object>> data = pmInfoDeptService.getYzgtData(dataList);
                    wb = ExcelExportByTemplate.getWorkBook("template/rq.xlsx");
                    ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
                    //  路演
                } else if (BizTypeEnum.LYDEPTINFO.equals(e.getBizType())) {
                    List<InfoDeptSatb> dataList = pmInfoDept.getDataList();
                    List<List<Object>> data = pmInfoDeptService.getSatbData(dataList);
                    wb = ExcelExportByTemplate.getWorkBook("template/ly.xlsx");
                    ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
                } else if(BizTypeEnum.INVESTMENT.equals(e.getBizType())) {
                    List<List<Object>> data = pmInfoDeptService.getYzgtData(e.getSnapShot());
                    wb = ExcelExportByTemplate.getWorkBook("template/invest.xlsx");
                    ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
                }
            });
        }

        //生成.zip文件;
        ZipUtil.zip(basePath + "创新发布.zip", filePaht);
        DownloadUtil.downloadFile(new File(basePath + "创新发布.zip"), "创新发布.zip", response, request);

        //删除目录下所有的文件;
        ZipUtil.delFile(new File(basePath));
    }

    private void iplExcel(Long idIpaMain, String filePaht) {
        List<IplManageMain> list = iplManageMainService
                .list(new LambdaQueryWrapper<IplManageMain>().eq(IplManageMain::getIdIpaMain, idIpaMain));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> {
                XSSFWorkbook wb;
                String snapshot = e.getSnapshot();
                // 发改局导出
                if (InnovationConstant.DEPARTMENT_DARB_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data = iplManageMainService.getDarbData(snapshot);
                    wb = ExcelExportByTemplate.getWorkBook("template/darb.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), data, e.getNotes(), wb);
                    //  科技局导出
                } else if (InnovationConstant.DEPARTMENT_SATB_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data = iplManageMainService.getSatbData(snapshot);
                    wb = ExcelExportByTemplate.getWorkBook("template/satb.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), data, e.getNotes(), wb);
                    // 组织部导出
                } else if (InnovationConstant.DEPARTMENT_OD_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data = iplManageMainService.getOdData(e.getSnapshot());
                    wb = ExcelExportByTemplate.getWorkBook("template/od.xls");
                    ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
                    //  企服局导出
                } else if (InnovationConstant.DEPARTMENT_ESB_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data = iplManageMainService.getEbsData(e.getSnapshot());
                    wb = ExcelExportByTemplate.getWorkBook("template/esb.xls");
                    ExcelExportByTemplate.setData(2, e.getTitle(), data, e.getNotes(), wb);
                } else {
                    throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("数据不存在").build();
                }

                ExcelExportByTemplate.downloadToPath(filePaht + "创新发布清单/" + e.getTitle() + ".xlsx", wb);
            });
        }
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
        if (StringUtils.isBlank(entity.getPublishResult())){
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM.getName());
        }
        if (entity.getId() == null || ipaManageMainService.getById(entity.getId()) == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        entity.setStatus(IpaStatusEnum.UPDATED.getId());
        ipaManageMainService.updateIpaMain(entity);
        return success();
    }

    /**
     * 发布
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/19 11:32 上午
     */
    @PostMapping("/getPublisResult/{id}")
    public Mono<ResponseEntity<SystemResponse<Object>>> getPublisResult(@PathVariable("id") Long id) {
        IpaManageMain byId = ipaManageMainService.getById(id);
        if (byId == null){
            return success();
        }
        String participateMedia = byId.getParticipateMedia();
        String publishMedia = byId.getPublishMedia();
        Set<Long> idMedias = new HashSet<>();
        if (StringUtils.isNotBlank(participateMedia)){
            idMedias.addAll(Arrays.asList(participateMedia.split(",")).stream().map(s -> Long.parseLong(s)).collect(Collectors.toSet()));
        }
        if (StringUtils.isNotBlank(publishMedia)){
            idMedias.addAll(Arrays.asList(publishMedia.split(",")).stream().map(s -> Long.parseLong(s)).collect(Collectors.toSet()));
        }
        StringBuilder participateMediaName = new StringBuilder();
        StringBuilder publishMediaName = new StringBuilder();
        if (CollectionUtils.isNotEmpty(idMedias)){
            List<MediaManager> list = mediaManagerService.list(new LambdaQueryWrapper<MediaManager>().in(MediaManager::getId, idMedias));
            Map<Long, String> collect = list.stream().collect(Collectors.toMap(MediaManager::getId, MediaManager::getMediaName));

            if (StringUtils.isNotBlank(participateMedia)){
                Arrays.stream(participateMedia.split(",")).forEach(e->{
                    participateMediaName.append(collect.get(Long.parseLong(e)) + ",");
                });
            }
            if (StringUtils.isNotBlank(publishMedia)){
                Arrays.stream(publishMedia.split(",")).forEach(e->{
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
        if (entity.getId() == null || ipaManageMainService.getById(entity.getId()) == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }
        IpaManageMain build = IpaManageMain.newInstance().status(IpaStatusEnum.UNUPDATE.getId()).build();
        build.setId(entity.getId());
        ipaManageMainService.updateIpaMain(build);
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
        int count = ipaManageMainService.count(new LambdaQueryWrapper<IpaManageMain>().in(IpaManageMain::getId, ids).ne(IpaManageMain::getStatus, IpaStatusEnum.UNPUBLISH));
        if (count > 0){
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
            if (byId == null){
                throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST).message("数据不存在").build();
            }
            if (!IpaStatusEnum.UNPUBLISH.getId().equals(byId.getStatus())){
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

