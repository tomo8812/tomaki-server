package net.satisfy.candlelight.core.item;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.candlelight.core.block.TableSetBlock;
import net.satisfy.candlelight.core.block.entity.TableSetBlockEntity;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class WineGlassItem extends BlockItem {
   public static final int RAISE_TICKS = 8;
   public static final int DRINK_TICKS = 40;
   private static final String TABLE_KEY = "CandlelightTableSet";
   private static final String TIME_KEY = "CandlelightBorrowedAt";
   private static final String FILLED_KEY = "CandlelightFilled";
   private static final long RETURN_GRACE_TICKS = 10L;
   private static final double TOAST_RANGE = 3.5;
   private static final Map<UUID, Long> LAST_TOAST = new WeakHashMap<>();

   public WineGlassItem(Block block, Properties settings) {
      super(block, settings);
   }

   public static ItemStack borrowedFrom(BlockPos tablePos, long gameTime, boolean filled) {
      ItemStack stack = new ItemStack((ItemLike)ObjectRegistry.WINE_GLASS.get());
      CompoundTag tag = new CompoundTag();
      tag.putLong("CandlelightTableSet", tablePos.asLong());
      tag.putLong("CandlelightBorrowedAt", gameTime);
      tag.putBoolean("CandlelightFilled", filled);
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      return stack;
   }

   public static boolean isBorrowed(ItemStack stack) {
      return stack.is((Item)ObjectRegistry.WINE_GLASS.get())
         && ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).contains("CandlelightTableSet");
   }

   public static boolean isFilled(ItemStack stack) {
      return isBorrowed(stack) && ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getBoolean("CandlelightFilled");
   }

   public static boolean isRaising(LivingEntity entity) {
      return entity.isUsingItem() && isBorrowed(entity.getUseItem());
   }

   @NotNull
   public InteractionResult useOn(UseOnContext context) {
      return isBorrowed(context.getItemInHand()) ? InteractionResult.PASS : super.useOn(context);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      return isBorrowed(player.getItemInHand(hand)) ? ItemUtils.startUsingInstantly(level, player, hand) : super.use(level, player, hand);
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.NONE;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return isBorrowed(stack) ? 72000 : super.getUseDuration(stack, entity);
   }

   public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
      if (!level.isClientSide() && isBorrowed(stack)) {
         int ticks = entity.getTicksUsingItem();
         if (ticks == 8) {
            tryToast(level, entity);
         }

         if (ticks >= 40 && isFilled(stack)) {
            drinkFromTable(stack, level, entity);
            entity.stopUsingItem();
            returnToTable(stack, level, entity);
         }
      }
   }

   private static void tryToast(Level level, LivingEntity entity) {
      long now = level.getGameTime();

      for (Player other : level.getEntitiesOfClass(
         Player.class, entity.getBoundingBox().inflate(3.5), p -> p != entity && isRaising(p) && p.getTicksUsingItem() >= 8
      )) {
         if (now - LAST_TOAST.getOrDefault(other.getUUID(), -100L) >= 40L) {
            LAST_TOAST.put(entity.getUUID(), now);
            LAST_TOAST.put(other.getUUID(), now);
            Vec3 middle = entity.getEyePosition().add(other.getEyePosition()).scale(0.5);
            level.playSound(
               null, middle.x, middle.y, middle.z, SoundEvents.AMETHYST_CLUSTER_HIT, SoundSource.PLAYERS, 0.9F, 1.7F + level.random.nextFloat() * 0.2F
            );
            level.playSound(null, middle.x, middle.y, middle.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7F, 1.5F);
            if (level instanceof ServerLevel serverLevel) {
               serverLevel.sendParticles(ParticleTypes.HEART, middle.x, middle.y + 0.2, middle.z, 3, 0.25, 0.15, 0.25, 0.0);
            }

            return;
         }
      }
   }

   private static void drinkFromTable(ItemStack stack, Level level, LivingEntity entity) {
      BlockPos tablePos = tablePos(stack);
      level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
      if (level.getBlockEntity(tablePos) instanceof TableSetBlockEntity tableSet) {
         ItemStack var9 = tableSet.getDrink(true);
         tableSet.setDrink(true, ItemStack.EMPTY);
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         tag.putBoolean("CandlelightFilled", false);
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         if (!var9.isEmpty() && entity instanceof Player player) {
            ItemStack remainder = var9.getItem().finishUsingItem(var9.copy(), level, player);
            if (!remainder.isEmpty() && !ItemStack.isSameItem(remainder, var9) && !player.getInventory().add(remainder)) {
               player.drop(remainder, false);
            }
         }
      }
   }

   public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
      if (!level.isClientSide() && isBorrowed(stack)) {
         returnToTable(stack, level, entity);
      }
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
      if (!level.isClientSide() && isBorrowed(stack) && entity instanceof LivingEntity living) {
         boolean raising = living.isUsingItem() && living.getUseItem() == stack;
         long borrowedAt = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getLong("CandlelightBorrowedAt");
         if (!raising && level.getGameTime() - borrowedAt > 10L) {
            returnToTable(stack, level, living);
         }
      }
   }

   private static BlockPos tablePos(ItemStack stack) {
      return BlockPos.of(((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getLong("CandlelightTableSet"));
   }

   private static void returnToTable(ItemStack stack, Level level, LivingEntity entity) {
      if (!stack.isEmpty()) {
         BlockPos tablePos = tablePos(stack);
         BlockState table = level.getBlockState(tablePos);
         stack.shrink(1);
         if (table.getBlock() instanceof TableSetBlock && !(Boolean)table.getValue(TableSetBlock.WINE_GLASS)) {
            boolean filled = level.getBlockEntity(tablePos) instanceof TableSetBlockEntity tableSet && !tableSet.getDrink(true).isEmpty();
            level.setBlockAndUpdate(
               tablePos, (BlockState)((BlockState)table.setValue(TableSetBlock.WINE_GLASS, true)).setValue(TableSetBlock.WINE_GLASS_DRINK, filled)
            );
            level.playSound(null, tablePos, SoundEvents.GLASS_STEP, SoundSource.BLOCKS, 0.5F, 1.6F);
         } else if (entity instanceof Player player && !player.getInventory().add(new ItemStack((ItemLike)ObjectRegistry.WINE_GLASS.get()))) {
            player.drop(new ItemStack((ItemLike)ObjectRegistry.WINE_GLASS.get()), false);
         }
      }
   }
}
