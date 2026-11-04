package net.satisfy.herbalbrews.core.items;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlaskItem extends Item {
   public FlaskItem(Properties properties) {
      super(properties.stacksTo(16));
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.DRINK;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
      return 32;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!stack.has(DataComponents.CUSTOM_MODEL_DATA)) {
         stack.set(DataComponents.CUSTOM_MODEL_DATA, this.newRandomTexture(level, stack));
      }

      player.startUsingItem(hand);
      return InteractionResultHolder.consume(stack);
   }

   @NotNull
   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      if (!level.isClientSide && entity instanceof Player player) {
         this.applyPotionEffects(stack, player);
         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }
      }

      return stack;
   }

   private CustomModelData newRandomTexture(@Nullable Level level, ItemStack stack) {
      if (!stack.has(DataComponents.CUSTOM_MODEL_DATA)) {
         int randomTexture;
         if (level != null) {
            randomTexture = level.getRandom().nextInt(6) + 1;
         } else {
            randomTexture = (int)(Math.random() * 6.0) + 1;
         }

         System.out.println("Set CustomModelData: " + randomTexture);
         return new CustomModelData(randomTexture);
      } else {
         return CustomModelData.DEFAULT;
      }
   }

   private void applyPotionEffects(ItemStack stack, Player player) {
      PotionContents data = (PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      if (data.hasEffects()) {
         data.forEachEffect(player::addEffect);
      }
   }

   public void onCraftedBy(ItemStack stack, Level world, Player player) {
      super.onCraftedBy(stack, world, player);
      stack.set(DataComponents.CUSTOM_MODEL_DATA, this.newRandomTexture(world, stack));
   }

   public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag context) {
      if (stack.has(DataComponents.POTION_CONTENTS)) {
         PotionContents potionContents = (PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
         potionContents.forEachEffect(
            mobEffectInstance -> {
               MutableComponent effectName = Component.translatable(((MobEffect)mobEffectInstance.getEffect().value()).getDescriptionId());
               if (mobEffectInstance.getAmplifier() >= 0) {
                  effectName = effectName.append(" " + this.toRomanNumerals(mobEffectInstance.getAmplifier() + 1));
               }

               if (mobEffectInstance.getDuration() > 20) {
                  effectName = Component.translatable(
                     "potion.withDuration", new Object[]{effectName, MobEffectUtil.formatDuration(mobEffectInstance, 1.0F, tooltipContext.tickRate())}
                  );
               }

               tooltip.add(effectName.withStyle(((MobEffect)mobEffectInstance.getEffect().value()).getCategory().getTooltipFormatting()));
            }
         );
      } else {
         tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
      }

      List<Pair<Attribute, AttributeModifier>> list3 = Lists.newArrayList();
      if (stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
         ItemAttributeModifiers itemAttributeModifiers = (ItemAttributeModifiers)stack.getOrDefault(
            DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY
         );
         itemAttributeModifiers.modifiers().forEach(entry -> {
            Attribute attribute = (Attribute)entry.attribute().value();
            double amount = entry.modifier().amount();
            if (attribute != null) {
               AttributeModifier modifierx = new AttributeModifier(entry.modifier().id(), amount, entry.modifier().operation());
               list3.add(new Pair(attribute, modifierx));
            }
         });
      }

      if (!list3.isEmpty()) {
         tooltip.add(Component.empty());
         tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

         for (Pair<Attribute, AttributeModifier> pair : list3) {
            AttributeModifier modifier = (AttributeModifier)pair.getSecond();
            double d = modifier.amount();
            double e;
            if (modifier.operation() != Operation.ADD_MULTIPLIED_BASE && modifier.operation() != Operation.ADD_MULTIPLIED_TOTAL) {
               e = modifier.amount();
            } else {
               e = modifier.amount() * 100.0;
            }

            if (d > 0.0) {
               tooltip.add(
                  Component.translatable(
                        "attribute.modifier.plus." + modifier.operation().id(),
                        new Object[]{
                           ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(((Attribute)pair.getFirst()).getDescriptionId())
                        }
                     )
                     .withStyle(ChatFormatting.BLUE)
               );
            } else if (d < 0.0) {
               e *= -1.0;
               tooltip.add(
                  Component.translatable(
                        "attribute.modifier.take." + modifier.operation().id(),
                        new Object[]{
                           ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(((Attribute)pair.getFirst()).getDescriptionId())
                        }
                     )
                     .withStyle(ChatFormatting.RED)
               );
            }
         }
      }
   }

   private String toRomanNumerals(int number) {
      if (number >= 1 && number <= 3999) {
         StringBuilder sb = new StringBuilder();
         int[] values = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
         String[] numerals = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

         for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
               number -= values[i];
               sb.append(numerals[i]);
            }
         }

         return sb.toString();
      } else {
         return String.valueOf(number);
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide && !stack.has(DataComponents.CUSTOM_MODEL_DATA)) {
         stack.set(DataComponents.CUSTOM_MODEL_DATA, this.newRandomTexture(world, stack));
      }
   }
}
