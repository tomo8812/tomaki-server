package reloc.org.sat4j.tools;

import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.core.ConstrGroup;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.TimeoutException;

@Feature("solver")
public class ModelIterator extends SolverDecorator<ISolver> {
   private static final long serialVersionUID = 1L;
   protected boolean trivialfalsity = false;
   private long bound;
   protected long nbModelFound = 0L;
   private final ConstrGroup blockingClauses = new ConstrGroup();

   public ModelIterator(ISolver solver) {
      this(solver, Long.MAX_VALUE);
   }

   public ModelIterator(ISolver solver, long bound) {
      super(solver);
      this.bound = bound;
   }

   @Override
   public int[] model() {
      int[] last = super.model();
      this.nbModelFound = this.nbModelFound + (1 << this.nVars() - last.length);

      try {
         this.blockingClauses.add(this.discardCurrentModel());
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
         this.expireTimeout();
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
   public boolean isSatisfiable(boolean global) throws TimeoutException {
      return this.isSatisfiable();
   }

   @Override
   public void reset() {
      this.trivialfalsity = false;
      this.nbModelFound = 0L;
      super.reset();
   }

   @Override
   public int[] primeImplicant() {
      int[] last = super.primeImplicant();
      this.nbModelFound = (long)(this.nbModelFound + Math.pow(2.0, this.nVars() - last.length));
      IVecInt clause = new VecInt(last.length);

      for (int q : last) {
         clause.push(-q);
      }

      try {
         this.blockingClauses.add(this.addBlockingClause(clause));
      } catch (ContradictionException e) {
         this.trivialfalsity = true;
      }

      return last;
   }

   public long numberOfModelsFoundSoFar() {
      return this.nbModelFound;
   }

   public void clearBlockingClauses() {
      this.trivialfalsity = false;
      this.nbModelFound = 0L;
      this.blockingClauses.removeFrom(this);
      this.blockingClauses.clear();
   }

   public long getBound() {
      return this.bound;
   }

   public void setBound(long bound) {
      this.bound = bound;
   }
}
