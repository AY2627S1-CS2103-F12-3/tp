package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;

import java.util.Arrays;

import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;

/**
 * Parses input arguments and creates a new {@code AddRoleCommand}.
 */
public class AddRoleCommandParser implements Parser<AddRoleCommand> {

    public static final String MESSAGE_MISSING_FIELD = "Missing required field: %1$s.";

    @Override
    public AddRoleCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_ROLE_TITLE, PREFIX_EXPERIENCE_LEVEL);

        Prefix missingPrefix = getMissingPrefix(argMultimap, PREFIX_ROLE_TITLE, PREFIX_EXPERIENCE_LEVEL);
        if (missingPrefix != null) {
            throw new ParseException(String.format(MESSAGE_MISSING_FIELD, missingPrefix));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ROLE_TITLE, PREFIX_EXPERIENCE_LEVEL);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddRoleCommand.MESSAGE_USAGE));
        }

        RoleTitle title = ParserUtil.parseRoleTitle(argMultimap.getValue(PREFIX_ROLE_TITLE).get());
        ExperienceLevel experienceLevel = ParserUtil.parseExperienceLevel(
                argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).get());
        Role role = new Role(title, experienceLevel);

        return new AddRoleCommand(role);
    }

    private static Prefix getMissingPrefix(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Arrays.stream(prefixes)
                .filter(prefix -> argumentMultimap.getValue(prefix).isEmpty())
                .findFirst()
                .orElse(null);
    }
}
