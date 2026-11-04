package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import net.minecraft.network.chat.Component;

public enum HeaderType implements ConfigEnum {
   VISIBLE("visible"),
   INVISIBLE("invisible");

   private final String name;

   HeaderType(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Component getText() {
      return EmiPort.translatable("emi.header." + this.name);
   }

   // $VF: synthetic method
   private static HeaderType[] $values() {
      return new HeaderType[]{VISIBLE, INVISIBLE};
   }
}
