package nova.command;

import java.time.LocalDate;

import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays deadlines occurring on a specified date.
 */
public class ShowOnDateCommand extends Command {
    private final LocalDate date;

    /** Creates a command that displays deadlines on the given date. */
    public ShowOnDateCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showDeadlinesOn(date);
        for (TaskList.NumberedTask task : tasks.getDeadlinesOn(date)) {
            ui.showNumberedTask(task.getTaskNumber(), task.getTask());
        }
    }
}
