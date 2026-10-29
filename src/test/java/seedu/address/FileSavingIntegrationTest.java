package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.testutil.FileSavingTestDriver;

/**
 * Integrates MainApp, LogsCenter and storage using isolated JVMs and temporary directories.
 * Run through Gradle so test.appJar points to the freshly built application JAR.
 */
public class FileSavingIntegrationTest {

    @TempDir
    public Path testFolder;

    @Test
    public void saveAndReload_differentWorkingDirectory_usesJarDirectory() throws Exception {
        assertSaveAndReload("application");
    }

    @Test
    public void saveAndReload_spacesAndHashInDirectory_preservesLiteralPath() throws Exception {
        assertSaveAndReload("application space #");
    }

    @Test
    public void saveAndReload_percentPatternsInDirectory_preservesLiteralPath() throws Exception {
        assertSaveAndReload("application %g %u %% %h %t");
    }

    @Test
    public void saveAndReload_classDirectory_usesWorkingDirectory() throws Exception {
        assertDevelopmentSaveAndReload("working");
    }

    @Test
    public void saveAndReload_classDirectoryWithSpecialWorkingPath_preservesLiteralPath() throws Exception {
        assertDevelopmentSaveAndReload("working space # %g %u %%");
    }

    @Test
    public void init_corruptDevelopmentData_recoversInWorkingDirectory() throws Exception {
        Path classes = copyApplicationClasses();
        Path workingDirectory = Files.createDirectory(testFolder.resolve("working"));
        assertRecovery(developmentClassPath(classes), workingDirectory, workingDirectory, classes.getParent());
    }

    @Test
    public void init_corruptJarData_recoversBesideJar() throws Exception {
        Path jar = copyJar("application");
        Path workingDirectory = Files.createDirectory(testFolder.resolve("working"));
        assertRecovery(jar.toString(), workingDirectory, jar.getParent(), workingDirectory);
    }

    @Test
    public void init_logFileCannotBeOpened_continuesWithoutFileLogging() throws Exception {
        Path jar = copyJar("application");
        // A directory at the log filename forces an IOException even with elevated filesystem permissions.
        Files.createDirectory(jar.getParent().resolve("addressbook.log.0"));
        Path workingDirectory = Files.createDirectory(testFolder.resolve("working"));

        String output = runDriver(jar.toString(), workingDirectory, "write", jar.getParent());

        assertTrue(output.contains("Error adding file handler for logger."), output);
        assertTrue(Files.isRegularFile(jar.getParent().resolve("data/addressbook.json")));
        assertTrue(Files.isRegularFile(jar.getParent().resolve("preferences.json")));
    }

    private void assertSaveAndReload(String directoryName) throws Exception {
        Path jar = copyJar(directoryName);
        Path workingDirectory = Files.createDirectory(testFolder.resolve("working"));
        runDriver(jar.toString(), workingDirectory, "write", jar.getParent());

        assertTrue(Files.isRegularFile(jar.getParent().resolve("data/addressbook.json")));
        assertTrue(Files.isRegularFile(jar.getParent().resolve("preferences.json")));
        assertTrue(Files.readString(jar.getParent().resolve("addressbook.log.0"))
                .contains("File-saving integration test marker"));
        try (Stream<Path> files = Files.list(workingDirectory)) {
            assertEquals(0, files.count(), "No application files should be created in the working directory");
        }

        // Decoy files must neither override saved state beside the JAR nor be overwritten on the next launch.
        Files.createDirectory(workingDirectory.resolve("data"));
        Path decoyData = workingDirectory.resolve("data/addressbook.json");
        Path decoyPrefs = workingDirectory.resolve("preferences.json");
        Files.writeString(decoyData, "{}");
        Files.writeString(decoyPrefs, "{}");
        runDriver(jar.toString(), workingDirectory, "read", jar.getParent());

        assertEquals("{}", Files.readString(decoyData));
        assertEquals("{}", Files.readString(decoyPrefs));
        assertFalse(Files.exists(workingDirectory.resolve("addressbook.log.0")));
        try (Stream<Path> files = Files.list(jar.getParent())) {
            assertFalse(files.anyMatch(path -> path.toString().endsWith(".lck")), "Log locks must be released");
        }
    }

    private Path copyJar(String directoryName) throws Exception {
        Path directory = Files.createDirectory(testFolder.resolve(directoryName));
        return Files.copy(Path.of(System.getProperty("test.appJar")), directory.resolve("addressbook.jar"));
    }

    private void assertDevelopmentSaveAndReload(String directoryName) throws Exception {
        Path classes = copyApplicationClasses();
        Path workingDirectory = Files.createDirectory(testFolder.resolve(directoryName));
        Path decoyData = classes.getParent().resolve("data/addressbook.json");
        Path decoyPrefs = classes.getParent().resolve("preferences.json");
        Files.createDirectories(decoyData.getParent());
        Files.writeString(decoyData, "{}");
        Files.writeString(decoyPrefs, "{}");

        runDriver(developmentClassPath(classes), workingDirectory, "write", workingDirectory);
        assertTrue(Files.isRegularFile(workingDirectory.resolve("data/addressbook.json")));
        assertTrue(Files.isRegularFile(workingDirectory.resolve("preferences.json")));
        assertTrue(Files.readString(workingDirectory.resolve("addressbook.log.0"))
                .contains("File-saving integration test marker"));
        runDriver(developmentClassPath(classes), workingDirectory, "read", workingDirectory);

        assertEquals("{}", Files.readString(decoyData));
        assertEquals("{}", Files.readString(decoyPrefs));
        assertFalse(Files.exists(classes.getParent().resolve("addressbook.log.0")));
        assertFalse(Files.exists(classes.resolve("data")));
        assertFalse(Files.exists(classes.resolve("preferences.json")));
    }

    private void assertRecovery(String appClassPath, Path workingDirectory, Path expectedDirectory,
            Path decoyDirectory) throws Exception {
        Path data = expectedDirectory.resolve("data/addressbook.json");
        Files.createDirectories(data.getParent());
        Files.writeString(data, "{broken recruitment data");
        Path decoyData = decoyDirectory.resolve("data/addressbook.json");
        Files.createDirectories(decoyData.getParent());
        Files.writeString(decoyData, "{unrelated data");

        runDriver(appClassPath, workingDirectory, "recover", expectedDirectory);
        runDriver(appClassPath, workingDirectory, "recover", expectedDirectory);

        assertEquals("{broken recruitment data", Files.readString(data.resolveSibling("addressbook.json.bak")));
        assertEquals("{unrelated data", Files.readString(decoyData));
        assertFalse(Files.exists(decoyData.resolveSibling("addressbook.json.bak")));
        try (Stream<Path> files = Files.list(data.getParent())) {
            assertEquals(1, files.filter(path -> path.getFileName().toString()
                    .startsWith("addressbook.json.bak")).count(), "Restart must not create another backup");
        }
    }

    private Path copyApplicationClasses() throws Exception {
        Path source = Path.of(MainApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path classes = Files.createDirectories(testFolder.resolve("development space # %g/classes"));
        try (Stream<Path> paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path destination = classes.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination);
                }
            }
        }
        return classes;
    }

    private String developmentClassPath(Path classes) {
        // Load production classes from a directory, with the JAR supplying third-party dependencies.
        return classes + File.pathSeparator + Path.of(System.getProperty("test.appJar")).toAbsolutePath();
    }

    private String runDriver(String appClassPath, Path workingDirectory, String mode, Path expectedDirectory)
            throws Exception {
        Path testClasses = Path.of(FileSavingTestDriver.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path outputFile = Files.createTempFile(testFolder, "process-", ".log");
        Process process = new ProcessBuilder(java.toString(), "-ea", "-cp",
                appClassPath + File.pathSeparator + testClasses, FileSavingTestDriver.class.getName(),
                mode, expectedDirectory.toString())
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(outputFile.toFile())
                .start();
        try {
            assertTrue(process.waitFor(30, TimeUnit.SECONDS), "Application test process timed out");
            String output = Files.readString(outputFile);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(10, TimeUnit.SECONDS);
            }
        }
    }
}
