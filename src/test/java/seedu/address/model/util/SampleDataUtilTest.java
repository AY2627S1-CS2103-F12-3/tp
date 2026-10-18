package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
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
    public void getSamplePersons_everyPerson_hasSampleRole() {
        AddressBook sampleAddressBook = new AddressBook(SampleDataUtil.getSampleAddressBook());
        for (Person samplePerson : SampleDataUtil.getSamplePersons()) {
            assertTrue(sampleAddressBook.findRole(samplePerson.getRole()).isPresent());
        }
    }

    @Test
    public void getSamplePersons_withAndWithoutInterviews_showsBothCardStates() {
        List<Person> samplePersons = Arrays.asList(SampleDataUtil.getSamplePersons());
        assertTrue(samplePersons.stream().anyMatch(person -> !person.getInterview().isEmpty()));
        assertTrue(samplePersons.stream().anyMatch(person -> person.getInterview().isEmpty()));
    }

    @Test
    public void getSamplePersons_everyInterview_notRepeatedInNote() {
        for (Person samplePerson : SampleDataUtil.getSamplePersons()) {
            String interview = samplePerson.getInterview().toString();
            if (!interview.isEmpty()) {
                assertFalse(samplePerson.getNote().value.contains(interview));
            }
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
