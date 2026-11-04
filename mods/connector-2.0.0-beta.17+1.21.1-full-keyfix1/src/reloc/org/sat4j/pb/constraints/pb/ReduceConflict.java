package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.core.VecInt;
import reloc.org.sat4j.specs.IVecInt;
import reloc.org.sat4j.specs.IteratorInt;

public class ReduceConflict implements IReduceConflictStrategy {
   private static final IReduceConflictStrategy INSTANCE = new ReduceConflict();

   private ReduceConflict() {
   }

   public static final IReduceConflictStrategy instance() {
      return INSTANCE;
   }

   @Override
   public void reduceConflict(ConflictMapDivideByPivot conflict, int literal) {
      BigInteger coef = conflict.weightedLits.get(literal);
      int size = conflict.weightedLits.size();
      IVecInt toRemove = new VecInt();

      for (int i = 0; i < size; i++) {
         int l = conflict.weightedLits.getLit(i);
         BigInteger coefLit = conflict.weightedLits.getCoef(i);
         if (conflict.voc.isFalsified(conflict.weightedLits.getLit(i))) {
            conflict.setCoef(l, ConflictMapDivideByPivot.ceildiv(conflict.weightedLits.getCoef(i), coef));
         } else {
            BigInteger[] tmp = conflict.divisionStrategy.divide(coefLit, coef);
            if (tmp[0].signum() == 0) {
               toRemove.push(l);
            } else {
               conflict.setCoef(l, tmp[0]);
            }

            conflict.degree = conflict.degree.subtract(tmp[1]);
         }
      }

      IteratorInt it = toRemove.iterator();

      while (it.hasNext()) {
         conflict.removeCoef(it.next());
      }

      conflict.degree = ConflictMapDivideByPivot.ceildiv(conflict.degree, coef);
      conflict.saturation();
      conflict.coefMultCons = BigInteger.ONE;
      conflict.stats.incNumberOfRoundingOperations();
   }
}
