package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import seedu.address.logic.parser.RoleCommandParser;

/**
 * Provides offline instructions for the commands supported by RecruiterBuddy.
 */
public final class CommandGuide {
    private static final Map<String, String> COMMAND_HELP = createCommandHelp();

    private CommandGuide() {
    }

    /**
     * Returns the purpose, syntax and examples of every supported command in a stable order.
     */
    public static String getOverview() {
        return "RecruiterBuddy command guide\n"
                + "Scroll to read all commands. INDEX refers to the number in the relevant displayed list.\n\n"
                + String.join("\n\n", COMMAND_HELP.values());
    }

    /**
     * Returns instructions for {@code commandWord}, ignoring case, or empty if it is unsupported.
     */
    public static Optional<String> findHelp(String commandWord) {
        requireNonNull(commandWord);
        return Optional.ofNullable(COMMAND_HELP.get(commandWord.toLowerCase(Locale.ROOT)));
    }

    /**
     * Builds the instructions from existing usage definitions where they already describe the command.
     */
    private static Map<String, String> createCommandHelp() {
        Map<String, String> help = new LinkedHashMap<>();
        help.put(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE
                + "\nThe role must exist and be OPEN. New candidates start with status RESUME.");
        help.put(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE
                + "\nProvide at least one field. A different destination role must be OPEN."
                + " Moving a candidate preserves their status and other details.");
        help.put(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE);
        help.put(NoteCommand.COMMAND_WORD, NoteCommand.MESSAGE_USAGE);
        help.put(InterviewCommand.COMMAND_WORD, InterviewCommand.MESSAGE_USAGE);
        help.put(StatusCommand.COMMAND_WORD, StatusCommand.MESSAGE_USAGE
                + "\nEach candidate has one status. At most one candidate per role may be HIRED."
                + " A rejected batch changes no candidates.");
        help.put(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE
                + "\nFilter by candidate names, roles or statuses. Use list to clear the candidate filter.");
        help.put(ListCommand.COMMAND_WORD, "list: Displays all candidates; list role displays all roles.\n"
                + "Parameters: [role]\nExamples: list; list role");
        help.put(RoleCommandParser.COMMAND_WORD, RoleCommandParser.MESSAGE_USAGE
                + "\nEach role has one vacancy. Closing requires no pending candidates and keeps its records."
                + " Deleting requires no associated candidates. Use list role to view role indices.");
        help.put(ClearCommand.COMMAND_WORD, "clear: Removes all candidates and roles.\n"
                + "Parameters: None\nExample: clear\nUse undo in the same session to restore the previous data.");
        help.put(UndoCommand.COMMAND_WORD, "undo: Reverts the most recent data-changing command in this session.\n"
                + "Parameters: None\nExample: undo\nHistory is not retained after restarting RecruiterBuddy.");
        help.put(RedoCommand.COMMAND_WORD, "redo: Reapplies the most recently undone change.\n"
                + "Parameters: None\nExample: redo\nA new data change clears redo history.");
        help.put(ExitCommand.COMMAND_WORD, "exit: Closes RecruiterBuddy.\nParameters: None\nExample: exit");
        help.put(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE
                + "\nThe Help menu and F1 show this same offline guide. Recruitment data and filters are unchanged.");
        return Collections.unmodifiableMap(help);
    }
}
