package org.valkyrienskies.core.impl.shadow

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

public object Fk {
   @JvmStatic
   public fun a(): SecretKey {
      val var0: KeyGenerator
      (var0 = KeyGenerator.getInstance("AES")).init(128)
      val var10000: SecretKey = var0.generateKey()
      return var10000
   }
}
