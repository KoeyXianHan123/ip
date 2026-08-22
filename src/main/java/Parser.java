import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Interprets user input as commands that Nova can execute.
 */
public class Parser {
    /**
     * Parses user input into a command and validates its arguments.
     *
     * @param input raw user input
     * @return parsed command
     * @throws NovaException if the command or its arguments are invalid
     */
    public Command parse(String input) throws NovaException {
        if (input.equals("bye")) {
            return new ExitCommand();
        } else if (input.equals("list")) {
            return new ListCommand();
        } else if (isCommand(input, "mark")) {
            return new MarkCommand(parseTaskNumber(input, "mark"), true);
        } else if (isCommand(input, "unmark")) {
            return new MarkCommand(parseTaskNumber(input, "unmark"), false);
        } else if (isCommand(input, "delete")) {
            return new DeleteCommand(parseTaskNumber(input, "delete"));
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

    /** Returns the task-list number in a numbered command. */
    private int parseTaskNumber(String input, String commandWord) throws NovaException {
        String taskNumberText = input.substring(commandWord.length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new NovaException("Please enter a task number, for example: " + commandWord + " 1");
        }
    }

    /** Returns an add command containing a todo. */
    private Command parseTodo(String input) throws NovaException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("The description of a todo cannot be empty.");
        }
        return new AddCommand(new Todo(description));
    }

    /** Returns an add command containing a deadline. */
    private Command parseDeadline(String input) throws NovaException {
        String details = input.substring("deadline".length()).trim();
        int byMarker = details.indexOf(" /by ");
        if (byMarker < 1 || byMarker + " /by ".length() >= details.length()) {
            throw new NovaException("A deadline must follow: deadline DESCRIPTION /by yyyy-MM-dd");
        }

        String description = details.substring(0, byMarker).trim();
        String byText = details.substring(byMarker + " /by ".length()).trim();
        try {
            return new AddCommand(new Deadline(description, LocalDate.parse(byText)));
        } catch (DateTimeParseException exception) {
            throw new NovaException("The deadline date must be a valid date in yyyy-MM-dd format.");
        }
    }

    /** Returns an add command containing an event. */
    private Command parseEvent(String input) throws NovaException {
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
        return new AddCommand(new Event(description, from, to));
    }

    /** Returns a command that searches for deadlines on a date. */
    private Command parseDateSearch(String input) throws NovaException {
        String dateText = input.substring("on".length()).trim();
        try {
            LocalDate date = LocalDate.parse(dateText);
            return new ShowOnDateCommand(date);
        } catch (DateTimeParseException exception) {
            throw new NovaException("The date must be a valid date in yyyy-MM-dd format.");
        }
    }

}
