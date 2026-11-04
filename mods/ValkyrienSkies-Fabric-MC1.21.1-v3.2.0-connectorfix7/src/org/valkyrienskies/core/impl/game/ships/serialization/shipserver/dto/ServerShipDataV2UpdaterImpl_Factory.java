package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto;

import org.valkyrienskies.core.impl.shadow.ek;

public final class ServerShipDataV2UpdaterImpl_Factory implements ek<ServerShipDataV2UpdaterImpl> {
   public final ServerShipDataV2UpdaterImpl get() {
      return newInstance();
   }

   public static ServerShipDataV2UpdaterImpl_Factory create() {
      return ServerShipDataV2UpdaterImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static ServerShipDataV2UpdaterImpl newInstance() {
      return new ServerShipDataV2UpdaterImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final ServerShipDataV2UpdaterImpl_Factory INSTANCE = new ServerShipDataV2UpdaterImpl_Factory();

      private InstanceHolder() {
      }
   }
}
