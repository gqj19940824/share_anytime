package com.unity.system.controller.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.system.entity.Cfg;
import com.unity.system.service.CfgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 配置项
 * @author creator
 * 生成时间 2018-12-20 17:12:38
 */
@Controller
@RequestMapping("/feign/cfg")
public class CfgFeignController extends BaseWebController  {

    @Autowired
    CfgServiceImpl service;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 根据cfgType，查询cfgValue
     * @param cfgType
     * @return
     */
    @PostMapping("/getCfgValue")
    @ResponseBody
    public String getCfgValue(@RequestParam("cfgType") String cfgType) {
        Object cfgValue = redisTemplate.opsForValue().get("cfgType" + cfgType);
        if(cfgValue!=null) return cfgValue.toString();
        LambdaQueryWrapper<Cfg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cfg::getCfgType,cfgType);
        return  service.getOne(wrapper).getCfgVal();
    }

    /**
     * 根据cfgType，查询对象
     *
     * @param cfgType 参数类型
     * @return 查询参数信息
     * @author zhangxiaogang
     * @since 2019/3/3 14:59
     */
    @PostMapping("/getCfgObject")
    @ResponseBody
    public Cfg getCfgObject(@RequestParam("cfgType") String cfgType) {
        QueryWrapper<Cfg> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Cfg::getCfgType, cfgType);
        return service.getOne(wrapper);
    }

}
