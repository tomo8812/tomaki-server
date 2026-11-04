package org.valkyrienskies.core.impl.program;

import org.valkyrienskies.core.impl.shadow.FV;
import org.valkyrienskies.core.impl.shadow.ek;
import org.valkyrienskies.core.impl.shadow.ev;

public final class VSCoreModule_GetPhysicsThreadCheckerFactory implements ek<FV> {
   private final VSCoreModule module;

   public VSCoreModule_GetPhysicsThreadCheckerFactory(VSCoreModule module) {
      this.module = module;
   }

   public final FV get() {
      return getPhysicsThreadChecker(this.module);
   }

   public static VSCoreModule_GetPhysicsThreadCheckerFactory create(VSCoreModule module) {
      return new VSCoreModule_GetPhysicsThreadCheckerFactory(module);
   }

   public static FV getPhysicsThreadChecker(VSCoreModule instance) {
      return ev.b(instance.getPhysicsThreadChecker());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
