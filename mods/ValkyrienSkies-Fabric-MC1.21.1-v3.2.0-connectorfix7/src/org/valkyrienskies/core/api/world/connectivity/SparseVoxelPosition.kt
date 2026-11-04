package org.valkyrienskies.core.api.world.connectivity

import java.text.NumberFormat
import java.util.LinkedHashSet
import org.joml.Vector3i
import org.joml.Vector3ic

public class SparseVoxelPosition(x: Int, y: Int, z: Int, extent: Long = 1L) : Vector3i(x, y, z) {
   public final var extent: Long

   init {
      this.extent = extent
   }

   public override fun toString(): String {
      return "${super.toString()} with extent: ${this.extent}"
   }

   public override fun toString(formatter: NumberFormat): String {
      return "${super.toString(formatter)} with extent: ${formatter.format(this.extent)}"
   }

   public override operator fun equals(other: Any?): Boolean {
      return this === other
         || other is SparseVoxelPosition
            && this.x == (other as SparseVoxelPosition).x
            && this.y == (other as SparseVoxelPosition).y
            && this.z == (other as SparseVoxelPosition).z
            && this.extent == (other as SparseVoxelPosition).extent
         }

   public fun equalsCertain(other: SparseVoxelPosition): Boolean {
      return this.x == other.x && this.y == other.y && this.z == other.z && this.extent == other.extent
   }

   public override fun equals(x: Int, y: Int, z: Int): Boolean {
      return if (this.extent == 1L)
         super.equals(x, y, z)
         else
         x >= this.x
            && x <= this.x + (int)this.extent - 1
            && y >= this.y
            && y <= this.y + (int)this.extent - 1
            && z >= this.z
            && z <= this.z + (int)this.extent - 1
         }

   public fun contains(x: Int, y: Int, z: Int): Boolean {
      return x >= this.x
         && x <= this.x + (int)this.extent - 1
         && y >= this.y
         && y <= this.y + (int)this.extent - 1
         && z >= this.z
         && z <= this.z + (int)this.extent - 1
      }

   public fun contains(other: SparseVoxelPosition): Boolean {
      return other.x >= this.x
         && other.x + (int)other.extent - 1 <= this.x + (int)this.extent - 1
         && other.y >= this.y
         && other.y + (int)other.extent - 1 <= this.y + (int)this.extent - 1
         && other.z >= this.z
         && other.z + (int)other.extent - 1 <= this.z + (int)this.extent - 1
      }

   public fun contains(vec: Vector3i): Boolean {
      return this.contains(vec.x, vec.y, vec.z)
   }

   public fun contains(vec: Vector3ic): Boolean {
      return this.contains(vec.x(), vec.y(), vec.z())
   }

   public fun getVolume(): Long {
      return this.extent * this.extent * this.extent
   }

   public fun modExtent(extent: Long): SparseVoxelPosition {
      this.extent = extent
      return this
   }

   public fun overlaps(other: SparseVoxelPosition): Boolean {
      return this.x <= other.x + (int)other.extent - 1
         && this.x + (int)this.extent - 1 >= other.x
         && this.y <= other.y + (int)other.extent - 1
         && this.y + (int)this.extent - 1 >= other.y
         && this.z <= other.z + (int)other.extent - 1
         && this.z + (int)this.extent - 1 >= other.z
      }

   public fun getContaining(): Set<Vector3i> {
      val set: java.util.Set = LinkedHashSet()
      var i: Int = this.x

      for (var3 in this.x + (int)this.extent..i) {
         var j: Int = this.y

         for (var5 in this.y + (int)this.extent..j) {
            var k: Int = this.z

            for (var7 in this.z + (int)this.extent..k) {
               set.add(Vector3i(i, j, k))
            }
         }
      }

      return set
   }

   public fun getContainingAsVoxel(): Set<SparseVoxelPosition> {
      val set: java.util.Set = LinkedHashSet()
      var i: Int = this.x

      for (var3 in this.x + (int)this.extent..i) {
         var j: Int = this.y

         for (var5 in this.y + (int)this.extent..j) {
            var k: Int = this.z

            for (var7 in this.z + (int)this.extent..k) {
               set.add(SparseVoxelPosition(i, j, k, 0L, 8, null))
            }
         }
      }

      return set
   }

   public fun getHigherLevelVoxel(x: Int = this.x, y: Int = this.y, z: Int = this.z, currentExtent: Long = this.extent): SparseVoxelPosition {
      val targetExtent: Long = currentExtent * 2L
      val chunkX: Int = x shr 4
      val chunkY: Int = y shr 4
      val chunkZ: Int = z shr 4
      val cornerX: Int = chunkX shl 4
      val cornerY: Int = chunkY shl 4
      val cornerZ: Int = chunkZ shl 4
      var voxel: SparseVoxelPosition = SparseVoxelPosition(cornerX, cornerY, chunkZ shl 4, 16L)
      var xInChunk: Int = x - cornerX
      var yInChunk: Int = y - cornerY
      var zInChunk: Int = z - cornerZ
      if (targetExtent == 16L) {
         return voxel
      } else {
         var loopExtent: Long = 8L

         do {
            val voxelUpper: Boolean = yInChunk / (int)loopExtent != 0
            val voxelSouth: Boolean = zInChunk / (int)loopExtent != 0
            voxel = SparseVoxelPosition(
               voxel.x + (if (xInChunk / (int)loopExtent != 0) (int)loopExtent else 0),
               voxel.y + (if (voxelUpper) (int)loopExtent else 0),
               voxel.z + (if (zInChunk / (int)loopExtent != 0) (int)loopExtent else 0),
               loopExtent
            )
            xInChunk = if (xInChunk / (int)loopExtent != 0) xInChunk - (int)loopExtent else xInChunk
            yInChunk = if (voxelUpper) yInChunk - (int)loopExtent else yInChunk
            zInChunk = if (voxelSouth) zInChunk - (int)loopExtent else zInChunk
            loopExtent /= 2L
         } while (loopExtent >= targetExtent)

         return voxel.modExtent(targetExtent)
      }
   }

   public fun relativeTo(dir: Vector3ic): Vector3ic {
      return SparseVoxelPosition(
         (if (dir.x() > 0) this.x + (int)this.extent - 1 else this.x) + dir.x(),
         (if (dir.y() > 0) this.y + (int)this.extent - 1 else this.y) + dir.y(),
         (if (dir.z() > 0) this.z + (int)this.extent - 1 else this.z) + dir.z(),
         this.extent
      )
   }

   public fun allRelativeTo(includeCorners: Boolean, includeEdges: Boolean): Set<Vector3ic> {
      val neighbors: java.util.Set = LinkedHashSet()

      for (i in -1..1) {
         for (j in -1..1) {
            for (k in -1..1) {
               if ((i != 0 || j != 0 || k != 0)
                  && (includeCorners || i == 0 || j == 0 || k == 0)
                  && (includeEdges || (i == 0 || j == 0) && (i == 0 || k == 0) && (j == 0 || k == 0))) {
                  neighbors.add(
                     Vector3i(
                        this.x + (if (i < 0) i else i * (int)this.extent),
                        this.y + (if (j < 0) j else j * (int)this.extent),
                        this.z + (if (k < 0) k else k * (int)this.extent)
                     )
                  )
               }
            }
         }
      }

      return neighbors
   }

   public override fun hashCode(): Int {
      return super.hashCode() * 31 + (int)this.extent
   }
}
