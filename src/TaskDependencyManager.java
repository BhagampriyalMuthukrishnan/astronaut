import java.util.HashMap;
import java.util.Map;

public class TaskDependencyManager {
    private Map<String, String> dependencies;

    public TaskDependencyManager() {
        this.dependencies = new HashMap<>();
    }

    public void addDependency(String task, String dependentTask) {
        dependencies.put(task, dependentTask);
    }

    public boolean isTaskAllowed(String task, String dependentTask) {
        return dependencies.getOrDefault(task, "").equals(dependentTask);
    }
}