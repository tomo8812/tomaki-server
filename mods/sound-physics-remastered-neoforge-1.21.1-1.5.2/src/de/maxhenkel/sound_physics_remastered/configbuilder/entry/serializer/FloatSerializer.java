package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class FloatSerializer implements ValueSerializer<Float> {
   public static final FloatSerializer INSTANCE = new FloatSerializer();

   @Nullable
   public Float deserialize(String str) {
      try {
         return Float.parseFloat(str);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   @Nullable
   public String serialize(Float val) {
      return String.valueOf(val);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public String serialize(Object var1) {
      return this.serialize((Float)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object deserialize(String var1) {
      return this.deserialize(var1);
   }
}
