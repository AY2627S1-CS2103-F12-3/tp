package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Finds candidates matching any supplied name keyword, role keyword, or candidate status.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {
    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all candidates matching any of "
            + "the specified name/role keywords or candidate statuses (case-insensitive).\n"
            + "Parameters: [KEYWORD ...] [r/ ROLE ...] [st/ STATUS ...]\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);

    private final NameContainsKeywordsPredicate predicate;

    /** Creates a find command with the given predicate. */
    public FindCommand(NameContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        logger.info("Applying candidate find filter");
        model.updateFilteredPersonList(predicate);
        String message = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                model.getFilteredPersonList().size());
        return new CommandResult(message + (predicate.hasFieldFilters()
                ? " List filter: " + predicate.getFilterDescription() : ""));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
