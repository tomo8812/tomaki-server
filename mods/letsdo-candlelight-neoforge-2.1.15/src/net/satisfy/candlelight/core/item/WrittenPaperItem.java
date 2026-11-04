package net.satisfy.candlelight.core.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.satisfy.candlelight.core.util.CandlelightUtil;
import org.jetbrains.annotations.NotNull;

public class WrittenPaperItem extends Item {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !WrittenPaperItem.class.desiredAssertionStatus();

   public WrittenPaperItem(Properties settings) {
      super(settings);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
      ItemStack itemStack = user.getItemInHand(hand);
      if (world.isClientSide()) {
         CandlelightUtil.setSignedPaperScreen(itemStack);
      } else {
         user.containerMenu.broadcastChanges();
      }

      user.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
   }

   @NotNull
   public Component getName(ItemStack stack) {
      CompoundTag nbtCompound = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (nbtCompound != null) {
         String string = nbtCompound.getString("title");
         if (!StringUtil.isNullOrEmpty(string)) {
            return Component.literal(string);
         }
      }

      return super.getName(stack);
   }

   public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      if (stack.has(DataComponents.CUSTOM_DATA)) {
         CompoundTag nbtCompound = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         assert nbtCompound != null;
         String string = nbtCompound.getString("author");
         if (!StringUtil.isNullOrEmpty(string)) {
            tooltip.add(Component.translatable("book.byAuthor", new Object[]{string}).withStyle(ChatFormatting.GRAY));
         }
      }
   }
}
