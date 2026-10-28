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
            + "candidate list. "
            + "Everything after the index is saved as the note. Leave NOTE empty to clear the existing note.\n"
            + "Parameters: INDEX (must be a positive integer) [NOTE]\n"
            + "Example: " + COMMAND_WORD + " 1 Interview 2026-10-20 14:00";

    /*
     * Messages that remove an existing note suggest undo, so a note replaced or cleared by mistake is easy
     * to restore. The candidate's name comes first and the note last, so a long note cannot push the name
     * or the hint out of view.
     */
    public static final String MESSAGE_UPDATE_NOTE_SUCCESS = "Updated note of %1$s.\nNew note: %2$s";
    public static final String MESSAGE_REPLACE_NOTE_SUCCESS =
            "Replaced note of %1$s. Use undo to revert.\nNew note: %2$s";
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
        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getRole(), note, personToEdit.getInterview(),
                personToEdit.getStatus(), personToEdit.getTags());
        assert editedPerson.getNote().equals(note) : "Edited person should hold the new note";

        model.setPerson(personToEdit, editedPerson);
        logger.info("Note of candidate at index " + index.getOneBased() + " ("
                + editedPerson.getName() + ") " + (note.isEmpty() ? "cleared" : "updated"));
        return new CommandResult(generateSuccessMessage(personToEdit.getNote(), editedPerson));
    }

    /**
     * Returns the success message for {@code editedPerson}, naming the candidate.
     * A new note is shown after the message. Clearing a note shows the cleared note instead,
     * since the user cannot see it anywhere else.
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

        boolean isOtherNoteReplaced = !previousNote.isEmpty() && !previousNote.equals(note);
        String messageFormat = isOtherNoteReplaced ? MESSAGE_REPLACE_NOTE_SUCCESS : MESSAGE_UPDATE_NOTE_SUCCESS;
        return String.format(messageFormat, editedPerson.getName(), note);
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
