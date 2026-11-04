package org.valkyrienskies.core.impl.program

import javax.inject.Singleton
import org.valkyrienskies.core.impl.shadow.dS

@Singleton
@dS(a = [VSCoreClientModule::class])
public interface VSCoreClientFactory {
   public abstract fun client(): VSCoreClientImpl {
   }
}
