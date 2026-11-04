package net.satisfy.brewery.core.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.satisfy.brewery.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

public class PacifyEffect extends MobEffect {
   public PacifyEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   @NotNull
   public MobEffect addAttributeModifier(Holder<Attribute> holder, ResourceLocation id, double amount, Operation op) {
      return super.addAttributeModifier(holder, id, amount, op);
   }

   public void removeAttributeModifiers(AttributeMap map) {
      super.removeAttributeModifiers(map);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player) {
         player.level()
            .getEntitiesOfClass(EnderMan.class, player.getBoundingBox().inflate(PlatformHelper.getPacifyEffectRange()))
            .forEach(e -> e.setTarget(null));
      }

      return true;
   }

   public void onMobRemoved(LivingEntity entity, int amplifier, RemovalReason reason) {
   }
}
