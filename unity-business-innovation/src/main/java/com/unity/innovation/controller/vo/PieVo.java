package com.unity.innovation.controller.vo;

import lombok.*;

import java.util.List;

/**
 * @ClassName PieVo
 * @Description 饼图
 * @Author JH
 * @Date 2019/10/29 15:04
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PieVo {

    private String title;
    private LegendBean legend;
    private List<SeriesBean> series;
    private Integer count;
    private Long total;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LegendBean getLegend() {
        return legend;
    }

    public void setLegend(LegendBean legend) {
        this.legend = legend;
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
            private Long value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Long getValue() {
                return value;
            }

            public void setValue(Long value) {
                this.value = value;
            }
        }
    }
}
