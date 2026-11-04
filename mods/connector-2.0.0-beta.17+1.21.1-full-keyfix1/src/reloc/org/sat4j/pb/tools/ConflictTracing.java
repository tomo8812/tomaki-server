package reloc.org.sat4j.pb.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolverService;
import reloc.org.sat4j.specs.SearchListenerAdapter;

public class ConflictTracing extends SearchListenerAdapter<ISolverService> {
   private static final long serialVersionUID = 1L;
   private final String filename;
   private PrintStream out;
   private long index = 1L;

   public ConflictTracing(String filename) {
      this.filename = filename;
      this.updateWriter();
   }

   private void updateWriter() {
      try {
         this.out = new PrintStream(new FileOutputStream(this.filename + ".dat"));
      } catch (FileNotFoundException e) {
         this.out = System.out;
      }
   }

   @Override
   public void learn(IConstr c) {
      PBConstr myConstr = (PBConstr)c;
      if (myConstr.size() > 0) {
         this.out.printf("%d %d %d%n", this.index++, myConstr.getCoef(0), myConstr.size());
      }
   }
}
