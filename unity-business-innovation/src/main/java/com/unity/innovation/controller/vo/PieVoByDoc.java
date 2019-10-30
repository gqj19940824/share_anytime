package com.unity.innovation.controller.vo;

import java.util.List;

/**
 * <p>
 * create by qinhuan at 2019/10/30 10:34 上午
 */
public class PieVoByDoc {
    /**
     * legend : {"orient":"vertical","x":"left","data":["直接访问","邮件营销","联盟广告","视频广告","搜索引擎"]}
     * data : [{"value":335,"name":"直接访问"}]
     */

    private LegendBean legend;
    private List<DataBean> data;

    public LegendBean getLegend() {
        return legend;
    }

    public void setLegend(LegendBean legend) {
        this.legend = legend;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class LegendBean {
        /**
         * orient : vertical
         * x : left
         * data : ["直接访问","邮件营销","联盟广告","视频广告","搜索引擎"]
         */

        private String orient;
        private String x;
        private List<String> data;

        public String getOrient() {
            return orient;
        }

        public void setOrient(String orient) {
            this.orient = orient;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }

    public static class DataBean {
        /**
         * value : 335
         * name : 直接访问
         */

        private int value;
        private String name;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
