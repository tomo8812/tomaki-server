package org.valkyrienskies.mod.common.item

import net.minecraft.class_1269
import net.minecraft.class_1657
import net.minecraft.class_1792
import net.minecraft.class_1799
import net.minecraft.class_1838
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2561
import net.minecraft.class_3218
import net.minecraft.class_1792.class_1793
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.assembly.ShipAssembler

public class ShipRemoverItem(properties: class_1793) : class_1792(properties) {
   fun method_7886(stack: class_1799): Boolean {
      true
   }

   fun method_7884(ctx: class_1838): class_1269 {
      val ship: class_1937 = ctx.method_8045()
      val var10000: class_3218 = ship as? class_3218
      if ((ship as? class_3218) == null) {
         val var11: class_1269 = super.method_7884(ctx)
         var11
      } else {
         val pos: class_2338 = ctx.method_8037()
         if (!var10000.field_9236) {
            val var6: ServerShip = VSGameUtilsKt.getShipManagingPos(var10000, pos)
            if (var6 == null) {
               val var7: class_1657 = ctx.method_8036()
               if (var7 != null) {
                  var7.method_43496(class_2561.method_43471("command.valkyrienskies.get_ship.fail") as class_2561)
               }
            } else {
               val var8: ShipAssembler = ShipAssembler.INSTANCE
               val var10004: class_1657 = ctx.method_8036()
               var8.deleteShip(var10000, var6, true, var10004 != null && var10004.method_5715())
               val var9: class_1657 = ctx.method_8036()
               if (var9 != null) {
                  var9.method_43496(class_2561.method_43469("command.valkyrienskies.delete.success_one", arrayOf(var6.slug)) as class_2561)
               }
            }
         }

         val var10: class_1269 = super.method_7884(ctx)
         var10
      }
   }
}
