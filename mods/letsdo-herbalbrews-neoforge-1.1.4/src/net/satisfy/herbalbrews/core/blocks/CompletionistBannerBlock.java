package net.satisfy.herbalbrews.core.blocks;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.herbalbrews.core.blocks.entity.CompletionistBannerEntity;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;
import net.satisfy.herbalbrews.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompletionistBannerBlock extends BaseEntityBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   public static final MapCodec<CompletionistBannerBlock> CODEC = simpleCodec(CompletionistBannerBlock::new);

   public CompletionistBannerBlock(Properties properties) {
      super(properties);
      this.makeDefaultState();
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
      return new CompletionistBannerEntity(blockPos, blockState);
   }

   protected void makeDefaultState() {
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0));
   }

   public boolean canSurvive(@NotNull BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockState belowBlockState = levelReader.getBlockState(blockPos.below());
      return belowBlockState.isSolid();
   }

   public boolean isPossibleToRespawnInThis(@NotNull BlockState blockState) {
      return true;
   }

   @NotNull
   public VoxelShape getShape(
      @NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext
   ) {
      return SHAPE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction clickedFace = context.getClickedFace();
      if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
         return (BlockState)this.defaultBlockState().setValue(ROTATION, Mth.floor((180.0F + context.getRotation()) * 16.0F / 360.0F + 0.5) & 15);
      } else {
         return this == ObjectRegistry.HERBALBREWS_BANNER.get()
            ? (BlockState)((Block)ObjectRegistry.HERBALBREWS_WALL_BANNER.get())
               .defaultBlockState()
               .setValue(HorizontalDirectionalBlock.FACING, clickedFace.getOpposite())
            : this.defaultBlockState();
      }
   }

   @NotNull
   public BlockState rotate(BlockState blockState, Rotation rotation) {
      return (BlockState)blockState.setValue(ROTATION, rotation.rotate((Integer)blockState.getValue(ROTATION), 16));
   }

   @NotNull
   public BlockState mirror(BlockState blockState, Mirror mirror) {
      return (BlockState)blockState.setValue(ROTATION, mirror.mirror((Integer)blockState.getValue(ROTATION), 16));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ROTATION});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide
         ? null
         : createTickerHelper(
            type, (BlockEntityType)EntityTypeRegistry.HERBALBREWS_BANNER.get(), (level1, pos, state1, entity) -> CompletionistBannerEntity.tick(level1, pos)
         );
   }

   @NotNull
   public BlockState updateShape(
      @NotNull BlockState blockState,
      @NotNull Direction direction,
      @NotNull BlockState blockState2,
      @NotNull LevelAccessor levelAccessor,
      @NotNull BlockPos blockPos,
      @NotNull BlockPos blockPos2
   ) {
      return direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
   }

   public ResourceLocation getRenderTexture() {
      return HerbalBrewsIdentifier.identifier("textures/banner/herbalbrews_banner.png");
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      if (PlatformHelper.shouldShowTooltip()) {
         tooltip.add(Component.translatable("tooltip.herbalbrews.banner.thankyou_1").withStyle(style -> style.withColor(TextColor.fromRgb(8364907))));
         tooltip.add(Component.empty());
         tooltip.add(Component.translatable("tooltip.herbalbrews.banner.thankyou_2").withStyle(style -> style.withColor(TextColor.fromRgb(8364907))));
         tooltip.add(Component.translatable("tooltip.herbalbrews.banner.thankyou_4").withStyle(style -> style.withColor(TextColor.fromRgb(8364907))));
         tooltip.add(Component.empty());
         tooltip.add(Component.translatable("tooltip.herbalbrews.banner.thankyou_3").withStyle(style -> style.withColor(TextColor.fromRgb(8364907))));
      }
   }
}
