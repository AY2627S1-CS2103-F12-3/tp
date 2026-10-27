package seedu.address.logic;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_ROLE_DISPLAYED_INDEX = "Role index must refer to a displayed role.";
    public static final String MESSAGE_DUPLICATE_ROLE = "A role named “%1$s” already exists.";
    public static final String MESSAGE_ROLE_LIST_NOT_DISPLAYED =
            "The role list is not displayed. Use list role first.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The candidate index provided is invalid.";
    public static final String MESSAGE_PERSON_LIST_NOT_DISPLAYED =
            "No candidate list is displayed. Run list before using a candidate index command.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d candidate(s) listed!";
    public static final String MESSAGE_DUPLICATE_EMAIL = "This email is already used by the candidate %1$s.";
    public static final String MESSAGE_DUPLICATE_PHONE =
            "This phone number is already used by the candidate %1$s.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_UNCOMMON_EMAIL_SYMBOLS =
            "Warning: the email has unusual symbols (%1$s). Check that it is not a typo.";
    public static final String MESSAGE_DOTLESS_EMAIL_DOMAIN = "Warning: the email domain “%1$s” has no dot "
            + "(e.g. example.com). Check that it is not a typo.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns an error message naming {@code existingPerson} and the field that {@code person}
     * shares with them.
     */
    public static String getErrorMessageForDuplicatePerson(Person person, Person existingPerson) {
        requireAllNonNull(person, existingPerson);
        assert person.isSamePerson(existingPerson) : "Message requested for candidates who do not clash";

        String message = person.hasSameEmail(existingPerson)
                ? MESSAGE_DUPLICATE_EMAIL
                : MESSAGE_DUPLICATE_PHONE;
        return String.format(message, existingPerson.getName());
    }

    /**
     * Returns {@code successMessage} followed by a warning on a new line for each likely typo in {@code email}:
     * symbols that email providers do not issue, and a domain without a dot.
     * The email is still saved, because both are valid.
     */
    public static String appendEmailWarnings(String successMessage, Email email) {
        requireAllNonNull(successMessage, email);
        StringBuilder message = new StringBuilder(successMessage);

        String uncommonSymbols = email.getUncommonSymbols();
        if (!uncommonSymbols.isEmpty()) {
            message.append("\n").append(String.format(MESSAGE_UNCOMMON_EMAIL_SYMBOLS, uncommonSymbols));
        }
        if (email.hasDotlessDomain()) {
            message.append("\n").append(String.format(MESSAGE_DOTLESS_EMAIL_DOMAIN, email.getDomain()));
        }
        return message.toString();
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Role: ")
                .append(person.getRole())
                .append("; Note: ")
                .append(person.getNote())
                .append("; Interview: ")
                .append(person.getInterview())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
