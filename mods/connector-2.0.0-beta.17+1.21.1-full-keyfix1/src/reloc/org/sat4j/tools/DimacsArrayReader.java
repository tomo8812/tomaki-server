package reloc.org.sat4j.tools;

import java.io.Serializable;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public class DimacsArrayReader implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final ISolver solver;

   public DimacsArrayReader(ISolver solver) {
      this.solver = solver;
   }

   protected boolean handleConstr(int gateType, int output, int[] inputs) throws ContradictionException {
      IVecInt literals = new VecInt(inputs);
      this.solver.addClause(literals);
      return true;
   }

   public ISolver parseInstance(int[] gateType, int[] outputs, int[][] inputs, int maxVar) throws ContradictionException {
      this.solver.reset();
      this.solver.newVar(maxVar);
      this.solver.setExpectedNumberOfClauses(outputs.length);

      for (int i = 0; i < outputs.length; i++) {
         this.handleConstr(gateType[i], outputs[i], inputs[i]);
      }

      return this.solver;
   }

   public String decode(int[] model) {
      StringBuilder stb = new StringBuilder(4 * model.length);

      for (int element : model) {
         stb.append(element);
         stb.append(" ");
      }

      stb.append("0");
      return stb.toString();
   }

   protected ISolver getSolver() {
      return this.solver;
   }
}
