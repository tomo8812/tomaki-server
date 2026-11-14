package org.millenaire.block.mock;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.millenaire.building.SpecialPoint;

public abstract class MockPayloadBlock extends MockBlock implements EntityBlock {
   protected MockPayloadBlock(Properties properties) {
      super(properties);
   }

   public abstract String pointType();

   @Nullable
   public abstract String payloadFor(ItemStack var1);

   @Nullable
   public String payloadFor(ItemStack stack, BlockPlaceContext context) {
      return this.payloadFor(stack);
   }

   public boolean authorsWhileSneaking() {
      return true;
   }

   public abstract String acceptedItemsKey();

   public abstract ItemStack displayStack(String var1);

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new MockPayloadBlockEntity(pos, state);
   }

   @Override
   public SpecialPoint toSpecialPoint(BlockState state, BlockPos pos) {
      return new SpecialPoint(this.pointType(), null, null, pos);
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      ItemStack stack = this.variantProperty() != null ? super.getCloneItemStack(level, pos, state) : new ItemStack(this);
      if (level.getBlockEntity(pos) instanceof MockPayloadBlockEntity be && !be.getPayload().isEmpty()) {
         be.saveToItem(stack, level.registryAccess());
      }

      return stack;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      String payload = payloadOf(stack);
      if (payload.isEmpty()) {
         tooltip.add(Component.translatable("block.millenaire.mock_generic.tooltip.not_set").withStyle(ChatFormatting.GRAY));
      } else {
         tooltip.add(Component.translatable("block.millenaire.mock_generic.tooltip.payload", new Object[]{payload}).withStyle(ChatFormatting.GRAY));
      }

      tooltip.add(Component.translatable(this.acceptedItemsKey()).withStyle(ChatFormatting.DARK_GRAY));
   }

   public static String payloadOf(ItemStack stack) {
      CustomData data = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
      return data == null ? "" : data.copyTag().getString("payload");
   }
}
