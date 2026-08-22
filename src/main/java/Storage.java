import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
