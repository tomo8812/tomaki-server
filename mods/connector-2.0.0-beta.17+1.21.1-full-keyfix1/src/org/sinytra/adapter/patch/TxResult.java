package org.sinytra.adapter.patch;

public enum TxResult {
   SUCCESS,
   FINALIZE,
   PASS,
   FAIL;

   // $VF: synthetic method
   private static TxResult[] $values() {
      return new TxResult[]{SUCCESS, FINALIZE, PASS, FAIL};
   }
}
