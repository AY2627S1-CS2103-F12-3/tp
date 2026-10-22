package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.RecruitmentDataRecovery;
import seedu.address.testutil.LogCapture;
import seedu.address.ui.StartupDataNotice;

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
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MainApp(null, dataPath));
        assertThrows(NullPointerException.class, () -> new MainApp(prefsPath, null));
        assertThrows(NullPointerException.class, () -> new MainApp(prefsPath, dataPath, null));
        assertThrows(NullPointerException.class, () -> new MainApp(
                prefsPath, dataPath, new RecruitmentDataRecovery(), null));
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
    public void init_malformedData_backsUpAndStartsEmpty() throws Exception {
        Files.writeString(dataPath, "{");
        try (LogCapture logs = new LogCapture()) {
            assertRecoveredStartupPreservesFile();

            assertLoadWarning(logs);
            logs.assertContains(RecruitmentDataRecovery.class, Level.INFO,
                    "Preserved unreadable recruitment data at " + dataPath.resolveSibling("addressbook.json.bak"));
            logs.assertContains(RecruitmentDataRecovery.class, Level.INFO,
                    "Created empty recruitment data at " + dataPath);
            assertEquals(2, logs.getRecords(RecruitmentDataRecovery.class).size());
            assertTrue(logs.getRecords(MainApp.class).stream().noneMatch(record -> record.getLevel() == Level.SEVERE));
        }
    }

    @Test
    public void init_trailingContent_backsUpEntireFileWithoutImportingEitherObject() throws Exception {
        new JsonAddressBookStorage(dataPath).saveAddressBook(getTypicalAddressBook());
        String validJson = Files.readString(dataPath);

        for (String suffix : new String[] {validJson, " \r\n" + validJson, " []", " null", " extra", "{"}) {
            Files.writeString(dataPath, validJson + suffix);
            assertRecoveredStartupPreservesFile();
        }
    }

    @Test
    public void init_trailingContent_allowsCommandsWithoutChangingBackup() throws Exception {
        new JsonAddressBookStorage(dataPath).saveAddressBook(getTypicalAddressBook());
        String validJson = Files.readString(dataPath);
        Files.writeString(dataPath, validJson + validJson);
        byte[] original = Files.readAllBytes(dataPath);
        MainApp recoveredApp = new MainApp(prefsPath, dataPath);
        recoveredApp.init();

        assertEquals("Added open role: Recovery Test.",
                recoveredApp.logic.execute("role add r/Recovery Test x/Entry level").getFeedbackToUser());
        MainApp restartedApp = new MainApp(prefsPath, dataPath);
        restartedApp.init();

        assertEquals(recoveredApp.model.getAddressBook(), restartedApp.model.getAddressBook());
        assertEquals(1, restartedApp.model.getAddressBook().getRoleList().size());
        assertTrue(restartedApp.model.getAddressBook().getPersonList().isEmpty());
        assertArrayEquals(original, Files.readAllBytes(dataPath.resolveSibling("addressbook.json.bak")));
    }

    @Test
    public void init_nullDataOrRecords_backsUpAndStartsEmpty() throws Exception {
        for (String json : new String[] {"null", "{\"persons\":[null]}", "{\"roles\":[null]}",
            "{\"persons\":[{\"tags\":[null]}]}"}) {
            Files.writeString(dataPath, json);
            assertRecoveredStartupPreservesFile();
        }
    }

    @Test
    public void init_invalidRecords_backsUpAndStartsEmpty() throws Exception {
        for (String fileName : new String[] {"invalidPersonAddressBook.json", "invalidRoleAddressBook.json",
            "duplicatePersonAddressBook.json", "duplicateRoleAddressBook.json", "unknownRoleAddressBook.json"}) {
            Files.write(dataPath, Files.readAllBytes(INVALID_DATA_FOLDER.resolve(fileName)));
            assertRecoveredStartupPreservesFile();
        }
    }

    @Test
    public void init_validAndInvalidRecords_doesNotExposePartialData() throws Exception {
        Path fixture = Path.of("src", "test", "data", "JsonAddressBookStorageTest",
                "invalidAndValidPersonAddressBook.json");
        Files.copy(fixture, dataPath);

        assertRecoveredStartupPreservesFile();
    }

    @Test
    public void init_backupFails_blocksStartupAndPreservesOriginal() throws Exception {
        Files.writeString(dataPath, "{");
        RecruitmentDataRecovery failingRecovery = new RecruitmentDataRecovery() {
            @Override
            public Path backUp(Path path) throws IOException {
                throw new IOException("Backup denied");
            }
        };
        StartupNoticeApp app = new StartupNoticeApp(prefsPath, dataPath, failingRecovery);

        try (LogCapture logs = new LogCapture()) {
            assertBlockedStartup(app);

            assertRecoveryFailure(logs, "java.io.IOException: Backup denied");
            logs.assertNoRecords(RecruitmentDataRecovery.class);
        }

        assertEquals("{", Files.readString(dataPath));
        assertFalse(Files.exists(dataPath.resolveSibling("addressbook.json.bak")));
    }

    @Test
    public void init_replacementFails_blocksStartupAndPreservesBackup() throws Exception {
        Files.writeString(dataPath, "{");
        RecruitmentDataRecovery failingRecovery = new RecruitmentDataRecovery() {
            @Override
            public void createEmptyDataFile(Path path) throws IOException {
                throw new IOException("Replacement denied");
            }
        };
        StartupNoticeApp app = new StartupNoticeApp(prefsPath, dataPath, failingRecovery);

        try (LogCapture logs = new LogCapture()) {
            assertBlockedStartup(app);

            assertRecoveryFailure(logs, "java.io.IOException: Replacement denied");
            logs.assertContains(RecruitmentDataRecovery.class, Level.INFO,
                    "Preserved unreadable recruitment data at " + dataPath.resolveSibling("addressbook.json.bak"));
            assertEquals(1, logs.getRecords(RecruitmentDataRecovery.class).size());
        }

        assertEquals("{", Files.readString(dataPath.resolveSibling("addressbook.json.bak")));
        assertFalse(Files.exists(dataPath));
    }

    @Test
    public void init_fileAppearsAfterBackup_blocksStartupWithoutOverwritingEitherFile() throws Exception {
        Files.writeString(dataPath, "{");
        RecruitmentDataRecovery racingRecovery = new RecruitmentDataRecovery() {
            @Override
            public void createEmptyDataFile(Path path) throws IOException {
                Files.writeString(path, "Unexpected replacement");
                super.createEmptyDataFile(path);
            }
        };
        StartupNoticeApp app = new StartupNoticeApp(prefsPath, dataPath, racingRecovery);

        assertBlockedStartup(app);

        assertEquals("{", Files.readString(dataPath.resolveSibling("addressbook.json.bak")));
        assertEquals("Unexpected replacement", Files.readString(dataPath));
        assertTrue(app.notices.latestNotice.message().contains(
                dataPath.resolveSibling("addressbook.json.bak").toAbsolutePath().normalize().toString()));
    }

    @Test
    public void init_securityFailure_blocksStartupAndPreservesOriginal() throws Exception {
        Files.writeString(dataPath, "{");
        RecruitmentDataRecovery failingRecovery = new RecruitmentDataRecovery() {
            @Override
            public Path backUp(Path path) {
                throw new SecurityException("Access denied");
            }
        };

        try (LogCapture logs = new LogCapture()) {
            assertBlockedStartup(new StartupNoticeApp(prefsPath, dataPath, failingRecovery));

            assertRecoveryFailure(logs, "java.lang.SecurityException: Access denied");
            logs.assertNoRecords(RecruitmentDataRecovery.class);
        }

        assertEquals("{", Files.readString(dataPath));
    }

    @Test
    public void init_recoveredData_persistsEmptyListsAcrossRestartWithoutCommands() throws Exception {
        Files.writeString(dataPath, "{");
        MainApp recoveredApp = new MainApp(prefsPath, dataPath);
        recoveredApp.init();
        recoveredApp.stop();
        StartupNoticeApp restartedApp = new StartupNoticeApp(prefsPath, dataPath);
        restartedApp.init();
        restartedApp.ui = stage -> { };

        restartedApp.start(null);

        assertEquals(new AddressBook(), restartedApp.model.getAddressBook());
        assertFalse(restartedApp.notices.wasRecoveryWarningShown);
        assertFalse(restartedApp.notices.wasStartupErrorShown);
        assertEquals("{", Files.readString(dataPath.resolveSibling("addressbook.json.bak")));
    }

    @Test
    public void init_recoveredData_allowsCommandsAndReloadWithoutChangingBackup() throws Exception {
        Files.writeString(dataPath, "{");
        MainApp recoveredApp = new MainApp(prefsPath, dataPath);
        recoveredApp.init();

        assertEquals("Added open role: QA Engineer.",
                recoveredApp.logic.execute("role add r/QA Engineer x/Entry level").getFeedbackToUser());
        MainApp restartedApp = new MainApp(prefsPath, dataPath);
        restartedApp.init();

        assertEquals(recoveredApp.model.getAddressBook(), restartedApp.model.getAddressBook());
        assertEquals(1, restartedApp.model.getAddressBook().getRoleList().size());
        assertEquals("{", Files.readString(dataPath.resolveSibling("addressbook.json.bak")));
    }

    @Test
    public void init_unreadableDataPath_blocksStartupWithoutChangingDirectory() throws Exception {
        Files.createDirectory(dataPath);
        Path originalFile = dataPath.resolve("keep.txt");
        Files.writeString(originalFile, "Preserve this file.");
        StartupNoticeApp app = new StartupNoticeApp(prefsPath, dataPath);

        app.init();
        app.start(null);
        app.stop();

        assertTrue(app.notices.wasStartupErrorShown);
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
        StartupNoticeApp app = new StartupNoticeApp(prefsPath, dataPath);
        app.init();
        boolean[] wasUiStarted = {false};
        app.ui = stage -> wasUiStarted[0] = true;

        app.start(null);

        assertTrue(wasUiStarted[0]);
        assertFalse(app.notices.wasStartupErrorShown);
        assertFalse(app.notices.wasRecoveryWarningShown);
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
    public void init_preferencesWithTrailingContent_usesExistingDefaultFallback() throws Exception {
        UserPrefs preferences = new UserPrefs();
        preferences.setGuiSettings(new GuiSettings(500, 600, 10, 20));
        new JsonUserPrefsStorage(prefsPath).saveUserPrefs(preferences);
        Files.writeString(prefsPath, Files.readString(prefsPath) + "{}");
        MainApp app = new MainApp(prefsPath, dataPath);

        app.init();

        assertEquals(new UserPrefs(), app.model.getUserPrefs());
        assertEquals(SampleDataUtil.getSampleAddressBook(), app.model.getAddressBook());
        assertNotNull(app.logic);
        assertNotNull(app.ui);
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

    private void assertLoadWarning(LogCapture logs) {
        String prefix = "Recruitment data at " + dataPath + " could not be loaded: ";
        assertTrue(logs.getRecords(MainApp.class).stream().anyMatch(record ->
                record.getLevel() == Level.WARNING && record.getMessage().startsWith(prefix)));
    }

    private void assertRecoveryFailure(LogCapture logs, String reason) {
        assertLoadWarning(logs);
        logs.assertContains(MainApp.class, Level.SEVERE, "Safe recruitment-data recovery failed: " + reason);
    }

    /**
     * Checks that recovery preserves the original bytes, exposes empty lists and warns before commands open.
     */
    private void assertRecoveredStartupPreservesFile() throws Exception {
        byte[] original = Files.readAllBytes(dataPath);
        List<Path> existingBackups = getBackupPaths();
        StartupNoticeApp app = new StartupNoticeApp(prefsPath, dataPath);
        app.init();
        boolean[] wasUiStarted = {false};
        app.ui = stage -> {
            assertTrue(app.notices.wasRecoveryWarningShown);
            wasUiStarted[0] = true;
        };

        app.start(null);
        app.stop();

        assertTrue(wasUiStarted[0]);
        assertFalse(app.notices.wasStartupErrorShown);
        assertEquals(new AddressBook(), app.model.getAddressBook());
        assertNotNull(app.logic);
        List<Path> newBackups = getBackupPaths().stream().filter(path -> !existingBackups.contains(path)).toList();
        assertEquals(1, newBackups.size());
        assertArrayEquals(original, Files.readAllBytes(newBackups.get(0)));
        assertEquals(new AddressBook(), new JsonAddressBookStorage(dataPath).readAddressBook().orElseThrow());
        assertTrue(app.notices.latestNotice.message().contains(newBackups.get(0).toAbsolutePath().toString()));
    }

    private List<Path> getBackupPaths() throws IOException {
        try (var files = Files.list(testFolder)) {
            return files.filter(path -> path.getFileName().toString().startsWith("addressbook.json.bak")).toList();
        }
    }

    /**
     * Checks that unsuccessful recovery cannot expose commands or fail again during shutdown.
     */
    private void assertBlockedStartup(StartupNoticeApp app) throws Exception {
        app.init();
        byte[] preferencesAfterInit = Files.readAllBytes(prefsPath);

        app.start(null);
        app.stop();

        assertTrue(app.notices.wasStartupErrorShown);
        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        assertFalse(app.notices.wasRecoveryWarningShown);
        assertArrayEquals(preferencesAfterInit, Files.readAllBytes(prefsPath));
    }

    /**
     * Records real startup notifications without requiring a JavaFX display for lifecycle tests.
     */
    private static class StartupNoticeApp extends MainApp {
        private final NoticeRecorder notices;

        StartupNoticeApp(Path prefsPath, Path dataPath) {
            this(prefsPath, dataPath, new RecruitmentDataRecovery());
        }

        StartupNoticeApp(Path prefsPath, Path dataPath, RecruitmentDataRecovery recovery) {
            this(prefsPath, dataPath, recovery, new NoticeRecorder());
        }

        private StartupNoticeApp(Path prefsPath, Path dataPath, RecruitmentDataRecovery recovery,
                NoticeRecorder notices) {
            super(prefsPath, dataPath, recovery, notices);
            this.notices = notices;
        }
    }

    private static class NoticeRecorder implements Consumer<StartupDataNotice> {
        private boolean wasStartupErrorShown;
        private boolean wasRecoveryWarningShown;
        private StartupDataNotice latestNotice;

        @Override
        public void accept(StartupDataNotice notice) {
            latestNotice = notice;
            wasStartupErrorShown = notice.isFatal();
            wasRecoveryWarningShown = !notice.isFatal();
        }
    }
}
