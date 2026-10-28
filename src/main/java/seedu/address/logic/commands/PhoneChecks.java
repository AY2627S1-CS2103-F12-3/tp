package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds phone number problems that are worth a warning but should not block a candidate change.
 * Exact duplicates are rejected elsewhere; these checks only add notes to a successful command's message.
 */
final class PhoneChecks {

    private PhoneChecks() {
    }

    /**
     * Returns {@code message} followed by a note for each phone number problem of {@code candidate},
     * one per line, or {@code message} unchanged if there is none.
     * Call this after {@code candidate} is stored in {@code model}, so that an edited candidate is not compared
     * with their own previous number. The stored candidate itself is never reported, since only numbers that are
     * not exactly the same count as possible duplicates.
     */
    static String appendPhoneWarnings(String message, Model model, Person candidate) {
        requireAllNonNull(message, model, candidate);
        List<String> warnings = new ArrayList<>();

        if (!candidate.getPhone().isPlausibleNumber()) {
            warnings.add(Messages.MESSAGE_UNUSUAL_PHONE);
        }

        String possibleDuplicates = model.getAddressBook().getPersonList().stream()
                .filter(candidate::hasPossiblySamePhone)
                .map(person -> person.getName() + " (" + person.getPhone() + ")")
                .collect(Collectors.joining(", "));
        if (!possibleDuplicates.isEmpty()) {
            warnings.add(String.format(Messages.MESSAGE_POSSIBLE_DUPLICATE_PHONE, possibleDuplicates));
        }

        if (warnings.isEmpty()) {
            return message;
        }
        return message + "\n" + String.join("\n", warnings);
    }
}
