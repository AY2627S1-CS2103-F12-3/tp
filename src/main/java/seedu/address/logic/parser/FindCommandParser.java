package seedu.address.logic.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.role.RoleStatus;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {
    private static final Logger logger = LogsCenter.getLogger(FindCommandParser.class);

    private static final String MESSAGE_INVALID_STATUS =
            "Invalid role status. Use one of: " + java.util.Arrays.toString(RoleStatus.values()) + ".";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_ROLE_TITLE,
                new Prefix("st/"));

        List<String> names = Arrays.stream(map.getPreamble().split("\\s+"))
                .filter(s -> !s.isBlank())
                .toList();

        logger.info("Parsing find command with " + names.size() + " keyword(s)");

        List<String> roleKeywords = map.getAllValues(CliSyntax.PREFIX_ROLE_TITLE).stream()
                .flatMap(s -> Arrays.stream(s.trim().split("\\s+")))
                .filter(s -> !s.isBlank())
                .toList();

        List<String> statusValues = map.getAllValues(new Prefix("st/"));
        if (statusValues.stream().anyMatch(String::isBlank)) {
            throw new ParseException(MESSAGE_INVALID_STATUS);
        }

        Set<RoleStatus> statuses = new java.util.HashSet<>();
        for (String value : statusValues) {
            for (String status : value.trim().split("\\s+")) {
                statuses.add(parseStatus(status));
            }
        }

        return new FindCommand(new NameContainsKeywordsPredicate(
                names, roleKeywords, statuses, List.of()));
    }

    private RoleStatus parseStatus(String status) throws ParseException {
        try {
            return RoleStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(MESSAGE_INVALID_STATUS, exception);
        }
    }

}
