package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto;

import org.valkyrienskies.core.impl.shadow.ek;

public final class ServerShipDataV3UpdaterImpl_Factory implements ek<ServerShipDataV3UpdaterImpl> {
   public final ServerShipDataV3UpdaterImpl get() {
      return newInstance();
   }

   public static ServerShipDataV3UpdaterImpl_Factory create() {
      return ServerShipDataV3UpdaterImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static ServerShipDataV3UpdaterImpl newInstance() {
      return new ServerShipDataV3UpdaterImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final ServerShipDataV3UpdaterImpl_Factory INSTANCE = new ServerShipDataV3UpdaterImpl_Factory();

      private InstanceHolder() {
      }
   }
}
