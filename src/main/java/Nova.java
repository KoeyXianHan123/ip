import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    private static final String DIVIDER = "____________________________________________________________";

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
        List<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(DIVIDER);
            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            try {
                if (command.equals("list")) {
                    listTasks(tasks);
                } else if (isCommand(command, "mark")) {
                    markTask(command, tasks, true);
                } else if (isCommand(command, "unmark")) {
                    markTask(command, tasks, false);
                } else if (isCommand(command, "delete")) {
                    deleteTask(command, tasks);
                } else if (isCommand(command, "todo")) {
                    addTodo(command, tasks);
                } else if (isCommand(command, "deadline")) {
                    addDeadline(command, tasks);
                } else if (isCommand(command, "event")) {
                    addEvent(command, tasks);
                } else {
                    throw new NovaException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (NovaException exception) {
                System.out.println(" OOPS!!! " + exception.getMessage());
            }
            System.out.println(DIVIDER);
        }
    }

    /** Returns whether the input contains the given command word. */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /** Displays all stored tasks. */
    private static void listTasks(List<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Marks or unmarks the task selected by a command. */
    private static void markTask(String command, List<Task> tasks, boolean shouldMark)
            throws NovaException {
        String commandWord = shouldMark ? "mark" : "unmark";
        String taskNumberText = command.substring(commandWord.length()).trim();
        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumberText) - 1;
        } catch (NumberFormatException exception) {
            throw new NovaException("Please enter a task number, for example: " + commandWord + " 1");
        }
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NovaException("Task " + (taskIndex + 1) + " does not exist in the list.");
        }
        if (shouldMark) {
            tasks.get(taskIndex).markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            tasks.get(taskIndex).markAsNotDone();
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + tasks.get(taskIndex));
    }

    /** Deletes the task selected by a {@code delete NUMBER} command. */
    private static void deleteTask(String command, List<Task> tasks) throws NovaException {
        String taskNumberText = command.substring("delete".length()).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new NovaException("Please enter a task number, for example: delete 1");
        }

        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NovaException("Task " + taskNumber + " does not exist in the list.");
        }

        Task removedTask = tasks.remove(taskIndex);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Adds a todo described by a {@code todo DESCRIPTION} command. */
    private static void addTodo(String command, List<Task> tasks) throws NovaException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("The description of a todo cannot be empty.");
        }
        storeTask(new Todo(description), tasks);
    }

    /** Adds a deadline described by a {@code deadline DESCRIPTION /by TIME} command. */
    private static void addDeadline(String command, List<Task> tasks) throws NovaException {
        String details = command.substring("deadline".length()).trim();
        int byMarker = details.indexOf(" /by ");
        if (byMarker < 1 || byMarker + " /by ".length() >= details.length()) {
            throw new NovaException("A deadline must follow: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        String description = details.substring(0, byMarker).trim();
        String by = details.substring(byMarker + " /by ".length()).trim();
        storeTask(new Deadline(description, by), tasks);
    }

    /** Adds an event described by an {@code event DESCRIPTION /from START /to END} command. */
    private static void addEvent(String command, List<Task> tasks) throws NovaException {
        String details = command.substring("event".length()).trim();
        int fromMarker = details.indexOf(" /from ");
        int toMarker = details.indexOf(" /to ", fromMarker + 1);
        boolean isInvalid = fromMarker < 1
                || toMarker < fromMarker + " /from ".length()
                || toMarker + " /to ".length() >= details.length();
        if (isInvalid) {
            throw new NovaException("An event must follow: event DESCRIPTION /from START /to END");
        }

        String description = details.substring(0, fromMarker).trim();
        String from = details.substring(fromMarker + " /from ".length(), toMarker).trim();
        String to = details.substring(toMarker + " /to ".length()).trim();
        if (from.isEmpty()) {
            throw new NovaException("An event needs a start date or time after /from.");
        }
        storeTask(new Event(description, from, to), tasks);
    }

    /** Stores and displays a task in the dynamically sized task list. */
    private static void storeTask(Task task, List<Task> tasks) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}
