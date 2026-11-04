package net.caffeinemc.mods.lithium.mixin.util.block_tracking;

import net.caffeinemc.mods.lithium.common.block.BlockCountingSection;
import net.caffeinemc.mods.lithium.common.block.BlockListeningSection;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlagHolder;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlags;
import net.caffeinemc.mods.lithium.common.block.ListeningBlockStatePredicate;
import net.caffeinemc.mods.lithium.common.block.TrackedBlockStatePredicate;
import net.caffeinemc.mods.lithium.common.tracking.block.ChunkSectionChangeCallback;
import net.caffeinemc.mods.lithium.common.tracking.block.SectionedBlockChangeTracker;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainer.CountConsumer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements BlockCountingSection, BlockListeningSection {
   @Shadow
   @Final
   private PalettedContainer<BlockState> states;
   @Unique
   private short[] countsByFlag = null;
   @Unique
   private ChunkSectionChangeCallback changeListener;
   @Unique
   private short listeningMask;

   @Unique
   private static void addToFlagCount(short[] countsByFlag, BlockState state, short change) {
      int flags = ((BlockStateFlagHolder)state).lithium$getAllFlags();

      int i;
      while ((i = Integer.numberOfTrailingZeros(flags)) < 32 && i < countsByFlag.length) {
         countsByFlag[i] += change;
         flags &= ~(1 << i);
      }
   }

   @Override
   public boolean lithium$mayContainAny(TrackedBlockStatePredicate trackedBlockStatePredicate) {
      if (this.countsByFlag == null) {
         this.fastInitClientCounts();
      }

      return this.countsByFlag[trackedBlockStatePredicate.getIndex()] != 0;
   }

   @Unique
   private void fastInitClientCounts() {
      this.countsByFlag = new short[BlockStateFlags.NUM_TRACKED_FLAGS];

      for (TrackedBlockStatePredicate trackedBlockStatePredicate : BlockStateFlags.TRACKED_FLAGS) {
         if (this.states.maybeHas(trackedBlockStatePredicate)) {
            this.countsByFlag[trackedBlockStatePredicate.getIndex()] = 4096;
         }
      }
   }

   @Inject(method = "Lnet/minecraft/world/level/chunk/LevelChunkSection;<init>(Lnet/minecraft/core/Registry;)V", at = @At("RETURN"))
   private void initAirSection(Registry<?> registry, CallbackInfo ci) {
      if (this.countsByFlag != null) {
         throw new IllegalStateException("CountsByFlag already initialized!");
      }

      this.countsByFlag = new short[BlockStateFlags.NUM_TRACKED_FLAGS];

      for (TrackedBlockStatePredicate trackedBlockStatePredicate : BlockStateFlags.TRACKED_FLAGS) {
         if (this.states.maybeHas(trackedBlockStatePredicate)) {
            this.countsByFlag[trackedBlockStatePredicate.getIndex()] = 4096;
         }
      }
   }

   @Redirect(
      method = "recalcBlockCounts()V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/chunk/PalettedContainer;count(Lnet/minecraft/world/level/chunk/PalettedContainer$CountConsumer;)V"
      )
   )
   private void initFlagCounters(PalettedContainer<BlockState> palettedContainer, CountConsumer<BlockState> consumer) {
      palettedContainer.count((state, count) -> {
         consumer.accept(state, count);
         addToFlagCount(this.countsByFlag, state, (short)count);
      });
   }

   @Inject(method = "recalcBlockCounts()V", at = @At("HEAD"))
   private void createFlagCounters(CallbackInfo ci) {
      this.countsByFlag = new short[BlockStateFlags.NUM_TRACKED_FLAGS];
   }

   @Inject(method = "read(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("HEAD"))
   private void resetData(FriendlyByteBuf buf, CallbackInfo ci) {
      this.countsByFlag = null;
   }

   @Inject(
      method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;",
         ordinal = 0,
         shift = Shift.BEFORE
      ),
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void updateFlagCounters(int x, int y, int z, BlockState newState, boolean lock, CallbackInfoReturnable<BlockState> cir, BlockState oldState) {
      this.lithium$trackBlockStateChange(newState, oldState);
   }

   @Override
   public void lithium$trackBlockStateChange(BlockState newState, BlockState oldState) {
      short[] countsByFlag = this.countsByFlag;
      if (countsByFlag != null) {
         int prevFlags = ((BlockStateFlagHolder)oldState).lithium$getAllFlags();
         int flags = ((BlockStateFlagHolder)newState).lithium$getAllFlags();
         int flagsXOR = prevFlags ^ flags;
         int iterateFlags = ~BlockStateFlags.LISTENING_MASK_OR & flagsXOR | BlockStateFlags.LISTENING_MASK_OR & this.listeningMask & (prevFlags | flags);

         int flagIndex;
         while ((flagIndex = Integer.numberOfTrailingZeros(iterateFlags)) < 32 && flagIndex < countsByFlag.length) {
            int flagBit = 1 << flagIndex;
            if ((flagsXOR & flagBit) != 0) {
               countsByFlag[flagIndex] += (short)(1 - ((prevFlags >>> flagIndex & 1) << 1));
            }

            if ((this.listeningMask & flagBit) != 0) {
               this.listeningMask = this.changeListener.onBlockChange(flagIndex, this);
            }

            iterateFlags &= ~flagBit;
         }
      }
   }

   @Override
   public void lithium$addToCallback(ListeningBlockStatePredicate blockGroup, SectionedBlockChangeTracker tracker, long sectionPos, Level world) {
      if (this.changeListener == null) {
         if (sectionPos == Long.MIN_VALUE || world == null) {
            throw new IllegalArgumentException("Expected world and section pos during intialization!");
         }

         this.changeListener = ChunkSectionChangeCallback.create(sectionPos, world);
      }

      this.listeningMask = this.changeListener.addTracker(tracker, blockGroup);
   }

   @Override
   public void lithium$removeFromCallback(ListeningBlockStatePredicate blockGroup, SectionedBlockChangeTracker tracker) {
      if (this.changeListener != null) {
         this.listeningMask = this.changeListener.removeTracker(tracker, blockGroup);
      }
   }
}
