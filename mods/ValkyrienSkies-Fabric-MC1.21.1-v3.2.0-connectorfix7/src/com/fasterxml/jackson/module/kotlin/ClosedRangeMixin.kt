package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore

internal abstract class ClosedRangeMixin<T> {
   public final val start: Any
   public final val endInclusive: Any

   @JsonCreator
   open fun ClosedRangeMixin(start: T, endInclusive: T) {
      this.start = (T)start
      this.endInclusive = (T)endInclusive
   }

   @JsonIgnore
   public abstract fun getEnd(): Any {
   }

   @JsonIgnore
   public abstract fun getFirst(): Any {
   }

   @JsonIgnore
   public abstract fun getLast(): Any {
   }

   @JsonIgnore
   public abstract fun getIncrement(): Any {
   }

   @JsonIgnore
   public abstract fun isEmpty(): Boolean {
   }

   @JsonIgnore
   public abstract fun getStep(): Any {
   }

   @JsonIgnore
   public abstract fun getEndExclusive(): Any {
   }
}
