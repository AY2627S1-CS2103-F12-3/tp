package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.role.Role;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a candidate to RecruiterBuddy. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + PREFIX_ROLE_TITLE + "ROLE "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_ROLE_TITLE + "Software Engineer "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New candidate added: %1$s";
    public static final String MESSAGE_ROLE_NOT_FOUND =
            "No role named “%1$s” exists. Add the role first, then add the candidate.";
    public static final String MESSAGE_ROLE_CLOSED =
            "The role “%1$s” is closed, so no new candidates can be added to it.";

    private static final Logger logger = LogsCenter.getLogger(AddCommand.class);

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Person> existingPerson = model.findSamePerson(toAdd);
        if (existingPerson.isPresent()) {
            String message = Messages.getErrorMessageForDuplicatePerson(toAdd, existingPerson.get());
            logger.info("Rejected duplicate candidate " + toAdd.getName() + ": " + message);
            throw new CommandException(message);
        }

        Role role = findOpenRole(model);

        // Saves the role's own spelling of the title, so each role is shown and stored in one way.
        Person personToAdd = toAdd.withRole(role.getTitle());
        model.addPerson(personToAdd);
        String successMessage = String.format(MESSAGE_SUCCESS, Messages.format(personToAdd));
        logger.info("Added candidate " + personToAdd.getName());
        String messageWithPhoneWarnings = PhoneChecks.appendPhoneWarnings(successMessage, model, personToAdd);
        return new CommandResult(EmailChecks.appendEmailWarnings(messageWithPhoneWarnings, personToAdd.getEmail()));
    }

    /**
     * Returns the open role that the candidate to add is applying for.
     *
     * @throws CommandException if no such role exists or the role is closed.
     */
    private Role findOpenRole(Model model) throws CommandException {
        Role role = model.findRole(toAdd.getRole())
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_ROLE_NOT_FOUND, toAdd.getRole())));

        if (role.isClosed()) {
            throw new CommandException(String.format(MESSAGE_ROLE_CLOSED, role.getTitle()));
        }
        return role;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
