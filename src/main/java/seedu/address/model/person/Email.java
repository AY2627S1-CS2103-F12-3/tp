package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.stream.Collectors;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 *
 * <p>The part before the {@code @} follows the unquoted (dot-atom) form of RFC 5322, which is also the form
 * that the HTML standard accepts for email inputs. Quoted forms such as {@code "john doe"@example.com} are
 * not supported, because their spaces and slashes could be mistaken for command prefixes.
 */
public class Email {

    public static final String MESSAGE_CONSTRAINTS = "Emails should look like name@example.com.\n"
            + "Before the @, use letters, digits and standard email symbols such as . _ - + ' "
            + "(no spaces or quotation marks). A dot cannot come first, last, or twice in a row.\n"
            + "After the @, use letters, digits, hyphens and dots. Each part between dots must start and end "
            + "with a letter or digit, and the last part must be at least 2 characters long.";

    /** Letters, digits and the symbols that RFC 5322 allows in an unquoted local part. */
    private static final String LOCAL_PART_CHARACTERS = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+";
    /** A character that common email providers issue, so other allowed symbols are likely typos. */
    private static final String COMMON_LOCAL_PART_CHARACTER = "[A-Za-z0-9._+'-]";
    /** Dot-separated runs of allowed characters, so a dot is never first, last, or next to another dot. */
    private static final String LOCAL_PART_REGEX = "^" + LOCAL_PART_CHARACTERS
            + "(\\." + LOCAL_PART_CHARACTERS + ")*";
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]+"; // alphanumeric characters except underscore
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_LAST_PART_REGEX = "(" + DOMAIN_PART_REGEX + "){2,}$"; // At least two chars
    private static final String DOMAIN_REGEX = "(" + DOMAIN_PART_REGEX + "\\.)*" + DOMAIN_LAST_PART_REGEX;
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns the part of this email after the {@code @}, such as {@code example.com}.
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * Returns the symbols before the {@code @} that common email providers do not issue, separated by spaces
     * and each listed once, such as {@code "# !"} for {@code john#doe!@example.com}.
     * Returns an empty string if there are none.
     * Such symbols are valid but are most likely typos, so they are worth warning the user about.
     */
    public String getUncommonSymbols() {
        String localPart = value.substring(0, value.indexOf('@'));
        return localPart.replaceAll(COMMON_LOCAL_PART_CHARACTER, "")
                .chars()
                .distinct()
                .mapToObj(Character::toString)
                .collect(Collectors.joining(" "));
    }

    /**
     * Returns true if the domain of this email has no dot, such as in {@code john@gmailcom}.
     * Such a domain is valid but is most likely a typo, so it is worth warning the user about.
     */
    public boolean hasDotlessDomain() {
        return !getDomain().contains(".");
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Returns true if both emails are the same, ignoring case.
     * Email providers treat addresses case-insensitively, so {@code Alex@Example.com} and
     * {@code alex@example.com} reach the same person.
     */
    public boolean isSameEmail(Email otherEmail) {
        requireNonNull(otherEmail);
        return value.equalsIgnoreCase(otherEmail.value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
