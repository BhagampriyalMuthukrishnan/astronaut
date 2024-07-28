import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ScheduleManager manager = ScheduleManager.getInstance();
        NotificationManager notificationManager = new NotificationManager(); // Now it should work
        DailyReportGenerator reportGenerator = new DailyReportGenerator();
        for (int i = 0; i < 15; i++) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTask(scanner, manager);
                    manager.displayRemainingBreaksAndExercise();
                    break;
                case 2:
                    removeTask(scanner, manager);
                    break;
                case 3:
                    manager.viewTasks();
                    break;

                case 4:
                    manager.toggleEmergencyMode();
                    break;
                case 5:
                    FileManager.saveTasks(manager.getTasks());
                    manager.markTaskCompletion(scanner);
                    reportGenerator.generateReport(manager.getTasks());
                    System.out.println("Tasks saved. Daily report generated. Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n1. Add Task");
        System.out.println("2. Remove Task");
        System.out.println("3. View Tasks");
        System.out.println("4. Toggle Emergency Mode");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addTask(Scanner scanner, ScheduleManager manager) {
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter start time (HH:MM): ");
        String startTime = scanner.nextLine();
        System.out.print("Enter end time (HH:MM): ");
        String endTime = scanner.nextLine();
        System.out.print("Enter priority (High/Medium/Low): ");
        String priority = scanner.nextLine();
        System.out.print("Enter team members (comma separated): ");
        String teamMembers = scanner.nextLine();

        Task task = TaskFactory.createTask(description, startTime, endTime, priority, teamMembers);
        if (manager.addTask(task, scanner)) {
            System.out.println("Task added successfully. No conflicts.");
        }
    }

    private static void removeTask(Scanner scanner, ScheduleManager manager) {
        System.out.print("Enter task description to remove: ");
        String removeDesc = scanner.nextLine();
        if (manager.removeTask(removeDesc)) {
            System.out.println("Task removed successfully.");
        } else {
            System.out.println("Error: Task not found.");
        }
    }

    private static void markTaskComplete(Scanner scanner, ScheduleManager manager) {
        System.out.print("Enter task description to mark as complete: ");
        String desc = scanner.nextLine();

    }
}
