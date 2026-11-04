package org.valkyrienskies.core.impl.program;

import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.impl.shadow.ek;
import org.valkyrienskies.core.impl.shadow.ev;

public final class VSCoreModule_ServerConfigFactory implements ek<VSCoreConfig.Server> {
   private final VSCoreModule module;

   public VSCoreModule_ServerConfigFactory(VSCoreModule module) {
      this.module = module;
   }

   public final VSCoreConfig.Server get() {
      return serverConfig(this.module);
   }

   public static VSCoreModule_ServerConfigFactory create(VSCoreModule module) {
      return new VSCoreModule_ServerConfigFactory(module);
   }

   public static VSCoreConfig.Server serverConfig(VSCoreModule instance) {
      return ev.b(instance.serverConfig());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
