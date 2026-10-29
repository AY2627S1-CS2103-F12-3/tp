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

    @Test
    public void execute_possiblySamePhoneAsExistingPerson_successWithWarning() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person personWithCountryCode = new PersonBuilder().withPhone("+65" + personInList.getPhone().value).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithCountryCode);
        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithCountryCode))
                + "\n" + String.format(Messages.MESSAGE_POSSIBLE_DUPLICATE_PHONE,
                personInList.getName() + " (" + personInList.getPhone() + ")");

        assertCommandSuccess(new AddCommand(personWithCountryCode), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_possiblySamePhoneAsTwoPersons_namesBothInListOrder() {
        Person firstPerson = model.getAddressBook().getPersonList().get(0);
        Person personWithCountryCode = new PersonBuilder().withName("Code Person").withEmail("code@example.com")
                .withPhone("+65" + firstPerson.getPhone().value).build();
        model.addPerson(personWithCountryCode);
        Person personWithCodeWithoutPlus = new PersonBuilder().withPhone("65" + firstPerson.getPhone().value).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithCodeWithoutPlus);
        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS,
                Messages.format(personWithCodeWithoutPlus))
                + "\n" + String.format(Messages.MESSAGE_POSSIBLE_DUPLICATE_PHONE,
                firstPerson.getName() + " (" + firstPerson.getPhone() + "), Code Person (+65"
                + firstPerson.getPhone() + ")");

        assertCommandSuccess(new AddCommand(personWithCodeWithoutPlus), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unusualPhone_successWithWarning() {
        Person personWithUnusualPhone = new PersonBuilder().withPhone("12345678").build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(personWithUnusualPhone);
        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(personWithUnusualPhone))
                + "\n" + Messages.MESSAGE_UNUSUAL_PHONE;

        assertCommandSuccess(new AddCommand(personWithUnusualPhone), model, expectedMessage, expectedModel);
    }

}
