package org.valkyrienskies.core.impl.program;

import org.valkyrienskies.core.impl.shadow.Fm;
import org.valkyrienskies.core.impl.shadow.ek;
import org.valkyrienskies.core.impl.shadow.ev;

public final class VSCoreModule_GetConfiguratorFactory implements ek<Fm> {
   private final VSCoreModule module;

   public VSCoreModule_GetConfiguratorFactory(VSCoreModule module) {
      this.module = module;
   }

   public final Fm get() {
      return getConfigurator(this.module);
   }

   public static VSCoreModule_GetConfiguratorFactory create(VSCoreModule module) {
      return new VSCoreModule_GetConfiguratorFactory(module);
   }

   public static Fm getConfigurator(VSCoreModule instance) {
      return ev.b(instance.getConfigurator());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
