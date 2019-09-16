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
package com.unity.system.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.util.JsonUtil;
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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br> 
 * 〈字典项选择〉
 *
 * @author sunchao
 * @create 2018/12/26
 * @since 1.0.0
 */
@RestController
@RequestMapping("/feign/Dictionary")
public class DictionaryFeignController extends BaseWebController {
    @Autowired
    DictionaryItemServiceImpl dicItemService;
    @Autowired
    DictionaryServiceImpl dicService;

    /**
     * 获取字典项列表
     * @param names 字典名称列表
     * @return
     */
    @RequestMapping("/getDic")
    public Object getDic(@RequestBody List<String> names){
        List<DictionaryItem> result= new ArrayList<>();

            LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Dictionary::getName,names.toArray());
            List<Dictionary> list = dicService.list(wrapper);
            List<Object> ids = list.stream().map(o->o.getId()).collect(Collectors.toList());

            LambdaQueryWrapper<DictionaryItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.in(DictionaryItem::getIdSysDictionary,ids.toArray())
                .orderBy(true, false,DictionaryItem::getSort);;
            result= dicItemService.list(itemWrapper);

        return JsonUtil.ObjectToList(result,new String[]{
                "id","name","gradationCode","idParent"
            },(m,entity)->{
                m.put("dic",
                    list.stream().filter(o->o.getId().equals(entity.getIdSysDictionary()))
                            .map(o->o.getName()).findFirst().orElse(null)
                );
            });

    }
}