package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Status;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.tag.Tag;

/**
 * Updates status of candidate.
 */
public class StatusCommand extends Command {
    public static final String COMMAND_WORD = "status";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates status of candidate(s) "
            + "by the index number(s) displayed in the candidate list. "
            + "Parameters: INDEX... (must be a positive integer) "
            + PREFIX_STATUS + "STATUS\n"
            + "STATUS must be one of the following (not case sensitive): "
            + Arrays.stream(Status.values()).map(Status::name).collect(Collectors.joining(", ")) + ".\n"
            + "Example: " + COMMAND_WORD + " 1 2 3 " + PREFIX_STATUS + Status.values()[0].name();

    public static final String MESSAGE_ADD_STATUS_SUCCESS = "Status of candidate: %1$s";
    public static final String MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE =
            CandidateRoleChecks.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE;
    public static final String MESSAGE_CANDIDATES_HIRED_SAME_ROLE =
            CandidateRoleChecks.MESSAGE_CANDIDATES_HIRED_SAME_ROLE;
    public static final String MESSAGE_PENDING_STATUS_FOR_CLOSED_ROLE =
            "Cannot set a pending status for %1$s because role %2$s is closed.";

    private final Set<Index> indices;
    private final Status status;

    /**
     * Creates a command that replaces the selected candidate's recruitment status.
     *
     * @param indices of the people in the filtered person list to edit.
     * @param status recruitment status to assign to the candidate.
     */
    public StatusCommand(Set<Index> indices, Status status) {
        requireAllNonNull(indices);
        requireNonNull(status);

        this.indices = indices;
        this.status = status;
    }

    /**
     * Executes StatusCommand based on current model.
     *
     * @param model {@code Model} which the command should operate on.
     * @return {@code CommandResult} containing the success message.
     * @throws CommandException if no candidate list is displayed, the index is invalid, a pending status is
     *     requested for a closed role, or another candidate for the same role is already hired when assigning
     *     {@code HIRED}.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (model.isRoleListDisplayed()) {
            throw new CommandException(Messages.MESSAGE_PERSON_LIST_NOT_DISPLAYED);
        }

        Set<Person> peopleToEdit = new HashSet<>();
        for (Index idx : indices) {
            if (idx.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            Person personToEdit = lastShownList.get(idx.getZeroBased());
            peopleToEdit.add(personToEdit);
        }

        verifyPendingStatusAllowed(model, peopleToEdit);
        verifyHiringAllowed(model, peopleToEdit);
        assert peopleToEdit.size() == indices.size() : "Each distinct index must resolve to a distinct candidate";

        for (Person p : peopleToEdit) {
            Person editedPerson = createNewStatusPerson(p, status);
            model.setPerson(p, editedPerson);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ADD_STATUS_SUCCESS, status));
    }

    /**
     * Checks that a pending status will not resume recruitment for a closed role.
     */
    private void verifyPendingStatusAllowed(Model model, Set<Person> peopleToEdit) throws CommandException {
        for (Person p : peopleToEdit) {
            if (status.isPending() && model.findRole(p.getRole()).filter(Role::isClosed).isPresent()) {
                throw new CommandException(String.format(MESSAGE_PENDING_STATUS_FOR_CLOSED_ROLE,
                        p.getName(), p.getRole()));
            }
        }
    }

    /**
     * Checks that assigning {@code HIRED} will not fill the same role more than once.
     */
    private void verifyHiringAllowed(Model model, Set<Person> peopleToEdit) throws CommandException {
        for (Person p : peopleToEdit) {
            if (status != Status.HIRED) {
                return;
            }
            boolean hasOtherAlreadyHiredCandidate = model.getAddressBook().getPersonList().stream()
                    .anyMatch(candidate -> !candidate.isSamePerson(p)
                            && candidate.getStatus() == Status.HIRED
                            && candidate.getRole().isSameTitle(p.getRole()));

            boolean hasOtherNowHiredCandidate = peopleToEdit.stream()
                    .anyMatch(candidate -> !candidate.isSamePerson(p)
                            && candidate.getRole().isSameTitle(p.getRole()));

            if (hasOtherAlreadyHiredCandidate) {
                throw new CommandException(String.format(MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE, p.getRole()));
            }

            if (hasOtherNowHiredCandidate) {
                throw new CommandException(String.format(MESSAGE_CANDIDATES_HIRED_SAME_ROLE, p.getRole()));
            }
        }
    }

    /**
     * Creates a {@code Person} with the updated status.
     *
     * @param personToEdit {@code Person} to be updated.
     * @param newStatus {@code Status} new status of the candidate.
     * @return a new {@code Person} with updated status. All other attributes are identical.
     */
    private static Person createNewStatusPerson(Person personToEdit, Status newStatus) {
        assert personToEdit != null : "Candidate to update must not be null";
        assert newStatus != null : "New status must not be null";

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        Address address = personToEdit.getAddress();
        RoleTitle role = personToEdit.getRole();
        Note note = personToEdit.getNote();
        Set<Tag> tags = personToEdit.getTags();

        return new Person(name, phone, email, address, role, note, newStatus, tags);
    }

    /**
     * Equality test for StatusCommand.
     * @return true if and only if they have same index and status.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StatusCommand otherStatusCommand)) {
            return false;
        }

        return indices.equals(otherStatusCommand.indices)
                && status.equals(otherStatusCommand.status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("indices", indices)
                .add("status", status)
                .toString();
    }
}
