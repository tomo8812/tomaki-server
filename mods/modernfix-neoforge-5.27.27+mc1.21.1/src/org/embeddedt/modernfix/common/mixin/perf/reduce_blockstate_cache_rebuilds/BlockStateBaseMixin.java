package org.embeddedt.modernfix.common.mixin.perf.reduce_blockstate_cache_rebuilds;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase.Cache;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.embeddedt.modernfix.duck.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> implements IBlockState {
   private static final FluidState MFIX$VANILLA_DEFAULT_FLUID = Fluids.EMPTY.defaultFluidState();
   @Shadow
   private Cache cache;
   @Shadow
   private FluidState fluidState;
   @Shadow
   private boolean isRandomlyTicking;
   @Shadow
   @Deprecated
   private boolean legacySolid;
   private volatile boolean cacheInvalid = false;
   private static boolean buildingCache = false;

   protected BlockStateBaseMixin(Block object, Reference2ObjectArrayMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
      super(object, immutableMap, mapCodec);
   }

   @Shadow
   public abstract void initCache();

   @Shadow
   protected abstract BlockState asState();

   @Override
   public void clearCache() {
      this.cacheInvalid = true;
   }

   @Override
   public boolean isCacheInvalid() {
      return this.cacheInvalid;
   }

   private void mfix$generateCache() {
      if (this.cacheInvalid) {
         synchronized (BlockStateBase.class) {
            if (this.cacheInvalid && !buildingCache) {
               buildingCache = true;

               try {
                  this.initCache();
                  this.cacheInvalid = false;
               } finally {
                  buildingCache = false;
               }
            }
         }
      }
   }

   @Redirect(
      method = "*",
      at = @At(
         value = "FIELD",
         opcode = 180,
         target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;cache:Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase$Cache;",
         ordinal = 0
      )
   )
   private Cache dynamicCacheGen(BlockStateBase base) {
      this.mfix$generateCache();
      return this.cache;
   }

   @Redirect(
      method = "*",
      at = @At(
         value = "FIELD",
         opcode = 180,
         target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;fluidState:Lnet/minecraft/world/level/material/FluidState;",
         ordinal = 0
      ),
      require = 0
   )
   private FluidState genCacheBeforeGettingFluid(BlockStateBase base) {
      if (this.cacheInvalid && this.fluidState == MFIX$VANILLA_DEFAULT_FLUID) {
         synchronized (BlockStateBase.class) {
            if (!buildingCache) {
               buildingCache = true;

               try {
                  this.fluidState = ((BlockBehaviourInvoker)this.owner).invokeGetFluidState(this.asState());
               } finally {
                  buildingCache = false;
               }
            }
         }
      }

      return this.fluidState;
   }

   @Redirect(
      method = "*",
      at = @At(value = "FIELD", opcode = 180, target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;isRandomlyTicking:Z", ordinal = 0)
   )
   private boolean genCacheBeforeGettingTicking(BlockStateBase base) {
      return this.cacheInvalid ? ((BlockBehaviourInvoker)this.owner).invokeIsRandomlyTicking(this.asState()) : this.isRandomlyTicking;
   }

   @Redirect(
      method = "*",
      at = @At(value = "FIELD", opcode = 180, target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;legacySolid:Z", ordinal = 0)
   )
   private boolean genCacheBeforeCheckingSolid(BlockStateBase base) {
      this.mfix$generateCache();
      return this.legacySolid;
   }
}
