package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds phone number problems that are worth a warning but should not block a candidate change.
 * Exact duplicates are rejected elsewhere; these checks only add warnings to a successful command's message.
 */
final class PhoneChecks {

    private static final Logger logger = LogsCenter.getLogger(PhoneChecks.class);

    private PhoneChecks() {
    }

    /**
     * Returns {@code message} followed by a warning for each phone number problem of {@code candidate},
     * one per line, or {@code message} unchanged if there is none.
     * Call this after {@code candidate} is stored in {@code model}, so that an edited candidate is
     * not compared with their own previous number. The stored candidate itself is never reported,
     * since only numbers that are not exactly the same count as possible duplicates.
     */
    static String appendPhoneWarnings(String message, Model model, Person candidate) {
        requireAllNonNull(message, model, candidate);
        List<String> warnings = new ArrayList<>();

        if (!candidate.getPhone().isPlausibleNumber()) {
            warnings.add(Messages.MESSAGE_UNUSUAL_PHONE);
        }

        String possibleDuplicatesText = model.getAddressBook().getPersonList().stream()
                .filter(candidate::hasPossiblySamePhone)
                .map(person -> person.getName() + " (" + person.getPhone() + ")")
                .collect(Collectors.joining(", "));
        if (!possibleDuplicatesText.isEmpty()) {
            warnings.add(String.format(Messages.MESSAGE_POSSIBLE_DUPLICATE_PHONE, possibleDuplicatesText));
        }

        if (!warnings.isEmpty()) {
            logger.info("Phone of candidate " + candidate.getName() + " saved with warnings: " + warnings);
        }
        return Messages.appendWarnings(message, warnings);
    }
}
