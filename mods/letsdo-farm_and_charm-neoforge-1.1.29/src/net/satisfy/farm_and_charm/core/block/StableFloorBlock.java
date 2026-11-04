package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class StableFloorBlock extends Block {
   public static final MapCodec<StableFloorBlock> CODEC = simpleCodec(StableFloorBlock::new);
   public static final IntegerProperty WEAR = IntegerProperty.create("wear", 0, 27);
   public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
   private Supplier<Block> trampled;
   private Supplier<Block> straw;

   public StableFloorBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(WEAR, 0)).setValue(SEALED, false));
   }

   public void setTrampled(Supplier<Block> s) {
      this.trampled = s;
   }

   public void setStraw(Supplier<Block> s) {
      this.straw = s;
   }

   @NotNull
   public MapCodec<? extends Block> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> b) {
      b.add(new Property[]{WEAR, SEALED});
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
      if (!level.isClientSide && !(Boolean)state.getValue(SEALED) && this.straw != null && hasHayInBox(level, pos) && level.getRandom().nextFloat() < 0.25F) {
         level.setBlock(pos, this.straw.get().defaultBlockState(), 3);
      } else {
         super.onPlace(state, level, pos, old, moving);
      }
   }

   @NotNull
   protected BlockState updateShape(BlockState state, Direction dir, BlockState other, LevelAccessor level, BlockPos pos, BlockPos fromPos) {
      if (!(Boolean)state.getValue(SEALED)
         && this.straw != null
         && level instanceof Level lvl
         && !lvl.isClientSide
         && hasHayInBox(level, pos)
         && lvl.getRandom().nextFloat() < 0.25F) {
         lvl.setBlock(pos, this.straw.get().defaultBlockState(), 3);
         return state;
      } else {
         return super.updateShape(state, dir, other, level, pos, fromPos);
      }
   }

   public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
      if (!level.isClientSide && entity instanceof LivingEntity && !(Boolean)state.getValue(SEALED)) {
         if (entity instanceof LivingEntity
            && !(Boolean)state.getValue(SEALED)
            && this.straw != null
            && hasHayInBox(level, pos)
            && level.getRandom().nextFloat() < 0.25F) {
            level.setBlock(pos, this.straw.get().defaultBlockState(), 3);
            super.stepOn(level, pos, state, entity);
            return;
         }

         long t = level.getGameTime();
         if ((t + pos.asLong() & 7L) == 0L) {
            RandomSource r = level.getRandom();
            if (r.nextFloat() < 0.15F) {
               int w = (Integer)state.getValue(WEAR);
               if (w >= 26 && this.trampled != null) {
                  level.setBlock(pos, this.trampled.get().defaultBlockState(), 3);
               } else {
                  level.setBlock(pos, (BlockState)state.setValue(WEAR, Math.min(27, w + 1)), 2);
               }
            }
         }
      }

      super.stepOn(level, pos, state, entity);
   }

   private static boolean hasHayInBox(LevelAccessor level, BlockPos pos) {
      for (int dx = -1; dx <= 1; dx++) {
         for (int dy = -1; dy <= 1; dy++) {
            for (int dz = -1; dz <= 1; dz++) {
               if (level.getBlockState(pos.offset(dx, dy, dz)).is(Blocks.HAY_BLOCK)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @NotNull
   public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!(stack.getItem() instanceof ShovelItem) && !(stack.getItem() instanceof HoeItem)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      boolean sealed = (Boolean)state.getValue(SEALED);
      if (!level.isClientSide) {
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
