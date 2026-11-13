package com.flipkart.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ExcelUtility
 * <p>
 * Thin wrapper around Apache POI for reading test data out of, and writing
 * Pass/Fail results back into, an .xlsx workbook. Row 0 is treated as a
 * header row; data rows/cells are addressed 0-indexed, same as POI itself.
 * <p>
 * Each call opens and closes the workbook rather than holding it open across
 * calls - simpler to reason about for a data-driven test loop, at the cost of
 * re-reading the file on every access. Fine for the sheet sizes this
 * framework deals with.
 */
public class ExcelUtility {

    private static final Logger log = LogManager.getLogger(ExcelUtility.class);

    private final String filePath;

    public ExcelUtility(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Index (0-based) of the last row that has data, i.e. the number of data
     * rows below the header when the header is row 0.
     */
    public int getRowCount(String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = getSheetOrThrow(workbook, sheetName);
            return sheet.getLastRowNum();
        } catch (IOException e) {
            log.error("Failed to read row count from {}", filePath, e);
            throw new RuntimeException(e);
        }
    }

    public int getColumnCount(String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = getSheetOrThrow(workbook, sheetName);
            Row header = sheet.getRow(0);
            return header == null ? 0 : header.getLastCellNum();
        } catch (IOException e) {
            log.error("Failed to read column count from {}", filePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads cell [rowNum, colNum] as a String, regardless of its underlying
     * Excel cell type. Returns "" for a missing row/cell rather than null, so
     * callers can safely call .isEmpty()/.trim() without a null check.
     */
    public String getCellData(String sheetName, int rowNum, int colNum) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = getSheetOrThrow(workbook, sheetName);
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                return "";
            }
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                return "";
            }
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    return String.valueOf((long) cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return "";
            }
        } catch (IOException e) {
            log.error("Failed to read cell [{}, {}] from {}", rowNum, colNum, filePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes value into [rowNum, colNum] - creating the row and/or cell if
     * they don't exist yet - and persists the workbook back to filePath.
     * Used to write "Pass"/"Fail" into the Status column after each
     * data-driven iteration.
     */
    public void setCellData(String sheetName, int rowNum, int colNum, String value) {
        XSSFWorkbook workbook;
        try (FileInputStream fis = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            log.error("Failed to open {} for writing", filePath, e);
            throw new RuntimeException(e);
        }

        try {
            Sheet sheet = getSheetOrThrow(workbook, sheetName);
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }
            cell.setCellValue(value);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            log.info("Wrote '{}' to [{}, {}] in {}", value, rowNum, colNum, filePath);
        } catch (IOException e) {
            log.error("Failed to write cell [{}, {}] in {}", rowNum, colNum, filePath, e);
            throw new RuntimeException(e);
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
                // nothing more we can do
            }
        }
    }

    private Sheet getSheetOrThrow(XSSFWorkbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + filePath);
        }
        return sheet;
    }
}
