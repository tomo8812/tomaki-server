package org.valkyrienskies.core.impl.program

import org.valkyrienskies.core.impl.shadow.EY
import org.valkyrienskies.core.impl.shadow.Fb
import org.valkyrienskies.core.internal.VsiCoreClient
import org.valkyrienskies.core.internal.VsiCoreFactory
import org.valkyrienskies.core.internal.VsiCoreServer
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut

public class VsiCoreFactoryImpl : VsiCoreFactory {
   public override fun newVsCoreClient(hooks: VsiCoreHooksOut): VsiCoreClient {
      val var2: VSCoreClientImpl
      EY.a((var2 = DaggerVSCoreClientFactory.builder().vSCoreModule(this.makeModule(hooks)).build().client()).hooks)
      return var2
   }

   public override fun newVsCoreServer(hooks: VsiCoreHooksOut): VsiCoreServer {
      val var2: VSCoreServerImpl
      EY.a((var2 = DaggerVSCoreServerFactory.builder().vSCoreModule(this.makeModule(hooks)).build().server()).hooks)
      return var2
   }

   private fun makeModule(hooks: VsiCoreHooksOut): VSCoreModule {
      return VSCoreModule(
         hooks,
         { it: Fb ->
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.IllegalStateException: Anonymous class does not have Class Kotlin metadata
            //   at org.vineflower.kotlin.KotlinWriter.writeClassDefinition(KotlinWriter.java:742)
            //   at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:309)
            //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:178)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:770)
            //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:736)
         },
         null,
         4,
         null
      )
   }
}
