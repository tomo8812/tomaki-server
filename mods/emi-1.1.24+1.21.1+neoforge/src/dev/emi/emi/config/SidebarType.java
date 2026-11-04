package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum SidebarType implements ConfigEnum {
   NONE("none", 0, 0),
   INDEX("index", 0, 146),
   CRAFTABLES("craftables", 16, 146),
   FAVORITES("favorites", 32, 146),
   LOOKUP_HISTORY("lookup-history", 80, 146),
   CRAFT_HISTORY("craft-history", 64, 146),
   EMPTY("empty", 96, 146),
   CHESS("chess", 48, 146);

   private final String name;
   public final int u;
   public final int v;

   SidebarType(String name, int u, int v) {
      this.name = name;
      this.u = u;
      this.v = v;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Component getText() {
      return EmiPort.translatable("emi.sidebar.type." + this.name.replace("-", "_"));
   }

   public Component getDescription() {
      return EmiPort.translatable("emi.sidebar.type." + this.name.replace("-", "_") + ".description").withStyle(ChatFormatting.GRAY);
   }

   public static SidebarType fromName(String name) {
      for (SidebarType type : values()) {
         if (type.getName().equals(name)) {
            return type;
         }
      }

      return NONE;
   }

   // $VF: synthetic method
   private static SidebarType[] $values() {
      return new SidebarType[]{NONE, INDEX, CRAFTABLES, FAVORITES, LOOKUP_HISTORY, CRAFT_HISTORY, EMPTY, CHESS};
   }
}
