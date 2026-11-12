package maki.chunksmithcache.mixin;

import maki.chunksmithcache.CacheManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityStorage.class)
public abstract class EntityStorageMixin {
   @Shadow
   @Final
   private ServerLevel level;

   @Inject(method = "storeEntities(Lnet/minecraft/world/level/entity/ChunkEntities;)V", at = @At("HEAD"), cancellable = true)
   private void maki$temporaryEntityVeto(ChunkEntities<Entity> var1, CallbackInfo var2) {
      if (CacheManager.pregenPersistenceSuppressed(this.level, var1.getPos())) {
         var2.cancel();
      }
   }
}
