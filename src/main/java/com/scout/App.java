package com.scout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Scouting App: Hello world!
 * Reading my first Excel file!
 * Writing directly to Excel without HTML!
 * Writes directly to: src/main/java/com/scout/FRC Scouting App.xlsx (in the project code folder)
 */
public class App 
{
    public static void main( String[] args )
    {
        String fileLocation = System.getProperty("user.home") + "/Documents/FRC_Scouting_App.xlsx";
        
        try (FileInputStream file = new FileInputStream(new File(fileLocation));
            Workbook workbook = new XSSFWorkbook(file)) {
            
            System.out.println("Workbook opened successfully at: " + fileLocation);
            System.out.println("Number of sheets: " + workbook.getNumberOfSheets());
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // Check if headers exist (row 0)
            Row headerRow = sheet.getRow(0);
            boolean hasHeaders = false;
            
            if (headerRow != null && headerRow.getCell(0) != null && headerRow.getCell(0).getStringCellValue().equals("Question")) {
                hasHeaders = true;
                System.out.println("Headers already exist!");
            }
            
            // If no headers, create them
            if (!hasHeaders) {
                if (headerRow == null) {
                    headerRow = sheet.createRow(0);
                }
                headerRow.createCell(0).setCellValue("Question");
                headerRow.createCell(1).setCellValue("Answer");
                System.out.println("Headers added: Question, Answer");
            }
            
            // Read and display existing data
            Map<Integer, List<String>> data = new HashMap<>();
            int i = 0;
            for (Row row : sheet) {
                data.put(i, new ArrayList<String>());
                for (Cell cell : row) {
                    data.get(i).add(cell.toString());
                }
                if (!data.get(i).isEmpty()) {
                    System.out.println("Row " + i + " data: " + data.get(i));
                }
                i++;
            }
            
            // Save the workbook with headers
            FileOutputStream outFile = new FileOutputStream(new File(fileLocation));
            workbook.write(outFile);
            outFile.close();
            
            System.out.println("\nFile saved successfully!");
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}