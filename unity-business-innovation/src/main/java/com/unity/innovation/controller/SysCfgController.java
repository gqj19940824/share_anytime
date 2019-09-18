
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.client.RbacClient;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.ui.PageEntity;
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
    RbacClient rbacClient;

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

        LambdaQueryWrapper<SysCfg> ew = new LambdaQueryWrapper<>();
        SysCfg cfg = pageEntity.getEntity();
        if(StringUtils.isEmpty(cfg.getCfgType())) {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM,"缺少必要参数");
        }
        ew.eq(SysCfg::getCfgType,cfg.getCfgType());
        Long scope = cfg.getScope();
        if(scope != null) {
            ew.eq(SysCfg::getScope,scope);
        }
        ew.last("ORDER BY CONVERT(cfg_val USING gbk)");
        IPage<SysCfg> p = service.page(pageEntity.getPageable(),ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

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
    public Mono<ResponseEntity<SystemResponse<Object>>> save(@RequestBody SysCfg entity) {
        //参数校验
        if(entity.getUseStatus() == null) {
            entity.setUseStatus(YesOrNoEnum.YES.getType());
        }
        if(entity.getScope() == null) {
            entity.setScope(0L);
        }
        service.saveOrUpdate(entity);
        return success(null);
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
        return success(null);
    }
    

    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<SysCfg> list){
       
        return JsonUtil.ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,SysCfg::getId,SysCfg::getSort,SysCfg::getNotes,SysCfg::getCfgType,SysCfg::getCfgVal,SysCfg::getScope
        );
    }
    

    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,SysCfg entity){

    }
    


}

