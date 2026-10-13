package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_everyPerson_hasDefaultStatus() {
        for (Person samplePerson : SampleDataUtil.getSamplePersons()) {
            assertEquals(Person.DEFAULT_STATUS, samplePerson.getStatus());
        }
    }

    @Test
    public void getSampleAddressBook_noDuplicatePersons_containsEverySamplePerson() {
        // Adding a duplicate person throws, so this also checks that the sample persons are distinct.
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();

        assertEquals(Arrays.asList(SampleDataUtil.getSamplePersons()), sampleAddressBook.getPersonList());
    }
}
