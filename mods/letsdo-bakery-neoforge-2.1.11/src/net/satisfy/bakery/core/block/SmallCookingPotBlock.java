package net.satisfy.bakery.core.block;

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
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.bakery.core.block.entity.SmallCookingPotBlockEntity;
import net.satisfy.bakery.core.util.BakeryTiers;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallCookingPotBlock extends BaseEntityBlock {
   public static final MapCodec<SmallCookingPotBlock> CODEC = simpleCodec(SmallCookingPotBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty LIT = BooleanProperty.create("lit");
   public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
   public static final BooleanProperty NEEDS_SUPPORT = BooleanProperty.create("needs_support");
   public static final EnumProperty<SmallCookingPotBlock.CookpotStage> STAGE = EnumProperty.create("stage", SmallCookingPotBlock.CookpotStage.class);
   public static final BakeryTiers COOKING_POT_TIER = BakeryTiers.IRON;
   public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 0, COOKING_POT_TIER.getUses());
   private static final Map<Direction, VoxelShape> SHAPES = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      Supplier<VoxelShape> voxelShapeSupplier = () -> {
         VoxelShape shape = Shapes.empty();
         shape = Shapes.join(shape, Shapes.box(0.25, 0.0, 0.25, 0.75, 0.375, 0.75), BooleanOp.OR);
         return Shapes.join(shape, Shapes.box(-0.125, 0.1875, 0.4375, 0.25, 0.3125, 0.5625), BooleanOp.OR);
      };

      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public SmallCookingPotBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any())
                           .setValue(FACING, Direction.NORTH))
                        .setValue(LIT, false))
                     .setValue(COOKING, false))
                  .setValue(NEEDS_SUPPORT, false))
               .setValue(STAGE, SmallCookingPotBlock.CookpotStage.NORMAL))
            .setValue(DAMAGE, 0)
      );
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, LIT, COOKING, NEEDS_SUPPORT, STAGE, DAMAGE});
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPES.getOrDefault(state.getValue(FACING), Shapes.empty());
   }

   public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      if (!world.isClientSide && (Boolean)state.getValue(NEEDS_SUPPORT)) {
         boolean isSupported = world.getBlockState(pos.below()).isSolidRender(world, pos.below()) || world.getBlockState(pos.below()).is(BlockTags.CAMPFIRES);
         if (!isSupported) {
            for (Direction direction : Plane.HORIZONTAL) {
               BlockPos neighborPos = pos.relative(direction);
               BlockState neighborState = world.getBlockState(neighborPos);
               if (neighborState.getBlock() instanceof SmallCookingPotBlock && (Boolean)neighborState.getValue(NEEDS_SUPPORT)) {
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
      int itemDamage = ctx.getItemInHand().getDamageValue();
      int clampedDamage = Math.min(itemDamage, COOKING_POT_TIER.getUses());
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()))
               .setValue(NEEDS_SUPPORT, needsSupport))
            .setValue(STAGE, SmallCookingPotBlock.CookpotStage.NORMAL))
         .setValue(DAMAGE, clampedDamage);
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
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity pot) {
         if (SmallCookingPotBlockEntity.isJar(stack)) {
            if (!pot.canBottle()) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!level.isClientSide) {
               ItemStack jam = pot.bottle();
               stack.consume(1, player);
               if (!player.getInventory().add(jam)) {
                  player.drop(jam, false);
               }
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         } else if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
         } else {
            return pot.addIngredient(stack, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity pot) {
         if (level.isClientSide) {
            return InteractionResult.SUCCESS;
         }

         if (player.isShiftKeyDown()) {
            return pot.dump(player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
         }

         pot.stir();
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static void updateHeatState(Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity pot) {
         BlockState currentState = level.getBlockState(pos);
         boolean heated = (Boolean)currentState.getValue(LIT);
         SmallCookingPotBlock.CookpotStage stage;
         if (pot.isBottling() || pot.isReady()) {
            stage = SmallCookingPotBlock.CookpotStage.FILLED;
         } else if (heated && pot.isCooking()) {
            stage = SmallCookingPotBlock.CookpotStage.COOKING;
         } else if (heated) {
            stage = SmallCookingPotBlock.CookpotStage.WARM;
         } else {
            stage = SmallCookingPotBlock.CookpotStage.NORMAL;
         }

         BlockState updatedState = (BlockState)((BlockState)currentState.setValue(STAGE, stage))
            .setValue(COOKING, stage == SmallCookingPotBlock.CookpotStage.COOKING);
         if (!updatedState.equals(currentState)) {
            level.setBlock(pos, updatedState, 3);
         }
      }
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      SmallCookingPotBlock.CookpotStage stage = (SmallCookingPotBlock.CookpotStage)state.getValue(STAGE);
      if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity burntPot && burntPot.isBurnt()) {
         if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 0.0, 0.03, 0.0);
         }
      } else if (stage != SmallCookingPotBlock.CookpotStage.NORMAL) {
         double centerX = pos.getX() + 0.5;
         double centerY = pos.getY() + 0.7;
         double centerZ = pos.getZ() + 0.5;
         if (stage == SmallCookingPotBlock.CookpotStage.WARM) {
            if (random.nextInt(100) < 18) {
               level.addParticle(ParticleTypes.SMOKE, centerX, centerY + 0.4, centerZ, 0.0, 0.05, 0.0);
            }
         } else if (stage != SmallCookingPotBlock.CookpotStage.COOKING) {
            if (stage == SmallCookingPotBlock.CookpotStage.FILLED) {
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
            if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity pot && pot.isStirDue()) {
               for (int index = 0; index < 3; index++) {
                  double offsetX = (random.nextDouble() - 0.5) * 0.3;
                  double offsetZ = (random.nextDouble() - 0.5) * 0.3;
                  level.addParticle(
                     jamColor(pot, (ParticleType<ColorParticleOption>)FoundationParticles.COLORED_SOUP_BUBBLE.get()),
                     centerX + offsetX,
                     pos.getY() + 0.32,
                     centerZ + offsetZ,
                     0.0,
                     0.04,
                     0.0
                  );
               }

               if (random.nextInt(4) == 0) {
                  level.playLocalSound(
                     centerX, centerY, centerZ, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.6F, 0.7F + random.nextFloat() * 0.3F, false
                  );
               }
            }

            if (random.nextInt(100) < 95) {
               int bubbleAmount = 2 + random.nextInt(3);

               for (int index = 0; index < bubbleAmount; index++) {
                  double offsetX = (random.nextDouble() - 0.5) * 0.4;
                  double offsetZ = (random.nextDouble() - 0.5) * 0.4;
                  double bubbleY = centerY - 0.2;
                  if (level.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity jam) {
                     level.addParticle(
                        jamColor(jam, (ParticleType<ColorParticleOption>)FoundationParticles.COLORED_SOUP_BUBBLE.get()),
                        centerX + offsetX,
                        bubbleY,
                        centerZ + offsetZ,
                        0.0,
                        0.0,
                        0.0
                     );
                     level.addParticle(
                        jamColor(jam, (ParticleType<ColorParticleOption>)FoundationParticles.COLORED_SOUP_COOKING_BUBBLE.get()),
                        centerX + offsetX,
                        bubbleY,
                        centerZ + offsetZ,
                        0.0,
                        0.0,
                        0.0
                     );
                  }
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

   private static ColorParticleOption jamColor(SmallCookingPotBlockEntity pot, ParticleType<ColorParticleOption> type) {
      return ColorParticleOption.create(type, 0xFF000000 | pot.getDisplayColor());
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new SmallCookingPotBlockEntity(pos, state);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (world.getBlockEntity(pos) instanceof SmallCookingPotBlockEntity cookingPotBlockEntity) {
            cookingPotBlockEntity.getDrops().forEach(drop -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), drop));
            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return !world.isClientSide ? (lvl, pos, blkState, blockEntity) -> {
         if (blockEntity instanceof SmallCookingPotBlockEntity cookingPot) {
            SmallCookingPotBlockEntity.serverTick(lvl, pos, blkState, cookingPot);
            updateHeatState(lvl, pos);
         }
      } : (lvl, pos, blkState, blockEntity) -> {
         if (blockEntity instanceof SmallCookingPotBlockEntity cookingPot) {
            SmallCookingPotBlockEntity.clientTick(lvl, pos, blkState, cookingPot);
         }
      };
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
      private static SmallCookingPotBlock.CookpotStage[] $values() {
         return new SmallCookingPotBlock.CookpotStage[]{NORMAL, WARM, COOKING, FILLED};
      }
   }
}
