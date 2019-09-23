
package com.unity.innovation.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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


import com.unity.innovation.client.SystemClient;
import com.unity.innovation.client.RbacClient;
import com.unity.innovation.service.IplManageMainServiceImpl;
import com.unity.innovation.entity.IplManageMain;
import com.unity.innovation.enums.*;







/**
 * 创新发布清单-发布管理主表
 * @author zhang
 * 生成时间 2019-09-21 15:45:37
 */
@Controller
@RequestMapping("/iplmanagemain")
public class IplManageMainController extends BaseWebController {
    @Autowired
    IplManageMainServiceImpl service;
    
    @Autowired
    RbacClient rbacClient;

    @Autowired
    SystemClient systemClient;

    /**
     * 模块入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @return 返回视图
     */
    @RequestMapping("/view/moduleEntrance/{iframe}")
    public String moduleEntrance(Model model,@PathVariable("iframe") String iframe) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("button", JSON.toJSONString(rbacClient.getMenuButton(iframe)));
        return "IplManageMainList";
    }

    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 创新发布清单-发布管理主表id
     * @return 返回视图
     */
    @RequestMapping(value = "/view/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            IplManageMain entity = service.getById(id);
            if(entity==null) model.addAttribute("entity", "{}");
            else model.addAttribute("entity", JSON.toJSONString(convert2Map(entity)));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "IplManageMainEdit";
    }
    
     /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<IplManageMain> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2List(p.getRecords())).build();
        return success(result);

    }
    
         /**
     * 添加或修改
     * @param entity 创新发布清单-发布管理主表实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody IplManageMain entity) {
        
        service.saveOrUpdate(entity);
        return success(null);
    }
    
    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletResponse res,String cond) {
        String fileName="创新发布清单-发布管理主表";
        ExportEntity<IplManageMain> excel =  ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            LambdaQueryWrapper<IplManageMain> ew = wrapper(search);
            List<IplManageMain> list = service.list(ew);
     
            excel
                .addColumn(IplManageMain::getId,"编号")
                .addColumn(IplManageMain::getIdIpaManageMainIplManageMain,"编号_创新发布活动-活动管理一对多发布管理表")
                .addColumn(IplManageMain::getNotes,"备注")
                .addColumn(IplManageMain::getEditor,"修改人")
                .addColumn(IplManageMain::getTitle,"标题")
                .addColumn(IplManageMain::getStatus,"状态")
                .addColumn(IplManageMain::getAttachmentCode,"附件")
                .addColumn(IplManageMain::getIdRbacDepartment,"单位id")
                .addColumn(IplManageMain::getPublishResult,"发布结果")
                 .export(fileName,convert2List(list));
        }
        catch (Exception ex){
            excel.exportError(fileName,ex);
        }
    }

    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {
    
        LambdaQueryWrapper<IplManageMain> ew = wrapper(search);

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
    private LambdaQueryWrapper<IplManageMain> wrapper(SearchElementGrid search){
        LambdaQueryWrapper<IplManageMain> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule(IplManageMain::getGmtCreate).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule(IplManageMain::getGmtModified).forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityLambdaWrapper(IplManageMain.class);

        }
        else{
            ew = new LambdaQueryWrapper<IplManageMain>();
        }

        ew.orderBy(true, false,IplManageMain::getSort);
        
        return ew;
    }
    
    
    
     /**
     * 将实体列表 转换为List Map
     * @param list 实体列表
     * @return
     */
    private List<Map<String, Object>> convert2List(List<IplManageMain> list){
       
        return JsonUtil.<IplManageMain>ObjectToList(list,
                (m, entity) -> {
                    adapterField(m, entity);
                }
                ,IplManageMain::getId,IplManageMain::getIdIpaManageMainIplManageMain,IplManageMain::getSort,IplManageMain::getNotes,IplManageMain::getTitle,IplManageMain::getStatus,IplManageMain::getAttachmentCode,IplManageMain::getIdRbacDepartment,IplManageMain::getPublishResult
        );
    }
    
     /**
     * 将实体 转换为 Map
     * @param ent 实体
     * @return
     */
    private Map<String, Object> convert2Map(IplManageMain ent){
        return JsonUtil.<IplManageMain>ObjectToMap(ent,
                (m, entity) -> {
                    adapterField(m,entity);
                }
                ,IplManageMain::getId,IplManageMain::getIdIpaManageMainIplManageMain,IplManageMain::getIsDeleted,IplManageMain::getSort,IplManageMain::getNotes,IplManageMain::getTitle,IplManageMain::getStatus,IplManageMain::getAttachmentCode,IplManageMain::getIdRbacDepartment,IplManageMain::getPublishResult
        );
    }
    
    /**
     * 字段适配
     * @param m 适配的结果
     * @param entity 需要适配的实体
     */
    private void adapterField(Map<String, Object> m,IplManageMain entity){
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
        IplManageMain entity = service.getById(id);
        Long sort = entity.getSort();
        LambdaQueryWrapper<IplManageMain> wrapper = new LambdaQueryWrapper();

        String msg ="";
        if(up==1) {
            wrapper.lt(IplManageMain::getSort, sort);
            msg ="已经是最后一条数据";
            wrapper.orderByDesc(IplManageMain::getSort);
        }
        else {
            wrapper.gt(IplManageMain::getSort, sort);
            msg ="已经是第一条数据";
            wrapper.orderByAsc(IplManageMain::getSort);
        }


        IplManageMain entity1 = service.getOne(wrapper);
        if(entity1==null) throw new UnityRuntimeException(msg);

        entity.setSort(entity1.getSort());

        IplManageMain entityA = new IplManageMain();
        entityA.setId(entity.getId());
        entityA.setSort(entity1.getSort());
        service.updateById(entityA);

        IplManageMain entityB = new IplManageMain();
        entityB.setId(entity1.getId());
        entityB.setSort(sort);
        service.updateById(entityB);

        return success("移动成功");
    }
}

