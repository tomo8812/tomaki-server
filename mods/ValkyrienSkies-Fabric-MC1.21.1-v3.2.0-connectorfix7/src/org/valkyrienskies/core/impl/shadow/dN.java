package org.valkyrienskies.core.impl.shadow;

public enum dN {
   SKIP("SkipWalk", "Skip only the walk method, but continue invoking the other listeners"),
   ABORT("Abort", "Aborts all the walk listeners and walk method itself"),
   CONTINUE("ContinueToWalk", "continue to invoke the walk method and other listeners");

   private String name;
   private String description;

   dN(String var3, String var4) {
      this.name = var3;
      this.description = var4;
   }

   public final String a() {
      return this.name;
   }

   public final String b() {
      return this.description;
   }
}
