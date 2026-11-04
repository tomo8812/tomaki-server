package net.satisfy.brewery.core.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import net.satisfy.brewery.platform.PlatformHelper;

public class IntoxicationEffect extends MobEffect {
   private static final ResourceLocation SPEED_ID = Brewery.identifier("effect.intoxication.speed");
   private static final int BLACKOUT_DURATION = 240;

   public IntoxicationEffect() {
      super(MobEffectCategory.HARMFUL, 10251324);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_ID, 0.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      int lvl = Math.max(0, amplifier);
      if (lvl >= 5
         && PlatformHelper.isBlackoutEnabled()
         && !entity.level().isClientSide()
         && entity.getRandom().nextDouble() < PlatformHelper.getBlackoutChance() / 100.0) {
         Holder<MobEffect> intox = BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.DRUNK.get());
         Holder<MobEffect> blackout = BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.BLACKOUT.get());
         entity.removeEffect(intox);
         entity.addEffect(new MobEffectInstance(blackout, 240, 0, false, false));
         return true;
      }

      if (lvl > 1 && PlatformHelper.isDrunkSwayEnabled()) {
         if (entity.level().isClientSide()) {
            float t = entity.tickCount * 0.03F;
            float f = 0.45F + 0.15F * lvl;
            float yaw = Mth.sin(t * f) * (0.3F + 0.35F * lvl);
            float pitch = Mth.cos(t * (f * 0.85F)) * (0.12F + 0.18F * lvl);
            entity.setYRot(entity.getYRot() + yaw * 0.5F);
            entity.setXRot(Mth.clamp(entity.getXRot() + pitch * 0.5F, -90.0F, 90.0F));
            entity.yRotO = Mth.lerp(0.6F, entity.yRotO, entity.getYRot());
            entity.xRotO = Mth.lerp(0.6F, entity.xRotO, entity.getXRot());
         } else {
            int interval = Math.max(18 - lvl * 2, 6);
            if (entity.tickCount % interval == 0) {
               double s = 0.01 * lvl;
               double dx = (entity.getRandom().nextDouble() - 0.5) * s;
               double dz = (entity.getRandom().nextDouble() - 0.5) * s;
               entity.push(dx, 0.0, dz);
            }
         }
      }

      if (lvl >= 4 && !entity.level().isClientSide() && entity.tickCount % 40 == 0) {
         entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false, false));
      }

      return true;
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
      AttributeInstance inst = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
      if (inst != null) {
         inst.removeModifier(SPEED_ID);
         double amount = amplifier > 1 && PlatformHelper.isDrunkSlownessEnabled() ? -0.05 * (amplifier - 1) : 0.0;
         if (amount != 0.0) {
            inst.addPermanentModifier(new AttributeModifier(SPEED_ID, amount, Operation.ADD_MULTIPLIED_TOTAL));
         }
      }
   }

   public void removeAttributeModifiers(AttributeMap attributeMap) {
      AttributeInstance inst = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
      if (inst != null) {
         inst.removeModifier(SPEED_ID);
      }
   }
}
