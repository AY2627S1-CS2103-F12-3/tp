package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class InterviewTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Interview(null));
    }

    @Test
    public void constructor_invalidFormat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Interview.MESSAGE_INVALID_FORMAT, () ->
                new Interview("2026-10-20 9:00"));
    }

    @Test
    public void constructor_impossibleDateTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Interview.MESSAGE_INVALID_DATE_TIME, () ->
                new Interview("2026-02-30 14:00"));
    }

    @Test
    public void isValidFormat() {
        // null
        assertThrows(NullPointerException.class, () -> Interview.isValidFormat(null));

        // invalid formats
        assertFalse(Interview.isValidFormat("")); // empty string
        assertFalse(Interview.isValidFormat("2026-10-20")); // date only
        assertFalse(Interview.isValidFormat("2026-10-20 9:00")); // single-digit hour
        assertFalse(Interview.isValidFormat("2026-1-20 14:00")); // single-digit month
        assertFalse(Interview.isValidFormat("20/10/2026 14:00")); // wrong date separators
        assertFalse(Interview.isValidFormat("2026-10-20T14:00")); // ISO separator instead of a space
        assertFalse(Interview.isValidFormat("2026-10-20 14:00:00")); // includes seconds
        assertFalse(Interview.isValidFormat(" 2026-10-20 14:00")); // leading space

        // valid formats, including impossible values
        assertTrue(Interview.isValidFormat("2026-10-20 14:00"));
        assertTrue(Interview.isValidFormat("2026-02-30 25:61"));
    }

    @Test
    public void isValidDateTime() {
        // null
        assertThrows(NullPointerException.class, () -> Interview.isValidDateTime(null));

        // invalid date and time
        assertFalse(Interview.isValidDateTime("2026-10-20 9:00")); // wrong format
        assertFalse(Interview.isValidDateTime("2026-02-30 14:00")); // day does not exist
        assertFalse(Interview.isValidDateTime("2026-02-29 14:00")); // leap day in a non-leap year
        assertFalse(Interview.isValidDateTime("2026-13-01 14:00")); // month does not exist
        assertFalse(Interview.isValidDateTime("2026-10-20 24:00")); // hour out of range
        assertFalse(Interview.isValidDateTime("2026-10-20 14:60")); // minute out of range

        // valid date and time
        assertTrue(Interview.isValidDateTime("2026-10-20 14:00"));
        assertTrue(Interview.isValidDateTime("2028-02-29 09:30")); // leap day in a leap year
        assertTrue(Interview.isValidDateTime("2026-12-31 23:59")); // last minute of the year
        assertTrue(Interview.isValidDateTime("2026-01-01 00:00")); // first minute of the year
        assertTrue(Interview.isValidDateTime("2020-01-15 09:30")); // past date
    }

    @Test
    public void findValidationError() {
        // null interview
        assertThrows(NullPointerException.class, () -> Interview.findValidationError(null));

        // wrong format -> format message
        assertEquals(Optional.of(Interview.MESSAGE_INVALID_FORMAT), Interview.findValidationError(""));
        assertEquals(Optional.of(Interview.MESSAGE_INVALID_FORMAT),
                Interview.findValidationError("2026-10-20 9:00"));

        // right format but impossible values -> date and time message
        assertEquals(Optional.of(Interview.MESSAGE_INVALID_DATE_TIME),
                Interview.findValidationError("2026-02-30 14:00"));
        assertEquals(Optional.of(Interview.MESSAGE_INVALID_DATE_TIME),
                Interview.findValidationError("2026-10-20 24:00"));

        // valid interview -> no error
        assertEquals(Optional.empty(), Interview.findValidationError("2026-10-20 14:00"));
    }

    @Test
    public void isEmpty() {
        assertTrue(Interview.NONE.isEmpty());
        assertFalse(new Interview("2026-10-20 14:00").isEmpty());
    }

    @Test
    public void getDateTime() {
        assertEquals(Optional.empty(), Interview.NONE.getDateTime());
        assertEquals(Optional.of(LocalDateTime.of(2026, 10, 20, 14, 0)),
                new Interview("2026-10-20 14:00").getDateTime());
    }

    @Test
    public void equals() {
        Interview interview = new Interview("2026-10-20 14:00");

        // same values -> returns true
        assertTrue(interview.equals(new Interview("2026-10-20 14:00")));

        // same object -> returns true
        assertTrue(interview.equals(interview));

        // null -> returns false
        assertFalse(interview.equals(null));

        // different types -> returns false
        assertFalse(interview.equals("2026-10-20 14:00"));

        // different values -> returns false
        assertFalse(interview.equals(new Interview("2026-10-20 15:00")));

        // no interview -> returns false
        assertFalse(interview.equals(Interview.NONE));
        assertFalse(Interview.NONE.equals(interview));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new Interview("2026-10-20 14:00").hashCode(),
                new Interview("2026-10-20 14:00").hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("2026-10-20 14:00", new Interview("2026-10-20 14:00").toString());
        assertEquals("", Interview.NONE.toString());
    }

}
