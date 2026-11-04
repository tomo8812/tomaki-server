package org.valkyrienskies.eureka.fabric

import java.util.concurrent.CompletableFuture
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack

public class EurekaDataGenerator : DataGeneratorEntrypoint {
   public open fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
      val var10000: Pack = fabricDataGenerator.createPack()
      var10000.addProvider({ output: FabricDataOutput, registriesFuture: CompletableFuture ->
         EurekaBlockTagsProvider(output, registriesFuture)
      })
   }
}
