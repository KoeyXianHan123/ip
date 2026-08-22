import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

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
        TaskList tasks;
        try {
            tasks = new TaskList(STORAGE.load(), STORAGE);
            if (STORAGE.getSkippedRecordCount() > 0) {
                UI.showSkippedRecords(STORAGE.getSkippedRecordCount());
            }
        } catch (IOException exception) {
            UI.showError("I could not load your tasks from the data file.");
            tasks = new TaskList(new ArrayList<>(), STORAGE);
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
    private static void executeCommand(Parser.ParsedCommand command, TaskList tasks)
            throws NovaException, IOException {
        switch (command.getType()) {
            case LIST:
                UI.showTasks(tasks.getTasks());
                break;
            case MARK:
                Task markedTask = tasks.setMarked(command.getTaskNumber(), true);
                UI.showMarkedTask(markedTask, true);
                break;
            case UNMARK:
                Task unmarkedTask = tasks.setMarked(command.getTaskNumber(), false);
                UI.showMarkedTask(unmarkedTask, false);
                break;
            case DELETE:
                Task deletedTask = tasks.delete(command.getTaskNumber());
                UI.showDeletedTask(deletedTask, tasks.size());
                break;
            case ADD:
                tasks.add(command.getTask());
                UI.showAddedTask(command.getTask(), tasks.size());
                break;
            case SHOW_ON_DATE:
                UI.showDeadlinesOn(command.getDate());
                for (TaskList.NumberedTask task : tasks.getDeadlinesOn(command.getDate())) {
                    UI.showNumberedTask(task.getTaskNumber(), task.getTask());
                }
                break;
            case BYE:
                break;
            default:
                throw new AssertionError("Unhandled command type: " + command.getType());
        }
    }
}
