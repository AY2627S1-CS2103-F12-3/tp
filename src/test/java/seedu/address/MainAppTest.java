package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;

public class MainAppTest {

    private static final Path INVALID_DATA_FOLDER = Path.of("src", "test", "data",
            "JsonSerializableAddressBookTest");

    @TempDir
    public Path testFolder;

    private Path prefsPath;
    private Path dataPath;

    @BeforeEach
    public void setUp() {
        prefsPath = testFolder.resolve("preferences.json");
        dataPath = testFolder.resolve("addressbook.json");
    }

    @Test
    public void constructor_defaultPaths_defersInitializationUntilJavaFxInit() {
        MainApp app = new MainApp();

        assertNull(app.storage);
        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
    }

    @Test
    public void init_validData_loadsRecordsWithoutChangingFile() throws Exception {
        AddressBook expected = getTypicalAddressBook();
        new JsonAddressBookStorage(dataPath).saveAddressBook(expected);
        byte[] original = Files.readAllBytes(dataPath);
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertEquals(expected, app.model.getAddressBook());
        assertNotNull(app.logic);
        assertNotNull(app.ui);
        assertArrayEquals(original, Files.readAllBytes(dataPath));
    }

    @Test
    public void init_missingData_usesSampleDataWithoutCreatingFile() throws Exception {
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertEquals(SampleDataUtil.getSampleAddressBook(), app.model.getAddressBook());
        assertNotNull(app.logic);
        assertNotNull(app.ui);
        assertFalse(Files.exists(dataPath));
    }

    @Test
    public void init_emptyData_preservesEmptyLists() throws Exception {
        new JsonAddressBookStorage(dataPath).saveAddressBook(new AddressBook());
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertTrue(app.model.getAddressBook().getPersonList().isEmpty());
        assertTrue(app.model.getAddressBook().getRoleList().isEmpty());
    }

    @Test
    public void init_malformedData_blocksStartupAndPreservesFile() throws Exception {
        Files.writeString(dataPath, "{");
        assertFailedStartupPreservesFile();
    }

    @Test
    public void init_nullDataOrRecords_blocksStartupAndPreservesFile() throws Exception {
        for (String json : new String[] {"null", "{\"persons\":[null]}", "{\"roles\":[null]}",
            "{\"persons\":[{\"tags\":[null]}]}"}) {
            Files.writeString(dataPath, json);
            assertFailedStartupPreservesFile();
        }
    }

    @Test
    public void init_invalidRecords_blocksStartupAndPreservesFile() throws Exception {
        for (String fileName : new String[] {"invalidPersonAddressBook.json", "invalidRoleAddressBook.json",
            "duplicatePersonAddressBook.json", "duplicateRoleAddressBook.json", "unknownRoleAddressBook.json"}) {
            Files.write(dataPath, Files.readAllBytes(INVALID_DATA_FOLDER.resolve(fileName)));
            assertFailedStartupPreservesFile();
        }
    }

    @Test
    public void init_validAndInvalidRecords_doesNotExposePartialData() throws Exception {
        Path fixture = Path.of("src", "test", "data", "JsonAddressBookStorageTest",
                "invalidAndValidPersonAddressBook.json");
        Files.copy(fixture, dataPath);

        assertFailedStartupPreservesFile();
    }

    @Test
    public void init_unreadableDataPath_blocksStartupWithoutChangingDirectory() throws Exception {
        Files.createDirectory(dataPath);
        Path originalFile = dataPath.resolve("keep.txt");
        Files.writeString(originalFile, "Preserve this file.");
        StartupErrorApp app = new StartupErrorApp(prefsPath, dataPath);

        app.init();
        app.start(null);
        app.stop();

        assertTrue(app.wasStartupErrorShown);
        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        assertEquals("Preserve this file.", Files.readString(originalFile));
        try (var files = Files.list(dataPath)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void start_successfulInitialization_startsCommandUi() throws Exception {
        StartupErrorApp app = new StartupErrorApp(prefsPath, dataPath);
        app.init();
        boolean[] wasUiStarted = {false};
        app.ui = stage -> wasUiStarted[0] = true;

        app.start(null);

        assertTrue(wasUiStarted[0]);
        assertFalse(app.wasStartupErrorShown);
    }

    @Test
    public void stop_successfulInitialization_savesPreferencesButNotRecruitmentData() throws Exception {
        MainApp app = new MainApp(prefsPath, dataPath);
        app.init();
        GuiSettings expected = new GuiSettings(500, 600, 10, 20);
        app.model.setGuiSettings(expected);

        app.stop();

        assertEquals(expected, new JsonUserPrefsStorage(prefsPath).readUserPrefs().orElseThrow().getGuiSettings());
        assertFalse(Files.exists(dataPath));
    }

    @Test
    public void init_existingPreferences_preservesSettings() throws Exception {
        UserPrefs expected = new UserPrefs();
        expected.setGuiSettings(new GuiSettings(500, 600, 10, 20));
        new JsonUserPrefsStorage(prefsPath).saveUserPrefs(expected);
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertEquals(expected, app.model.getUserPrefs());
    }

    @Test
    public void init_invalidPreferences_keepsExistingDefaultFallback() throws Exception {
        Files.writeString(prefsPath, "{");
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertEquals(new UserPrefs(), app.model.getUserPrefs());
    }

    @Test
    public void init_nullPreferences_usesDefaultPreferencesWithoutBlockingStartup() throws Exception {
        Files.writeString(prefsPath, "null");
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertEquals(new UserPrefs(), app.model.getUserPrefs());
        assertNotNull(app.logic);
        assertNotNull(app.ui);
    }

    @Test
    public void init_preferencesCannotBeSaved_stillLoadsRecruitmentData() throws Exception {
        Files.createDirectory(prefsPath);
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertEquals(SampleDataUtil.getSampleAddressBook(), app.model.getAddressBook());
        assertTrue(Files.isDirectory(prefsPath));
    }

    @Test
    public void stop_preferencesCannotBeSaved_doesNotChangeRecruitmentData() throws Exception {
        MainApp app = new MainApp(prefsPath, dataPath);
        app.init();
        Files.delete(prefsPath);
        Files.createDirectory(prefsPath);

        app.stop();

        assertTrue(Files.isDirectory(prefsPath));
        assertFalse(Files.exists(dataPath));
    }

    /**
     * Checks that failed initialization cannot expose commands or overwrite the original file on shutdown.
     */
    private void assertFailedStartupPreservesFile() throws Exception {
        byte[] original = Files.readAllBytes(dataPath);
        StartupErrorApp app = new StartupErrorApp(prefsPath, dataPath);
        app.init();
        byte[] preferencesAfterInit = Files.readAllBytes(prefsPath);

        app.start(null);
        app.stop();

        assertTrue(app.wasStartupErrorShown);
        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        assertArrayEquals(original, Files.readAllBytes(dataPath));
        assertArrayEquals(preferencesAfterInit, Files.readAllBytes(prefsPath));
        try (var files = Files.list(testFolder)) {
            assertEquals(2, files.count());
        }
    }

    /**
     * Records the startup-error handoff without requiring a JavaFX display for lifecycle tests.
     */
    private static class StartupErrorApp extends MainApp {
        private boolean wasStartupErrorShown;

        StartupErrorApp(Path prefsPath, Path dataPath) {
            super(prefsPath, dataPath);
        }

        @Override
        protected void showDataLoadingErrorAndShutdown() {
            wasStartupErrorShown = true;
        }
    }
}
