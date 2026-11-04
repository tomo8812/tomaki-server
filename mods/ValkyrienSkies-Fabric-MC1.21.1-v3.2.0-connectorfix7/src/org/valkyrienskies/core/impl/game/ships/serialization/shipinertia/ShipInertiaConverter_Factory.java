package org.valkyrienskies.core.impl.game.ships.serialization.shipinertia;

import org.valkyrienskies.core.impl.shadow.ek;

public final class ShipInertiaConverter_Factory implements ek<ShipInertiaConverter> {
   public final ShipInertiaConverter get() {
      return newInstance();
   }

   public static ShipInertiaConverter_Factory create() {
      return ShipInertiaConverter_Factory.InstanceHolder.INSTANCE;
   }

   public static ShipInertiaConverter newInstance() {
      return new ShipInertiaConverter();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final ShipInertiaConverter_Factory INSTANCE = new ShipInertiaConverter_Factory();

      private InstanceHolder() {
      }
   }
}
