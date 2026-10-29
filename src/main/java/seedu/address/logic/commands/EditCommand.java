package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the candidate identified "
            + "by the index number used in the displayed candidate list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_ROLE_TITLE + "ROLE_TITLE] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited candidate: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_ROLE_NOT_FOUND =
            "No role named “%1$s” exists. Add the role first, then edit the candidate.";
    public static final String MESSAGE_ROLE_CLOSED =
            "The role “%1$s” is closed, so candidates cannot be moved to it.";

    private static final Logger logger = LogsCenter.getLogger(EditCommand.class);

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        RoleTitle updatedRole = resolveRole(model, personToEdit);
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor, updatedRole);

        // The candidate being edited is excluded, so keeping their own email or phone is not a duplicate.
        Optional<Person> existingPerson = model.findSamePersonExcluding(editedPerson, personToEdit);
        if (existingPerson.isPresent()) {
            String message = Messages.getErrorMessageForDuplicatePerson(editedPerson, existingPerson.get());
            logger.info("Rejected edit of candidate " + personToEdit.getName() + ": " + message);
            throw new CommandException(message);
        }

        boolean isRoleTransfer = isRoleChanged(personToEdit, updatedRole);
        if (isRoleTransfer) {
            CandidateRoleChecks.verifyHiringAllowed(model, personToEdit, updatedRole, editedPerson.getStatus());
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        if (isRoleTransfer) {
            logger.info("Updated candidate role in memory at displayed index " + index.getOneBased() + ".");
        }
        String successMessage = String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        // notes on an unchanged number were already shown when it was saved
        boolean isPhoneChanged = !editedPerson.getPhone().equals(personToEdit.getPhone());
        if (!isPhoneChanged) {
            return new CommandResult(successMessage);
        }
        return new CommandResult(PhoneChecks.appendPhoneWarnings(successMessage, model, editedPerson));
    }

    /**
     * Returns the destination role's canonical title, or the unchanged role when no role is specified.
     *
     * @throws CommandException if the requested role does not exist or a transfer targets a closed role.
     */
    private RoleTitle resolveRole(Model model, Person personToEdit) throws CommandException {
        if (editPersonDescriptor.getRole().isEmpty()) {
            return personToEdit.getRole();
        }
        RoleTitle requestedRole = editPersonDescriptor.getRole().get();
        Role destination = model.findRole(requestedRole)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_ROLE_NOT_FOUND, requestedRole)));
        assert destination.getTitle().isSameTitle(requestedRole) : "Role lookup must match the request";
        if (isRoleChanged(personToEdit, destination.getTitle()) && destination.isClosed()) {
            logger.fine("Rejected candidate role transfer: destination role is closed.");
            throw new CommandException(String.format(MESSAGE_ROLE_CLOSED, destination.getTitle()));
        }
        return destination.getTitle();
    }

    /**
     * Returns true if {@code newRole} identifies a different role from the candidate's current role.
     */
    private static boolean isRoleChanged(Person personToEdit, RoleTitle newRole) {
        return !personToEdit.getRole().isSameTitle(newRole);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor,
            RoleTitle updatedRole) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Note note = personToEdit.getNote();
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedRole, note,
                personToEdit.getInterview(), personToEdit.getStatus(), updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private RoleTitle role;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setRole(toCopy.role);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, role, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setRole(RoleTitle role) {
            this.role = role;
        }

        public Optional<RoleTitle> getRole() {
            return Optional.ofNullable(role);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(role, otherEditPersonDescriptor.role)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("role", role)
                    .add("tags", tags)
                    .toString();
        }
    }
}
