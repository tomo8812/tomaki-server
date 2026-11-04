package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto;

import org.valkyrienskies.core.impl.shadow.ek;

public final class VSPipelineDataV4UpdaterImpl_Factory implements ek<VSPipelineDataV4UpdaterImpl> {
   public final VSPipelineDataV4UpdaterImpl get() {
      return newInstance();
   }

   public static VSPipelineDataV4UpdaterImpl_Factory create() {
      return VSPipelineDataV4UpdaterImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static VSPipelineDataV4UpdaterImpl newInstance() {
      return new VSPipelineDataV4UpdaterImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final VSPipelineDataV4UpdaterImpl_Factory INSTANCE = new VSPipelineDataV4UpdaterImpl_Factory();

      private InstanceHolder() {
      }
   }
}
