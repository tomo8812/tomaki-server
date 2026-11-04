package com.kishku7.chunksmith.platform;

public final class ServerEnvironment {
   private static volatile boolean dedicated = true;

   private ServerEnvironment() {
   }

   public static void setDedicated(boolean isDedicated) {
      dedicated = isDedicated;
   }

   public static boolean isDedicated() {
      return dedicated;
   }
}
