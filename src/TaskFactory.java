public class TaskFactory {
    public static Task createTask(String description, String startTime, String endTime, String priority, String teamMembers) {
        return new Task(description, startTime, endTime, priority, teamMembers);
    }
}