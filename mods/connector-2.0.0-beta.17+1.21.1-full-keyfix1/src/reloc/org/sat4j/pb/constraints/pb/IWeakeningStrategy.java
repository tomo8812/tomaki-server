package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;
import reloc.org.sat4j.minisat.core.ILits;

public interface IWeakeningStrategy {
   IWeakeningStrategy UNASSIGNED_FIRST = new IWeakeningStrategy() {
      // $VF: synthetic field
      static final boolean $assertionsDisabled = !IWeakeningStrategy.class.desiredAssertionStatus();

      @Override
      public int findLiteralToRemove(ILits voc, IWatchPb wpb, BigInteger[] coefsBis, int indLitImplied, BigInteger degreeBis) {
         int lit = -1;
         int size = wpb.size();

         for (int ind = 0; ind < size && lit == -1; ind++) {
            if (coefsBis[ind].signum() != 0 && voc.isUnassigned(wpb.get(ind))) {
               assert coefsBis[ind].compareTo(degreeBis) < 0;
               lit = ind;
            }
         }

         if (lit == -1) {
            for (int ind = 0; ind < size && lit == -1; ind++) {
               if (coefsBis[ind].signum() != 0 && voc.isSatisfied(wpb.get(ind)) && ind != indLitImplied) {
                  lit = ind;
               }
            }
         }

         return lit;
      }

      @Override
      public String toString() {
         return "Weaken first using unassigned literals";
      }
   };
   IWeakeningStrategy SATISFIED_FIRST = new IWeakeningStrategy() {
      // $VF: synthetic field
      static final boolean $assertionsDisabled = !IWeakeningStrategy.class.desiredAssertionStatus();

      @Override
      public int findLiteralToRemove(ILits voc, IWatchPb wpb, BigInteger[] coefsBis, int indLitImplied, BigInteger degreeBis) {
         int lit = -1;
         int size = wpb.size();

         for (int ind = 0; ind < size && lit == -1; ind++) {
            if (coefsBis[ind].signum() != 0 && voc.isSatisfied(wpb.get(ind))) {
               assert coefsBis[ind].compareTo(degreeBis) < 0;
               lit = ind;
            }
         }

         if (lit == -1) {
            for (int ind = 0; ind < size && lit == -1; ind++) {
               if (coefsBis[ind].signum() != 0 && voc.isUnassigned(wpb.get(ind)) && ind != indLitImplied) {
                  lit = ind;
               }
            }
         }

         return lit;
      }

      @Override
      public String toString() {
         return "Weaken first using satisfied literals";
      }
   };
   IWeakeningStrategy ANY = new IWeakeningStrategy() {
      // $VF: synthetic field
      static final boolean $assertionsDisabled = !IWeakeningStrategy.class.desiredAssertionStatus();

      @Override
      public int findLiteralToRemove(ILits voc, IWatchPb wpb, BigInteger[] coefsBis, int indLitImplied, BigInteger degreeBis) {
         int lit = -1;
         int size = wpb.size();

         for (int ind = 0; ind < size && lit == -1; ind++) {
            if (coefsBis[ind].signum() != 0 && !voc.isFalsified(wpb.get(ind)) && ind != indLitImplied) {
               assert coefsBis[ind].compareTo(degreeBis) < 0;
               lit = ind;
            }
         }

         return lit;
      }

      @Override
      public String toString() {
         return "Weaken with no priority";
      }
   };

   int findLiteralToRemove(ILits var1, IWatchPb var2, BigInteger[] var3, int var4, BigInteger var5);
}
