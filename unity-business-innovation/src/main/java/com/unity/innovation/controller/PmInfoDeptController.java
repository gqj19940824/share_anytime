
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.constants.ParamConstants;
import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.entity.InfoDeptYzgt;
import com.unity.innovation.entity.PmInfoDept;
import com.unity.innovation.entity.PmInfoDeptLog;
import com.unity.innovation.enums.InfoTypeEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.service.InfoDeptSatbServiceImpl;
import com.unity.innovation.service.InfoDeptYzgtServiceImpl;
import com.unity.innovation.service.PmInfoDeptServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 企业信息发布管理
 *
 * @author zhang
 * 生成时间 2019-10-15 15:33:01
 */
@RestController
@RequestMapping("/pmInfoDept")
public class PmInfoDeptController extends BaseWebController {

    @Resource
    PmInfoDeptServiceImpl service;
    @Resource
    InfoDeptSatbServiceImpl satbService;
    @Resource
    InfoDeptYzgtServiceImpl yzgtService;


    /**
    * 列表查询
    *
    * @param pageEntity 分页条件
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/17 11:15
    */
    @PostMapping("/listByPage/{flag}")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<PmInfoDept> pageEntity) {
        Page<PmInfoDept> pageable = pageEntity.getPageable();
        PmInfoDept entity = pageEntity.getEntity();
        LambdaQueryWrapper<PmInfoDept> ew = service.wrapper(entity);

        IPage<PmInfoDept> p = service.page(pageable, ew);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }


    /**
     * 功能描述
     *
     * @param entity 保存计划
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/saveOrUpdate/{flag}")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody PmInfoDept entity) {
        Mono<ResponseEntity<SystemResponse<Object>>> obj = verifyParam(entity);
        if (obj != null) {
            return obj;
        }
        service.saveEntity(entity);
        return success("操作成功");
    }

    /**
     * 功能描述 数据校验
     *
     * @param entity 实体
     * @return 异常信息
     * @author gengzhiqiang
     * @date 2019/9/18 18:36
     */
    private Mono<ResponseEntity<SystemResponse<Object>>> verifyParam(PmInfoDept entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity, PmInfoDept::getTitle, PmInfoDept::getDataIdList, PmInfoDept::getCategory);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        if (StringUtils.isNotBlank(entity.getNotes()) && entity.getNotes().length() > ParamConstants.PARAM_MAX_LENGTH_500) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "备注限制500字");
        }
        if (entity.getTitle().length() > ParamConstants.PARAM_MAX_LENGTH_50) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "标题限制50字");
        }
        return null;
    }


    /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<PmInfoDept> list) {

        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    m.put("infoTypeName", InfoTypeEnum.of(entity.getIdRbacDepartment()).getName());
                    m.put("departmentName", InnovationUtil.getDeptNameById(entity.getIdRbacDepartment()));
                    m.put("statusName", Objects.requireNonNull(WorkStatusAuditingStatusEnum.of(entity.getStatus())).getName());
                }
                , PmInfoDept::getId, PmInfoDept::getSort, PmInfoDept::getNotes, PmInfoDept::getTitle, PmInfoDept::getGmtSubmit, PmInfoDept::getStatus, PmInfoDept::getAttachmentCode, PmInfoDept::getIdRbacDepartment, PmInfoDept::getInfoType
        );
    }



    /**
    * 批量删除
    *
    * @param ids 主键集合
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/17 11:15
    */
    @PostMapping("/removeByIds/{flag}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  removeByIds(@RequestBody List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "id不能为空");
        }
        service.removeDeptInfoByIds(ids);
        return success();
    }

    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listForSatb")
    public Mono<ResponseEntity<SystemResponse<Object>>> listForSatb(@RequestBody PageEntity<InfoDeptSatb> search) {
        IPage<InfoDeptSatb> list = satbService.listForSatb(search);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(convert2ListForSatb(list.getRecords())).build();
        return success(result);
    }

    /**
     * 功能描述 数据整理
     *
     * @param list 集合
     * @return java.util.List<java.util.Map   <   java.lang.String   ,   java.lang.Object>> 规范数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    private List<Map<String, Object>> convert2ListForSatb(List<InfoDeptSatb> list) {
        return JsonUtil.<InfoDeptSatb>ObjectToList(list,
                (m, entity) -> {
                },
                InfoDeptSatb::getId, InfoDeptSatb::getEnterpriseName,
                InfoDeptSatb::getIndustryCategory, InfoDeptSatb::getIndustryCategoryName,
                InfoDeptSatb::getEnterpriseScale, InfoDeptSatb::getEnterpriseScaleName,
                InfoDeptSatb::getEnterpriseNature, InfoDeptSatb::getEnterpriseNatureName,
                InfoDeptSatb::getInGeneralSituation, InfoDeptSatb::getInDetail,
                InfoDeptSatb::getAchievementLevel, InfoDeptSatb::getAchievementLevelName,
                InfoDeptSatb::getIsPublishFirst, InfoDeptSatb::getContactPerson, InfoDeptSatb::getContactWay,
                InfoDeptSatb::getGmtCreate, InfoDeptSatb::getGmtModified, InfoDeptSatb::getStatus
        );
    }


    /**
    * 通过/驳回
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/17 14:09
    */
    @PostMapping("/passOrReject/{flag}")
    public Mono<ResponseEntity<SystemResponse<Object>>> passOrReject(@RequestBody PmInfoDeptLog entity) {
        if(entity == null || entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "id不能为空");
        }
        PmInfoDept old = service.getById(entity.getId());
        if(old == null) {
            return error(SystemResponse.FormalErrorCode.MODIFY_DATA_OVER_LENTTH, "数据不存在");
        }
        //待审核才能审核
        if(!WorkStatusAuditingStatusEnum.TWENTY.getId().equals(old.getStatus())) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("此状态不能不能审核").build();
        }
        service.passOrReject(entity,old);
        return success();
    }
    /**
     * 功能描述  导出接口
     * @param id 数据id
     * @return 数据流
     * @author gengzhiqiang
     * @date 2019/10/11 11:07
     */
    @GetMapping({"/export/excel/{flag}"})
    public Mono<ResponseEntity<byte[]>> exportExcel(@RequestParam("id") Long id) {
        if (id == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到要导出的id").build();
        }
        PmInfoDept entity = service.getById(id);
        if (entity == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        String filename = entity.getTitle();
        byte[] content;
        HttpHeaders headers = new HttpHeaders();
        try {
            content = service.export(entity);
            //处理乱码
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("企业信息发布", new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".xls");
        } catch (Exception e) {
            throw UnityRuntimeException.newInstance()
                    .message(e.getMessage())
                    .code(SystemResponse.FormalErrorCode.SERVER_ERROR)
                    .build();
        }
        return Mono.just(new ResponseEntity<>(content, headers, HttpStatus.CREATED));

    }



    /**
     * 功能描述 提交接口
     *
     * @param entity 实体
     * @return 成功返回成功信息
     * @author gengzhiqiang
     * @date 2019/7/26 16:12
     */
    @PostMapping("/submit/{flag}")
    public Mono<ResponseEntity<SystemResponse<Object>>> submit(@RequestBody PmInfoDept entity) {
        String msg = ValidFieldUtil.checkEmptyStr(entity,PmInfoDept::getId);
        if (StringUtils.isNotBlank(msg)) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, msg);
        }
        service.submit(entity);
        return success("操作成功");
    }


    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 分页数据
     * @author gengzhiqiang
     * @date 2019/9/17 13:36
     */
    @PostMapping("/listForYzgt")
    public Mono<ResponseEntity<SystemResponse<Object>>> listForYzgt(@RequestBody PageEntity<InfoDeptYzgt> search) {
        IPage<InfoDeptYzgt> list = yzgtService.listForYzgt(search);
        List<InfoDeptYzgt> records = list.getRecords();
        yzgtService.convert2List(records);
        PageElementGrid result = PageElementGrid.<Map<String, Object>>newInstance()
                .total(list.getTotal())
                .items(JsonUtil.ObjectToList(records,
                        null
                        ,InfoDeptYzgt::getId,InfoDeptYzgt::getSort,InfoDeptYzgt::getNotes,InfoDeptYzgt::getEnterpriseName,InfoDeptYzgt::getIndustryCategory,InfoDeptYzgt::getEnterpriseScale,InfoDeptYzgt::getEnterpriseNature,InfoDeptYzgt::getContactPerson,InfoDeptYzgt::getContactWay,InfoDeptYzgt::getEnterpriseIntroduction,InfoDeptYzgt::getAttachmentCode,InfoDeptYzgt::getIdPmInfoDept,InfoDeptYzgt::getStatus,InfoDeptYzgt::getGmtModified,InfoDeptYzgt::getGmtCreate,InfoDeptYzgt::getEnterpriseScaleName,InfoDeptYzgt::getEnterpriseNatureName,InfoDeptYzgt::getIndustryCategoryName,InfoDeptYzgt::getStatusName,InfoDeptYzgt::getAttachmentList
                )).build();
        return success(result);
    }


    /**
    * 详情接口
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/17 15:34
    */
    @PostMapping("/detailById/{flag}")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody PmInfoDept entity) {
        if(entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "id不能为空");
        }
        return success(service.detailById(entity.getId()));
    }

}

