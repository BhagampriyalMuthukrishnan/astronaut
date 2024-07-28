import java.util.List;

public class ExerciseTime {
    private static final int MIN_EXERCISE_MINUTES = 120;

    public static boolean isExerciseTimeSufficient(List<Task> tasks) {
        int totalExerciseMinutes = tasks.stream()
            .filter(task -> task.getDescription().equalsIgnoreCase("exercise"))
            .mapToInt(task -> calculateMinutes(task.getStartTime(), task.getEndTime()))
            .sum();
        return totalExerciseMinutes >= MIN_EXERCISE_MINUTES;
    }

    private static int calculateMinutes(String startTime, String endTime) {
        String[] start = startTime.split(":");
        String[] end = endTime.split(":");
        int startHour = Integer.parseInt(start[0]);
        int startMinute = Integer.parseInt(start[1]);
        int endHour = Integer.parseInt(end[0]);
        int endMinute = Integer.parseInt(end[1]);
        return (endHour - startHour) * 60 + (endMinute - startMinute);
    }
}