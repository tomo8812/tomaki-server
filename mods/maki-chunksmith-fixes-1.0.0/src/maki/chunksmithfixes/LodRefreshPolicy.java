package maki.chunksmithfixes;

public final class LodRefreshPolicy {
   private LodRefreshPolicy() {
   }

   public static boolean shouldTake(boolean settingOn, boolean lodOn, boolean full, boolean dirty, boolean stored) {
      return settingOn && lodOn && full && !stored;
   }

   public static boolean shouldTakeOnUnload(boolean dirty, boolean stored) {
      return dirty || !stored;
   }
}
