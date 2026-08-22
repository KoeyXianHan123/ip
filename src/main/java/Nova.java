import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    private static final Storage STORAGE = new Storage(Path.of("data", "nova.txt"));
    private static final Ui UI = new Ui();
    private static final Parser PARSER = new Parser();

    /**
     * Greets the user, stores tasks, lists stored tasks, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        UI.showWelcome();
        List<Task> tasks;
        try {
            tasks = STORAGE.load();
            if (STORAGE.getSkippedRecordCount() > 0) {
                UI.showSkippedRecords(STORAGE.getSkippedRecordCount());
            }
        } catch (IOException exception) {
            UI.showError("I could not load your tasks from the data file.");
            tasks = new ArrayList<>();
        }

        while (UI.hasNextCommand()) {
            UI.showDivider();
            try {
                Parser.ParsedCommand command = PARSER.parse(UI.readCommand());
                if (command.getType() == Parser.CommandType.BYE) {
                    UI.showGoodbye();
                    break;
                }
                executeCommand(command, tasks);
            } catch (NovaException exception) {
                UI.showError(exception.getMessage());
            } catch (IOException exception) {
                UI.showError("I could not save your tasks to the data file.");
            }
            UI.showDivider();
        }
    }

    /** Executes a parsed command. */
    private static void executeCommand(Parser.ParsedCommand command, List<Task> tasks)
            throws NovaException, IOException {
        switch (command.getType()) {
            case LIST:
                UI.showTasks(tasks);
                break;
            case MARK:
                markTask(command.getTaskNumber(), tasks, true);
                break;
            case UNMARK:
                markTask(command.getTaskNumber(), tasks, false);
                break;
            case DELETE:
                deleteTask(command.getTaskNumber(), tasks);
                break;
            case ADD:
                storeTask(command.getTask(), tasks);
                break;
            case SHOW_ON_DATE:
                showDeadlinesOn(command.getDate(), tasks);
                break;
            case BYE:
                break;
            default:
                throw new AssertionError("Unhandled command type: " + command.getType());
        }
    }

    /** Marks or unmarks the task selected by a command. */
    private static void markTask(int taskNumber, List<Task> tasks, boolean shouldMark)
            throws NovaException, IOException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NovaException("Task " + taskNumber + " does not exist in the list.");
        }
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (shouldMark) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
        UI.showMarkedTask(task, shouldMark);
    }

    /** Deletes the task selected by a {@code delete NUMBER} command. */
    private static void deleteTask(int taskNumber, List<Task> tasks) throws NovaException, IOException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NovaException("Task " + taskNumber + " does not exist in the list.");
        }

        Task removedTask = tasks.remove(taskIndex);
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        UI.showDeletedTask(removedTask, tasks.size());
    }

    /** Displays deadlines that fall on the date in an {@code on yyyy-MM-dd} command. */
    private static void showDeadlinesOn(LocalDate date, List<Task> tasks) {
        UI.showDeadlinesOn(date);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task instanceof Deadline deadline && deadline.isDueOn(date)) {
                UI.showNumberedTask(i + 1, task);
            }
        }
    }

    /** Stores and displays a task in the dynamically sized task list. */
    private static void storeTask(Task task, List<Task> tasks) throws IOException {
        tasks.add(task);
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        UI.showAddedTask(task, tasks.size());
    }
}
