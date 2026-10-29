package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a candidate's scheduled interview date and time in the address book.
 * Guarantees: immutable; is either {@link #NONE} or a real date and time,
 * as declared in {@link #isValidDateTime(String)}.
 */
public class Interview {

    public static final String MESSAGE_INVALID_FORMAT =
            "Interview date and time should be in the format YYYY-MM-DD HH:MM, e.g. 2026-10-20 14:00";
    public static final String MESSAGE_INVALID_DATE_TIME =
            "Interview date and time should be a real date and a valid 24-hour time, e.g. 2026-10-20 14:00";

    /**
     * Zero-padded {@code YYYY-MM-DD HH:MM}, checked before parsing so format errors get their own message.
     */
    public static final String FORMAT_REGEX = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}";

    /** Represents a candidate with no interview scheduled. */
    public static final Interview NONE = new Interview();

    /*
     * "uuuu" (proleptic year) is needed instead of "yyyy" (year of era), because strict resolving
     * rejects a year of era that has no era. Strict resolving rejects impossible values such as
     * 2026-02-30 or 24:00, instead of adjusting them to the nearest valid date and time.
     */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    /** The interview date and time, which is null only for {@link #NONE}. */
    private final LocalDateTime dateTime;

    /**
     * Constructs the interview that represents no interview being scheduled.
     */
    private Interview() {
        dateTime = null;
    }

    /**
     * Constructs an {@code Interview}.
     *
     * @param interview A valid interview date and time in the format {@code YYYY-MM-DD HH:MM}.
     */
    public Interview(String interview) {
        requireNonNull(interview);
        checkArgument(isValidFormat(interview), MESSAGE_INVALID_FORMAT);
        checkArgument(isValidDateTime(interview), MESSAGE_INVALID_DATE_TIME);
        dateTime = LocalDateTime.parse(interview, FORMATTER);
    }

    /**
     * Returns true if a given string has the format {@code YYYY-MM-DD HH:MM},
     * even if its values are impossible.
     */
    public static boolean isValidFormat(String test) {
        return test.matches(FORMAT_REGEX);
    }

    /**
     * Returns true if a given string has the format {@code YYYY-MM-DD HH:MM}
     * and represents a real date and a valid 24-hour time.
     */
    public static boolean isValidDateTime(String test) {
        if (!isValidFormat(test)) {
            return false;
        }

        try {
            LocalDateTime.parse(test, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            // The format is right, so the exception means the date or time does not exist.
            return false;
        }
    }

    /**
     * Returns true if no interview is scheduled.
     */
    public boolean isEmpty() {
        return dateTime == null;
    }

    /**
     * Returns the interview date and time, or an empty {@code Optional} if no interview is scheduled.
     */
    public Optional<LocalDateTime> getDateTime() {
        return Optional.ofNullable(dateTime);
    }

    /**
     * Returns the interview in the format {@code YYYY-MM-DD HH:MM},
     * or an empty string if no interview is scheduled.
     */
    @Override
    public String toString() {
        return isEmpty() ? "" : FORMATTER.format(dateTime);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Interview otherInterview)) {
            return false;
        }

        return Objects.equals(dateTime, otherInterview.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dateTime);
    }

}
