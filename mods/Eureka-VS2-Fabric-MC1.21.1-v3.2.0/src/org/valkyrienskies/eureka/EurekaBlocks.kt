package org.valkyrienskies.eureka

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1747
import net.minecraft.class_1792
import net.minecraft.class_2246
import net.minecraft.class_2248
import net.minecraft.class_2358
import net.minecraft.class_2498
import net.minecraft.class_3620
import net.minecraft.class_5321
import net.minecraft.class_7924
import net.minecraft.class_1792.class_1793
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.eureka.block.AnchorBlock
import org.valkyrienskies.eureka.block.BallastBlock
import org.valkyrienskies.eureka.block.BalloonBlock
import org.valkyrienskies.eureka.block.EngineBlock
import org.valkyrienskies.eureka.block.FloaterBlock
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.block.WoodType
import org.valkyrienskies.eureka.registry.DeferredRegister
import org.valkyrienskies.eureka.registry.RegistrySupplier
import org.valkyrienskies.mod.common.hooks.VSGameEvents

@SourceDebugExtension(["SMAP\nEurekaBlocks.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaBlocks.kt\norg/valkyrienskies/eureka/EurekaBlocks\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,215:1\n1863#2,2:216\n*S KotlinDebug\n*F\n+ 1 EurekaBlocks.kt\norg/valkyrienskies/eureka/EurekaBlocks\n*L\n210#1:216,2\n*E\n"])
public object EurekaBlocks {
   internal final val BLOCKS: DeferredRegister<class_2248>
   public final val ANCHOR: RegistrySupplier<AnchorBlock>
   public final val ENGINE: RegistrySupplier<EngineBlock>
   public final val FLOATER: RegistrySupplier<FloaterBlock>
   public final val BALLAST: RegistrySupplier<BallastBlock>
   public final val OAK_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val SPRUCE_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val BIRCH_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val JUNGLE_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val ACACIA_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val DARK_OAK_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val CRIMSON_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val WARPED_SHIP_HELM: RegistrySupplier<ShipHelmBlock>
   public final val BALLOON: RegistrySupplier<BalloonBlock>
   public final val WHITE_BALLOON: RegistrySupplier<BalloonBlock>
   public final val LIGHT_GRAY_BALLOON: RegistrySupplier<BalloonBlock>
   public final val GRAY_BALLOON: RegistrySupplier<BalloonBlock>
   public final val BLACK_BALLOON: RegistrySupplier<BalloonBlock>
   public final val RED_BALLOON: RegistrySupplier<BalloonBlock>
   public final val ORANGE_BALLOON: RegistrySupplier<BalloonBlock>
   public final val YELLOW_BALLOON: RegistrySupplier<BalloonBlock>
   public final val LIME_BALLOON: RegistrySupplier<BalloonBlock>
   public final val GREEN_BALLOON: RegistrySupplier<BalloonBlock>
   public final val LIGHT_BLUE_BALLOON: RegistrySupplier<BalloonBlock>
   public final val CYAN_BALLOON: RegistrySupplier<BalloonBlock>
   public final val BLUE_BALLOON: RegistrySupplier<BalloonBlock>
   public final val PURPLE_BALLOON: RegistrySupplier<BalloonBlock>
   public final val MAGENTA_BALLOON: RegistrySupplier<BalloonBlock>
   public final val PINK_BALLOON: RegistrySupplier<BalloonBlock>
   public final val BROWN_BALLOON: RegistrySupplier<BalloonBlock>

   public fun register() {
      BLOCKS.applyAll()
      VSGameEvents.INSTANCE.getRegistriesCompleted().on({ var0: Unit, var1: RegisteredListener ->
         INSTANCE.makeFlammables()
      })
   }

   public fun flammableBlock(block: class_2248, flameOdds: Int, burnOdds: Int) {
      val var10000: class_2248 = class_2246.field_10036
      (var10000 as class_2358).method_10189(block, flameOdds, burnOdds)
   }

   public fun makeFlammables() {
      this.flammableBlock(OAK_SHIP_HELM.get() as class_2248, 5, 20)
      this.flammableBlock(SPRUCE_SHIP_HELM.get() as class_2248, 5, 20)
      this.flammableBlock(BIRCH_SHIP_HELM.get() as class_2248, 5, 20)
      this.flammableBlock(JUNGLE_SHIP_HELM.get() as class_2248, 5, 20)
      this.flammableBlock(ACACIA_SHIP_HELM.get() as class_2248, 5, 20)
      this.flammableBlock(DARK_OAK_SHIP_HELM.get() as class_2248, 5, 20)
      this.flammableBlock(BALLOON.get(), 30, 60)
      this.flammableBlock(WHITE_BALLOON.get(), 30, 60)
      this.flammableBlock(LIGHT_GRAY_BALLOON.get(), 30, 60)
      this.flammableBlock(GRAY_BALLOON.get(), 30, 60)
      this.flammableBlock(BLACK_BALLOON.get(), 30, 60)
      this.flammableBlock(RED_BALLOON.get(), 30, 60)
      this.flammableBlock(ORANGE_BALLOON.get(), 30, 60)
      this.flammableBlock(YELLOW_BALLOON.get(), 30, 60)
      this.flammableBlock(LIME_BALLOON.get(), 30, 60)
      this.flammableBlock(GREEN_BALLOON.get(), 30, 60)
      this.flammableBlock(LIGHT_BLUE_BALLOON.get(), 30, 60)
      this.flammableBlock(CYAN_BALLOON.get(), 30, 60)
      this.flammableBlock(BLUE_BALLOON.get(), 30, 60)
      this.flammableBlock(PURPLE_BALLOON.get(), 30, 60)
      this.flammableBlock(MAGENTA_BALLOON.get(), 30, 60)
      this.flammableBlock(PINK_BALLOON.get(), 30, 60)
      this.flammableBlock(BROWN_BALLOON.get(), 30, 60)
      this.flammableBlock(FLOATER.get(), 5, 20)
   }

   public fun registerItems(items: DeferredRegister<class_1792>) {
      for (`element$iv` in BLOCKS) {
         items.register((`element$iv` as RegistrySupplier).name, { 
            class_1747(`$it`.get() as class_2248, class_1793())
         })
      }
   }

   @JvmStatic
   fun {
      val var10000: DeferredRegister.Companion = DeferredRegister.Companion
      val var10002: class_5321 = class_7924.field_41254
      BLOCKS = var10000.create("vs_eureka", var10002)
      ANCHOR = BLOCKS.register("anchor", <unrepresentable>.INSTANCE)
      ENGINE = BLOCKS.register("engine", <unrepresentable>.INSTANCE)
      FLOATER = BLOCKS.register("floater", <unrepresentable>.INSTANCE)
      BALLAST = BLOCKS.register("ballast", <unrepresentable>.INSTANCE)
      OAK_SHIP_HELM = BLOCKS.register(
         "oak_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15996)
               .method_9632(2.5F)
               .method_9626(class_2498.field_11547)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.OAK)
         }
      )
      SPRUCE_SHIP_HELM = BLOCKS.register(
         "spruce_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15996)
               .method_9632(2.5F)
               .method_9626(class_2498.field_11547)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.SPRUCE)
         }
      )
      BIRCH_SHIP_HELM = BLOCKS.register(
         "birch_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15996)
               .method_9632(2.5F)
               .method_9626(class_2498.field_11547)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.BIRCH)
         }
      )
      JUNGLE_SHIP_HELM = BLOCKS.register(
         "jungle_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15996)
               .method_9632(2.5F)
               .method_9626(class_2498.field_11547)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.JUNGLE)
         }
      )
      ACACIA_SHIP_HELM = BLOCKS.register(
         "acacia_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15996)
               .method_9632(2.5F)
               .method_9626(class_2498.field_11547)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.ACACIA)
         }
      )
      DARK_OAK_SHIP_HELM = BLOCKS.register(
         "dark_oak_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15996)
               .method_9632(2.5F)
               .method_9626(class_2498.field_11547)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.DARK_OAK)
         }
      )
      CRIMSON_SHIP_HELM = BLOCKS.register(
         "crimson_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16012)
               .method_9632(2.5F)
               .method_9626(class_2498.field_22152)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.CRIMSON)
         }
      )
      WARPED_SHIP_HELM = BLOCKS.register(
         "warped_ship_helm",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16012)
               .method_9632(2.5F)
               .method_9626(class_2498.field_22152)
               .method_29292()
               ShipHelmBlock(var10002, WoodType.WARPED)
         }
      )
      BALLOON = BLOCKS.register(
         "balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15979)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      WHITE_BALLOON = BLOCKS.register(
         "white_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16022)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      LIGHT_GRAY_BALLOON = BLOCKS.register(
         "light_gray_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15993)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      GRAY_BALLOON = BLOCKS.register(
         "gray_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15978)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      BLACK_BALLOON = BLOCKS.register(
         "black_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16009)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      RED_BALLOON = BLOCKS.register(
         "red_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16020)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      ORANGE_BALLOON = BLOCKS.register(
         "orange_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15987)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      YELLOW_BALLOON = BLOCKS.register(
         "yellow_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16010)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      LIME_BALLOON = BLOCKS.register(
         "lime_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15997)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      GREEN_BALLOON = BLOCKS.register(
         "green_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15995)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      LIGHT_BLUE_BALLOON = BLOCKS.register(
         "light_blue_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16024)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      CYAN_BALLOON = BLOCKS.register(
         "cyan_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16026)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      BLUE_BALLOON = BLOCKS.register(
         "blue_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15984)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      PURPLE_BALLOON = BLOCKS.register(
         "purple_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16014)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      MAGENTA_BALLOON = BLOCKS.register(
         "magenta_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15998)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      PINK_BALLOON = BLOCKS.register(
         "pink_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_16030)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
      BROWN_BALLOON = BLOCKS.register(
         "brown_balloon",
         { 
            val var10002: class_2251 = class_2251.method_9637()
               .method_31710(class_3620.field_15977)
               .method_9632(0.8F)
               .method_9626(class_2498.field_11543)
               .method_29292()
               BalloonBlock(var10002)
         }
      )
   }
}
