package org.sinytra.adapter.patch;

public enum TxPhase {
   EARLY,
   LOADED,
   VALIDATED;

   // $VF: synthetic method
   private static TxPhase[] $values() {
      return new TxPhase[]{EARLY, LOADED, VALIDATED};
   }
}
