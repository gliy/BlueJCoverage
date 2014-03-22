package bluej.codecoverage.utils.join;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a source file located on the machine.
 * <p>
 * This allows for easier pairing of coverage information to specific lines.
 * TODO: change to iterator?
 * @see Locatable
 * @author Ian
 * 
 */
public interface ClassInfo {
   /**
    * The name of the file as stored on the local filesystem.
    * @return the full name of the file, including the path.
    */
   String getName();

   /**
    * Returns the text at the specified line number.
    * @param lineNum line to get text for.
    * @return The entire String between the start of the line, and the end of the line.
    */
   String getLine(int lineNum);

   /**
    * A Unique Id for the instance.
    * @return a UUID for identifying the class.
    */
   String getId();

   /**
    * Returns the total number of lines in the file.
    * <p>
    * The caller will be able to call {@link #getLine(int)}
    * for all values 0 <= n < the return of this method.
    * @return number of lines in the file.
    */
   int getNumberOfLines();
}

class FileClassInfo implements ClassInfo {
   private File file;
   private String name;
   private LineNumberReader reader;
   private List<String> fileData;
   private String id;

   public FileClassInfo(File source, String name) throws Exception {
      this.file = source;
      this.name = name;
      this.id = UUID.randomUUID().toString();
      reader = new LineNumberReader(new InputStreamReader(new FileInputStream(
            source)));
      fileData = new ArrayList<String>();
      String input;
      while ((input = reader.readLine()) != null) {
         fileData.add(input);
      }
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getLine(int lineNum) {
      return fileData.get(lineNum);
   }

   @Override
   public int getNumberOfLines() {
      return fileData.size();
   }

   @Override
   public String getId() {
      return id;
   }
}
