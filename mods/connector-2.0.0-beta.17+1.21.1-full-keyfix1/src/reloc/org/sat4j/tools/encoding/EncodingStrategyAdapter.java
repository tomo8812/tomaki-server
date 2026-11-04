package reloc.org.sat4j.tools.encoding;

import java.io.Serializable;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;

public abstract class EncodingStrategyAdapter implements Serializable {
   private static final long serialVersionUID = 1L;

   public IConstr addAtLeast(ISolver solver, IVecInt literals, int degree) throws ContradictionException {
      int n = literals.size();
      IVecInt newLiterals = new VecInt(n);

      for (int i = 0; i < n; i++) {
         newLiterals.push(-literals.get(i));
      }

      return this.addAtMost(solver, newLiterals, n - degree);
   }

   public IConstr addAtLeastOne(ISolver solver, IVecInt literals) throws ContradictionException {
      return solver.addClause(literals);
   }

   public IConstr addAtMost(ISolver solver, IVecInt literals, int degree) throws ContradictionException {
      return solver.addAtMost(literals, degree);
   }

   public IConstr addAtMostOne(ISolver solver, IVecInt literals) throws ContradictionException {
      return this.addAtMost(solver, literals, 1);
   }

   public IConstr addExactly(ISolver solver, IVecInt literals, int degree) throws ContradictionException {
      return solver.addExactly(literals, degree);
   }

   public IConstr addExactlyOne(ISolver solver, IVecInt literals) throws ContradictionException {
      return this.addExactly(solver, literals, 1);
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }
}
