package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.util.StructureFaultReporter;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockAttachedEntity.class)
public abstract class BlockAttachedEntityMixin {
   @Redirect(
      method = "readAdditionalSaveData",
      at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false),
      require = 0
   )
   private void chunksmith$captureInvalidPosition(Logger logger, String message, Object storedPos) {
      try {
         StructureFaultReporter.get().recordBlockAttached(storedPos == null);
      } catch (Throwable var5) {
      }
   }
}
