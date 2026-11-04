package net.satisfy.candlelight.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.candlelight.core.block.TableSetBlock;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

public class NapkinItem extends Item {
   private static final int DAB_SOUND_INTERVAL = 12;
   private static final String TABLE_KEY = "CandlelightTableSet";
   private static final String TIME_KEY = "CandlelightBorrowedAt";
   private static final long RETURN_GRACE_TICKS = 10L;

   public NapkinItem(Properties settings) {
      super(settings);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      return ItemUtils.startUsingInstantly(level, player, hand);
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.NONE;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public static ItemStack borrowedFrom(BlockPos tablePos, long gameTime) {
      ItemStack stack = new ItemStack((ItemLike)ObjectRegistry.NAPKIN.get());
      CompoundTag tag = new CompoundTag();
      tag.putLong("CandlelightTableSet", tablePos.asLong());
      tag.putLong("CandlelightBorrowedAt", gameTime);
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      return stack;
   }

   public static boolean isBorrowed(ItemStack stack) {
      return stack.is((Item)ObjectRegistry.NAPKIN.get())
         && ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).contains("CandlelightTableSet");
   }

   public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
      if (!level.isClientSide() && isBorrowed(stack)) {
         returnToTable(stack, level, entity);
      }
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
      if (!level.isClientSide() && isBorrowed(stack) && entity instanceof LivingEntity living) {
         boolean dabbing = living.isUsingItem() && living.getUseItem() == stack;
         long borrowedAt = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getLong("CandlelightBorrowedAt");
         if (!dabbing && level.getGameTime() - borrowedAt > 10L) {
            returnToTable(stack, level, living);
         }
      }
   }

   private static void returnToTable(ItemStack stack, Level level, LivingEntity entity) {
      BlockPos tablePos = BlockPos.of(((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getLong("CandlelightTableSet"));
      BlockState table = level.getBlockState(tablePos);
      stack.shrink(1);
      if (table.getBlock() instanceof TableSetBlock && !(Boolean)table.getValue(TableSetBlock.NAPKIN)) {
         level.setBlockAndUpdate(tablePos, (BlockState)table.setValue(TableSetBlock.NAPKIN, true));
      } else if (entity instanceof Player player && !player.getInventory().add(new ItemStack((ItemLike)ObjectRegistry.NAPKIN.get()))) {
         player.drop(new ItemStack((ItemLike)ObjectRegistry.NAPKIN.get()), false);
      }
   }

   public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
      if (PlatformHelper.enableNapkinSound() && !level.isClientSide() && remainingUseDuration % 12 == 0) {
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WOOL_HIT, SoundSource.PLAYERS, 0.4F, 1.4F + level.random.nextFloat() * 0.2F
         );
      }
   }
}
