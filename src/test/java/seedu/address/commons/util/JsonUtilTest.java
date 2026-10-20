package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    @Test
    public void fromJsonString_singleObjectWithWhitespace_returnsObject() throws IOException {
        SerializableTestClass actual = JsonUtil.fromJsonString(
                " \t\r\n" + SerializableTestClass.JSON_STRING_REPRESENTATION + " \t\r\n",
                SerializableTestClass.class);

        assertEquals(SerializableTestClass.getNameTestValue(), actual.getName());
        assertEquals(SerializableTestClass.getListTestValues(), actual.getListOfLocalDateTimes());
        assertEquals(SerializableTestClass.getHashMapTestValues(), actual.getMapOfIntegerToString());
    }

    @Test
    public void fromJsonString_additionalRootValue_throwsIoException() {
        for (String suffix : new String[] {"{}", " \r\n{}", " []", " null", " 42", " \"extra\"", " true"}) {
            String json = SerializableTestClass.JSON_STRING_REPRESENTATION + suffix;
            assertThrows(IOException.class, () -> JsonUtil.fromJsonString(json, SerializableTestClass.class));
        }
    }

    @Test
    public void fromJsonString_trailingGarbage_throwsIoException() {
        for (String suffix : new String[] {"{", " extra", "]", " /* comment */"}) {
            String json = SerializableTestClass.JSON_STRING_REPRESENTATION + suffix;
            assertThrows(IOException.class, () -> JsonUtil.fromJsonString(json, SerializableTestClass.class));
        }
    }

    @Test
    public void fromJsonString_singleScalarOrNull_returnsValue() throws IOException {
        assertEquals(42, JsonUtil.fromJsonString("42 \n", Integer.class));
        assertNull(JsonUtil.fromJsonString("null \n", SerializableTestClass.class));
    }

    @Test
    public void fromJsonString_scalarOrNullWithAdditionalValue_throwsIoException() {
        assertThrows(IOException.class, () -> JsonUtil.fromJsonString("42 43", Integer.class));
        assertThrows(IOException.class, () -> JsonUtil.fromJsonString("null {}", SerializableTestClass.class));
    }

    //TODO: @Test jsonUtil_writeThenReadObjectToJson_correctObject()
}
