package org.valkyrienskies.mod.mixin.client.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2540;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_3568;
import net.minecraft.class_4076;
import net.minecraft.class_631;
import net.minecraft.class_638;
import net.minecraft.class_631.class_3681;
import net.minecraft.class_6603.class_6605;
import net.minecraft.class_846.class_851;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ChunkClaim;
import org.valkyrienskies.core.internal.world.VsiClientShipWorld;
import org.valkyrienskies.core.internal.world.chunks.VsiTerrainUpdate;
import org.valkyrienskies.mod.common.BlockStateInfo;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.compat.SodiumCompat;
import org.valkyrienskies.mod.compat.VSRenderer;
import org.valkyrienskies.mod.mixin.ValkyrienCommonMixinConfigPlugin;
import org.valkyrienskies.mod.mixin.accessors.client.multiplayer.ClientLevelAccessor;
import org.valkyrienskies.mod.mixin.accessors.client.render.LevelRendererAccessor;
import org.valkyrienskies.mod.mixin.accessors.client.world.ClientChunkCacheStorageAccessor;
import org.valkyrienskies.mod.mixinducks.client.render.IVSViewAreaMethods;
import org.valkyrienskies.mod.mixinducks.client.world.ClientChunkCacheDuck;
import org.valkyrienskies.mod.mixinducks.mod_compat.vanilla_renderer.LevelRendererDuck;
import org.valkyrienskies.mod.util.ClientConnectivityUpdateQueue;

@Mixin(class_631.class)
public abstract class MixinClientChunkCache implements ClientChunkCacheDuck {
   @Shadow
   volatile class_3681 field_16246;
   @Shadow
   @Final
   class_638 field_16525;
   @Unique
   private final Long2ObjectMap<class_2818> emptyShipChunks = new Long2ObjectOpenHashMap();
   @Unique
   private final Long2ObjectMap<class_2818> vs$shipChunks = new Long2ObjectOpenHashMap();

   @Override
   public Long2ObjectMap<class_2818> vs$getShipChunks() {
      return this.vs$shipChunks;
   }

   @Override
   public void vs$removeShip(ClientShip ship) {
      ChunkClaim chunks = ship.getChunkClaim();

      for (int x = chunks.getXStart(); x <= chunks.getXEnd(); x++) {
         for (int z = chunks.getZStart(); z <= chunks.getZEnd(); z++) {
            this.removeShipChunk(x, z);
         }
      }
   }

   @Inject(method = "replaceWithPacketData", at = @At("HEAD"), cancellable = true)
   private void preLoadChunkFromPacket(int x, int z, class_2540 buf, class_2487 tag, Consumer<class_6605> consumer, CallbackInfoReturnable<class_2818> cir) {
      ClientChunkCacheStorageAccessor clientChunkMapAccessor = ClientChunkCacheStorageAccessor.class.cast(this.field_16246);
      if (!clientChunkMapAccessor.callInRange(x, z) && VSGameUtilsKt.isChunkInShipyard(this.field_16525, x, z)) {
         if (class_310.method_1551().field_1769 instanceof LevelRendererDuck levelRenderer) {
            levelRenderer.vs$setNeedsFrustumUpdate();
         }

         long chunkPosLong = class_1923.method_8331(x, z);
         class_1923 pos = new class_1923(x, z);
         class_2818 oldChunk = (class_2818)this.vs$shipChunks.get(chunkPosLong);
         class_2818 oldEmptyChunk = (class_2818)this.emptyShipChunks.remove(chunkPosLong);
         boolean shouldForce = false;
         class_2818 worldChunk;
         if (oldChunk != null) {
            worldChunk = oldChunk;
            oldChunk.method_12224(buf, tag, consumer);
         } else {
            worldChunk = oldEmptyChunk == null ? new class_2818(this.field_16525, pos) : oldEmptyChunk;
            worldChunk.method_12224(buf, tag, consumer);
            this.vs$shipChunks.put(chunkPosLong, worldChunk);
            clientChunkMapAccessor.setChunkCount(clientChunkMapAccessor.getChunkCount() + 1);
         }

         if (oldChunk != null) {
            shouldForce = true;
         }

         boolean shouldDefer = !BlockStateInfo.isSortedRegistryInitialized();
         if (shouldDefer) {
            ClientConnectivityUpdateQueue.queueChunkForInitialization(pos, shouldForce);
         } else {
            VsiClientShipWorld clientShipWorld = VSGameUtilsKt.getShipObjectWorld(this.field_16525);
            if (clientShipWorld != null && VSGameConfig.CLIENT.getConnectivity().getEnableClientConnectivity()) {
               class_2826[] chunkSections = worldChunk.method_12006();
               ArrayList<VsiTerrainUpdate> voxelShapeUpdates = new ArrayList<>(chunkSections.length);

               for (int i = 0; i < chunkSections.length; i++) {
                  class_2826 chunkSection = chunkSections[i];
                  int sectionY = worldChunk.method_31604(i);
                  voxelShapeUpdates.add(
                     chunkSection != null && !chunkSection.method_38292()
                        ? VSGameUtilsKt.toDenseVoxelUpdate(chunkSection, new Vector3i(pos.field_9181, sectionY, pos.field_9180))
                        : ValkyrienSkiesMod.getVsCore().newEmptyVoxelShapeUpdate(pos.field_9181, sectionY, pos.field_9180, true)
                  );
               }

               String dimensionId = ValkyrienSkiesMod.getApi().getDimensionId(this.field_16525);
               if (shouldForce) {
                  for (VsiTerrainUpdate update : voxelShapeUpdates) {
                     clientShipWorld.forceUpdateConnectivityChunk(dimensionId, update.getChunkX(), update.getChunkY(), update.getChunkZ(), update);
                  }
               } else {
                  clientShipWorld.addTerrainUpdates(dimensionId, voxelShapeUpdates);
               }
            }
         }

         this.relightChunk(worldChunk);

         while (this.field_16525.method_22336().method_15516() > 0) {
         }

         if (ValkyrienCommonMixinConfigPlugin.getVSRenderer() != VSRenderer.SODIUM) {
            IVSViewAreaMethods viewArea = (IVSViewAreaMethods)((LevelRendererAccessor)((ClientLevelAccessor)this.field_16525).getLevelRenderer()).getViewArea();

            for (int dx = -1; dx <= 1; dx++) {
               for (int dz = -1; dz <= 1; dz++) {
                  for (int sy = this.field_16525.method_32891(); sy < this.field_16525.method_31597(); sy++) {
                     class_851 renderSection = viewArea.vs$getShipRenderSection(x + dx, sy, z + dz);
                     if (renderSection != null) {
                        renderSection.method_3654(true);
                     }
                  }
               }
            }
         }

         this.field_16525.method_23782(pos);
         if (ValkyrienCommonMixinConfigPlugin.getVSRenderer() == VSRenderer.SODIUM) {
            SodiumCompat.onChunkAdded(this.field_16525, x, z);
         }

         cir.setReturnValue(worldChunk);
      }
   }

   @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
   public void preUnload(class_1923 chunkPos, CallbackInfo ci) {
      int chunkX = chunkPos.field_9181;
      int chunkZ = chunkPos.field_9180;
      if (VSGameUtilsKt.isChunkInShipyard(this.field_16525, chunkX, chunkZ)) {
         this.vs$shipChunks.remove(class_1923.method_8331(chunkX, chunkZ));
         if (ValkyrienCommonMixinConfigPlugin.getVSRenderer() != VSRenderer.SODIUM) {
            ((IVSViewAreaMethods)((LevelRendererAccessor)((ClientLevelAccessor)this.field_16525).getLevelRenderer()).getViewArea())
               .valkyrienskies$unloadChunk(chunkX, chunkZ);
         }

         SodiumCompat.onChunkRemoved(this.field_16525, chunkX, chunkZ);
         ci.cancel();
      }
   }

   @Inject(
      method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/LevelChunk;",
      at = @At("HEAD"),
      cancellable = true
   )
   public void preGetChunk(int chunkX, int chunkZ, class_2806 chunkStatus, boolean bl, CallbackInfoReturnable<class_2818> cir) {
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkX, chunkZ)) {
         class_2818 shipChunk = (class_2818)this.vs$shipChunks.get(class_1923.method_8331(chunkX, chunkZ));
         if (shipChunk != null) {
            cir.setReturnValue(shipChunk);
         } else {
            cir.setReturnValue(this.getOrCreateEmptyChunk(chunkX, chunkZ));
         }
      }
   }

   @Unique
   private void removeShipChunk(int chunkX, int chunkZ) {
      class_2818 chunk = (class_2818)this.vs$shipChunks.remove(class_1923.method_8331(chunkX, chunkZ));
      this.emptyShipChunks.remove(class_1923.method_8331(chunkX, chunkZ));
      if (chunk != null) {
         ClientChunkCacheStorageAccessor clientChunkMapAccessor = ClientChunkCacheStorageAccessor.class.cast(this.field_16246);
         clientChunkMapAccessor.setChunkCount(clientChunkMapAccessor.getChunkCount() - 1);
         this.field_16525.method_18110(chunk);
         if (ValkyrienCommonMixinConfigPlugin.getVSRenderer() != VSRenderer.SODIUM) {
            ((IVSViewAreaMethods)((LevelRendererAccessor)((ClientLevelAccessor)this.field_16525).getLevelRenderer()).getViewArea())
               .valkyrienskies$unloadChunk(chunkX, chunkZ);
         }

         SodiumCompat.onChunkRemoved(this.field_16525, chunkX, chunkZ);
      }
   }

   @Unique
   private void relightChunk(class_2818 chunk) {
      try {
         class_3568 lightEngine = this.field_16525.method_22336();
         class_1923 cp = chunk.method_12004();
         int baseX = cp.method_8326();
         int baseZ = cp.method_8328();
         class_2826[] sections = chunk.method_12006();

         for (int sIdx = 0; sIdx < sections.length; sIdx++) {
            class_2826 section = sections[sIdx];
            if (section != null && !section.method_38292()) {
               int sectionY = chunk.method_31604(sIdx);
               lightEngine.method_15551(class_4076.method_18676(cp.field_9181, sectionY, cp.field_9180), false);
            }
         }

         for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
               lightEngine.method_51471(new class_1923(cp.field_9181 + dx, cp.field_9180 + dz));
            }
         }

         for (int sIdx = 0; sIdx < sections.length; sIdx++) {
            class_2826 section = sections[sIdx];
            if (section != null && !section.method_38292()) {
               int sectionY = chunk.method_31604(sIdx);
               int baseY = sectionY << 4;

               for (int lx = 0; lx < 16; lx++) {
                  for (int ly = 0; ly < 16; ly++) {
                     for (int lz = 0; lz < 16; lz++) {
                        if (!section.method_12254(lx, ly, lz).method_27852(class_2246.field_10124)) {
                           lightEngine.method_15513(new class_2338(baseX + lx, baseY + ly, baseZ + lz));
                        }
                     }
                  }
               }
            }
         }
      } catch (Exception var14) {
      }
   }

   @Unique
   private class_2818 getOrCreateEmptyChunk(int x, int z) {
      long posLong = class_1923.method_8331(x, z);
      class_2818 cached = (class_2818)this.emptyShipChunks.get(posLong);
      if (cached != null) {
         return cached;
      }

      class_2818 chunk = new class_2818(this.field_16525, new class_1923(x, z));
      this.emptyShipChunks.put(posLong, chunk);
      return chunk;
   }
}
