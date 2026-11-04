package reloc.org.sat4j.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.LiteralsUtils;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.Lbool;
import reloc.org.sat4j.specs.SearchListenerAdapter;

@Feature("searchlistener")
public class RupSearchListener<S extends ISolverService> extends SearchListenerAdapter<S> {
   private static final long serialVersionUID = 1L;
   private PrintStream out;
   private final File file;

   public RupSearchListener(String filename) {
      this.file = new File(filename);
   }

   @Override
   public void init(S solverService) {
      try {
         this.out = new PrintStream(new FileOutputStream(this.file));
      } catch (FileNotFoundException e) {
         this.out = System.out;
      }
   }

   @Override
   public void end(Lbool result) {
      if (result == Lbool.FALSE) {
         this.out.println("0");
         this.out.close();
      } else {
         this.out.close();
         if (!this.file.delete()) {
            Logger.getLogger("reloc.org.sat4j.core").info("Cannot delete file " + this.file.getName());
         }
      }
   }

   @Override
   public void learn(IConstr c) {
      this.printConstr(c);
   }

   @Override
   public void delete(IConstr c) {
      this.out.print("d ");
      this.printConstr(c);
   }

   private void printConstr(IConstr c) {
      for (int i = 0; i < c.size(); i++) {
         this.out.print(LiteralsUtils.toDimacs(c.get(i)));
         this.out.print(" ");
      }

      this.out.println("0");
   }

   @Override
   public void learnUnit(int p) {
      this.out.print(p);
      this.out.println(" 0");
   }
}
