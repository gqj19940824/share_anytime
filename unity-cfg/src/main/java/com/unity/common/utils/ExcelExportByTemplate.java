package com.unity.common.utils;

import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * create by qinhuan at 2019/10/15 4:15 下午
 */
@Slf4j
public class ExcelExportByTemplate {
    public static void setData(List<List<Object>> dataList, String header, String footer, XSSFWorkbook wb, int startRowIndex) {
        //读取了模板内图表所需数据
        XSSFSheet sheet = wb.getSheetAt(0);
        wb.setSheetName(0, header);
        XSSFRow headerRow = sheet.getRow(0);
        headerRow.getCell(0).setCellValue(header);
        int dataListSize = dataList.size();
        int rowNum = 0;
        for (; rowNum<dataListSize; rowNum++) {
            List<Object> rowData = dataList.get(rowNum);
            int colSize = rowData.size();
            XSSFRow row = sheet.createRow(rowNum + startRowIndex);
            for (int celNum = 0; celNum<colSize; celNum++) {
                XSSFCell cell = row.createCell(celNum);
                setCellData(cell, rowData.get(celNum));
            }
        }

        XSSFRow row = sheet.createRow(startRowIndex + rowNum);
        row.createCell(0).setCellValue("备 注：" + footer);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(startRowIndex +rowNum, startRowIndex+rowNum, 0, headerRow.getLastCellNum()-1);
        sheet.addMergedRegion(cellRangeAddress);
    }

    /**
     * 给单元格设置值
     *
     * @return 单元格对象
     * @author zhangxiaogang
     * @since 2018/9/3 17:00
     */
    public static XSSFCell setCellData(XSSFCell cell, Object val) {
        if (val == null) {
            cell.setCellValue("");
        } else if (val instanceof String) {
            cell.setCellValue((String) val);
        } else if (val instanceof Integer) {
            cell.setCellValue((Integer) val);
        } else if (val instanceof Long) {
            cell.setCellValue((Long) val);
        } else if (val instanceof Double) {
            cell.setCellValue((Double) val);
        } else if (val instanceof Float) {
            cell.setCellValue((Float) val);
        } else {
            throw UnityRuntimeException.newInstance().code(SystemResponse.FormalErrorCode.ORIGINAL_DATA_ERR).message("数据类型不支持").build();
        }
        return cell;
    }

    public static XSSFWorkbook getWorkBook(String templateAddress) {
        //报表模板路径
        Resource r = new ClassPathResource(templateAddress);
        XSSFWorkbook wb = null;
        try {
            //excel模板路径
            InputStream in = new FileInputStream(r.getFile());
            //读取excel模板
            wb = new XSSFWorkbook(in);
        } catch (FileNotFoundException e) {
            log.error("输入流异常", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("读取Excel模板异常", e);
        }
        return wb;
    }

    public static void download(HttpServletRequest request, HttpServletResponse response, XSSFWorkbook wb, String fileName){
        try {
            //下载时文件的名称
            String filename = getFileName(request, fileName + ".xlsx");
            //文件类型
            response.setContentType("application/vnd.ms-excel");
            //导出文件的信息
            response.setHeader("Content-disposition", "attachment;filename=" + filename);
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (IOException e) {
            log.error("写出Excel IO异常", e);
        }
    }

    public static String getFileName(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        //设置编码
        if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
            filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }
        return filename;
    }
}
