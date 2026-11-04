package reloc.org.sat4j.pb.core;

import java.io.PrintWriter;
import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.SolverStats;

public class PBSolverStats extends SolverStats {
   private static final long serialVersionUID = 1L;
   private long numberOfReductions;
   private long numberOfReductionsByPower2;
   private long numberOfRightShiftsForCoeffs;
   private long numberOfReductionsByGCD;
   private long numberOfLearnedConstraintsReduced;
   private long numberOfResolution;
   private long numberOfCP;
   private long numberOfRoundingOperations;
   private long numberOfEasyRoundingOperations;
   private long numberOfEndingSkipping;
   private long numberOfInternalSkipping;
   private long numberOfDerivationSteps;
   private long numberOfRemainingUnassigned;
   private long numberOfRemainingAssigned;
   private long falsifiedLiteralsRemovedFromConflict;
   private long falsifiedLiteralsRemovedFromReason;
   private long timeForArtithmeticOperations;
   private BigInteger minRemoved;
   private BigInteger maxRemoved;
   private int nbRemoved;

   @Override
   public void reset() {
      super.reset();
      this.numberOfReductions = 0L;
      this.numberOfLearnedConstraintsReduced = 0L;
      this.numberOfResolution = 0L;
      this.numberOfCP = 0L;
      this.numberOfRoundingOperations = 0L;
      this.numberOfReductionsByPower2 = 0L;
      this.numberOfRightShiftsForCoeffs = 0L;
      this.numberOfReductionsByGCD = 0L;
      this.numberOfEndingSkipping = 0L;
      this.numberOfInternalSkipping = 0L;
      this.numberOfDerivationSteps = 0L;
      this.numberOfRemainingUnassigned = 0L;
      this.numberOfRemainingAssigned = 0L;
   }

   @Override
   public void printStat(PrintWriter out, String prefix) {
      super.printStat(out, prefix);
      out.println(prefix + "number of reductions to clauses (during analyze)\t: " + this.getNumberOfReductions());
      out.println(prefix + "number of learned constraints concerned by reduction\t: " + this.numberOfLearnedConstraintsReduced);
      out.println(prefix + "number of learning phase by resolution\t: " + this.numberOfResolution);
      out.println(prefix + "number of learning phase by cutting planes\t: " + this.numberOfCP);
      out.println(prefix + "number of rounding to 1 operations\t: " + this.numberOfRoundingOperations);
      out.println(prefix + "number of easy rounding to 1 operations (no literal elimination needed)\t: " + this.numberOfEasyRoundingOperations);
      out.println(prefix + "number of reductions of the coefficients by power 2 \t: " + this.getNumberOfReductionsByPower2());
      out.println(prefix + "number of right shift for reduction by power 2 \t: " + this.numberOfRightShiftsForCoeffs);
      out.println(prefix + "number of reductions of the coefficients by GCD over coefficients \t: " + this.numberOfReductionsByGCD);
      out.println(prefix + "number of ending skipping \t: " + this.numberOfEndingSkipping);
      out.println(prefix + "number of internal skipping \t: " + this.numberOfInternalSkipping);
      out.println(prefix + "number of derivation steps \t: " + this.getNumberOfDerivationSteps());
      out.println(prefix + "number of skipped derivation steps \t: " + (this.numberOfInternalSkipping + this.numberOfEndingSkipping));
      out.println(prefix + "number of remaining unassigned \t: " + this.numberOfRemainingUnassigned);
      out.println(prefix + "number of remaining assigned \t: " + this.numberOfRemainingAssigned);
      out.println(prefix + "number of falsified literals weakened from reason\t: " + this.falsifiedLiteralsRemovedFromReason);
      out.println(prefix + "number of falsified literals weakened from conflict\t: " + this.falsifiedLiteralsRemovedFromConflict);
      out.println(prefix + "time for arithmetic operations\t: " + this.timeForArtithmeticOperations);
      out.println(prefix + "minimum degree of deleted constraints\t: " + this.minRemoved);
      out.println(prefix + "maximum degree of deleted constraints\t: " + this.maxRemoved);
      out.println(prefix + "number of deleted constraints\t: " + this.nbRemoved);
   }

   public long getNumberOfReductions() {
      return this.numberOfReductions;
   }

   public void incNumberOfReductions(long increment) {
      this.numberOfReductions += increment;
   }

   public long getNumberOfReductionsByPower2() {
      return this.numberOfReductionsByPower2;
   }

   public void incNumberOfReductionsByPower2() {
      this.numberOfReductionsByPower2++;
   }

   public long getNumberOfRightShiftsForCoeffs() {
      return this.numberOfRightShiftsForCoeffs;
   }

   public void incNumberOfRightShiftsForCoeffs(int increment) {
      this.numberOfRightShiftsForCoeffs += increment;
   }

   public long getNumberOfReductionsByGCD() {
      return this.numberOfReductionsByGCD;
   }

   public void incNumberOfReductionsByGCD() {
      this.numberOfReductionsByGCD++;
   }

   public long getNumberOfLearnedConstraintsReduced() {
      return this.numberOfLearnedConstraintsReduced;
   }

   public void incNumberOfLearnedConstraintsReduced() {
      this.numberOfLearnedConstraintsReduced++;
   }

   public long getNumberOfResolution() {
      return this.numberOfResolution;
   }

   public void incNumberOfResolution() {
      this.numberOfResolution++;
   }

   public long getNumberOfCP() {
      return this.numberOfCP;
   }

   public void incNumberOfCP() {
      this.numberOfCP++;
   }

   public long getNumberOfRoundingOperations() {
      return this.numberOfRoundingOperations;
   }

   public void incNumberOfRoundingOperations() {
      this.numberOfRoundingOperations++;
   }

   public long getNumberOfEasyRoundingOperations() {
      return this.numberOfEasyRoundingOperations;
   }

   public void incNumberOfEasyRoundingOperations() {
      this.numberOfEasyRoundingOperations++;
   }

   public long getNumberOfEndingSkipping() {
      return this.numberOfEndingSkipping;
   }

   public void incNumberOfEndingSkipping() {
      this.numberOfEndingSkipping++;
   }

   public long getNumberOfInternalSkipping() {
      return this.numberOfInternalSkipping;
   }

   public void incNumberOfInternalSkipping() {
      this.numberOfInternalSkipping++;
   }

   public long getNumberOfDerivationSteps() {
      return this.numberOfDerivationSteps;
   }

   public void incNumberOfDerivationSteps() {
      this.numberOfDerivationSteps++;
   }

   public long getNumberOfRemainingUnassigned() {
      return this.numberOfRemainingUnassigned;
   }

   public void incNumberOfRemainingUnassigned() {
      this.numberOfRemainingUnassigned++;
   }

   public long getNumberOfRemainingAssigned() {
      return this.numberOfRemainingAssigned;
   }

   public void incNumberOfRemainingAssigned() {
      this.numberOfRemainingAssigned++;
   }

   public void incFalsifiedLiteralsRemovedFromReason() {
      this.falsifiedLiteralsRemovedFromReason++;
   }

   public void incFalsifiedLiteralsRemovedFromConflict() {
      this.falsifiedLiteralsRemovedFromConflict++;
   }

   public void incTimeForArithmeticOperations(long time) {
      this.timeForArtithmeticOperations += time;
   }

   public void setMinRemoved(BigInteger minRemoved) {
      if (this.minRemoved == null) {
         this.minRemoved = minRemoved;
      } else {
         this.minRemoved = this.minRemoved.min(minRemoved);
      }
   }

   public void setMaxRemoved(BigInteger maxRemoved) {
      if (this.maxRemoved == null) {
         this.maxRemoved = maxRemoved;
      } else {
         this.maxRemoved = this.maxRemoved.max(maxRemoved);
      }
   }

   public void incNbRemoved() {
      this.nbRemoved++;
   }
}
