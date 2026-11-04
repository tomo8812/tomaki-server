package org.embeddedt.modernfix.annotation;

public enum FeatureLevel {
   GA,
   BETA;

   public boolean isAtLeast(FeatureLevel required) {
      return this.ordinal() >= required.ordinal();
   }

   // $VF: synthetic method
   private static FeatureLevel[] $values() {
      return new FeatureLevel[]{GA, BETA};
   }
}
