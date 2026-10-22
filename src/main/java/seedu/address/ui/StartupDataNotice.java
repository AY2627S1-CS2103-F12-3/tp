package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;

/**
 * Describes a startup recovery outcome independently of the JavaFX dialog that displays it.
 */
public record StartupDataNotice(boolean isFatal, String title, String header, String message) {

    public StartupDataNotice {
        requireAllNonNull(title, header, message);
    }

    /**
     * Returns a warning explaining successful recovery and manual restoration.
     */
    public static StartupDataNotice recovered(Path dataFilePath, Path backupPath) {
        requireAllNonNull(dataFilePath, backupPath);
        return new StartupDataNotice(false, "Recruitment data backed up",
                "RecruiterBuddy is starting with empty candidate and role lists.",
                "The unreadable file was preserved without changing its contents."
                        + "\n\nBackup: " + backupPath.toAbsolutePath().normalize()
                        + "\n\nNew data file: " + dataFilePath.toAbsolutePath().normalize()
                        + "\n\nTo restore: close RecruiterBuddy, save a copy of any new recruitment data, "
                        + "correct the backup's JSON or file permissions, then copy the corrected backup "
                        + "to the data-file path above. Restart the app. "
                        + "The backup is not imported or merged automatically.");
    }

    /**
     * Returns an error identifying the preserved original, whether or not backup completed.
     */
    public static StartupDataNotice failed(Path dataFilePath, Path backupPath, String reason) {
        requireAllNonNull(dataFilePath, reason);
        String originalLocation = backupPath == null
                ? "The original data remains at the data-file path."
                : "The original data was preserved at: " + backupPath.toAbsolutePath().normalize();
        return new StartupDataNotice(true, "Unable to load recruitment data",
                "Recruitment data could not be recovered safely.",
                "Data file: " + dataFilePath.toAbsolutePath().normalize()
                        + "\n\n" + originalLocation + "\n\nReason: " + reason
                        + "\n\nRecruiterBuddy will close without opening commands. "
                        + "Resolve the file-access problem or restore corrected data before restarting. "
                        + "Keep any backup until restoration is complete.");
    }
}
