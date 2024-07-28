import java.util.ArrayList;
import java.util.List;

public class TeamCollaborationManager {
    private List<Task> teamTasks;

    public TeamCollaborationManager() {
        this.teamTasks = new ArrayList<>();
    }

    public void addTeamTask(Task task) {
        teamTasks.add(task);
    }

    public List<Task> getTeamTasks() {
        return teamTasks;
    }
}