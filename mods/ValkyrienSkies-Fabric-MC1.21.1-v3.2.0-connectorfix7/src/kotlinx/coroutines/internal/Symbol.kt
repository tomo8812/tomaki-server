package kotlinx.coroutines.internal

import org.jetbrains.annotations.NotNull

internal class Symbol(symbol: String) {
   @JvmField
   @NotNull
   public final val symbol: String

   init {
      this.symbol = symbol
   }

   public override fun toString(): String {
      return "<${this.symbol}>"
   }

   public inline fun <T> unbox(value: Any?): Any {
      return (T)(if (value === this) null else value)
   }
}
