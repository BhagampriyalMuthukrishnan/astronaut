import java.util.ArrayList;
import java.util.List;

public class EmergencyScheduler {
    private List<Task> emergencyTasks;

    public EmergencyScheduler() {
        this.emergencyTasks = new ArrayList<>();
    }

    public void addEmergencyTask(Task task) {
        emergencyTasks.add(task);
    }

    public List<Task> getEmergencyTasks() {
        return emergencyTasks;
    }
}