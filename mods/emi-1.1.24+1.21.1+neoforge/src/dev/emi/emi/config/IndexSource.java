package dev.emi.emi.config;

import dev.emi.emi.EmiPort;
import net.minecraft.network.chat.Component;

public enum IndexSource implements ConfigEnum {
   CREATIVE("creative"),
   REGISTERED("registered"),
   CREATIVE_PLUS_REGISTERED("creative-plus-registered");

   public final String name;

   IndexSource(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Component getText() {
      return EmiPort.translatable("emi.index_source." + this.name.replace("-", "_"));
   }

   // $VF: synthetic method
   private static IndexSource[] $values() {
      return new IndexSource[]{CREATIVE, REGISTERED, CREATIVE_PLUS_REGISTERED};
   }
}
