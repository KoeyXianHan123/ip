/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTasks(tasks.getTasks());
    }
}
