package org.embeddedt.modernfix.common.mixin.perf.resourcepacks;

import java.nio.file.Path;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PathPackResources;
import org.embeddedt.modernfix.resources.ICachingResourcePack;
import org.embeddedt.modernfix.resources.PackResourcesCacheEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PathPackResources.class, priority = 1100)
public abstract class PathPackResourcesMixin implements ICachingResourcePack {
   @Shadow
   @Final
   private Path root;
   private PackResourcesCacheEngine cacheEngine;

   @Inject(method = "<init>", at = @At("TAIL"))
   private void cacheResources(PackLocationInfo location, Path root, CallbackInfo ci) {
      this.invalidateCache();
   }

   private PackResourcesCacheEngine generateResourceCache() {
      synchronized (this) {
         PackResourcesCacheEngine engine = this.cacheEngine;
         if (engine != null) {
            return engine;
         }

         this.cacheEngine = engine = new PackResourcesCacheEngine(type -> this.root.resolve(type.getDirectory()));
         return engine;
      }
   }

   @Override
   public void invalidateCache() {
      this.cacheEngine = null;
   }
}
