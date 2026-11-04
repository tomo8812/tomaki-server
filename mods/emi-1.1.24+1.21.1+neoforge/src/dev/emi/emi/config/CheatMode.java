package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import net.minecraft.network.chat.Component;

public enum CheatMode implements ConfigEnum {
   TRUE("true"),
   FALSE("false"),
   CREATIVE("creative");

   private final String name;

   CheatMode(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Component getText() {
      return EmiPort.translatable("emi.cheat_mode." + this.name.replace("-", "_"));
   }

   // $VF: synthetic method
   private static CheatMode[] $values() {
      return new CheatMode[]{TRUE, FALSE, CREATIVE};
   }
}
