package org.embeddedt.modernfix.common.mixin.perf.worldgen_allocation;

import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.LazyCondition;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   targets = {
         "net/minecraft/world/level/levelgen/SurfaceRules$BiomeConditionSource$1BiomeCondition",
         "net/minecraft/world/level/levelgen/SurfaceRules$StoneDepthCheck$1StoneDepthCondition",
         "net/minecraft/world/level/levelgen/SurfaceRules$VerticalGradientConditionSource$1VerticalGradientCondition",
         "net/minecraft/world/level/levelgen/SurfaceRules$WaterConditionSource$1WaterCondition",
         "net/minecraft/world/level/levelgen/SurfaceRules$YConditionSource$1YCondition"
   }
)
public abstract class SurfaceRulesMixin extends LazyCondition {
   protected SurfaceRulesMixin(Context context) {
      super(context);
   }

   public boolean test() {
      return this.compute();
   }
}
