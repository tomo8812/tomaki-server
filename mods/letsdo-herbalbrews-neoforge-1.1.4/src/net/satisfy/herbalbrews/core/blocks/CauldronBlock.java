package net.satisfy.herbalbrews.core.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.herbalbrews.core.blocks.entity.CauldronBlockEntity;
import net.satisfy.herbalbrews.core.util.HerbalBrewsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CauldronBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.0, 0.25, 0.1875, 0.25), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.75, 0.0, 0.0, 1.0, 0.1875, 0.25), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.75, 0.0, 0.75, 1.0, 0.1875, 1.0), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.0, 0.0, 0.75, 0.25, 0.1875, 1.0), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.0, 0.1875, 0.0, 1.0, 0.6875, 1.0), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.0625, 0.6875, 0.0625, 0.9375, 0.8125, 0.9375), BooleanOp.OR);
      return Shapes.join(shape, Shapes.box(0.0, 0.8125, 0.0, 1.0, 1.0, 1.0), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, HerbalBrewsUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public CauldronBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (level.getBlockEntity(blockPos) instanceof MenuProvider factory) {
         player.openMenu(factory);
         return InteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
      if (!state.is(newState.getBlock())) {
         if (world.getBlockEntity(pos) instanceof CauldronBlockEntity entity) {
            if (world instanceof ServerLevel) {
               Containers.dropContents(world, pos, entity);
            }

            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, moved);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return !world.isClientSide ? (lvl, pos, blkState, t) -> {
         if (t instanceof CauldronBlockEntity cauldron) {
            cauldron.tick(lvl, pos, blkState, cauldron);
         }
      } : null;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CauldronBlockEntity(pos, state);
   }

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
      double x = pos.getX() + 0.5;
      double y = pos.getY() + 0.24;
      double z = pos.getZ() + 0.5;
      if (random.nextDouble() < 0.1) {
         world.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      }

      world.playLocalSound(x, y, z, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      world.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);

      for (Direction dir : Direction.values()) {
         if (dir != Direction.DOWN) {
            double offsetX = random.nextDouble() * 0.6 - 0.3;
            double offsetY = random.nextDouble() * 0.375;
            double offsetZ = random.nextDouble() * 0.6 - 0.3;
            double px = x + offsetX + 0.5 * dir.getStepX();
            double py = y + offsetY;
            double pz = z + offsetZ + 0.5 * dir.getStepZ();
            world.addParticle(ParticleTypes.SMOKE, px, py, pz, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.SMALL_FLAME, px, py, pz, 0.0, 0.0, 0.0);
            if (random.nextDouble() < 0.6) {
               world.addParticle(
                  ParticleTypes.BUBBLE_POP,
                  px + 0.5 * dir.getStepX() * random.nextDouble(),
                  py + 0.7 * random.nextDouble(),
                  pz + 0.5 * dir.getStepZ() * random.nextDouble(),
                  0.0,
                  0.0,
                  0.0
               );
            }

            if (random.nextDouble() < 0.1) {
               world.addAlwaysVisibleParticle(
                  ParticleTypes.CAMPFIRE_COSY_SMOKE,
                  true,
                  pos.getX() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                  pos.getY() + random.nextDouble() * 2.0,
                  pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
                  0.0,
                  0.07,
                  0.0
               );
               world.addParticle(
                  ParticleTypes.SMOKE,
                  pos.getX() + 0.5 + random.nextDouble() / 4.0 * (random.nextBoolean() ? 1 : -1),
                  pos.getY() + 2.2,
                  pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (random.nextBoolean() ? 1 : -1),
                  0.0,
                  0.005,
                  0.0
               );
            }
         }
      }
   }

   public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
      if (!entity.fireImmune() && entity instanceof LivingEntity livingEntity) {
         livingEntity.hurt(world.damageSources().hotFloor(), 1.0F);
      }

      super.stepOn(world, pos, state, entity);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.herbalbrews.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
