import java.io.IOException;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /** Creates a command that adds the given task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
    }
}
