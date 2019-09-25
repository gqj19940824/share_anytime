
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.client.RbacClient;
import com.unity.common.client.SystemClient;
import com.unity.common.exception.UnityRuntimeException;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.ui.tree.zTree;
import com.unity.common.ui.tree.zTreeStructure;
import com.unity.common.ui.SearchCondition;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.constants.ConstString;

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import com.unity.common.enums.FlagEnum;
import javax.servlet.http.HttpServletResponse;

import com.unity.innovation.service.IplAssistServiceImpl;
import com.unity.innovation.entity.generated.IplAssist;
import com.unity.innovation.enums.*;







/**
 * 创新发布清单-协同事项
 * @author zhang
 * 生成时间 2019-09-21 15:45:35
 */
@Controller
@RequestMapping("/iplassist")
public class IplAssistController extends BaseWebController {
    @Autowired
    IplAssistServiceImpl service;
    
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
    
        LambdaQueryWrapper<IplAssist> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity 创新发布清单-协同事项实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IplAssist entity) {
        
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
    
        LambdaQueryWrapper<IplAssist> ew = wrapper(search);

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
    private LambdaQueryWrapper<IplAssist> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<IplAssist> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule(IplAssist::getGmtCreate).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule(IplAssist::getGmtModified).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(IplAssist.class);

        }
        else{
            ew = new LambdaQueryWrapper<IplAssist>();
        }

        ew.orderBy(true, false,IplAssist::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplAssist> list){
       
        return JsonUtil.<IplAssist>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,IplAssist::getId,IplAssist::getSort,IplAssist::getNotes,IplAssist::getIdRbacDepartmentAssist,IplAssist::getInviteInfo,IplAssist::getIdRbacDepartmentDuty
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplAssist ent){
        return JsonUtil.<IplAssist>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,IplAssist::getId,IplAssist::getSort,IplAssist::getNotes,IplAssist::getIdRbacDepartmentAssist,IplAssist::getInviteInfo,IplAssist::getIdRbacDepartmentDuty
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,IplAssist entity){
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
        IplAssist entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<IplAssist> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(IplAssist::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(IplAssist::getSort);
        }
        else {
            wrapper.gt(IplAssist::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(IplAssist::getSort);
        }


        IplAssist entity1 = service.getOne(wrapper);
        if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        IplAssist entityA = new IplAssist();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        IplAssist entityB = new IplAssist();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }
}

