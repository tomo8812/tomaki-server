package reloc.org.sat4j.tools.counting;

import java.math.BigInteger;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.TimeoutException;
import reloc.org.sat4j.tools.ModelIteratorToSATAdapter;
import reloc.org.sat4j.tools.SolutionFoundListener;

public final class ModelCounterAdapter implements IModelCounter {
   private final ModelIteratorToSATAdapter adaptee;

   private ModelCounterAdapter(ISolver solver) {
      this.adaptee = new ModelIteratorToSATAdapter(solver, SolutionFoundListener.VOID);
   }

   public static ModelCounterAdapter newInstance(ISolver solver) {
      return newInstance(solver, Long.MAX_VALUE);
   }

   public static ModelCounterAdapter newInstance(ISolver solver, long bound) {
      ModelCounterAdapter counter = new ModelCounterAdapter(solver);
      counter.setBound(bound);
      return counter;
   }

   public static BigInteger countModels(ISolver solver) {
      return countModels(solver, Long.MAX_VALUE);
   }

   public static BigInteger countModels(ISolver solver, long bound) {
      IModelCounter counter = newInstance(solver, bound);
      BigInteger nbModels = counter.countModels();
      counter.reset();
      return nbModels;
   }

   public void setBound(long bound) {
      this.adaptee.setBound(bound);
   }

   @Override
   public BigInteger countModels() {
      try {
         this.adaptee.isSatisfiable();
      } catch (TimeoutException var2) {
      }

      return BigInteger.valueOf(this.adaptee.numberOfModelsFoundSoFar());
   }

   @Override
   public void reset() {
      this.adaptee.clearBlockingClauses();
   }
}
