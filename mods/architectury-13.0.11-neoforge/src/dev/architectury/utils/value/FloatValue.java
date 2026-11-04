package dev.architectury.utils.value;

import it.unimi.dsi.fastutil.floats.FloatConsumer;

public interface FloatValue extends Value<Float>, FloatSupplier, FloatConsumer {
   default Float get() {
      return this.getAsFloat();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default Object get() {
      return this.get();
   }
}
