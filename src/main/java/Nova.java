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
        String[] tasks = new String[MAX_TASKS];
        boolean[] isDone = new boolean[MAX_TASKS];
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
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = isDone[i] ? "X" : " ";
                    System.out.println(" " + (i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                String taskNumberText = command.substring("mark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex < 0 || taskIndex >= taskCount) {
                        System.out.println(" Please enter the number of an existing task.");
                    } else {
                        isDone[taskIndex] = true;
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("  [X] " + tasks[taskIndex]);
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
                        isDone[taskIndex] = false;
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("  [ ] " + tasks[taskIndex]);
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Please enter a task number after unmark, for example: unmark 1");
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println(" added: " + command);
            }
            System.out.println(DIVIDER);
        }
    }
}
