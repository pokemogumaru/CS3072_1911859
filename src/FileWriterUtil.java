import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterUtil {

    private BufferedWriter writer;
    private String filename;
    private boolean isCsv;
    
    public FileWriterUtil(String filename, String filetype) throws IOException {
    	String FOLDER_PATH = "C:\\Users\\james\\Documents\\TSP_FYP_data";
    	this.filename = FOLDER_PATH + "\\" + filename;
        this.isCsv = filetype.equalsIgnoreCase("csv");
        start();
    }

    public void start() throws IOException { //start by opening file
        writer = new BufferedWriter(new FileWriter(filename)); 
    }
    
    public void addLineTXT(String text) throws IOException {
    	//comment this out when doing large numbers of repeats
        writer.write(text); //add text to txt file
        writer.newLine(); //So next time we add the next on new line
    }
    
    public void printTXT(String text) throws IOException { //the same thing as addLineTXT but on the same line...
        writer.write(text); //add text to txt file
    }

    public void close() throws IOException  { //finish by closing file
        writer.close();
    }

    public void addRowCSV(String text) throws IOException { //add string to next row / cell on the right
        if(!isCsv) { //should only be used by CSV file
            throw new IOException("Not in CSV mode");
        }
        writer.write(text);
        writer.write(",");
    }

    public void addColumnCSV(String text) throws IOException { //add string to next line
        if(!isCsv) {
            throw new IOException("Not in CSV mode");
        } 
        writer.write(text); 
        writer.newLine();
    }
}