package org.valkyrienskies.eureka.fabric

import java.util.concurrent.CompletableFuture
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.class_3481
import net.minecraft.class_7225.class_7874
import org.valkyrienskies.eureka.EurekaBlocks

public class EurekaBlockTagsProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<class_7874>) : BlockTagProvider(output, registriesFuture) {
   fun method_10514(arg: class_7874) {
      this.getOrCreateTagBuilder(class_3481.field_15481)
         .add(EurekaBlocks.INSTANCE.BALLOON.get())
         .add(EurekaBlocks.INSTANCE.WHITE_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.LIGHT_GRAY_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.GRAY_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.BLACK_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.RED_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.ORANGE_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.YELLOW_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.LIME_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.GREEN_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.LIGHT_BLUE_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.CYAN_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.BLUE_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.PURPLE_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.MAGENTA_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.PINK_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.BROWN_BALLOON.get())
         .add(EurekaBlocks.INSTANCE.FLOATER.get())
         this.getOrCreateTagBuilder(class_3481.field_15471)
         .add(EurekaBlocks.INSTANCE.OAK_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.SPRUCE_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.BIRCH_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.JUNGLE_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.ACACIA_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.DARK_OAK_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.CRIMSON_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.WARPED_SHIP_HELM.get())
         this.getOrCreateTagBuilder(class_3481.field_33713)
         .add(EurekaBlocks.INSTANCE.OAK_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.SPRUCE_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.BIRCH_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.JUNGLE_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.ACACIA_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.DARK_OAK_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.CRIMSON_SHIP_HELM.get())
         .add(EurekaBlocks.INSTANCE.WARPED_SHIP_HELM.get())
         this.getOrCreateTagBuilder(class_3481.field_33715)
         .add(EurekaBlocks.INSTANCE.ANCHOR.get())
         .add(EurekaBlocks.INSTANCE.ENGINE.get())
         .add(EurekaBlocks.INSTANCE.BALLAST.get())
      }
}
