
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.RedisConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.Customer;
import com.unity.common.ui.PageEntity;
import com.unity.innovation.entity.DailyWorkKeyword;
import com.unity.innovation.entity.IplYzgtMain;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.util.InnovationUtil;
import com.unity.springboot.support.holder.LoginContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unity.innovation.entity.IplYzgtMain;
import com.unity.innovation.dao.IplYzgtMainDao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * 亦庄国投业务处理
 * <p>
 * create by zhangxiaogang at 2019/9/27 16:05
 */
@Service
public class IplYzgtMainServiceImpl extends BaseServiceImpl<IplYzgtMainDao, IplYzgtMain> {


    /**
     * 功能描述 分页列表查询
     *
     * @param search 查询条件
     * @return 返回数据
     * @author zhangxiaogang
     * @date 2019/9/27 13:43
     */
    public IPage<IplYzgtMain> listByPage(PageEntity<IplYzgtMain> search) {
        LambdaQueryWrapper<IplYzgtMain> lqw = new LambdaQueryWrapper<>();
        //标题
        if (StringUtils.isNotBlank(search.getEntity().getEnterpriseName())) {
            lqw.like(IplYzgtMain::getEnterpriseName, search.getEntity().getEnterpriseName());
        }
        //行业类别
        if (search.getEntity().getIndustryCategory() != null) {
            lqw.eq(IplYzgtMain::getIndustryCategory, search.getEntity().getIndustryCategory());
        }
        //状态
        if (search.getEntity().getSource() != null) {
            lqw.eq(IplYzgtMain::getSource, search.getEntity().getSource());
        }
        //创建时间
        if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
            long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
            long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
            //gt 大于 lt 小于
            lqw.lt(IplYzgtMain::getGmtCreate, end);
            lqw.gt(IplYzgtMain::getGmtCreate, begin);
        }
        //本单位数据 管理员 列表数据都要显示
        Customer customer = LoginContextHolder.getRequestAttributes();
        /*if (customer.getIdRbacDepartment() != null && customer.isAdmin != null) {
            if (YesOrNoEnum.NO.getType() == customer.isAdmin) {
                lqw.eq(IplYzgtMain::getIdRbacDepartment, customer.getIdRbacDepartment());
            }
        }
        //管理员 单位数据
        if (search.getEntity().getIdRbacDepartment() != null) {
            lqw.eq(IplYzgtMain::getIdRbacDepartment, search.getEntity().getIdRbacDepartment());
        }*/
        //排序规则      未提请发布在前，已提请发布在后；未提请发布按创建时间倒序，已提请发布按提请时间倒序
        lqw.last(" ORDER BY state ASC , gmt_create desc ");
        IPage<IplYzgtMain> list = page(search.getPageable(), lqw);
        if (CollectionUtils.isEmpty(list.getRecords())) {
            return list;
        }



        return null;
    }


}
