package org.valkyrienskies.core.impl.game.ships.serialization.shiptransform;

import org.valkyrienskies.core.impl.shadow.ek;

public final class ShipTransformConverterImpl_Factory implements ek<ShipTransformConverterImpl> {
   public final ShipTransformConverterImpl get() {
      return newInstance();
   }

   public static ShipTransformConverterImpl_Factory create() {
      return ShipTransformConverterImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static ShipTransformConverterImpl newInstance() {
      return new ShipTransformConverterImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final ShipTransformConverterImpl_Factory INSTANCE = new ShipTransformConverterImpl_Factory();

      private InstanceHolder() {
      }
   }
}
