package org.sinytra.adapter.patch.resolver.target;

import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.resolver.CompoundResolver;
import org.sinytra.adapter.util.MethodQualifier;

public class TargetMethodResolver extends CompoundResolver {
   public TargetMethodResolver() {
      this.addSubResolver(new SplitTargetMethodSubResolver());
      this.addSubResolver(TargetMethodSubResolvers.CHANGED_LAMBDA_INDEX);
      this.addSubResolver(TargetMethodSubResolvers.CHANGED_METHOD_PARAMS);
      this.addSubResolver(TargetMethodSubResolvers.MOVED_INTO_LAMBDA);
   }

   @Override
   protected boolean canApply(Recipe recipe) {
      return recipe.dirty().getTargetMethod() == null;
   }

   @Override
   protected Configuration tryReuse(MixinContext context, Recipe recipe) {
      MethodQualifier cleanQualifier = recipe.clean().getTargetMethod();
      TargetPair target = context.methods().findOwnMethodPair(context.dirtyLookup(), cleanQualifier);
      return target != null && context.methods().hasInjectionTargetInsns(target) ? recipe.dirty().copyClean().inheritTargetMethod() : null;
   }

   @Override
   protected Configuration useFallback(MixinContext context, Recipe recipe) {
      MethodQualifier cleanQualifier = recipe.clean().getTargetMethod();
      TargetPair target = context.methods().findOwnMethodPair(context.dirtyLookup(), cleanQualifier);
      return target != null ? recipe.dirty().copyClean().inheritTargetMethod() : null;
   }
}
