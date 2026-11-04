package org.valkyrienskies.eureka.block

import kotlin.enums.EnumEntries
import kotlin.enums.EnumEntriesKt
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1676
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2680
import net.minecraft.class_3218
import net.minecraft.class_3965
import net.minecraft.class_4970.class_2251
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.VSGameUtilsKt

@SourceDebugExtension(["SMAP\nBalloonBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BalloonBlock.kt\norg/valkyrienskies/eureka/block/BalloonBlock\n+ 2 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,61:1\n66#2:62\n1863#3,2:63\n*S KotlinDebug\n*F\n+ 1 BalloonBlock.kt\norg/valkyrienskies/eureka/block/BalloonBlock\n*L\n42#1:62\n51#1:63,2\n*E\n"])
public class BalloonBlock(properties: class_2251) : class_2248(properties) {
   fun method_9554(level: class_1937, state: class_2680, blockPos: class_2338, entity: class_1297, f: Float) {
      entity.method_5747(f, 0.2F, entity.method_48923().method_48827())
   }

   fun method_9615(state: class_2680, level: class_1937, pos: class_2338, oldState: class_2680, isMoving: Boolean) {
      super.method_9615(state, level, pos, oldState, isMoving)
      if (!level.field_9236) {
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var7: EurekaShipControl = EurekaShipControl.Companion.getOrCreate(var10000)
            var7.balloons = var7.balloons + 1
         }
      }
   }

   fun method_9536(state: class_2680, level: class_1937, pos: class_2338, newState: class_2680, isMoving: Boolean) {
      super.method_9536(state, level, pos, newState, isMoving)
      if (!level.field_9236) {
         val var10000: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(level as class_3218, pos as class_2382)
         if (var10000 != null) {
            val var10: EurekaShipControl = (var10000 as AttachmentHolder).getAttachment(EurekaShipControl.class) as EurekaShipControl
            if (var10 != null) {
               var10.balloons = var10.balloons - 1
            }
         }
      }
   }

   fun method_19286(level: class_1937, state: class_2680, hit: class_3965, projectile: class_1676) {
      if (!level.field_9236) {
         level.method_22352(hit.method_17777(), false)

         for (`element$iv` in BalloonBlock.EntriesMappings.entries$0 as java.lang.Iterable) {
            val neighbor: class_2338 = hit.method_17777().method_10093(`element$iv` as class_2350)
            if (level.method_8320(neighbor).method_26204() == this && level.field_9229.method_43057() < EurekaConfig.SERVER.popSideBalloonChance) {
               level.method_22352(neighbor, false)
            }
         }
      }
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class EntriesMappings {
      @JvmStatic
      @JvmSynthetic
      public EnumEntries<class_2350> entries$0 = EnumEntriesKt.enumEntries(class_2350.values() as Array<java.lang.Enum>);
   }
}
