
package com.unity.system.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.client.RbacClient;
import com.unity.common.constant.SafetyConstant;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchCondition;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.ui.excel.ExcelEntity;
import com.unity.common.ui.excel.ExportEntity;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.common.util.RedisUtils;
import com.unity.system.entity.Cfg;
import com.unity.system.service.CfgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 系统配置
 * @author creator
 * 生成时间 2018-12-21 13:25:57
 */
@Controller
@RequestMapping("/cfg")
public class CfgController extends BaseWebController {
    @Autowired
    CfgServiceImpl service;

    @Autowired
    RbacClient rbacClient;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 系统配置id
     * @return 返回视图
     */
    @RequestMapping(value = "/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            Cfg entity = service.getById(id);
            model.addAttribute("entity", JSON.toJSONString(entity));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "CfgEdit";
    }

    /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        QueryWrapper<Cfg> ew = wrapper(search);

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
    
        QueryWrapper<Cfg> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2Map(list)).build();
        return success(result);

    }

    private QueryWrapper<Cfg> wrapper(SearchElementGrid search){
        search.getCond().findRule("gmtCreate").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });
        search.getCond().findRule("gmtModified").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });
    
        QueryWrapper<Cfg> ew = search.toEntityWrapper(Cfg.class);

        ew.lambda().orderBy(true, false,Cfg::getSort);
        
        return ew;
    }
    
    private List<Map<String, Object>> convert2Map(List<Cfg> list){
        return JsonUtil.<Cfg>ObjectToList(list,
                    new String[]{ "id","isDeleted","iSort","notes","creator","editor","cfgType","cfgVal" }, 
                        (m, entity) -> {
                                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                                    m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
                        }
             );
    }
    
     /**
     * 添加或修改
     * @param entity 系统配置实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody Cfg entity) {

        if(entity.getId()==null){
            LambdaQueryWrapper<Cfg> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Cfg::getCfgType,entity.getCfgType());
            int count = service.count(wrapper);
            if(count>0) throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                    .message("新增的配置已存在").build();
        }else{
            LambdaQueryWrapper<Cfg> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Cfg::getCfgType,entity.getCfgType());
            wrapper.ne(Cfg::getId,entity.getId());
            int count = service.count(wrapper);
            if(count>0) throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.MODIFY_DATA_ALREADY_EXISTS)
                    .message("修改的配置已存在").build();
        }
        redisUtils.valueOperationSet("CfgType"+entity.getCfgType(), entity.getCfgVal(), 3600 * 24 * 8L);
        service.saveOrUpdate(entity);
        return success("操作成功");
    }
    
    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        LambdaQueryWrapper<Cfg> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Cfg::getId,ids.split(","));
        List<Cfg> list = service.list(wrapper);
        List<String> cfgCollect = list.stream().map(item -> item.getCfgType()).collect(Collectors.toList());
        redisTemplate.delete(cfgCollect);

        service.removeByIds(ConvertUtil.arrString2Long(ids.split(",")));
        return success(SafetyConstant.SUCCESS);
    }

    /**
     * 批量删除
     * @param ids id集合
     * @return
     */
    @ResponseBody
    @PostMapping("/delete")
    public Mono<ResponseEntity<SystemResponse<Object>>>  delete(@RequestBody List<Integer> ids) {
        LambdaQueryWrapper<Cfg> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Cfg::getId,ids);
        List<Cfg> list = service.list(wrapper);
        List<String> cfgCollect = list.stream().map(item -> item.getCfgType()).collect(Collectors.toList());
        redisTemplate.delete(cfgCollect);
        service.removeByIds(ids);
        return success(SafetyConstant.SUCCESS);
    }





    @RequestMapping({"/export/excel"})
    public void exportExcel(HttpServletResponse res,String cond) {
        String fileName="系统配置";
        ExportEntity<Cfg> excel = ExcelEntity.exportEntity(res);

        try {
            SearchElementGrid search = new SearchElementGrid();
            search.setCond(JSON.parseObject(cond, SearchCondition.class));
            QueryWrapper<Cfg> ew = wrapper(search);
            List<Cfg> list = service.list(ew);

            excel.addColumn(Cfg::getCfgVal,"值")
                    .addColumn(Cfg::getCfgType,"类型")
                    .addColumn(Cfg::getGmtModified,"修改时间")
                    .addColumn(Cfg::getEditor,"修改人")
                    .export(fileName,convert2Map(list));
        }
        catch (Exception ex){
//            excel.exportError(fileName,ex);
        }


    }

    @RequestMapping({"/inport/excel"})
    public void inportExcel(){

    }


}

