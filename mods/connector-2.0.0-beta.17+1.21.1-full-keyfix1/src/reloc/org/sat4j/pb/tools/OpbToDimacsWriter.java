package reloc.org.sat4j.pb.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.PBSolverHandle;
import reloc.org.sat4j.pb.reader.OPBReader2012;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.tools.DimacsStringSolver;
import reloc.org.sat4j.tools.encoding.EncodingStrategy;
import reloc.org.sat4j.tools.encoding.Policy;

public class OpbToDimacsWriter {
   public static void main(String[] args) throws ParseFormatException, IOException, ContradictionException {
      new OpbToDimacsWriter(args);
   }

   public OpbToDimacsWriter(String[] args) throws ParseFormatException, IOException, ContradictionException {
      Set<EncodingStrategy> tabooEncodings = new HashSet<>();
      tabooEncodings.add(EncodingStrategy.NATIVE);

      for (EncodingStrategy strategy : EncodingStrategy.values()) {
         if (!tabooEncodings.contains(strategy)) {
            DimacsStringSolver dss = new DimacsStringSolver();
            IPBSolver solver = new ClausalConstraintsDecorator(new PBAdapter(dss), Policy.getAdapterFromEncodingName(strategy));
            OPBReader2012 reader = new OPBReader2012(new PBSolverHandle(solver));
            solver.setVerbose(false);
            reader.parseInstance(args[0]);
            FileWriter fwriter = new FileWriter(args[0] + ".cardEncoding." + strategy.name() + ".cnf");

            try {
               fwriter.write(dss.toString());
            } finally {
               fwriter.close();
            }
         }
      }
   }
}
