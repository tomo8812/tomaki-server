package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.WaterSprinklerBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.registry.FoundationParticles;
import org.jetbrains.annotations.NotNull;

public class WaterSprinklerBlock extends BaseEntityBlock {
   public static final MapCodec<WaterSprinklerBlock> CODEC = simpleCodec(WaterSprinklerBlock::new);
   private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 12.0, 15.0);
   public static final EnumProperty<SprinklerPressure> PRESSURE = EnumProperty.create("pressure", SprinklerPressure.class);

   public WaterSprinklerBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(PRESSURE, SprinklerPressure.STEADY));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PRESSURE});
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (!level.isClientSide) {
         level.setBlock(pos, (BlockState)state.cycle(PRESSURE), 3);
         level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.4F, 1.2F);
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @NotNull
   public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return SHAPE;
   }

   public void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
      if (!world.isClientSide) {
         world.scheduleTick(pos, this, 1);
      }
   }

   public void animateTick(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull RandomSource random) {
      if (!world.isRaining() && !world.isThundering() && world.isClientSide && world.getBlockEntity(pos) instanceof WaterSprinklerBlockEntity sprinkler) {
         float angle = sprinkler.getRotationAngle();
         boolean high = state.getValue(PRESSURE) == SprinklerPressure.HIGH;
         double x = pos.getX() + 0.5;
         double y = pos.getY() + 1.0;
         double z = pos.getZ() + 0.5;
         double velocity = high ? 0.3 : 0.2;
         double reach = high ? 4.5 : 3.0;
         double startOffset = 0.5;

         for (int i = 0; i < 4; i++) {
            double a = Math.toRadians(angle + 90 * i);
            double cos = Math.cos(a);
            double sin = Math.sin(a);
            double dx = cos * velocity;
            double dz = sin * velocity;
            double startX = x + cos * startOffset;
            double startZ = z + sin * startOffset;

            for (double len = 0.0; len < reach; len += 0.5) {
               double cx = startX + dx * len;
               double cz = startZ + dz * len;
               world.addParticle((ParticleOptions)FoundationParticles.WATER_SPLASH.get(), cx, y, cz, dx, 0.0, dz);
            }
         }
      }
   }

   public void tick(@NotNull BlockState state, ServerLevel world, BlockPos pos, @NotNull RandomSource random) {
      int range = PlatformHelper.getWaterSprinklerRange();
      BlockPos.betweenClosed(pos.offset(-range, -1, -range), pos.offset(range, 1, range)).forEach(p -> {
         BlockState blockState = world.getBlockState(p);
         if (blockState.getBlock() instanceof FarmBlock && blockState.hasProperty(BlockStateProperties.MOISTURE)) {
            world.setBlock(p, (BlockState)blockState.setValue(BlockStateProperties.MOISTURE, 7), 2);
         }

         if (blockState.is(Blocks.FIRE)) {
            world.removeBlock(p, false);
            world.playSound(null, p, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
         }

         if ((blockState.is(Blocks.CAMPFIRE) || blockState.is(Blocks.SOUL_CAMPFIRE)) && (Boolean)blockState.getValue(CampfireBlock.LIT)) {
            world.setBlock(p, (BlockState)blockState.setValue(CampfireBlock.LIT, false), 3);
            world.playSound(null, p, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
         }

         if (blockState.getBlock() instanceof CandleBlock && (Boolean)blockState.getValue(CandleBlock.LIT)) {
            world.setBlock(p, (BlockState)blockState.setValue(CandleBlock.LIT, false), 3);
            world.playSound(null, p, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
         }
      });
      world.playSound(null, pos, (SoundEvent)SoundEventRegistry.WATER_SPRINKLER.get(), SoundSource.BLOCKS, 0.25F, 0.75F);
      world.scheduleTick(pos, this, 20);
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new WaterSprinklerBlockEntity(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
      return level.isClientSide
         ? createTickerHelper(type, (BlockEntityType)EntityTypeRegistry.SPRINKLER_BLOCK_ENTITY.get(), WaterSprinklerBlockEntity::clientTick)
         : null;
   }

   @NotNull
   public RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      int earthy = 16767334;
      int gold = 16766720;
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.translatable("tooltip.farm_and_charm.water_sprinkler.info_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
      } else {
         tooltip.add(
            Component.translatable(
                  "tooltip.farm_and_charm.tooltip_information.hold",
                  new Object[]{Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
         );
      }
   }
}
