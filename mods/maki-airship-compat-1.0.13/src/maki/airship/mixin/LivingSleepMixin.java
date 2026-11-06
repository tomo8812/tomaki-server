package maki.airship.mixin;

import maki.airship.SleepCompat;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingSleepMixin {
   @Unique
   private SleepCompat.WakeAnchor maki$wakeAnchor;

   @Inject(method = "tick()V", at = @At("TAIL"), remap = false)
   private void maki$followBed(CallbackInfo ci) {
      SleepCompat.follow((LivingEntity)this);
   }

   @Inject(method = "stopSleeping()V", at = @At("HEAD"), remap = false)
   private void maki$rememberWake(CallbackInfo ci) {
      this.maki$wakeAnchor = SleepCompat.beforeWake((LivingEntity)this);
   }

   @Inject(method = "stopSleeping()V", at = @At("RETURN"), remap = false)
   private void maki$finishWake(CallbackInfo ci) {
      SleepCompat.WakeAnchor anchor = this.maki$wakeAnchor;
      this.maki$wakeAnchor = null;
      SleepCompat.afterWake((LivingEntity)this, anchor);
   }
}
