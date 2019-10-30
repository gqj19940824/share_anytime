package com.unity.innovation.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName RadarVo
 * @Description 雷达图实体
 * @Author JH
 * @Date 2019/10/29 11:23
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RadarVo {
    /**
     * title : {"text":"基础雷达图"}
     * legend : {"data":["预算分配","实际开销"]}
     * radar : {"indicator":[{"name":"销售","max":6500},{"name":"管理","max":16000}]}
     * series : [{"name":"预算 vs 开销","type":"radar","data":[{"value":[4300,10000,28000,35000,50000,19000],"name":"预算分配"},{"value":[5000,14000,28000,31000,42000,21000],"name":"实际开销"}]}]
     */

    private TitleBean title;
    private LegendBean legend;
    private RadarBean radar;
    private List<SeriesBean> series;

    public TitleBean getTitle() {
        return title;
    }

    public void setTitle(TitleBean title) {
        this.title = title;
    }

    public LegendBean getLegend() {
        return legend;
    }

    public void setLegend(LegendBean legend) {
        this.legend = legend;
    }

    public RadarBean getRadar() {
        return radar;
    }

    public void setRadar(RadarBean radar) {
        this.radar = radar;
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
    public static class TitleBean {
        /**
         * text : 基础雷达图
         */

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
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
    public static class RadarBean {
        private List<IndicatorBean> indicator;

        public List<IndicatorBean> getIndicator() {
            return indicator;
        }

        public void setIndicator(List<IndicatorBean> indicator) {
            this.indicator = indicator;
        }

        @Builder(builderMethodName = "newInstance")
        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class IndicatorBean {
            /**
             * name : 销售
             * max : 6500
             */

            private String name;
            private Long max;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Long getMax() {
                return max;
            }

            public void setMax(Long max) {
                this.max = max;
            }
        }
    }
    @Builder(builderMethodName = "newInstance")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SeriesBean {

        /**
         * data : [{"value":[4300,10000,28000,35000,50000,19000],"name":"预算分配"},{"value":[5000,14000,28000,31000,42000,21000],"name":"实际开销"}]
         */

        private List<DataBean> data;

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }
        @Builder(builderMethodName = "newInstance")
        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class DataBean {
            /**
             * value : [4300,10000,28000,35000,50000,19000]
             * name : 预算分配
             */

            private String name;
            private List<Long> value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<Long> getValue() {
                return value;
            }

            public void setValue(List<Long> value) {
                this.value = value;
            }
        }
    }
}
