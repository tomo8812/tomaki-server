package reloc.org.sat4j.pb.reader;

import java.util.Collection;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.PBSolverHandle;
import reloc.org.sat4j.pb.PseudoOptDecorator;
import reloc.org.sat4j.reader.InstanceReader;
import reloc.org.sat4j.reader.Reader;

public class PBInstanceReader extends InstanceReader {
   private OPBReader2012 opb;
   private final IPBSolver solver;

   public PBInstanceReader(IPBSolver solver) {
      super(solver);
      this.solver = solver;
   }

   private Reader getDefaultOPBReader() {
      if (this.opb == null) {
         this.opb = new OPBReader2012(new PBSolverHandle(new PseudoOptDecorator(this.solver)));
      }

      return this.opb;
   }

   public boolean hasObjectiveFunction() {
      return this.opb.hasObjFunc;
   }

   @Override
   protected Reader handleFileName(String fname, String prefix) {
      return !fname.endsWith(".opb") && !"PB".equals(prefix) ? super.handleFileName(fname, prefix) : this.getDefaultOPBReader();
   }

   @Override
   protected Collection<String> getReservedPrefixes() {
      Collection<String> prefixes = super.getReservedPrefixes();
      prefixes.add("PB");
      return prefixes;
   }
}
