package seedu.address.storage;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Status;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    public static final String INVALID_STATUS_MESSAGE_FORMAT = "Candidate status '%s' is invalid.";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String role;
    private final String note;
    private final String interview;
    private final String status;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("role") String role, @JsonProperty("note") String note,
            @JsonProperty("interview") String interview, @JsonProperty("status") String status,
            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.role = role;
        this.note = note;
        this.interview = interview;
        this.status = status;
        if (tags != null) {
            requireAllNonNull(tags);
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        role = source.getRole().value;
        note = source.getNote().value;
        interview = source.getInterview().toString();
        status = source.getStatus().name();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final RoleTitle modelRole = toModelRole();

        // Notes are optional, so a missing note (e.g., in a file saved before notes existed) means no note.
        final Note modelNote = (note == null) ? new Note("") : new Note(note);

        final Interview modelInterview = toModelInterview();

        final Status modelStatus = toModelStatus();

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelRole, modelNote, modelInterview,
                modelStatus, modelTags);
    }

    /**
     * Converts the stored interview into the model's {@code Interview} object.
     * A missing interview (e.g., in a file saved before interviews existed) or an empty one means no interview.
     *
     * @throws IllegalValueException if the stored interview has the wrong format or is not a real date and time.
     */
    private Interview toModelInterview() throws IllegalValueException {
        if (interview == null || interview.isEmpty()) {
            return Interview.NONE;
        }
        if (!Interview.isValidFormat(interview)) {
            throw new IllegalValueException(Interview.MESSAGE_INVALID_FORMAT);
        }
        if (!Interview.isValidDateTime(interview)) {
            throw new IllegalValueException(Interview.MESSAGE_INVALID_DATE_TIME);
        }
        return new Interview(interview);
    }

    /**
     * Converts the stored role title into the model's {@code RoleTitle} object.
     * Unlike a status, a missing role has no sensible default, so it makes the candidate invalid.
     *
     * @throws IllegalValueException if the stored role title is missing or invalid.
     */
    private RoleTitle toModelRole() throws IllegalValueException {
        if (role == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    RoleTitle.class.getSimpleName()));
        }
        if (!RoleTitle.isValidRoleTitle(role)) {
            throw new IllegalValueException(RoleTitle.MESSAGE_CONSTRAINTS);
        }
        return new RoleTitle(role);
    }

    /**
     * Converts the stored status into the model's {@code Status} object.
     * A missing status (e.g., in a file saved before statuses existed) means the default status.
     *
     * @throws IllegalValueException if the stored status is not the exact name of a {@code Status}.
     */
    private Status toModelStatus() throws IllegalValueException {
        if (status == null) {
            return Person.DEFAULT_STATUS;
        }

        try {
            return Status.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(String.format(INVALID_STATUS_MESSAGE_FORMAT, status), exception);
        }
    }

}
