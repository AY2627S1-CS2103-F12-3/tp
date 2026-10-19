package seedu.address.testutil;

import java.nio.file.Path;
import java.util.logging.Handler;
import java.util.logging.Logger;

import seedu.address.MainApp;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;

/**
 * Exercises application initialization and saving in a separate JVM without opening the GUI.
 * The entry point does not extend Application, to avoid invoking the JavaFX launcher.
 */
public class FileSavingTestDriver {

    public static void main(String[] args) throws Exception {
        try {
            new HeadlessApp().exercise(args[0], Path.of(args[1]));
        } finally {
            Logger logger = Logger.getLogger("ab3");
            for (Handler handler : logger.getHandlers()) {
                handler.close();
                logger.removeHandler(handler);
            }
        }
    }

    /**
     * Gives the driver access to the model and storage without changing production visibility.
     */
    private static class HeadlessApp extends MainApp {
        private void exercise(String mode, Path expectedDirectory) throws Exception {
            // Expand Windows short-path aliases before comparing with the code-source-based storage paths.
            Path realDirectory = expectedDirectory.toRealPath();
            init();
            if (mode.equals("development")) {
                if (storage.getAddressBookFilePath().startsWith(realDirectory)
                        || storage.getUserPrefsFilePath().startsWith(realDirectory)) {
                    throw new AssertionError("Persistent files must not be saved inside the build directory: "
                            + storage.getAddressBookFilePath());
                }
                stop();
                return;
            }

            if (!storage.getAddressBookFilePath().equals(realDirectory.resolve("data/addressbook.json"))
                    || !storage.getUserPrefsFilePath().equals(realDirectory.resolve("preferences.json"))) {
                throw new AssertionError("Storage paths must resolve beside the JAR");
            }

            AddressBook expected = new AddressBook();
            expected.addRole(new RoleBuilder().withTitle(PersonBuilder.DEFAULT_ROLE).build());
            expected.addPerson(new PersonBuilder().withName("Saved Candidate").build());
            GuiSettings settings = new GuiSettings(1000, 500, 300, 100);
            if (mode.equals("read")) {
                if (!expected.equals(new AddressBook(model.getAddressBook()))
                        || !settings.equals(model.getGuiSettings())) {
                    throw new AssertionError("Saved records and preferences must survive a restart");
                }
            } else if (mode.equals("write")) {
                model.setAddressBook(expected);
                model.setGuiSettings(settings);
                storage.saveAddressBook(model.getAddressBook());
            } else {
                throw new IllegalArgumentException("Unknown test mode: " + mode);
            }
            Logger.getLogger("ab3").info("File-saving integration test marker");
            stop();
        }
    }
}
