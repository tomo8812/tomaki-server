package reloc.org.sat4j.tools.xplain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.FullClauseSelectorSolver;
import reloc.org.sat4j.tools.SolverDecorator;

public class Xplain<T extends ISolver> extends FullClauseSelectorSolver<T> implements Explainer {
   private IVecInt assump;
   private MinimizationStrategy xplainStrategy = new DeletionStrategy();
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !Xplain.class.desiredAssertionStatus();

   public Xplain(T solver, boolean skipDuplicatedEntries) {
      super(solver, skipDuplicatedEntries);
   }

   public Xplain(T solver) {
      this(solver, true);
   }

   @Override
   public IConstr addExactly(IVecInt literals, int n) throws ContradictionException {
      throw new UnsupportedOperationException("Explanation requires Pseudo Boolean support. See XplainPB class instead.");
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException("Explanation requires Pseudo Boolean support. See XplainPB class instead.");
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException("Explanation requires Pseudo Boolean support. See XplainPB class instead.");
   }

   private IVecInt explanationKeys() throws TimeoutException {
      assert !this.isSatisfiable(this.assump);
      ISolver solver = this.decorated();
      if (solver instanceof SolverDecorator) {
         solver = ((SolverDecorator)solver).decorated();
      }

      return this.xplainStrategy.explain(solver, this.getConstrs(), this.assump);
   }

   @Override
   public int[] minimalExplanation() throws TimeoutException {
      IVecInt keys = this.explanationKeys();
      keys.sort();
      List<Integer> allKeys = new ArrayList<>(this.getConstrs().keySet());
      Collections.sort(allKeys);
      int[] model = new int[keys.size()];
      int i = 0;
      IteratorInt it = keys.iterator();

      while (it.hasNext()) {
         model[i++] = allKeys.indexOf(it.next()) + 1;
      }

      return model;
   }

   public Collection<IConstr> explain() throws TimeoutException {
      IVecInt keys = this.explanationKeys();
      Collection<IConstr> explanation = new ArrayList<>(keys.size());
      IteratorInt it = keys.iterator();

      while (it.hasNext()) {
         explanation.add(this.getConstrs().get(it.next()));
      }

      return explanation;
   }

   public void cancelExplanation() {
      this.xplainStrategy.cancelExplanationComputation();
   }

   @Override
   public int[] findModel() throws TimeoutException {
      this.assump = VecInt.EMPTY;
      return super.findModel();
   }

   @Override
   public int[] findModel(IVecInt assumps) throws TimeoutException {
      this.assump = assumps;
      return super.findModel(assumps);
   }

   @Override
   public boolean isSatisfiable() throws TimeoutException {
      this.assump = VecInt.EMPTY;
      return super.isSatisfiable();
   }

   @Override
   public boolean isSatisfiable(boolean global) throws TimeoutException {
      this.assump = VecInt.EMPTY;
      return super.isSatisfiable(global);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
      this.assump = assumps;
      return super.isSatisfiable(assumps);
   }

   @Override
   public boolean isSatisfiable(IVecInt assumps, boolean global) throws TimeoutException {
      this.assump = assumps;
      return super.isSatisfiable(assumps, global);
   }

   @Override
   public String toString(String prefix) {
      System.out.println(prefix + "Explanation (MUS) enabled solver");
      System.out.println(prefix + this.xplainStrategy);
      return super.toString(prefix);
   }

   @Override
   public void setMinimizationStrategy(MinimizationStrategy strategy) {
      this.xplainStrategy = strategy;
   }

   @Override
   public boolean removeConstr(IConstr c) {
      if (this.getLastConstr() == c) {
         this.getLastClause().clear();
         this.setLastConstr(null);
      }

      return super.removeConstr(c);
   }

   @Override
   public boolean removeSubsumedConstr(IConstr c) {
      if (this.getLastConstr() == c) {
         this.getLastClause().clear();
         this.setLastConstr(null);
      }

      return super.removeSubsumedConstr(c);
   }
}
