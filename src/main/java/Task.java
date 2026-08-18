/**
 * Represents a todo, deadline, or event and whether it has been completed.
 *
 * <p>The different task types are represented by fields instead of subclasses so
 * that the model does not require inheritance.</p>
 */
public class Task {
    /** The supported kinds of task. */
    public enum Type {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String symbol;

        Type(String symbol) {
            this.symbol = symbol;
        }
    }

    private final String description;
    private final Type type;
    private final String start;
    private final String end;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this(description, Type.TODO, null, null);
    }

    private Task(String description, Type type, String start, String end) {
        this.description = description;
        this.type = type;
        this.start = start;
        this.end = end;
        this.isDone = false;
    }

    /**
     * Creates a todo without an attached date or time.
     *
     * @param description description of the todo
     * @return the new todo
     */
    public static Task todo(String description) {
        return new Task(description, Type.TODO, null, null);
    }

    /**
     * Creates a deadline that must be completed by the given date or time.
     *
     * @param description description of the deadline
     * @param by date or time by which it should be completed
     * @return the new deadline
     */
    public static Task deadline(String description, String by) {
        return new Task(description, Type.DEADLINE, null, by);
    }

    /**
     * Creates an event with a start and an end date or time.
     *
     * @param description description of the event
     * @param from event start date or time
     * @param to event end date or time
     * @return the new event
     */
    public static Task event(String description, String from, String to) {
        return new Task(description, Type.EVENT, from, to);
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

    /**
     * Returns the task in its display format, including its status icon.
     *
     * @return formatted task
     */
    @Override
    public String toString() {
        String timing = switch (type) {
        case TODO -> "";
        case DEADLINE -> " (by: " + end + ")";
        case EVENT -> " (from: " + start + " to: " + end + ")";
        };
        return "[" + type.symbol + "][" + getStatusIcon() + "] " + description + timing;
    }
}
