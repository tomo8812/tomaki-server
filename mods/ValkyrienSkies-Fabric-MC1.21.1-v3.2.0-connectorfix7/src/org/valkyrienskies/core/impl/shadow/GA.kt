package org.valkyrienskies.core.impl.shadow

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors

public object GA : Gz {
   @JvmStatic
   public GA INSTANCE;
   private const val b: Int = 6801
   private const val a: Int = 3
   private final val c: List<String>

   public override fun a(): String {
      return a(3)
   }

   @JvmStatic
   public fun a(p0: Int): String {
      val var10000: Any = ThreadLocalRandom.current()
         .ints(3L, 0, d.size())
         .mapToObj(org/valkyrienskies/core/impl/shadow/GA##Lambda_0_75())
         .collect(Collectors.joining("-"))
         return var10000 as java.lang.String
   }

   @JvmStatic
   fun b(var0: Int): java.lang.String {
      d.get(var0) as java.lang.String
   }

   @JvmStatic
   fun {
      val var10000: ClassLoader = (INSTANCE = GA()).getClass().getClassLoader()
      val var3: InputStream = var10000.getResourceAsStream("nounlist.txt")
      val var4: Any = BufferedReader(InputStreamReader(var3, Charsets.UTF_8), 8192).lines().collect(Collectors.toList())
      d = var4 as MutableList<java.lang.String>
   }
}
