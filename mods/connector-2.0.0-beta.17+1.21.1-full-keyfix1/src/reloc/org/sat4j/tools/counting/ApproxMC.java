package reloc.org.sat4j.tools.counting;

import java.math.BigInteger;
import reloc.org.sat4j.specs.ISolver;

public final class ApproxMC extends AbstractApproxMC {
   public ApproxMC(ISolver solver, double epsilon, double delta) {
      super(solver, epsilon, delta);
   }

   public ApproxMC(ISolver solver, SamplingSet samplingSet, double epsilon, double delta) {
      super(solver, samplingSet, epsilon, delta);
   }

   public ApproxMC(ISolver solver, SamplingSet samplingSet) {
      super(solver, samplingSet);
   }

   public ApproxMC(ISolver solver) {
      super(solver);
   }

   @Override
   protected int computeThreshold() {
      return (int)(3.0 * Math.exp(0.5) * (1.0 + 1.0 / this.epsilon) * (1.0 + 1.0 / this.epsilon)) << 1;
   }

   @Override
   protected int computeIterCount() {
      return (int)(35.0 * Math.log(3.0 / this.delta) / Math.log(2.0));
   }

   @Override
   protected BigInteger internalCountModels(int threshold) {
      int l = 32 - Integer.numberOfLeadingZeros(threshold) - 2;

      for (int i = l; i <= this.samplingSet.nVars(); i++) {
         long count = this.boundedSAT(i - l, threshold + 1);
         if (1L <= count && count <= threshold) {
            return BigInteger.valueOf(count).shiftLeft(i - l);
         }
      }

      return null;
   }

   private long boundedSAT(int nbConstraints, int bound) {
      this.generator.generate(nbConstraints);
      long count = this.boundedSAT(bound);
      this.generator.clear();
      return count;
   }
}
