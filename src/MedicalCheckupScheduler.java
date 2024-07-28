import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MedicalCheckupScheduler {
    private static final LocalTime CHECKUP_START_TIME = LocalTime.of(10, 0);
    private static final LocalTime CHECKUP_END_TIME = LocalTime.of(11, 0);

    public static boolean isCheckupScheduled(List<Task> tasks) {
        return tasks.stream()
                .anyMatch(task -> task.getDescription().equalsIgnoreCase("medical checkup") &&
                        isMedicalCheckupTimeValid(task));
    }

    private static boolean isMedicalCheckupTimeValid(Task task) {
        LocalTime startTime = LocalTime.parse(task.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        return !startTime.isBefore(CHECKUP_START_TIME) && !startTime.isAfter(CHECKUP_END_TIME);
    }
}