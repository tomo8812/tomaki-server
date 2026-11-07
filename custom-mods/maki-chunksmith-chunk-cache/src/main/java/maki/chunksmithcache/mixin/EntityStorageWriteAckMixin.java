package maki.chunksmithcache.mixin;

import java.util.concurrent.CompletableFuture;
import maki.chunksmithcache.PromotionLedger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityStorage.class)
public abstract class EntityStorageWriteAckMixin {
    @Shadow @Final private ServerLevel level;

    @Redirect(method = "storeEntities(Lnet/minecraft/world/level/entity/ChunkEntities;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/SimpleRegionStorage;write(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/concurrent/CompletableFuture;"), require = 2)
    private CompletableFuture<Void> maki$observeEntityWrite(SimpleRegionStorage storage, ChunkPos pos, CompoundTag data) {
        CompletableFuture<Void> written = storage.write(pos, data);
        PromotionLedger.observeEntityWrite(level, pos, written);
        return written;
    }
}
