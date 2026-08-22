package nova.command;

import java.io.IOException;

import nova.exception.NovaException;
import nova.task.Task;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Deletes a numbered task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /** Creates a command that deletes the given task number. */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws NovaException, IOException {
        Task deletedTask = tasks.delete(taskNumber);
        ui.showDeletedTask(deletedTask, tasks.size());
    }
}
