package org.valkyrienskies.core.internal.world

import org.valkyrienskies.core.impl.api_impl.config.ConfigPhysicsBackendType

public interface VsiPipeline {
   public var arePhysicsRunning: Boolean

   public var deleteResources: Boolean

   public val isUsingDummyPhysics: Boolean

   public val shipWorld: VsiServerShipWorld

   public abstract fun preTickGame() {
   }

   public abstract fun postTickGame() {
   }

   public abstract fun computePhysTps(): Double {
   }

   public abstract fun getLoadedVoxelChunks(): Int {
   }

   public abstract fun getPhysicsBackendType(): ConfigPhysicsBackendType {
   }
}
