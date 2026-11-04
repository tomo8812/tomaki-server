package org.valkyrienskies.core.util.datastructures

public object MurmurHash3 {
   private const val C1_32: Int = -862048943
   private const val C2_32: Int = 461845907
   private const val R1_32: Int = 15
   private const val R2_32: Int = 13
   private const val M_32: Int = 5
   private const val N_32: Int = -430675100

   public fun mix32(k: Int, hash: Int): Int {
      return Integer.rotateLeft(hash xor Integer.rotateLeft(k * -862048943, 15) * 461845907, 13) * 5 + -430675100
   }

   public fun fmix32(hash: Int): Int {
      return ((hash xor hash ushr 16) * -2048144789 xor (hash xor hash ushr 16) * -2048144789 ushr 13) * -1028477387 xor (
            (hash xor hash ushr 16) * -2048144789 xor (hash xor hash ushr 16) * -2048144789 ushr 13
         )
         * -1028477387 ushr 16
   }
}
