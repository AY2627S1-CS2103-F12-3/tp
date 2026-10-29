package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandGuide;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;

public class HelpCommandParserTest {
    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noTopic_returnsOverview() throws Exception {
        for (String arguments : List.of("", " ", "\t \r\n")) {
            assertEquals(CommandGuide.getOverview(),
                    parser.parse(arguments).execute(new ModelManager()).getFeedbackToUser());
        }
    }

    @Test
    public void parse_oneTopic_trimsSurroundingWhitespace() throws Exception {
        for (String arguments : List.of("add", "  add  ", "\tadd\t", "\u2003add\u2003", " AdD ")) {
            assertEquals(CommandGuide.findHelp("add").orElseThrow(),
                    parser.parse(arguments).execute(new ModelManager()).getFeedbackToUser());
        }
    }

    @Test
    public void parse_multipleTopics_rejectsBeforeTopicLookup() {
        for (String arguments : List.of("list role", "role close 1", "add\tdelete", "unknown extra",
                " add \n role ")) {
            assertParseFailure(parser, arguments, HelpCommand.MESSAGE_TOO_MANY_ARGUMENTS);
        }
    }

    @Test
    public void parse_unknownTopic_commandReportsUnknownTopic() throws Exception {
        HelpCommand command = parser.parse(" unknown ");
        assertThrows(CommandException.class,
                "No help is available for “unknown”. Use help to view all commands.", () ->
                        command.execute(new ModelManager()));
    }
}
