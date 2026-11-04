package com.fasterxml.jackson.module.kotlin

public enum class SingletonSupport {
   DISABLED,
   CANONICALIZE;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<SingletonSupport> {
      arrayOf(DISABLED, CANONICALIZE)
   }
}
