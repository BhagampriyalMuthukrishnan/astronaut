import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NotificationManager {
    private Timer timer;

    public NotificationManager() {
        this.timer = new Timer(true);
    }

    public void scheduleReminder(Task task) {
        LocalTime taskTime = LocalTime.parse(task.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Reminder: " + task.getDescription() + " starts at " + task.getStartTime());
            }
        }, java.sql.Time.valueOf(taskTime).getTime());
    }
}