package org.valkyrienskies.core.impl.api_impl.physics.blockstates;

import org.valkyrienskies.core.impl.shadow.ek;

public final class LiquidStateImpl_BuilderImpl_Factory implements ek<LiquidStateImpl.BuilderImpl> {
   public final LiquidStateImpl.BuilderImpl get() {
      return newInstance();
   }

   public static LiquidStateImpl_BuilderImpl_Factory create() {
      return LiquidStateImpl_BuilderImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static LiquidStateImpl.BuilderImpl newInstance() {
      return new LiquidStateImpl.BuilderImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final LiquidStateImpl_BuilderImpl_Factory INSTANCE = new LiquidStateImpl_BuilderImpl_Factory();

      private InstanceHolder() {
      }
   }
}
