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
package com.unity.system.controller.view;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
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
@RequestMapping("/view/dic")
public class DictionaryViewController extends BaseWebController {
    @Autowired
    DictionaryItemServiceImpl dicItemService;
    @Autowired
    DictionaryServiceImpl dicService;

    private boolean isValidLong(String str){
        try{
            long _v = Long.parseLong(str);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * 字典项页面选择
     * @param model MVC模型
     * @param iframe 用于刷新或调用iframe内容
     * @return 返回视图
     */
    @RequestMapping("/sel/{iframe}/{dicName}/{reqType}/{multiple}/{method}")
    public String moduleEntranceDic(Model model, @PathVariable("iframe") String iframe,
           @PathVariable("dicName")String dicName,
            @PathVariable("reqType") String reqType,
            @PathVariable("multiple")Integer multiple
            ,@PathVariable("method")String method
            ,String selectList) {
        model.addAttribute("iframe", iframe);
        //兼容id和name的情况
        String dicId = dicName;
        if(!isValidLong(dicName)){
            LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Dictionary::getName,dicName);
            Dictionary dic = dicService.getOne(wrapper);
            if(dic==null) throw new UnityRuntimeException("未找到字典");
            dicId = dic.getId().toString();
        }

        model.addAttribute("idSysDictionary", dicId);
        // 1 只取叶子节点 2 取半选中状态节点 3 不取半选中状态节点
        model.addAttribute("reqType", reqType);
        // 回调函数名
        model.addAttribute("method", method);
        model.addAttribute("selectList", selectList==null?"":selectList);

        if(multiple==null) multiple = 1;
        if(multiple == 1){
            model.addAttribute("multipleFlag",multiple);
            model.addAttribute("multiple", "setting:{check: { enable: true}},");
        }
        else {
            model.addAttribute("multipleFlag",multiple);
            model.addAttribute("multiple", "");
        }
        return "interface/Dictionary";
    }
}