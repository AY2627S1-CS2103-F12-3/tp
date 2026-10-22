package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;

/**
 * Preserves unreadable recruitment data before creating an empty replacement.
 */
public class RecruitmentDataRecovery {
    private static final Logger logger = LogsCenter.getLogger(RecruitmentDataRecovery.class);
    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final Clock clock;

    public RecruitmentDataRecovery() {
        this(Clock.systemDefaultZone());
    }

    RecruitmentDataRecovery(Clock clock) {
        this.clock = requireNonNull(clock);
    }

    /**
     * Moves a regular data file to an unused sibling backup path, preserving its contents.
     *
     * @throws IOException if the path is not a regular file or the move fails.
     */
    public Path backUp(Path dataFilePath) throws IOException {
        requireNonNull(dataFilePath);
        BasicFileAttributes attributes = Files.readAttributes(dataFilePath, BasicFileAttributes.class,
                LinkOption.NOFOLLOW_LINKS);
        if (!attributes.isRegularFile()) {
            throw new IOException("The recruitment-data path is not a regular file.");
        }

        String backupName = dataFilePath.getFileName() + ".bak";
        String timestamp = LocalDateTime.now(clock).format(BACKUP_TIMESTAMP_FORMAT);
        Path backupPath = dataFilePath.resolveSibling(backupName);
        int collisions = 0;
        while (true) {
            try {
                // Omitting REPLACE_EXISTING also protects backups created after the naming decision.
                Files.move(dataFilePath, backupPath);
                logger.info("Preserved unreadable recruitment data at " + backupPath);
                return backupPath;
            } catch (FileAlreadyExistsException e) {
                String suffix = collisions == 0 ? "" : "-" + collisions;
                backupPath = dataFilePath.resolveSibling(backupName + "." + timestamp + suffix);
                collisions++;
            }
        }
    }

    /**
     * Creates valid empty recruitment data without replacing an existing file or following a symbolic link.
     * Call only after the original file has been backed up successfully.
     *
     * @throws IOException if the replacement cannot be created or already exists.
     */
    public void createEmptyDataFile(Path dataFilePath) throws IOException {
        requireNonNull(dataFilePath);
        String emptyData = JsonUtil.toJsonString(new JsonSerializableAddressBook(new AddressBook()));
        Files.writeString(dataFilePath, emptyData, StandardOpenOption.CREATE_NEW);
        logger.info("Created empty recruitment data at " + dataFilePath);
    }
}
