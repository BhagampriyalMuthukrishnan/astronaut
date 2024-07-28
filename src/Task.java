import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Task {
    private String description;
    private String startTime;
    private String endTime;
    private String priority;
    private String teamMembers;
    private boolean isComplete;

    public Task(String description, String startTime, String endTime, String priority, String teamMembers) {
        TaskValidator.validate(description, startTime, endTime, priority);

        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.teamMembers = teamMembers;
        this.isComplete = false;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getPriority() {
        return priority;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public String toString() {
        return startTime + " - " + endTime + ": " + description + " [" + priority + "]";
    }
}

class TaskValidator {
    public static void validate(String description, String startTime, String endTime, String priority) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        if (!isValidTime(startTime)) {
            throw new IllegalArgumentException("Invalid start time");
        }

        if (!isValidTime(endTime)) {
            throw new IllegalArgumentException("Invalid end time");
        }

        if (!isValidPriority(priority)) {
            throw new IllegalArgumentException("Invalid priority");
        }
    }

    private static boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isValidPriority(String priority) {
        return priority.equalsIgnoreCase("High") ||
                priority.equalsIgnoreCase("Medium") ||
                priority.equalsIgnoreCase("Low");
    }
}
