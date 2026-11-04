package com.kishku7.chunksmith.ducks;

import java.util.function.BooleanSupplier;

public interface MinecraftServerExtension {
   void chunksmith$runChunkSystemHousekeeping(BooleanSupplier var1);

   void chunksmith$markChunkSystemHousekeeping();

   void chunksmith$atTicketSafePoint(Runnable var1);

   void chunksmith$drainTicketSafePointNow();

   boolean chunksmith$onTicketSafePoint();

   double chunksmith$getMillisPerTick();
}
