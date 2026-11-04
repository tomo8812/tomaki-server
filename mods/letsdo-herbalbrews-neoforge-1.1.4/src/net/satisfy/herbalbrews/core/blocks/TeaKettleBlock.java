package net.satisfy.herbalbrews.core.blocks;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import net.satisfy.herbalbrews.core.registry.SoundEventRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsUtil;
import org.jetbrains.annotations.NotNull;

public class TeaKettleBlock extends BaseEntityBlock {
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.5, 0.8125), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.125, 0.4375, 0.1875, 0.5, 0.5625), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0.5, 0.3125, 0.6875, 0.5625, 0.6875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0.5625, 0.4375, 0.5625, 0.625, 0.5625), BooleanOp.OR);
      return Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.5, 0.4375, 0.1875, 0.625, 0.5625), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, HerbalBrewsUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty LIT = BooleanProperty.create("lit");
   public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
   public static final MapCodec<TeaKettleBlock> CODEC = simpleCodec(TeaKettleBlock::new);

   public TeaKettleBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(COOKING, false)).setValue(LIT, false)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (level.getBlockEntity(blockPos) instanceof MenuProvider factory) {
         player.openMenu(factory);
         return InteractionResult.SUCCESS;
      } else {
         return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
      }
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
      if (!state.is(newState.getBlock())) {
         if (world.getBlockEntity(pos) instanceof TeaKettleBlockEntity pot) {
            if (world instanceof ServerLevel) {
               Containers.dropContents(world, pos, pot);
            }

            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, moved);
      }
   }

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
      if (world.isClientSide() && world.getBlockEntity(pos) instanceof TeaKettleBlockEntity be && be.doEffect) {
         double d = pos.getX() + 0.5;
         double e = pos.getY() + 0.5;
         double f = pos.getZ() + 0.5;
         world.playLocalSound(d, e, f, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         double radius = 0.75;
         int particleCount = 16;

         for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2) * i / particleCount;
            double offsetX = radius * Math.cos(angle);
            double offsetZ = radius * Math.sin(angle);
            double px = d + offsetX;
            double pz = f + offsetZ;
            world.addParticle(ParticleTypes.FLAME, px, e, pz, 0.0, 0.02, 0.0);
         }

         be.doEffect = false;
      }

      if ((Boolean)state.getValue(COOKING) || (Boolean)state.getValue(LIT)) {
         double d = pos.getX() + 0.5;
         double e = pos.getY() + 8.0F + 5.0F;
         double f = pos.getZ() + 0.5;
         if (random.nextDouble() < 0.3) {
            world.playLocalSound(d, e, f, (SoundEvent)SoundEventRegistry.TEA_KETTLE_BOILING.get(), SoundSource.BLOCKS, 0.05F, 1.0F, false);
         }

         SimpleParticleType cozySmokeParticle = ParticleTypes.SMOKE;
         this.addParticle(world, cozySmokeParticle, pos.getX(), pos.getY() + 0.5, pos.getZ(), random, 0.02);
         this.addParticle(world, ParticleTypes.SMOKE, pos.getX(), pos.getY() + 2.2, pos.getZ(), random, 0.002);
      }
   }

   private void addParticle(Level world, ParticleOptions particleOptions, double x, double y, double z, RandomSource random, double velocityY) {
      world.addAlwaysVisibleParticle(
         particleOptions,
         x + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
         y + random.nextDouble() + random.nextDouble(),
         z + 0.5 + random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1),
         0.0,
         velocityY,
         0.0
      );
   }

   public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
      boolean isLit = (Boolean)state.getValue(LIT);
      if (isLit && !entity.fireImmune() && entity instanceof LivingEntity livingEntity) {
         livingEntity.hurt(world.damageSources().hotFloor(), 1.0F);
      }

      super.stepOn(world, pos, state, entity);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, COOKING, LIT});
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return createTickerHelper(
         type, (BlockEntityType)EntityTypeRegistry.TEA_KETTLE_BLOCK_ENTITY.get(), (world1, pos, state1, be) -> be.tick(world1, pos, state1)
      );
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TeaKettleBlockEntity(pos, state);
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.herbalbrews.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
