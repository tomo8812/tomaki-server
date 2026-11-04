package org.valkyrienskies.core.impl.shadow;

import org.valkyrienskies.physics_api_krunch.KrunchNativePhysicsThreadPoolReference;
import org.valkyrienskies.physics_api_krunch.KrunchNativePhysicsWorldReference;

public final class IC {
   private static void a() {
      Ix.a();
      new KrunchNativePhysicsWorldReference(new KrunchNativePhysicsThreadPoolReference(1));
   }

   // $VF: synthetic method
   private static void b() {
      Ix.a();
      new KrunchNativePhysicsWorldReference(new KrunchNativePhysicsThreadPoolReference(1));
   }
}
