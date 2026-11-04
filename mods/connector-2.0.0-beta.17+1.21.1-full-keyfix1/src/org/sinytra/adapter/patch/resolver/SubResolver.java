package org.sinytra.adapter.patch.resolver;

import org.jetbrains.annotations.Nullable;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;

public interface SubResolver {
   @Nullable
   Configuration resolve(MixinContext var1, Recipe var2);
}
