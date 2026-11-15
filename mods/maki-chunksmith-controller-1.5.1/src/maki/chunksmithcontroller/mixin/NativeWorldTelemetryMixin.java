package maki.chunksmithcontroller.mixin;

import com.kishku7.chunksmith.ducks.MinecraftServerExtension;
import com.kishku7.chunksmith.platform.NeoForgeWorld;
import java.util.concurrent.Executor;
import maki.chunksmithcontroller.NativeLodTelemetry;
import maki.chunksmithcontroller.RequestContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.kishku7.chunksmith.platform.NeoForgeWorld", remap = false)
public abstract class NativeWorldTelemetryMixin {
   @Shadow
   @Final
   private ServerLevel world;

   @Redirect(
      method = "lambda$getChunkAtAsync$8",
      at = @At(
         value = "INVOKE",
         target = "Lcom/kishku7/chunksmith/lod/LodSupport;offer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
      ),
      require = 1
   )
   private void maki$offer(ServerLevel level, LevelChunk chunk) {
      NativeLodTelemetry.offer(level, chunk);
   }

   @Redirect(
      method = "getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;",
      at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/platform/NeoForgeWorld;ticketSafePoint()Ljava/util/concurrent/Executor;"),
      require = 2
   )
   private Executor maki$context(NeoForgeWorld ignored) {
      return RequestContext.wrap(action -> ((MinecraftServerExtension)this.world.getServer()).chunksmith$atTicketSafePoint(action));
   }
}
