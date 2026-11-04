package net.caffeinemc.mods.lithium.mixin.world.explosions.cache_exposure;

import net.caffeinemc.mods.lithium.common.world.ExplosionCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExplosionDamageCalculator.class)
public class ExplosionDamageCalculatorMixin {
   @Unique
   private ExplosionCache explosion;

   @Inject(method = "getEntityDamageAmount", at = @At("HEAD"))
   private void captureExplosion(Explosion explosion, Entity entity, CallbackInfoReturnable<Float> cir) {
      this.explosion = (ExplosionCache)explosion;
   }

   @Redirect(
      method = "getEntityDamageAmount",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Explosion;getSeenPercent(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;)F"
      )
   )
   private float useCachedExposure(Vec3 source, Entity entity) {
      float exposure = Explosion.getSeenPercent(source, entity);
      this.explosion.lithium_fabric$cacheExposure(entity, exposure);
      return exposure;
   }
}
