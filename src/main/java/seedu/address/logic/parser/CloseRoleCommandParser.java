package seedu.address.logic.parser;

import seedu.address.logic.commands.CloseRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the displayed role index for a {@code CloseRoleCommand}.
 */
public class CloseRoleCommandParser implements Parser<CloseRoleCommand> {

    @Override
    public CloseRoleCommand parse(String args) throws ParseException {
        return new CloseRoleCommand(ParserUtil.parseRoleIndex(args, CloseRoleCommand.MESSAGE_EXTRA_TEXT));
    }
}
