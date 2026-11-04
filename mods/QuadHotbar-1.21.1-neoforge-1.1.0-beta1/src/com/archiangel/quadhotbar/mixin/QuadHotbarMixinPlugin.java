package com.archiangel.quadhotbar.mixin;

import java.util.List;
import java.util.Set;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class QuadHotbarMixinPlugin implements IMixinConfigPlugin {
   public boolean shouldApplyMixin(String target, String mixin) {
      if (mixin.endsWith("CorpseTransferMixin")) {
         return FMLLoader.getLoadingModList().getMods().stream().anyMatch(mod -> mod.getModId().equals("corpse"));
      } else if (mixin.endsWith("GraveStoneRecoveryMixin")) {
         return FMLLoader.getLoadingModList().getMods().stream().anyMatch(mod -> mod.getModId().equals("gravestone"));
      } else if (mixin.endsWith("DeathInventoryMixin")) {
         String id = target.startsWith("de.maxhenkel.corpse.") ? "corpse" : "gravestone";
         return FMLLoader.getLoadingModList().getMods().stream().anyMatch(mod -> mod.getModId().equals(id));
      } else {
         return true;
      }
   }

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public void acceptTargets(Set<String> own, Set<String> other) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String name, ClassNode target, String mixin, IMixinInfo info) {
   }

   public void postApply(String name, ClassNode target, String mixin, IMixinInfo info) {
   }
}
