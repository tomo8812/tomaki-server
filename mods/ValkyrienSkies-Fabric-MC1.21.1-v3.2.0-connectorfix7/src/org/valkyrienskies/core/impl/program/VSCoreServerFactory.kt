package org.valkyrienskies.core.impl.program

import javax.inject.Singleton
import org.valkyrienskies.core.impl.shadow.dS

@dS(a = [VSCoreModule::class])
@Singleton
public interface VSCoreServerFactory {
   public abstract fun server(): VSCoreServerImpl {
   }
}
