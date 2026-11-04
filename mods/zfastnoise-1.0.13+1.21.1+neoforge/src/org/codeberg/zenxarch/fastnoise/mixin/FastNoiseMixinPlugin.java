package org.codeberg.zenxarch.fastnoise.mixin;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Set;
import org.codeberg.zenxarch.fastnoise.FastNoiseConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class FastNoiseMixinPlugin implements IMixinConfigPlugin {
   private Object2BooleanMap<String> config = FastNoiseConfig.loadConfig();
   private String mixinPackage;

   public void onLoad(String mixinPackage) {
      this.mixinPackage = mixinPackage;
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (!mixinClassName.startsWith(this.mixinPackage)) {
         return false;
      }

      String name = mixinClassName.substring(this.mixinPackage.length() - "mixin".length());
      if (!name.startsWith("mixin.")) {
         return false;
      }

      ObjectIterator var4 = this.config.keySet().iterator();

      while (var4.hasNext()) {
         String key = (String)var4.next();
         if (name.startsWith(key) && !this.config.getBoolean(key)) {
            return false;
         }
      }

      return true;
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
