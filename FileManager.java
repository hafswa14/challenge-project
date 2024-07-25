package com.challenge.server;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public List<Question> readQuestionsFromExcel(String filePath) throws IOException {
        List<Question> questions = new ArrayList<>();
        FileInputStream file = new FileInputStream(new File(filePath));

        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) { // Skip the header row
                continue;
            }
            String questionText = getStringValue(row.getCell(0));
            String answer = getStringValue(row.getCell(1));
            int marks = (int) getNumericCellValue(row.getCell(2));
            questions.add(new Question(questionText, answer, marks));
        }
        workbook.close();
        return questions;
    }

    private String getStringValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                throw new IllegalArgumentException("Unsupported cell type: " + cell.getCellType());
        }
    }

    private double getNumericCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Cannot parse numeric value from cell: " + cell.getStringCellValue());
                }
            default:
                throw new IllegalArgumentException("Unsupported cell type: " + cell.getCellType());
        }
    }
}
