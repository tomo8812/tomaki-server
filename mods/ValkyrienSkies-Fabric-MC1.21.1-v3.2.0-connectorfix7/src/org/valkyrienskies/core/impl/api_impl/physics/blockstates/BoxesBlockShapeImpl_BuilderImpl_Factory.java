package org.valkyrienskies.core.impl.api_impl.physics.blockstates;

import org.valkyrienskies.core.impl.shadow.ek;

public final class BoxesBlockShapeImpl_BuilderImpl_Factory implements ek<BoxesBlockShapeImpl.BuilderImpl> {
   public final BoxesBlockShapeImpl.BuilderImpl get() {
      return newInstance();
   }

   public static BoxesBlockShapeImpl_BuilderImpl_Factory create() {
      return BoxesBlockShapeImpl_BuilderImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static BoxesBlockShapeImpl.BuilderImpl newInstance() {
      return new BoxesBlockShapeImpl.BuilderImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final BoxesBlockShapeImpl_BuilderImpl_Factory INSTANCE = new BoxesBlockShapeImpl_BuilderImpl_Factory();

      private InstanceHolder() {
      }
   }
}
