
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.entity.generated.IplManageMain;
import com.unity.innovation.enums.IplCategoryEnum;
import com.unity.innovation.enums.WorkStatusAuditingStatusEnum;
import com.unity.innovation.service.IplManageMainServiceImpl;
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
import java.util.Objects;
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

    @Resource
    private IplManageMainServiceImpl iplManageMainService;

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
        LambdaQueryWrapper<IplSupervisionMain> ew = service.wrapper(entity);

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
                ,IplSupervisionMain::getId,IplSupervisionMain::getCategory,IplSupervisionMain::getDescription,IplSupervisionMain::getGmtCreate
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

    //------------------------------------------------我是一个分割线

    /**
    * 一次打包新增/修改
    *
    * @param entity 清单发布管理实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/8 14:52
    */
    @PostMapping("/saveOrUpdateIplManageMain")
    public Mono<ResponseEntity<SystemResponse<Object>>>  saveOrUpdateIplManageMain(@RequestBody IplManageMain entity) {
        validateIplManageMain(entity);
        service.saveOrUpdateIplManageMain(entity);
        return success(null);
    }

    /**
    * 参数校验
    *
    * @param entity 实体
    * @author JH
    * @date 2019/10/9 10:21
    */
    private void validateIplManageMain(IplManageMain entity) {

        if(StringUtils.isBlank(entity.getTitle())) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "标题不能为空");
        }
        if(CollectionUtils.isEmpty(entity.getSupervisionMainList())) {
            throw new UnityRuntimeException(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "内容不能为空");
        }
        //新增
        if(entity.getId() == null) {
            if(CollectionUtils.isEmpty(entity.getAttachments()) && YesOrNoEnum.YES.getType() == entity.getIsCommit()) {
                throw new UnityRuntimeException(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "只有上传附件才可以提交");
            }
        //编辑
        }else {
            IplManageMain old = iplManageMainService.getById(entity.getId());
            if(old == null) {
                throw new UnityRuntimeException(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "数据不存在");
            }else {
                //只有待提交、已驳回可以提交
                if(YesOrNoEnum.YES.getType() == entity.getIsCommit() && !WorkStatusAuditingStatusEnum.FORTY.getId().equals(old.getStatus()) && !WorkStatusAuditingStatusEnum.TEN.getId().equals(old.getStatus())) {
                    throw new UnityRuntimeException(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR, "只有待提交、已驳回可以提交");
                }
            }
        }
    }


    /**
     * 清亲政商关系清单发布管理-纪检组列表查询
     *
     * @param pageEntity 分页条件
     * @return 分页数据
     * @author JH
     * @date 2019/10/9 15:13
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/listByPageIplManageMain")
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPageIplManageMain(@RequestBody PageEntity<IplManageMain> pageEntity) {
        Page<IplManageMain> pageable = pageEntity.getPageable();
        IplManageMain entity = pageEntity.getEntity();
        LambdaQueryWrapper<IplManageMain> ew = wrapperIplManageMain(entity);

        IPage<IplManageMain> p = service.pageIplManageMain(pageable, ew);
        List<IplManageMain> list = p.getRecords();
        list.forEach(n -> n.setStatusName(Objects.requireNonNull(WorkStatusAuditingStatusEnum.of(n.getStatus())).getName()));
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(JsonUtil.ObjectToList(list,
                       null
                        ,IplManageMain::getId,IplManageMain::getStatus,IplManageMain::getStatusName,IplManageMain::getGmtCreate
                )).build();
        return success(result);

    }


    /**
    * 查询条件封装
    *
    * @param entity 分页条件
    * @return 封装后的wrapper
    * @author JH
    * @date 2019/10/9 15:13
    */
    @SuppressWarnings("unchecked")
    private LambdaQueryWrapper<IplManageMain> wrapperIplManageMain(IplManageMain entity){
        LambdaQueryWrapper<IplManageMain> ew = new LambdaQueryWrapper<>();
        //只查询纪检组
        ew.eq(IplManageMain::getIdRbacDepartmentDuty, InnovationConstant.DEPARTMENT_JJ_ID);
        if(entity.getStatus() != null) {
            ew.eq(IplManageMain::getStatus,entity.getStatus());
        }
        //提交时间
        if (StringUtils.isNotBlank(entity.getSubmitTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(entity.getSubmitTime(), false);
            //gt 大于 lt 小于
            ew.lt(IplManageMain::getGmtSubmit, end);
            ew.gt(IplManageMain::getGmtSubmit, begin);
        }
        ew.orderByDesc(IplManageMain::getGmtSubmit,IplManageMain::getGmtModified);
        return ew;
    }


    /**
    * 详情接口
    *
    * @param entity 包含主键的实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/10 10:56
    */
    @PostMapping("/detailIplManageMainById")
    public Mono<ResponseEntity<SystemResponse<Object>>> detailIplManageMainById(@RequestBody IplManageMain entity) {
        if(entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少id");
        }
        return success(service.detailIplManageMainById(entity.getId()));
    }

    /**
    * 返回可选择的基础数据以及已选择的数据
    *
    * @param entity 查询条件
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/10 13:39
    */
    @PostMapping("/listSupervisionToAdd")
    public Mono<ResponseEntity<SystemResponse<Object>>> listSupervisionToAdd(@RequestBody IplSupervisionMain entity) {
        if(entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少id");
        }
        return success(service.listSupervisionToAdd(entity));
    }

    /**
    * 清亲政商关系清单发布管理-纪检组 删除接口
    *
    * @param entity 实体
    * @return reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<com.unity.common.pojos.SystemResponse<java.lang.Object>>>
    * @author JH
    * @date 2019/10/10 13:50
    */
    @PostMapping("/removeIplManageMainById")
    public Mono<ResponseEntity<SystemResponse<Object>>>  removeIplManageMainById(@RequestBody IplManageMain entity) {
        if(entity.getId() == null) {
            return error(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR,"缺少id");
        }
        service.removeIplManageMainById(entity.getId());
        return success();
    }


}

