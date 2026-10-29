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
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;

/**
 * Replaces the note of a person identified using its displayed index from the address book.
 * An empty note clears the person's existing note.
 * The current candidate filter is kept, so a recruiter can work through a filtered list by its numbers.
 */
public class NoteCommand extends Command {

    public static final String COMMAND_WORD = "note";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Replaces the note of the candidate identified by the index number used in the displayed "
            + "candidate list. Everything after the index is saved as the note. "
            + "Leave NOTE empty to clear the existing note.\n"
            + "Parameters: INDEX (must be a positive integer) [NOTE]\n"
            + "Example: " + COMMAND_WORD + " 1 Prefers hybrid work";

    /*
     * A new note is not repeated in the message, since the candidate's card already shows it.
     * Replacing a note says so, so an existing note is never overwritten silently. Clearing suggests
     * undo and shows the cleared note last, since the user cannot see it anywhere else and a long note
     * cannot push the hint out of view.
     */
    public static final String MESSAGE_ADD_NOTE_SUCCESS = "Added note to %1$s.";
    public static final String MESSAGE_REPLACE_NOTE_SUCCESS = "Replaced note of %1$s.";
    public static final String MESSAGE_UNCHANGED_NOTE_SUCCESS = "Updated note of %1$s.";
    public static final String MESSAGE_CLEAR_NOTE_SUCCESS =
            "Cleared note of %1$s. Use undo to revert.\nPrevious note: %2$s";
    public static final String MESSAGE_NO_NOTE_TO_CLEAR = "%1$s has no note to clear.";

    private static final Logger logger = LogsCenter.getLogger(NoteCommand.class);

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
        Person editedPerson = personToEdit.withNote(note);
        assert editedPerson.getNote().equals(note) : "Edited person should hold the new note";

        model.setPerson(personToEdit, editedPerson);
        String noteChange = note.isEmpty() ? "cleared" : "updated";
        logger.info(String.format("Note of candidate at index %d (%s) %s",
                index.getOneBased(), editedPerson.getName(), noteChange));
        return new CommandResult(generateSuccessMessage(personToEdit.getNote(), editedPerson));
    }

    /**
     * Returns the success message for {@code editedPerson}, naming the candidate and saying whether the note
     * was added, replaced, kept the same or cleared. Clearing a note also shows the cleared note.
     *
     * @param previousNote The note the candidate had before this command.
     * @param editedPerson The candidate with the new note.
     */
    private String generateSuccessMessage(Note previousNote, Person editedPerson) {
        if (note.isEmpty()) {
            return previousNote.isEmpty()
                    ? String.format(MESSAGE_NO_NOTE_TO_CLEAR, editedPerson.getName())
                    : String.format(MESSAGE_CLEAR_NOTE_SUCCESS, editedPerson.getName(), previousNote);
        }

        if (previousNote.isEmpty()) {
            return String.format(MESSAGE_ADD_NOTE_SUCCESS, editedPerson.getName());
        }
        if (previousNote.equals(note)) {
            return String.format(MESSAGE_UNCHANGED_NOTE_SUCCESS, editedPerson.getName());
        }
        return String.format(MESSAGE_REPLACE_NOTE_SUCCESS, editedPerson.getName());
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
