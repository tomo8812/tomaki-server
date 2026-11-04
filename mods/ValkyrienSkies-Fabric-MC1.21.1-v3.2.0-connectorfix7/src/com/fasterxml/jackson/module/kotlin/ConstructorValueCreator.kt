package com.fasterxml.jackson.module.kotlin

import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.KCallablesJvm

internal class ConstructorValueCreator<T>(callable: KFunction<Any>) : ValueCreator() {
   protected open val callable: KFunction<Any>
   protected open val accessible: Boolean

   init {
      this.callable = callable
      this.accessible = KCallablesJvm.isAccessible(this.callable as KCallable)
      if (!this.accessible) {
         KCallablesJvm.setAccessible(this.callable as KCallable, true)
      }
   }
}
