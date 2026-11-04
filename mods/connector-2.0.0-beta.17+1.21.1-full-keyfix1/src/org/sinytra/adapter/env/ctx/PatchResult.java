package org.sinytra.adapter.env.ctx;

public enum PatchResult {
   PASS,
   APPLY,
   COMPUTE_FRAMES;

   public PatchResult or(PatchResult other) {
      if (this == PASS && other != PASS) {
         return other;
      } else {
         return this == APPLY && other == COMPUTE_FRAMES ? COMPUTE_FRAMES : this;
      }
   }

   // $VF: synthetic method
   private static PatchResult[] $values() {
      return new PatchResult[]{PASS, APPLY, COMPUTE_FRAMES};
   }
}
