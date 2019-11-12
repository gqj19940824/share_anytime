package com.unity.innovation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.unity.common.constant.DicConstants;
import com.unity.common.enums.YesOrNoEnum;
import com.unity.common.pojos.Dic;
import com.unity.common.util.GsonUtils;
import com.unity.common.utils.DicUtils;
import com.unity.innovation.controller.vo.CylinderVo;
import com.unity.innovation.controller.vo.PieVo;
import com.unity.innovation.controller.vo.RadarVo;
import com.unity.innovation.entity.*;
import com.unity.innovation.entity.generated.IpaManageMain;
import com.unity.innovation.enums.BizTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @ClassName StatisticsInfoServiceImpl
 * @Description 企业统计信息
 * @Author JH
 * @Date 2019/10/28 15:50
 */
@Service
public class StatisticsInfoServiceImpl {

    @Resource
    IpaManageMainServiceImpl ipaManageMainService;
    @Resource
    PmInfoDeptServiceImpl pmInfoDeptService;
    @Resource
    InfoDeptYzgtServiceImpl yzgtService;
    @Resource
    SysCfgServiceImpl cfgService;
    @Resource
    InfoDeptSatbServiceImpl satbService;
    @Resource
    DicUtils dicUtils;
    @Resource
    MediaManagerServiceImpl mediaManagerService;

    /**
    * 与会企业基本情况
    *
    * @param timeMap 时间map
    * @return java.util.Map<java.lang.String,java.lang.Object>
    * @author JH
    * @date 2019/10/30 10:10
    */
    public Map<String, Object> getParticipateDeptInfo(Map<String, Long> timeMap,String title) {

        //结果
        Map<String, Object> res = new HashMap<>(16);
        //这个时间段二次打包数据
        List<IpaManageMain> ipaList = ipaManageMainService.list(new LambdaQueryWrapper<IpaManageMain>().ge(IpaManageMain::getGmtCreate, timeMap.get("beginTime")).le(IpaManageMain::getGmtCreate, timeMap.get("endTime")));

        //单位集合
        Set<Long> set = ipaList.stream().map(IpaManageMain::getId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(set)) {

            //所有的在这个时间段发布的入区一次打包数据
            List<PmInfoDept> rqList = pmInfoDeptService.list(new LambdaQueryWrapper<PmInfoDept>()
                    .eq(PmInfoDept::getBizType, BizTypeEnum.RQDEPTINFO.getType())
                    .in(PmInfoDept::getIdIpaMain, set));
            List<Long> rqIds = rqList.stream().map(PmInfoDept::getId).collect(Collectors.toList());
            //所有的在这个时间段发布的路演一次打包数据
            List<PmInfoDept> lyList = pmInfoDeptService.list(new LambdaQueryWrapper<PmInfoDept>()
                    .eq(PmInfoDept::getBizType, BizTypeEnum.LYDEPTINFO.getType())
                    .in(PmInfoDept::getIdIpaMain, set));
            List<Long> lyIds = lyList.stream().map(PmInfoDept::getId).collect(Collectors.toList());

            //入区基础数据
            List<InfoDeptYzgt> yzgtList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(rqIds)) {
                yzgtList = yzgtService.list(new LambdaQueryWrapper<InfoDeptYzgt>().in(InfoDeptYzgt::getIdPmInfoDept, rqIds));
            }
            //路演基础数据
            List<InfoDeptSatb> satbList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(lyIds)) {
                satbList = satbService.list(new LambdaQueryWrapper<InfoDeptSatb>().in(InfoDeptSatb::getIdPmInfoDept, lyIds));
            }

            //legend
            List<String> list = Lists.newArrayList("入区企业", "路演企业");

            //与会企业行业分布统计
            //按行业求出个数 key 行业类型id value 个数
            Map<Long, Long> rqIdCountByIndustryCategoryMap = yzgtList.stream().collect(Collectors.groupingBy(InfoDeptYzgt::getIndustryCategory, Collectors.counting()));
            Map<Long, Long> lyIdCountByIndustryCategoryMap = satbList.stream().collect(Collectors.groupingBy(InfoDeptSatb::getIndustryCategory, Collectors.counting()));
            if(MapUtils.isEmpty(rqIdCountByIndustryCategoryMap) && MapUtils.isEmpty(lyIdCountByIndustryCategoryMap)) {
                res.put("industryCategory", null);
            } else {
                //所有的行业类别
                List<SysCfg> industryCategoryList = cfgService.list(new LambdaQueryWrapper<SysCfg>().eq(SysCfg::getCfgType, 3).eq(SysCfg::getUseStatus,YesOrNoEnum.YES.getType()));

                //data
                List<RadarVo.SeriesBean.DataBean> industryCategoryDataList = Lists.newArrayList();
                for(String n : list) {
                    List<Long> countList = Lists.newArrayList();
                    if (n.equals("入区企业")) {
                        if(MapUtils.isNotEmpty(rqIdCountByIndustryCategoryMap)){
                            for(SysCfg v : industryCategoryList) {
                                countList.add(rqIdCountByIndustryCategoryMap.getOrDefault(v.getId(),0L));
                            }
                        }
                    } else {
                        if(MapUtils.isNotEmpty(lyIdCountByIndustryCategoryMap)) {
                            for(SysCfg v : industryCategoryList) {
                                countList.add(lyIdCountByIndustryCategoryMap.getOrDefault(v.getId(),0L));
                            }
                        }
                    }
                    RadarVo.SeriesBean.DataBean dataBean = RadarVo.SeriesBean.DataBean
                            .newInstance()
                            .name(n)
                            .value(countList).build();
                    industryCategoryDataList.add(dataBean);
                }

                List<RadarVo.RadarBean.IndicatorBean> industryCategoryIndicator = Lists.newArrayList();
                for (SysCfg n : industryCategoryList) {
                    RadarVo.RadarBean.IndicatorBean indicatorBean = RadarVo.RadarBean.IndicatorBean.newInstance().name(n.getCfgVal())
                            .max(Math.max(MapUtils.isEmpty(rqIdCountByIndustryCategoryMap) ? 0 :rqIdCountByIndustryCategoryMap.getOrDefault(n.getId(),0L), MapUtils.isEmpty(lyIdCountByIndustryCategoryMap) ? 0 : lyIdCountByIndustryCategoryMap.getOrDefault(n.getId(),0L)) + 1)
                            .build();
                    industryCategoryIndicator.add(indicatorBean);
                }

                RadarVo industryCategory = RadarVo.newInstance()
                        .title(RadarVo.TitleBean.newInstance().text(title).build())
                        .legend(RadarVo.LegendBean.newInstance().data(list).build())
                        .radar(RadarVo.RadarBean.newInstance().indicator(industryCategoryIndicator).build())
                        .series(Lists.newArrayList(RadarVo.SeriesBean.newInstance().data(industryCategoryDataList).build()))
                        .build();
                res.put("industryCategory", industryCategory);
            }


            //与会企业性质统计
            //按企业性质求出个数 key 行业类型id value 个数
            Map<Long, Long> rqIdCountByEnterpriseNatureMap = yzgtList.stream().collect(Collectors.groupingBy(InfoDeptYzgt::getEnterpriseNature, Collectors.counting()));
            Map<Long, Long> lyIdCountByEnterpriseNatureMap = satbList.stream().collect(Collectors.groupingBy(InfoDeptSatb::getEnterpriseNature, Collectors.counting()));
           if(MapUtils.isEmpty(rqIdCountByEnterpriseNatureMap) && MapUtils.isEmpty(lyIdCountByEnterpriseNatureMap)) {
               res.put("enterpriseNature", null);
           }else {
               //所有的企业性质
               List<SysCfg> enterpriseNatureList = cfgService.list(new LambdaQueryWrapper<SysCfg>().eq(SysCfg::getCfgType, 6).eq(SysCfg::getUseStatus,YesOrNoEnum.YES.getType()));
               //data
               List<RadarVo.SeriesBean.DataBean> enterpriseNatureDataList = Lists.newArrayList();
               list.forEach(n -> {
                   List<Long> countList = Lists.newArrayList();
                   if (n.equals("入区企业")) {
                       if(MapUtils.isNotEmpty(rqIdCountByEnterpriseNatureMap)) {
                           enterpriseNatureList.forEach(v -> countList.add(rqIdCountByEnterpriseNatureMap.getOrDefault(v.getId(),0L)));
                       }
                   } else {
                       if(MapUtils.isNotEmpty(lyIdCountByEnterpriseNatureMap)) {
                           enterpriseNatureList.forEach(v -> countList.add(lyIdCountByEnterpriseNatureMap.getOrDefault(v.getId(),0L)));
                       }
                   }
                   RadarVo.SeriesBean.DataBean dataBean = RadarVo.SeriesBean.DataBean
                           .newInstance()
                           .name(n)
                           .value(countList).build();
                   enterpriseNatureDataList.add(dataBean);
               });

               List<RadarVo.RadarBean.IndicatorBean> enterpriseNatureIndicator = Lists.newArrayList();
               enterpriseNatureList.forEach(n -> {
                   RadarVo.RadarBean.IndicatorBean indicatorBean = RadarVo.RadarBean.IndicatorBean.newInstance().name(n.getCfgVal())
                           .max(Math.max(MapUtils.isEmpty(rqIdCountByEnterpriseNatureMap) ? 0 :rqIdCountByEnterpriseNatureMap.getOrDefault(n.getId(),0L), MapUtils.isEmpty(lyIdCountByEnterpriseNatureMap) ? 0 : lyIdCountByEnterpriseNatureMap.getOrDefault(n.getId(),0L)) + 1)
                           .build();
                   enterpriseNatureIndicator.add(indicatorBean);
               });
               RadarVo enterpriseNature = RadarVo.newInstance()
                       .title(RadarVo.TitleBean.newInstance().text(title).build())
                       .legend(RadarVo.LegendBean.newInstance().data(list).build())
                       .radar(RadarVo.RadarBean.newInstance().indicator(enterpriseNatureIndicator).build())
                       .series(Lists.newArrayList(RadarVo.SeriesBean.newInstance().data(enterpriseNatureDataList).build()))
                       .build();
               res.put("enterpriseNature", enterpriseNature);
           }



//            //与会企业规模统计
//            //按企业规模求出个数 key 行业类型id value 个数
            Map<Long, Long> rqIdCountByEnterpriseScaleMap = yzgtList.stream().collect(Collectors.groupingBy(InfoDeptYzgt::getEnterpriseScale, Collectors.counting()));
            Map<Long, Long> lyIdCountByEnterpriseScaleMap = satbList.stream().collect(Collectors.groupingBy(InfoDeptSatb::getEnterpriseScale, Collectors.counting()));
            if(MapUtils.isEmpty(rqIdCountByEnterpriseScaleMap) && MapUtils.isEmpty(lyIdCountByEnterpriseScaleMap)) {
                res.put("enterpriseScale", null);
            }else {
                //所有的企业规模
                List<Dic> enterpriseScaleList = dicUtils.getDicsByGroupCode(DicConstants.ENTERPRISE_SCALE);

                //data
                List<RadarVo.SeriesBean.DataBean> enterpriseScaleDataList = Lists.newArrayList();
                list.forEach(n -> {
                    List<Long> countList = Lists.newArrayList();
                    if (n.equals("入区企业")) {
                        if(MapUtils.isNotEmpty(rqIdCountByEnterpriseScaleMap)) {
                            enterpriseScaleList.forEach(dic -> countList.add(rqIdCountByEnterpriseScaleMap.getOrDefault(Long.parseLong(dic.getDicCode()),0L)));

                        }
                    } else {
                        if(MapUtils.isNotEmpty(lyIdCountByEnterpriseScaleMap)) {
                            enterpriseScaleList.forEach(dic -> countList.add(lyIdCountByEnterpriseScaleMap.getOrDefault(Long.parseLong(dic.getDicCode()),0L)));
                        }
                    }
                    RadarVo.SeriesBean.DataBean dataBean = RadarVo.SeriesBean.DataBean
                            .newInstance()
                            .name(n)
                            .value(countList).build();
                    enterpriseScaleDataList.add(dataBean);
                });
                List<RadarVo.RadarBean.IndicatorBean> enterpriseScaleIndicator = Lists.newArrayList();
                for(Dic n :enterpriseScaleList) {
                    RadarVo.RadarBean.IndicatorBean indicatorBean = RadarVo.RadarBean.IndicatorBean.newInstance().name(n.getDicValue())
                            .max(Math.max(MapUtils.isEmpty(rqIdCountByEnterpriseScaleMap) ? 0 :rqIdCountByEnterpriseScaleMap.getOrDefault(Long.parseLong(n.getDicCode()),0L), MapUtils.isEmpty(lyIdCountByEnterpriseScaleMap) ? 0 : lyIdCountByEnterpriseScaleMap.getOrDefault(n.getId(),0L)) + 1)
                            .build();
                    enterpriseScaleIndicator.add(indicatorBean);
                }
                RadarVo enterpriseScale = RadarVo.newInstance()
                        .title(RadarVo.TitleBean.newInstance().text(title).build())
                        .legend(RadarVo.LegendBean.newInstance().data(list).build())
                        .radar(RadarVo.RadarBean.newInstance().indicator(enterpriseScaleIndicator).build())
                        .series(Lists.newArrayList(RadarVo.SeriesBean.newInstance().data(enterpriseScaleDataList).build()))
                        .build();
                res.put("enterpriseScale", enterpriseScale);
            }

        } else {
            res.put("industryCategory", null);
            res.put("enterpriseNature", null);
            res.put("enterpriseScale", null);
        }

        return res;
    }

    /**
     * 与会投资机构基本情况
     *
     * @param timeMap 时间map
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author JH
     * @date 2019/10/30 10:10
     */
    public Map<String, Object> getParticipateInvestInfo(Map<String, Long> timeMap,String title) {

        //结果
        Map<String, Object> res = new HashMap<>();
        //二次包数据
        List<IpaManageMain> ipaList = ipaManageMainService.list(new LambdaQueryWrapper<IpaManageMain>().ge(IpaManageMain::getGmtCreate, timeMap.get("beginTime")).le(IpaManageMain::getGmtCreate, timeMap.get("endTime")));
        Set<Long> set = ipaList.stream().map(IpaManageMain::getId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(set)) {
            //所有的在这个时间段发布的投资机构一次打包数据
            List<PmInfoDept> investList = pmInfoDeptService.list(new LambdaQueryWrapper<PmInfoDept>()
                    .eq(PmInfoDept::getBizType, BizTypeEnum.INVESTMENT.getType())
                    .in(PmInfoDept::getIdIpaMain, set));
            //所有的基础数据
            List<IplYzgtMain> dataList = Lists.newArrayList();
            for (PmInfoDept pm : investList) {
                String snapShot = pm.getSnapShot();
                List<IplYzgtMain> list = GsonUtils.parse(snapShot, new TypeToken<List<IplYzgtMain>>() {
                });
                dataList.addAll(list);
            }

            System.out.println("-------------我是一个分割线-----------------");

            //与会投资机构企业规模统计
            Map<Long, Long> countByEnterpriseScaleMap = dataList.stream().collect(Collectors.groupingBy(IplYzgtMain::getEnterpriseScale, Collectors.counting()));
            if(MapUtils.isEmpty(countByEnterpriseScaleMap)) {
                res.put("enterpriseScale", null);
            }else {
                //所有的企业规模
                List<Dic> enterpriseScaleList = dicUtils.getDicsByGroupCode(DicConstants.ENTERPRISE_SCALE);
                List<String> enterpriseScaleLegendData = Lists.newArrayList();
                //seriesData
                List<PieVo.SeriesBean.DataBean> enterpriseScaleSeriesDataList = Lists.newArrayList();
                for (Dic dic : enterpriseScaleList) {
                    Long count = countByEnterpriseScaleMap.getOrDefault(Long.parseLong(dic.getDicCode()), 0L);
                    if(count != 0L) {
                        enterpriseScaleLegendData.add(dic.getDicValue());
                        PieVo.SeriesBean.DataBean seriesData = PieVo.SeriesBean.DataBean
                                .newInstance()
                                .name(dic.getDicValue())
                                .value(count)
                                .build();
                        enterpriseScaleSeriesDataList.add(seriesData);
                    }

                }

                PieVo enterpriseScale = PieVo.newInstance()
                        .title(title)
                        .legend(PieVo.LegendBean.newInstance().data(enterpriseScaleLegendData).build())
                        .series(Lists.newArrayList(PieVo.SeriesBean.newInstance().data(enterpriseScaleSeriesDataList).build()))
                        .total(enterpriseScaleSeriesDataList.stream().mapToLong(PieVo.SeriesBean.DataBean::getValue).sum())
                        .build();
                res.put("enterpriseScale", enterpriseScale);

            }


            System.out.println("-------------我是一个分割线-----------------");


            //与会投资机构企业规模统计
            Map<Long, Long> countByEnterpriseLocationMap = dataList.stream().collect(Collectors.groupingBy(IplYzgtMain::getEnterpriseLocation, Collectors.counting()));
            if(MapUtils.isEmpty(countByEnterpriseLocationMap)) {
                res.put("enterpriseLocation", null);
            }else {
                //所有的企业属地
                List<Dic> enterpriseLocationList = dicUtils.getDicsByGroupCode(DicConstants.ENTERPRISE_LOCATION);
                List<String> legendData = Lists.newArrayList();
                //seriesData
                List<PieVo.SeriesBean.DataBean> seriesDataList = Lists.newArrayList();
                for (Dic dic : enterpriseLocationList) {
                    Long locationCount = countByEnterpriseLocationMap.getOrDefault(Long.parseLong(dic.getDicCode()), 0L);
                    if(locationCount != 0L) {
                        legendData.add(dic.getDicValue());
                        PieVo.SeriesBean.DataBean seriesData = PieVo.SeriesBean.DataBean
                                .newInstance()
                                .name(dic.getDicValue())
                                .value(locationCount)
                                .build();
                        seriesDataList.add(seriesData);
                    }

                }
                PieVo enterpriseLocation = PieVo.newInstance()
                        .title(title)
                        .legend(PieVo.LegendBean.newInstance().data(legendData).build())
                        .series(Lists.newArrayList(PieVo.SeriesBean.newInstance().data(seriesDataList).build()))
                        .total(seriesDataList.stream().mapToLong(PieVo.SeriesBean.DataBean::getValue).sum())
                        .build();
                res.put("enterpriseLocation", enterpriseLocation);

            }

            System.out.println("-------------我是一个分割线-----------------");

            //与会投资机构企业性质统计
            Map<Long, Long> countByEnterpriseNatureMap = dataList.stream().collect(Collectors.groupingBy(IplYzgtMain::getEnterpriseNature, Collectors.counting()));
            if(MapUtils.isEmpty(countByEnterpriseNatureMap)) {
                res.put("enterpriseNature", null);
            }else {
                //所有的企业性质
                List<SysCfg> enterpriseNatureList = cfgService.list(new LambdaQueryWrapper<SysCfg>().eq(SysCfg::getCfgType, 6));
                List<String> enterpriseNatureLegendData = Lists.newArrayList();
                //seriesData
                List<PieVo.SeriesBean.DataBean> enterpriseNatureSeriesDataList = Lists.newArrayList();
                for (SysCfg cfg : enterpriseNatureList) {
                    Long natureCount = countByEnterpriseNatureMap.getOrDefault(cfg.getId(), 0L);
                    if(natureCount != 0L){
                        enterpriseNatureLegendData.add(cfg.getCfgVal());
                        PieVo.SeriesBean.DataBean seriesData = PieVo.SeriesBean.DataBean
                                .newInstance()
                                .name(cfg.getCfgVal())
                                .value(natureCount)
                                .build();
                        enterpriseNatureSeriesDataList.add(seriesData);
                    }

                }
                PieVo enterpriseNature = PieVo.newInstance()
                        .title(title)
                        .legend(PieVo.LegendBean.newInstance().data(enterpriseNatureLegendData).build())
                        .series(Lists.newArrayList(PieVo.SeriesBean.newInstance().data(enterpriseNatureSeriesDataList).build()))
                        .total(enterpriseNatureSeriesDataList.stream().mapToLong(PieVo.SeriesBean.DataBean::getValue).sum())
                        .build();
                res.put("enterpriseNature", enterpriseNature);
            }



        } else {
            res.put("enterpriseScale", null);
            res.put("enterpriseLocation", null);
            res.put("enterpriseNature", null);
        }

        return res;
    }

    /**
     * 与会媒体基本情况
     *
     * @param timeMap 时间map
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author JH
     * @date 2019/10/30 10:10
     */
    public Map<String, Object> getParticipateMediaInfo(Map<String, Long> timeMap,String title) {

        //结果
        Map<String, Object> res = new HashMap<>();
        //二次打包数据
        List<IpaManageMain> ipaList = ipaManageMainService.list(new LambdaQueryWrapper<IpaManageMain>().ge(IpaManageMain::getGmtCreate, timeMap.get("beginTime")).le(IpaManageMain::getGmtCreate, timeMap.get("endTime")));
        if (CollectionUtils.isNotEmpty(ipaList)) {
            List<String> collect = ipaList.stream().filter(n-> StringUtils.isNotBlank(n.getParticipateMedia())).map(IpaManageMain::getParticipateMedia).collect(Collectors.toList());
            List<Long> mediaIdList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(collect)) {
                for (String s : collect) {
                    String[] mediaIds = s.split(",");
                    for (String id : mediaIds) {
                        mediaIdList.add(Long.parseLong(id));
                    }
                }
            }
            //key 媒体id ,value 出现的次数
            Map<Long, Integer> idCountMap = new HashMap<>();
            for (long id : mediaIdList) {
                idCountMap.put(id, idCountMap.getOrDefault(id, 0) + 1);
            }
            //所有的媒体
            List<MediaManager> allMediaList = mediaManagerService.list(new LambdaQueryWrapper<MediaManager>().eq(MediaManager::getStatus, YesOrNoEnum.YES.getType()));
            //被使用过的媒体
            List<MediaManager> mediaList = allMediaList.stream().filter(n -> idCountMap.getOrDefault(n.getId(), 0) != 0).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(mediaList)) {
                res.put("cylinderVo",null);
            }else {
                //key 媒体type ,value 出现的次数
                Map<Long, Integer> typeCountMap = new HashMap<>();
                for (MediaManager mediaManager : mediaList) {
                    typeCountMap.put(mediaManager.getMediaType(), typeCountMap.getOrDefault(mediaManager.getMediaType(), 0) + 1);
                }
                //出现的媒体类型集合
                Set<Long> typeSet = typeCountMap.keySet();
                List<String> xAxisData = Lists.newArrayList();
                List<Integer> seriesData = Lists.newArrayList();

                for (Long typeId : typeSet) {
                    Dic dic = dicUtils.getDicByCode(DicConstants.MEDIA_TYPE, typeId.toString());
                    xAxisData.add(dic.getDicValue());
                    seriesData.add(typeCountMap.get(typeId));
                }

                CylinderVo cylinderVo = CylinderVo.newInstance()
                        .title(title)
                        .seriesData(seriesData)
                        .xAxisData(xAxisData)
                        .build();
                res.put("cylinderVo",cylinderVo);
            }

        }else {
            res.put("cylinderVo",null);
        }
        return res;
    }


}
