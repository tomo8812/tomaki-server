package org.valkyrienskies.mod.mixinducks.client.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.class_2818;
import org.valkyrienskies.core.api.ships.ClientShip;

public interface ClientChunkCacheDuck {
   Long2ObjectMap<class_2818> vs$getShipChunks();

   void vs$removeShip(ClientShip var1);

   interface StorageDuck {
      void vs$incChunkCount();

      void vs$decChunkCount();
   }
}
