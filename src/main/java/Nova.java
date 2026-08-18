import java.util.Scanner;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    private static final String DIVIDER = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    /**
     * Greets the user, stores tasks, lists stored tasks, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String banner = " _   _                  \n"
                + "| \\ | | _____   ____ _ \n"
                + "|  \\| |/ _ \\ \\ / / _` |\n"
                + "| |\\  | (_) \\ V / (_| |\n"
                + "|_| \\_|\\___/ \\_/ \\__,_|\n";
        System.out.println(banner);
        System.out.println("Hello! I'm Nova.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(DIVIDER);
            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                String taskNumberText = command.substring("mark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println(" Please enter the number of an existing task.");
                    } else {
                        tasks[taskIndex].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("  " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Please enter a task number after mark, for example: mark 1");
                }
            } else if (command.startsWith("unmark ")) {
                String taskNumberText = command.substring("unmark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println(" Please enter the number of an existing task.");
                    } else {
                        tasks[taskIndex].markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Please enter a task number after unmark, for example: unmark 1");
                }
            } else if (command.startsWith("todo ")) {
                taskCount = addTodo(command, tasks, taskCount);
            } else if (command.startsWith("deadline ")) {
                taskCount = addDeadline(command, tasks, taskCount);
            } else if (command.startsWith("event ")) {
                taskCount = addEvent(command, tasks, taskCount);
            } else {
                System.out.println(" Please use todo, deadline, or event to add a task.");
            }
            System.out.println(DIVIDER);
        }
    }

    /** Adds a todo described by a {@code todo DESCRIPTION} command. */
    private static int addTodo(String command, Task[] tasks, int taskCount) {
        String description = command.substring("todo ".length()).trim();
        if (description.isEmpty()) {
            System.out.println(" A todo needs a description.");
            return taskCount;
        }
        return storeTask(new Todo(description), tasks, taskCount);
    }

    /** Adds a deadline described by a {@code deadline DESCRIPTION /by TIME} command. */
    private static int addDeadline(String command, Task[] tasks, int taskCount) {
        String details = command.substring("deadline ".length()).trim();
        int byMarker = details.indexOf(" /by ");
        if (byMarker < 1 || byMarker + " /by ".length() >= details.length()) {
            System.out.println(" Use: deadline DESCRIPTION /by DATE_OR_TIME");
            return taskCount;
        }

        String description = details.substring(0, byMarker).trim();
        String by = details.substring(byMarker + " /by ".length()).trim();
        return storeTask(new Deadline(description, by), tasks, taskCount);
    }

    /** Adds an event described by an {@code event DESCRIPTION /from START /to END} command. */
    private static int addEvent(String command, Task[] tasks, int taskCount) {
        String details = command.substring("event ".length()).trim();
        int fromMarker = details.indexOf(" /from ");
        int toMarker = details.indexOf(" /to ", fromMarker + 1);
        boolean isInvalid = fromMarker < 1
                || toMarker < fromMarker + " /from ".length()
                || toMarker + " /to ".length() >= details.length();
        if (isInvalid) {
            System.out.println(" Use: event DESCRIPTION /from START /to END");
            return taskCount;
        }

        String description = details.substring(0, fromMarker).trim();
        String from = details.substring(fromMarker + " /from ".length(), toMarker).trim();
        String to = details.substring(toMarker + " /to ".length()).trim();
        if (from.isEmpty()) {
            System.out.println(" Use: event DESCRIPTION /from START /to END");
            return taskCount;
        }
        return storeTask(new Event(description, from, to), tasks, taskCount);
    }

    /** Stores and displays a task if there is room in the task list. */
    private static int storeTask(Task task, Task[] tasks, int taskCount) {
        if (taskCount >= tasks.length) {
            System.out.println(" The task list is full.");
            return taskCount;
        }
        tasks[taskCount] = task;
        System.out.println(" Got it. I've added this task:");
        System.out.println("  " + task);
        int newTaskCount = taskCount + 1;
        System.out.println(" Now you have " + newTaskCount + " tasks in the list.");
        return newTaskCount;
    }
}
