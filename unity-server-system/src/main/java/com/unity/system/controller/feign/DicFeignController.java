package com.unity.system.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.system.entity.Dic;
import com.unity.system.entity.DicGroup;
import com.unity.system.service.DicGroupServiceImpl;
import com.unity.system.service.DicServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈字典项选择〉
 *
 * @author qinhuan
 * @create 2019/07/23
 * @since 1.0.0
 */
@RestController
@RequestMapping("/feign/dic")
public class DicFeignController extends BaseWebController {

    @Autowired
    private DicServiceImpl dicService;
    @Autowired
    private DicGroupServiceImpl dicGroupService;

    /**
     * 根据字典组编码和字典项编码查询字典
     *
     * @param groupCode 字典组编码
     * @param dicCode 字典项编码
     * @author qinhuan
     * @since 2019年07月11日13:56:45
     */
    @PostMapping("/getDicByCode")
    public Dic getDicByCode(@RequestParam("groupCode") String groupCode, @RequestParam("dicCode") String dicCode){
        if (StringUtils.isEmpty(groupCode) || StringUtils.isEmpty(dicCode)){
            return null;
        }
        return dicService.getOne(new LambdaQueryWrapper<Dic>().eq(Dic::getGroupCode, groupCode).eq(Dic::getDicCode, dicCode));
    }

    /**
     * 根据字典组编码查询字典组
     *
     * @param groupCode 字典组编码
     * @author qinhuan
     * @since 2019年07月11日13:56:45
     */
    @PostMapping("/getDicGroupByGroupCode")
    public DicGroup getDicGroupByGroupCode(@RequestParam("groupCode") String groupCode){ // TODO 新增是限制groupcode不能重复
        return dicGroupService.getOne(new LambdaQueryWrapper<DicGroup>().eq(DicGroup::getGroupCode, groupCode));
    }

    /**
     * 根据字典组编码查询字典项列表
     *
     * @param groupCode 字典组编码
     * @author qinhuan
     * @since 2019年07月11日13:56:45
     */
    @PostMapping("/getDicsByGroupCode")
    public List<Dic> getDicsByGroupCode(@RequestParam("groupCode") String groupCode){
        return dicService.list(new LambdaQueryWrapper<Dic>().eq(Dic::getGroupCode,groupCode).orderByAsc(Dic::getSort));
    }
}