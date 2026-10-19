package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 100;

    public static final String MESSAGE_CONSTRAINTS = "Names should be 1 to " + MAX_LENGTH + " characters long "
            + "and start with a letter. They can contain letters, digits, spaces and the symbols ' - . , / ( )";

    /*
     * The first character must be a letter in any language, so blank names, names starting with a digit and
     * symbol-only names are rejected. Accent marks (\p{M}) are allowed after it because an accented letter
     * can be typed as a plain letter followed by a separate mark.
     */
    public static final String VALIDATION_REGEX = "\\p{L}[\\p{L}\\p{M}0-9 '\\-./,()]{0,"
            + (MAX_LENGTH - 1) + "}";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
