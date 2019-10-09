
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.ui.PageEntity;
import com.unity.common.util.ValidFieldFactory;
import com.unity.common.util.ValidFieldUtil;
import com.unity.innovation.entity.SysCfgScope;
import com.unity.innovation.service.SysCfgScopeServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.util.JsonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import com.unity.innovation.service.SysCfgServiceImpl;
import com.unity.innovation.entity.SysCfg;

/**
 * 系统配置
 * @author zhang
 * 生成时间 2019-09-17 14:53:55
 */
@RestController
@RequestMapping("/sysCfg")
public class SysCfgController extends BaseWebController {

    @Resource
    SysCfgServiceImpl service;
    @Resource
    SysCfgScopeServiceImpl scopeService;


     /**
     * 分页查询
     *
     * @param pageEntity 统一查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/9/17 16:17
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<SysCfg> pageEntity) {

        SysCfg cfg = pageEntity.getEntity();
        LambdaQueryWrapper<SysCfg> ew = wrapper(cfg);
        IPage<SysCfg> p = service.page(pageEntity.getPageable(),ew);
        List<SysCfg> list = p.getRecords();
        //id集合
        List<Long> cfgIds = list.stream().map(SysCfg::getId).collect(Collectors.toList());
        //按主表id分组
        if(CollectionUtils.isNotEmpty(cfgIds)) {
            List<SysCfgScope> scopeList = scopeService.list(new LambdaQueryWrapper<SysCfgScope>().in(SysCfgScope::getIdSysCfg, cfgIds));
            scopeList.forEach(n -> {
                if(n.getIdRbacDepartment() == 0) {
                    n.setDepartmentName("共用");
                } else {
                    n.setDepartmentName(InnovationUtil.getDeptNameById(n.getIdRbacDepartment()));
                }
            });
            Map<Long, List<SysCfgScope>> map = scopeList.stream().collect(Collectors.groupingBy(SysCfgScope::getIdSysCfg));
            list.forEach( n ->
                    n.setScopeList(map.get(n.getId()))
            );
        }
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(list)).build();
        return success(result);

    }

    /**
    * 查询条件封装
    *
    * @param cfg 入参
    * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.SysCfg>
    * @author JH
    * @date 2019/9/23 9:51
    */
    private LambdaQueryWrapper<SysCfg> wrapper(SysCfg cfg) {
        LambdaQueryWrapper<SysCfg> ew = new LambdaQueryWrapper<>();
        if(cfg == null) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"缺少必要参数");
        }
        if(cfg.getCfgType() == null) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"缺少必要参数");
        }
        ew.eq(SysCfg::getCfgType,cfg.getCfgType());
        if(cfg.getUseStatus() != null) {
            ew.eq(SysCfg::getUseStatus,cfg.getUseStatus());
        }
        if(StringUtils.isNotBlank(cfg.getCfgVal())) {
            ew.like(SysCfg::getCfgVal,cfg.getCfgVal());
        }
        ew.orderByDesc(SysCfg::getUseStatus);
        ew.last(", CONVERT(cfg_val USING gbk)");
        return ew;
    }
    

    /**
    * 添加/修改
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/17 15:58
    */
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>> saveOrUpdate(@RequestBody SysCfg entity) {
        //参数校验
        validate(entity);
        service.saveOrUpdateSysCfg(entity);
        return success();
    }

    /**
    * 参数校验
    *
    * @param entity 待校验参数
    * @author JH
    * @date 2019/9/18 10:57
    */
    private void validate(SysCfg entity) {
        if(entity == null) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("缺少参数").build();
        }
        //非空校验
        String result = ValidFieldUtil.checkEmptyStr(entity
                , ValidFieldFactory.emptyReg("模块类别不能为空! ",SysCfg::getCfgType)
                , ValidFieldFactory.emptyReg("类别名称不能为空! ",SysCfg::getCfgVal)
                , ValidFieldFactory.emptyReg("启用状态不能为空! ",SysCfg::getUseStatus)
                , ValidFieldFactory.emptyReg("适用范围不能为空! ",SysCfg::getScope));

        if(StringUtils.isNotEmpty(result)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message(result).build();
        }
        //类别名称去空格
        entity.setCfgVal(entity.getCfgVal().trim());
        ValidFieldUtil.checkReg(entity,ValidFieldFactory.lengthReg(1,20,"类别名称不能超过20字符!",SysCfg::getCfgVal));
        List<SysCfg> list;
        //新增
        if(entity.getId() == null) {
            list = service.list(new LambdaUpdateWrapper<SysCfg>().eq(SysCfg::getCfgVal, entity.getCfgVal()).eq(SysCfg::getCfgType,entity.getCfgType()));

        } else {
            list = service.list(new LambdaUpdateWrapper<SysCfg>().eq(SysCfg::getCfgVal, entity.getCfgVal()).eq(SysCfg::getCfgType,entity.getCfgType()).ne(SysCfg::getId,entity.getId()));
        }
        if(CollectionUtils.isNotEmpty(list)) {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR)
                    .message("名称已存在!").build();
        }
    }

    /**
    * 修改禁用/启用状态
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/17 16:01
    */
    @PostMapping("/changeUseStatus")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeUseStatus(@RequestBody SysCfg entity) {
        if(entity.getId() == null || entity.getUseStatus() == null) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"缺少必要参数");
        }
        service.updateById(entity);
        return success(InnovationConstant.SUCCESS);
    }

     /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author JH
     * @date 2019/9/18 14:53
     */
     @SuppressWarnings("unchecked")
    private List<Map<String, Object>> convert2List(List<SysCfg> list){
       
        return JsonUtil.ObjectToList(list,
            null
                ,SysCfg::getId,SysCfg::getCfgType,SysCfg::getCfgVal,SysCfg::getScopeList,SysCfg::getUseStatus
        );
    }


}

