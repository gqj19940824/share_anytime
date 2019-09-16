package com.unity.system.controller.api;//package com.unity.system.controller.api;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.unity.system.entity.Dictionary;
//import com.unity.system.entity.DictionaryItem;
//import com.unity.system.service.DictionaryItemServiceImpl;
//import com.unity.system.service.DictionaryServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// * <p>
// * createby:zhaozesheng at 2019-01-08 14:17
// */
//@RestController
//@RequestMapping("api/dictionary")
//@Slf4j
//public class DictionaryApiController {
//
//    @Autowired
//    DictionaryServiceImpl service;
//    @Autowired
//    DictionaryItemServiceImpl itemService;
//    /**
//     * 查询业务类型的字典ID
//     * @return  List<Dictionary>
//     * @author  zhaozesheng
//     * @since   2019/1/8 11:07
//     */
//    @GetMapping("/getDictionaryByName/{name}")
//    public List<DictionaryItem> getDictionaryByName(@PathVariable("name") String name){
//        LambdaQueryWrapper<Dictionary> lamdic = new LambdaQueryWrapper<>();
//        lamdic.eq(Dictionary::getName,name);
//        Dictionary dic = service.getOne(lamdic);
//        if(dic==null) return null;
//        LambdaQueryWrapper<DictionaryItem> lamDicItem = new LambdaQueryWrapper<>();
//        lamDicItem.eq(DictionaryItem::getIdSysDictionary,dic.getId());
//        List<DictionaryItem> dictionaryItems = itemService.list(lamDicItem);
//        return dictionaryItems;
//    }
//}
