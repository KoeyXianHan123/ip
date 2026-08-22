package nova.command;

import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTasks(tasks.getTasks());
    }
}
