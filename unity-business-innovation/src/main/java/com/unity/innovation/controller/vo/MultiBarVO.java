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
    private List<YAxisBean> yAxis;

    private List<SeriesBean> series;
    private String title;


    @Builder(builderMethodName = "newInstance")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LegendBean {
        private List<String> data;
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
        private AxisPointerBean axisPointer;

        public AxisPointerBean getAxisPointer() {
            return axisPointer;
        }

        public void setAxisPointer(AxisPointerBean axisPointer) {
            this.axisPointer = axisPointer;
        }
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
    public static class YAxisBean {
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
    public static class AxisPointerBean {

        /**
         * type : shadow
         */

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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
        private List<? extends Number> data;
    }
}
