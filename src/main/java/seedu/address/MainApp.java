package seedu.address;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.RecruitmentDataRecovery;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.StartupDataNotice;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.3";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "addressbook.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    private final Path userPrefsFilePath;
    private final Path addressBookFilePath;
    private final RecruitmentDataRecovery dataRecovery;
    private final Consumer<StartupDataNotice> startupNoticeConsumer;
    private StartupDataNotice startupNotice;

    /**
     * Creates the application using the default local file paths.
     */
    public MainApp() {
        this(USER_PREFS_FILE_PATH, ADDRESS_BOOK_FILE_PATH);
    }

    /**
     * Creates the application using the given preference and recruitment-data paths.
     */
    MainApp(Path userPrefsFilePath, Path addressBookFilePath) {
        this(userPrefsFilePath, addressBookFilePath, new RecruitmentDataRecovery());
    }

    /**
     * Creates the application with the given paths and recruitment-data recovery operations.
     */
    MainApp(Path userPrefsFilePath, Path addressBookFilePath, RecruitmentDataRecovery dataRecovery) {
        this(userPrefsFilePath, addressBookFilePath, dataRecovery, UiManager::showStartupDataNotice);
    }

    /**
     * Creates the application with explicit recovery and startup-notification boundaries.
     */
    MainApp(Path userPrefsFilePath, Path addressBookFilePath, RecruitmentDataRecovery dataRecovery,
            Consumer<StartupDataNotice> startupNoticeConsumer) {
        requireAllNonNull(userPrefsFilePath, addressBookFilePath, dataRecovery, startupNoticeConsumer);
        this.userPrefsFilePath = userPrefsFilePath;
        this.addressBookFilePath = addressBookFilePath;
        this.dataRecovery = dataRecovery;
        this.startupNoticeConsumer = startupNoticeConsumer;
    }

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing AddressBook ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(userPrefsFilePath);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFilePath);
        storage = new StorageManager(addressBookStorage, userPrefsStorage);

        try {
            model = initModelManager(storage, userPrefs);
        } catch (DataLoadingException e) {
            logger.warning("Recruitment data at " + addressBookFilePath + " could not be loaded: " + e);
            if (!recoverRecruitmentData(userPrefs)) {
                return;
            }
        }

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath());
    }

    /**
     * Starts with empty records only after preserving the original and saving a replacement.
     */
    private boolean recoverRecruitmentData(ReadOnlyUserPrefs userPrefs) {
        Path dataBackupPath = null;
        try {
            dataBackupPath = dataRecovery.backUp(addressBookFilePath);
            dataRecovery.createEmptyDataFile(addressBookFilePath);
            model = new ModelManager(new AddressBook(), userPrefs);
            startupNotice = StartupDataNotice.recovered(addressBookFilePath, dataBackupPath);
            return true;
        } catch (IOException | SecurityException e) {
            startupNotice = StartupDataNotice.failed(addressBookFilePath, dataBackupPath, e.toString());
            logger.severe("Safe recruitment-data recovery failed: " + e);
            return false;
        }
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The sample address book is used only if the stored address book is not found.
     *
     * @throws DataLoadingException if the stored data cannot be read or validated.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) throws DataLoadingException {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional = storage.readAddressBook();
        if (addressBookOptional.isEmpty()) {
            logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                    + " populated with a sample AddressBook.");
        }
        ReadOnlyAddressBook initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting AddressBook " + MainApp.VERSION);
        if (startupNotice != null) {
            startupNoticeConsumer.accept(startupNotice);
            if (startupNotice.isFatal()) {
                return;
            }
        }
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping AddressBook ] =============================");
        if (model == null) {
            return;
        }
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
