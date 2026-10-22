package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class StartupDataNoticeTest {
    private static final Path DATA_PATH = Path.of("data", "addressbook.json");
    private static final Path BACKUP_PATH = Path.of("data", "addressbook.json.bak");

    @Test
    public void constructor_nullText_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StartupDataNotice(false, null, "Header", "Message"));
        assertThrows(NullPointerException.class, () -> new StartupDataNotice(false, "Title", null, "Message"));
        assertThrows(NullPointerException.class, () -> new StartupDataNotice(false, "Title", "Header", null));
    }

    @Test
    public void recovered_success_namesBothPathsAndRestorationSteps() {
        StartupDataNotice notice = StartupDataNotice.recovered(DATA_PATH, BACKUP_PATH);

        assertFalse(notice.isFatal());
        assertEquals("Recruitment data backed up", notice.title());
        assertEquals("RecruiterBuddy is starting with empty candidate and role lists.", notice.header());
        assertEquals("The unreadable file was preserved without changing its contents."
                + "\n\nBackup: " + BACKUP_PATH.toAbsolutePath().normalize()
                + "\n\nNew data file: " + DATA_PATH.toAbsolutePath().normalize()
                + "\n\nTo restore: close RecruiterBuddy, save a copy of any new recruitment data, "
                + "correct the backup's JSON or file permissions, then copy the corrected backup "
                + "to the data-file path above. Restart the app. "
                + "The backup is not imported or merged automatically.", notice.message());
    }

    @Test
    public void recovered_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StartupDataNotice.recovered(null, BACKUP_PATH));
        assertThrows(NullPointerException.class, () -> StartupDataNotice.recovered(DATA_PATH, null));
    }

    @Test
    public void failed_backupNotCreated_identifiesOriginalAndReason() {
        StartupDataNotice notice = StartupDataNotice.failed(DATA_PATH, null, "Backup denied");

        assertTrue(notice.isFatal());
        assertEquals("Unable to load recruitment data", notice.title());
        assertEquals("Recruitment data could not be recovered safely.", notice.header());
        assertTrue(notice.message().contains("Data file: " + DATA_PATH.toAbsolutePath().normalize()));
        assertTrue(notice.message().contains("The original data remains at the data-file path."));
        assertTrue(notice.message().contains("Reason: Backup denied"));
        assertTrue(notice.message().contains("RecruiterBuddy will close without opening commands."));
        assertTrue(notice.message().contains("Resolve the file-access problem or restore corrected data"));
    }

    @Test
    public void failed_backupCreated_identifiesBackupAndDoesNotClaimOriginalStillExists() {
        StartupDataNotice notice = StartupDataNotice.failed(DATA_PATH, BACKUP_PATH, "Replacement denied");

        assertTrue(notice.isFatal());
        assertTrue(notice.message().contains("Data file: " + DATA_PATH.toAbsolutePath().normalize()));
        assertTrue(notice.message().contains("The original data was preserved at: "
                + BACKUP_PATH.toAbsolutePath().normalize()));
        assertTrue(notice.message().contains("Reason: Replacement denied"));
        assertFalse(notice.message().contains("The original data remains at the data-file path."));
        assertTrue(notice.message().contains("Keep any backup until restoration is complete."));
    }

    @Test
    public void failed_nullRequiredInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StartupDataNotice.failed(null, BACKUP_PATH, "Failed"));
        assertThrows(NullPointerException.class, () -> StartupDataNotice.failed(DATA_PATH, BACKUP_PATH, null));
    }
}
