package de.maxhenkel.gravestone.corelib.config;

import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class ConfigBase {
   protected ModConfigSpec configSpec;

   public ConfigBase(Builder builder) {
   }

   public void onLoad(Loading evt) {
   }

   public void onReload(Reloading event) {
   }

   public void setConfigSpec(ModConfigSpec configSpec) {
      this.configSpec = configSpec;
   }

   public ModConfigSpec getConfigSpec() {
      return this.configSpec;
   }
}
