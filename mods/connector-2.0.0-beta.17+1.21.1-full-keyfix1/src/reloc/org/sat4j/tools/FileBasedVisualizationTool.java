package reloc.org.sat4j.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileBasedVisualizationTool implements IVisualizationTool {
   private String filename;
   private PrintStream out;

   public FileBasedVisualizationTool(String filename) {
      this.filename = filename;
      this.updateWriter();
   }

   public void updateWriter() {
      try {
         this.out = new PrintStream(new FileOutputStream(this.filename + ".dat"));
      } catch (FileNotFoundException e) {
         this.out = System.out;
      }
   }

   public String getFilename() {
      return this.filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   @Override
   public void addPoint(double x, double y) {
      this.out.println(x + "\t" + y);
   }

   @Override
   public void addInvisiblePoint(double x, double y) {
      this.out.println("#" + x + "\t1/0");
   }

   @Override
   public void init() {
      this.updateWriter();
   }

   @Override
   public void end() {
      this.out.close();
   }
}
