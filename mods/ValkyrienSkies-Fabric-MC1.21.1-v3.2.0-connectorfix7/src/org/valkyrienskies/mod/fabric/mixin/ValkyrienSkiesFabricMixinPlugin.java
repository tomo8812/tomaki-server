package org.valkyrienskies.mod.fabric.mixin;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.valkyrienskies.mod.compat.LoadedMods;

public class ValkyrienSkiesFabricMixinPlugin implements IMixinConfigPlugin {
   private static boolean classExists(String className) {
      try {
         Class.forName(className, false, ValkyrienSkiesFabricMixinPlugin.class.getClassLoader());
         return true;
      } catch (ClassNotFoundException ex) {
         return false;
      }
   }

   public void onLoad(String s) {
   }

   public String getRefMapperConfig() {
      return "";
   }

   public boolean shouldApplyMixin(String s, String mixinClassName) {
      boolean isMixinBoosterLoaded = classExists("io.github.steelwoolmc.mixintransmog.MixinModlauncherRemapper");
      return mixinClassName.contains("org.valkyrienskies.mod.fabric.mixin.compat.old_create") ? LoadedMods.getOldCreate() : true;
   }

   public void acceptTargets(Set<String> set, Set<String> set1) {
   }

   public List<String> getMixins() {
      return List.of();
   }

   public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
   }

   public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
   }
}
