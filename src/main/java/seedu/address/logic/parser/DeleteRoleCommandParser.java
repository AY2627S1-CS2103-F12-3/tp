package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the displayed role index for a {@code DeleteRoleCommand}.
 */
public class DeleteRoleCommandParser implements Parser<DeleteRoleCommand> {

    @Override
    public DeleteRoleCommand parse(String args) throws ParseException {
        return new DeleteRoleCommand(ParserUtil.parseRoleIndex(args, DeleteRoleCommand.MESSAGE_EXTRA_TEXT));
    }
}
