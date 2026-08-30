package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.parser.Parser;
import nova.storage.Storage;
import nova.ui.Ui;

class NovaTest {
    @TempDir
    private Path temporaryDirectory;

    private Nova nova;

    @BeforeEach
    void setUp() {
        Storage storage = new Storage(temporaryDirectory.resolve("nova.txt"));
        Ui unusedConsoleUi = new Ui(InputStream.nullInputStream(),
                new PrintStream(OutputStream.nullOutputStream()));
        nova = new Nova(storage, unusedConsoleUi, new Parser());
    }

    @Test
    void startGui_newSession_returnsGreeting() {
        assertEquals("Hello! I'm Nova.\nWhat can I do for you?", normalizeLineEndings(nova.startGui()));
    }

    @Test
    void getResponse_addThenList_usesSharedTaskListAndStorage() {
        String addResponse = normalizeLineEndings(nova.getResponse("todo read book"));
        String listResponse = normalizeLineEndings(nova.getResponse("list"));

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals(" Here are the tasks in your list:\n 1.[T][ ] read book", listResponse);
        assertTrue(temporaryDirectory.resolve("nova.txt").toFile().isFile());
    }

    @Test
    void getResponse_invalidThenBye_reportsErrorAndEndsSessionOnlyAfterBye() {
        String errorResponse = nova.getResponse("unknown");

        assertTrue(errorResponse.contains("I don't know what that means"));
        assertFalse(nova.isExitRequested());

        assertEquals(" Bye. Hope to see you again soon!", nova.getResponse("bye"));
        assertTrue(nova.isExitRequested());
    }

    private String normalizeLineEndings(String value) {
        return value.replace("\r\n", "\n").replace('\r', '\n');
    }
}
