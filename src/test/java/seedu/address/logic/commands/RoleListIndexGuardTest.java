package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Note;
import seedu.address.model.person.Status;
import seedu.address.testutil.TypicalPersons;

public class RoleListIndexGuardTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        model.setRoleListDisplayed(true);
    }

    @Test
    public void candidateIndexCommandsWhileRoleListDisplayed_areRejected() {
        assertThrows(CommandException.class, () -> new DeleteCommand(INDEX_FIRST_PERSON).execute(model));
        assertThrows(CommandException.class, () -> new EditCommand(INDEX_FIRST_PERSON,
                new EditCommand.EditPersonDescriptor()).execute(model));
        assertThrows(CommandException.class, () -> new NoteCommand(INDEX_FIRST_PERSON,
                new Note("note")).execute(model));
        assertThrows(CommandException.class, () -> new StatusCommand(Set.of(INDEX_FIRST_PERSON),
                Status.HIRED).execute(model));
    }
}
