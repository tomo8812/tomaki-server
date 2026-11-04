package reloc.org.sat4j.pb.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.OPBStringSolver;
import reloc.org.sat4j.reader.DimacsReader;
import reloc.org.sat4j.reader.ParseFormatException;
import reloc.org.sat4j.specs.ContradictionException;

public class DimacsToOpb {
   private static final Logger LOGGER = Logger.getLogger("reloc.org.sat4j.pb");
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !DimacsToOpb.class.desiredAssertionStatus();

   public static void main(String[] args) {
      if (args.length != 1) {
         System.out.println("Usage : dimacs2opb filename.cnf");
      } else {
         String cnfFileName = args[0];
         assert cnfFileName.endsWith("cnf");
         String opbFileName = cnfFileName.replace("cnf", "opb");
         IPBSolver solver = new OPBStringSolver();
         DimacsReader reader = new DimacsReader(solver);

         try {
            reader.parseInstance(cnfFileName);
            PrintWriter out = new PrintWriter(new FileWriter(opbFileName));
            out.println(solver.toString());
            out.close();
         } catch (ParseFormatException e) {
            LOGGER.log(Level.INFO, "Input format error", e);
         } catch (IOException e) {
            LOGGER.log(Level.INFO, "Input error", e);
         } catch (ContradictionException e) {
            LOGGER.log(Level.INFO, "Formula is UNSAT", e);
         }
      }
   }
}
