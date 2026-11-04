package org.valkyrienskies.mod.util

import net.minecraft.class_2487
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc

public fun class_2487.putVector3d(prefix: String, vector3d: Vector3dc) {
   `$this$putVector3d`.method_10549("$prefixx", vector3d.x())
   `$this$putVector3d`.method_10549("$prefixy", vector3d.y())
   `$this$putVector3d`.method_10549("$prefixz", vector3d.z())
}

public fun class_2487.getVector3d(prefix: String): Vector3d? {
   return if (`$this$getVector3d`.method_10545("$prefixx") && `$this$getVector3d`.method_10545("$prefixy") && `$this$getVector3d`.method_10545("$prefixz"))
      Vector3d(`$this$getVector3d`.method_10574("$prefixx"), `$this$getVector3d`.method_10574("$prefixy"), `$this$getVector3d`.method_10574("$prefixz"))
      else
      null
   }

public fun class_2487.putQuatd(prefix: String, quat: Quaterniondc) {
   `$this$putQuatd`.method_10549("$prefixx", quat.x())
   `$this$putQuatd`.method_10549("$prefixy", quat.y())
   `$this$putQuatd`.method_10549("$prefixz", quat.z())
   `$this$putQuatd`.method_10549("$prefixw", quat.w())
}

public fun class_2487.getQuatd(prefix: String): Quaterniond? {
   return if (`$this$getQuatd`.method_10545("$prefixx")
         && `$this$getQuatd`.method_10545("$prefixy")
         && `$this$getQuatd`.method_10545("$prefixz")
         && `$this$getQuatd`.method_10545("$prefixw"))
      Quaterniond(
         `$this$getQuatd`.method_10574("$prefixx"),
         `$this$getQuatd`.method_10574("$prefixy"),
         `$this$getQuatd`.method_10574("$prefixz"),
         `$this$getQuatd`.method_10574("$prefixw")
      )
      else
      null
   }
