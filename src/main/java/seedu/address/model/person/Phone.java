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
            + MAX_DIGITS + " digits, optionally starting with +. Digits may be separated by single spaces "
            + "or hyphens, e.g. +65 9123-4567.";

    /** An optional leading '+', then digits that may be separated by a single space or hyphen. */
    public static final String VALIDATION_REGEX = "\\+?\\d(?:[ -]?\\d)*";

    private static final String SEPARATOR_REGEX = "[ -]";

    /** The phone number without separators, e.g. {@code +6591234567}. */
    public final String value;

    /**
     * Constructs a {@code Phone}, removing any spaces and hyphens between digits so that differently
     * formatted inputs of the same number are stored identically.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone.replaceAll(SEPARATOR_REGEX, "");
    }

    /**
     * Returns true if a given string is a valid phone number.
     * The '+' and any separators do not count towards the number of digits.
     */
    public static boolean isValidPhone(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }
        int digitCount = test.replaceAll("\\D", "").length();
        return digitCount >= MIN_DIGITS && digitCount <= MAX_DIGITS;
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
