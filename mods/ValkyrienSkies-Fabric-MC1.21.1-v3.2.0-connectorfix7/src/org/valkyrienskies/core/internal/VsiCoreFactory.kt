package org.valkyrienskies.core.internal

import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut

public interface VsiCoreFactory {
   @JvmStatic
   VsiCoreFactory.Companion Companion = VsiCoreFactory.Companion.$$INSTANCE;

   public abstract fun newVsCoreClient(hooks: VsiCoreHooksOut): VsiCoreClient {
   }

   public abstract fun newVsCoreServer(hooks: VsiCoreHooksOut): VsiCoreServer {
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      VsiCoreFactory.Companion $$INSTANCE = VsiCoreFactory.Companion();

      @JvmStatic
      public final val instance: VsiCoreFactory = $$INSTANCE.findInstance()

      private fun findInstance(): VsiCoreFactory {
         val var1: Any = Class.forName("org.valkyrienskies.core.impl.program.VsiCoreFactoryImpl").getDeclaredConstructor().newInstance()
         return var1 as VsiCoreFactory
      }
   }
}
