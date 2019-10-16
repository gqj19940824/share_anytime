
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.pojos.Dic;
import com.unity.common.ui.PageEntity;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.dao.InfoDeptSatbDao;
import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InfoDeptSatbServiceImpl extends BaseServiceImpl<InfoDeptSatbDao, InfoDeptSatb> {

    @Resource
    private SysCfgServiceImpl sysCfgService;

    @Resource
    private DicUtils dicUtils;

    /**
     * 功能描述 分页接口
     *
     * @param search 查询条件
     * @return 分页集合
     * @author gengzhiqiang
     * @date 2019/9/25 16:26
     */
    public IPage<InfoDeptSatb> listByPage(PageEntity<InfoDeptSatb> search) {
        LambdaQueryWrapper<InfoDeptSatb> lqw = new LambdaQueryWrapper<>();
        if (search != null && search.getEntity() != null) {
            //企业名称
            if (StringUtils.isNotBlank(search.getEntity().getEnterpriseName())) {
                lqw.like(InfoDeptSatb::getEnterpriseName, search.getEntity().getEnterpriseName());
            }
            //行业类型
            if (search.getEntity().getIndustryCategory() != null) {
                lqw.eq(InfoDeptSatb::getIndustryCategory, search.getEntity().getIndustryCategory());
            }
            //企业规模
            if (search.getEntity().getEnterpriseScale() != null) {
                lqw.eq(InfoDeptSatb::getEnterpriseScale, search.getEntity().getEnterpriseScale());
            }
            //企业性质
            if (search.getEntity().getEnterpriseNature() != null) {
                lqw.eq(InfoDeptSatb::getEnterpriseNature, search.getEntity().getEnterpriseNature());
            }
            //创新成果
            if (StringUtils.isNotBlank(search.getEntity().getInDetail())) {
                lqw.like(InfoDeptSatb::getInDetail, search.getEntity().getInDetail());
            }
            //创新水平
            if (search.getEntity().getAchievementLevel() != null) {
                lqw.eq(InfoDeptSatb::getAchievementLevel, search.getEntity().getAchievementLevel());
            }
            //首次发布
            if (search.getEntity().getIsPublishFirst() != null) {
                lqw.eq(InfoDeptSatb::getIsPublishFirst, search.getEntity().getIsPublishFirst());
            }
            //创建时间
            if (StringUtils.isNotBlank(search.getEntity().getCreateTime())) {
                long begin = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), true);
                //gt 大于 lt 小于
                lqw.gt(InfoDeptSatb::getGmtCreate, begin);
                long end = InnovationUtil.getFirstTimeInMonth(search.getEntity().getCreateTime(), false);
                lqw.lt(InfoDeptSatb::getGmtCreate, end);
            }
            //状态
            if (search.getEntity().getStatus() != null) {
                lqw.eq(InfoDeptSatb::getStatus, search.getEntity().getStatus());
            }
        }
        //排序规则      未提请发布在前，已提请发布在后；各自按创建时间倒序
        lqw.orderByDesc(InfoDeptSatb::getStatus,InfoDeptSatb::getGmtCreate);
        IPage<InfoDeptSatb> list = null;
        if (search != null) {
            list = page(search.getPageable(), lqw);
            List<Integer> enumList = Arrays.asList(new Integer[]{SysCfgEnum.THREE.getId(), SysCfgEnum.SIX.getId()});
            List<SysCfg> typeList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>().in(SysCfg::getCfgType, enumList));
            Map<Long, String> collect = typeList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
            list.getRecords().forEach(is -> {
                //行业类型
                if ((is.getIndustryCategory() != null) && (collect.get(is.getIndustryCategory()) != null)) {
                    is.setIndustryCategoryName(collect.get(is.getIndustryCategory()));
                }
                //企业规模
                if (is.getEnterpriseScale() != null) {
                    Dic type = dicUtils.getDicByCode(DicConstants.ENTERPRISE_SCALE, is.getEnterpriseScale().toString());
                    if (type != null && StringUtils.isNotBlank(type.getDicValue())) {
                        is.setEnterpriseScaleName(type.getDicValue());
                    }
                }
                //企业性质
                if ((is.getEnterpriseNature() != null) && (collect.get(is.getEnterpriseNature()) != null)) {
                    is.setEnterpriseNatureName(collect.get(is.getEnterpriseNature()));
                }
                //成果创新水平
                if (is.getAchievementLevel() != null) {
                    Dic type = dicUtils.getDicByCode(DicConstants.ACHIEVEMENT_INNOVATI, is.getAchievementLevel().toString());
                    if (type != null && StringUtils.isNotBlank(type.getDicValue())) {
                        is.setAchievementLevelName(type.getDicValue());
                    }
                }
            });
        }
        return list;
    }
}
