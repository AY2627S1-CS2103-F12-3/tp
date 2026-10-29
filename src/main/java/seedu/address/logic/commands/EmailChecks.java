package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.model.person.Email;

/**
 * Finds email problems that are worth a warning but should not block a candidate change.
 * Invalid emails are rejected elsewhere; these checks only add notes to a successful command's message.
 */
final class EmailChecks {

    private static final Logger logger = LogsCenter.getLogger(EmailChecks.class);

    private EmailChecks() {
    }

    /**
     * Returns {@code message} followed by a note for each likely typo in {@code email}, one per line,
     * or {@code message} unchanged if there is none.
     * The likely typos are symbols that email providers do not issue, and a domain without a dot.
     * Both are valid, so the email is still saved.
     */
    static String appendEmailWarnings(String message, Email email) {
        requireAllNonNull(message, email);
        List<String> warnings = new ArrayList<>();

        String uncommonSymbols = email.getUncommonSymbols();
        if (!uncommonSymbols.isEmpty()) {
            warnings.add(String.format(Messages.MESSAGE_UNCOMMON_EMAIL_SYMBOLS, uncommonSymbols));
        }
        if (email.hasDotlessDomain()) {
            warnings.add(String.format(Messages.MESSAGE_DOTLESS_EMAIL_DOMAIN, email.getDomain()));
        }

        if (!warnings.isEmpty()) {
            logger.info("Candidate email saved with warnings: " + warnings);
        }
        return Messages.appendWarnings(message, warnings);
    }
}
