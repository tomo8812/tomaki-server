package net.satisfy.farm_and_charm.core.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class PackedDirtBlock extends Block {
   public static final IntegerProperty WEAR = IntegerProperty.create("wear", 0, 7);
   public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
   private Supplier<Block> trampled;

   public PackedDirtBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(WEAR, 0)).setValue(SEALED, false));
   }

   public void setTrampled(Supplier<Block> target) {
      this.trampled = target;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> b) {
      b.add(new Property[]{WEAR, SEALED});
   }

   public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
      if (!level.isClientSide && entity instanceof LivingEntity && this.trampled != null && !(Boolean)state.getValue(SEALED)) {
         long t = level.getGameTime();
         if ((t + pos.asLong() & 7L) == 0L) {
            RandomSource r = level.getRandom();
            if (r.nextFloat() < 0.2F) {
               int w = (Integer)state.getValue(WEAR);
               if (w >= 6) {
                  level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 0.6F, 1.0F);
                  level.setBlock(pos, this.trampled.get().defaultBlockState(), 3);
               } else {
                  level.setBlock(pos, (BlockState)state.setValue(WEAR, w + 1), 2);
               }
            }
         }
      }

      super.stepOn(level, pos, state, entity);
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
      if (!level.isClientSide && entity instanceof LivingEntity && this.trampled != null && !(Boolean)state.getValue(SEALED) && fallDistance > 0.9F) {
         int w = (Integer)state.getValue(WEAR);
         int inc = Math.min(2, 1 + (int)(fallDistance / 3.0F));
         int nw = Math.min(7, w + inc);
         if (nw == 7) {
            level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 0.8F, 0.9F);
            level.setBlock(pos, this.trampled.get().defaultBlockState(), 3);
         } else {
            level.setBlock(pos, (BlockState)state.setValue(WEAR, nw), 2);
         }
      }

      super.fallOn(level, state, pos, entity, fallDistance);
   }

   @NotNull
   public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!(stack.getItem() instanceof ShovelItem) && !(stack.getItem() instanceof HoeItem)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      boolean sealed = (Boolean)state.getValue(SEALED);
      if (!level.isClientSide) {
         level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.setBlock(pos, (BlockState)state.setValue(SEALED, !sealed), 3);
      } else {
         RandomSource r = level.getRandom();

         for (int i = 0; i < 12; i++) {
            double x = pos.getX() + 0.5 + (r.nextDouble() - 0.5) * 0.4;
            double y = pos.getY() + 1.02;
            double z = pos.getZ() + 0.5 + (r.nextDouble() - 0.5) * 0.4;
            double vx = (r.nextDouble() - 0.5) * 0.06;
            double vy = 0.15 + r.nextDouble() * 0.2;
            double vz = (r.nextDouble() - 0.5) * 0.06;
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, vx, vy, vz);
         }
      }

      return ItemInteractionResult.sidedSuccess(level.isClientSide);
   }
}
