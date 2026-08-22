import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Interprets user input as commands that Nova can execute.
 */
public class Parser {
    /** Represents the supported kinds of commands. */
    enum CommandType {
        BYE, LIST, MARK, UNMARK, DELETE, ADD, SHOW_ON_DATE
    }

    /** Holds a parsed command and any argument needed to execute it. */
    static class ParsedCommand {
        private final CommandType type;
        private final Task task;
        private final int taskNumber;
        private final LocalDate date;

        private ParsedCommand(CommandType type, Task task, int taskNumber, LocalDate date) {
            this.type = type;
            this.task = task;
            this.taskNumber = taskNumber;
            this.date = date;
        }

        CommandType getType() {
            return type;
        }

        Task getTask() {
            return task;
        }

        int getTaskNumber() {
            return taskNumber;
        }

        LocalDate getDate() {
            return date;
        }
    }

    /**
     * Parses user input into a command and validates its arguments.
     *
     * @param input raw user input
     * @return parsed command
     * @throws NovaException if the command or its arguments are invalid
     */
    public ParsedCommand parse(String input) throws NovaException {
        if (input.equals("bye")) {
            return commandOfType(CommandType.BYE);
        } else if (input.equals("list")) {
            return commandOfType(CommandType.LIST);
        } else if (isCommand(input, "mark")) {
            return parseNumberedCommand(input, "mark", CommandType.MARK);
        } else if (isCommand(input, "unmark")) {
            return parseNumberedCommand(input, "unmark", CommandType.UNMARK);
        } else if (isCommand(input, "delete")) {
            return parseNumberedCommand(input, "delete", CommandType.DELETE);
        } else if (isCommand(input, "todo")) {
            return parseTodo(input);
        } else if (isCommand(input, "deadline")) {
            return parseDeadline(input);
        } else if (isCommand(input, "event")) {
            return parseEvent(input);
        } else if (isCommand(input, "on")) {
            return parseDateSearch(input);
        }
        throw new NovaException("I'm sorry, but I don't know what that means :-(");
    }

    /** Returns whether the input contains the given command word. */
    private boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /** Returns a command that does not have an argument. */
    private ParsedCommand commandOfType(CommandType type) {
        return new ParsedCommand(type, null, 0, null);
    }

    /** Returns a command containing a task-list number. */
    private ParsedCommand parseNumberedCommand(String input, String commandWord, CommandType type)
            throws NovaException {
        String taskNumberText = input.substring(commandWord.length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            return new ParsedCommand(type, null, taskNumber, null);
        } catch (NumberFormatException exception) {
            throw new NovaException("Please enter a task number, for example: " + commandWord + " 1");
        }
    }

    /** Returns an add command containing a todo. */
    private ParsedCommand parseTodo(String input) throws NovaException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("The description of a todo cannot be empty.");
        }
        return addCommand(new Todo(description));
    }

    /** Returns an add command containing a deadline. */
    private ParsedCommand parseDeadline(String input) throws NovaException {
        String details = input.substring("deadline".length()).trim();
        int byMarker = details.indexOf(" /by ");
        if (byMarker < 1 || byMarker + " /by ".length() >= details.length()) {
            throw new NovaException("A deadline must follow: deadline DESCRIPTION /by yyyy-MM-dd");
        }

        String description = details.substring(0, byMarker).trim();
        String byText = details.substring(byMarker + " /by ".length()).trim();
        try {
            return addCommand(new Deadline(description, LocalDate.parse(byText)));
        } catch (DateTimeParseException exception) {
            throw new NovaException("The deadline date must be a valid date in yyyy-MM-dd format.");
        }
    }

    /** Returns an add command containing an event. */
    private ParsedCommand parseEvent(String input) throws NovaException {
        String details = input.substring("event".length()).trim();
        int fromMarker = details.indexOf(" /from ");
        int toMarker = details.indexOf(" /to ", fromMarker + 1);
        boolean isInvalid = fromMarker < 1
                || toMarker < fromMarker + " /from ".length()
                || toMarker + " /to ".length() >= details.length();
        if (isInvalid) {
            throw new NovaException("An event must follow: event DESCRIPTION /from START /to END");
        }

        String description = details.substring(0, fromMarker).trim();
        String from = details.substring(fromMarker + " /from ".length(), toMarker).trim();
        String to = details.substring(toMarker + " /to ".length()).trim();
        if (from.isEmpty()) {
            throw new NovaException("An event needs a start date or time after /from.");
        }
        return addCommand(new Event(description, from, to));
    }

    /** Returns a command that searches for deadlines on a date. */
    private ParsedCommand parseDateSearch(String input) throws NovaException {
        String dateText = input.substring("on".length()).trim();
        try {
            LocalDate date = LocalDate.parse(dateText);
            return new ParsedCommand(CommandType.SHOW_ON_DATE, null, 0, date);
        } catch (DateTimeParseException exception) {
            throw new NovaException("The date must be a valid date in yyyy-MM-dd format.");
        }
    }

    /** Returns an add command containing the given task. */
    private ParsedCommand addCommand(Task task) {
        return new ParsedCommand(CommandType.ADD, task, 0, null);
    }
}
