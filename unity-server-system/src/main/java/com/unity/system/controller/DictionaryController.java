
package com.unity.system.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.client.RbacClient;
import com.unity.common.constant.InnovationConstant;
import com.unity.common.constants.ConstString;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.system.entity.Dictionary;
import com.unity.system.service.DictionaryItemServiceImpl;
import com.unity.system.service.DictionaryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * 数据字典
 * @author creator
 * 生成时间 2018-12-21 13:25:58
 */
@Controller
@RequestMapping("/dictionary")
public class DictionaryController extends BaseWebController {
    @Autowired
    DictionaryServiceImpl service;
    @Autowired
    DictionaryItemServiceImpl itemService;
    @Autowired
    RbacClient rbacClient;


    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 数据字典id
     * @return 返回视图
     */
    @RequestMapping(value = "/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            Dictionary entity = service.getById(id);
            model.addAttribute("entity", JSON.toJSONString(entity));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "DictionaryEdit";
    }

    /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        QueryWrapper<Dictionary> ew = wrapper(search);

        IPage p = service.page(search.getPageable(), ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(p.getTotal())
                .items(convert2Map(p.getRecords())).build();
        return success(result);

    }
    
     /**
     * 获取数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> list(@RequestBody SearchElementGrid search) {

        QueryWrapper<Dictionary> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2Map(list)).build();
        return success(result);

    }

    private QueryWrapper<Dictionary> wrapper(SearchElementGrid search){
        search.getCond().findRule("gmtCreate").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });
        search.getCond().findRule("gmtModified").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });
    
        QueryWrapper<Dictionary> ew = search.toEntityWrapper(Dictionary.class);

        ew.lambda().orderBy(true, false,Dictionary::getSort);
        
        return ew;
    }
    
    private List<Map<String, Object>> convert2Map(List<Dictionary> list){
        return JsonUtil.<Dictionary>ObjectToList(list,
                    new String[]{ "id","isDeleted","iSort","notes","creator","editor","name" }, 
                        (m, entity) -> {
                                    if(!StringUtils.isEmpty(entity.getCreator())) {
                                        if(entity.getCreator().indexOf(".")>1)
                                            m.put("creator", entity.getCreator().split(ConstString.SPLIT_POINT)[1]);
                                        else
                                            m.put("creator", entity.getCreator());
                                    }
                                    if(!StringUtils.isEmpty(entity.getEditor())) {
                                        if(entity.getEditor().indexOf(".")>1)
                                            m.put("editor", entity.getEditor().split(ConstString.SPLIT_POINT)[1]);
                                        else
                                            m.put("editor", entity.getEditor());
                                    }
                                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                                    m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
                        }
             );
    }

     /**
     * 添加或修改
     * @param entity 数据字典实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody Dictionary entity) {
        if(entity != null){
            service.saveOrUpdate(entity);
            return success("操作成功");
        }else {
            return error(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM, "操作失败");
        }

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

