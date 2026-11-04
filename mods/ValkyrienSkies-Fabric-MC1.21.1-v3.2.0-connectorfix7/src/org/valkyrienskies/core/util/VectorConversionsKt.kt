package org.valkyrienskies.core.util

import org.joml.Vector3dc
import org.joml.Vector3ic
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic

public operator fun Vector3ic.component1(): Int {
   return x
}

public operator fun Vector3ic.component2(): Int {
   return y
}

public operator fun Vector3ic.component3(): Int {
   return z
}

public final val x: Int
   public final get() {
      return `$this$x`.x()
   }


public final val y: Int
   public final get() {
      return `$this$y`.y()
   }


public final val z: Int
   public final get() {
      return `$this$z`.z()
   }


public fun Vector3ic.multiplyTerms(): Int {
   return x * y * z
}

public operator fun Vector3dc.component1(): Double {
   return x
}

public operator fun Vector3dc.component2(): Double {
   return y
}

public operator fun Vector3dc.component3(): Double {
   return z
}

public final val x: Double
   public final get() {
      return `$this$x`.x()
   }


public final val y: Double
   public final get() {
      return `$this$y`.y()
   }


public final val z: Double
   public final get() {
      return `$this$z`.z()
   }


public fun Vector3dc.multiplyTerms(): Double {
   return x * y * z
}

public fun Vector3dc.addTerms(): Double {
   return x + y + z
}

public fun Vector3dc.horizontalLengthSq(): Double {
   return x * x + z * z
}

public fun Vector3dc.differenceHorLengthSq(other: Vector3dc): Double {
   val xDif: Double = `$this$differenceHorLengthSq`.x() - other.x()
   val zDif: Double = `$this$differenceHorLengthSq`.z() - other.z()
   return xDif * xDif + zDif * zDif
}

public fun Vector3dc.horizontalLength(): Double {
   return Math.sqrt(horizontalLengthSq(`$this$horizontalLength`))
}

public fun AABBdc.toAABBi(dest: AABBi = AABBi()): AABBi {
   dest.setMin((int)Math.rint(`$this$toAABBi`.minX()), (int)Math.rint(`$this$toAABBi`.minY()), (int)Math.rint(`$this$toAABBi`.minZ()))
   dest.setMax((int)Math.rint(`$this$toAABBi`.maxX()), (int)Math.rint(`$this$toAABBi`.maxY()), (int)Math.rint(`$this$toAABBi`.maxZ()))
   return dest
}

public fun AABBic.expand(expansion: Int, dest: AABBi = AABBi()): AABBi {
   dest.minX = `$this$expand`.minX() - expansion
   dest.minY = `$this$expand`.minY() - expansion
   dest.minZ = `$this$expand`.minZ() - expansion
   dest.maxX = `$this$expand`.maxX() + expansion
   dest.maxY = `$this$expand`.maxY() + expansion
   dest.maxZ = `$this$expand`.maxZ() + expansion
   return dest
}
