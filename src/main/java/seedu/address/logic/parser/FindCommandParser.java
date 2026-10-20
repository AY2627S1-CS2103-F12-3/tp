package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.role.RoleStatus;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap map = ArgumentTokenizer.tokenize(trimmedArgs, CliSyntax.PREFIX_ROLE_TITLE,
                new Prefix("st/"));
        List<String> names = Arrays.stream(map.getPreamble().split("\\s+")).filter(s -> !s.isBlank()).toList();
        List<String> roleKeywords = map.getAllValues(CliSyntax.PREFIX_ROLE_TITLE).stream()
                .flatMap(s -> Arrays.stream(s.trim().split("\\s+"))).filter(s -> !s.isBlank()).toList();
        Set<RoleStatus> statuses = map.getAllValues(new Prefix("st/")).stream()
                .flatMap(s -> Arrays.stream(s.trim().split("\\s+")))
                .map(s -> RoleStatus.valueOf(s.toUpperCase())).collect(Collectors.toSet());
        return new FindCommand(new NameContainsKeywordsPredicate(names, roleKeywords, statuses, List.of()));
    }

}
