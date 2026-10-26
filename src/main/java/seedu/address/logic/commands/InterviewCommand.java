package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Person;

/**
 * Schedules, replaces, or clears the interview of a candidate identified using its displayed index.
 * The current candidate filter is kept, so a recruiter can work through a filtered list by its numbers.
 */
public class InterviewCommand extends Command {

    public static final String COMMAND_WORD = "interview";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Schedules an interview for the candidate identified by the index number used in the displayed "
            + "candidate list, replacing any existing interview. Leave out d/ to clear the interview.\n"
            + "Parameters: INDEX (must be a positive integer) [d/YYYY-MM-DD HH:MM]\n"
            + "Example: " + COMMAND_WORD + " 1 d/2026-10-20 14:00";

    public static final String MESSAGE_SCHEDULE_SUCCESS = "Scheduled interview for %1$s on %2$s";
    public static final String MESSAGE_CLEAR_SUCCESS = "Cleared interview for %1$s";

    private static final Logger logger = LogsCenter.getLogger(InterviewCommand.class);

    private final Index index;
    private final Interview interview;

    /**
     * Creates an InterviewCommand to set the interview of the person at {@code index} to {@code interview}.
     *
     * @param index Index of the person in the filtered person list.
     * @param interview New interview of the person, which is {@link Interview#NONE} to clear the existing one.
     */
    public InterviewCommand(Index index, Interview interview) {
        requireNonNull(index);
        requireNonNull(interview);

        this.index = index;
        this.interview = interview;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        assert personToEdit != null : "The displayed candidate list should not contain null";
        Person editedPerson = personToEdit.withInterview(interview);

        model.setPerson(personToEdit, editedPerson);
        logger.info(String.format("Interview of candidate at index %d set to \"%s\"",
                index.getOneBased(), interview));
        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Returns the success message for {@code editedPerson}, stating whether the interview was scheduled or cleared.
     */
    private String generateSuccessMessage(Person editedPerson) {
        String name = editedPerson.getName().fullName;
        if (interview.isEmpty()) {
            return String.format(MESSAGE_CLEAR_SUCCESS, name);
        }
        return String.format(MESSAGE_SCHEDULE_SUCCESS, name, interview);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof InterviewCommand otherInterviewCommand)) {
            return false;
        }

        return index.equals(otherInterviewCommand.index)
                && interview.equals(otherInterviewCommand.interview);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("interview", interview)
                .toString();
    }
}
