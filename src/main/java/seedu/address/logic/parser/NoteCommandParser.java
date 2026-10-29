package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Note;

/**
 * Parses input arguments and creates a new NoteCommand object
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    /**
     * Separates the index from the rest of the arguments, which form the note.
     * The note is not tokenized by prefix, so text such as "t/" or "n/" stays in the note.
     */
    private static final Pattern NOTE_COMMAND_FORMAT = Pattern.compile("(?<index>\\S+)(?<note>.*)");

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public NoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = NOTE_COMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }

        Index index = parseNoteIndex(matcher.group("index"));
        Note note = ParserUtil.parseNote(matcher.group("note"));
        return new NoteCommand(index, note);
    }

    /**
     * Parses {@code index} into an {@code Index}, reporting an invalid index with the note command's usage.
     *
     * @throws ParseException if {@code index} is not a valid index.
     */
    private static Index parseNoteIndex(String index) throws ParseException {
        try {
            return ParserUtil.parseIndex(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE), pe);
        }
    }

}
