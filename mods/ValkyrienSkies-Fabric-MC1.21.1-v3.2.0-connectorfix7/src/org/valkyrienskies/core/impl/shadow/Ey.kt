package org.valkyrienskies.core.impl.shadow

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry
import it.unimi.dsi.fastutil.objects.ObjectCollection
import it.unimi.dsi.fastutil.objects.ObjectIterator
import java.util.ArrayList
import org.joml.Matrix4dc
import org.valkyrienskies.core.api.ships.Wing
import org.valkyrienskies.core.api.ships.WingGroupChanges
import org.valkyrienskies.core.api.ships.WingManager
import org.valkyrienskies.core.api.ships.WingManagerChanges
import org.valkyrienskies.core.util.datastructures.BlockPos2ObjectOpenHashMap

public class Ey : WingManager {
   private final val b: Int2ObjectMap<Ex>
   private final val c: IntList
   private final var a: Int

   public override fun createWingGroup(p0: Boolean): Int {
      val var4: Int
      (this.a as java.util.Map).put(var4 = this.c++, Ex())
      val var10000: Any = this.a.get(var4)
      (var10000 as Ex).e = isContraption
      return var4
   }

   public override fun deleteWingGroup(p0: Int): Boolean {
      val var2: Boolean
      if (var2 = this.a.remove(wingGroupId) != null) {
         this.b.add(wingGroupId)
      }

      return var2
   }

   public override fun setWing(p0: Int, p1: Int, p2: Int, p3: Int, p4: Wing?): Boolean {
      var var10000: Ex = (Ex)this.a.get(wingGroupId)
      var10000 = var10000
      val var6: Byte = 16
      return var10000.a(posX, posY, posZ, wing, true)
   }

   public override fun getWing(p0: Int, p1: Int, p2: Int, p3: Int): Wing? {
      val var10000: Any = this.a.get(wingGroupId)
      return (var10000 as Ex).a.get(posX, posY, posZ)
   }

   public override fun setWingGroupTransform(p0: Int, p1: Matrix4dc) {
      var var10000: Ex = (Ex)this.a.get(wingGroupId)
      var10000 = var10000
      var10000.c = transform
      var10000.d = true
   }

   public override fun getWingChanges(): WingManagerChanges? {
      var var1: java.util.List = null
      val var2: ObjectIterator = this.a.int2ObjectEntrySet().iterator()

      while (var2.hasNext()) {
         val var3: Entry
         val var4: Int = (var3 = var2.next() as Entry).getIntKey()
         val var5: Ex
         val var6: WingGroupChanges
         if ((var6 = if ((var5 = var3.getValue() as Ex).b == null && !var5.d) null else WingGroupChanges(var5.b, if (var5.d) var5.c else null)) != null) {
            if (var1 == null) {
               var1 = ArrayList()
            }

            var1.add(Pair(var4, var6))
         }
      }

      return if (var1 == null && this.b.isEmpty()) null else WingManagerChanges(var1, IntArrayList(this.b) as MutableList<Int>)
   }

   public override fun clearWingChanges() {
      val var1: ObjectIterator = (this.a.values() as ObjectCollection).iterator()

      while (var1.hasNext()) {
         val var2: Ex
         (var2 = var1.next() as Ex).b = null
         var2.d = false
      }

      this.b.clear()
   }

   public override fun getFirstWingGroupId(): Int {
      if (this.c == 0) {
         throw IllegalStateException("This wing manager has no wing groups!")
      } else {
         return 0
      }
   }

   public fun a(p0: WingManagerChanges) {
      var var10000: java.util.List = var1.deletedWingGroups
      if (var10000 != null) {
         val var2: java.util.Iterator = var10000.iterator()

         while (var2.hasNext()) {
            this.a.remove((var2.next() as java.lang.Number).intValue())
         }
      }

      var10000 = var1.wingGroupChanges
      if (var10000 != null) {
         for (var9 in var10000) {
            val var6: java.util.Map = this.a as java.util.Map
            val var4: Any = var9.getFirst()
            val var5: Any
            var var15: Any
            if ((var5 = var6.get(var4)) == null) {
               var5 = Ex()
               var6.put(var4, var5)
               var15 = (Ex)var5
            } else {
               var15 = (Ex)var5
            }

            var15 = var15
            val var10: WingGroupChanges = var9.getSecond() as WingGroupChanges
            val var7: Ex = var15
            if (var10.changedTransform != null) {
               val var10001: Matrix4dc = var10.changedTransform
               var15.c = var10001
            }

            val var17: java.util.List = var10.changedWings
            if (var17 != null) {
               for (var12 in var17) {
                  var7.a(var12.posX, var12.posY, var12.posZ, var12.wing, false)
               }
            }
         }
      }
   }

   internal inline fun a(p0: (Matrix4dc, Int, Int, Int, Wing, Boolean) -> Unit) {
      val var2: ObjectIterator = (this.a.values() as ObjectCollection).iterator()

      while (var2.hasNext()) {
         val var3: Ex
         val var4: BlockPos2ObjectOpenHashMap
         if ((var4 = (var3 = var2.next() as Ex).a).containsNullKey) {
            var1(var3.c, var4.keys[var4.n * 3], var4.keys[var4.n * 3 + 1], var4.keys[var4.n * 3 + 2], var4.values[var4.n] as Wing, var3.e)
         }

         // $VF: Unable to resugar Kotlin loop from Java for loop
         var var9: Int = var4.n
         while (true) {
            if (var9 >= 0) break
            if (var4.keys[var9 * 3] != 0 || var4.keys[var9 * 3 + 1] != 0 || var4.keys[var9 * 3 + 2] != 0) {
               var1(var3.c, var4.keys[var9 * 3], var4.keys[var9 * 3 + 1], var4.keys[var9 * 3 + 2], var4.values[var9] as Wing, var3.e)
            }

            var9--
         }
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun a(var0: Ey): Int2ObjectMap {
      var0.a
   }
}
