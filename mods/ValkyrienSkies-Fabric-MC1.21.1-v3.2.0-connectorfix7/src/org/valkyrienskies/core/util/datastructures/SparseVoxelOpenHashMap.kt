package org.valkyrienskies.core.util.datastructures

import java.io.Serializable
import java.util.HashMap
import java.util.HashSet
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition

public class SparseVoxelOpenHashMap<T> : HashMap<SparseVoxelPosition, T> {
   public final val chunkIndex: HashMap<Vector3ic, HashSet<SparseVoxelPosition>> = HashMap()

   public fun get(x: Int, y: Int, z: Int): Any? {
      val value: Any = super.get(SparseVoxelPosition(x, y, z, 0L, 8, null))
      if (value == null) {
         val containingVoxel: SparseVoxelPosition = this.findContainingVoxel(x, y, z)
         if (containingVoxel != null) {
            return (T)super.get(containingVoxel)
         }
      }

      return (T)value
   }

   public fun get(x: Int, y: Int, z: Int, extent: Long): Any? {
      val value: Any = super.get(SparseVoxelPosition(x, y, z, extent))
      if (value == null) {
         val containingVoxel: SparseVoxelPosition = this.findContainingVoxel(SparseVoxelPosition(x, y, z, extent))
         if (containingVoxel != null) {
            return (T)super.get(containingVoxel)
         }
      }

      return (T)value
   }

   public fun get(vec: Vector3ic): Any? {
      return this.get(vec.x(), vec.y(), vec.z())
   }

   public fun get(vec: Vector3i): Any? {
      return this.get(vec.x, vec.y, vec.z)
   }

   public open operator fun get(key: SparseVoxelPosition): Any? {
      val var10000: Any = super.get(key)
      return (T)(if (var10000 == null) this.get(key.x, key.y, key.z, key.extent) else var10000)
   }

   public fun getCertain(key: SparseVoxelPosition): Any? {
      return (T)super.get(key)
   }

   public fun put(x: Int, y: Int, z: Int, value: Any): Any? {
      if (this.findContainingVoxel(x, y, z) != null) {
         val var9: SparseVoxelPosition = this.findContainingVoxel(x, y, z)
         return (T)super.put(var9, value)
      } else {
         val var10000: HashSet = this.chunkIndex.get(Vector3i(x shr 4, y shr 4, z shr 4))
         if (var10000 != null) {
            val var6: Serializable = var10000.add(SparseVoxelPosition(x, y, z, 0L, 8, null))
         } else {
            val var8: Serializable = this.chunkIndex
               .put(Vector3i(x shr 4, y shr 4, z shr 4), SetsKt.hashSetOf(arrayOf(SparseVoxelPosition(x, y, z, 0L, 8, null))))
            }

         return (T)super.put(SparseVoxelPosition(x, y, z, 0L, 8, null), value)
      }
   }

   public fun put(vec: Vector3ic, value: Any): Any? {
      return this.put(vec.x(), vec.y(), vec.z(), (T)value)
   }

   public fun put(vec: Vector3i, value: Any): Any? {
      return this.put(vec.x, vec.y, vec.z, (T)value)
   }

   public open fun put(key: SparseVoxelPosition, value: Any): Any? {
      val containingVoxel: SparseVoxelPosition = this.findContainingVoxel(key)
      if (containingVoxel != null) {
         if (containingVoxel.extent < key.extent) {
            containingVoxel.modExtent(key.extent)
         }

         return (T)super.put(containingVoxel, value)
      } else {
         val var10000: HashSet = this.chunkIndex.get(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))
         if (var10000 != null) {
            val var5: Serializable = var10000.add(key)
         } else {
            val var7: Serializable = this.chunkIndex.put(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4), SetsKt.hashSetOf(arrayOf(key)))
         }

         return (T)super.put(key, value)
      }
   }

   public fun putCertain(key: SparseVoxelPosition, value: Any): Any? {
      val var10000: HashSet = this.chunkIndex.get(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))
      if (var10000 != null) {
         val var4: Serializable = var10000.add(key)
      } else {
         val var6: Serializable = this.chunkIndex.put(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4), SetsKt.hashSetOf(arrayOf(key)))
      }

      return (T)super.put(key, value)
   }

   public fun containsKey(x: Int, y: Int, z: Int): Boolean {
      return this.findContainingVoxel(x, y, z) != null
   }

   public fun containsKey(vec: Vector3ic): Boolean {
      return this.containsKey(vec.x(), vec.y(), vec.z())
   }

   public fun containsKey(vec: Vector3i): Boolean {
      return this.containsKey(vec.x, vec.y, vec.z)
   }

   public open fun containsKey(key: SparseVoxelPosition): Boolean {
      return super.containsKey(key) || this.findContainingVoxel(key) != null
   }

   public fun containsKeyCertain(key: SparseVoxelPosition): Boolean {
      return super.containsKey(key)
   }

   public fun contains(x: Int, y: Int, z: Int): Boolean {
      return this.get(x, y, z) != null
   }

   public fun contains(vec: Vector3ic): Boolean {
      return this.contains(vec.x(), vec.y(), vec.z())
   }

   public fun contains(vec: Vector3i): Boolean {
      return this.contains(vec.x, vec.y, vec.z)
   }

   public fun remove(x: Int, y: Int, z: Int): Any? {
      val key: SparseVoxelPosition = SparseVoxelPosition(x, y, z, 0L, 8, null)
      val value: Any = super.remove(key)
      if (value != null && this.chunkIndex.containsKey(Vector3i(x shr 4, y shr 4, z shr 4))) {
         val var10000: HashSet = this.chunkIndex.get(Vector3i(x shr 4, y shr 4, z shr 4))
         if (var10000 != null) {
            var10000.remove(key)
         }
      }

      if (value == null) {
         val containingVoxel: SparseVoxelPosition = this.findContainingVoxel(x, y, z)
         if (containingVoxel != null) {
            if (this.chunkIndex.containsKey(Vector3i(containingVoxel.x shr 4, containingVoxel.y shr 4, containingVoxel.z shr 4))) {
               val var7: HashSet = this.chunkIndex.get(Vector3i(containingVoxel.x shr 4, containingVoxel.y shr 4, containingVoxel.z shr 4))
               if (var7 != null) {
                  var7.remove(containingVoxel)
               }
            }

            return (T)super.remove(containingVoxel)
         }
      }

      return (T)value
   }

   public fun remove(vec: Vector3ic): Any? {
      return this.remove(vec.x(), vec.y(), vec.z())
   }

   public fun remove(vec: Vector3i): Any? {
      return this.remove(vec.x, vec.y, vec.z)
   }

   public open fun remove(key: SparseVoxelPosition): Any? {
      val value: Any = super.remove(key)
      if (value != null && this.chunkIndex.containsKey(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))) {
         val var10000: HashSet = this.chunkIndex.get(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))
         if (var10000 != null) {
            var10000.remove(key)
         }
      }

      if (value == null) {
         val containingVoxel: SparseVoxelPosition = this.findContainingVoxel(key)
         if (containingVoxel != null) {
            if (this.chunkIndex.containsKey(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))) {
               val var4: HashSet = this.chunkIndex.get(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))
               if (var4 != null) {
                  var4.remove(key)
               }
            }

            return (T)super.remove(containingVoxel)
         }
      }

      return (T)value
   }

   public fun removeCertain(key: SparseVoxelPosition): Any? {
      if (this.chunkIndex.containsKey(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))) {
         val var10000: HashSet = this.chunkIndex.get(Vector3i(key.x shr 4, key.y shr 4, key.z shr 4))
         if (var10000 != null) {
            var10000.remove(key)
         }
      }

      return (T)super.remove(key)
   }

   public fun findContainingVoxel(x: Int, y: Int, z: Int): SparseVoxelPosition? {
      var var9: Any
      if (this.chunkIndex.containsKey(Vector3i(x shr 4, y shr 4, z shr 4))) {
         var9 = this.chunkIndex.get(Vector3i(x shr 4, y shr 4, z shr 4))
         val voxels: HashSet = var9 as HashSet
         val returnVoxel: SparseVoxelPosition = SparseVoxelPosition(x, y, z, 0L, 8, null)
         if (voxels.contains(SparseVoxelPosition(x, y, z, 0L, 8, null))) {
            return returnVoxel
         }

         var currentExtent: Long = 1L

         do {
            val higherVoxel: SparseVoxelPosition = SparseVoxelPosition.getHigherLevelVoxel$default(returnVoxel, 0, 0, 0, currentExtent, 7, null)
            if (voxels.contains(higherVoxel) && higherVoxel.contains(x, y, z)) {
               return higherVoxel
            }

            currentExtent *= 2L
         } while (currentExtent <= 8L)

         var9 = null
      } else {
         var9 = null
      }

      return (SparseVoxelPosition)var9
   }

   public fun findContainingVoxel(voxel: SparseVoxelPosition): SparseVoxelPosition? {
      var var6: Any
      if (this.chunkIndex.containsKey(Vector3i(voxel.x shr 4, voxel.y shr 4, voxel.z shr 4))) {
         var6 = this.chunkIndex.get(Vector3i(voxel.x shr 4, voxel.y shr 4, voxel.z shr 4))
         val voxels: HashSet = var6 as HashSet
         if ((var6 as HashSet).contains(voxel)) {
            return voxel
         }

         var currentExtent: Long = 1L

         do {
            val higherVoxel: SparseVoxelPosition = SparseVoxelPosition.getHigherLevelVoxel$default(voxel, 0, 0, 0, currentExtent, 7, null)
            if (voxels.contains(higherVoxel) && higherVoxel.contains(voxel)) {
               return higherVoxel
            }

            currentExtent *= 2L
         } while (currentExtent <= 8L)

         var6 = null
      } else {
         var6 = null
      }

      return (SparseVoxelPosition)var6
   }

   public fun getBorderingSides(chunkX: Int, chunkY: Int, chunkZ: Int): Set<SparseVoxelPosition> {
      val returnSet: HashSet = HashSet()
      if (this.chunkIndex.containsKey(Vector3i(chunkX, chunkY, chunkZ))) {
         returnSet.addAll(this.getBorderingDirection(chunkX, chunkY, chunkZ, Vector3i(1, 0, 0)))
         returnSet.addAll(this.getBorderingDirection(chunkX, chunkY, chunkZ, Vector3i(-1, 0, 0)))
         returnSet.addAll(this.getBorderingDirection(chunkX, chunkY, chunkZ, Vector3i(0, 1, 0)))
         returnSet.addAll(this.getBorderingDirection(chunkX, chunkY, chunkZ, Vector3i(0, -1, 0)))
         returnSet.addAll(this.getBorderingDirection(chunkX, chunkY, chunkZ, Vector3i(0, 0, 1)))
         returnSet.addAll(this.getBorderingDirection(chunkX, chunkY, chunkZ, Vector3i(0, 0, -1)))
      }

      return returnSet
   }

   public fun getBorderingDirection(chunkX: Int, chunkY: Int, chunkZ: Int, dir: Vector3ic): Set<SparseVoxelPosition> {
      val returnSet: HashSet = HashSet()
      if (this.chunkIndex.containsKey(Vector3i(chunkX, chunkY, chunkZ))) {
         val var10000: Any = this.chunkIndex.get(Vector3i(chunkX, chunkY, chunkZ))
         val voxels: HashSet = var10000 as HashSet
         val checkerVoxel: SparseVoxelPosition = SparseVoxelPosition((chunkX shl 4) + dir.x(), (chunkY shl 4) + dir.y(), (chunkZ shl 4) + dir.z(), 16L)

         for (voxel in voxels) {
            if (checkerVoxel.contains(voxel.relativeTo(dir))) {
               returnSet.add(voxel)
            }
         }
      }

      return returnSet
   }

   public fun containsNodesToFill(voxel: SparseVoxelPosition): Boolean {
      val required: Long = voxel.getVolume()
      var filled: Long = 0L
      if (this.chunkIndex.containsKey(Vector3i(voxel.x shr 4, voxel.y shr 4, voxel.z shr 4))) {
         val var10000: Any = this.chunkIndex.get(Vector3i(voxel.x shr 4, voxel.y shr 4, voxel.z shr 4))

         for (key in var10000 as HashSet) {
            if (key.contains(voxel)) {
               filled += key.getVolume()
            } else if (voxel.contains(key)) {
               filled += key.getVolume()
            }
         }
      }

      return filled >= required
   }

   public fun containsNodesToFill(voxel: SparseVoxelPosition, extraVolume: Long): Boolean {
      val required: Long = voxel.getVolume()
      var filled: Long = extraVolume
      if (this.chunkIndex.containsKey(Vector3i(voxel.x shr 4, voxel.y shr 4, voxel.z shr 4))) {
         val var10000: Any = this.chunkIndex.get(Vector3i(voxel.x shr 4, voxel.y shr 4, voxel.z shr 4))

         for (key in var10000 as HashSet) {
            if (key.contains(voxel)) {
               filled += key.getVolume()
            } else if (voxel.contains(key)) {
               filled += key.getVolume()
            }
         }
      }

      return filled >= required
   }

   // $VF: bridge methodSupplier
   override final fun get(key: Any): T {
      if (key !is SparseVoxelPosition) null else this.get(key as SparseVoxelPosition)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun put(key: Any, value: Any) {
      this.put(key as SparseVoxelPosition, (T)value)
   }

   // $VF: bridge methodSupplier
   override final fun containsKey(key: Any): Boolean {
      key is SparseVoxelPosition && this.containsKey(key as SparseVoxelPosition)
   }

   // $VF: bridge methodSupplier
   override final fun remove(key: Any): T {
      if (key !is SparseVoxelPosition) null else this.remove(key as SparseVoxelPosition)
   }

   // $VF: bridge methodSupplier
   fun remove(key: SparseVoxelPosition, value: Any): Boolean {
      super.remove(key, value)
   }

   // $VF: bridge methodSupplier
   override final fun remove(key: Any, value: Any): Boolean {
      if (key !is SparseVoxelPosition) {
         false
      } else {
         if (value == null) {
         }

         this.remove(key as SparseVoxelPosition, value)
      }
   }

   // $VF: bridge methodSupplier
   fun getOrDefault(key: SparseVoxelPosition, defaultValue: Any) {
      super.getOrDefault(key, defaultValue)
   }

   // $VF: bridge methodSupplier
   override final fun getOrDefault(key: Any, defaultValue: Any) {
      if (key !is SparseVoxelPosition) defaultValue else this.getOrDefault(key as SparseVoxelPosition, defaultValue)
   }

   // $VF: bridge methodSupplier
   fun getSize(): Int {
      super.size()
   }

   // $VF: bridge methodSupplier
   override final fun size(): Int {
      this.getSize()
   }

   // $VF: bridge methodSupplier
   fun getValues(): MutableCollection<Any> {
      super.values()
   }

   // $VF: bridge methodSupplier
   override final fun values(): MutableCollection<T> {
      this.getValues()
   }

   // $VF: bridge methodSupplier
   fun getKeys(): MutableSet<SparseVoxelPosition> {
      super.keySet()
   }

   // $VF: bridge methodSupplier
   override final fun keySet(): MutableSet<SparseVoxelPosition> {
      this.getKeys()
   }

   // $VF: bridge methodSupplier
   fun getEntries(): MutableSet<MutableMap.MutableEntry<SparseVoxelPosition, Any>> {
      super.entrySet()
   }

   // $VF: bridge methodSupplier
   override final fun entrySet(): MutableSet<MutableMap.MutableEntry<SparseVoxelPosition, T>> {
      this.getEntries()
   }
}
