package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import net.minecraft.network.chat.Component;

public class ScreenAlign {
   public ScreenAlign.Horizontal horizontal;
   public ScreenAlign.Vertical vertical;

   public ScreenAlign(ScreenAlign.Horizontal horizontal, ScreenAlign.Vertical vertical) {
      this.horizontal = horizontal;
      this.vertical = vertical;
   }

   public enum Horizontal implements ConfigEnum {
      LEFT("left"),
      CENTER("center"),
      RIGHT("right");

      private final String name;

      Horizontal(String name) {
         this.name = name;
      }

      @Override
      public String getName() {
         return this.name;
      }

      @Override
      public Component getText() {
         return EmiPort.translatable("emi.align.horizontal." + this.name);
      }

      public static ScreenAlign.Horizontal fromName(String name) {
         for (ScreenAlign.Horizontal s : values()) {
            if (s.getName().equals(name)) {
               return s;
            }
         }

         return CENTER;
      }

      // $VF: synthetic method
      private static ScreenAlign.Horizontal[] $values() {
         return new ScreenAlign.Horizontal[]{LEFT, CENTER, RIGHT};
      }
   }

   public enum Vertical implements ConfigEnum {
      TOP("top"),
      CENTER("center"),
      BOTTOM("bottom");

      private final String name;

      Vertical(String name) {
         this.name = name;
      }

      @Override
      public String getName() {
         return this.name;
      }

      @Override
      public Component getText() {
         return EmiPort.translatable("emi.align.vertical." + this.name);
      }

      public static ScreenAlign.Vertical fromName(String name) {
         for (ScreenAlign.Vertical s : values()) {
            if (s.getName().equals(name)) {
               return s;
            }
         }

         return CENTER;
      }

      // $VF: synthetic method
      private static ScreenAlign.Vertical[] $values() {
         return new ScreenAlign.Vertical[]{TOP, CENTER, BOTTOM};
      }
   }
}
