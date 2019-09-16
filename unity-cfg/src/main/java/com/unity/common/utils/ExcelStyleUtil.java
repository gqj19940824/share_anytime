package com.unity.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出excel样式工具
 * <p>
 * create by zhangxiaogang at 2018/11/17 16:05
 */
public class ExcelStyleUtil {

    /**
     * 自定义结果
     *
     * @author zhangxiaogang
     * @since 2018/11/17 17:17
     */
    private static Map<Integer, String> getResultMap() {
        HashMap<Integer, String> resultMap = new HashMap<>();
        resultMap.put(1, "优秀");
        resultMap.put(2, "良好");
        resultMap.put(3, "合格");
        resultMap.put(4, "不合格");
        return resultMap;
    }


    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     * @author zhangxiaogang
     * @since 2018/9/3 17:00
     */
    public static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = Maps.newHashMap();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        style.setWrapText(true);
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBold(true);
        style.setFont(titleFont);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setWrapText(true);
        style.setFont(dataFont);
        style.setDataFormat(wb.createDataFormat().getFormat("@"));
        styles.put("data", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        style.setWrapText(true);
        style.setDataFormat(wb.createDataFormat().getFormat("@"));
        styles.put("description", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        style.setFont(headerFont);
        styles.put("header", style);
        return styles;
    }



    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     * @author zhangxiaogang
     * @since 2018/9/3 17:00
     */
    public static Map<String, CellStyle> createProjectStyles(Workbook wb) {
        Map<String, CellStyle> styles = Maps.newHashMap();
        CellStyle title = wb.createCellStyle();
        title.setAlignment(HorizontalAlignment.CENTER);
        title.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        title.setWrapText(true);
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 10);
        title.setFont(titleFont);
        title.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        title.setFillPattern(CellStyle.SOLID_FOREGROUND);
        title.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        title.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        title.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        title.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        title.setDataFormat(wb.createDataFormat().getFormat("@"));

        styles.put("title", title);

        title = wb.createCellStyle();
        title.setVerticalAlignment(VerticalAlignment.CENTER);
        title.setAlignment(HorizontalAlignment.CENTER);
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        title.setWrapText(true);
        title.setFont(dataFont);
        title.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        title.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        title.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        title.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        title.setDataFormat(wb.createDataFormat().getFormat("@"));

        styles.put("data", title);

        return styles;
    }



    public void exportExacel() {

        List<Map<String, List<String>>> list = Lists.newArrayList();
        Map<String, List<String>> map1 = new HashMap<>();

        List<String> headerList = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            headerList.add("表头" + i);
        }
        map1.put("header", headerList);
        List<String> contentList = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            contentList.add("数据" + i);
        }
        map1.put("content", contentList);
        list.add(map1);

        Map<String, List<String>> map2 = new HashMap<>();
        List<String> headerList2 = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            headerList2.add("表头" + i);
        }
        map2.put("header", headerList2);
        List<String> contentList2 = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            contentList2.add("数据" + i);
        }
        map2.put("content", contentList2);
        list.add(map2);

    }

    public static void main(String[] args){
        ExcelStyleUtil exportExcelUtil = new ExcelStyleUtil();
        exportExcelUtil.exportExacel();
    }
}
