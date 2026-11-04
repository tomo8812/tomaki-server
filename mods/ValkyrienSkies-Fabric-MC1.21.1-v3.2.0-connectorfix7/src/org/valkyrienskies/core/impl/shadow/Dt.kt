package org.valkyrienskies.core.impl.shadow

import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap
import org.joml.Vector3ic
import org.joml.primitives.AABBi
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.util.datastructures.IBlockPosSet

public class Dt(p0: IBlockPosSet = DenseBlockPosSet() as IBlockPosSet) : Dv, IBlockPosSet {
   private final val a: IBlockPosSet
   private final val c: Int2IntAVLTreeMap
   private final val d: Int2IntAVLTreeMap
   private final val e: Int2IntAVLTreeMap

   init {
      this.a = var1
      this.b = Int2IntAVLTreeMap()
      this.c = Int2IntAVLTreeMap()
      this.d = Int2IntAVLTreeMap()
   }

   public open val b: Int

   public override fun canStore(p0: Int, p1: Int, p2: Int): Boolean {
      return this.a.canStore(p0, p1, p2)
   }

   public override fun contains(p0: Int, p1: Int, p2: Int): Boolean {
      return this.a.contains(p0, p1, p2)
   }

   public override operator fun iterator(): MutableIterator<Vector3ic> {
      return this.a.iterator()
   }

   public override fun add(p0: Int, p1: Int, p2: Int): Boolean {
      if (this.a.add(x, y, z)) {
         this.b.addTo(x, 1)
         this.c.addTo(y, 1)
         this.d.addTo(z, 1)
         return true
      } else {
         return false
      }
   }

   public override fun remove(p0: Int, p1: Int, p2: Int): Boolean {
      if (this.a.remove(x, y, z)) {
         if (this.b.addTo(x, -1) == 1) {
            this.b.remove(x)
         }

         if (this.c.addTo(y, -1) == 1) {
            this.c.remove(y)
         }

         if (this.d.addTo(z, -1) == 1) {
            this.d.remove(z)
         }

         return true
      } else {
         return false
      }
   }

   public override fun clear() {
      this.b.clear()
      this.c.clear()
      this.d.clear()
      this.a.clear()
   }

   public override fun a(): AABBi? {
      return if (this.isEmpty())
         null
         else
         AABBi(this.b.firstIntKey(), this.c.firstIntKey(), this.d.firstIntKey(), this.b.lastIntKey(), this.c.lastIntKey(), this.d.lastIntKey())
      }

   fun Dt() {
      this(null, 1, null)
   }

   // $VF: bridge methodSupplier
   override final fun size(): Int {
      this.a.size()
   }
}
