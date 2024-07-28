import java.util.List;

public class FoodSchedule {
    private static final int MEALS_PER_DAY = 3;

    public static boolean isFoodScheduleValid(List<Task> tasks) {
        long mealCount = tasks.stream()
            .filter(task -> task.getDescription().equalsIgnoreCase("breakfast") ||
                            task.getDescription().equalsIgnoreCase("lunch") ||
                            task.getDescription().equalsIgnoreCase("dinner"))
            .count();
        return mealCount == MEALS_PER_DAY;
    }
}