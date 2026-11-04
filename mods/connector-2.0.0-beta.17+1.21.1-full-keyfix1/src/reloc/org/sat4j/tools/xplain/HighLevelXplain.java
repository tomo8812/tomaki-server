package reloc.org.sat4j.tools.xplain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IConstr;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.GroupClauseSelectorSolver;
import reloc.org.sat4j.tools.SolverDecorator;

public class HighLevelXplain<T extends ISolver> extends GroupClauseSelectorSolver<T> implements Explainer {
   private IVecInt assump;
   private MinimizationStrategy xplainStrategy = new DeletionStrategy();
   private static final long serialVersionUID = 1L;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !HighLevelXplain.class.desiredAssertionStatus();

   public HighLevelXplain(T solver) {
      super(solver);
   }

   @Override
   public IConstr addAtLeast(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public IConstr addAtMost(IVecInt literals, int degree) throws ContradictionException {
      throw new UnsupportedOperationException();
   }

   private IVecInt explanationKeys() throws TimeoutException {
      assert !this.isSatisfiable(this.assump);
      ISolver solver = this.decorated();
      if (solver instanceof SolverDecorator) {
         solver = ((SolverDecorator)solver).decorated();
      }

      return this.xplainStrategy.explain(solver, this.getVarToHighLevel(), this.assump);
   }

   @Override
   public int[] minimalExplanation() throws TimeoutException {
      Collection<Integer> components = this.explain();
      int[] model = new int[components.size()];
      int i = 0;

      for (int c : components) {
         model[i++] = c;
      }

      Arrays.sort(model);
      return model;
   }

   public Collection<Integer> explain() throws TimeoutException {
      IVecInt keys = this.explanationKeys();
      Collection<Integer> explanation = new HashSet<>(keys.size());
      IteratorInt it = keys.iterator();

      while (it.hasNext()) {
         explanation.add(this.getVarToHighLevel().get(it.next()));
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
      System.out.println(prefix + "High Level Explanation (MUS) enabled solver");
      System.out.println(prefix + this.xplainStrategy);
      return super.toString(prefix);
   }

   @Override
   public void setMinimizationStrategy(MinimizationStrategy strategy) {
      this.xplainStrategy = strategy;
   }
}
