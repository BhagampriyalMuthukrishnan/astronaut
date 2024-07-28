import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DailyReportGenerator {
    private static final String REPORT_FILE_PATH = "daily_reports.txt";

    public void generateReport(List<Task> tasks) {
        StringBuilder report = new StringBuilder("Daily Report:\n\n");

        int completedTasks = 0;
        int totalTasks = tasks.size();
        int exerciseMinutes = 0;
        int breaks = 0;
        boolean hadBreakfast = false;
        boolean hadLunch = false;
        boolean hadDinner = false;

        for (Task task : tasks) {
            if (task.isComplete()) {
                completedTasks++;
            }
            if (task.getDescription().equalsIgnoreCase("exercise")) {
                exerciseMinutes += calculateMinutes(task.getStartTime(), task.getEndTime());
            }
            if (task.getDescription().equalsIgnoreCase("break")) {
                breaks++;
            }
            if (task.getDescription().equalsIgnoreCase("breakfast")) {
                hadBreakfast = true;
            }
            if (task.getDescription().equalsIgnoreCase("lunch")) {
                hadLunch = true;
            }
            if (task.getDescription().equalsIgnoreCase("dinner")) {
                hadDinner = true;
            }
        }

        report.append("Completed tasks: ").append(completedTasks).append("/").append(totalTasks).append("\n");
        report.append("Exercise time: ").append(exerciseMinutes).append(" minutes\n");
        report.append("Number of breaks: ").append(breaks).append("\n\n");

        if (exerciseMinutes < 120) {
            report.append("Warning: Less than 2 hours of exercise completed.\n");
        }
        if (breaks < 2) {
            report.append("Warning: Less than 2 breaks taken.\n");
        }
        if (!hadBreakfast || !hadLunch || !hadDinner) {
            report.append("Warning: Missing one or more main meals (breakfast, lunch, dinner).\n");
        }

        saveReport(report.toString());
    }

    private void saveReport(String report) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILE_PATH))) {
            writer.write(report);
            System.out.println("Daily report saved to " + REPORT_FILE_PATH);
        } catch (IOException e) {
            System.out.println("Error saving daily report: " + e.getMessage());
        }
    }

    private int calculateMinutes(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
        return (int) java.time.Duration.between(start, end).toMinutes();
    }
}