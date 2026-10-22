package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_roleInDifferentCase_savesRoleTitle() {
        Person personWithUpperCaseRole = new PersonBuilder().withRole("PRODUCT ANALYST").build();
        Person expectedPerson = new PersonBuilder().withRole(PRODUCT_ANALYST.getTitle().value).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(expectedPerson);

        assertCommandSuccess(new AddCommand(personWithUpperCaseRole), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)),
                expectedModel);
    }

    @Test
    public void execute_roleNotFound_throwsCommandException() {
        Person personWithUnknownRole = new PersonBuilder().withRole("Chief Executive").build();
        assertCommandFailure(new AddCommand(personWithUnknownRole), model,
                String.format(AddCommand.MESSAGE_ROLE_NOT_FOUND, personWithUnknownRole.getRole()));
    }

    @Test
    public void execute_closedRole_throwsCommandException() {
        Person personForClosedRole = new PersonBuilder().withRole(CLOSED_DESIGNER.getTitle().value).build();
        assertCommandFailure(new AddCommand(personForClosedRole), model,
                String.format(AddCommand.MESSAGE_ROLE_CLOSED, CLOSED_DESIGNER.getTitle()));
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                String.format(Messages.MESSAGE_DUPLICATE_EMAIL, personInList.getName()));
    }

    @Test
    public void execute_samePhoneAsExistingPerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person personWithSamePhone = new PersonBuilder().withPhone(personInList.getPhone().value).build();
        assertCommandFailure(new AddCommand(personWithSamePhone), model,
                String.format(Messages.MESSAGE_DUPLICATE_PHONE, personInList.getName()));
    }

}
