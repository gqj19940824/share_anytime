package com.unity.innovation.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * create by qinhuan at 2019/10/28 7:13 下午
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MultiBarVO {

    private LegendBean legend;
    private List<XAxisBean> xAxis;
    private List<SeriesBean> series;

    public LegendBean getLegend() {
        return legend;
    }

    public void setLegend(LegendBean legend) {
        this.legend = legend;
    }

    public List<XAxisBean> getXAxis() {
        return xAxis;
    }

    public void setXAxis(List<XAxisBean> xAxis) {
        this.xAxis = xAxis;
    }

    public List<SeriesBean> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesBean> series) {
        this.series = series;
    }

    @Builder(builderMethodName = "newInstance")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LegendBean {
        private List<String> data;

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }

    @Builder(builderMethodName = "newInstance")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class XAxisBean {
        /**
         * type : category
         * data : ["周一","周二","周三","周四","周五","周六","周日"]
         */

        private String type;
        private List<String> data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }

    @Builder(builderMethodName = "newInstance")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SeriesBean {
        /**
         * name : 邮件营销
         * type : bar
         * stack : 广告
         * data : [120,132,101,134,90,230,210]
         */

        private String name;
        private String type;
        private String stack;
        private List<Integer> data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStack() {
            return stack;
        }

        public void setStack(String stack) {
            this.stack = stack;
        }

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
            this.data = data;
        }
    }
}
