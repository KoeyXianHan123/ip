package nova.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.TaskList.NumberedTask;

class TaskListTest {
    private static final LocalDate SEARCH_DATE = LocalDate.of(2026, 8, 24);
    private static final Storage UNUSED_STORAGE = new Storage(Path.of("unused"));

    @Test
    void add_saveSucceeds_addsAndSavesTask() throws IOException {
        RecordingStorage storage = new RecordingStorage();
        TaskList taskList = new TaskList(List.of(), storage);
        Todo task = new Todo("read book");

        taskList.add(task);

        assertEquals(List.of(task), taskList.getTasks());
        assertEquals(List.of(task), storage.savedTasks);
    }

    @Test
    void add_saveFails_restoresOriginalList() {
        RecordingStorage storage = new RecordingStorage();
        storage.shouldFail = true;
        Todo originalTask = new Todo("read book");
        TaskList taskList = new TaskList(List.of(originalTask), storage);

        assertThrows(IOException.class, () -> taskList.add(new Todo("write tests")));

        assertEquals(List.of(originalTask), taskList.getTasks());
    }

    @Test
    void delete_validMiddleTask_deletesAndSavesTask() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        Todo firstTask = new Todo("first");
        Todo middleTask = new Todo("middle");
        Todo lastTask = new Todo("last");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask), storage);

        Task deletedTask = taskList.delete(2);

        assertSame(middleTask, deletedTask);
        assertEquals(List.of(firstTask, lastTask), taskList.getTasks());
        assertEquals(List.of(firstTask, lastTask), storage.savedTasks);
    }

    @Test
    void delete_taskNumberOutsideList_throwsNovaExceptionWithoutSaving() {
        RecordingStorage storage = new RecordingStorage();
        TaskList taskList = new TaskList(List.of(new Todo("read book")), storage);

        assertThrows(NovaException.class, () -> taskList.delete(0));
        assertThrows(NovaException.class, () -> taskList.delete(2));

        assertEquals(0, storage.saveCount);
        assertEquals(1, taskList.size());
    }

    @Test
    void delete_saveFails_restoresTaskAtOriginalPosition() {
        RecordingStorage storage = new RecordingStorage();
        storage.shouldFail = true;
        Todo firstTask = new Todo("first");
        Todo middleTask = new Todo("middle");
        Todo lastTask = new Todo("last");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask), storage);

        assertThrows(IOException.class, () -> taskList.delete(2));

        assertEquals(List.of(firstTask, middleTask, lastTask), taskList.getTasks());
    }

    @Test
    void setMarked_markAndUnmark_updatesAndSavesTask() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        Todo task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task), storage);

        assertSame(task, taskList.setMarked(1, true));
        assertTrue(task.isDone());
        assertSame(task, taskList.setMarked(1, false));
        assertFalse(task.isDone());
        assertEquals(2, storage.saveCount);
    }

    @Test
    void setMarked_taskNumberOutsideList_throwsNovaExceptionWithoutSaving() {
        RecordingStorage storage = new RecordingStorage();
        TaskList taskList = new TaskList(List.of(new Todo("read book")), storage);

        assertThrows(NovaException.class, () -> taskList.setMarked(0, true));
        assertThrows(NovaException.class, () -> taskList.setMarked(2, true));

        assertEquals(0, storage.saveCount);
    }

    @Test
    void setMarked_saveFails_restoresOriginalCompletionState() {
        RecordingStorage storage = new RecordingStorage();
        storage.shouldFail = true;
        Todo incompleteTask = new Todo("read book");
        Todo completedTask = new Todo("write tests");
        completedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(incompleteTask, completedTask), storage);

        assertThrows(IOException.class, () -> taskList.setMarked(1, true));
        assertThrows(IOException.class, () -> taskList.setMarked(2, false));

        assertFalse(incompleteTask.isDone());
        assertTrue(completedTask.isDone());
    }

    @Test
    void getDeadlinesOn_emptyList_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(), UNUSED_STORAGE);

        assertTrue(taskList.getDeadlinesOn(SEARCH_DATE).isEmpty());
    }

    @Test
    void getDeadlinesOn_noMatchingDeadlines_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(
                new Todo("buy stationery"),
                new Event("project meeting", "2pm", "4pm"),
                new Deadline("submit report", SEARCH_DATE.plusDays(1))), UNUSED_STORAGE);

        assertTrue(taskList.getDeadlinesOn(SEARCH_DATE).isEmpty());
    }

    @Test
    void getDeadlinesOn_mixedTasks_returnsMatchingDeadlinesWithOriginalNumbersInOrder() {
        Deadline firstMatch = new Deadline("submit report", SEARCH_DATE);
        Deadline nonMatch = new Deadline("renew membership", SEARCH_DATE.minusDays(1));
        Deadline secondMatch = new Deadline("return book", SEARCH_DATE);
        secondMatch.markAsDone();
        TaskList taskList = new TaskList(List.of(
                new Todo("buy stationery"),
                firstMatch,
                new Event("project meeting", "2pm", "4pm"),
                nonMatch,
                secondMatch), UNUSED_STORAGE);

        List<NumberedTask> matches = taskList.getDeadlinesOn(SEARCH_DATE);

        assertEquals(2, matches.size());
        assertEquals(2, matches.get(0).getTaskNumber());
        assertSame(firstMatch, matches.get(0).getTask());
        assertEquals(5, matches.get(1).getTaskNumber());
        assertSame(secondMatch, matches.get(1).getTask());
    }

    private static class RecordingStorage extends Storage {
        private boolean shouldFail;
        private int saveCount;
        private List<Task> savedTasks;

        RecordingStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void save(List<Task> tasks) throws IOException {
            saveCount++;
            if (shouldFail) {
                throw new IOException("Simulated save failure");
            }
            savedTasks = List.copyOf(tasks);
        }
    }
}
