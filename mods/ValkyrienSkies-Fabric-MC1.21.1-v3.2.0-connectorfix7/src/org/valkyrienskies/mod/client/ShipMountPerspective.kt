package org.valkyrienskies.mod.client

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_310
import net.minecraft.class_5498

@SourceDebugExtension(["SMAP\nShipMountPerspective.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipMountPerspective.kt\norg/valkyrienskies/mod/client/ShipMountPerspective\n*L\n1#1,187:1\n115#1,8:188\n115#1,8:196\n115#1,8:204\n115#1,8:212\n*S KotlinDebug\n*F\n+ 1 ShipMountPerspective.kt\norg/valkyrienskies/mod/client/ShipMountPerspective\n*L\n73#1:188,8\n86#1:196,8\n95#1:204,8\n98#1:212,8\n*E\n"])
public object ShipMountPerspective {
   private final var shipView: Boolean

   @JvmStatic
   public fun isShipViewEngaged(): Boolean {
      return shipView && class_310.method_1551().field_1690.method_31044() === class_5498.field_26666
   }

   @JvmStatic
   public fun reset() {
      shipView = false
   }

   @JvmStatic
   public fun tickMounted() {
      if (shipView && (class_310.method_1551().field_1690.method_31044() != class_5498.field_26666 || ShoulderSurfingCompat.INSTANCE.isShoulderSurfing())) {
         shipView = false
      }
   }

   @JvmStatic
   public fun cycleMounted(mc: class_310) {
      if (shipView) {
         shipView = false
         val `this_$iv`: ShipMountPerspective = INSTANCE
         val `wasFirstPerson$iv`: Boolean = mc.field_1690.method_31044().method_31034()
         if (ShoulderSurfingCompat.INSTANCE.replacesDefaultPerspective() || !ShoulderSurfingCompat.INSTANCE.enterShoulderSurfing()) {
            mc.field_1690.method_31043(class_5498.field_26664)
         }

         val var14: Boolean = mc.field_1690.method_31044().method_31034()
         if (`wasFirstPerson$iv` != var14) {
            mc.field_1773.method_3167(if (var14) mc.field_1719 else null)
         }

         mc.field_1769.method_3292()
      } else if (ShoulderSurfingCompat.INSTANCE.isShoulderSurfing()) {
         val var5: ShipMountPerspective = INSTANCE
         val var11: Boolean = mc.field_1690.method_31044().method_31034()
         mc.field_1690.method_31043(if (ShoulderSurfingCompat.INSTANCE.replacesDefaultPerspective()) class_5498.field_26666 else class_5498.field_26664)
         val var16: Boolean = mc.field_1690.method_31044().method_31034()
         if (var11 != var16) {
            mc.field_1773.method_3167(if (var16) mc.field_1719 else null)
         }

         mc.field_1769.method_3292()
      } else if (mc.field_1690.method_31044() === class_5498.field_26664) {
         val var6: ShipMountPerspective = INSTANCE
         val var12: Boolean = mc.field_1690.method_31044().method_31034()
         mc.field_1690.method_31043(class_5498.field_26665)
         val var18: Boolean = mc.field_1690.method_31044().method_31034()
         if (var12 != var18) {
            mc.field_1773.method_3167(if (var18) mc.field_1719 else null)
         }

         mc.field_1769.method_3292()
      } else if (mc.field_1690.method_31044() === class_5498.field_26665) {
         val var7: ShipMountPerspective = INSTANCE
         val var13: Boolean = mc.field_1690.method_31044().method_31034()
         mc.field_1690.method_31043(class_5498.field_26666)
         val var20: Boolean = mc.field_1690.method_31044().method_31034()
         if (var13 != var20) {
            mc.field_1773.method_3167(if (var20) mc.field_1719 else null)
         }

         mc.field_1769.method_3292()
      } else {
         shipView = true
         mc.field_1769.method_3292()
      }
   }

   private inline fun withVanillaToggleParity(mc: class_310, change: () -> Unit) {
      val wasFirstPerson: Boolean = mc.field_1690.method_31044().method_31034()
      change()
      val isFirstPerson: Boolean = mc.field_1690.method_31044().method_31034()
      if (wasFirstPerson != isFirstPerson) {
         mc.field_1773.method_3167(if (isFirstPerson) mc.field_1719 else null)
      }

      mc.field_1769.method_3292()
   }
}
