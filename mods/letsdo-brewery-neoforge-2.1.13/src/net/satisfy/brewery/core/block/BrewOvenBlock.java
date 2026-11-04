package net.satisfy.brewery.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.block.property.Heat;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class BrewOvenBlock extends BrewingstationBlock {
   public static final EnumProperty<Heat> HEAT = BlockStateRegistry.HEAT;
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, BrewOvenBlock.voxelShapeSupplier.get()));
      }
   });
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.125, 0.0, 0.0, 1.0, 0.125, 0.875));
      return Shapes.or(shape, Shapes.box(0.0, 0.125, 0.0, 1.0, 1.0, 0.9375));
   };
   private long lastSoundTime = 0L;

   public BrewOvenBlock(Properties properties) {
      super(properties.lightLevel(state -> hasHeat(state) ? 13 : 0));
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(MATERIAL, BrewMaterial.WOOD)).setValue(HEAT, Heat.OFF));
   }

   private static boolean hasHeat(BlockState state) {
      return state.getValue(HEAT) != Heat.OFF;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (blockState.getValue(HEAT) != Heat.LIT && AbstractFurnaceBlockEntity.isFuel(itemStack)) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(HEAT, Heat.LIT), 3);
         level.playSound(null, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
         if (!player.isCreative()) {
            itemStack.shrink(1);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
      }
   }

   public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
      boolean isHeated = state.getValue(HEAT) != Heat.OFF;
      if (isHeated && !entity.fireImmune() && entity instanceof Player) {
         entity.hurt(world.damageSources().inFire(), 1.0F);
      }

      super.stepOn(world, pos, state, entity);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
      if (blockState.getValue(HEAT) != Heat.OFF) {
         double x = blockPos.getX() + 0.5;
         double y = blockPos.getY() + 0.7;
         double z = blockPos.getZ() + 0.5;
         if (randomSource.nextDouble() < 0.1) {
            level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction direction = ((Direction)blockState.getValue(FACING)).getCounterClockWise();
         Axis axis = direction.getAxis();
         double h = randomSource.nextDouble() * 0.6 - 0.3;
         double i = axis == Axis.X ? direction.getStepX() * 0.62 : h;
         double j = randomSource.nextDouble() * 9.0 / 16.0;
         double k = axis == Axis.Z ? direction.getStepZ() * 0.62 : h;
         if (blockState.getValue(HEAT) == Heat.WEAK) {
            ColorParticleOption blackSteam = ColorParticleOption.create((ParticleType)FoundationParticles.COLORED_STEAM.get(), -16777216);
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastSoundTime >= 9500L) {
               level.playLocalSound(x, y, z, (SoundEvent)SoundEventRegistry.BREWSTATION_OVEN.get(), SoundSource.BLOCKS, 0.7F, 1.0F, false);
               this.lastSoundTime = currentTime;
            }

            for (int l = 0; l < 4; l++) {
               double spread = randomSource.nextDouble() * 0.3 - 0.15;
               level.addParticle(blackSteam, x + i + (axis == Axis.Z ? spread : 0.0), y + j * 0.5, z + k + (axis == Axis.X ? spread : 0.0), 0.0, 0.03, 0.0);
            }

            if (randomSource.nextDouble() < 0.3) {
               level.addParticle(ParticleTypes.SMALL_FLAME, x + i, y - 0.1 + j * 0.2, z + k, 0.0, 0.0, 0.0);
            }

            if (randomSource.nextDouble() < 0.15) {
               level.addParticle(ParticleTypes.WHITE_ASH, x + i, y + j * 0.5, z + k, 0.0, 0.02, 0.0);
            }

            for (int l = 0; l < 3; l++) {
               level.addParticle(blackSteam, x + (randomSource.nextDouble() - 0.5) * 0.6, y + 0.35, z + (randomSource.nextDouble() - 0.5) * 0.6, 0.0, 0.04, 0.0);
            }
         } else {
            for (int l = 0; l < 2; l++) {
               double spread = randomSource.nextDouble() * 0.2 - 0.1;
               level.addParticle(ParticleTypes.FLAME, x + i + (axis == Axis.Z ? spread : 0.0), y + j, z + k + (axis == Axis.X ? spread : 0.0), 0.0, 0.01, 0.0);
            }

            level.addParticle(ParticleTypes.SMOKE, x + i, y + j, z + k, 0.0, 0.07, 0.0);
            if (randomSource.nextDouble() < 0.08) {
               level.addParticle(ParticleTypes.LAVA, x + i, y + j, z + k, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{HEAT});
   }
}
