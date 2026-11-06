package maki.millenairelod.mixin;

import maki.millenairelod.Compat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.millenaire.Millenaire;
import org.millenaire.world.VillageSpawnQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Millenaire.class, remap = false)
public abstract class MillenaireMixin {
    @Inject(method = "lambda$new$32", at = @At("HEAD"), cancellable = true)
    private static void compat$chunk(VillageSpawnQueue queue, ChunkPos pos, boolean isNew,
                                     ServerLevel level, CallbackInfo ci) {
        Compat.onChunk(queue, pos, isNew, level);
        ci.cancel();
    }
}
