package reloc.org.sat4j.pb.restarts;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.CircularBuffer;
import reloc.org.sat4j.minisat.core.RestartStrategy;
import reloc.org.sat4j.minisat.core.SearchParams;
import reloc.org.sat4j.minisat.core.SolverStats;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;
import reloc.org.sat4j.specs.Constr;

public class GrowingCoefficientRestarts implements RestartStrategy {
   private static final long serialVersionUID = 1L;
   private final BigIntegerCircularBuffer bufferDegrees = new BigIntegerCircularBuffer(50);
   private final CircularBuffer bufferTrail = new CircularBuffer(5000);
   private BigInteger sumOfAllDegrees = BigInteger.ZERO;
   private SolverStats stats;

   @Override
   public void reset() {
      this.sumOfAllDegrees = BigInteger.ZERO;
      this.bufferDegrees.clear();
      this.bufferTrail.clear();
   }

   @Override
   public void newConflict() {
   }

   @Override
   public void init(SearchParams params, SolverStats stats) {
      this.stats = stats;
      this.reset();
   }

   @Override
   public long nextRestartNumberOfConflict() {
      return 0L;
   }

   @Override
   public boolean shouldRestart() {
      return this.bufferDegrees.isFull()
         && this.bufferDegrees
               .average()
               .multiply(BigInteger.valueOf(this.stats.getConflicts()).shiftLeft(2))
               .compareTo(this.sumOfAllDegrees.multiply(BigInteger.valueOf(5L)))
            > 0;
   }

   @Override
   public void onRestart() {
      this.bufferDegrees.clear();
   }

   @Override
   public void onBackjumpToRootLevel() {
   }

   @Override
   public void newLearnedClause(Constr learned, int trailLevel) {
      BigInteger degree;
      if (learned instanceof PBConstr) {
         degree = ((PBConstr)learned).getDegree();
      } else {
         degree = BigInteger.ONE;
      }

      this.bufferDegrees.push(degree);
      this.sumOfAllDegrees = this.sumOfAllDegrees.add(degree);
      this.bufferTrail.push(trailLevel);
      if (this.stats.getConflicts() > 10000L && this.bufferTrail.isFull() && trailLevel * 5L > 7L * this.bufferTrail.average()) {
         this.bufferDegrees.clear();
      }
   }

   @Override
   public String toString() {
      return "Dynamic restart strategy considering the size of the coefficients";
   }
}
