package org.valkyrienskies.mod.mixin.client.world;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.class_1109;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1923;
import net.minecraft.class_1934;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2388;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_3610;
import net.minecraft.class_4076;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_631;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_6880;
import net.minecraft.class_761;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_638.class_5271;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.world.ShipWorld;
import org.valkyrienskies.core.internal.world.VsiClientShipWorld;
import org.valkyrienskies.core.util.AABBdUtilKt;
import org.valkyrienskies.core.util.VectorConversionsKt;
import org.valkyrienskies.mod.client.audio.SimpleSoundInstanceOnShip;
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.config.DimensionParametersResolver;
import org.valkyrienskies.mod.util.McMathUtilKt;

@Mixin(class_638.class)
public abstract class MixinClientLevel implements IShipObjectWorldClientProvider {
   @Unique
   private final class_5819 vsRandom = class_5819.method_43047();
   @Shadow
   @Final
   private class_310 field_3729;
   @Shadow
   @Final
   private class_631 field_24605;
   @Unique
   private final Map<class_1923, List<Vector3ic>> vs$knownChunks = new HashMap<>();
   @Unique
   private final Long2LongOpenHashMap vs$chunksToUnload = new Long2LongOpenHashMap();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !MixinClientLevel.class.desiredAssertionStatus();

   @NotNull
   @Override
   public VsiClientShipWorld getShipObjectWorld() {
      return ((IShipObjectWorldClientProvider)this.field_3729).getShipObjectWorld();
   }

   @Shadow
   private void method_2938(class_2338 blockPos, class_2680 blockState, class_2394 particleData, boolean shapeDownSolid) {
   }

   @Inject(method = "<init>", at = @At("TAIL"))
   private void postInit(
      class_634 clientPacketListener,
      class_5271 clientLevelData,
      class_5321 resourceKey,
      class_6880 holder,
      int i,
      int j,
      Supplier supplier,
      class_761 levelRenderer,
      boolean bl,
      long l,
      CallbackInfo ci
   ) {
      if (this.getShipObjectWorld() != null) {
         DimensionParametersResolver.Parameters params = DimensionParametersResolver.INSTANCE
            .getDimensionMap()
            .get(VSGameUtilsKt.getDimensionId((class_638)this));
         if (params != null) {
            this.getShipObjectWorld()
               .addDimension(
                  VSGameUtilsKt.getDimensionId((class_638)this),
                  VSGameUtilsKt.getYRange((class_638)this),
                  params.getGravity(),
                  params.getSeaLevel(),
                  params.getMaxY()
               );
            return;
         }

         this.getShipObjectWorld()
            .addDimension(
               VSGameUtilsKt.getDimensionId((class_638)this), VSGameUtilsKt.getYRange((class_638)this), McMathUtilKt.getDEFAULT_WORLD_GRAVITY(), 62.0, 962.0
            );
      }
   }

   @Inject(method = "disconnect", at = @At("TAIL"))
   private void afterDisconnect(CallbackInfo ci) {
      ValkyrienSkiesMod.getVsCore().getHooks().afterDisconnect();
   }

   @Inject(at = @At("TAIL"), method = "animateTick")
   private void afterAnimatedTick(int posX, int posY, int posZ, CallbackInfo ci) {
      boolean holdingBarrierItem = false;
      if (this.field_3729.field_1761.method_2920() == class_1934.field_9220) {
         for (class_1799 itemStack : this.field_3729.field_1724.method_5877()) {
            if (itemStack.method_7909() == class_2246.field_10499.method_8389()) {
               holdingBarrierItem = true;
               break;
            }
         }
      }

      class_1657 player = this.field_3729.field_1724;
      assert player != null;
      double playerScale = player.method_17681() / class_1657.field_18135.comp_2185();
      class_243 pos = player.method_19538();
      AABBdc playerCenterBB = new AABBd(pos.field_1352, pos.field_1351, pos.field_1350, pos.field_1352, pos.field_1351, pos.field_1350);
      AABBdc shipIntersectBB = AABBdUtilKt.expand(new AABBd(playerCenterBB), 32.0);
      double biggerBBProbability = 0.020385742F;
      double smallerBBProbability = 0.16308594F;
      AABBd temp0 = new AABBd();
      AABBi temp1 = new AABBi();
      AABBd temp2 = new AABBd();
      AABBi temp3 = new AABBi();
      AABBi temp4 = new AABBi();
      AABBi temp5 = new AABBi();
      AABBd temp6 = new AABBd();
      AABBd temp7 = new AABBd();

      for (Ship ship : VSGameUtilsKt.getShipsIntersecting((class_1937)class_638.class.cast(this), shipIntersectBB)) {
         AABBic shipVoxelAABB = ship.getShipAABB();
         if (shipVoxelAABB != null) {
            AABBdc biggerBB = AABBdUtilKt.expand(temp6.set(playerCenterBB), 32.0 * playerScale);
            AABBdc smallerBB = AABBdUtilKt.expand(temp7.set(playerCenterBB), 16.0 * playerScale);
            AABBic biggerBBTransformed = VectorConversionsKt.toAABBi(biggerBB.transform(ship.getWorldToShip(), temp0), temp1);
            AABBic smallerBBTransformed = VectorConversionsKt.toAABBi(smallerBB.transform(ship.getWorldToShip(), temp2), temp3);
            AABBic biggerBBIntersection = VectorConversionsKt.expand(shipVoxelAABB, 1, temp4).intersection(biggerBBTransformed);
            AABBic smallerBBIntersection = VectorConversionsKt.expand(shipVoxelAABB, 1, temp5).intersection(smallerBBTransformed);
            if (biggerBBIntersection.isValid()) {
               this.animateTickVS(biggerBBIntersection, 0.020385742F, holdingBarrierItem);
            }

            if (smallerBBIntersection.isValid()) {
               this.animateTickVS(smallerBBIntersection, 0.16308594F, holdingBarrierItem);
            }
         }
      }
   }

   @Unique
   private void animateTickVS(AABBic region, double regionBlockProbability, boolean holdingBarrierItem) {
      int volume = (region.maxX() - region.minX() + 1) * (region.maxY() - region.minY() + 1) * (region.maxZ() - region.minZ() + 1);
      double blocksToTickAsDouble = volume * regionBlockProbability;
      int blocksToTick = (int)Math.floor(blocksToTickAsDouble);
      if (this.vsRandom.method_43058() > blocksToTickAsDouble - blocksToTick) {
         blocksToTick++;
      }

      class_638 thisAsClientLevel = class_638.class.cast(this);
      class_2339 mutableBlockPos = new class_2339();
      int minCX = class_4076.method_18675(region.minX());
      int maxCX = class_4076.method_18675(region.maxX());
      int minCZ = class_4076.method_18675(region.minZ());
      int maxCZ = class_4076.method_18675(region.maxZ());
      class_2818[][] chunkCache = new class_2818[maxCX - minCX + 1][maxCZ - minCZ + 1];

      for (int i = 0; i < blocksToTick; i++) {
         int posX = region.minX() + this.vsRandom.method_43048(region.maxX() - region.minX() + 1);
         int posY = region.minY() + this.vsRandom.method_43048(region.maxY() - region.minY() + 1);
         int posZ = region.minZ() + this.vsRandom.method_43048(region.maxZ() - region.minZ() + 1);
         mutableBlockPos.method_10103(posX, posY, posZ);
         if (!thisAsClientLevel.method_31606(mutableBlockPos)) {
            int cx = class_4076.method_18675(posX) - minCX;
            int cz = class_4076.method_18675(posZ) - minCZ;
            class_2818 levelChunk = chunkCache[cx][cz];
            if (levelChunk == null) {
               levelChunk = thisAsClientLevel.method_8497(cx + minCX, cz + minCZ);
               chunkCache[cx][cz] = levelChunk;
            }

            class_2680 blockState = levelChunk.method_8320(mutableBlockPos);
            blockState.method_26204().method_9496(blockState, thisAsClientLevel, mutableBlockPos, this.vsRandom);
            class_3610 fluidState = levelChunk.method_8316(mutableBlockPos);
            if (!fluidState.method_15769()) {
               fluidState.method_15768(thisAsClientLevel, mutableBlockPos, this.vsRandom);
               class_2394 particleOptions = fluidState.method_15766();
               if (particleOptions != null && this.vsRandom.method_43048(10) == 0) {
                  boolean bl2 = blockState.method_26206(thisAsClientLevel, mutableBlockPos, class_2350.field_11033);
                  class_2338 blockPos = mutableBlockPos.method_10074();
                  if (!thisAsClientLevel.method_31606(blockPos)) {
                     this.method_2938(blockPos, levelChunk.method_8320(blockPos), particleOptions, bl2);
                  }
               }
            }

            if (holdingBarrierItem && blockState.method_27852(class_2246.field_10499)) {
               thisAsClientLevel.method_8406(new class_2388(class_2398.field_35434, blockState), posX + 0.5, posY + 0.5, posZ + 0.5, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   @Redirect(
      at = @At(
         value = "NEW",
         target = "(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;DDD)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
      ),
      method = "playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V"
   )
   private class_1109 redirectNewSoundInstance(
      class_3414 soundEvent, class_3419 soundSource, float volume, float pitch, class_5819 randomSource, double x, double y, double z
   ) {
      Ship ship = VSGameUtilsKt.getShipManagingPos((class_1937)class_638.class.cast(this), x, y, z);
      return ship != null
         ? new SimpleSoundInstanceOnShip(soundEvent, soundSource, volume, pitch, randomSource, x, y, z, ship)
         : new class_1109(soundEvent, soundSource, volume, pitch, randomSource, x, y, z);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   @Override
   public ShipWorld getShipObjectWorld() {
      return this.getShipObjectWorld();
   }
}
