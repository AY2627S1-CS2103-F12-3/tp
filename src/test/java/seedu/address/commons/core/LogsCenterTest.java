package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

public class LogsCenterTest {

    @Test
    public void getLogger_name_returnsChildOfBaseLogger() {
        Logger logger = LogsCenter.getLogger("LogsCenterTestNamedLogger");

        assertEquals("ab3.LogsCenterTestNamedLogger", logger.getName());
        assertEquals("ab3", logger.getParent().getName());
        assertTrue(logger.getUseParentHandlers());
        assertEquals(0, logger.getHandlers().length);
    }

    @Test
    public void getLogger_class_usesSimpleClassName() {
        Logger logger = LogsCenter.getLogger(LogsCenterTest.class);

        assertEquals("ab3.LogsCenterTest", logger.getName());
        assertSame(LogsCenter.getLogger("LogsCenterTest"), logger);
    }

    @Test
    public void getLogger_nullClass_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> LogsCenter.getLogger((Class<?>) null));
    }

    @Test
    public void getLogger_sameName_returnsSameLogger() {
        Logger logger = LogsCenter.getLogger("LogsCenterTestRepeatedLogger");

        assertSame(logger, LogsCenter.getLogger("LogsCenterTestRepeatedLogger"));
    }

    @Test
    public void getLogger_existingLogger_removesOwnHandlersAndRestoresParentHandlers() {
        Logger logger = LogsCenter.getLogger("LogsCenterTestExistingLogger");
        ConsoleHandler handler = new ConsoleHandler();
        try {
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);

            Logger retrieved = LogsCenter.getLogger("LogsCenterTestExistingLogger");

            assertSame(logger, retrieved);
            assertEquals(0, retrieved.getHandlers().length);
            assertTrue(retrieved.getUseParentHandlers());
        } finally {
            logger.removeHandler(handler);
            logger.setUseParentHandlers(true);
            handler.close();
        }
    }

    @Test
    public void getLogger_newLogger_inheritsInfoLevel() {
        Logger logger = LogsCenter.getLogger("LogsCenterTestLevelLogger");

        assertTrue(logger.isLoggable(Level.INFO));
        assertTrue(logger.isLoggable(Level.WARNING));
        assertFalse(logger.isLoggable(Level.FINE));
    }
}
