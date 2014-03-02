package bluej.codecoverage.utils.join;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ClassInfo {
   String getName();

   String getLine(int lineNum);

   String getId();

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
