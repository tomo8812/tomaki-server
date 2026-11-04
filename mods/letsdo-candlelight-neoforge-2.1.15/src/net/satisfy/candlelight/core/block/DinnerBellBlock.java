package net.satisfy.candlelight.core.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.core.block.entity.DinnerBellBlockEntity;
import net.satisfy.candlelight.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DinnerBellBlock extends BaseEntityBlock {
   public static final MapCodec<DinnerBellBlock> CODEC = simpleCodec(DinnerBellBlock::new);

   public DinnerBellBlock(Properties properties) {
      super(properties);
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
      VoxelShape bottom = Block.box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0);
      VoxelShape top = Block.box(5.0, 2.0, 5.0, 11.0, 5.0, 11.0);
      return Shapes.or(bottom, top);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
      if (!level.isClientSide) {
         level.playSound(null, pos, (SoundEvent)SoundEventRegistry.DINNER_BELL_RING.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
         level.blockEvent(pos, this, 1, 0);
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new DinnerBellBlockEntity(pos, state);
   }

   @Nullable
   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
