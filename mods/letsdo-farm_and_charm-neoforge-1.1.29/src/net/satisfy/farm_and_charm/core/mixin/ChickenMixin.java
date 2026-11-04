package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Chicken;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenGotoAndEnterCoopGoal;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenLocateCoopGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public class ChickenMixin implements ChickenCoopAccess {
   @Unique
   private BlockPos farmAndCharm$coopTarget;
   @Unique
   private int farmAndCharm$coopCooldown = 0;

   @Override
   public BlockPos farmAndCharm$getCoopTarget() {
      return this.farmAndCharm$coopTarget;
   }

   @Override
   public void farmAndCharm$setCoopTarget(BlockPos pos) {
      this.farmAndCharm$coopTarget = pos;
   }

   @Override
   public void farmAndCharm$clearCoopTarget() {
      this.farmAndCharm$coopTarget = null;
   }

   @Override
   public boolean farmAndCharm$hasCoopTarget() {
      return this.farmAndCharm$coopTarget != null;
   }

   @Override
   public int farmAndCharm$getCoopCooldown() {
      return this.farmAndCharm$coopCooldown;
   }

   @Override
   public void farmAndCharm$setCoopCooldown(int cooldown) {
      this.farmAndCharm$coopCooldown = cooldown;
   }

   @Inject(method = "aiStep()V", at = @At("HEAD"))
   private void farmAndCharm$tickCoopCooldown(CallbackInfo ci) {
      if (this.farmAndCharm$coopCooldown > 0) {
         this.farmAndCharm$coopCooldown--;
      }
   }

   @Inject(method = "registerGoals()V", at = @At("TAIL"))
   private void addCustomGoals(CallbackInfo ci) {
      Chicken chicken = (Chicken)this;
      GoalSelector goalSelector = ((MobAccessor)chicken).farmAndCharm$getGoalSelector();
      goalSelector.addGoal(8, new ChickenLocateCoopGoal(chicken));
      goalSelector.addGoal(9, new ChickenGotoAndEnterCoopGoal(chicken));
   }

   @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
   private void farmAndCharm$saveCoopData(CompoundTag tag, CallbackInfo ci) {
      tag.putInt("CoopCooldown", this.farmAndCharm$coopCooldown);
      if (this.farmAndCharm$coopTarget != null) {
         tag.putInt("CoopTargetX", this.farmAndCharm$coopTarget.getX());
         tag.putInt("CoopTargetY", this.farmAndCharm$coopTarget.getY());
         tag.putInt("CoopTargetZ", this.farmAndCharm$coopTarget.getZ());
      }
   }

   @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
   private void farmAndCharm$loadCoopData(CompoundTag tag, CallbackInfo ci) {
      if (tag.contains("CoopCooldown")) {
         this.farmAndCharm$coopCooldown = tag.getInt("CoopCooldown");
      }

      if (tag.contains("CoopTargetX") && tag.contains("CoopTargetY") && tag.contains("CoopTargetZ")) {
         int x = tag.getInt("CoopTargetX");
         int y = tag.getInt("CoopTargetY");
         int z = tag.getInt("CoopTargetZ");
         this.farmAndCharm$coopTarget = new BlockPos(x, y, z);
      }
   }
}
