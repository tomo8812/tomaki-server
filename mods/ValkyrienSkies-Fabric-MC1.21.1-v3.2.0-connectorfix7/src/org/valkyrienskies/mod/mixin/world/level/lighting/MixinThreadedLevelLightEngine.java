package org.valkyrienskies.mod.mixin.world.level.lighting;

import java.util.function.IntSupplier;
import net.minecraft.class_1923;
import net.minecraft.class_3194;
import net.minecraft.class_3227;
import net.minecraft.class_3898;
import net.minecraft.class_8563;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;
import org.valkyrienskies.mod.mixin.accessors.server.level.ChunkMapAccessor;

@Mixin(class_3227.class)
public abstract class MixinThreadedLevelLightEngine {
   private static final int VS$FULL_CHUNK_LEVEL = class_8563.method_51828(class_3194.field_44855);

   @Redirect(
      method = "addTask(IILnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;getChunkQueueLevel(J)Ljava/util/function/IntSupplier;")
   )
   private IntSupplier vs$forceShipyardPriority(class_3898 instance, long chunkPosLong) {
      int x = class_1923.method_8325(chunkPosLong);
      int z = class_1923.method_8332(chunkPosLong);
      return VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(x, z)
         ? () -> VS$FULL_CHUNK_LEVEL
         : ((ChunkMapAccessor)instance).callGetChunkQueueLevel(chunkPosLong);
   }
}
