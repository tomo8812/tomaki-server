package reloc.org.sat4j.tools.counting;

import java.math.BigInteger;
import java.util.Arrays;
import reloc.org.sat4j.specs.ISolver;

public class ApproxMC2 extends AbstractApproxMC {
   private int nCells = 2;

   public ApproxMC2(ISolver solver, double epsilon, double delta) {
      super(solver, epsilon, delta);
   }

   public ApproxMC2(ISolver solver, SamplingSet samplingSet, double epsilon, double delta) {
      super(solver, samplingSet, epsilon, delta);
   }

   public ApproxMC2(ISolver solver, SamplingSet samplingSet) {
      super(solver, samplingSet);
   }

   public ApproxMC2(ISolver solver) {
      super(solver);
   }

   @Override
   protected int computeThreshold() {
      return (int)(1.0 + 9.84 * (1.0 + this.epsilon / (1.0 + this.epsilon)) * (1.0 + 1.0 / this.epsilon) * (1.0 + 1.0 / this.epsilon));
   }

   @Override
   protected int computeIterCount() {
      return (int)(17.0 * Math.log(3.0 / this.delta) / Math.log(2.0));
   }

   @Override
   protected BigInteger internalCountModels(int thresh) {
      this.generator.generate(this.samplingSet.nVars());
      long count = this.boundedSAT(thresh);
      this.generator.deactivate();
      if (count >= thresh) {
         return null;
      }

      int nConstrPrev = 32 - Integer.numberOfLeadingZeros(this.nCells) - 1;
      int nConstr = this.logSatSearch(thresh, nConstrPrev);
      BigInteger nSols = BigInteger.valueOf(this.boundedSAT(nConstr, thresh));
      this.generator.clear();
      return nSols.shiftLeft(nConstr);
   }

   private int logSatSearch(int thresh, int nConstrPrev) {
      int loIndex = 0;
      int hiIndex = this.samplingSet.nVars() - 1;
      int nConstr = nConstrPrev;
      int[] cells = new int[this.samplingSet.nVars()];
      Arrays.fill(cells, -1);
      cells[0] = 1;
      cells[this.samplingSet.nVars() - 1] = 0;

      while (true) {
         long y = this.boundedSAT(nConstr, thresh);
         if (y >= thresh) {
            if (cells[nConstr + 1] == 0) {
               return nConstr + 1;
            }

            Arrays.fill(cells, 1, nConstr + 1, 1);
            loIndex = nConstr;
            if (Math.abs(nConstr - nConstrPrev) < 3) {
               nConstr++;
            } else if (nConstr << 1 < this.samplingSet.nVars() - 1) {
               nConstr <<= 1;
            } else {
               nConstr = hiIndex + nConstr >> 1;
            }
         } else {
            if (cells[nConstr - 1] == 1) {
               return nConstr;
            }

            Arrays.fill(cells, nConstr, this.samplingSet.nVars(), 0);
            hiIndex = nConstr;
            if (Math.abs(nConstr - nConstrPrev) < 3) {
               nConstr--;
            } else {
               nConstr = nConstr + loIndex >> 1;
            }
         }
      }
   }

   private long boundedSAT(int nbConstraints, int bound) {
      this.generator.activate(nbConstraints);
      long count = this.boundedSAT(bound);
      this.generator.deactivate(nbConstraints);
      return count;
   }

   @Override
   public void reset() {
      this.nCells = 2;
   }
}
