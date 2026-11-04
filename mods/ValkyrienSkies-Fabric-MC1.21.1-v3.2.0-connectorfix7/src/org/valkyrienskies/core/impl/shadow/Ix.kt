package org.valkyrienskies.core.impl.shadow

import org.valkyrienskies.physics_api_krunch.KrunchNativePhysicsThreadPoolReference
import org.valkyrienskies.physics_api_krunch.KrunchNativePhysicsWorldReference

public object Ix {
   private final var a: Boolean

   @JvmStatic
   public fun a(p0: Int): Hd {
      if (!b) {
         throw Iw()
      } else {
         return KrunchNativePhysicsThreadPoolReference(var0)
      }
   }

   @JvmStatic
   public fun a(p0: Hd): He {
      if (!b) {
         throw Iw()
      } else {
         return KrunchNativePhysicsWorldReference(var0 as KrunchNativePhysicsThreadPoolReference)
      }
   }

   @JvmStatic
   public fun a(p0: He, p1: IA) {
      (var0 as KrunchNativePhysicsWorldReference).a(var1)
   }

   @JvmStatic
   public fun a() {
      if (!b) {
         var var0: IQ = IQ()
         val var7: java.lang.String = "KrunchJni"
         var0 = var0
         if (!IQ.a) {
            val var2: Class<IQ> = IQ::class.java
            synchronized (IQ::class.java) {
               if (!IQ.e(var7)) {
                  val var3: java.lang.String = IQ.a(var7)
                  System.out.println("KrunchPhysicsAPISharedLibraryLoader loading library $var7")

                  try {
                     if (IQ.b) {
                        System.loadLibrary(var3)
                     } else {
                        var0.c(var3)
                     }

                     IQ.d(var7)
                  } catch (var4: java.lang.Throwable) {
                     throw RuntimeException(
                        "Couldn't load shared library '$var3' for target: ${System.getProperty("os.name")}${if (IQ.c) ", 64-bit" else ", 32-bit"}", var4
                     )
                  }
               }
            }
         }

         b = true
      }
   }

   @JvmStatic
   public fun a(p0: String) {
      if (!b) {
         System.load(var0)
         b = true
      }
   }

   @JvmStatic
   public fun b(): Ho {
      return IM.INSTANCE
   }
}
