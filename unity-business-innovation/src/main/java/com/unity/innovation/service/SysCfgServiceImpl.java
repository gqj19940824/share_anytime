
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.Customer;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.dao.SysCfgDao;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.entity.SysCfgScope;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * ClassName: SysCfgService
 * date: 2019-09-17 14:53:55
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
public class SysCfgServiceImpl extends BaseServiceImpl<SysCfgDao, SysCfg> {

    @Resource
    private SysCfgScopeServiceImpl scopeService;

    /**
     * 根据ids查询其对应的values
     *
     * @param ids
     * @return
     * @author qinhuan
     * @since 2019-09-26 13:48
     */
    public List<Map<String, Object>> getValues(Set<Long> ids){
        return baseMapper.getValues(ids);
    }

    /**
     * 功能描述 根据类型 获取列表
     *          类型 1：工作类别 2：关键字 3：产业类型 4：需求类型
     * @return java.util.List<java.util.Map   <   java.lang.String   ,   java.lang.Object>>
     * @author gengzhiqiang
     * @date 2019/9/17 19:50
     */
    public List<Map<String, Object>> getSysList(Integer type) {
        List<Long> list = Lists.newArrayList();
        list.add(0L);
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (customer.getIdRbacDepartment() != null) {
            list.add(customer.getIdRbacDepartment());
        }

        List<SysCfg> typeList = list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, type).eq(SysCfg::getUseStatus, 1).in(SysCfg::getScope, list));
        return JsonUtil.ObjectToList(typeList, null, SysCfg::getId, SysCfg::getCfgVal);
    }

    /**
     * 功能描述 根据类型 获取列表
     *          类型 1：工作类别 2：关键字 3：产业类型 4：需求类型
     * @return java.util.List<java.util.Map   <   java.lang.String   ,   java.lang.Object>>
     * @author gengzhiqiang
     * @date 2019/9/17 19:50
     */
    public List<Map<String, Object>> getSysList1(Integer type) {
        List<Long> list = Lists.newArrayList();
        list.add(0L);
        Customer customer = LoginContextHolder.getRequestAttributes();
        if (customer.getIdRbacDepartment() != null) {
            list.add(customer.getIdRbacDepartment());
        }
        List<SysCfgScope> mList = scopeService.list(new LambdaQueryWrapper<SysCfgScope>().in(SysCfgScope::getIdRbacDepartment, list));
        List<Long> ids = mList.stream().map(SysCfgScope::getIdSysCfg).collect(Collectors.toList());
        List<SysCfg> typeList = list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, type).eq(SysCfg::getUseStatus, 1).in(SysCfg::getId, ids));
        return JsonUtil.ObjectToList(typeList, null, SysCfg::getId, SysCfg::getCfgVal);
    }

    /**
    * 新增/修改
    *
    * @param entity 实体
    * @author JH
    * @date 2019/9/21 15:53
    */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateSysCfg(SysCfg entity) {
        List<Long> scope = entity.getScope();
        List<SysCfgScope> list = Lists.newArrayList();
        super.saveOrUpdate(entity);
        scope.forEach( departmentId ->{
            SysCfgScope cfgScope = new SysCfgScope();
            cfgScope.setIdSysCfg(entity.getId());
            cfgScope.setIdRbacDepartment(departmentId);
            list.add(cfgScope);
        });
        //修改 删除原有关联表数据范围
        if(entity.getId() != null) {
            scopeService.remove(new LambdaUpdateWrapper<SysCfgScope>().eq(SysCfgScope::getIdSysCfg,entity.getId())) ;
        }
        scopeService.saveBatch(list);
    }

    /**
     * 获取行业类别
     *
     * @return 行业类别
     * @author gengjiajia
     * @since 2019/10/08 10:49
     */
    public Map<Long, String> getSysCfgMap(Integer cfgType) {
        List<SysCfg> cfgList = this.list(new LambdaQueryWrapper<SysCfg>()
                .eq(SysCfg::getCfgType, cfgType)
                .eq(SysCfg::getUseStatus, YesOrNoEnum.YES.getType()));
        return cfgList.stream()
                .collect(groupingBy(SysCfg::getId, mapping(SysCfg::getCfgVal, joining())));
    }

}
