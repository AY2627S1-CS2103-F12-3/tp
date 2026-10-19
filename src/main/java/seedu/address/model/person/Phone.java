package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final int MIN_DIGITS = 8;
    public static final int MAX_DIGITS = 15;

    public static final String MESSAGE_CONSTRAINTS = "Phone numbers should have " + MIN_DIGITS + " to "
            + MAX_DIGITS + " digits, with an optional + at the start. Spaces and hyphens are ignored, "
            + "such as in +65 9123-4567.";

    /** An optional leading '+' and the allowed number of digits, checked after separators are removed. */
    public static final String VALIDATION_REGEX = "\\+?\\d{" + MIN_DIGITS + "," + MAX_DIGITS + "}";

    private static final String SEPARATOR_REGEX = "[ -]";

    /** The phone number without separators, e.g. {@code +6591234567}. */
    public final String value;

    /**
     * Constructs a {@code Phone}, removing all spaces and hyphens so that differently formatted inputs
     * of the same number are stored identically.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = removeSeparators(phone);
    }

    /**
     * Returns true if a given string is a valid phone number once its spaces and hyphens are ignored.
     */
    public static boolean isValidPhone(String test) {
        return removeSeparators(test).matches(VALIDATION_REGEX);
    }

    private static String removeSeparators(String phone) {
        return phone.replaceAll(SEPARATOR_REGEX, "");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
