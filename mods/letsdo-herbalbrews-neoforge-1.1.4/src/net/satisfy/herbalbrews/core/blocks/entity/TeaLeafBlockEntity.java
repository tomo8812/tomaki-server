package net.satisfy.herbalbrews.core.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.satisfy.herbalbrews.core.blocks.TeaLeafBlock;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.platform.PlatformHelper;

public class TeaLeafBlockEntity extends BlockEntity implements BlockEntityTicker<TeaLeafBlockEntity> {
   private int timer = 0;

   public TeaLeafBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.TEA_LEAF_BLOCK_ENTITY.get(), pos, state);
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.timer = compoundTag.getInt("Timer");
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      compoundTag.putInt("Timer", this.timer);
   }

   private Block getEndBlock(Block startBlock) {
      if (startBlock == ObjectRegistry.GREEN_TEA_LEAF_BLOCK.get()) {
         return (Block)ObjectRegistry.DRIED_OUT_GREEN_TEA_LEAF_BLOCK.get();
      } else if (startBlock == ObjectRegistry.MIXED_TEA_LEAF_BLOCK.get()) {
         return (Block)ObjectRegistry.OOLONG_TEA_LEAF_BLOCK.get();
      } else {
         return startBlock == ObjectRegistry.DRIED_GREEN_TEA_LEAF_BLOCK.get() ? (Block)ObjectRegistry.BLACK_TEA_LEAF_BLOCK.get() : Blocks.AIR;
      }
   }

   public void tick(Level level, BlockPos blockPos, BlockState blockState, TeaLeafBlockEntity blockEntity) {
      if (level != null && !level.isClientSide) {
         int dryingDuration = PlatformHelper.getDryingDuration();
         this.timer++;
         if (this.timer >= dryingDuration) {
            BlockState state = level.getBlockState(blockPos);
            if (!state.hasProperty(TeaLeafBlock.DRYING)) {
               return;
            }

            int dryingStage = (Integer)state.getValue(TeaLeafBlock.DRYING);
            if (dryingStage < 4) {
               level.levelEvent(2001, blockPos, Block.getId(state));
               level.playSound(null, blockPos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.setBlockAndUpdate(blockPos, (BlockState)state.setValue(TeaLeafBlock.DRYING, dryingStage + 1));
               level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, Context.of(state));
               this.timer = 0;
            } else {
               Block endBlock = this.getEndBlock(state.getBlock());
               if (endBlock != Blocks.AIR) {
                  level.setBlockAndUpdate(blockPos, endBlock.defaultBlockState());
               }
            }
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void tick(Level param1, BlockPos nullx, BlockState nullxx, BlockEntity nullxxx) {
      this.tick(var1, nullx, nullxx, (TeaLeafBlockEntity)nullxxx);
   }
}
