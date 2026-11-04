package org.sinytra.adapter.patch.resolver;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;

public interface Resolver {
   @NotNull
   Resolver.ResolutionResult resolve(MixinContext var1, Recipe var2);

   record ResolutionResult(Resolver.ResultType type, @Nullable Configuration patch) {
      public static Resolver.ResolutionResult fail() {
         return new Resolver.ResolutionResult(Resolver.ResultType.FAIL, null);
      }

      public static Resolver.ResolutionResult pass() {
         return new Resolver.ResolutionResult(Resolver.ResultType.PASS, null);
      }

      public static Resolver.ResolutionResult success(Configuration patch) {
         return new Resolver.ResolutionResult(Resolver.ResultType.SUCCESS, patch);
      }

      public static Resolver.ResolutionResult replace(Configuration patch) {
         return new Resolver.ResolutionResult(Resolver.ResultType.REPLACE, patch);
      }

      public Optional<Configuration> maybePatch() {
         return Optional.ofNullable(this.patch);
      }
   }

   enum ResultType {
      REPLACE,
      SUCCESS,
      PASS,
      FAIL;

      // $VF: synthetic method
      private static Resolver.ResultType[] $values() {
         return new Resolver.ResultType[]{REPLACE, SUCCESS, PASS, FAIL};
      }
   }
}
