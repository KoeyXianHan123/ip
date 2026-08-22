package nova;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import nova.command.Command;
import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

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
            boolean isExit = false;
            try {
                Command command = parser.parse(ui.readCommand());
                command.execute(tasks, ui);
                isExit = command.isExit();
            } catch (NovaException exception) {
                ui.showError(exception.getMessage());
            } catch (IOException exception) {
                ui.showError("I could not save your tasks to the data file.");
            } finally {
                ui.showDivider();
            }
            if (isExit) {
                break;
            }
        }
    }
}
