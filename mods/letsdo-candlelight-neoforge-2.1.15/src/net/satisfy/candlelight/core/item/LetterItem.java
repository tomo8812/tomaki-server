package net.satisfy.candlelight.core.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.satisfy.candlelight.client.gui.handler.LetterGuiHandler;
import org.jetbrains.annotations.NotNull;

public class LetterItem extends Item {
   public LetterItem(Properties settings) {
      super(settings);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
      ItemStack itemStack = user.getItemInHand(hand);
      if (!world.isClientSide()) {
         MenuProvider screenHandlerFactory = new MenuProvider() {
            @NotNull
            public Component getDisplayName() {
               return Component.literal("");
            }

            @NotNull
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
               return new LetterGuiHandler(syncId, inv);
            }
         };
         user.openMenu(screenHandlerFactory);
      }

      return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("item.candlelight.letter.tooltip").withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY}));
   }
}
