package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto;

import org.valkyrienskies.core.impl.shadow.ek;

public final class ServerShipDataV1UpdaterImpl_Factory implements ek<ServerShipDataV1UpdaterImpl> {
   public final ServerShipDataV1UpdaterImpl get() {
      return newInstance();
   }

   public static ServerShipDataV1UpdaterImpl_Factory create() {
      return ServerShipDataV1UpdaterImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static ServerShipDataV1UpdaterImpl newInstance() {
      return new ServerShipDataV1UpdaterImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final ServerShipDataV1UpdaterImpl_Factory INSTANCE = new ServerShipDataV1UpdaterImpl_Factory();

      private InstanceHolder() {
      }
   }
}
