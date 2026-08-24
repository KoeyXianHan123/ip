package nova.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.DeleteCommand;
import nova.command.ExitCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.ShowOnDateCommand;
import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Todo;

/**
 * Interprets user input as commands that Nova can execute.
 */
public class Parser {
    /**
     * Creates a parser for Nova commands.
     */
    public Parser() {
    }

    /**
     * Parses user input into a command and validates its arguments.
     *
     * @param input raw user input.
     * @return parsed command.
     * @throws NovaException if the command or its arguments are invalid.
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

    /**
     * Returns whether the input contains the given command word.
     *
     * @param input raw user input
     * @param commandWord command word to match
     * @return {@code true} if the input starts with the complete command word
     */
    private boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Returns the task-list number in a numbered command.
     *
     * @param input raw user input
     * @param commandWord command word preceding the task number
     * @return parsed task number
     * @throws NovaException if the command does not contain an integer task number
     */
    private int parseTaskNumber(String input, String commandWord) throws NovaException {
        String taskNumberText = input.substring(commandWord.length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new NovaException("Please enter a task number, for example: " + commandWord + " 1");
        }
    }

    /**
     * Returns an add command containing a todo.
     *
     * @param input raw todo command
     * @return command that adds the parsed todo
     * @throws NovaException if the todo description is empty
     */
    private Command parseTodo(String input) throws NovaException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("The description of a todo cannot be empty.");
        }
        return new AddCommand(new Todo(description));
    }

    /**
     * Returns an add command containing a deadline.
     *
     * @param input raw deadline command
     * @return command that adds the parsed deadline
     * @throws NovaException if the command format or deadline date is invalid
     */
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

    /**
     * Returns an add command containing an event.
     *
     * @param input raw event command
     * @return command that adds the parsed event
     * @throws NovaException if the command format or event times are invalid
     */
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

    /**
     * Returns a command that searches for deadlines on a date.
     *
     * @param input raw date-search command
     * @return command that searches the parsed date
     * @throws NovaException if the date is invalid
     */
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
