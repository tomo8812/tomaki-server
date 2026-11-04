package dev.terminalmc.clientsort.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Localization {
   private Localization() {
      throw new UnsupportedOperationException("This class cannot be instantiated.");
   }

   public static String translationKey(String path) {
      return "clientsort." + path;
   }

   public static String translationKey(String domain, String path) {
      return domain + ".clientsort." + path;
   }

   public static MutableComponent localized(String path, Object... args) {
      return Component.translatable(translationKey(path), args);
   }

   public static MutableComponent localized(String domain, String path, Object... args) {
      return Component.translatable(translationKey(domain, path), args);
   }
}
