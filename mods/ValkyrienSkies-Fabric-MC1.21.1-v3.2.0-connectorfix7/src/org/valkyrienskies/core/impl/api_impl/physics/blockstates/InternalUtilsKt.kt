package org.valkyrienskies.core.impl.api_impl.physics.blockstates

import org.joml.primitives.AABBic

public fun require4bits(name: String, aabb: AABBic) {
   var var2: Int = aabb.minX()
   if (var2 < 0 || var2 >= 16) {
      throw IllegalArgumentException(("$name.minX must be >= 0 and <= 15, but is $var2").toString())
   } else {
      var2 = aabb.minY()
      if (var2 < 0 || var2 >= 16) {
         throw IllegalArgumentException(("$name.minY must be >= 0 and <= 15, but is $var2").toString())
      } else {
         var2 = aabb.minZ()
         if (var2 < 0 || var2 >= 16) {
            throw IllegalArgumentException(("$name.minZ must be >= 0 and <= 15, but is $var2").toString())
         } else {
            var2 = aabb.maxX()
            if (var2 < 0 || var2 >= 16) {
               throw IllegalArgumentException(("$name.maxX must be >= 0 and <= 15, but is $var2").toString())
            } else {
               var2 = aabb.maxY()
               if (var2 < 0 || var2 >= 16) {
                  throw IllegalArgumentException(("$name.maxY must be >= 0 and <= 15, but is $var2").toString())
               } else {
                  var2 = aabb.maxZ()
                  if (var2 < 0 || var2 >= 16) {
                     throw IllegalArgumentException(("$name.maxZ must be >= 0 and <= 15, but is $var2").toString())
                  }
               }
            }
         }
      }
   }
}

public fun require4bits(name: String, value: Int) {
   if (value < 0 || value >= 16) {
      throw IllegalArgumentException(("$name must be >= 0 and <= 15, but is $value").toString())
   }
}

public inline fun require4bits(name: () -> String, value: Int) {
   if (value < 0 || value >= 16) {
      throw IllegalArgumentException(("${name()} must be >= 0 and <= 15, but is $value").toString())
   }
}
