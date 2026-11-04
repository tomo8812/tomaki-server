package net.caffeinemc.mods.lithium.mixin.entity.inactive_navigations;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.caffeinemc.mods.lithium.common.entity.NavigatingEntity;
import net.caffeinemc.mods.lithium.common.world.LithiumData;
import net.caffeinemc.mods.lithium.common.world.ServerWorldExtended;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements ServerWorldExtended {
   @Mutable
   @Shadow
   @Final
   Set<Mob> navigatingMobs;

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

   @Redirect(
      method = "sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V",
      at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;")
   )
   private Iterator<Mob> getActiveListeners(Set<Mob> set) {
      return Collections.emptyIterator();
   }

   @Inject(method = "<init>", at = @At("TAIL"))
   private void init(
      MinecraftServer server,
      Executor workerExecutor,
      LevelStorageAccess session,
      ServerLevelData properties,
      ResourceKey worldKey,
      LevelStem dimensionOptions,
      ChunkProgressListener worldGenerationProgressListener,
      boolean debugWorld,
      long seed,
      List spawners,
      boolean shouldTickTime,
      RandomSequences randomSequencesState,
      CallbackInfo ci
   ) {
      this.navigatingMobs = new ReferenceOpenHashSet(this.navigatingMobs);
   }

   @Override
   public void lithium$setNavigationActive(Mob mobEntity) {
      ReferenceOpenHashSet<PathNavigation> activeNavigations = ((LithiumData)this).lithium$getData().activeNavigations();
      activeNavigations.add(((NavigatingEntity)mobEntity).lithium$getRegisteredNavigation());
   }

   @Override
   public void lithium$setNavigationInactive(Mob mobEntity) {
      ReferenceOpenHashSet<PathNavigation> activeNavigations = ((LithiumData)this).lithium$getData().activeNavigations();
      activeNavigations.remove(((NavigatingEntity)mobEntity).lithium$getRegisteredNavigation());
   }

   @Inject(
      method = "sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V",
      at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"),
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void updateActiveListeners(
      BlockPos pos, BlockState oldState, BlockState newState, int arg3, CallbackInfo ci, VoxelShape string, VoxelShape voxelShape, List<PathNavigation> list
   ) {
      ReferenceOpenHashSet<PathNavigation> activeNavigations = ((LithiumData)this).lithium$getData().activeNavigations();
      ObjectIterator var10 = activeNavigations.iterator();

      while (var10.hasNext()) {
         PathNavigation entityNavigation = (PathNavigation)var10.next();
         if (entityNavigation.shouldRecomputePath(pos)) {
            list.add(entityNavigation);
         }
      }
   }

   @Unique
   public boolean areEntityNavigationsConsistent() {
      ReferenceOpenHashSet<PathNavigation> activeNavigations = ((LithiumData)this).lithium$getData().activeNavigations();
      int i = 0;

      for (Mob mobEntity : this.navigatingMobs) {
         PathNavigation entityNavigation = mobEntity.getNavigation();
         if ((entityNavigation.getPath() != null && ((NavigatingEntity)mobEntity).lithium$isRegisteredToWorld())
            != activeNavigations.contains(entityNavigation)) {
            return false;
         }

         if (entityNavigation.getPath() != null) {
            i++;
         }
      }

      return activeNavigations.size() == i;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ChunkAccess getChunk(int param1, int nullx) {
      return super.getChunk(var1, nullx);
   }
}
