/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ApiDictionaryItemSelect
 * Author:   admin
 * Date:     2018/12/26 10:47
 * Description: 字典项选择
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.unity.system.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.system.entity.Dictionary;
import com.unity.system.entity.DictionaryItem;
import com.unity.system.service.DictionaryItemServiceImpl;
import com.unity.system.service.DictionaryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈字典项选择〉
 *
 * @author sunchao
 * @create 2018/12/26
 * @since 1.0.0
 */
@Controller
@RequestMapping("/apiDictionaryItem")
public class ApiDictionaryItemSelectController extends BaseWebController {
    @Autowired
    DictionaryItemServiceImpl dicItemService;
    @Autowired
    DictionaryServiceImpl dicService;

    /**
     * 模块入口
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @return 返回视图
     */
    @RequestMapping("/moduleEntrance/{iframe}")
    public String moduleEntrance(Model model, @PathVariable("iframe") String iframe) {
        model.addAttribute("iframe", iframe);
        return "ApiDictionaryItemSelect";
    }

//    /**
//     * 字典项页面入口
//     * @param model MVC模型
//     * @param iframe 用于刷新或调用iframe内容
//     * @return 返回视图
//     */
//    @RequestMapping("/moduleEntrance/dic/{iframe}/{idSysDictionary}/{reqType}/{multiple}/{method}")
//    public String moduleEntranceDic(Model model, @PathVariable("iframe") String iframe,
//           @PathVariable("idSysDictionary")String idSysDictionary,
//            @PathVariable("reqType") String reqType,
//            @PathVariable("multiple")Integer multiple
//            ,@PathVariable("method")String method
//            ,String selectList) {
//        model.addAttribute("iframe", iframe);
//        model.addAttribute("idSysDictionary", idSysDictionary);
//        // 1 只取叶子节点 2 取半选中状态节点 3 不取半选中状态节点
//        model.addAttribute("reqType", reqType);
//        // 回调函数名
//        model.addAttribute("method", method);
//        model.addAttribute("selectList", selectList==null?"":selectList);
//
//        if(multiple==null) multiple = 1;
//        if(multiple == 1){
//            model.addAttribute("multipleFlag",multiple);
//            model.addAttribute("multiple", "setting:{check: { enable: true}},");
//        }
//        else {
//            model.addAttribute("multipleFlag",multiple);
//            model.addAttribute("multiple", "");
//        }
//        return "ApiDictionaryItemList";
//    }
//
//    // 获取所有的字典项
//    @RequestMapping("/getAllDicItems")
//    public Mono<ResponseEntity<SystemResponse<Object>>> getAllDicItems(@RequestBody Dictionary dic){
//        List<DictionaryItem> result= new ArrayList<>();
//        if(dic.getId()!=null){
//            QueryWrapper<DictionaryItem> wrapper = new QueryWrapper<>();
//            wrapper.lambda().eq(DictionaryItem::getIdSysDictionary,dic.getId());
//            result= dicItemService.list(wrapper);
//            return success(result);
//        }
//        if (dic.getName()!=null){
//            QueryWrapper<Dictionary> wrapper = new QueryWrapper<>();
//            wrapper.lambda().eq(Dictionary::getName,dic.getName());
//            Dictionary one = dicService.getOne(wrapper);
//
//            QueryWrapper<DictionaryItem> itemWrapper = new QueryWrapper<>();
//            itemWrapper.lambda().eq(DictionaryItem::getIdSysDictionary,dic.getId());
//            result= dicItemService.list(itemWrapper);
//            return success(result);
//        }
//        return success(result);
//    }
}