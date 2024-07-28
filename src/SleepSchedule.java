import java.time.LocalTime;

public class SleepSchedule {
    private static final LocalTime SLEEP_START = LocalTime.of(21, 30);
    private static final LocalTime SLEEP_END = LocalTime.of(6, 0);

    public static boolean isSleepTime(LocalTime time) {
        return !time.isBefore(SLEEP_START) || !time.isAfter(SLEEP_END);
    }

    public static boolean isValidTaskTime(LocalTime startTime, LocalTime endTime) {
        return !isSleepTime(startTime) && !isSleepTime(endTime);
    }
}