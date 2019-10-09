
package com.unity.system.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.client.RbacClient;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.ui.tree.zTree;
import com.unity.common.ui.tree.zTreeStructure;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.system.entity.DictionaryItem;
import com.unity.system.service.DictionaryItemServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 数据字典项
 * @author creator
 * 生成时间 2018-12-24 19:52:55
 */
@Controller
@RequestMapping("/dictionaryitem")
public class DictionaryItemController extends BaseWebController {
    @Autowired
    DictionaryItemServiceImpl service;
    @Autowired
    RbacClient rbacClient;


    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 数据字典项id
     * @param idParent 父id
     * @param gradationCode 父集次编码
     * @return 返回视图
     */
    @RequestMapping(value = "/editEntrance/{iframe}/{idSysDictionary}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id,String idParent,String gradationCode,@PathVariable("idSysDictionary") String idSysDictionary) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        model.addAttribute("idParent", StringUtils.isEmpty(idParent)?"":idParent);
        model.addAttribute("gradationCode", StringUtils.isEmpty(gradationCode)?"":gradationCode);
        //传入 编号_数据字典
        model.addAttribute("idSysDictionary",idSysDictionary);
        if(id!=null){
            DictionaryItem entity = service.getById(id);
            model.addAttribute("entity", JSON.toJSONString(entity));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "DictionaryItemEdit";
    }


    /**
     * 按id获取字典项数据
     * @param entity 数据字典项
     */
    @PostMapping(value = "/getByEntity")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> getByEntity(@RequestBody DictionaryItem entity) {

        if(entity != null && entity.getId() != null){
            entity = service.getById(entity.getId());
            return success(entity);
        }else {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "查询失败");
        }
    }




     /**
     * 获取树数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/tree")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> tree(@RequestBody SearchElementGrid search,String selectList) {

        QueryWrapper<DictionaryItem> ew = wrapper(search);
        List checkedList =new ArrayList();
        List<DictionaryItem> list = service.list(ew);
        if(!StringUtils.isEmpty(selectList)){
            checkedList = Arrays.asList(selectList.split(","));
        }
        zTreeStructure structure = zTreeStructure.newInstance()
                .idField("id")
                .textField("name")
                .parentField("idParent")
                .kidField("gradationCode,level")
                .checkedList(checkedList)
                .build();
        return success(zTree.getTree(list,structure));

    }
    
         /**
     * 添加或修改
     * @param entity 数据字典项实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody DictionaryItem entity) {
    
         if(entity.getId()==null){
            LambdaQueryWrapper<DictionaryItem> ew = new LambdaQueryWrapper<DictionaryItem>();
            String gradationCode = "";
            if(entity.getIdParent()==null){
                ew.isNull(DictionaryItem::getIdParent);
            }
            else {
                ew.eq(DictionaryItem::getIdParent,entity.getIdParent());
                gradationCode = entity.getGradationCode();
            }
            int max = service.count(ew) + 1;
            entity.setGradationCode(gradationCode+"."+max);
            entity.setLevel(entity.getGradationCode().split(ConstString.SPLIT_POINT).length-2);
        }
        service.saveOrUpdate(entity);
        return success("操作成功");

    }

    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {
    
        QueryWrapper<DictionaryItem> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2Map(list)).build();
        return success(result);

    }

    private QueryWrapper<DictionaryItem> wrapper(SearchElementGrid search){
        QueryWrapper<DictionaryItem> ew = null;
        if(search!=null){
            if(search.getCond()!=null){
                search.getCond().findRule("gmtCreate").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
                search.getCond().findRule("gmtModified").forEach(r->{
                   r.setData(DateUtils.parseDate(r.getData()).getTime());
                });
            }
            ew = search.toEntityWrapper(DictionaryItem.class);

        }
        else{
            ew = new QueryWrapper<DictionaryItem>();
        }

        ew.lambda().orderBy(true, false,DictionaryItem::getSort);
        
        return ew;
    }
    
    private List<Map<String, Object>> convert2Map(List<DictionaryItem> list){
        return JsonUtil.<DictionaryItem>ObjectToList(list,
                    new String[]{ "id","idSysDictionary","isDeleted","sort","notes","creator","editor","name","gradationCode","level","idParent" }, 
                        (m, entity) -> {
                                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                                    m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
                        }
             );
    }
    
    
    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(",")));
        return success(InnovationConstant.SUCCESS);
    }

    /**
     *
     * @param list id集合
     * @return
     */
    @ResponseBody
    @PostMapping ("delete")
    public Mono<ResponseEntity<SystemResponse<Object>>>  delete(@RequestBody List<Integer> list) {
        if(list != null && list.size() > 0 ){
            service.removeByIds(list);
            return success("删除成功");
        }else {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "删除失败");
        }
    }


}

