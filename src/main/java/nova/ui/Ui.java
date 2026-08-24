package nova.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import nova.task.Task;

/**
 * Handles console input and output for Nova.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Nova's greeting. */
    public void showWelcome() {
        String banner = " _   _                  \n"
                + "| \\ | | _____   ____ _ \n"
                + "|  \\| |/ _ \\ \\ / / _` |\n"
                + "| |\\  | (_) \\ V / (_| |\n"
                + "|_| \\_|\\___/ \\_/ \\__,_|\n";
        System.out.println(banner);
        System.out.println("Hello! I'm Nova.");
        System.out.println("What can I do for you?");
        showDivider();
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} if another command is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command from standard input.
     *
     * @return next command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the divider between command responses. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Displays Nova's farewell. */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Displays an error message.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        System.out.println(" OOPS!!! " + message);
    }

    /**
     * Displays the number of corrupted records skipped while loading.
     *
     * @param skippedRecordCount number of corrupted records skipped
     */
    public void showSkippedRecords(int skippedRecordCount) {
        System.out.println(" OOPS!!! I skipped " + skippedRecordCount
                + " corrupted task record(s) in the data file.");
    }

    /**
     * Displays all tasks with their task-list numbers.
     *
     * @param tasks tasks to display
     */
    public void showTasks(List<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays a task whose completion state changed.
     *
     * @param task task whose completion state changed
     * @param isMarked whether the task is now completed
     */
    public void showMarkedTask(Task task, boolean isMarked) {
        if (isMarked) {
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
    }

    /**
     * Displays a deleted task and the remaining task count.
     *
     * @param task deleted task
     * @param taskCount number of tasks remaining
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays an added task and the new task count.
     *
     * @param task added task
     * @param taskCount number of tasks after the addition
     */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the heading for deadlines that occur on a given date.
     *
     * @param date date whose deadlines will be displayed
     */
    public void showDeadlinesOn(LocalDate date) {
        System.out.println(" Here are the deadlines on " + date + ":");
    }

    /**
     * Displays a task with its task-list number.
     *
     * @param taskNumber one-based task number
     * @param task task to display
     */
    public void showNumberedTask(int taskNumber, Task task) {
        System.out.println(" " + taskNumber + "." + task);
    }
}
