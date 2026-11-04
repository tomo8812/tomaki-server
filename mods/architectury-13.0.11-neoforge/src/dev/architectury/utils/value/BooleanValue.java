package dev.architectury.utils.value;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.function.BooleanSupplier;

public interface BooleanValue extends Value<Boolean>, BooleanSupplier, BooleanConsumer {
   default Boolean get() {
      return this.getAsBoolean();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default Object get() {
      return this.get();
   }
}
