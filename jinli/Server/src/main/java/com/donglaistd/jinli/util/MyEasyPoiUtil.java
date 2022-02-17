package com.donglaistd.jinli.util;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

public class MyEasyPoiUtil {

    public static <T> Map<Long,List<T>> importMultipleSheetExcel(MultipartFile multipartFile, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        HSSFWorkbook sheets = null;
        Map<Long, List<T>> dataMap = new LinkedHashMap<>();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(multipartFile.getInputStream());
            sheets = new HSSFWorkbook(fs);
            int sheetsNum = sheets.getNumberOfSheets();
            for (int i = 0; i < sheetsNum; i++) {
                long time = TimeUtil.strTimeStampToTimeStamp(sheets.getSheetName(i));
                if(time<=0) continue;
                List<T> data = importExcelBySheet(multipartFile.getInputStream(), i, titleRows, headerRows, pojoClass);
                dataMap.put(time, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.clear();
        }
        return dataMap;
    }

    /**
     * 功能描述：根据接收的Excel文件来导入多个sheet,根据索引可返回一个集合
     * @param sheetIndex  导入sheet索引
     * @param titleRows  表标题的行数
     * @param headerRows 表头行数
     * @param pojoClass  Excel实体类
     * @return*/
    public static <T> List<T> importExcelBySheet(InputStream fileStream, int sheetIndex, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        // 根据file得到Workbook,主要是要根据这个对象获取,传过来的excel有几个sheet页
        ImportParams params = new ImportParams();
        // 第几个sheet页
        params.setStartSheetIndex(sheetIndex);
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(fileStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static boolean isRowEmpty(Row row){
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
        if (cell != null && cell.getCellType() != CellType.BLANK)
            return false;
        }
        return true;
    }
}
