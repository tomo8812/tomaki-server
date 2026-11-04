package org.valkyrienskies.mod.mixin.server.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_3193;
import net.minecraft.class_3215;
import net.minecraft.class_3218;
import net.minecraft.class_4076;
import net.minecraft.class_4153;
import net.minecraft.class_9259;
import net.minecraft.class_2338.class_2339;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.Wing;
import org.valkyrienskies.core.api.ships.WingManager;
import org.valkyrienskies.core.api.world.ShipWorld;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.internal.world.VsiServerShipWorld;
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.block.WingBlock;
import org.valkyrienskies.mod.common.config.DimensionParametersResolver;
import org.valkyrienskies.mod.common.util.DragInfoReporter;
import org.valkyrienskies.mod.common.util.VSServerLevel;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixin.accessors.server.level.ChunkMapAccessor;
import org.valkyrienskies.mod.mixin.accessors.server.level.DistanceManagerAccessor;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;
import org.valkyrienskies.mod.mixinducks.world.level.NoVSLevelDuck;
import org.valkyrienskies.mod.util.McMathUtilKt;

@Mixin(class_3218.class)
public abstract class MixinServerLevel implements IShipObjectWorldServerProvider, VSServerLevel {
   @Shadow
   @Final
   private class_3215 field_24624;
   @Unique
   private static final boolean VS$TRACE_SCHEDULED_TICKS = Boolean.getBoolean("vs.traceScheduledTicks");
   @Unique
   private static final Logger VS$TICK_TRACE_LOG = LoggerFactory.getLogger("VS2-TickTrace");
   @Unique
   private final Map<class_1923, List<Vector3ic>> vs$knownChunks = new HashMap<>();
   @Unique
   private final Long2LongOpenHashMap vs$chunksToUnload = new Long2LongOpenHashMap();
   @Unique
   private static final long VS$CHUNK_UNLOAD_THRESHOLD = 100L;
   @Unique
   private final LongOpenHashSet vs$pendingForcedChunks = new LongOpenHashSet();

   @Shadow
   @NotNull
   public abstract MinecraftServer method_8503();

   @Shadow
   public abstract int method_19498(class_4076 var1);

   @Inject(method = "shouldTickBlocksAt(J)Z", at = @At("HEAD"), cancellable = true)
   private void vs$allowShipyardBlockTicking(long packedChunkPos, CallbackInfoReturnable<Boolean> cir) {
      int chunkX = class_1923.method_8325(packedChunkPos);
      int chunkZ = class_1923.method_8332(packedChunkPos);
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkX, chunkZ)) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "isPositionTickingWithEntitiesLoaded", at = @At("HEAD"), cancellable = true)
   private void vs$allowShipyardPositionTicking(long packedPos, CallbackInfoReturnable<Boolean> cir) {
      int chunkX = class_1923.method_8325(packedPos);
      int chunkZ = class_1923.method_8332(packedPos);
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkX, chunkZ)) {
         cir.setReturnValue(true);
      }
   }

   @Nullable
   @Override
   public VsiServerShipWorld getShipObjectWorld() {
      return ((IShipObjectWorldServerProvider)this.method_8503()).getShipObjectWorld();
   }

   @Inject(method = "<init>", at = @At("RETURN"))
   void onInit(CallbackInfo ci) {
      if (!(this instanceof NoVSLevelDuck)) {
         if (this.getShipObjectWorld() != null) {
            DimensionParametersResolver.Parameters params = DimensionParametersResolver.INSTANCE
               .getDimensionMap()
               .get(VSGameUtilsKt.getDimensionId((class_3218)this));
            if (params != null) {
               this.getShipObjectWorld()
                  .addDimension(
                     VSGameUtilsKt.getDimensionId((class_3218)this),
                     VSGameUtilsKt.getYRange((class_3218)this),
                     params.getGravity(),
                     params.getSeaLevel(),
                     params.getMaxY()
                  );
               return;
            }

            this.getShipObjectWorld()
               .addDimension(
                  VSGameUtilsKt.getDimensionId((class_3218)this),
                  VSGameUtilsKt.getYRange((class_3218)this),
                  McMathUtilKt.getDEFAULT_WORLD_GRAVITY(),
                  62.0,
                  962.0
               );
         }
      }
   }

   @Inject(method = "getPoiManager", at = @At("HEAD"))
   void onGetPoiManager(CallbackInfoReturnable<class_4153> cir) {
      if (this.field_24624.method_19493() instanceof OfLevel levelProvider) {
         levelProvider.setLevel((class_3218)this);
      }
   }

   @Inject(method = "isCloseToVillage", at = @At("HEAD"), cancellable = true)
   void preIsCloseToVillage(class_2338 blockPos, int i, CallbackInfoReturnable<Boolean> cir) {
      if (i <= 6) {
         boolean[] found = new boolean[]{false};
         VSGameUtilsKt.transformToNearbyShipsAndWorld(
            (class_1937)class_3218.class.cast(this),
            blockPos.method_10263(),
            blockPos.method_10264(),
            blockPos.method_10260(),
            i * 100.0,
            (x, y, z) -> found[0] = found[0] || this.method_19498(class_4076.method_18682(class_2338.method_49637(x, y, z))) <= i
         );
         if (found[0]) {
            cir.setReturnValue(true);
         }
      }
   }

   @WrapOperation(
      method = "sendParticles(Lnet/minecraft/server/level/ServerPlayer;ZDDDLnet/minecraft/network/protocol/Packet;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z")
   )
   private boolean includeShipsInParticleDistanceCheck(class_2338 player, class_2374 particle, double distance, Operation<Boolean> closerToCenterThan) {
      class_3218 self = class_3218.class.cast(this);
      LoadedServerShip ship = VSGameUtilsKt.getLoadedShipManagingPos(self, (int)particle.method_10216() >> 4, (int)particle.method_10215() >> 4);
      if (ship == null) {
         return (Boolean)closerToCenterThan.call(new Object[]{player, particle, distance});
      }

      Vector3d posInWorld = ship.getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(particle));
      return posInWorld.distanceSquared(player.method_10263(), player.method_10264(), player.method_10260()) < distance * distance;
   }

   @Unique
   private void vs$loadChunk(@NotNull class_2791 worldChunk, List<VsiTerrainUpdate> voxelShapeUpdates) {
      this.vs$chunksToUnload.remove(worldChunk.method_12004().method_8324());
      if (!this.vs$knownChunks.containsKey(worldChunk.method_12004())) {
         if (worldChunk instanceof class_2818 levelChunk) {
            int cx = worldChunk.method_12004().field_9181;
            int cz = worldChunk.method_12004().field_9180;
            if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(cx, cz)) {
               class_3218 self = class_3218.class.cast(this);
               levelChunk.method_39305(self);
               self.method_39223(levelChunk);
               if (VS$TRACE_SCHEDULED_TICKS) {
                  VS$TICK_TRACE_LOG.info(
                     "vs$loadChunk registered tick container for shipyard chunk=({},{}) gameTime={}", new Object[]{cx, cz, self.method_8510()}
                  );
               }
            }
         }

         List<Vector3ic> voxelChunkPositions = new ArrayList<>();
         int chunkX = worldChunk.method_12004().field_9181;
         int chunkZ = worldChunk.method_12004().field_9180;
         class_2826[] chunkSections = worldChunk.method_12006();

         for (int sectionY = 0; sectionY < chunkSections.length; sectionY++) {
            class_2826 chunkSection = chunkSections[sectionY];
            Vector3ic chunkPos = new Vector3i(chunkX, worldChunk.method_31604(sectionY), chunkZ);
            voxelChunkPositions.add(chunkPos);
            if (chunkSection != null && !chunkSection.method_38292()) {
               VsiTerrainUpdate voxelShapeUpdate = VSGameUtilsKt.toDenseVoxelUpdate(chunkSection, chunkPos);
               voxelShapeUpdates.add(voxelShapeUpdate);
               class_3218 thisAsLevel = class_3218.class.cast(this);
               LoadedServerShip ship = VSGameUtilsKt.getLoadedShipManagingPos(thisAsLevel, chunkX, chunkZ);
               if (ship != null) {
                  WingManager shipAsWingManager = ship.getWingManager();
                  class_2339 mutableBlockPos = new class_2339();

                  for (int x = 0; x < 16; x++) {
                     for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                           class_2680 blockState = chunkSection.method_12254(x, y, z);
                           int posX = (chunkX << 4) + x;
                           int posY = worldChunk.method_31607() + (sectionY << 4) + y;
                           int posZ = (chunkZ << 4) + z;
                           if (blockState.method_26204() instanceof WingBlock) {
                              mutableBlockPos.method_10103(posX, posY, posZ);
                              Wing wing = ((WingBlock)blockState.method_26204()).getWing(thisAsLevel, mutableBlockPos, blockState);
                              if (wing != null) {
                                 shipAsWingManager.setWing(shipAsWingManager.getFirstWingGroupId(), posX, posY, posZ, wing);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               VsiTerrainUpdate emptyVoxelShapeUpdate = ValkyrienSkiesMod.getVsCore().newEmptyVoxelShapeUpdate(chunkPos.x(), chunkPos.y(), chunkPos.z(), true);
               voxelShapeUpdates.add(emptyVoxelShapeUpdate);
            }
         }

         this.vs$knownChunks.put(worldChunk.method_12004(), voxelChunkPositions);
      }
   }

   @Inject(method = "unload", at = @At("HEAD"), cancellable = true)
   private void vs$keepActiveShipChunksLoaded(class_2818 chunk, CallbackInfo ci) {
      class_1923 pos = chunk.method_12004();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(pos.field_9181, pos.field_9180)) {
         class_3218 self = class_3218.class.cast(this);
         if (VSGameUtilsKt.getShipManagingPos(self, pos.field_9181, pos.field_9180) != null) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "tick", at = @At("TAIL"))
   private void postTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
      class_3218 self = class_3218.class.cast(this);
      VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(self);
      ChunkMapAccessor chunkMapAccessor = (ChunkMapAccessor)this.field_24624.field_17254;
      List<VsiTerrainUpdate> voxelShapeUpdates = new ArrayList<>();
      DistanceManagerAccessor distanceManagerAccessor = (DistanceManagerAccessor)this.field_24624.field_17254.method_17263();

      for (class_3193 chunkHolder : chunkMapAccessor.callGetChunks()) {
         class_9259<class_2818> worldChunkResult = chunkHolder.method_16145().getNow(class_3193.field_16427);
         if (!this.vs$knownChunks.containsKey(chunkHolder.method_60473())
            && distanceManagerAccessor.getTickets().containsKey(chunkHolder.method_60473().method_8324())) {
            class_2818[] loadedChunk = new class_2818[1];
            worldChunkResult.method_57126(chunk -> loadedChunk[0] = chunk);
            if (loadedChunk[0] != null) {
               this.vs$loadChunk(loadedChunk[0], voxelShapeUpdates);
            } else {
               class_1923 cp = chunkHolder.method_60473();
               if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(cp.field_9181, cp.field_9180)) {
                  class_2818 cachedChunk = this.field_24624.method_21730(cp.field_9181, cp.field_9180);
                  if (cachedChunk != null) {
                     this.vs$loadChunk(cachedChunk, voxelShapeUpdates);
                  }
               }
            }
         }
      }

      Iterator<Entry<class_1923, List<Vector3ic>>> knownChunkPosIterator = this.vs$knownChunks.entrySet().iterator();

      while (knownChunkPosIterator.hasNext()) {
         Entry<class_1923, List<Vector3ic>> knownChunkPosEntry = knownChunkPosIterator.next();
         long chunkPos = knownChunkPosEntry.getKey().method_8324();
         if (!distanceManagerAccessor.getTickets().containsKey(chunkPos) || chunkMapAccessor.callGetVisibleChunkIfPresent(chunkPos) == null) {
            long ticksWaitingToUnload = this.vs$chunksToUnload.getOrDefault(chunkPos, 0L);
            if (ticksWaitingToUnload > 100L) {
               for (Vector3ic unloadedChunk : knownChunkPosEntry.getValue()) {
                  VsiTerrainUpdate deleteVoxelShapeUpdate = ValkyrienSkiesMod.getVsCore()
                     .newDeleteTerrainUpdate(unloadedChunk.x(), unloadedChunk.y(), unloadedChunk.z());
                  voxelShapeUpdates.add(deleteVoxelShapeUpdate);
               }

               knownChunkPosIterator.remove();
               this.vs$chunksToUnload.remove(chunkPos);
            } else {
               this.vs$chunksToUnload.put(chunkPos, ticksWaitingToUnload + 1L);
            }
         }
      }

      shipObjectWorld.addTerrainUpdates(VSGameUtilsKt.getDimensionId(self), voxelShapeUpdates);
      if (VSCoreConfig.SERVER.getSp().getEnableSplitting()) {
         ValkyrienSkiesMod.splitHandler.tick(class_3218.class.cast(this));
      }

      DragInfoReporter.INSTANCE.tick((class_3218)this);
   }

   @Override
   public void removeChunk(int chunkX, int chunkZ) {
      class_1923 chunkPos = new class_1923(chunkX, chunkZ);
      this.vs$knownChunks.remove(chunkPos);
   }

   @Override
   public void addPendingForcedChunk(int chunkX, int chunkZ) {
      this.vs$pendingForcedChunks.add(class_1923.method_8331(chunkX, chunkZ));
   }

   @NotNull
   @Override
   public LongOpenHashSet getPendingForcedChunks() {
      return this.vs$pendingForcedChunks;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public ShipWorld getShipObjectWorld() {
      return this.getShipObjectWorld();
   }
}
