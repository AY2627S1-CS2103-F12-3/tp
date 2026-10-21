package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.RoleTitle;

/**
 * Parses a displayed role index and optional replacement title and experience level.
 */
public class EditRoleCommandParser implements Parser<EditRoleCommand> {

    @Override
    public EditRoleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_EXPERIENCE_LEVEL, PREFIX_ROLE_TITLE);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditRoleCommand.MESSAGE_USAGE);
        Index index = ParserUtil.parseRoleIndex(argMultimap.getPreamble(), invalidFormatMessage);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ROLE_TITLE, PREFIX_EXPERIENCE_LEVEL);
        if (argMultimap.getValue(PREFIX_ROLE_TITLE).isEmpty()
                && argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).isEmpty()) {
            throw new ParseException(EditRoleCommand.MESSAGE_NOT_EDITED);
        }
        RoleTitle title = argMultimap.getValue(PREFIX_ROLE_TITLE).isPresent()
                ? ParserUtil.parseRoleTitle(argMultimap.getValue(PREFIX_ROLE_TITLE).get()) : null;
        ExperienceLevel experienceLevel = argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).isPresent()
                ? ParserUtil.parseExperienceLevel(argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).get()) : null;
        return new EditRoleCommand(index, title, experienceLevel);
    }
}
