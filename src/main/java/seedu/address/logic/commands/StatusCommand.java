package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Arrays;
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
import seedu.address.model.role.RoleTitle;
import seedu.address.model.tag.Tag;

/**
 * Updates status of candidate.
 */
public class StatusCommand extends Command {
    public static final String COMMAND_WORD = "status";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates status of candidate "
            + "by the index number displayed in the candidate list. "
            + "Parameters: INDEX (must be a positive integer) STATUS\n"
            + "STATUS must be one of the following (not case sensitive): "
            + Arrays.stream(Status.values()).map(Status::name).collect(Collectors.joining(", ")) + ".\n"
            + "Example: " + COMMAND_WORD + " 1 " + Status.values()[0].name();

    public static final String MESSAGE_ADD_STATUS_SUCCESS = "Status of candidate: %1$s";

    private final Index index;
    private final Status status;

    /**
     * Creates a new StatusCommand that appends a Status to person's currently assigned tags.
     *
     * @param index of the person in the filtered person list to edit.
     * @param status status (currently implemented as a tag) to assign to the candidate.
     */
    public StatusCommand(Index index, Status status) {
        requireNonNull(index);
        requireNonNull(status);

        this.index = index;
        this.status = status;
    }

    /**
     * Executes StatusCommand based on current model.
     *
     * @param model {@code Model} which the command should operate on.
     * @return {@code CommandResult} containing the success message.
     * @throws CommandException if index is larger than size of displayed candidates list.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (model.isRoleListDisplayed()) {
            throw new CommandException(Messages.MESSAGE_PERSON_LIST_NOT_DISPLAYED);
        }
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = addStatusToPerson(personToEdit, status);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ADD_STATUS_SUCCESS, status));
    }

    /**
     * Creates a {@code Person} with the updated status.
     *
     * @param personToEdit {@code Person} to be updated.
     * @param newStatus {@code Status} new status of the candidate.
     * @return a new {@code Person} with updated status. All other attributes are identical.
     */
    private static Person addStatusToPerson(Person personToEdit, Status newStatus) {
        assert personToEdit != null;

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

        return index.equals(otherStatusCommand.index)
                && status.equals(otherStatusCommand.status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("status", status)
                .toString();
    }
}
