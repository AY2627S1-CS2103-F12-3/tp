package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailureWithReason;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.NoteCommand;
import seedu.address.model.person.Note;

public class NoteCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE);

    private NoteCommandParser parser = new NoteCommandParser();

    @Test
    public void parse_indexAndNote_success() {
        assertParseSuccess(parser, "1 Interview 2026-10-20 14:00",
                new NoteCommand(INDEX_FIRST_PERSON, new Note("Interview 2026-10-20 14:00")));

        // surrounding whitespace trimmed, inner whitespace kept
        assertParseSuccess(parser, "  1   Call back  after  lunch  ",
                new NoteCommand(INDEX_FIRST_PERSON, new Note("Call back  after  lunch")));
    }

    @Test
    public void parse_noteWithPrefixLikeText_noteKeptAsTyped() {
        String note = "Discuss t/java, Referee: n/Alice, nt/x, said \"hi\", scored 8/10";
        assertParseSuccess(parser, "1 " + note, new NoteCommand(INDEX_FIRST_PERSON, new Note(note)));
    }

    @Test
    public void parse_indexOnly_emptyNote() {
        assertParseSuccess(parser, "1", new NoteCommand(INDEX_FIRST_PERSON, new Note("")));
        assertParseSuccess(parser, " 1   ", new NoteCommand(INDEX_FIRST_PERSON, new Note("")));
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        // non-numeric index
        assertParseFailureWithReason(parser, "a Call back", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);

        // note joined to index without a space
        assertParseFailureWithReason(parser, "1Call back", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);

        // zero index
        assertParseFailureWithReason(parser, "0 Call back", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);

        // negative index
        assertParseFailureWithReason(parser, "-1 Call back", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);
    }
}
