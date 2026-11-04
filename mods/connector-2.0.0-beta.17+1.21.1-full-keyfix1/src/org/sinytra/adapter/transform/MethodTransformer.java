package org.sinytra.adapter.transform;

import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.patch.config.Configuration;

public interface MethodTransformer {
   PatchResult apply(MixinContext var1, Configuration var2);
}
