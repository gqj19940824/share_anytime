package com.unity.common.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.util.Map;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * <p>
 * create by zhangxiaogang at 2019/4/18 13:45
 */
public class ExcelOutPutUtil {


    public static void main(String[] args) throws Exception {
        FileOutputStream out = null;
        try {
            // excel对象
            HSSFWorkbook wb = new HSSFWorkbook();
            // sheet对象
            HSSFSheet sheet = wb.createSheet("sheet1");
            // 输出excel对象
            dealExcelHeader(sheet, wb);
            out = new FileOutputStream("D:\\test_dir\\table.xls");
            String[] DATA_LIST = new String[] { "男", "女", };
            HSSFDataValidation validate = ExcelOutPutUtil.setBoxs(DATA_LIST,1,1000,3,3);
            //HSSFDataValidation validate = ExcelOutPutUtil.setValidate(1,1000,3,3);
            sheet.addValidationData(validate);

            // 输出excel
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        System.out.println("在D盘成功生成了excel，请去查看");

    }

    private static void dealExcelHeader(Sheet sheet, HSSFWorkbook wb) {
        Row row = sheet.createRow(0);
        Map<String, CellStyle> styleMap = ExcelStyleUtil.createStyles(wb);
        int width = 256 * 15 + 184;
        HSSFDataFormat format = wb.createDataFormat();

        addCellHeaderInfo(sheet,0, row, styleMap.get("header"), width,"公司名称",format);
        addCellHeaderInfo(sheet,1, row, styleMap.get("header"), width,"公司地址",format);
        addCellHeaderInfo(sheet,2, row, styleMap.get("header"), width,"联系方式",format);
        addCellHeaderInfo(sheet,3, row, styleMap.get("header"), width,"所属哨站",format);
    }

   /* *
     * 新增选定的列内容
     *
     * @param sheet     工作sheet
     * @param rowNum    列数
     * @param row       当前行对象
     * @param cellStyle 样式
     * @param width     列宽
     * @param title     内容
     * @author zhangxiaogang
     * @since 2019/4/19 10:13
     * */

    private static void addCellHeaderInfo(Sheet sheet, int rowNum, Row row, CellStyle cellStyle, int width, String title,HSSFDataFormat format) {
        row.setHeightInPoints(30.0f);
        Cell cellDate = row.createCell(rowNum);

        if(rowNum==2) {
            cellStyle.setDataFormat(format.getFormat("@"));
            cellDate.setCellType(CellType.STRING);
        }
        cellDate.setCellStyle(cellStyle);
        cellDate.setCellValue(title);
        sheet.setColumnWidth(rowNum, width);
    }


    /**
     * 数字大小控制：设置单元格只能在1-20之间
     *
     * @param firstRow 开始行数
     * @param lastRow  结束行数
     * @param firstCol 开始列
     * @param lastCol  结束列
     * @author zhangxiaogang
     * @since 2019/4/18 14:21
     */
    public static HSSFDataValidation setValidate(int firstRow, int lastRow, int firstCol, int lastCol) {
        // 创建一个规则：1-100的数字
       /* DVConstraint constraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.TEXT_LENGTH,
                DVConstraint.OperatorType.BETWEEN, "1", "20");*/
        DVConstraint constraint = DVConstraint.createFormulaListConstraint("1234123");
        //DVConstraint.c
        // 设定在哪个单元格生效
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        // 创建规则对象
        return new HSSFDataValidation(regions, constraint);

    }

    /**
     * 下拉框限制
     *
     * @param DATA_LIST 下拉条内容
     * @param firstRow  开始行数
     * @param lastRow   结束行数
     * @param firstCol  开始列
     * @param lastCol   结束列
     * @return 设置对象
     * @author zhangxiaogang
     * @since 2019/4/18 14:20
     */
    public static HSSFDataValidation setBoxs(String[] DATA_LIST, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        //final String[] DATA_LIST = new String[] { "男", "女", };
        DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(DATA_LIST);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        dataValidation.createPromptBox("输入提示", "请从下拉列表中选择所属服务港");
        dataValidation.setShowPromptBox(true);
        return dataValidation;
    }

    public static HSSFDataValidation setDate(short firstRow, short lastRow, short firstCol, short lastCol) {
        CellRangeAddressList addressList = new CellRangeAddressList(0, 1, 0, 2);
        DVConstraint dvConstraint = DVConstraint.createDateConstraint(DVConstraint.OperatorType.BETWEEN, "1900-01-01",
                "5000-01-01", "yyyy-mm-dd");
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        dataValidation.createPromptBox("输入提示", "请填写日期格式");
// 设置输入错误提示信息		
        dataValidation.createErrorBox("日期格式错误提示", "你输入的日期格式不符合'yyyy-mm-dd'格式规范，请重新输入！");
        dataValidation.setShowPromptBox(true);
        return dataValidation;
    }


}
