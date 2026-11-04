package net.caffeinemc.mods.lithium.mixin.profiler;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
   protected ServerLevelMixin(
      WritableLevelData properties,
      ResourceKey<Level> registryRef,
      RegistryAccess registryManager,
      Holder<DimensionType> dimensionEntry,
      Supplier<ProfilerFiller> profiler,
      boolean isClient,
      boolean debugWorld,
      long biomeAccess,
      int maxChainedNeighborUpdates
   ) {
      super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
   }

   @Shadow
   @NotNull
   public abstract MinecraftServer getServer();

   public ProfilerFiller getProfiler() {
      return this.getServer().getProfiler();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ChunkAccess getChunk(int param1, int nullx) {
      return super.getChunk(var1, nullx);
   }
}
