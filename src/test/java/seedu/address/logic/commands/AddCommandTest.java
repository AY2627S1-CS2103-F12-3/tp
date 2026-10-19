package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded(SOFTWARE_ENGINEER);
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_roleInDifferentCase_addsPersonWithRoleTitle() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded(SOFTWARE_ENGINEER);
        Person personWithLowerCaseRole = new PersonBuilder().withRole("software engineer").build();

        new AddCommand(personWithLowerCaseRole).execute(modelStub);

        Person expectedPerson = new PersonBuilder().withRole(SOFTWARE_ENGINEER.getTitle().value).build();
        assertEquals(List.of(expectedPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_roleNotFound_throwsCommandException() {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);

        String expectedMessage = String.format(AddCommand.MESSAGE_ROLE_NOT_FOUND, validPerson.getRole());
        assertThrows(CommandException.class, expectedMessage, () -> addCommand.execute(modelStub));
        assertTrue(modelStub.personsAdded.isEmpty());
    }

    @Test
    public void execute_closedRole_throwsCommandException() {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded(CLOSED_DESIGNER);
        Person personForClosedRole = new PersonBuilder().withRole(CLOSED_DESIGNER.getTitle().value).build();
        AddCommand addCommand = new AddCommand(personForClosedRole);

        String expectedMessage = String.format(AddCommand.MESSAGE_ROLE_CLOSED, CLOSED_DESIGNER.getTitle());
        assertThrows(CommandException.class, expectedMessage, () -> addCommand.execute(modelStub));
        assertTrue(modelStub.personsAdded.isEmpty());
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        String expectedMessage = String.format(Messages.MESSAGE_DUPLICATE_EMAIL, validPerson.getName());
        assertThrows(CommandException.class, expectedMessage, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSamePerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSamePersonExcluding(Person person, Person excludedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasRole(Role role) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Role> findRole(RoleTitle title) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addRole(Role role) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteRole(Role role) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setRole(Role target, Role editedRole) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Role> getFilteredRoleList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredRoleList(Predicate<Role> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public Optional<Person> findSamePerson(Person person) {
            requireNonNull(person);
            return Optional.of(this.person).filter(person::isSamePerson);
        }
    }

    /**
     * A Model stub that contains the given roles and always accepts the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();
        private final List<Role> roles;

        ModelStubAcceptingPersonAdded(Role... roles) {
            this.roles = List.of(roles);
        }

        @Override
        public Optional<Role> findRole(RoleTitle title) {
            requireNonNull(title);
            return roles.stream()
                    .filter(role -> role.getTitle().isSameTitle(title))
                    .findFirst();
        }

        @Override
        public Optional<Person> findSamePerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().filter(person::isSamePerson).findFirst();
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
