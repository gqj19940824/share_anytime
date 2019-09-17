
package com.unity.resource.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.ContextHolder;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.client.RbacClient;
import com.unity.common.client.SystemClient;
import com.unity.common.constants.ConstString;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.resource.entity.ResourceType;
import com.unity.resource.service.ResourceTypeServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;






/**
 * 资源类型
 * @author creator
 * 生成时间 2019-01-25 13:46:29
 */
@Controller
@RequestMapping("/resourcetype")
public class ResourceTypeController extends BaseWebController {
    @Autowired
    ResourceTypeServiceImpl service;
    
    @Autowired
    RbacClient rbacClient;

    @Autowired
    SystemClient systemClient;



    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 资源类型id
     * @return 返回视图
     */
    @RequestMapping(value = "/view/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            ResourceType entity = service.getById(id);
            model.addAttribute("entity", JSON.toJSONString(entity));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "ResourceTypeEdit";
    }
    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<ResourceType> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity 资源类型实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody ResourceType entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }

    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<ResourceType> ew = wrapper(search);

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
    private LambdaQueryWrapper<ResourceType> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<ResourceType> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule("gmtCreate").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule("gmtModified").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(ResourceType.class);

        }
        else{
            ew = new LambdaQueryWrapper<ResourceType>();
        }

        ew.orderBy(true, false,ResourceType::getSort);
        
        return ew;
    }
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<ResourceType> list){
       
        return JsonUtil.<ResourceType>ObjectToList(list,
                new String[]{ "id","sort","notes","typeName","remark" }, 
                (m, entity) -> {
                    adapterField(m, entity);
                }
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(ResourceType ent){
        return JsonUtil.<ResourceType>ObjectToMap(ent,
                new String[]{ "id","sort","notes","typeName","remark" }, 
                (m, entity) -> {
                    adapterField(m,entity);
                }
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,ResourceType entity){
        if(!StringUtils.isEmpty(entity.getCreator())) {
            if(entity.getCreator().indexOf(ConstString.SEPARATOR_POINT)>1)
                m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
            else
                m.put("creator", entity.getCreator());
        }
        if(!StringUtils.isEmpty(entity.getEditor())) {
            if(entity.getEditor().indexOf(ConstString.SEPARATOR_POINT)>1)
                m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
            else
                m.put("editor", entity.getEditor());
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
        return success(null);
    }


    /**
     * 更改排序
     * @param id
     * @param up 1 下降 0 上升
     * @return
     */
    @PostMapping("/changeOrder/{id}/{up}")
    public Mono<ResponseEntity<SystemResponse<Object>>> changeOrder(@PathVariable Integer id,@PathVariable Integer up){
        ResourceType entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<ResourceType> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(ResourceType::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(ResourceType::getSort);
        }
        else {
            wrapper.gt(ResourceType::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(ResourceType::getSort);
        }


        ResourceType entity1 = service.getOne(wrapper);
        if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        ResourceType entityA = new ResourceType();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        ResourceType entityB = new ResourceType();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }
}

