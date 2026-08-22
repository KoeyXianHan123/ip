import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves Nova's task list to a file on the hard disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that writes tasks to the given path.
     *
     * @param filePath relative or absolute path of the data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     * Returns an empty list when the data file does not exist and skips malformed records.
     *
     * @return tasks stored in the data file
     * @throws IOException if an existing data file cannot be read
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            Task task = parseTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     * Creates the parent directory and file when they do not exist.
     *
     * @param tasks current task list
     * @throws IOException if the tasks cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> taskLines = tasks.stream()
                .map(Task::toDataString)
                .toList();
        Files.write(filePath, taskLines, StandardCharsets.UTF_8);
    }

    /** Returns the task represented by a data-file record, or {@code null} if it is malformed. */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (!hasValidStatus(fields)) {
            return null;
        }

        Task task;
        switch (fields[0]) {
            case "T":
                task = fields.length == 3 ? new Todo(fields[2]) : null;
                break;
            case "D":
                task = fields.length == 4 ? new Deadline(fields[2], fields[3]) : null;
                break;
            case "E":
                task = fields.length == 5 ? new Event(fields[2], fields[3], fields[4]) : null;
                break;
            default:
                task = null;
                break;
        }

        if (task != null && fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Returns whether a record has a supported completion-state field. */
    private boolean hasValidStatus(String[] fields) {
        return fields.length >= 2 && (fields[1].equals("0") || fields[1].equals("1"));
    }
}
