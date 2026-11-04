package net.satisfy.herbalbrews.client.gui.handler.slot;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;
import org.jetbrains.annotations.NotNull;

public class OutputSlot extends Slot {
   private final Player player;
   private int amount;

   public OutputSlot(Player player, Container inventory, int index, int x, int y) {
      super(inventory, index, x, y);
      this.player = player;
   }

   public boolean mayPlace(ItemStack stack) {
      return false;
   }

   public int getMaxStackSize(ItemStack stack) {
      return 64;
   }

   @NotNull
   public ItemStack remove(int amount) {
      if (this.hasItem()) {
         this.amount = this.amount + Math.min(amount, this.getItem().getCount());
      }

      return super.remove(amount);
   }

   public void onTake(Player player, ItemStack stack) {
      this.checkTakeAchievements(stack);
      super.onTake(player, stack);
   }

   protected void onQuickCraft(ItemStack stack, int amount) {
      this.amount += amount;
      this.checkTakeAchievements(stack);
   }

   protected void checkTakeAchievements(ItemStack stack) {
      stack.onCraftedBy(this.player.level(), this.player, this.amount);
      if (this.player instanceof ServerPlayer && this.container instanceof TeaKettleBlockEntity && this.player.level() instanceof ServerLevel) {
         ((TeaKettleBlockEntity)this.container).dropExperience((ServerLevel)this.player.level(), this.player.position());
      }

      this.amount = 0;
   }
}
