package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto;

import org.valkyrienskies.core.impl.shadow.ek;

public final class ServerShipDataV0UpdaterImpl_Factory implements ek<ServerShipDataV0UpdaterImpl> {
   public final ServerShipDataV0UpdaterImpl get() {
      return newInstance();
   }

   public static ServerShipDataV0UpdaterImpl_Factory create() {
      return ServerShipDataV0UpdaterImpl_Factory.InstanceHolder.INSTANCE;
   }

   public static ServerShipDataV0UpdaterImpl newInstance() {
      return new ServerShipDataV0UpdaterImpl();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }

   static final class InstanceHolder {
      static final ServerShipDataV0UpdaterImpl_Factory INSTANCE = new ServerShipDataV0UpdaterImpl_Factory();

      private InstanceHolder() {
      }
   }
}
