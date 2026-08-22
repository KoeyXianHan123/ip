package nova.command;

import java.io.IOException;

import nova.exception.NovaException;
import nova.task.Task;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Marks or unmarks a numbered task.
 */
public class MarkCommand extends Command {
    private final int taskNumber;
    private final boolean shouldMark;

    /** Creates a command that changes a task's completion state. */
    public MarkCommand(int taskNumber, boolean shouldMark) {
        this.taskNumber = taskNumber;
        this.shouldMark = shouldMark;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws NovaException, IOException {
        Task task = tasks.setMarked(taskNumber, shouldMark);
        ui.showMarkedTask(task, shouldMark);
    }
}
