package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.HELP_TOPICS;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.RoleCommandParser;

public class CommandGuideTest {
    @Test
    public void getOverview_supportedCommands_containsEveryCommandOnce() {
        String overview = CommandGuide.getOverview();
        for (String command : HELP_TOPICS) {
            assertEquals(1, overview.lines().filter(line -> line.startsWith(command + ": ")).count(), command);
        }
        assertTrue(overview.startsWith("RecruiterBuddy command guide\n"));
        assertTrue(overview.contains("INDEX refers to the number in the relevant displayed list."));
    }

    @Test
    public void getOverview_existingUsage_containsCurrentFormatsAndRoleRules() {
        String overview = CommandGuide.getOverview();
        for (String usage : List.of(AddCommand.MESSAGE_USAGE, EditCommand.MESSAGE_USAGE, DeleteCommand.MESSAGE_USAGE,
                NoteCommand.MESSAGE_USAGE, InterviewCommand.MESSAGE_USAGE, StatusCommand.MESSAGE_USAGE,
                FindCommand.MESSAGE_USAGE, RoleCommandParser.MESSAGE_USAGE, HelpCommand.MESSAGE_USAGE)) {
            assertTrue(overview.contains(usage), usage);
        }
        assertTrue(overview.contains("Closing requires no pending candidates"));
        assertTrue(overview.contains("Deleting requires no associated candidates"));
        assertTrue(overview.contains("At most one candidate per role may be HIRED"));
    }

    @Test
    public void getOverview_examples_parseAsSupportedCommands() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        List<String> examples = CommandGuide.getOverview().lines()
                .filter(line -> line.startsWith("Example: ") || line.startsWith("Examples: "))
                .map(line -> line.substring(line.indexOf(": ") + 2))
                .flatMap(line -> List.of(line.split("; ")).stream())
                .toList();
        assertEquals(20, examples.size());
        for (String example : examples) {
            assertNotNull(parser.parseCommand(example), example);
        }
    }

    @Test
    public void findHelp_unknownTopic_returnsEmpty() {
        assertTrue(CommandGuide.findHelp("mystery").isEmpty());
        assertTrue(CommandGuide.findHelp("").isEmpty());
    }

    @Test
    public void findHelp_nullTopic_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CommandGuide.findHelp(null));
    }

    @Test
    public void findHelp_mixedCaseAndTurkishLocale_returnsSameInstructions() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            for (String command : HELP_TOPICS) {
                assertEquals(CommandGuide.findHelp(command), CommandGuide.findHelp(command.toUpperCase(Locale.ROOT)));
            }
        } finally {
            Locale.setDefault(original);
        }
    }
}
