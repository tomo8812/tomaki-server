package org.valkyrienskies.core.impl.program;

import org.valkyrienskies.core.impl.shadow.ek;
import org.valkyrienskies.core.impl.shadow.ev;
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut;

public final class VSCoreModule_GetHooksFactory implements ek<VsiCoreHooksOut> {
   private final VSCoreModule module;

   public VSCoreModule_GetHooksFactory(VSCoreModule module) {
      this.module = module;
   }

   public final VsiCoreHooksOut get() {
      return getHooks(this.module);
   }

   public static VSCoreModule_GetHooksFactory create(VSCoreModule module) {
      return new VSCoreModule_GetHooksFactory(module);
   }

   public static VsiCoreHooksOut getHooks(VSCoreModule instance) {
      return ev.b(instance.getHooks());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
