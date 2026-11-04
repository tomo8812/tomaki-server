package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import net.minecraft.network.chat.Component;

public enum SidebarTheme implements ConfigEnum {
   TRANSPARENT("transparent", 0, 0),
   VANILLA("vanilla", 9, 9),
   MODERN("modern", 0, 0);

   private final String name;
   public final int horizontalPadding;
   public final int verticalPadding;

   SidebarTheme(String name, int horizontalPadding, int verticalPadding) {
      this.name = name;
      this.horizontalPadding = horizontalPadding;
      this.verticalPadding = verticalPadding;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Component getText() {
      return EmiPort.translatable("emi.sidebar.theme." + this.name);
   }

   // $VF: synthetic method
   private static SidebarTheme[] $values() {
      return new SidebarTheme[]{TRANSPARENT, VANILLA, MODERN};
   }
}
