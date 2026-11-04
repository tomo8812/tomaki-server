package org.sinytra.adapter.patch.resolver.special;

import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.resolver.Resolver;
import org.spongepowered.asm.mixin.injection.At.Shift;

public class ModifyVarUpgradeResolver implements Resolver {
   @Override
   public Resolver.ResolutionResult resolve(MixinContext context, Recipe recipe) {
      if (!recipe.hasInjectionPointValue("INVOKE") && !recipe.hasInjectionPointValue("INVOKE_ASSIGN")) {
         return Resolver.ResolutionResult.pass();
      }

      AtData at = recipe.clean().getAtData();
      if (at != null && !at.getTarget().isEmpty()) {
         Shift shift = at.getProperty(AtData.Keys.SHIFT).orElse(null);
         if (shift != Shift.BY) {
            return Resolver.ResolutionResult.pass();
         }

         boolean byTwo = at.getProperty(AtData.Keys.BY).map(i -> i == 2).orElse(false);
         if (!byTwo) {
            return Resolver.ResolutionResult.pass();
         }

         String target = at.getTargetOrThrow();
         Configuration config = recipe.dirty()
            .copyClean()
            .setMixinType("Lcom/llamalad7/mixinextras/injector/ModifyExpressionValue;")
            .inheritTargetClass()
            .inheritTargetMethod()
            .setAtData(AtData.create("INVOKE", target))
            .inheritParameters()
            .inheritReturnType();
         return Resolver.ResolutionResult.replace(config);
      } else {
         return Resolver.ResolutionResult.pass();
      }
   }
}
