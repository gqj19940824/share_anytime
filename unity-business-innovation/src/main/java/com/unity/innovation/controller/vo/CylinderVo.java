package com.unity.innovation.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName CylinderVo
 * @Description 圆柱表
 * @Author JH
 * @Date 2019/10/29 16:47
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CylinderVo {
    private List<String>  xAxisData;
    private List<Integer>  seriesData;

    public List<String> getxAxisData() {
        return xAxisData;
    }

    public void setxAxisData(List<String> xAxisData) {
        this.xAxisData = xAxisData;
    }

    public List<Integer> getSeriesData() {
        return seriesData;
    }

    public void setSeriesData(List<Integer> seriesData) {
        this.seriesData = seriesData;
    }
}
