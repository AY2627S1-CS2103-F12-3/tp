package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.RoleStatus;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;
import seedu.address.testutil.TypicalRoles;

public class EditCandidateRoleCommandTest {

    @Test
    public void execute_openOrClosedSource_preservesEveryStatusAndOtherDetails() {
        for (RoleStatus sourceStatus : RoleStatus.values()) {
            for (Status status : Status.values()) {
                Person original = new PersonBuilder().withName("Alice").withNote("Interview tomorrow")
                        .withTags("referral").withStatus(status).build();
                Model model = createModel(original);
                model.setRole(SOFTWARE_ENGINEER, new RoleBuilder(SOFTWARE_ENGINEER).withStatus(sourceStatus).build());

                assertEditSuccess(model, original, roleDescriptor("  product analyst  "),
                        original.withRole(PRODUCT_ANALYST.getTitle()));
            }
        }
    }

    @Test
    public void execute_combinedEdit_namesUpdatedCandidateAndPreservesStatusAndNote() {
        Person original = new PersonBuilder().withName("Alice").withNote("Keep this note")
                .withStatus(Status.HIRED).build();
        Model model = createModel(original);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRole("Product Analyst")
                .withName("Updated Alice").withPhone("91234567").withEmail("updated@example.com")
                .withAddress("New address").withTags("updated").build();
        Person expected = new PersonBuilder(original).withRole("Product Analyst").withName("Updated Alice")
                .withPhone("91234567").withEmail("updated@example.com").withAddress("New address")
                .withTags("updated").build();

        assertEditSuccess(model, original, descriptor, expected);
    }

    @Test
    public void execute_filteredList_usesDisplayedCandidateAndResetsFilterAfterSuccess() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder(BENSON).withName("Bob").build();
        Model model = createModel(alice, bob);
        model.updateFilteredPersonList(bob::equals);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updatedBob = bob.withRole(PRODUCT_ANALYST.getTitle());
        expected.setPerson(bob, updatedBob);

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, roleDescriptor("Product Analyst")), model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(updatedBob)), expected);
        assertEquals(2, model.getFilteredPersonList().size());
        assertEquals(alice, model.getAddressBook().getPersonList().getFirst());
    }

    @Test
    public void execute_hiddenDestinationHire_rejectsHiredTransferWithoutChangingState() {
        Person alice = new PersonBuilder().withName("Alice").withStatus(Status.HIRED).build();
        Person bob = new PersonBuilder(BENSON).withName("Bob").withRole("Product Analyst")
                .withStatus(Status.HIRED).build();
        Model model = createModel(alice, bob);
        model.updateFilteredPersonList(alice::equals);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRole("product analyst")
                .withName("Updated Alice").build();

        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                String.format(StatusCommand.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE, PRODUCT_ANALYST.getTitle()));
    }

    @Test
    public void execute_destinationHasHire_acceptsNonHiredTransfers() {
        for (Status status : Status.values()) {
            if (status == Status.HIRED) {
                continue;
            }
            Person alice = new PersonBuilder().withName("Alice").withStatus(status).build();
            Person bob = new PersonBuilder(BENSON).withName("Bob").withRole("Product Analyst")
                    .withStatus(Status.HIRED).build();
            Model model = createModel(alice, bob);

            assertEditSuccess(model, alice, roleDescriptor("Product Analyst"),
                    alice.withRole(PRODUCT_ANALYST.getTitle()));
        }
    }

    @Test
    public void execute_hiresForOtherRolesAndPendingDestinationCandidates_doNotBlockTransfer() {
        Person alice = new PersonBuilder().withName("Alice").withStatus(Status.HIRED).build();
        Person bob = new PersonBuilder(BENSON).withName("Bob").withRole("Product Analyst").build();
        Person carol = new PersonBuilder(CARL).withName("Carol").withRole("Product Designer")
                .withStatus(Status.HIRED).build();
        Model model = createModel(alice, bob, carol);

        assertEditSuccess(model, alice, roleDescriptor("Product Analyst"), alice.withRole(PRODUCT_ANALYST.getTitle()));
    }

    @Test
    public void execute_sameOpenOrClosedRole_allowsNoOpAndOtherCorrections() {
        for (RoleStatus roleStatus : RoleStatus.values()) {
            Person original = new PersonBuilder().withStatus(Status.HIRED).build();
            Model model = createModel(original);
            model.setRole(SOFTWARE_ENGINEER, new RoleBuilder(SOFTWARE_ENGINEER).withStatus(roleStatus).build());
            assertEditSuccess(model, original, roleDescriptor("  software engineer  "), original);

            EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRole("SOFTWARE ENGINEER")
                    .withName("Updated Name").build();
            assertEditSuccess(model, original, descriptor,
                    new PersonBuilder(original).withName("Updated Name").build());
        }
    }

    @Test
    public void execute_unknownOrClosedDestination_rejectsEntireCombinedEdit() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder(BENSON).withName("Bob").build();
        Model model = createModel(alice, bob);
        model.updateFilteredPersonList(alice::equals);

        EditPersonDescriptor unknown = new EditPersonDescriptorBuilder().withRole("Unknown Role")
                .withPhone("91234567").build();
        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, unknown), model,
                "No role named “Unknown Role” exists. Add the role first, then edit the candidate.");
        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, roleDescriptor("product designer")), model,
                "The role “Product Designer” is closed, so candidates cannot be moved to it.");
    }

    @Test
    public void execute_duplicateNameDuringTransfer_rejectsEntireEdit() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder(BENSON).withName("Bob").build();
        Model model = createModel(alice, bob);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRole("Product Analyst")
                .withName("Bob").build();

        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_roleViewOrInvalidIndex_rejectsTransfer() {
        Model model = createModel(new PersonBuilder().build());
        model.setRoleListDisplayed(true);
        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, roleDescriptor("Product Analyst")), model,
                Messages.MESSAGE_PERSON_LIST_NOT_DISPLAYED);
        model.setRoleListDisplayed(false);
        assertCommandFailure(new EditCommand(Index.fromOneBased(2), roleDescriptor("Product Analyst")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_originalDescriptorChanged_usesDefensiveCopyOfRole() {
        Person original = new PersonBuilder().build();
        Model model = createModel(original);
        EditPersonDescriptor descriptor = roleDescriptor("Product Analyst");
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        descriptor.setRole(CLOSED_DESIGNER.getTitle());
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person updated = original.withRole(PRODUCT_ANALYST.getTitle());
        expected.setPerson(original, updated);

        assertCommandSuccess(command, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(updated)), expected);
    }

    private static EditPersonDescriptor roleDescriptor(String title) {
        return new EditPersonDescriptorBuilder().withRole(title).build();
    }

    private static Model createModel(Person... candidates) {
        AddressBook addressBook = TypicalRoles.getTypicalRoleAddressBook();
        for (Person candidate : candidates) {
            addressBook.addPerson(candidate);
        }
        return new ModelManager(addressBook, new UserPrefs());
    }

    private static void assertEditSuccess(Model model, Person original, EditPersonDescriptor descriptor,
            Person updated) {
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.setPerson(original, updated);
        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(updated)), expected);
    }
}
