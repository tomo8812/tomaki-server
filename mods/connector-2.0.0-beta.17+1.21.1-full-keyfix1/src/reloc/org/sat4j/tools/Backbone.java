package reloc.org.sat4j.tools;

import java.util.BitSet;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;

public final class Backbone {
   private static final Backbone.Backboner BB = new Backbone.Backboner() {
      @Override
      IVecInt compute(ISolver solver, IVecInt assumptions, IVecInt litsToTest) throws TimeoutException {
         IVecInt candidates = new VecInt();
         assumptions.copyTo(candidates);
         int initLitsToTestSize = litsToTest.size();
         this.listener.start(initLitsToTestSize);

         for (; !litsToTest.isEmpty(); this.incSatTests()) {
            this.listener.inProgress(initLitsToTestSize - litsToTest.size(), initLitsToTestSize);
            int p = litsToTest.last();
            candidates.push(p);
            litsToTest.pop();
            if (solver.isSatisfiable(candidates)) {
               candidates.pop();
               int[] implicant = this.simplifiedModel(solver);
               removeVarNotPresentAndSatisfiedLits(implicant, litsToTest, solver.nVars());
            } else {
               candidates.pop().push(-p);
            }
         }

         this.listener.end(this.nbSatTests);
         return candidates;
      }
   };
   private static final Backbone.Backboner IBB = new Backbone.Backboner() {
      @Override
      IVecInt compute(ISolver solver, IVecInt assumptions, IVecInt litsToTest) throws TimeoutException {
         IVecInt candidates = new VecInt();
         assumptions.copyTo(candidates);
         int initLitsToTestSize = litsToTest.size();
         this.listener.start(initLitsToTestSize);

         for (; !litsToTest.isEmpty(); this.incSatTests()) {
            this.listener.inProgress(initLitsToTestSize - litsToTest.size(), initLitsToTestSize);

            try {
               IConstr constr = solver.addBlockingClause(litsToTest);
               if (solver.isSatisfiable(candidates)) {
                  int[] implicant = this.simplifiedModel(solver);
                  removeVarNotPresentAndSatisfiedLits(implicant, litsToTest, solver.nVars());
               } else {
                  IteratorInt it = litsToTest.iterator();

                  while (it.hasNext()) {
                     candidates.push(-it.next());
                  }

                  litsToTest.clear();
               }

               solver.removeSubsumedConstr(constr);
            } catch (ContradictionException e) {
               IteratorInt it = litsToTest.iterator();

               while (it.hasNext()) {
                  candidates.push(-it.next());
               }

               litsToTest.clear();
            }
         }

         this.listener.end(this.nbSatTests);
         solver.clearLearntClauses();
         return candidates;
      }
   };
   private final Backbone.Backboner bb;
   private static final Backbone instance = bb();

   private Backbone(Backbone.Backboner bb) {
      this.bb = bb;
   }

   public static Backbone instance() {
      return instance;
   }

   public static Backbone instance(IBackboneProgressListener listener, boolean primeImplicantSimplification) {
      instance.bb.setBackboneProgressListener(listener);
      instance.bb.setImplicantSimplification(primeImplicantSimplification);
      return instance;
   }

   public static Backbone bb() {
      return new Backbone(BB);
   }

   public static Backbone ibb() {
      return new Backbone(IBB);
   }

   public IVecInt compute(ISolver solver) throws TimeoutException {
      return this.compute(solver, VecInt.EMPTY);
   }

   public IVecInt compute(ISolver solver, IVecInt assumptions) throws TimeoutException {
      boolean result = solver.isSatisfiable(assumptions);
      if (!result) {
         throw new IllegalArgumentException("Formula is UNSAT!");
      } else {
         return this.bb.compute(solver, solver.primeImplicant(), assumptions);
      }
   }

   public IVecInt compute(ISolver solver, IVecInt assumptions, IVecInt filter) throws TimeoutException {
      boolean result = solver.isSatisfiable(assumptions);
      if (!result) {
         throw new IllegalArgumentException("Formula is UNSAT!");
      } else {
         return this.bb.compute(solver, solver.primeImplicant(), assumptions, filter);
      }
   }

   public IVecInt compute(ISolver solver, int[] implicant) throws TimeoutException {
      return this.bb.compute(solver, implicant, VecInt.EMPTY);
   }

   public IVecInt compute(ISolver solver, int[] implicant, IVecInt assumptions) throws TimeoutException {
      return this.bb.compute(solver, implicant, assumptions);
   }

   public int getNumberOfSatCalls() {
      return this.bb.nbSatTests();
   }

   abstract static class Backboner {
      protected IBackboneProgressListener listener = IBackboneProgressListener.VOID;
      protected int nbSatTests;
      private boolean implicant = true;

      public void setBackboneProgressListener(IBackboneProgressListener listener) {
         this.listener = listener;
      }

      public void setImplicantSimplification(boolean b) {
         this.implicant = b;
      }

      public int[] simplifiedModel(ISolver solver) {
         return this.implicant ? solver.primeImplicant() : solver.model();
      }

      public IVecInt compute(ISolver solver, int[] implicant, IVecInt assumptions) throws TimeoutException {
         this.nbSatTests = 0;
         BitSet assumptionsSet = new BitSet(solver.nVars());
         IteratorInt it = assumptions.iterator();

         while (it.hasNext()) {
            assumptionsSet.set(Math.abs(it.next()));
         }

         IVecInt litsToTest = new VecInt();

         for (int p : implicant) {
            if (!assumptionsSet.get(Math.abs(p))) {
               litsToTest.push(-p);
            }
         }

         return this.compute(solver, assumptions, litsToTest);
      }

      public int nbSatTests() {
         return this.nbSatTests;
      }

      void incSatTests() {
         this.nbSatTests++;
      }

      public IVecInt compute(ISolver solver, int[] implicant, IVecInt assumptions, IVecInt filter) throws TimeoutException {
         this.nbSatTests = 0;
         BitSet assumptionsSet = new BitSet(solver.nVars());
         IteratorInt it = assumptions.iterator();

         while (it.hasNext()) {
            assumptionsSet.set(Math.abs(it.next()));
         }

         BitSet filterSet = new BitSet();
         IteratorInt itx = filter.iterator();

         while (itx.hasNext()) {
            filterSet.set(Math.abs(itx.next()));
         }

         IVecInt litsToTest = new VecInt();

         for (int p : implicant) {
            if (!assumptionsSet.get(Math.abs(p)) && filterSet.get(Math.abs(p))) {
               litsToTest.push(-p);
            }
         }

         return this.compute(solver, assumptions, litsToTest);
      }

      abstract IVecInt compute(ISolver var1, IVecInt var2, IVecInt var3) throws TimeoutException;

      static void removeVarNotPresentAndSatisfiedLits(int[] implicant, IVecInt litsToTest, int n) {
         int[] marks = new int[n + 1];

         for (int p : implicant) {
            marks[p > 0 ? p : -p] = p;
         }

         int i = 0;

         while (i < litsToTest.size()) {
            int q = litsToTest.get(i);
            int mark = marks[q > 0 ? q : -q];
            if (mark != 0 && mark != q) {
               i++;
            } else {
               litsToTest.delete(i);
            }
         }
      }
   }
}
