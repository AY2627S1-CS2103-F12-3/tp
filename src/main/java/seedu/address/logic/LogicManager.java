package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.role.Role;
import seedu.address.model.tag.Tag;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);
        AddressBook stateBeforeCommand = command.isUndoable()
                ? new AddressBook(model.getAddressBook()) : null;
        if (command.isUndoable()) {
            model.recordAddressBookState();
        }
        try {
            commandResult = command.execute(model);
            if (command.isUndoable()) {
                if (hasSameStoredValues(stateBeforeCommand, model.getAddressBook())) {
                    model.discardAddressBookState();
                } else {
                    model.commitAddressBookState();
                }
            }
        } catch (CommandException | RuntimeException e) {
            if (command.isUndoable()) {
                model.discardAddressBookState();
            }
            throw e;
        }

        // Viewing instructions must not depend on permission to save recruitment data.
        if (command instanceof HelpCommand) {
            return commandResult;
        }

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    private static boolean hasSameStoredValues(AddressBook first, seedu.address.model.ReadOnlyAddressBook second) {
        if (first.getPersonList().size() != second.getPersonList().size()
                || first.getRoleList().size() != second.getRoleList().size()) {
            return false;
        }
        for (int i = 0; i < first.getPersonList().size(); i++) {
            Person left = first.getPersonList().get(i);
            Person right = second.getPersonList().get(i);
            if (!left.getName().equals(right.getName()) || !left.getPhone().equals(right.getPhone())
                    || !left.getEmail().equals(right.getEmail()) || !left.getAddress().equals(right.getAddress())
                    || !left.getRole().equals(right.getRole()) || !left.getNote().equals(right.getNote())
                    || !left.getInterview().equals(right.getInterview()) || left.getStatus() != right.getStatus()
                    || !storedTagNames(left.getTags()).equals(storedTagNames(right.getTags()))) {
                return false;
            }
        }
        return first.getRoleList().equals(second.getRoleList());
    }

    private static Set<String> storedTagNames(Set<Tag> tags) {
        Set<String> names = new HashSet<>();
        for (Tag tag : tags) {
            names.add(tag.tagName);
        }
        return names;
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return model.getAddressBook().getPersonList();
    }

    @Override
    public ObservableList<Role> getFilteredRoleList() {
        return model.getFilteredRoleList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
