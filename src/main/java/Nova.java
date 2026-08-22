import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Starts the Nova chatbot application.
 */
public class Nova {
    private final Storage storage;
    private final Ui ui;
    private final Parser parser;

    /**
     * Creates a Nova application with its collaborating components.
     *
     * @param storage storage used to load and save tasks
     * @param ui UI used for console interaction
     * @param parser parser used to interpret commands
     */
    public Nova(Storage storage, Ui ui, Parser parser) {
        this.storage = storage;
        this.ui = ui;
        this.parser = parser;
    }

    /**
     * Greets the user, stores tasks, lists stored tasks, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Storage storage = new Storage(Path.of("data", "nova.txt"));
        Nova nova = new Nova(storage, new Ui(), new Parser());
        nova.run();
    }

    /** Starts Nova's command loop. */
    public void run() {
        ui.showWelcome();
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load(), storage);
            if (storage.getSkippedRecordCount() > 0) {
                ui.showSkippedRecords(storage.getSkippedRecordCount());
            }
        } catch (IOException exception) {
            ui.showError("I could not load your tasks from the data file.");
            tasks = new TaskList(new ArrayList<>(), storage);
        }

        while (ui.hasNextCommand()) {
            ui.showDivider();
            try {
                Parser.ParsedCommand command = parser.parse(ui.readCommand());
                if (command.getType() == Parser.CommandType.BYE) {
                    ui.showGoodbye();
                    break;
                }
                executeCommand(command, tasks);
            } catch (NovaException exception) {
                ui.showError(exception.getMessage());
            } catch (IOException exception) {
                ui.showError("I could not save your tasks to the data file.");
            }
            ui.showDivider();
        }
    }

    /** Executes a parsed command. */
    private void executeCommand(Parser.ParsedCommand command, TaskList tasks)
            throws NovaException, IOException {
        switch (command.getType()) {
            case LIST:
                ui.showTasks(tasks.getTasks());
                break;
            case MARK:
                Task markedTask = tasks.setMarked(command.getTaskNumber(), true);
                ui.showMarkedTask(markedTask, true);
                break;
            case UNMARK:
                Task unmarkedTask = tasks.setMarked(command.getTaskNumber(), false);
                ui.showMarkedTask(unmarkedTask, false);
                break;
            case DELETE:
                Task deletedTask = tasks.delete(command.getTaskNumber());
                ui.showDeletedTask(deletedTask, tasks.size());
                break;
            case ADD:
                tasks.add(command.getTask());
                ui.showAddedTask(command.getTask(), tasks.size());
                break;
            case SHOW_ON_DATE:
                ui.showDeadlinesOn(command.getDate());
                for (TaskList.NumberedTask task : tasks.getDeadlinesOn(command.getDate())) {
                    ui.showNumberedTask(task.getTaskNumber(), task.getTask());
                }
                break;
            case BYE:
                break;
            default:
                throw new AssertionError("Unhandled command type: " + command.getType());
        }
    }
}
