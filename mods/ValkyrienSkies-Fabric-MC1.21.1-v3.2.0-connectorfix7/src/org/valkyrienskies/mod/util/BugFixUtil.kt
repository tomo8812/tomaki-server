package org.valkyrienskies.mod.util

import net.minecraft.class_238

public object BugFixUtil {
   public fun isCollisionBoxTooBig(aabb: class_238): Boolean {
      return aabb.method_17939() * aabb.method_17940() * aabb.method_17941() > 1.0E8
   }
}
