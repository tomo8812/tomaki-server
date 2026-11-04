package org.valkyrienskies.mod.common

import org.valkyrienskies.core.internal.VsiCore

public interface VSCoreProvider {
   public abstract fun newVSCore(): VsiCore {
   }
}
