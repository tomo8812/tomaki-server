package org.valkyrienskies.core.impl.program

import javax.inject.Singleton
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder
import org.valkyrienskies.core.impl.api_impl.physics.blockstates.BoxesBlockShapeImpl.BuilderImpl
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.impl.config.VSCoreConfig.Server
import org.valkyrienskies.core.impl.game.ships.serialization.ShipSerializationModule
import org.valkyrienskies.core.impl.shadow.DN
import org.valkyrienskies.core.impl.shadow.Do
import org.valkyrienskies.core.impl.shadow.EX
import org.valkyrienskies.core.impl.shadow.FP
import org.valkyrienskies.core.impl.shadow.FV
import org.valkyrienskies.core.impl.shadow.Fl
import org.valkyrienskies.core.impl.shadow.Fm
import org.valkyrienskies.core.impl.shadow.GK
import org.valkyrienskies.core.impl.shadow.dP
import org.valkyrienskies.core.impl.shadow.dW
import org.valkyrienskies.core.impl.shadow.dX
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.core.internal.collision.VsiSolidShapeUtils
import org.valkyrienskies.core.internal.hooks.VsiCoreHooks
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut
import org.valkyrienskies.core.internal.world.chunks.VsiBlockTypes

@dW(a = [Fl.a::class, GK::class, ShipSerializationModule::class, VSCoreModule.Declarations::class], b = [FP::class])
public class VSCoreModule(hooks: VsiCoreHooksOut, configurator: Fm, physicsThreadChecker: FV = b) {
   public final val hooks: VsiCoreHooksOut
   public final val configurator: Fm
   public final val physicsThreadChecker: FV

   init {
      this.hooks = hooks
      this.configurator = configurator
      this.physicsThreadChecker = physicsThreadChecker
   }

   @dX
   @Singleton
   public fun serverConfig(): Server {
      return VSCoreConfig.SERVER
   }

   @dW
   public interface Declarations {
      @dP
      public abstract fun solidStateBoxesShapeBuilder(impl: BuilderImpl): Builder {
      }

      @dP
      public abstract fun solidStateBuilder(impl: org.valkyrienskies.core.impl.api_impl.physics.blockstates.SolidStateImpl.BuilderImpl): org.valkyrienskies.core.api.physics.blockstates.SolidState.Builder {
      }

      @dP
      public abstract fun liquidStateBuilder(impl: org.valkyrienskies.core.impl.api_impl.physics.blockstates.LiquidStateImpl.BuilderImpl): org.valkyrienskies.core.api.physics.blockstates.LiquidState.Builder {
      }

      @dP
      public abstract fun solidShapeUtils(impl: Do): VsiSolidShapeUtils {
      }

      @dP
      public abstract fun vsCore(impl: VSCoreImpl): VsiCore {
      }

      @dP
      public abstract fun vsCoreInternal(impl: VSCoreImpl): VSCoreInternal {
      }

      @dP
      public abstract fun hooks(impl: EX): VsiCoreHooks {
      }

      @dP
      public abstract fun blockTypes(impl: DN): VsiBlockTypes {
      }
   }
}
