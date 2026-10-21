package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_everyPerson_hasSampleRole() {
        AddressBook sampleAddressBook = new AddressBook(SampleDataUtil.getSampleAddressBook());
        for (Person samplePerson : SampleDataUtil.getSamplePersons()) {
            assertTrue(sampleAddressBook.findRole(samplePerson.getRole()).isPresent());
        }
    }

    @Test
    public void getSampleAddressBook_containsEverySampleRole() {
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();
        assertEquals(Arrays.asList(SampleDataUtil.getSampleRoles()), sampleAddressBook.getRoleList());
    }

    @Test
    public void getSampleAddressBook_noDuplicatePersons_containsEverySamplePerson() {
        // Adding a duplicate person throws, so this also checks that the sample persons are distinct.
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();

        assertEquals(Arrays.asList(SampleDataUtil.getSamplePersons()), sampleAddressBook.getPersonList());
    }
}
