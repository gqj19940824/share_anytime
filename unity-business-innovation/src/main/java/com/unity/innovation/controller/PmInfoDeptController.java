
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.constants.ConstString;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.entity.PmInfoDept;
import com.unity.innovation.enums.InfoTypeEnum;
import com.unity.innovation.service.InfoDeptSatbServiceImpl;
import com.unity.innovation.service.PmInfoDeptServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * 企业信息发布管理
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


    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<PmInfoDept> pageEntity) {
        Page<PmInfoDept> pageable = pageEntity.getPageable();
        PmInfoDept entity = pageEntity.getEntity();
        LambdaQueryWrapper<PmInfoDept> ew = service.wrapper(entity);

        IPage<PmInfoDept> p = service.page(pageable, ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    

    @PostMapping("/save")
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody PmInfoDept entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }
    



    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<PmInfoDept> list){
       
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    m.put("infoTypeName", InfoTypeEnum.of(entity.getIdRbacDepartment()).getName());
                    m.put("departmentName",InnovationUtil.getDeptNameById(entity.getIdRbacDepartment()));

                }
                ,PmInfoDept::getId,PmInfoDept::getSort,PmInfoDept::getNotes,PmInfoDept::getTitle,PmInfoDept::getGmtSubmit,PmInfoDept::getStatus,PmInfoDept::getAttachmentCode,PmInfoDept::getIdRbacDepartment,PmInfoDept::getInfoType
        );
    }

    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(null);
    }

    /**
     * 功能描述 分页列表查询
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
     * @param list 集合
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>> 规范数据
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

}

