package reloc.org.sat4j.tools.counting;

import java.math.BigInteger;
import java.util.Comparator;
import reloc.org.sat4j.core.Vec;
import reloc.org.sat4j.specs.ISolver;
import reloc.org.sat4j.specs.IVec;

public abstract class AbstractApproxMC implements IModelCounter {
   public static final double DEFAULT_EPSILON = 0.1;
   public static final double DEFAULT_DELTA = 0.1;
   protected final double epsilon;
   protected final double delta;
   protected final SamplingSet samplingSet;
   protected final ParityConstraintGenerator generator;
   private final ModelCounterAdapter counter;

   protected AbstractApproxMC(ISolver solver) {
      this(solver, AllVariablesSamplingSet.of(solver));
   }

   protected AbstractApproxMC(ISolver solver, SamplingSet samplingSet) {
      this(solver, samplingSet, 0.1, 0.1);
   }

   protected AbstractApproxMC(ISolver solver, double epsilon, double delta) {
      this(solver, AllVariablesSamplingSet.of(solver), epsilon, delta);
   }

   protected AbstractApproxMC(ISolver solver, SamplingSet samplingSet, double epsilon, double delta) {
      this.counter = ModelCounterAdapter.newInstance(solver);
      this.epsilon = epsilon;
      this.delta = delta;
      this.samplingSet = samplingSet;
      this.generator = new ParityConstraintGenerator(solver, samplingSet);
   }

   @Override
   public final BigInteger countModels() {
      int threshold = this.computeThreshold();
      long initialCount = this.boundedSAT(threshold);
      if (initialCount < threshold) {
         return BigInteger.valueOf(initialCount);
      }

      int iterCount = this.computeIterCount();
      IVec<BigInteger> counts = new Vec<>(iterCount);

      for (int i = 0; i < iterCount; i++) {
         BigInteger count = this.internalCountModels(threshold);
         if (count != null) {
            counts.push(count);
         }
      }

      return findMedian(counts);
   }

   protected abstract int computeThreshold();

   protected abstract int computeIterCount();

   protected abstract BigInteger internalCountModels(int var1);

   protected final long boundedSAT(int bound) {
      this.counter.setBound(bound);
      BigInteger foundModels = this.counter.countModels();
      this.counter.reset();
      return foundModels.longValue();
   }

   @Override
   public void reset() {
   }

   private static BigInteger findMedian(IVec<BigInteger> values) {
      if (values.isEmpty()) {
         return BigInteger.ZERO;
      }

      values.sort(new Comparator<BigInteger>() {
         public int compare(BigInteger o1, BigInteger o2) {
            return o1.compareTo(o2);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public int compare(Object var1, Object var2) {
            return this.compare((BigInteger)var1, (BigInteger)var2);
         }
      });
      return values.get(values.size() >> 1);
   }
}
