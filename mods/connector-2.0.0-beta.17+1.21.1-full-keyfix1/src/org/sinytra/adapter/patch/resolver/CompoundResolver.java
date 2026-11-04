package org.sinytra.adapter.patch.resolver;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;

public abstract class CompoundResolver implements Resolver {
   protected final List<SubResolver> subResolvers = new ArrayList<>();

   public CompoundResolver addSubResolverFirst(SubResolver subResolver) {
      this.subResolvers.addFirst(subResolver);
      return this;
   }

   public CompoundResolver addSubResolver(SubResolver subResolver) {
      this.subResolvers.add(subResolver);
      return this;
   }

   protected abstract boolean canApply(Recipe var1);

   @Nullable
   protected Configuration tryReuse(MixinContext context, Recipe recipe) {
      return null;
   }

   @Nullable
   protected Configuration useFallback(MixinContext context, Recipe recipe) {
      return null;
   }

   @Override
   public Resolver.ResolutionResult resolve(MixinContext context, Recipe recipe) {
      if (!this.canApply(recipe)) {
         return Resolver.ResolutionResult.pass();
      }

      Configuration resused = this.tryReuse(context, recipe);
      if (resused != null) {
         return Resolver.ResolutionResult.success(resused);
      }

      for (SubResolver subResolver : this.subResolvers) {
         context.pushAudit(subResolver);
         Configuration result = subResolver.resolve(context, recipe);
         context.popAudit();
         if (result != null) {
            return Resolver.ResolutionResult.success(result);
         }
      }

      Configuration fallback = this.useFallback(context, recipe);
      return fallback != null ? Resolver.ResolutionResult.success(fallback) : Resolver.ResolutionResult.fail();
   }
}
