package org.valkyrienskies.core.impl.game.ships;

import java.util.Map;
import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider;
import org.valkyrienskies.core.impl.shadow.DP;
import org.valkyrienskies.core.impl.shadow.DW;
import org.valkyrienskies.core.impl.shadow.EO;
import org.valkyrienskies.core.impl.shadow.ek;
import org.valkyrienskies.core.internal.world.chunks.VsiBlockTypes;

public final class ShipObjectClientWorld_Factory implements ek<ShipObjectClientWorld> {
   private final Provider<EO.b> networkManagerFactoryProvider;
   private final Provider<ChunkAllocatorProvider> chunkAllocatorsProvider;
   private final Provider<VsiBlockTypes> blockTypesProvider;
   private final Provider<DW> connectivityHandlerProvider;
   private final Provider<Map<String, DP>> dimensionInfoProvider;

   public ShipObjectClientWorld_Factory(
      Provider<EO.b> networkManagerFactoryProvider,
      Provider<ChunkAllocatorProvider> chunkAllocatorsProvider,
      Provider<VsiBlockTypes> blockTypesProvider,
      Provider<DW> connectivityHandlerProvider,
      Provider<Map<String, DP>> dimensionInfoProvider
   ) {
      this.networkManagerFactoryProvider = networkManagerFactoryProvider;
      this.chunkAllocatorsProvider = chunkAllocatorsProvider;
      this.blockTypesProvider = blockTypesProvider;
      this.connectivityHandlerProvider = connectivityHandlerProvider;
      this.dimensionInfoProvider = dimensionInfoProvider;
   }

   public final ShipObjectClientWorld get() {
      return newInstance(
         this.networkManagerFactoryProvider.get(),
         this.chunkAllocatorsProvider.get(),
         this.blockTypesProvider.get(),
         this.connectivityHandlerProvider.get(),
         this.dimensionInfoProvider.get()
      );
   }

   public static ShipObjectClientWorld_Factory create(
      Provider<EO.b> networkManagerFactoryProvider,
      Provider<ChunkAllocatorProvider> chunkAllocatorsProvider,
      Provider<VsiBlockTypes> blockTypesProvider,
      Provider<DW> connectivityHandlerProvider,
      Provider<Map<String, DP>> dimensionInfoProvider
   ) {
      return new ShipObjectClientWorld_Factory(
         networkManagerFactoryProvider, chunkAllocatorsProvider, blockTypesProvider, connectivityHandlerProvider, dimensionInfoProvider
      );
   }

   public static ShipObjectClientWorld newInstance(
      EO.b networkManagerFactory, ChunkAllocatorProvider chunkAllocators, VsiBlockTypes blockTypes, DW connectivityHandler, Map<String, DP> dimensionInfo
   ) {
      return new ShipObjectClientWorld(networkManagerFactory, chunkAllocators, blockTypes, connectivityHandler, dimensionInfo);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
