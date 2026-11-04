package net.satisfy.brewery.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.block.entity.BrewTimerBlockEntity;
import net.satisfy.brewery.core.block.entity.BrewstationBlockEntity;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.event.brew_event.BrewHelper;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrewTimerBlock extends BrewingstationBlock implements EntityBlock {
   public static final BooleanProperty TIME = BlockStateRegistry.TIME;
   public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
   public static final BooleanProperty PRESSED = BooleanProperty.create("pressed");
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, BrewTimerBlock.voxelShapeSupplier.get()));
      }
   });
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.125, 0.5, 0.9375, 0.5, 0.875, 1.0));
      shape = Shapes.or(shape, Shapes.box(0.6875, 0.6875, 0.9375, 0.8125, 0.8125, 1.0));
      shape = Shapes.or(shape, Shapes.box(0.6875, 0.5, 0.9375, 0.8125, 0.625, 1.0));
      shape = Shapes.or(shape, Shapes.box(0.0, 0.125, 0.875, 1.0, 1.0, 0.9375));
      shape = Shapes.or(shape, Shapes.box(0.875, 0.125, 0.0, 1.0, 1.0, 0.875));
      shape = Shapes.or(shape, Shapes.box(0.0, 0.0, 0.0, 0.875, 0.125, 0.875));
      return Shapes.or(shape, Shapes.box(0.0, 0.9375, 0.0, 0.875, 1.0, 0.875));
   };
   private long lastSoundTime = 0L;

   public BrewTimerBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(MATERIAL, BrewMaterial.WOOD)).setValue(TIME, false))
               .setValue(ACTIVATED, false))
            .setValue(PRESSED, false)
      );
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (!level.isClientSide && level.getBlockEntity(blockPos) instanceof BrewTimerBlockEntity timer) {
         timer.press();
         level.playSound(null, blockPos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.4F, 1.4F);
         BrewstationBlockEntity station = timer.findStation();
         if (!(Boolean)blockState.getValue(TIME) && station != null && station.tryStart()) {
            level.playSound(null, blockPos, (SoundEvent)SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 0.8F, 1.2F);
            BlockPos whistlePos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_WHISTLE.get(), station.getComponents(), level);
            if (whistlePos != null && level instanceof ServerLevel serverLevel) {
               BrewWhistleBlock.puff(serverLevel, whistlePos);
            }
         }
      }

      if (Boolean.TRUE.equals(blockState.getValue(TIME))) {
         level.setBlockAndUpdate(blockPos, (BlockState)((BlockState)blockState.setValue(TIME, false)).setValue(ACTIVATED, false));
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.sidedSuccess(level.isClientSide);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BrewTimerBlockEntity(pos, state);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{TIME, ACTIVATED, PRESSED});
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
      level.getBlockEntity(blockPos);
      if (Boolean.TRUE.equals(blockState.getValue(TIME))) {
         double x = blockPos.getX() + 0.5;
         double y = blockPos.getY() + 1.2;
         double z = blockPos.getZ() + 0.5;
         long currentTime = System.currentTimeMillis();
         if (currentTime - this.lastSoundTime >= 900L) {
            level.playLocalSound(x, y, z, (SoundEvent)SoundEventRegistry.BREWSTATION_TIMER_LOOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
            this.lastSoundTime = currentTime;
         }

         if (!Boolean.TRUE.equals(blockState.getValue(ACTIVATED))) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(ACTIVATED, true), 3);
         }
      }
   }
}
