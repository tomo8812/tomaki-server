package dev.terminalmc.clientsort.client.config;

public enum Policy {
   KEYBIND_BUTTON(true, true),
   KEYBIND(true, false),
   NONE(false, false);

   public final boolean keybind;
   public final boolean button;

   Policy(boolean keybind, boolean button) {
      this.keybind = keybind;
      this.button = button;
   }

   public String toSimpleString() {
      return switch (this) {
         case KEYBIND_BUTTON -> "2";
         case KEYBIND -> "1";
         case NONE -> "0";
      };
   }

   public static Policy fromSimpleString(String str) {
      return switch (str) {
         case "2" -> KEYBIND_BUTTON;
         case "1" -> KEYBIND;
         case "0" -> NONE;
         default -> throw new IllegalArgumentException();
      };
   }

   // $VF: synthetic method
   private static Policy[] $values() {
      return new Policy[]{KEYBIND_BUTTON, KEYBIND, NONE};
   }
}
