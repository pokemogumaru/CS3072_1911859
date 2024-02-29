import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant; // Import for nanosecond accuracy

public class FileUtils {
	//used so programs can say if they are keeping files open
	
    private static final String FILE_PATH = "C:\\Users\\james\\Documents\\TSP_FYP_data\\_OpenFiles";

    // Creates a new file with "open_" prefix and current timestamp
    public static String createOpenFile() {
        try {
            // Ensure the directory exists
            Files.createDirectories(Paths.get(FILE_PATH));
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = now.format(formatter);
            // Get nanosecond precision
            Instant instant = Instant.now();
            long nanoseconds = instant.getNano(); 
            String filename = "open_" + timestamp + "_" + nanoseconds + ".open"; // Underscore added
            File newFile = new File(FILE_PATH, filename);
            newFile.createNewFile();
            return filename;
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
            return null; 
        }
    }

    // Checks if the open files folder has any files
    public static boolean hasOpenFiles() {
        File directory = new File(FILE_PATH);
        File[] files = directory.listFiles();
        return files != null && files.length > 0; 
    }

    // Deletes a file by filename
    public static boolean deleteOpenFile(String filename) {
        try {
            Path filePath = Paths.get(FILE_PATH, filename);
            return Files.deleteIfExists(filePath); // Returns true if deletion successful
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false; 
        }
    }
}
