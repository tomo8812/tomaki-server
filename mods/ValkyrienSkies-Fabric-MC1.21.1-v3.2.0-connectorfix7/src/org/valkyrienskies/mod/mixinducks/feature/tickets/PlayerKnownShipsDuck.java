package org.valkyrienskies.mod.mixinducks.feature.tickets;

import it.unimi.dsi.fastutil.longs.LongSet;

public interface PlayerKnownShipsDuck {
   void vs_addKnownShip(long var1);

   void vs_removeKnownShip(long var1);

   LongSet vs_getKnownShips();

   void vs_setKnownShips(LongSet var1);

   boolean vs_isKnownShip(long var1);
}
