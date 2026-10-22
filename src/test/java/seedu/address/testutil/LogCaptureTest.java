package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.LogsCenter;

public class LogCaptureTest {
    @Test
    public void close_capturedRecords_restoresLevelAndHandlers() {
        Logger source = LogsCenter.getLogger(LogCaptureTest.class);
        Logger parent = source.getParent();
        Level previousLevel = parent.getLevel();
        Handler[] previousHandlers = parent.getHandlers();

        try (LogCapture capture = new LogCapture()) {
            capture.assertNoRecords(LogCaptureTest.class);
            var snapshot = capture.getRecords();
            source.fine("Diagnostic fixture");

            capture.assertContains(LogCaptureTest.class, Level.FINE, "Diagnostic fixture");
            capture.assertNoRecords(LogCapture.class);
            assertEquals(0, snapshot.size());
            assertEquals(1, capture.getRecords(LogCaptureTest.class).size());
            assertThrows(UnsupportedOperationException.class, () -> capture.getRecords().clear());
        }

        assertEquals(previousLevel, parent.getLevel());
        assertArrayEquals(previousHandlers, parent.getHandlers());
    }

    @Test
    public void close_exceptionInTest_restoresLoggingConfiguration() {
        Logger parent = LogsCenter.getLogger(LogCaptureTest.class).getParent();
        Level previousLevel = parent.getLevel();
        Handler[] previousHandlers = parent.getHandlers();

        assertThrows(IllegalStateException.class, () -> {
            try (LogCapture capture = new LogCapture()) {
                assertFalse(capture.getRecords().iterator().hasNext());
                throw new IllegalStateException("Simulated test failure");
            }
        });

        assertEquals(previousLevel, parent.getLevel());
        assertArrayEquals(previousHandlers, parent.getHandlers());
    }
}
