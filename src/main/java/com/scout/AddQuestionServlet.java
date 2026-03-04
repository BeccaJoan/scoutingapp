package com.scout;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Scouting App: Add Question Servlet
 * This is the file that works with the HTML form at http://localhost:8080/scoutingapp/
 * When you click "Submit All" on the webpage, THIS file receives the data and writes it to Excel!
 * Writes to: ~/Documents/FRC_Scouting_App.xlsx
 * Also handles TBA API requests securely (keeps the API key hidden from the browser)
 */
@WebServlet({"/addQuestion", "/tba/*"})
public class AddQuestionServlet extends HttpServlet {

    // Read the TBA API key from ~/.env (user's home directory)
    private String getTBAKey() {
        try {
            // Look for .env in the user's home directory (~/.env)
            // This works regardless of where the project was cloned
            String envPath = System.getProperty("user.home") + "/.env";
            for (String line : Files.readAllLines(Paths.get(envPath))) {
                if (line.startsWith("TBA_API_KEY=")) {
                    return line.substring("TBA_API_KEY=".length()).trim();
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read .env file: " + e.getMessage());
        }
        return "";
    }

    // Handle TBA API proxy requests (GET) - keeps API key hidden from browser
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo(); // e.g. /events or /event/2026alhu/teams
        String tbaUrl = "https://www.thebluealliance.com/api/v3" + pathInfo;

        HttpURLConnection conn = (HttpURLConnection) new URL(tbaUrl).openConnection();
        conn.setRequestProperty("X-TBA-Auth-Key", getTBAKey());
        conn.setRequestProperty("Accept", "application/json");

        int status = conn.getResponseCode();
        InputStream is = (status == 200) ? conn.getInputStream() : conn.getErrorStream();
        String json = new String(is.readAllBytes());

        response.setContentType("application/json");
        response.setStatus(status);
        // Allow the browser to call this endpoint
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().write(json);
    }

    // Handle form submissions (POST) - scouter submits only answers; questions come from Questions sheet
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String[] answers = request.getParameterValues("answer");
        String event = request.getParameter("event");
        String team  = request.getParameter("team");
        String match = request.getParameter("match");

        // Load question text from the Questions sheet so we can pair them with answers
        java.util.List<String> questions = AdminServlet.loadQuestions();

        String fileLocation = System.getProperty("user.home") + "/Documents/FRC_Scouting_App.xlsx";

        try (FileInputStream fis = new FileInputStream(new File(fileLocation));
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Write to the Responses sheet
            Sheet sheet = workbook.getSheet("Responses");
            if (sheet == null) {
                sheet = workbook.createSheet("Responses");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Event");
                header.createCell(1).setCellValue("Team");
                header.createCell(2).setCellValue("Match");
                header.createCell(3).setCellValue("Question");
                header.createCell(4).setCellValue("Answer");
            }

            // Find next empty row
            int nextRowNum = sheet.getLastRowNum() + 1;
            if (nextRowNum == 0) nextRowNum = 1; // skip header if sheet was just created

            if (answers != null) {
                for (int i = 0; i < answers.length; i++) {
                    String questionText = (i < questions.size()) ? questions.get(i) : "Question " + (i + 1);
                    Row newRow = sheet.createRow(nextRowNum + i);
                    newRow.createCell(0).setCellValue(event != null ? event : "");
                    newRow.createCell(1).setCellValue(team  != null ? team  : "");
                    newRow.createCell(2).setCellValue(match != null ? match : "");
                    newRow.createCell(3).setCellValue(questionText);
                    newRow.createCell(4).setCellValue(answers[i]);
                    System.out.println("Saved: [" + questionText + "] -> " + answers[i]);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(new File(fileLocation))) {
                workbook.write(fos);
            }

            response.sendRedirect("/scoutingapp/?submitted=true");

        } catch (Exception e) {
            response.getWriter().println("Error saving responses: " + e.getMessage());
            e.printStackTrace();
        }
    }
}