
package com.unity.system.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.client.RbacClient;
import com.unity.common.constant.SafetyConstant;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageElementGrid;
import com.unity.common.ui.SearchElementGrid;
import com.unity.common.util.ConvertUtil;
import com.unity.common.util.DateUtils;
import com.unity.common.util.JsonUtil;
import com.unity.system.entity.Serial;
import com.unity.system.service.SerialServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * 流水号
 * @author creator
 * 生成时间 2018-12-20 17:12:38
 */
@Controller
@RequestMapping("/serial")
public class SerialController extends BaseWebController {
    @Autowired
    SerialServiceImpl service;
    @Autowired
    RbacClient rbacClient;


    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id 流水号id
     * @return 返回视图
     */
    @RequestMapping(value = "/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            Serial entity = service.getById(id);
            model.addAttribute("entity", JSON.toJSONString(entity));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "SerialEdit";
    }

    /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {

        QueryWrapper<Serial> ew = wrapper(search);

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

        QueryWrapper<Serial> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2Map(list)).build();
        return success(result);

    }

    private QueryWrapper<Serial> wrapper(SearchElementGrid search){
        search.getCond().findRule("gmtCreate").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });
        search.getCond().findRule("gmtModified").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });

        QueryWrapper<Serial> ew = search.toEntityWrapper(Serial.class);

        ew.lambda().orderBy(true, false,Serial::getSort);

        return ew;
    }

    private List<Map<String, Object>> convert2Map(List<Serial> list){
        return JsonUtil.<Serial>ObjectToList(list,
                    new String[]{ "id","isDeleted","iSort","notes","creator","editor","serialPrefix","serialSuffixes","serialType","serialVal","serialCode" },
                        (m, entity) -> {
                                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                                    m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));
                        }
             );
    }

     /**
     * 添加或修改
     * @param entity 流水号实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody Serial entity) {
        service.saveOrUpdate(entity);
        return success(SafetyConstant.SUCCESS);
    }

    /**
     * 批量删除
     * @param ids id列表用英文逗号分隔
     * @return
     */
    @DeleteMapping("/{ids}")
    public Mono<ResponseEntity<SystemResponse<Object>>>  del(@PathVariable("ids") String ids) {
        service.removeByIds(ConvertUtil.arrString2Long(ids.split(",")));
        return success(SafetyConstant.SUCCESS);
    }


}

