package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;

public class RecruitmentDataRecoveryTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-18T08:00:00.123Z"), ZoneOffset.UTC);
    private static final String TIMESTAMP = "20261018-080000-123";

    @TempDir
    public Path testFolder;

    private final RecruitmentDataRecovery recovery = new RecruitmentDataRecovery(FIXED_CLOCK);

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RecruitmentDataRecovery(null));
    }

    @Test
    public void backUp_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> recovery.backUp(null));
    }

    @Test
    public void backUp_regularFile_movesOriginalBytesToSiblingBackup() throws Exception {
        Path source = testFolder.resolve("addressbook.json");
        byte[] original = {0, 13, 10, -1, 65};
        Files.write(source, original);

        Path backup = recovery.backUp(source);

        assertEquals(testFolder.resolve("addressbook.json.bak"), backup);
        assertArrayEquals(original, Files.readAllBytes(backup));
        assertFalse(Files.exists(source));
    }

    @Test
    public void backUp_existingBackups_usesTimestampAndCounterWithoutReplacingFiles() throws Exception {
        Path source = testFolder.resolve("addressbook.json");
        Path firstBackup = testFolder.resolve("addressbook.json.bak");
        Path timestampBackup = testFolder.resolve("addressbook.json.bak." + TIMESTAMP);
        Files.writeString(firstBackup, "First backup");
        Files.writeString(timestampBackup, "Timestamp backup");
        Files.writeString(source, "Second original");

        Path secondBackup = recovery.backUp(source);
        Files.writeString(source, "Third original");
        Path thirdBackup = recovery.backUp(source);

        assertEquals(testFolder.resolve("addressbook.json.bak." + TIMESTAMP + "-1"), secondBackup);
        assertEquals(testFolder.resolve("addressbook.json.bak." + TIMESTAMP + "-2"), thirdBackup);
        assertEquals("First backup", Files.readString(firstBackup));
        assertEquals("Timestamp backup", Files.readString(timestampBackup));
        assertEquals("Second original", Files.readString(secondBackup));
        assertEquals("Third original", Files.readString(thirdBackup));
        assertFalse(Files.exists(source));
    }

    @Test
    public void backUp_directoryAtBackupName_preservesDirectoryAndUsesTimestamp() throws Exception {
        Path source = testFolder.resolve("addressbook.json");
        Path existingDirectory = Files.createDirectory(testFolder.resolve("addressbook.json.bak"));
        Path marker = Files.writeString(existingDirectory.resolve("keep.txt"), "Keep this file");
        Files.writeString(source, "{");

        Path backup = recovery.backUp(source);

        assertEquals(testFolder.resolve("addressbook.json.bak." + TIMESTAMP), backup);
        assertEquals("{", Files.readString(backup));
        assertEquals("Keep this file", Files.readString(marker));
    }

    @Test
    public void backUp_directorySource_throwsWithoutMovingContents() throws Exception {
        Path source = Files.createDirectory(testFolder.resolve("addressbook.json"));
        Path marker = Files.writeString(source.resolve("keep.txt"), "Keep this file");

        assertThrows(IOException.class, () -> recovery.backUp(source));

        assertEquals("Keep this file", Files.readString(marker));
        assertFalse(Files.exists(testFolder.resolve("addressbook.json.bak")));
    }

    @Test
    public void backUp_missingSource_throwsWithoutCreatingBackup() throws Exception {
        Path source = testFolder.resolve("addressbook.json");

        assertThrows(IOException.class, () -> recovery.backUp(source));

        try (var files = Files.list(testFolder)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    public void createEmptyDataFile_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> recovery.createEmptyDataFile(null));
    }

    @Test
    public void createEmptyDataFile_absentSource_savesValidEmptyRecruitmentData() throws Exception {
        Path source = testFolder.resolve("addressbook.json");

        recovery.createEmptyDataFile(source);

        assertEquals(new AddressBook(), new JsonAddressBookStorage(source).readAddressBook().orElseThrow());
        assertTrue(Files.readString(source).contains("\"persons\""));
        assertTrue(Files.readString(source).contains("\"roles\""));
    }

    @Test
    public void createEmptyDataFile_existingSource_doesNotOverwriteUnexpectedFile() throws Exception {
        Path source = Files.writeString(testFolder.resolve("addressbook.json"), "New data from another writer");

        assertThrows(FileAlreadyExistsException.class, () -> recovery.createEmptyDataFile(source));

        assertEquals("New data from another writer", Files.readString(source));
    }

    @Test
    public void createEmptyDataFile_missingParent_throwsWithoutCreatingDirectories() {
        Path parent = testFolder.resolve("missing");

        assertThrows(IOException.class, () -> recovery.createEmptyDataFile(parent.resolve("addressbook.json")));

        assertFalse(Files.exists(parent));
    }
}
