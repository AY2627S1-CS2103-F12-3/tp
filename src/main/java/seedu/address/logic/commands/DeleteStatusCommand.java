package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import seedu.address.model.tag.Tag;

/**
 * Deletes a status from a candidate.
 * Candidate is identified using its displayed index from the address book, and status is identified by its name.
 */
public class DeleteStatusCommand extends Command {

    public static final String COMMAND_WORD = "deleteStatus";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the status (identified by name) from "
            + "person (identified by index number in the displayed person list).\n"
            + "Parameters: INDEX (must be a positive integer) NAME\n"
            + "Example: " + COMMAND_WORD + " 1 accepted";

    public static final String MESSAGE_DELETE_STATUS_SUCCESS = "Deleted status: %1$s";
    public static final String MESSAGE_STATUS_NOT_FOUND = "Requested status could not be found.";

    private final Index targetIndex;
    private final Tag targetStatus;

    /**
     * Creates a {@code DeleteStatusCommand} that removes {@code targetStatus} from the person located at
     * {@code targetIndex} in the currently displayed list.
     */
    public DeleteStatusCommand(Index targetIndex, Tag targetStatus) {
        this.targetIndex = targetIndex;
        this.targetStatus = targetStatus;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());
        Person editedPerson = removeStatusFromPerson(personToEdit, targetStatus);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_DELETE_STATUS_SUCCESS, targetStatus.toString()));
    }

    /**
     * Creates and returns a {@code Person} with the status removed.
     */
    private static Person removeStatusFromPerson(Person personToEdit, Tag targetStatus) throws CommandException {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        Address address = personToEdit.getAddress();
        Note note = personToEdit.getNote();
        Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());

        if (!updatedTags.remove(targetStatus)) {
            throw new CommandException(MESSAGE_STATUS_NOT_FOUND);
        }

        return new Person(name, phone, email, address, note, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteStatusCommand otherDeleteStatusCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteStatusCommand.targetIndex)
                && targetStatus.equals(otherDeleteStatusCommand.targetStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("targetStatus", targetStatus)
                .toString();
    }
}
