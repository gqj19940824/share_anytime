package com.unity.innovation.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * create by qinhuan at 2019/10/30 10:34 上午
 */
@Builder(builderMethodName = "newInstance")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PieVoByDoc {
    /**
     * legend : {"orient":"vertical","x":"left","data":["直接访问","邮件营销","联盟广告","视频广告","搜索引擎"]}
     * data : [{"value":335,"name":"直接访问"}]
     */

    private LegendBean legend;
    private List<DataBean> data;

    @Builder(builderMethodName = "newInstance")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LegendBean {
        /**
         * orient : vertical
         * x : left
         * data : ["直接访问","邮件营销","联盟广告","视频广告","搜索引擎"]
         */

        @Builder.Default
        private String orient="vertical";
        @Builder.Default
        private String x="left";
        private List<String> data;
    }

    @Builder(builderMethodName = "newInstance")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * value : 335
         * name : 直接访问
         */

        private Object value;
        private String name;
    }
}
