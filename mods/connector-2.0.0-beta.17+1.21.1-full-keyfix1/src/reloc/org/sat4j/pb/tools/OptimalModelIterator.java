package reloc.org.sat4j.pb.tools;

import java.math.BigInteger;
import reloc.org.sat4j.pb.IPBSolver;
import reloc.org.sat4j.pb.ObjectiveFunction;
import reloc.org.sat4j.pb.PBSolverDecorator;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

public class OptimalModelIterator extends PBSolverDecorator {
   private final long bound;
   protected long nbModelFound = 0L;
   private boolean trivialfalsity = false;
   private boolean isFirstModel = true;
   private BigInteger objectiveValue;
   private final IPBSolver solver;
   private static final long serialVersionUID = 1L;

   public OptimalModelIterator(IPBSolver solver) {
      this(solver, Long.MAX_VALUE);
   }

   public OptimalModelIterator(IPBSolver solver, long bound) {
      super(solver);
      this.solver = solver;
      this.bound = bound;
   }

   @Override
   public int[] model() {
      int[] last = super.model();
      this.nbModelFound++;

      try {
         ObjectiveFunction obj = this.getObjectiveFunction();
         if (this.isFirstModel && obj != null) {
            this.objectiveValue = obj.calculateDegree(this.solver);
            this.solver.addAtMost(obj.getVars(), obj.getCoeffs(), this.objectiveValue);
            this.isFirstModel = false;
         }
      } catch (ContradictionException e) {
         this.trivialfalsity = true;
      }

      return last;
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      if (!this.trivialfalsity && this.nbModelFound < this.bound) {
         this.trivialfalsity = false;
         return super.isSatisfiable(true);
      } else {
         return false;
      }
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      if (!this.trivialfalsity && this.nbModelFound < this.bound) {
         this.trivialfalsity = false;
         return super.isSatisfiable(assumps, true);
      } else {
         return false;
      }
   }

   @Override
   public void reset() {
      this.trivialfalsity = false;
      this.nbModelFound = 0L;
      super.reset();
   }
}
