package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;

/**
 * Replaces the note of a person identified using its displayed index from the address book.
 * An empty note clears the person's existing note.
 */
public class NoteCommand extends Command {

    public static final String COMMAND_WORD = "note";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Replaces the note of the person identified by the index number used in the displayed person list. "
            + "Everything after the index is saved as the note. Leave NOTE empty to clear the existing note.\n"
            + "Parameters: INDEX (must be a positive integer) [NOTE]\n"
            + "Example: " + COMMAND_WORD + " 1 Interview 2026-10-20 14:00";

    public static final String MESSAGE_UPDATE_NOTE_SUCCESS = "Updated note of person: %1$s";
    public static final String MESSAGE_CLEAR_NOTE_SUCCESS = "Cleared note of person: %1$s";

    private final Index index;
    private final Note note;

    /**
     * Creates a NoteCommand to replace the note of the person at {@code index} with {@code note}.
     *
     * @param index Index of the person in the filtered person list.
     * @param note New note of the person, which may be empty to clear the existing note.
     */
    public NoteCommand(Index index, Note note) {
        requireNonNull(index);
        requireNonNull(note);

        this.index = index;
        this.note = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), note, personToEdit.getTags());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Returns the success message for {@code editedPerson}, stating whether the note was updated or cleared.
     */
    private String generateSuccessMessage(Person editedPerson) {
        String message = note.isEmpty() ? MESSAGE_CLEAR_NOTE_SUCCESS : MESSAGE_UPDATE_NOTE_SUCCESS;
        return String.format(message, Messages.format(editedPerson));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NoteCommand otherNoteCommand)) {
            return false;
        }

        return index.equals(otherNoteCommand.index)
                && note.equals(otherNoteCommand.note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("note", note)
                .toString();
    }
}
