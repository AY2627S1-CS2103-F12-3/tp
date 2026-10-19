package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_ROLE_DISPLAYED_INDEX = "Role index must refer to a displayed role.";
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
        assert person.isSamePerson(existingPerson);

        String message = person.hasSameEmail(existingPerson)
                ? MESSAGE_DUPLICATE_EMAIL
                : MESSAGE_DUPLICATE_PHONE;
        return String.format(message, existingPerson.getName());
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
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
