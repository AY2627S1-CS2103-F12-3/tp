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

    private static final String COUNTRY_CODE_MARKER = "+";
    private static final String SINGAPORE_COUNTRY_CODE = "65";

    /** A Singapore number without its country code: 8 digits starting with 3, 6, 8 or 9. */
    private static final String SINGAPORE_LOCAL_NUMBER_REGEX = "[3689]\\d{7}";

    /** The Singapore country code followed by exactly 8 digits, with the + already removed. */
    private static final String SINGAPORE_NUMBER_WITH_CODE_REGEX = SINGAPORE_COUNTRY_CODE + "\\d{8}";

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
        requireNonNull(test);
        return removeSeparators(test).matches(VALIDATION_REGEX);
    }

    private static String removeSeparators(String phone) {
        return phone.replaceAll(SEPARATOR_REGEX, "");
    }

    /**
     * Returns true if this number may be the same as {@code otherPhone} written in another way,
     * such as with or without the + or the Singapore country code, but is not exactly the same.
     * For example, {@code +6591234567}, {@code 6591234567} and {@code 91234567} may all be the same number.
     */
    public boolean isPossiblySameNumber(Phone otherPhone) {
        requireNonNull(otherPhone);
        return !equals(otherPhone) && toComparisonKey().equals(otherPhone.toComparisonKey());
    }

    /**
     * Returns true if this number looks like a real phone number.
     * A number with a country code other than Singapore's is accepted as it is, since overseas formats vary.
     * Any other number is treated as a Singapore number, which has 8 digits starting with 3, 6, 8 or 9.
     */
    public boolean isPlausibleNumber() {
        boolean isOverseasNumber = value.startsWith(COUNTRY_CODE_MARKER)
                && !value.startsWith(COUNTRY_CODE_MARKER + SINGAPORE_COUNTRY_CODE);
        return isOverseasNumber || toComparisonKey().matches(SINGAPORE_LOCAL_NUMBER_REGEX);
    }

    /**
     * Returns this number without the + and, if it is a Singapore number written with its country code,
     * without the country code, so that different ways of writing the same number can be compared.
     */
    private String toComparisonKey() {
        String digits = value.replace(COUNTRY_CODE_MARKER, "");
        if (digits.matches(SINGAPORE_NUMBER_WITH_CODE_REGEX)) {
            return digits.substring(SINGAPORE_COUNTRY_CODE.length());
        }
        return digits;
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
