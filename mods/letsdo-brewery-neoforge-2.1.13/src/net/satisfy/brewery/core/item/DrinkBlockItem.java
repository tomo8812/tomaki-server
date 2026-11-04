package net.satisfy.brewery.core.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.registry.ArmorSetRegistry;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.platform.PlatformHelper;
import net.satisfy.foundation.food.PlaceableDrinkItem;
import org.jetbrains.annotations.NotNull;

public class DrinkBlockItem extends PlaceableDrinkItem {
   private final MobEffect effect;
   private final int baseDuration;
   private static final int MAX_QUALITY = 3;
   private static final ResourceLocation QUALITY_FONT = Brewery.identifier("quality");
   private static final String QUALITY_FULL = "\ue000";
   private static final String QUALITY_EMPTY = "\ue001";
   private static final String QUALITY_AGED = "\ue002";
   public static final int AGED_QUALITY = 4;

   public DrinkBlockItem(MobEffect effect, int duration, Block block, Properties settings) {
      super(block, settings);
      this.crouchToPlace().drinkAnytime();
      this.effect = effect;
      this.baseDuration = duration;
   }

   public static boolean isBottled(ItemStack stack) {
      return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().startsWith("whiskey_");
   }

   public static Item getContainer(ItemStack stack) {
      return isBottled(stack) ? Items.GLASS_BOTTLE : ((Block)ObjectRegistry.BEER_MUG.get()).asItem();
   }

   public static void addQuality(ItemStack itemStack, int quality) {
      CustomData customData = (CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      CompoundTag tag = customData.copyTag();
      tag.putInt("brewery.beer_quality", Mth.clamp(quality, 0, 4));
      itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
   }

   public static int getQuality(ItemStack itemStack) {
      CustomData customData = (CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      return customData.contains("brewery.beer_quality") ? customData.copyTag().getInt("brewery.beer_quality") : 3;
   }

   protected Item containerFor(ItemStack stack) {
      return getContainer(stack);
   }

   protected void applyEffects(ItemStack itemStack, Level level, LivingEntity livingEntity) {
      if (livingEntity instanceof ServerPlayer serverPlayer) {
         int quality = getQuality(itemStack);
         MobEffectInstance mainEffect = this.calculateEffectForQuality(quality);
         Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this.effect);
         MobEffectInstance current = serverPlayer.getEffect(holder);
         int currentAmp = current != null ? current.getAmplifier() : -1;
         int newAmp = Mth.clamp(Math.max(mainEffect.getAmplifier(), currentAmp + 1), 0, 5);
         serverPlayer.addEffect(new MobEffectInstance(holder, mainEffect.getDuration(), newAmp, false, false));
         if (PlatformHelper.isDrunkennessEnabled() && !ArmorSetRegistry.hasHarddrinking(serverPlayer)) {
            Holder<MobEffect> drunkHolder = MobEffectRegistry.holder(MobEffectRegistry.DRUNK);
            MobEffectInstance drunkCurrent = serverPlayer.getEffect(drunkHolder);
            int drunkAmp = drunkCurrent != null ? drunkCurrent.getAmplifier() : -1;
            int newDrunkAmp = Mth.clamp(drunkAmp + 1, 0, 5);
            int min = 1800;
            int max;
            if (quality <= 1) {
               max = 9600;
            } else if (quality == 2) {
               max = 6000;
            } else if (quality >= 4) {
               max = 2400;
            } else {
               max = 3600;
            }

            int drunkDuration = Mth.nextInt(level.getRandom(), min, max);
            serverPlayer.addEffect(new MobEffectInstance(drunkHolder, drunkDuration, newDrunkAmp, false, false));
         }
      }
   }

   @NotNull
   private MobEffectInstance calculateEffectForQuality(int quality) {
      int durationMultiplier;
      int amplifier;
      switch (quality) {
         case 0:
            durationMultiplier = 0;
            amplifier = 0;
            break;
         case 1:
         default:
            durationMultiplier = 1;
            amplifier = 0;
            break;
         case 2:
            durationMultiplier = 3;
            amplifier = 1;
            break;
         case 3:
            durationMultiplier = 5;
            amplifier = 2;
            break;
         case 4:
            durationMultiplier = 7;
            amplifier = 3;
      }

      int duration = quality == 0 ? this.baseDuration / 3 : this.baseDuration * durationMultiplier;
      return new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this.effect), duration, amplifier, false, false);
   }

   public void addCount(ItemStack resultSack, int solved) {
      resultSack.setCount(solved);
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 32;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      int beerQuality = 3;
      CustomData customData = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      if (customData.contains("brewery.beer_quality")) {
         beerQuality = customData.copyTag().getInt("brewery.beer_quality");
         String mugs = beerQuality >= 4 ? "\ue000".repeat(3) + "\ue002" : "\ue000".repeat(beerQuality) + "\ue001".repeat(Math.max(0, 3 - beerQuality));
         Component icons = Component.literal(mugs).withStyle(Style.EMPTY.withFont(QUALITY_FONT).withColor(ChatFormatting.WHITE));
         tooltip.add(Component.translatable("tooltip.brewery.beer_quality", new Object[]{icons}).withStyle(ChatFormatting.GOLD));
      }

      MobEffectInstance instance = this.calculateEffectForQuality(beerQuality);
      if (this.effect != null) {
         MutableComponent effectName = Component.translatable(this.effect.getDescriptionId());
         if (instance.getAmplifier() > 0) {
            effectName.append(" ").append(Component.translatable("potion.potency." + instance.getAmplifier()));
         }

         MutableComponent line = instance.getDuration() > 20
            ? Component.translatable("potion.withDuration", new Object[]{effectName, MobEffectUtil.formatDuration(instance, 1.0F, context.tickRate())})
            : effectName;
         tooltip.add(line.withStyle(this.effect.getCategory().getTooltipFormatting()));
      } else {
         tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
      }
   }
}
