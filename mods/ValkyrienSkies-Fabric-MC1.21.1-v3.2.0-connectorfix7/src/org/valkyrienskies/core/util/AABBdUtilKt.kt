package org.valkyrienskies.core.util

import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic

public fun AABBd.set(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): AABBd {
   `$this$set`.setMin(minX, minY, minZ)
   `$this$set`.setMax(maxX, maxY, maxZ)
   return `$this$set`
}

public fun AABBd.expand(expansion: Double): AABBd {
   `$this$expand`.minX -= expansion
   `$this$expand`.minY -= expansion
   `$this$expand`.minZ -= expansion
   `$this$expand`.maxX += expansion
   `$this$expand`.maxY += expansion
   `$this$expand`.maxZ += expansion
   return `$this$expand`
}

public fun AABBd.getSize(): Double {
   return (`$this$getSize`.lengthX() + `$this$getSize`.lengthY() + `$this$getSize`.lengthZ()) / 3.0
}

public fun AABBd.extend(extension: Vector3dc): AABBd {
   if (extension.x() > 0.0) {
      `$this$extend`.maxX = `$this$extend`.maxX + extension.x()
   } else {
      `$this$extend`.minX = `$this$extend`.minX + extension.x()
   }

   if (extension.y() > 0.0) {
      `$this$extend`.maxY = `$this$extend`.maxY + extension.y()
   } else {
      `$this$extend`.minY = `$this$extend`.minY + extension.y()
   }

   if (extension.z() > 0.0) {
      `$this$extend`.maxZ = `$this$extend`.maxZ + extension.z()
   } else {
      `$this$extend`.minZ = `$this$extend`.minZ + extension.z()
   }

   return `$this$extend`
}

public fun AABBdc.signedDistanceTo(pos: Vector3dc): Double {
   return if (`$this$signedDistanceTo`.containsPoint(pos))
      -Math.min(
         (`$this$signedDistanceTo`.maxX() - `$this$signedDistanceTo`.minX()) / 2.0
            - Math.abs(pos.x() - (`$this$signedDistanceTo`.minX() + `$this$signedDistanceTo`.maxX()) / 2.0),
         Math.min(
            (`$this$signedDistanceTo`.maxX() - `$this$signedDistanceTo`.minX()) / 2.0
               - Math.abs(pos.y() - (`$this$signedDistanceTo`.minY() + `$this$signedDistanceTo`.maxY()) / 2.0),
            (`$this$signedDistanceTo`.maxX() - `$this$signedDistanceTo`.minX()) / 2.0
               - Math.abs(pos.z() - (`$this$signedDistanceTo`.minZ() + `$this$signedDistanceTo`.maxZ()) / 2.0)
         )
      )
      else
      pos.distance(
         Math.max(`$this$signedDistanceTo`.minX(), Math.min(`$this$signedDistanceTo`.maxX(), pos.x())),
         Math.max(`$this$signedDistanceTo`.minY(), Math.min(`$this$signedDistanceTo`.maxY(), pos.y())),
         Math.max(`$this$signedDistanceTo`.minZ(), Math.min(`$this$signedDistanceTo`.maxZ(), pos.z()))
      )
   }

public fun AABBic.toAABBd(dest: AABBd): AABBd {
   dest.minX = `$this$toAABBd`.minX()
   dest.minY = `$this$toAABBd`.minY()
   dest.minZ = `$this$toAABBd`.minZ()
   dest.maxX = `$this$toAABBd`.maxX()
   dest.maxY = `$this$toAABBd`.maxY()
   dest.maxZ = `$this$toAABBd`.maxZ()
   return dest
}

public fun AABBdc.intersectsAABB(other: AABBdc): Boolean {
   return `$this$intersectsAABB`.intersectsAABB(other as AABBd)
}
