package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.role.ExperienceLevel;

/**
 * Parses a displayed role index and its replacement experience level.
 */
public class EditRoleCommandParser implements Parser<EditRoleCommand> {

    public static final String MESSAGE_MISSING_FIELD = "Missing required field: " + PREFIX_EXPERIENCE_LEVEL + ".";

    @Override
    public EditRoleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_EXPERIENCE_LEVEL, PREFIX_ROLE_TITLE);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditRoleCommand.MESSAGE_USAGE);
        Index index = ParserUtil.parseRoleIndex(argMultimap.getPreamble(), invalidFormatMessage);

        // Recognize r/ so a title edit cannot silently become part of the experience description.
        if (argMultimap.getValue(PREFIX_ROLE_TITLE).isPresent()) {
            throw new ParseException(invalidFormatMessage);
        }
        if (argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_FIELD);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_EXPERIENCE_LEVEL);
        ExperienceLevel experienceLevel = ParserUtil.parseExperienceLevel(
                argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).get());
        return new EditRoleCommand(index, experienceLevel);
    }
}
