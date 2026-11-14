package dev.diorama.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapInvoker {
   @Invoker("upgradeChunkTag")
   CompoundTag diorama$upgrade(CompoundTag var1);
}
