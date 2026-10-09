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
 * Updates status of candidate as a tag.
 */
public class StatusCommand extends Command {
    public static final String COMMAND_WORD = "status";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds status of candidate. "
            + "by the index number used in the displayed candidate list. "
            + "Status is displayed as a tag. Adding a status does not overwrite existing tags.\n"
            + "Parameters: INDEX (must be a positive integer) STATUS "
            + "Example: " + COMMAND_WORD + " 1 accepted ";

    public static final String MESSAGE_ADD_STATUS_SUCCESS = "Status of candidate: %1$s";

    private final Index index;
    private final Tag status;

    /**
     * Creates a new StatusCommand that appends a Status to person's currently assigned tags.
     *
     * @param index of the person in the filtered person list to edit
     * @param status status (currently implemented as a tag) to assign to the candidate
     */
    public StatusCommand(Index index, Tag status) {
        requireNonNull(index);
        requireNonNull(status);

        this.index = index;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

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
     * Creates and returns a {@code Person} with the updated status.
     */
    private static Person addStatusToPerson(Person personToEdit, Tag status) {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        Address address = personToEdit.getAddress();
        Note note = personToEdit.getNote();
        Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());

        updatedTags.add(status);

        return new Person(name, phone, email, address, note, updatedTags);
    }

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
