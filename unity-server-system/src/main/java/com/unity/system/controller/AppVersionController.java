
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
import com.unity.system.entity.AppVersion;
import com.unity.system.service.AppVersionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * app版本
 * @author creator
 * 生成时间 2018-12-21 13:25:56
 */
@Controller
@RequestMapping("/appversion")
public class AppVersionController extends BaseWebController {
    @Autowired
    AppVersionServiceImpl service;

    @Autowired
    RbacClient rbacClient;



    /**
     * 添加或修改表达入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @param id app版本id
     * @return 返回视图
     */
    @RequestMapping(value = "/editEntrance/{iframe}")
    public String editEntrance(Model model,@PathVariable("iframe") String iframe,String id) {
        model.addAttribute("iframe", iframe);
        model.addAttribute("id", id);
        if(id!=null){
            AppVersion entity = service.getById(id);
            model.addAttribute("entity", JSON.toJSONString(entity));
        }
        else{
            model.addAttribute("entity", "{}");
        }
        return "AppVersionEdit";
    }

    /**
     * 获取一页数据
     * @param search 统一查询条件
     * @return
     */
    @PostMapping("/listByPage")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>> listByPage(@RequestBody SearchElementGrid search) {
    
        QueryWrapper<AppVersion> ew = wrapper(search);

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
    
        QueryWrapper<AppVersion> ew = wrapper(search);

        List list = service.list(ew);
        PageElementGrid result = PageElementGrid.<Map<String,Object>>newInstance()
                .total(Long.valueOf(list.size()))
                .items(convert2Map(list)).build();
        return success(result);

    }

    private QueryWrapper<AppVersion> wrapper(SearchElementGrid search){
        search.getCond().findRule("gmtCreate").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });
        search.getCond().findRule("gmtModified").forEach(r->{
           r.setData(DateUtils.parseDate(r.getData()).getTime());
        });
    
        QueryWrapper<AppVersion> ew = search.toEntityWrapper(AppVersion.class);

        ew.lambda().orderBy(true, false,AppVersion::getSort);
        
        return ew;
    }
    
    private List<Map<String, Object>> convert2Map(List<AppVersion> list){
        return JsonUtil.<AppVersion>ObjectToList(list,
                    new String[]{ "id","isDeleted","iSort","notes","creator","editor","packName","name","version","versionInside","isMustUpgrade","systemType","downloadPath","sqlChange" }, 
                        (m, entity) -> {
                                    m.put("gmtCreate", DateUtils.timeStamp2Date(entity.getGmtCreate()));
                                    m.put("gmtModified", DateUtils.timeStamp2Date(entity.getGmtModified()));

                        }
             );
    }
    
     /**
     * 添加或修改
     * @param entity app版本实体
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<SystemResponse<Object>>>  save(@RequestBody AppVersion entity) {

      /*  String nowVersion = entity.getVersion();//新的版本
        QueryWrapper<AppVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(AppVersion::getId);
        List<AppVersion> list  = service.list(queryWrapper);*/

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




    /**
     * 版本号比较
     *
     * @param v1
     * @param v2
     * @return 0代表相等，1代表左边大，-1代表右边大
     * Utils.compareVersion("1.0.358_20180820090554","1.0.358_20180820090553")=1
     */
    public static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

}

