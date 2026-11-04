package net.satisfy.candlelight.core.block;

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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.core.block.entity.LargeCookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LargeCookingPotBlock extends BaseEntityBlock {
   public static final MapCodec<LargeCookingPotBlock> CODEC = simpleCodec(LargeCookingPotBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty LIT = BooleanProperty.create("lit");
   public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
   public static final BooleanProperty NEEDS_SUPPORT = BooleanProperty.create("needs_support");
   public static final EnumProperty<LargeCookingPotBlock.CookpotStage> STAGE = EnumProperty.create("stage", LargeCookingPotBlock.CookpotStage.class);
   private static final Map<Direction, VoxelShape> SHAPES_DEFAULT = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      Supplier<VoxelShape> voxelShapeSupplier = () -> {
         VoxelShape shape = Shapes.empty();
         shape = Shapes.join(shape, Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125), BooleanOp.OR);
         shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0.3125, 0.1875, 0.75, 0.6875), BooleanOp.OR);
         return Shapes.join(shape, Shapes.box(0.8125, 0.625, 0.3125, 0.9375, 0.75, 0.6875), BooleanOp.OR);
      };

      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });
   private static final Map<Direction, VoxelShape> SHAPES_NORMAL_NO_SUPPORT = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      Supplier<VoxelShape> voxelShapeSupplier = () -> {
         VoxelShape shape = Shapes.empty();
         shape = Shapes.join(shape, Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125), BooleanOp.OR);
         shape = Shapes.join(shape, Shapes.box(0.25, 0.0, 0.25, 0.75, 0.8125, 0.75), BooleanOp.OR);
         shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0.3125, 0.1875, 0.75, 0.6875), BooleanOp.OR);
         shape = Shapes.join(shape, Shapes.box(0.8125, 0.625, 0.3125, 0.9375, 0.75, 0.6875), BooleanOp.OR);
         return Shapes.join(shape, Shapes.box(0.4375, 0.8125, 0.4375, 0.5625, 0.875, 0.5625), BooleanOp.OR);
      };

      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public LargeCookingPotBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                     .setValue(LIT, false))
                  .setValue(COOKING, false))
               .setValue(NEEDS_SUPPORT, false))
            .setValue(STAGE, LargeCookingPotBlock.CookpotStage.NORMAL)
      );
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, LIT, COOKING, NEEDS_SUPPORT, STAGE});
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      Direction facing = (Direction)state.getValue(FACING);
      return state.getValue(STAGE) == LargeCookingPotBlock.CookpotStage.NORMAL && !state.getValue(NEEDS_SUPPORT)
         ? SHAPES_NORMAL_NO_SUPPORT.getOrDefault(facing, Shapes.empty())
         : SHAPES_DEFAULT.getOrDefault(facing, Shapes.empty());
   }

   public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      if (!world.isClientSide && (Boolean)state.getValue(NEEDS_SUPPORT)) {
         boolean isSupported = world.getBlockState(pos.below()).isSolidRender(world, pos.below()) || world.getBlockState(pos.below()).is(BlockTags.CAMPFIRES);
         if (!isSupported) {
            for (Direction direction : Plane.HORIZONTAL) {
               BlockPos neighborPos = pos.relative(direction);
               BlockState neighborState = world.getBlockState(neighborPos);
               if (neighborState.getBlock() instanceof LargeCookingPotBlock && (Boolean)neighborState.getValue(NEEDS_SUPPORT)) {
                  isSupported = true;
                  break;
               }
            }
         }

         if (!isSupported) {
            world.destroyBlock(pos, true);
         }
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      Level world = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();
      BlockState belowState = world.getBlockState(pos.below());
      boolean needsSupport = belowState.is(BlockTags.CAMPFIRES);
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()))
            .setValue(NEEDS_SUPPORT, needsSupport))
         .setValue(STAGE, LargeCookingPotBlock.CookpotStage.NORMAL);
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      BlockPos belowPos = pos.below();
      BlockState belowState = world.getBlockState(belowPos);
      boolean isCampfireBelow = belowState.is(BlockTags.CAMPFIRES);
      boolean isSolidBelow = belowState.isFaceSturdy(world, belowPos, Direction.UP);
      return isCampfireBelow || isSolidBelow;
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(world, pos)) {
         world.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      if (!level.isClientSide) {
         BlockEntity blockEntity = level.getBlockEntity(blockPos);
         if (blockEntity instanceof MenuProvider) {
            player.openMenu((MenuProvider)blockEntity);
            return InteractionResult.CONSUME;
         }
      }

      return InteractionResult.SUCCESS;
   }

   public static void updateHeatState(Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof LargeCookingPotBlockEntity cookingPotBlockEntity) {
         BlockState currentState = level.getBlockState(pos);
         boolean heated = (Boolean)currentState.getValue(LIT);
         boolean cooking = (Boolean)currentState.getValue(COOKING);
         boolean finished = cookingPotBlockEntity.hasOutputItem();
         LargeCookingPotBlock.CookpotStage stage;
         if (!heated && !finished) {
            stage = LargeCookingPotBlock.CookpotStage.NORMAL;
         } else if (cooking) {
            stage = LargeCookingPotBlock.CookpotStage.COOKING;
         } else if (finished) {
            stage = LargeCookingPotBlock.CookpotStage.FILLED;
         } else {
            stage = LargeCookingPotBlock.CookpotStage.WARM;
         }

         BlockState updatedState = (BlockState)((BlockState)currentState.setValue(STAGE, stage))
            .setValue(COOKING, stage == LargeCookingPotBlock.CookpotStage.COOKING);
         if (!updatedState.equals(currentState)) {
            level.setBlock(pos, updatedState, 3);
         }
      }
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      LargeCookingPotBlock.CookpotStage stage = (LargeCookingPotBlock.CookpotStage)state.getValue(STAGE);
      if (stage != LargeCookingPotBlock.CookpotStage.NORMAL) {
         double centerX = pos.getX() + 0.5;
         double centerY = pos.getY() + 0.7;
         double centerZ = pos.getZ() + 0.5;
         if (stage == LargeCookingPotBlock.CookpotStage.WARM) {
            if (random.nextInt(100) < 18) {
               level.addParticle(ParticleTypes.SMOKE, centerX, centerY + 0.4, centerZ, 0.0, 0.05, 0.0);
            }
         } else if (stage != LargeCookingPotBlock.CookpotStage.COOKING) {
            if (stage == LargeCookingPotBlock.CookpotStage.FILLED) {
               if (random.nextInt(100) < 24) {
                  level.addParticle(ParticleTypes.SMOKE, centerX, centerY + 0.45, centerZ, 0.0, 0.05, 0.0);
               }

               if (random.nextInt(100) < 38) {
                  double offsetX = (random.nextDouble() - 0.5) * 0.3;
                  double offsetZ = (random.nextDouble() - 0.5) * 0.3;
                  level.addParticle((ParticleOptions)FoundationParticles.SOUP_STEAM.get(), centerX + offsetX, centerY + 0.6, centerZ + offsetZ, 0.0, 0.07, 0.0);
               }
            }
         } else {
            if (random.nextInt(100) < 95) {
               int bubbleAmount = 2 + random.nextInt(3);

               for (int index = 0; index < bubbleAmount; index++) {
                  double offsetX = (random.nextDouble() - 0.5) * 0.4;
                  double offsetZ = (random.nextDouble() - 0.5) * 0.4;
                  double bubbleY = centerY + 0.1;
                  level.addParticle((ParticleOptions)FoundationParticles.SOUP_BUBBLE.get(), centerX + offsetX, bubbleY, centerZ + offsetZ, 0.0, 0.0, 0.0);
                  level.addParticle(
                     (ParticleOptions)FoundationParticles.SOUP_COOKING_BUBBLE.get(), centerX + offsetX, bubbleY, centerZ + offsetZ, 0.0, 0.0, 0.0
                  );
               }
            }

            if (random.nextInt(100) < 80) {
               int steamAmount = 2 + random.nextInt(3);

               for (int index = 0; index < steamAmount; index++) {
                  double offsetX = (random.nextDouble() - 0.5) * 0.35;
                  double offsetZ = (random.nextDouble() - 0.5) * 0.35;
                  level.addParticle((ParticleOptions)FoundationParticles.SOUP_STEAM.get(), centerX + offsetX, centerY + 0.3, centerZ + offsetZ, 0.0, 0.08, 0.0);
               }
            }

            if (random.nextInt(100) < 15) {
               level.addParticle(ParticleTypes.SMOKE, centerX, centerY + 0.45, centerZ, 0.0, 0.06, 0.0);
            }

            if (random.nextInt(100) < 6) {
               level.playLocalSound(
                  centerX, centerY, centerZ, (SoundEvent)SoundEventRegistry.COOKING_POT_BOILING.get(), SoundSource.BLOCKS, 0.75F, 0.75F, false
               );
            }
         }
      }
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new LargeCookingPotBlockEntity(pos, state);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof LargeCookingPotBlockEntity) {
            Containers.dropContents(world, pos, ((LargeCookingPotBlockEntity)blockEntity).getItems());
            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return !world.isClientSide ? (lvl, pos, blkState, blockEntity) -> {
         if (blockEntity instanceof LargeCookingPotBlockEntity cookingPot) {
            cookingPot.tick(lvl, pos, blkState, cookingPot);
            updateHeatState(lvl, pos);
         }
      } : null;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }

   public enum CookpotStage implements StringRepresentable {
      NORMAL("normal"),
      WARM("warm"),
      COOKING("cooking"),
      FILLED("filled");

      private final String name;

      CookpotStage(String name) {
         this.name = name;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }

      // $VF: synthetic method
      private static LargeCookingPotBlock.CookpotStage[] $values() {
         return new LargeCookingPotBlock.CookpotStage[]{NORMAL, WARM, COOKING, FILLED};
      }
   }
}
