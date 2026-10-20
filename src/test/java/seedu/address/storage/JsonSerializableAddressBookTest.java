package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;
import seedu.address.testutil.TypicalRoles;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path TYPICAL_ROLES_FILE = TEST_DATA_FOLDER.resolve("typicalRolesAddressBook.json");
    private static final Path INVALID_ROLE_FILE = TEST_DATA_FOLDER.resolve("invalidRoleAddressBook.json");
    private static final Path DUPLICATE_ROLE_FILE = TEST_DATA_FOLDER.resolve("duplicateRoleAddressBook.json");
    private static final Path UNKNOWN_ROLE_FILE = TEST_DATA_FOLDER.resolve("unknownRoleAddressBook.json");
    private static final Path PERSON_WITH_CLOSED_ROLE_FILE =
            TEST_DATA_FOLDER.resolve("personWithClosedRoleAddressBook.json");
    private static final Path PERSON_WITH_ROLE_IN_OTHER_CASE_FILE =
            TEST_DATA_FOLDER.resolve("personWithRoleInOtherCaseAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_unknownRole_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(UNKNOWN_ROLE_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_UNKNOWN_ROLE,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_personWithClosedRole_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PERSON_WITH_CLOSED_ROLE_FILE,
                JsonSerializableAddressBook.class).get();
        Person loadedPerson = dataFromFile.toModelType().getPersonList().get(0);
        assertEquals(TypicalRoles.CLOSED_DESIGNER.getTitle(), loadedPerson.getRole());
    }

    @Test
    public void toModelType_personWithRoleInOtherCase_usesRoleTitle() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(PERSON_WITH_ROLE_IN_OTHER_CASE_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();

        AddressBook expectedAddressBook = new AddressBook();
        expectedAddressBook.addRole(TypicalRoles.SOFTWARE_ENGINEER);
        expectedAddressBook.addPerson(new PersonBuilder().withName("Hans Muster").withPhone("9482424")
                .withEmail("hans@example.com").withAddress("4th street")
                .withRole(TypicalRoles.SOFTWARE_ENGINEER.getTitle().value).build());
        assertEquals(expectedAddressBook, addressBookFromFile);
    }

    @Test
    public void toModelType_typicalRolesFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_ROLES_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalRolesAddressBook = TypicalRoles.getTypicalRoleAddressBook();
        assertEquals(addressBookFromFile, typicalRolesAddressBook);
    }

    @Test
    public void toModelType_invalidRoleFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_ROLE_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateRoles_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_ROLE_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_ROLE,
                dataFromFile::toModelType);
    }

}
