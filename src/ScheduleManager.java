import java.util.*;
import java.time.*;
import java.time.format.*;
import java.util.logging.*;

public class ScheduleManager {
    private static ScheduleManager instance;
    private List<Task> tasks;
    private boolean emergencyMode;
    private static final Logger logger = Logger.getLogger(ScheduleManager.class.getName());
    private int remainingBreaks = 2;
    private int remainingExerciseMinutes = 120;
    private boolean hadBreakfast = false;
    private boolean hadLunch = false;
    private boolean hadDinner = false;
    private NotificationManager notificationManager;

    private ScheduleManager() {
        tasks = new ArrayList<>();
        emergencyMode = false;
        notificationManager = new NotificationManager();
    }

    public static synchronized ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    public boolean addTask(Task newTask, Scanner scanner) {
        try {
            if (!isValidInput(newTask)) {
                logger.warning("Invalid task input: " + newTask);
                return false;
            }

            // 1. Block scheduling for non-medical tasks between 10:00 AM and 11:00 AM
            if (!newTask.getDescription().equalsIgnoreCase("medical checkup") &&
                    isTimeBetween10and11(newTask.getStartTime())) {
                System.out
                        .println("Only medical checkups can be scheduled between 10:00 and 11:00. Please reschedule.");
                return false;
            }

            // 2. Medical Checkup Scheduling Logic:
            if (newTask.getDescription().equalsIgnoreCase("medical checkup")) {
                // Check time validity (must be between 10:00 and 11:00)
                if (!isMedicalCheckupTimeValid(newTask)) {
                    System.out.println("Medical checkup must be scheduled between 10:00 and 11:00. Please reschedule.");
                    return false;
                }

                // Check for conflicts (no other tasks allowed in this time slot)
                List<Task> conflictingTasks = getConflictingTasks(newTask);
                if (!conflictingTasks.isEmpty()) {
                    System.out.println("Medical checkup cannot be scheduled due to a conflict. Please reschedule.");
                    return false;
                }
            }

            // 3. Night Time Check (For all tasks)
            if (isNightTime(newTask.getStartTime()) || isNightTime(newTask.getEndTime())) {
                logger.warning("Task cannot be scheduled at night: " + newTask);
                return false;
            }

            // 4. Conflict Check (For all tasks, excluding the already checked medical
            // checkup)
            List<Task> conflictingTasks = getConflictingTasks(newTask);
            if (!conflictingTasks.isEmpty()) {
                if (emergencyMode) {
                    handleEmergencyConflict(newTask, conflictingTasks);
                    updateRemainingBreaksAndExercise(newTask);
                    return true;
                } else {
                    return handleNormalConflict(newTask, conflictingTasks, scanner);
                }
            }

            // 5. Add the task if no conflicts or time issues
            if (tasks.add(newTask)) {
                updateRemainingBreaksAndExercise(newTask);
                updateMeals(newTask);
                tasks.sort(Comparator.comparing(Task::getStartTime).thenComparing(Task::getPriority).reversed());
                notificationManager.scheduleReminder(newTask);
                return true;
            }
            return false;

        } catch (Exception e) {
            logger.severe("Exception while adding task: " + e.getMessage());
            return false;
        }
    }

    private boolean isTimeBetween10and11(String time) {
        LocalTime taskTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        return !taskTime.isBefore(LocalTime.of(10, 0)) && taskTime.isBefore(LocalTime.of(11, 0));
    }

    private boolean isMedicalCheckupTimeValid(Task task) {
        LocalTime startTime = LocalTime.parse(task.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        return !startTime.isBefore(LocalTime.of(10, 0)) && !startTime.isAfter(LocalTime.of(11, 0));
    }

    private void updateMeals(Task task) {
        if (task.getDescription().equalsIgnoreCase("breakfast")) {
            hadBreakfast = true;
        } else if (task.getDescription().equalsIgnoreCase("lunch")) {
            hadLunch = true;
        } else if (task.getDescription().equalsIgnoreCase("dinner")) {
            hadDinner = true;
        }
    }

    private void updateRemainingBreaksAndExercise(Task task) {
        if (task.getDescription().equalsIgnoreCase("break")) {
            remainingBreaks = Math.max(0, remainingBreaks - 1);
        } else if (task.getDescription().equalsIgnoreCase("exercise")) {
            int exerciseMinutes = calculateMinutes(task.getStartTime(), task.getEndTime());
            remainingExerciseMinutes = Math.max(0, remainingExerciseMinutes - exerciseMinutes);
        }
    }

    public void displayRemainingBreaksAndExercise() {
        System.out.println("Remaining breaks: " + remainingBreaks);
        System.out.println("Remaining exercise time: " + remainingExerciseMinutes + " minutes");
    }

    private List<Task> getConflictingTasks(Task newTask) {
        return tasks.stream()
                .filter(task -> isOverlap(task, newTask))
                .collect(java.util.stream.Collectors.toList());
    }

    private void handleEmergencyConflict(Task newTask, List<Task> conflictingTasks) {
        tasks.removeAll(conflictingTasks);
        tasks.add(newTask);
        logger.info("Emergency task added, conflicting tasks removed: " + newTask);
    }

    private boolean handleNormalConflict(Task newTask, List<Task> conflictingTasks, Scanner scanner) {
        logger.info("Handling normal conflict for task: " + newTask);
        System.out.println("The new task conflicts with existing task(s):");
        conflictingTasks.forEach(System.out::println);

        if (newTask.getPriority().compareTo(conflictingTasks.get(0).getPriority()) > 0) {
            System.out.print("Do you want to swap it with the conflicting task(s)? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                tasks.removeAll(conflictingTasks);
                tasks.add(newTask);
                updateRemainingBreaksAndExercise(newTask);
                logger.info("New task added, conflicting tasks removed: " + newTask);
                return true;
            }
        }

        System.out.print("Do you want to reschedule the new task? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Please add the task again with a different time.");
        } else {
            System.out.println("Task not added due to conflict.");
        }
        return false;
    }

    public boolean removeTask(String description) {
        boolean removed = tasks.removeIf(task -> task.getDescription().equalsIgnoreCase(description));
        if (removed) {
            logger.info("Task removed: " + description);
        } else {
            logger.warning("Task not found for removal: " + description);
        }
        return removed;
    }

    public void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks scheduled for the day.");
        } else {
            tasks.forEach(System.out::println);
        }
    }

    public void toggleEmergencyMode() {
        emergencyMode = !emergencyMode;
        logger.info("Emergency mode " + (emergencyMode ? "enabled" : "disabled"));
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks); // Return a copy to avoid external modification
    }

    private boolean isOverlap(Task t1, Task t2) {
        return t1.getStartTime().compareTo(t2.getEndTime()) < 0 && t1.getEndTime().compareTo(t2.getStartTime()) > 0;
    }

    private boolean isValidInput(Task task) {
        return task.getDescription() != null && !task.getDescription().isEmpty() &&
                isValidTime(task.getStartTime()) && isValidTime(task.getEndTime()) &&
                isValidPriority(task.getPriority());
    }

    private boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            logger.warning("Invalid time format: " + time);
            return false;
        }
    }

    private boolean isValidPriority(String priority) {
        return priority != null && (priority.equalsIgnoreCase("High") ||
                priority.equalsIgnoreCase("Medium") ||
                priority.equalsIgnoreCase("Low"));
    }

    private boolean isNightTime(String time) {
        LocalTime taskTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        return taskTime.isAfter(LocalTime.of(21, 30)) || taskTime.isBefore(LocalTime.of(6, 0));
    }

    public void markTaskCompletion(Scanner scanner) {
        tasks.forEach(task -> {
            System.out.print("Did you complete the task: " + task.getDescription() + "? (y/n): ");
            task.setComplete(scanner.nextLine().trim().equalsIgnoreCase("y"));
        });
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder("\nDaily Report:\n");
        int completedTasks = 0;
        int totalTasks = tasks.size();
        int exerciseMinutes = 0;
        int breaks = 0;
        boolean hadBreakfast = false;
        boolean hadLunch = false;
        boolean hadDinner = false;

        for (Task task : tasks) {
            if (task.isComplete()) {
                completedTasks++;
            }
            if (task.getDescription().equalsIgnoreCase("exercise")) {
                exerciseMinutes += calculateMinutes(task.getStartTime(), task.getEndTime());
            }
            if (task.getDescription().equalsIgnoreCase("break")) {
                breaks++;
            }
            if (task.getDescription().equalsIgnoreCase("breakfast")) {
                hadBreakfast = true;
            }
            if (task.getDescription().equalsIgnoreCase("lunch")) {
                hadLunch = true;
            }
            if (task.getDescription().equalsIgnoreCase("dinner")) {
                hadDinner = true;
            }
        }

        report.append("Completed tasks: ").append(completedTasks).append("/").append(totalTasks).append("\n")
                .append("Exercise time: ").append(exerciseMinutes).append(" minutes\n")
                .append("Number of breaks: ").append(breaks).append("\n");

        if (exerciseMinutes < 120) {
            report.append("Warning: Less than 2 hours of exercise completed.\n");
        }
        if (breaks < 2) {
            report.append("Warning: Less than 2 breaks taken.\n");
        }
        if (!hadBreakfast || !hadLunch || !hadDinner) {
            report.append("Warning: Missing one or more main meals (breakfast, lunch, dinner).\n");
        }

        return report.toString();
    }

    private int calculateMinutes(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
        return (int) java.time.Duration.between(start, end).toMinutes();
    }
}