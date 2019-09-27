
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.enums.IplCategoryEnum;
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
import javax.annotation.Resource;
import com.unity.innovation.service.IplSupervisionMainServiceImpl;
import com.unity.innovation.entity.IplSupervisionMain;








/**
 * 创新发布清单-纪检组-主表
 * @author zhang
 * 生成时间 2019-09-23 15:34:07
 */
@RestController
@RequestMapping("/iplSuperVisionMain")
public class IplSupervisionMainController extends BaseWebController {

    @Resource
    IplSupervisionMainServiceImpl service;

     /**
     * 列表查询
     *
     * @param pageEntity 统一查询条件
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/9/26 13:58
     */
    @PostMapping("/listByPage")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody PageEntity<IplSupervisionMain> pageEntity) {
        Page<IplSupervisionMain> pageable = pageEntity.getPageable();
        IplSupervisionMain entity = pageEntity.getEntity();
        LambdaQueryWrapper<IplSupervisionMain> ew = wrapper(entity);

        IPage<IplSupervisionMain> p = service.page(pageable, ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    

         
     /**
     * 新增/修改
     *
     * @param entity 实体
     * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
     * @author JH
     * @date 2019/9/26 11:20
     */    
    @PostMapping("/saveOrUpdate")
    public Mono<ResponseEntity<SystemResponse<Object>>>  saveOrUpdate(@RequestBody IplSupervisionMain entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }
    


    /**
    * 查询条件转换
    *
    * @param entity 统一查询对象
    * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.unity.innovation.entity.IplSupervisionMain>
    * @author JH
    * @date 2019/9/26 13:54
    */
    private LambdaQueryWrapper<IplSupervisionMain> wrapper(IplSupervisionMain entity){
        LambdaQueryWrapper<IplSupervisionMain> ew = new LambdaQueryWrapper<>();
        if(entity.getCategory() != null) {
            ew.eq(IplSupervisionMain::getCategory,entity.getCategory());
        }
        //创建时间
        if (StringUtils.isNotBlank(entity.getCreateTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(entity.getCreateTime(), false);
            //gt 大于 lt 小于
            ew.lt(IplSupervisionMain::getGmtCreate, end);
            ew.gt(IplSupervisionMain::getGmtCreate, begin);
        }
        if(entity.getDescription() != null) {
            ew.like(IplSupervisionMain::getDescription,entity.getDescription());
        }
        return ew;
    }


     /**
     * 将实体列表 转换为List Map
     *
     * @param list 实体列表
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author JH
     * @date 2019/9/26 14:19
     */
     @SuppressWarnings("unchecked")
    private List<Map<String, Object>> convert2List(List<IplSupervisionMain> list){
       
        return JsonUtil.ObjectToList(list,
                (m, entity) -> m.put("categoryName", IplCategoryEnum.ofName(entity.getCategory()))
                ,IplSupervisionMain::getId,IplSupervisionMain::getCategory,IplSupervisionMain::getDescription
        );
    }
    

    /**
    * 删除
    *
    * @param ids id集合
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/26 11:34
    */
    @PostMapping("/removeByIds")
    public Mono<ResponseEntity<SystemResponse<Object>>> removeByIds(@RequestBody List<Long> ids) {
        if(CollectionUtils.isNotEmpty(ids)) {
            service.removeByIds(ids);
            return success(null);
        }else {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少id");
        }
    }

    /**
    * 详情接口
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/9/26 14:26
    */
    @PostMapping("/detailById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailById(@RequestBody IplSupervisionMain entity) {
        if(entity == null || entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少id");
        }
        return success(service.getById(entity.getId()));
    }

}

