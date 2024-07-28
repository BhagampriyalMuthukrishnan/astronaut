import java.util.List;

public class BreakSchedule {
    private static final int BREAKS_PER_DAY = 2;

    public static boolean isBreakScheduleValid(List<Task> tasks) {
        long breakCount = tasks.stream()
            .filter(task -> task.getDescription().equalsIgnoreCase("break"))
            .count();
        return breakCount >= BREAKS_PER_DAY;
    }
}