package com.kishku7.chunksmith.platform;

import java.util.Locale;

public enum LodMode {
   AUTO,
   ON,
   OFF;

   public static LodMode parse(String raw) {
      if (raw != null && !raw.trim().isEmpty()) {
         switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "auto":
               return AUTO;
            case "true":
            case "on":
            case "yes":
               return ON;
            case "false":
            case "off":
            case "no":
               return OFF;
            default:
               return null;
         }
      } else {
         return AUTO;
      }
   }

   // $VF: synthetic method
   private static LodMode[] $values() {
      return new LodMode[]{AUTO, ON, OFF};
   }
}
