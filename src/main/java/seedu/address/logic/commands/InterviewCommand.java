package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
 * Scheduling an interview that looks like a mistake still succeeds, but the result message points it out.
 */
public class InterviewCommand extends Command {

    public static final String COMMAND_WORD = "interview";
    public static final String FORMAT = COMMAND_WORD + " INDEX [d/YYYY-MM-DD HH:MM]";
    public static final String EXAMPLE = COMMAND_WORD + " 1 d/2026-10-20 14:00";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Schedules an interview for the candidate identified by the index number used in the displayed "
            + "candidate list, replacing any existing interview. Leave out d/ to clear the interview.\n"
            + "Parameters: INDEX (must be a positive integer) [d/YYYY-MM-DD HH:MM]\n"
            + "Example: " + EXAMPLE;

    public static final String MESSAGE_SCHEDULE_SUCCESS = "Scheduled interview for %1$s on %2$s.";
    public static final String MESSAGE_CLEAR_SUCCESS = "Cleared interview for %1$s.";
    public static final String MESSAGE_FAR_FUTURE_WARNING = "Warning: date is over a year ahead; check the year.";
    public static final String MESSAGE_PAST_WARNING = "Warning: interview is in the past; check the date.";
    public static final String MESSAGE_FINAL_STATUS_WARNING = "Warning: candidate is %1$s.";
    public static final String MESSAGE_SAME_TIME = "Also at this time: %1$s.";

    /** Interviews further ahead than this are more likely to have a mistyped year. */
    private static final int FAR_FUTURE_YEARS = 1;
    /** Limits a same-time reminder to the first few candidates. */
    private static final int MAX_SAME_TIME_NAMES = 2;
    /** Keeps an unusually long candidate name from filling the result display. */
    private static final int MAX_DISPLAYED_NAME_LENGTH = 30;
    private static final String TRUNCATION_MARK = "…";

    private static final Logger logger = LogsCenter.getLogger(InterviewCommand.class);

    private final Index index;
    private final Interview interview;
    private final Clock clock;

    /**
     * Creates an InterviewCommand to set the interview of the person at {@code index} to {@code interview}.
     *
     * @param index Index of the person in the filtered person list.
     * @param interview New interview of the person, which is {@link Interview#NONE} to clear the existing one.
     */
    public InterviewCommand(Index index, Interview interview) {
        this(index, interview, Clock.systemDefaultZone());
    }

    /**
     * Creates an InterviewCommand that reads today's date from {@code clock}, so tests can fix the date.
     *
     * @param index Index of the person in the filtered person list.
     * @param interview New interview of the person, which is {@link Interview#NONE} to clear the existing one.
     * @param clock Clock that gives today's date, used to generate date warnings.
     */
    InterviewCommand(Index index, Interview interview, Clock clock) {
        requireNonNull(index);
        requireNonNull(interview);
        requireNonNull(clock);

        this.index = index;
        this.interview = interview;
        this.clock = clock;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.getErrorMessageForInvalidPersonIndex(index, lastShownList.size()));
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        assert personToEdit != null : "The displayed candidate list should not contain null";
        Person editedPerson = personToEdit.withInterview(interview);

        model.setPerson(personToEdit, editedPerson);
        logger.info(String.format("Interview of candidate at index %d set to \"%s\"",
                index.getOneBased(), interview));
        return new CommandResult(generateResultMessage(model, editedPerson));
    }

    /**
     * Returns the success message for {@code editedPerson}, followed by a line for each warning, if any.
     */
    private String generateResultMessage(Model model, Person editedPerson) {
        List<String> lines = new ArrayList<>();
        lines.add(generateSuccessMessage(editedPerson));

        // Clearing an interview can never be a scheduling mistake, so only a new interview is checked.
        // A final status comes first, as it suggests the interview may not be needed at all.
        if (!interview.isEmpty()) {
            findFinalStatusWarning(editedPerson).ifPresent(lines::add);
            findDateWarning().ifPresent(lines::add);
            findSameTimeCandidates(model, editedPerson).ifPresent(lines::add);
        }
        return String.join("\n", lines);
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

    /**
     * Returns a warning if the interview is before today or more than {@value #FAR_FUTURE_YEARS} year ahead.
     * The interview is still saved because past dates may be intentional historical records.
     */
    private Optional<String> findDateWarning() {
        LocalDate interviewDate = getInterviewDate();
        LocalDate today = LocalDate.now(clock);
        if (interviewDate.isBefore(today)) {
            return Optional.of(MESSAGE_PAST_WARNING);
        }
        if (interviewDate.isAfter(today.plusYears(FAR_FUTURE_YEARS))) {
            return Optional.of(MESSAGE_FAR_FUTURE_WARNING);
        }
        return Optional.empty();
    }

    /**
     * Returns a warning if {@code editedPerson} already has a final outcome, such as {@code REJECTED},
     * and the interview is today or later, since an upcoming interview usually means they are still in
     * the recruitment process. A past interview is not warned about, as it records what already happened.
     */
    private Optional<String> findFinalStatusWarning(Person editedPerson) {
        boolean isPastInterview = getInterviewDate().isBefore(LocalDate.now(clock));
        if (editedPerson.getStatus().isPending() || isPastInterview) {
            return Optional.empty();
        }
        return Optional.of(String.format(MESSAGE_FINAL_STATUS_WARNING, editedPerson.getStatus()));
    }

    /**
     * Returns the date of the interview being scheduled, which must not be {@link Interview#NONE}.
     */
    private LocalDate getInterviewDate() {
        return interview.getDateTime().orElseThrow().toLocalDate();
    }

    /**
     * Returns a line naming up to {@value #MAX_SAME_TIME_NAMES} other candidates with an interview at the
     * same date and time, if any. All candidates are checked, including those hidden by the current filter.
     */
    private Optional<String> findSameTimeCandidates(Model model, Person editedPerson) {
        List<String> names = model.getAddressBook().getPersonList().stream()
                .filter(candidate -> !candidate.isSamePerson(editedPerson))
                .filter(candidate -> candidate.getInterview().equals(interview))
                .map(candidate -> candidate.getName().fullName)
                .toList();
        if (names.isEmpty()) {
            return Optional.empty();
        }

        int displayedNameCount = Math.min(names.size(), MAX_SAME_TIME_NAMES);
        String displayedNames = names.stream()
                .limit(displayedNameCount)
                .map(InterviewCommand::truncateName)
                .collect(Collectors.joining(", "));
        int otherCandidates = names.size() - displayedNameCount;
        if (otherCandidates > 0) {
            displayedNames += String.format(" (+%d others)", otherCandidates);
        }
        return Optional.of(String.format(MESSAGE_SAME_TIME, displayedNames));
    }

    /**
     * Returns {@code name} shortened to fit the result display, if necessary.
     */
    private static String truncateName(String name) {
        if (name.length() <= MAX_DISPLAYED_NAME_LENGTH) {
            return name;
        }
        return name.substring(0, MAX_DISPLAYED_NAME_LENGTH - TRUNCATION_MARK.length()) + TRUNCATION_MARK;
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

        // The clock is not compared, as it only supplies today's date and is not part of the user's request.
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
