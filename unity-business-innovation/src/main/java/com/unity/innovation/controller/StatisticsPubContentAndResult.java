package com.unity.innovation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.pojos.SystemResponse;
import com.unity.innovation.controller.vo.MultiBarVO;
import com.unity.innovation.entity.IplEsbMain;
import com.unity.innovation.entity.IplOdMain;
import com.unity.innovation.entity.IplSatbMain;
import com.unity.innovation.entity.generated.IplDarbMain;
import com.unity.innovation.enums.SourceEnum;
import com.unity.innovation.service.IplDarbMainServiceImpl;
import com.unity.innovation.service.IplEsbMainServiceImpl;
import com.unity.innovation.service.IplOdMainServiceImpl;
import com.unity.innovation.service.IplSatbMainServiceImpl;
import com.unity.innovation.util.InnovationUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * <p>
 * create by qinhuan at 2019/10/28 4:40 下午
 */
@RestController
@RequestMapping("/statisticsPubContentAndResult")
public class StatisticsPubContentAndResult extends BaseWebController {

    @Resource
    private IplDarbMainServiceImpl iplDarbMainService;
    @Resource
    private IplSatbMainServiceImpl iplSatbMainService;
    @Resource
    private IplOdMainServiceImpl iplOdMainService;
    @Resource
    private IplEsbMainServiceImpl iplEsbMainService;

    @PostMapping("/")
    public Mono<ResponseEntity<SystemResponse<Object>>> dutyUpdateStatus(@RequestBody Map<String, String> map) {
        Long start = null;
        String startDate = MapUtils.getString(map, "startDate");
        if (StringUtils.isNotBlank(startDate)) {
            start = InnovationUtil.getFirstTimeInMonth(startDate, true);
        }
        Long end = null;
        String endDate = MapUtils.getString(map, "endDate");
        if (StringUtils.isNotBlank(endDate)) {
            end = InnovationUtil.getFirstTimeInMonth(endDate, false);
        }

        // 30成长目标投资（科技局）
        int satbSelfCount = iplSatbMainService.count(getIplSatbQw(SourceEnum.SELF.getId(), start, end));
        int satbEnterpriseCount = iplSatbMainService.count(getIplSatbQw(SourceEnum.ENTERPRISE.getId(), start, end));

        // 40高端才智需求（组织部）
        int odSelfCount = iplOdMainService.count(getIplOdQw(SourceEnum.SELF.getId(), start, end));
        int odEnterpriseCount = iplOdMainService.count(getIplOdQw(SourceEnum.ENTERPRISE.getId(), start, end));

        // 10城市创新合作(发改局)
        int darbSelfCount = iplDarbMainService.count(getIplDarbQw(SourceEnum.SELF.getId(), start, end));
        int darbEnterpriseCount = iplDarbMainService.count(getIplDarbQw(SourceEnum.ENTERPRISE.getId(), start, end));

        // 20企业创新发展（企服局）
        int esbSelfCount = iplEsbMainService.count(getIplEsbQw(SourceEnum.SELF.getId(), start, end));
        int esbEnterpriseCount = iplEsbMainService.count(getIplEsbQw(SourceEnum.ENTERPRISE.getId(), start, end));



        MultiBarVO multiBarVO = MultiBarVO.newInstance()
                .legend(
                        MultiBarVO.LegendBean.newInstance().data(
                                Arrays.asList("职能局代企业上报的需求", "企业自主上报的需求")
                        ).build()
                ).xAxis(
                        Arrays.asList(MultiBarVO.XAxisBean.newInstance()
                                .type("category")
                                .data(Arrays.asList("成长目标投资", "高端才智需求", "城市创新合作", "企业创新发展")).build())
                ).series(
                        Arrays.asList(
                                MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("职能局代企业上报的需求")
                                        .data(Arrays.asList(satbSelfCount, odSelfCount, darbSelfCount, esbSelfCount)).build()
                                , MultiBarVO.SeriesBean.newInstance().type("bar").stack("name").name("企业自主上报的需求")
                                        .data(Arrays.asList(satbEnterpriseCount, odEnterpriseCount, darbEnterpriseCount, esbEnterpriseCount)).build())).build();

        return success(multiBarVO);
    }

    private LambdaQueryWrapper<IplDarbMain> getIplDarbQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplDarbMain> qw = new LambdaQueryWrapper<>();
        qw.eq(IplDarbMain::getSource, source);
        if (start != null) {
            qw.ge(IplDarbMain::getGmtCreate, start);
        }
        if (end != null) {
            qw.le(IplDarbMain::getGmtCreate, end);
        }
        return qw;
    }

    private LambdaQueryWrapper<IplEsbMain> getIplEsbQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplEsbMain> iplDarbQw = new LambdaQueryWrapper<>();
        iplDarbQw.eq(IplEsbMain::getSource, source);
        if (start != null) {
            iplDarbQw.ge(IplEsbMain::getGmtCreate, start);
        }
        if (end != null) {
            iplDarbQw.le(IplEsbMain::getGmtCreate, end);
        }
        return iplDarbQw;
    }

    private LambdaQueryWrapper<IplOdMain> getIplOdQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplOdMain> iplDarbQw = new LambdaQueryWrapper<>();
        iplDarbQw.eq(IplOdMain::getSource, source);
        if (start != null) {
            iplDarbQw.ge(IplOdMain::getGmtCreate, start);
        }
        if (end != null) {
            iplDarbQw.le(IplOdMain::getGmtCreate, end);
        }
        return iplDarbQw;
    }

    private LambdaQueryWrapper<IplSatbMain> getIplSatbQw(Integer source, Long start, Long end) {

        LambdaQueryWrapper<IplSatbMain> qw = new LambdaQueryWrapper<>();
        qw.eq(IplSatbMain::getSource, source);
        if (start != null) {
            qw.ge(IplSatbMain::getGmtCreate, start);
        }
        if (end != null) {
            qw.le(IplSatbMain::getGmtCreate, end);
        }
        return qw;
    }


}