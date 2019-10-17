
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.*;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.lang3.StringUtils;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.Map;
import java.util.List;


import com.unity.innovation.service.InfoDeptYzgtServiceImpl;
import com.unity.innovation.entity.InfoDeptYzgt;
import javax.annotation.Resource;


/**
 * 入区企业信息管理-亦庄国投-基础数据表
 * @author zhang
 * 生成时间 2019-10-15 15:33:00
 */
@RestController
@RequestMapping("/infoDeptYZGT")
public class InfoDeptYzgtController extends BaseWebController {

    @Resource
    private InfoDeptYzgtServiceImpl service;




    /**
    * 列表查询
    *
    * @param pageEntity 分页条件
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/16 10:16
    */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<InfoDeptYzgt> pageEntity) {
        Page<InfoDeptYzgt> pageable = pageEntity.getPageable();
        InfoDeptYzgt entity = pageEntity.getEntity();
        LambdaQueryWrapper<InfoDeptYzgt> ew = wrapper(entity);
        IPage<InfoDeptYzgt> p = service.page(pageable, ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(service.convert2List(p.getRecords())).build();
        return success(result);

    }
    

     /**
     * 添加或修改
     *
     * @param entity 亦庄国投
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/10/15 16:10
     */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody InfoDeptYzgt entity) {
        validate(entity);
        service.saveOrUpdateYzgt(entity);
        return success(null);
    }

    /**
    * 参数校验
    *
    * @param entity 实体
    * @author JH
    * @date 2019/10/15 16:46
    */
    private void validate(InfoDeptYzgt entity) {
        if (entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("缺少参数").build();
        }else {
            if(entity.getId() != null) {
                InfoDeptYzgt old = service.getById(entity.getId());
                if (old == null) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                            .message("未获取到对象").build();
                }
                if (YesOrNoEnum.YES.getType()==old.getStatus()) {
                    throw UnityRuntimeException.newInstance()
                            .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                            .message("已提请发布状态下数据不可编辑").build();
                }
            }
        }
        //非空校验
        String result = ValidFieldUtil.checkEmptyStr(entity
                , ValidFieldFactory.emptyReg("行业类别不能为空! ", InfoDeptYzgt::getIndustryCategory)
                , ValidFieldFactory.emptyReg("企业规模不能为空! ", InfoDeptYzgt::getEnterpriseScale)
                , ValidFieldFactory.emptyReg("企业性质不能为空! ", InfoDeptYzgt::getEnterpriseNature)
                , ValidFieldFactory.emptyReg("企业名称不能为空! ", InfoDeptYzgt::getEnterpriseName)
                , ValidFieldFactory.emptyReg("企业简介不能为空! ", InfoDeptYzgt::getEnterpriseIntroduction)
                , ValidFieldFactory.emptyReg("联系人不能为空!   ", InfoDeptYzgt::getContactPerson)
                , ValidFieldFactory.emptyReg("联系方式不能为空! ", InfoDeptYzgt::getContactWay)
        );
        if (StringUtils.isNotEmpty(result)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message(result).build();
        }
        ValidFieldUtil.checkReg(entity
                ,ValidFieldFactory.lengthReg(1, 50, "企业名称不能超过50字符!", InfoDeptYzgt::getEnterpriseName)
                ,ValidFieldFactory.lengthReg(1, 20, "企业简介不能超过500字符!", InfoDeptYzgt::getEnterpriseIntroduction)
                ,ValidFieldFactory.lengthReg(1, 20, "联系人不能超过20字符!", InfoDeptYzgt::getContactPerson)
                ,ValidFieldFactory.lengthReg(1, 20, "联系方式不能超过50字符!", InfoDeptYzgt::getContactWay)
        );
        if(StringUtils.isNotEmpty(entity.getNotes())) {
            ValidFieldUtil.checkReg(entity, ValidFieldFactory.lengthReg(1, 500, "备注不能超过500字符!", InfoDeptYzgt::getNotes));
        }
    }


    /**
    * 详情接口
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/16 9:27
    */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody InfoDeptYzgt entity) {
        if(entity.getId() == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("id不能为空").build();
        }
        return success(service.detailById(entity.getId()));
    }



    /**
    * 查询条件封装
    *
    * @param entity 实体
    * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.InfoDeptYzgt>
    * @author JH
    * @date 2019/10/16 10:28
    */
    @SuppressWarnings("unchecked")
    private LambdaQueryWrapper<InfoDeptYzgt> wrapper(InfoDeptYzgt entity){
        LambdaQueryWrapper<InfoDeptYzgt> ew = new LambdaQueryWrapper<>();
        if(entity != null) {
            //企业名称
            if (StringUtils.isNotBlank(entity.getEnterpriseName())) {
                ew.like(InfoDeptYzgt::getEnterpriseName, entity.getEnterpriseName());
            }
            //行业类型
            if (entity.getIndustryCategory() != null) {
                ew.eq(InfoDeptYzgt::getIndustryCategory, entity.getIndustryCategory());
            }
            //企业规模
            if (entity.getEnterpriseScale() != null) {
                ew.eq(InfoDeptYzgt::getEnterpriseScale, entity.getEnterpriseScale());
            }
            //企业性质
            if (entity.getEnterpriseNature() != null) {
                ew.eq(InfoDeptYzgt::getEnterpriseNature, entity.getEnterpriseNature());
            }
            //创建时间
            if (StringUtils.isNotBlank(entity.getCreateTime())) {
                long begin = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), true);
                //gt 大于 lt 小于
                ew.gt(InfoDeptYzgt::getGmtCreate, begin);
                long end = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), false);
                ew.lt(InfoDeptYzgt::getGmtCreate, end);
            }
            //状态
            if (entity.getStatus() != null) {
                ew.eq(InfoDeptYzgt::getStatus, entity.getStatus());
            }
        }
        //排序规则      未提请发布在前，已提请发布在后；各自按创建时间倒序
        ew.orderByDesc(InfoDeptYzgt::getStatus,InfoDeptYzgt::getGmtCreate);
        return ew;
    }

    


    /**
    * 批量删除
    *
    * @param ids 主键集合
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/16 11:09
    */
    @RequestMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIds(@RequestBody List<Long> ids) {
        service.deleteByIds(ids);
        return success("删除成功");
    }

}

