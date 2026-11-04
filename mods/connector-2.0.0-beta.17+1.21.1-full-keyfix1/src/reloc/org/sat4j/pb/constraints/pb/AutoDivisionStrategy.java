package reloc.org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

public enum AutoDivisionStrategy {
   DISABLED {
      @Override
      boolean isCardinality(InternalMapPBStructure weightedLits) {
         return false;
      }
   },
   ENABLED {
      @Override
      boolean isCardinality(InternalMapPBStructure weightedLits) {
         BigInteger value = weightedLits.getCoef(0);

         for (int i = 1; i < weightedLits.size(); i++) {
            if (!weightedLits.getCoef(i).equals(value)) {
               return false;
            }
         }

         return true;
      }
   };

   AutoDivisionStrategy() {
   }

   abstract boolean isCardinality(InternalMapPBStructure var1);

   @Override
   public String toString() {
      return "Auto-division on coefficients is " + this.name().toLowerCase();
   }
}
