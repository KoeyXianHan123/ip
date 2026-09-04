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

    /**
     * Creates a command that changes a task's completion state.
     *
     * @param taskNumber one-based number of the task to update
     * @param shouldMark whether the task should be marked as completed
     */
    public MarkCommand(int taskNumber, boolean shouldMark) {
        this.taskNumber = taskNumber;
        this.shouldMark = shouldMark;
    }

    /**
     * Changes the task's completion state, saves the task list, and displays the result.
     *
     * @param tasks task list to update
     * @param ui UI used to display the updated task
     * @throws NovaException if the task number does not exist
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws NovaException, IOException {
        Task task = shouldMark ? tasks.mark(taskNumber) : tasks.unmark(taskNumber);
        ui.showMarkedTask(task, shouldMark);
    }
}
