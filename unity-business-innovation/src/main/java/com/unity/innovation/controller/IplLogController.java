
package com.unity.innovation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.client.RbacClient;
import com.unity.common.client.SystemClient;
import com.unity.common.constant.SafetyConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.entity.generated.IplLog;
import com.unity.innovation.service.IplLogServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;







/**
 * 创新发布清单-操作日志
 * @author zhang
 * 生成时间 2019-09-21 15:45:36
 */
@Controller
@RequestMapping("/ipllog")
public class IplLogController extends BaseWebController {
    @Autowired
    IplLogServiceImpl service;
    
    @Autowired
    RbacClient rbacClient;

    @Autowired
    SystemClient systemClient;
    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<IplLog> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity 创新发布清单-操作日志实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IplLog entity) {

        service.saveOrUpdate(entity);
        return success(SafetyConstant.SUCCESS);
    }
    

    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<IplLog> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2List(list)).build();
        return success(result);

    }

    /**
     * 查询条件转换
     * @param search 统一查询对象
     * @return
     */
    private LambdaQueryWrapper<IplLog> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<IplLog> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule(IplLog::getGmtCreate).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule(IplLog::getGmtModified).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(IplLog.class);

        }
        else{
            ew = new LambdaQueryWrapper<IplLog>();
        }

        ew.orderBy(true, false,IplLog::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplLog> list){

        return JsonUtil.<IplLog>ObjectToList(list,
                (m, entity) -> {
                   // adapterField(m, entity);
                }
                ,IplLog::getId,IplLog::getSort,IplLog::getNotes,IplLog::getProcessInfo,IplLog::getIdRbacDepartmentAssist,IplLog::getIdIplMain
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplLog ent){
        return JsonUtil.<IplLog>ObjectToMap(ent,
                (m, entity) -> {
                   // adapterField(m,entity);
                }
                ,IplLog::getId,IplLog::getIsDeleted,IplLog::getSort,IplLog::getNotes,IplLog::getProcessInfo,IplLog::getIdRbacDepartmentAssist,IplLog::getIdIplMain
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,IplLog entity){
        if(!StringUtils.isEmpty(entity.getCreator())) {
            if(entity.getCreator().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            }
            else {
                m.put("creator", entity.getCreator());
            }
        }
        if(!StringUtils.isEmpty(entity.getEditor())) {
            if(entity.getEditor().indexOf(ConstString.SEPARATOR_POINT)>-1) {
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            }
            else {
                m.put("editor", entity.getEditor());
            }
        }
        
        m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
        m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
    }
    
    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/del/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(ConstString.SPLIT_COMMA)));
        return success(SafetyConstant.SUCCESS);
    }


    /**
     * 更改排序
     * @param id
     * @param up 1 下降 0 上升
     * @return
     */
    @PostMapping("/changeOrder/{id}/{up}")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@PathVariable Integer id,@PathVariable Integer up){
        IplLog entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<IplLog> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(IplLog::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(IplLog::getSort);
        }
        else {
            wrapper.gt(IplLog::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(IplLog::getSort);
        }


        IplLog entity1 = service.getOne(wrapper);
     //   if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        IplLog entityA = new IplLog();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        IplLog entityB = new IplLog();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }
}

