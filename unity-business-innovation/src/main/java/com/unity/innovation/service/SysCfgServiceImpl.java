
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.pojos.Customer;
import com.unity.common.util.JsonUtil;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.dao.SysCfgDao;

import java.util.List;
import java.util.Map;

/**
 * ClassName: SysCfgService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2019-09-17 14:53:55
 *
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysCfgServiceImpl extends BaseServiceImpl<SysCfgDao, SysCfg> {

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

}
