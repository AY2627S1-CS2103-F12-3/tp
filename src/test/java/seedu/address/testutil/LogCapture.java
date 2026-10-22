package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Captures application diagnostics in memory and restores the previous logging level when closed.
 * Use within a try-with-resources block; logging configuration is shared by the test process.
 */
public class LogCapture extends Handler implements AutoCloseable {
    private final Logger logger = LogsCenter.getLogger(LogCapture.class).getParent();
    private final Level previousLevel = logger.getLevel();
    private final List<LogRecord> records = new ArrayList<>();

    /** Creates a capture without replacing existing handlers or configuring another log file. */
    public LogCapture() {
        setLevel(Level.ALL);
        logger.setLevel(Level.ALL);
        logger.addHandler(this);
    }

    /** Returns a snapshot of all captured records. */
    public List<LogRecord> getRecords() {
        return List.copyOf(records);
    }

    /** Returns captured records emitted by the given class. */
    public List<LogRecord> getRecords(Class<?> source) {
        String loggerName = logger.getName() + "." + source.getSimpleName();
        return records.stream().filter(record -> record.getLoggerName().equals(loggerName)).toList();
    }

    /** Checks that the class logged the exact message at the specified level. */
    public void assertContains(Class<?> source, Level level, String message) {
        boolean isRecorded = getRecords(source).stream().anyMatch(record ->
                record.getLevel().equals(level) && record.getMessage().equals(message));
        assertTrue(isRecorded, "Missing " + level + " diagnostic from " + source.getSimpleName() + ": " + message);
    }

    /** Checks that the class did not emit any records. */
    public void assertNoRecords(Class<?> source) {
        assertTrue(getRecords(source).isEmpty(), "Unexpected diagnostics from " + source.getSimpleName());
    }

    @Override
    public void publish(LogRecord record) {
        records.add(record);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
        logger.removeHandler(this);
        logger.setLevel(previousLevel);
    }
}
