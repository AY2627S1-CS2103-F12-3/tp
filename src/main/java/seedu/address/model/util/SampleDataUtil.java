package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Status;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    private static final RoleTitle SOFTWARE_ENGINEER = new RoleTitle("Software Engineer");
    private static final RoleTitle DATA_ANALYST = new RoleTitle("Data Analyst");
    private static final RoleTitle PRODUCT_MANAGER = new RoleTitle("Product Manager");
    private static final RoleTitle UX_DESIGNER = new RoleTitle("UX Designer");

    /**
     * Returns the sample roles, which include the role of every sample person.
     * One role is closed, so that first-time users see both role states.
     */
    public static Role[] getSampleRoles() {
        return new Role[] {
            new Role(SOFTWARE_ENGINEER, new ExperienceLevel("2 years")),
            new Role(DATA_ANALYST, new ExperienceLevel("Entry level")),
            new Role(PRODUCT_MANAGER, new ExperienceLevel("5 years")),
            new Role(UX_DESIGNER, new ExperienceLevel("3 years"), RoleStatus.CLOSED)
        };
    }

    /**
     * Returns the sample persons, who cover every status. Every candidate of the closed role has a final
     * status, and no role has more than one hired candidate, as the status and role commands require.
     */
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"), SOFTWARE_ENGINEER,
                new Note("Strong in Spring Boot and REST APIs, available to start immediately"),
                new Interview("2026-10-20 14:00"), Status.INTERVIEW, getTagSet("friends")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"), SOFTWARE_ENGINEER,
                new Note("Referred by Alex Yeoh. Strong Java and SQL skills, prefers hybrid work "
                        + "and can start in December after serving one month of notice."),
                Interview.NONE, Status.RESUME, getTagSet("colleagues", "friends")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"), DATA_ANALYST, new Note(""),
                new Interview("2026-10-23 10:00"), Status.OFFER, getTagSet("neighbours")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"), DATA_ANALYST, new Note(""),
                Interview.NONE, Status.REJECTED, getTagSet("family")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"), PRODUCT_MANAGER,
                new Note("Expects a salary of about 5k per month"),
                new Interview("2026-10-12 16:30"), Status.DECISION, getTagSet("classmates")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"), PRODUCT_MANAGER,
                new Note("Accepted an offer elsewhere"),
                Interview.NONE, Status.WITHDRAWN, getTagSet("colleagues")),
            new Person(new Name("Nur Aisyah"), new Phone("96543210"), new Email("nuraisyah@example.com"),
                new Address("Blk 12 Bedok North Avenue 4, #03-21"), UX_DESIGNER, new Note(""),
                Interview.NONE, Status.HIRED, getTagSet("figma")),
            new Person(new Name("Marcus Tan"), new Phone("81234567"), new Email("marcustan@example.com"),
                new Address("Blk 208 Jurong East Street 21, #09-12"), UX_DESIGNER, new Note(""),
                Interview.NONE, Status.REJECTED, getTagSet("graduate"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Role sampleRole : getSampleRoles()) {
            sampleAb.addRole(sampleRole);
        }
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
