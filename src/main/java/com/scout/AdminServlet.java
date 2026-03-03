package com.scout;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Admin Servlet
 * GET  /admin/getQuestions   → returns question list as JSON (used by index.html to load read-only questions)
 * POST /admin/saveQuestions  → saves the question list to the "Questions" sheet in Excel (used by admin.html)
 *
 * Questions are stored in a dedicated "Questions" sheet in ~/Documents/FRC_Scouting_App.xlsx
 * Column A = question text, one row per question (starting row 1, no header needed)
 */
@WebServlet({"/admin/getQuestions", "/admin/saveQuestions"})
public class AdminServlet extends HttpServlet {

    private static final String FILE_PATH =
        System.getProperty("user.home") + "/Documents/FRC_Scouting_App.xlsx";
    private static final String QUESTIONS_SHEET = "Questions";
    private static final String RESPONSES_SHEET = "Responses";

    // GET /admin/getQuestions - returns JSON array of question strings
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<String> questions = loadQuestions();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < questions.size(); i++) {
            // Escape quotes inside question text
            String escaped = questions.get(i).replace("\\", "\\\\").replace("\"", "\\\"");
            json.append("\"").append(escaped).append("\"");
            if (i < questions.size() - 1) json.append(",");
        }
        json.append("]");

        response.getWriter().write(json.toString());
    }

    // POST /admin/saveQuestions - saves questions[] array to the Questions sheet
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String[] questions = request.getParameterValues("question");

        if (questions == null || questions.length == 0) {
            response.getWriter().println("No questions provided.");
            return;
        }

        try {
            File f = new File(FILE_PATH);
            Workbook workbook;

            if (f.exists()) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    workbook = new XSSFWorkbook(fis);
                }
            } else {
                workbook = new XSSFWorkbook();
            }

            // Remove old Questions sheet if it exists, then recreate it
            int sheetIndex = workbook.getSheetIndex(QUESTIONS_SHEET);
            if (sheetIndex >= 0) {
                workbook.removeSheetAt(sheetIndex);
            }
            Sheet sheet = workbook.createSheet(QUESTIONS_SHEET);

            // Ensure Responses sheet also exists
            if (workbook.getSheetIndex(RESPONSES_SHEET) < 0) {
                Sheet responses = workbook.createSheet(RESPONSES_SHEET);
                Row header = responses.createRow(0);
                header.createCell(0).setCellValue("Event");
                header.createCell(1).setCellValue("Team");
                header.createCell(2).setCellValue("Match");
                header.createCell(3).setCellValue("Question");
                header.createCell(4).setCellValue("Answer");
            }

            // Write each question to its own row in column A
            for (int i = 0; i < questions.length; i++) {
                String q = questions[i].trim();
                if (!q.isEmpty()) {
                    sheet.createRow(i).createCell(0).setCellValue(q);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(f)) {
                workbook.write(fos);
            }
            workbook.close();

            System.out.println("Admin saved " + questions.length + " question(s) to Questions sheet.");
            response.sendRedirect("/scoutingapp/admin.html?saved=true");

        } catch (Exception e) {
            response.getWriter().println("Error saving questions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper: read all questions from the Questions sheet
    static List<String> loadQuestions() {
        List<String> list = new ArrayList<>();
        File f = new File(System.getProperty("user.home") + "/Documents/FRC_Scouting_App.xlsx");
        if (!f.exists()) return list;

        try (FileInputStream fis = new FileInputStream(f);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Questions");
            if (sheet == null) return list;
            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && !cell.getStringCellValue().trim().isEmpty()) {
                    list.add(cell.getStringCellValue().trim());
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading questions: " + e.getMessage());
        }
        return list;
    }
}
