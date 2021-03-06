
package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.unity.common.base.BaseServiceImpl;
import com.unity.common.constant.DicConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.Dic;
import com.unity.common.pojos.SystemResponse;
import com.unity.common.ui.PageEntity;
import com.unity.common.utils.DicUtils;
import com.unity.common.utils.UUIDUtil;
import com.unity.innovation.controller.vo.PieVoByDoc;
import com.unity.innovation.dao.InfoDeptSatbDao;
import com.unity.innovation.entity.Attachment;
import com.unity.innovation.entity.InfoDeptSatb;
import com.unity.innovation.entity.SysCfg;
import com.unity.innovation.enums.SysCfgEnum;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
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

    @Resource
    private AttachmentServiceImpl attachmentService;

    private static final String ACHIEVEMENT_LEVEL = "achievementLevel";
    private static final String YES_OR_NO = "yesOrNo";
    private static final String NUM = "num";

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
            if (StringUtils.isNotBlank(search.getEntity().getInGeneralSituation())) {
                lqw.like(InfoDeptSatb::getInGeneralSituation, search.getEntity().getInGeneralSituation());
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
        //lqw.last(" ORDER BY status ASC , gmt_create desc ");
        lqw.orderByDesc(InfoDeptSatb::getGmtCreate);
        IPage<InfoDeptSatb> list = null;
        if (search != null) {
            list = page(search.getPageable(), lqw);
            if (CollectionUtils.isNotEmpty(list.getRecords())) {
                dealData(list.getRecords());
            }
        }
        return list;
    }

    public void dealData(List<InfoDeptSatb> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        List<Integer> enumList = Arrays.asList(new Integer[]{SysCfgEnum.THREE.getId(), SysCfgEnum.SIX.getId()});
        List<SysCfg> typeList = sysCfgService.list(new LambdaQueryWrapper<SysCfg>().in(SysCfg::getCfgType, enumList));
        Map<Long, String> collect = typeList.stream().collect(Collectors.toMap(SysCfg::getId, SysCfg::getCfgVal));
        List<String> attachmentCodeList = records.stream().map(InfoDeptSatb::getAttachmentCode).collect(Collectors.toList());
        List<Attachment> list = attachmentService.list(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, attachmentCodeList));
        Map<String, List<Attachment>> attatchmentMap = list.stream().collect(Collectors.groupingBy(Attachment::getAttachmentCode));

        records.forEach(is -> {
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
            is.setAttachmentList(attatchmentMap.get(is.getAttachmentCode()));
        });
    }


    public IPage<InfoDeptSatb> listForSatb(PageEntity<InfoDeptSatb> search) {
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
            if (StringUtils.isNotBlank(search.getEntity().getInGeneralSituation())) {
                lqw.like(InfoDeptSatb::getInGeneralSituation, search.getEntity().getInGeneralSituation());
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
            //包内的和未提请的数据
            if (search.getEntity().getIdPmInfoDept() != null) {
                List<InfoDeptSatb> list = list(new LambdaQueryWrapper<InfoDeptSatb>()
                        .eq(InfoDeptSatb::getIdPmInfoDept, search.getEntity().getIdPmInfoDept()));
                List<Long> ids = list.stream().map(InfoDeptSatb::getId).collect(Collectors.toList());
                lqw.and(w -> w
                        .in(InfoDeptSatb::getId, ids)
                        .or()
                        .eq(InfoDeptSatb::getStatus, YesOrNoEnum.NO.getType()));
            } else {
                lqw.eq(InfoDeptSatb::getStatus, YesOrNoEnum.NO.getType());
            }
        }

        //排序规则      未提请发布在前，已提请发布在后；各自按创建时间倒序
        lqw.orderByDesc(InfoDeptSatb::getStatus, InfoDeptSatb::getGmtCreate);
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
                //企业性质
                if ((is.getEnterpriseNature() != null) && (collect.get(is.getEnterpriseNature()) != null)) {
                    is.setEnterpriseNatureName(collect.get(is.getEnterpriseNature()));
                }
                //企业规模
                if (is.getEnterpriseScale() != null) {
                    Dic type = dicUtils.getDicByCode(DicConstants.ENTERPRISE_SCALE, is.getEnterpriseScale().toString());
                    if (type != null && StringUtils.isNotBlank(type.getDicValue())) {
                        is.setEnterpriseScaleName(type.getDicValue());
                    }
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

    /**
     * 功能描述 新增编辑提交
     *
     * @param entity 实体
     * @author gengzhiqiang
     * @date 2019/10/16 10:40
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEntity(InfoDeptSatb entity) {
        if (entity.getId() == null) {
            //默认为提请发布
            entity.setStatus(YesOrNoEnum.NO.getType());
            String attachmentCode = UUIDUtil.getUUID();
            entity.setAttachmentCode(attachmentCode);
            save(entity);
            //保存附件
            attachmentService.updateAttachments(attachmentCode, entity.getAttachmentList());
        } else {
            InfoDeptSatb old = getById(entity.getId());
            if (old == null) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                        .message("未获取到对象").build();
            }
            if (YesOrNoEnum.YES.getType() == old.getStatus()) {
                throw UnityRuntimeException.newInstance()
                        .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                        .message("已提请发布状态下数据不可编辑").build();
            }
            String attachmentCode = old.getAttachmentCode();
            updateById(entity);
            //保存附件
            attachmentService.updateAttachments(attachmentCode, entity.getAttachmentList());
        }
    }

    /**
     * 功能描述 详情接口
     *
     * @param entity 对象
     * @return com.unity.innovation.entity.DailyWorkStatus 对象
     * @author gengzhiqiang
     * @date 2019/9/17 16:03
     */
    public InfoDeptSatb detailById(InfoDeptSatb entity) {
        InfoDeptSatb vo = getById(entity.getId());
        if (vo == null) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.LACK_REQUIRED_PARAM)
                    .message("未获取到对象").build();
        }
        //附件
        List<Attachment> attachmentList = attachmentService.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getAttachmentCode, vo.getAttachmentCode()));
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            vo.setAttachmentList(attachmentList);
        }
        HashSet<Long> set = new HashSet();
        //行业类型
        if (vo.getIndustryCategory() != null) {
            set.add(vo.getIndustryCategory());
        }
        //企业性质
        if (vo.getEnterpriseNature() != null) {
            set.add(vo.getEnterpriseNature());
        }
        if (CollectionUtils.isNotEmpty(set)) {
            Map<Long, String> map = sysCfgService.getListValues(set);
            //行业类型
            if ((vo.getIndustryCategory() != null) && (map.get(vo.getIndustryCategory()) != null)) {
                vo.setIndustryCategoryName(map.get(vo.getIndustryCategory()));
            }
            //企业性质
            if ((vo.getEnterpriseNature() != null) && (map.get(vo.getEnterpriseNature()) != null)) {
                vo.setEnterpriseNatureName(map.get(vo.getEnterpriseNature()));
            }
        }
        //企业规模
        if (vo.getEnterpriseScale() != null) {
            Dic type = dicUtils.getDicByCode(DicConstants.ENTERPRISE_SCALE, vo.getEnterpriseScale().toString());
            if (type != null && StringUtils.isNotBlank(type.getDicValue())) {
                vo.setEnterpriseScaleName(type.getDicValue());
            }
        }
        //成果创新水平
        if (vo.getAchievementLevel() != null) {
            Dic type = dicUtils.getDicByCode(DicConstants.ACHIEVEMENT_INNOVATI, vo.getAchievementLevel().toString());
            if (type != null && StringUtils.isNotBlank(type.getDicValue())) {
                vo.setAchievementLevelName(type.getDicValue());
            }
        }
        return vo;
    }

    /**
     * 功能描述 批量删除
     *
     * @param ids id集合
     * @author gengzhiqiang
     * @date 2019/9/17 16:14
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeById(List<Long> ids) {
        List<InfoDeptSatb> list = list(new LambdaQueryWrapper<InfoDeptSatb>().in(InfoDeptSatb::getId, ids));
        if (CollectionUtils.isEmpty(list)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("存在已删除数据,请刷新页面后重新操作").build();
        }
        List<InfoDeptSatb> list1 = list.stream().filter(i -> i.getStatus() == YesOrNoEnum.YES.getType()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list1)) {
            throw UnityRuntimeException.newInstance()
                    .code(SystemResponse.FormalErrorCode.ILLEGAL_OPERATION)
                    .message("已提请发布状态下数据不可删除").build();
        }
        List<String> codes = list.stream().map(InfoDeptSatb::getAttachmentCode).collect(Collectors.toList());
        //附件表
        attachmentService.remove(new LambdaQueryWrapper<Attachment>().in(Attachment::getAttachmentCode, codes));
        removeByIds(ids);
    }

    /**
     * 与会路演企业成果创新水平统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计结果
     * @author gengjiajia
     * @since 2019/10/30 09:37
     */
    public Map<String, Object> roadshowEnterpriseInnovationLevel(Long startTime, Long endTime) {
        List<String> nameList = Lists.newArrayList();
        List<PieVoByDoc.DataBean> dataList = Lists.newArrayList();
        //查询 创新成功水平类型对应的数量信息 [{"achievementLevel":"1","num":"2"}]
        List<Map<String, Long>> mapList = baseMapper.roadshowEnterpriseInnovationLevel(startTime, endTime);
        long sum = mapList.stream().mapToLong(map -> map.get(NUM)).sum();
        if(Long.valueOf(sum).equals(0L)){
            //说明没数据
            return null;
        }
        mapList.stream().filter(map -> !map.get(NUM).equals(0L)).forEach(map -> {
            Dic dic = dicUtils.getDicByCode(DicConstants.ACHIEVEMENT_INNOVATI, map.get(ACHIEVEMENT_LEVEL).toString());
            dataList.add(PieVoByDoc.DataBean.newInstance()
                    .name(dic.getDicValue())
                    .value(map.get(NUM))
                    .build());
            nameList.add(dic.getDicValue());
        });
        Map<String, Object> data = Maps.newHashMap();
        data.put("totalNum", sum);
        data.put("pieData", PieVoByDoc.newInstance()
                .legend(PieVoByDoc.LegendBean.newInstance()
                        .data(nameList)
                        .orient("vertical")
                        .x("left")
                        .build())
                .data(dataList)
                .build());
        return data;

    }

    /**
     * 与会路演企业成果首次对外发布情况统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计结果
     * @author gengjiajia
     * @since 2019/10/30 09:37
     */
    public Map<String, Object> firstExternalRelease(Long startTime, Long endTime) {
        List<PieVoByDoc.DataBean> dataList = Lists.newArrayList();
        //查询 创新成功水平类型对应的数量信息 [{"yesOrNo":"1","num":"2"}]
        List<Map<String, Object>> mapList = baseMapper.firstExternalRelease(startTime, endTime);
        long sum = mapList.stream().mapToLong(map -> Long.parseLong(map.get(NUM).toString())).sum();
        if(Long.valueOf(sum).equals(0L)){
            return null;
        }
        mapList.forEach(map -> {
            Integer parseInt = Integer.parseInt(map.get(YES_OR_NO).toString());
            String yesOrNo = parseInt.equals(YesOrNoEnum.YES.getType()) ? "是" : "否";
            dataList.add(PieVoByDoc.DataBean.newInstance()
                            .name(yesOrNo)
                            .value(Long.parseLong(map.get(NUM).toString()))
                            .build());
        });
        Map<String, Object> data = Maps.newHashMap();
        data.put("totalNum", sum);
        data.put("pieData", PieVoByDoc.newInstance()
                .legend(PieVoByDoc.LegendBean.newInstance()
                        .data(Lists.newArrayList("是","否"))
                        .x("left")
                        .orient("vertical")
                        .build())
                .data(dataList)
                .build());
        return data;
    }
}
