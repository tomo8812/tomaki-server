package fuzs.goldenagecombat.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {
   protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(
      method = "hurt",
      at = @At(value = "RETURN", ordinal = 0),
      slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;scalesWithDifficulty()Z")),
      cancellable = true
   )
   public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
      if (((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).weakAttacksKnockBackPlayers) {
         if (amount == 0.0F && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            callback.setReturnValue(super.hurt(source, amount));
         }
      }
   }

   @Inject(method = "attack", at = @At("HEAD"))
   public void attack$0(Entity target, CallbackInfo callback, @Share("sprints_during_attack") LocalBooleanRef sprintsDuringAttack) {
      sprintsDuringAttack.set(this.isSprinting());
   }

   @Inject(
      method = "attack",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V",
         ordinal = 0,
         shift = Shift.AFTER
      )
   )
   public void attack$1(Entity target, CallbackInfo callback) {
      if (((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).criticalHitsWhileSprinting) {
         this.setSharedFlag(3, false);
      }
   }

   @Inject(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;walkDist:F"))
   public void attack$2(Entity target, CallbackInfo callback, @Share("sprints_during_attack") LocalBooleanRef sprintsDuringAttack) {
      if (sprintsDuringAttack.get()) {
         this.setSharedFlag(3, true);
      }
   }

   @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V", shift = Shift.AFTER))
   public void attack$3(Entity target, CallbackInfo callback, @Share("sprints_during_attack") LocalBooleanRef sprintsDuringAttack) {
      if (((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).sprintAttacks && sprintsDuringAttack.get()) {
         this.setSprinting(true);
      }
   }

   @ModifyVariable(
      method = "attack",
      at = @At("LOAD"),
      ordinal = 3,
      slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V"))
   )
   public boolean attack$4(boolean triggerSweepAttack, Entity target) {
      return !((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).requireSweepingEdge
         ? triggerSweepAttack
         : triggerSweepAttack && this.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) > 0.0;
   }

   @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
   public void getAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> callback) {
      if (((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).removeAttackCooldown) {
         callback.setReturnValue(1.0F);
      }
   }
}
