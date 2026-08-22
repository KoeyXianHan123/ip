/**
 * Represents the common description and completion state shared by all tasks.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to show the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns this task's completion state in the data-file format. */
    protected String getDataStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return serialized task
     */
    public String toDataString() {
        return "T | " + getDataStatus() + " | " + description;
    }

    /**
     * Returns the task in its display format, including its status icon.
     *
     * @return formatted task
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
