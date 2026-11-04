package org.sinytra.adapter.transform.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.Configurations;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.resolver.Resolver;
import org.sinytra.adapter.transform.MethodTransformer;

public class MethodPatchResolver implements Resolver {
   private final List<MethodPatch> patches;

   public MethodPatchResolver(List<MethodPatch> patches) {
      this.patches = patches;
   }

   public boolean matches(Configuration config) {
      for (MethodPatch patch : this.patches) {
         if (patch.matcher().match(config)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Resolver.ResolutionResult resolve(MixinContext context, Recipe recipe) {
      List<MethodTransformer> postChanges = new ArrayList<>();
      Configuration config = recipe.clean();
      MutableConfiguration dirtyConfig = config.copyClean();
      boolean matched = false;

      for (MethodPatch patch : this.patches) {
         if (patch.matcher().match(config)) {
            matched = true;
            Configuration patchConfig = patch.configuration();
            dirtyConfig.mergeFrom(patchConfig);
            BiConsumer<Configuration, MutableConfiguration> completer = patch.configCompleter();
            completer.accept(recipe.clean(), dirtyConfig);
            postChanges.addAll(patch.transforms());
         }
      }

      if (matched) {
         if (!postChanges.isEmpty()) {
            recipe.processors().add(new MethodPatchProcessor(postChanges));
         }

         return dirtyConfig.shouldDelete() ? Resolver.ResolutionResult.replace(Configurations.DELETE) : Resolver.ResolutionResult.success(dirtyConfig);
      } else {
         return Resolver.ResolutionResult.pass();
      }
   }
}
