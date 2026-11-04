package org.millenaire.content.legacy;

public enum ConversionMode {
   AUTO,
   CONVERT;

   public boolean isStrict() {
      return this == CONVERT;
   }

   // $VF: synthetic method
   private static ConversionMode[] $values() {
      return new ConversionMode[]{AUTO, CONVERT};
   }
}
