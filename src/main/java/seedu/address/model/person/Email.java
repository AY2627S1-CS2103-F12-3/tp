package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.Optional;
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

    public static final String MESSAGE_EMPTY = "Email cannot be empty.";
    public static final String MESSAGE_MISSING_AT = "Email is missing an @, e.g. name@example.com.";
    public static final String MESSAGE_MULTIPLE_AT = "Email can contain only one @.";
    public static final String MESSAGE_MISSING_NAME = "Email needs a name before the @, e.g. name@example.com.";
    public static final String MESSAGE_INVALID_LOCAL_PART_CHARACTER = "Email cannot contain %1$s before the @.";
    public static final String MESSAGE_INVALID_DOMAIN_CHARACTER = "Email cannot contain %1$s after the @.";
    public static final String MESSAGE_LOCAL_PART_DOTS =
            "Before the @, a dot cannot come first, last, or twice in a row.";
    public static final String MESSAGE_DOMAIN_PARTS = "After the @, each part between dots must start and end "
            + "with a letter or digit, e.g. example.com.";
    public static final String MESSAGE_DOMAIN_ENDING =
            "The domain must end with a part of at least 2 characters, e.g. .com.";

    /** A letter, a digit, a dot, or a symbol that RFC 5322 allows in an unquoted local part. */
    private static final String LOCAL_PART_CHARACTER = "[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]";
    /** A character that common email providers issue, so other allowed symbols are likely typos. */
    private static final String COMMON_LOCAL_PART_CHARACTER = "[A-Za-z0-9._+'-]";
    /** A letter, a digit, a dot or a hyphen, the characters allowed in a domain name. */
    private static final String DOMAIN_CHARACTER = "[A-Za-z0-9.-]";
    private static final int MIN_LAST_DOMAIN_PART_LENGTH = 2;

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
        return findValidationError(test).isEmpty();
    }

    /**
     * Returns a message describing the first problem that makes {@code test} an invalid email,
     * or an empty {@code Optional} if {@code test} is valid.
     * The checks run in a fixed order, so an email with several problems always reports the same one.
     */
    public static Optional<String> findValidationError(String test) {
        requireNonNull(test);
        if (test.isEmpty()) {
            return Optional.of(MESSAGE_EMPTY);
        }

        long atCount = test.chars().filter(c -> c == '@').count();
        if (atCount == 0) {
            return Optional.of(MESSAGE_MISSING_AT);
        }
        if (atCount > 1) {
            return Optional.of(MESSAGE_MULTIPLE_AT);
        }

        int atIndex = test.indexOf('@');
        String localPart = test.substring(0, atIndex);
        String domain = test.substring(atIndex + 1);
        return findLocalPartError(localPart).or(() -> findDomainError(domain));
    }

    /**
     * Returns a message describing the first problem with the part of an email before the {@code @}, if any.
     */
    private static Optional<String> findLocalPartError(String localPart) {
        if (localPart.isEmpty()) {
            return Optional.of(MESSAGE_MISSING_NAME);
        }

        Optional<String> invalidCharacter = findInvalidCharacter(localPart, LOCAL_PART_CHARACTER);
        if (invalidCharacter.isPresent()) {
            return Optional.of(String.format(MESSAGE_INVALID_LOCAL_PART_CHARACTER, invalidCharacter.get()));
        }

        boolean hasMisplacedDot = localPart.startsWith(".") || localPart.endsWith(".") || localPart.contains("..");
        if (hasMisplacedDot) {
            return Optional.of(MESSAGE_LOCAL_PART_DOTS);
        }
        return Optional.empty();
    }

    /**
     * Returns a message describing the first problem with the part of an email after the {@code @}, if any.
     */
    private static Optional<String> findDomainError(String domain) {
        Optional<String> invalidCharacter = findInvalidCharacter(domain, DOMAIN_CHARACTER);
        if (invalidCharacter.isPresent()) {
            return Optional.of(String.format(MESSAGE_INVALID_DOMAIN_CHARACTER, invalidCharacter.get()));
        }

        // A limit of -1 keeps empty parts, so an empty domain or a leading, trailing or doubled dot is caught.
        String[] domainParts = domain.split("\\.", -1);
        boolean hasInvalidPart = Arrays.stream(domainParts).anyMatch(part -> !isValidDomainPart(part));
        if (hasInvalidPart) {
            return Optional.of(MESSAGE_DOMAIN_PARTS);
        }

        String lastDomainPart = domainParts[domainParts.length - 1];
        if (lastDomainPart.length() < MIN_LAST_DOMAIN_PART_LENGTH) {
            return Optional.of(MESSAGE_DOMAIN_ENDING);
        }
        return Optional.empty();
    }

    /**
     * Returns true if {@code domainPart}, a part of a domain between dots, is not empty and does not start
     * or end with a hyphen. Its characters are assumed to be checked already.
     */
    private static boolean isValidDomainPart(String domainPart) {
        return !domainPart.isEmpty() && !domainPart.startsWith("-") && !domainPart.endsWith("-");
    }

    /**
     * Returns a readable description of the first character in {@code text} that does not match
     * {@code allowedCharacterRegex}, if any.
     */
    private static Optional<String> findInvalidCharacter(String text, String allowedCharacterRegex) {
        return text.codePoints()
                .mapToObj(Character::toString)
                .filter(character -> !character.matches(allowedCharacterRegex))
                .findFirst()
                .map(Email::describeCharacter);
    }

    /**
     * Returns {@code character} in quotes, or a name for it if it is invisible or is itself a quotation mark,
     * such as "a space".
     */
    private static String describeCharacter(String character) {
        switch (character) {
            case "\"":
                return "a quotation mark";
            case " ":
                return "a space";
            case "\t":
                return "a tab";
            case "\n":
            case "\r":
                return "a new line";
            default:
                return "“" + character + "”";
        }
    }

    /**
     * Returns the part of this email after the {@code @}, such as {@code example.com}.
     */
    public String getDomain() {
        return value.substring(getAtSignIndex() + 1);
    }

    /**
     * Returns the symbols before the {@code @} that common email providers do not issue, separated by spaces
     * and each listed once, such as {@code "# !"} for {@code john#doe!@example.com}.
     * Returns an empty string if there are none.
     * Such symbols are valid but are most likely typos, so they are worth warning the user about.
     */
    public String getUncommonSymbols() {
        String localPart = value.substring(0, getAtSignIndex());
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

    /**
     * Returns the position of the {@code @} that separates the two parts of this email.
     */
    private int getAtSignIndex() {
        int atIndex = value.indexOf('@');
        assert atIndex > 0 && atIndex == value.lastIndexOf('@') : "A valid email has exactly one @, after its name";
        return atIndex;
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
