package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseEntity;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Customer;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.JsonUtil;
import com.unity.common.utils.ExcelExportByTemplate;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.entity.DailyWorkStatusPackage;
import com.unity.innovation.entity.PmInfoDept;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.IpaStatusEnum;
import com.unity.innovation.service.DailyWorkStatusPackageServiceImpl;
import com.unity.innovation.service.IpaManageMainServiceImpl;
import com.unity.innovation.service.IplManageMainServiceImpl;
import com.unity.innovation.service.PmInfoDeptServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import com.unity.innovation.util.ZipUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 创新发布活动-管理-主表
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

    /**
     * 二次打包一键下载
     *
     * @param
     * @return
     * @author qinhuan
     * @since 2019/10/21 2:42 下午
     */
    @GetMapping("batchExport")
    public void batchExport(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) throws Exception{
        IpaManageMain entity = ipaManageMainService.getById(id);
        if (entity == null){
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
            dwspList.forEach(e->{
                // TODO
            });
        }
        // 与会信息的excel
        List<PmInfoDept> pmpList = pmInfoDeptService
                .list(new LambdaQueryWrapper<PmInfoDept>().eq(PmInfoDept::getIdIpaMain, idIpaMain));
        if (CollectionUtils.isNotEmpty(pmpList)) {
            pmpList.forEach(e->{
                // TODO
            });
        }
        
        //生成.zip文件;
        ZipUtil.zip(basePath + "创新发布.zip", filePaht);
        ExcelExportByTemplate.responseFile(request, response, "创新发布.zip");

        //删除目录下所有的文件;
//        ZipUtil.delFile(new File(basePath));  TODO
    }
    
    private void iplExcel(Long idIpaMain, String filePaht) {
        List<IplManageMain> list = iplManageMainService
                .list(new LambdaQueryWrapper<IplManageMain>().eq(IplManageMain::getIdIpaMain, idIpaMain));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> {
                XSSFWorkbook wb;
                // 发改局导出
                if (InnovationConstant.DEPARTMENT_DARB_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data = iplManageMainService.getDarbData(e.getSnapshot());
                    wb = ExcelExportByTemplate.getWorkBook("template/darb.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), data, e.getNotes(), wb);
                    //  科技局导出
                } else if (InnovationConstant.DEPARTMENT_SATB_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data = null; // TODO
                    wb = ExcelExportByTemplate.getWorkBook("template/darb.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), data, e.getNotes(), wb);
                    // 组织部导出
                } else if (InnovationConstant.DEPARTMENT_OD_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data =iplManageMainService.getOdData(e.getSnapshot());
                    wb = ExcelExportByTemplate.getWorkBook("template/od.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), data, e.getNotes(), wb);
                    //  企服局导出
                } else if (InnovationConstant.DEPARTMENT_ESB_ID.equals(e.getIdRbacDepartmentDuty())) {
                    List<List<Object>> data = iplManageMainService.getEbsData(e.getSnapshot());
                    wb = ExcelExportByTemplate.getWorkBook("template/esb.xlsx");
                    ExcelExportByTemplate.setData(4, e.getTitle(), data, e.getNotes(), wb);
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
        ipaManageMainService.update(new LambdaUpdateWrapper<IpaManageMain>().eq(IpaManageMain::getId, entity.getId()).set(IpaManageMain::getPublishResult, entity.getPublishResult()));
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
        if (entity != null){
            String createDate = entity.getCreateDate();
            if (StringUtils.isNotBlank(createDate)) {
                ew.gt(IpaManageMain::getGmtCreate, InnovationUtil.getFirstTimeInMonth(createDate, true));
                ew.lt(IpaManageMain::getGmtCreate, InnovationUtil.getFirstTimeInMonth(createDate, false));
            }
            Integer status = entity.getStatus();
            if (status != null){
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

    private List<Map<String, Object>> convert2List(List<IpaManageMain> list){
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    m.put("statusName", IpaStatusEnum.getNameById(entity.getStatus()));
                }
                ,IpaManageMain::getTitle, BaseEntity::getGmtCreate, BaseEntity::getId, IpaManageMain::getStatus
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
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody IpaManageMain entity){
        // 新增和编辑需要登录
        Customer customer = LoginContextHolder.getRequestAttributes();
        // 新增
        if (entity.getId() == null){
            ipaManageMainService.add(entity); // TODO 列表是否需要自己写
        // 编辑
        }else {
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
        if (entity == null){
            return error(SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST, SystemResponse.FormalErrorCode.DATA_DOES_NOT_EXIST.getName());
        }

        // 清单一次包
        LambdaQueryWrapper<IplManageMain> iplQw = new LambdaQueryWrapper<>();
        iplQw.eq(IplManageMain::getIdIpaMain, entity.getId()).orderByDesc(IplManageMain::getGmtSubmit, IplManageMain::getGmtModified);
        List<IplManageMain> iplpList = iplManageMainService.list(iplQw);
        entity.setIplpList(iplpList);

        // 与会一次包
        LambdaQueryWrapper<PmInfoDept> pmQw = new LambdaQueryWrapper<>();
        pmQw.eq(PmInfoDept::getIdIpaMain, entity.getId()).orderByDesc(PmInfoDept::getGmtSubmit, PmInfoDept::getGmtModified);
        List<PmInfoDept> pmpList = pmInfoDeptService.list(pmQw);
        entity.setPmpList(pmpList);

        // 工作动态一次包
        LambdaQueryWrapper<DailyWorkStatusPackage> dwspQw = new LambdaQueryWrapper<>();
        dwspQw.eq(DailyWorkStatusPackage::getIdIpaMain, entity.getId()).orderByDesc(DailyWorkStatusPackage::getGmtSubmit,DailyWorkStatusPackage::getGmtModified);
        List<DailyWorkStatusPackage> dwspList = dailyWorkStatusPackageService.list(dwspQw);
        entity.setDwspList(dwspList);

        return success(entity);
    }
}

