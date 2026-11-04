package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class LongSerializer implements ValueSerializer<Long> {
   public static final LongSerializer INSTANCE = new LongSerializer();

   @Nullable
   public Long deserialize(String str) {
      try {
         return Long.parseLong(str);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   @Nullable
   public String serialize(Long val) {
      return String.valueOf(val);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public String serialize(Object var1) {
      return this.serialize((Long)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object deserialize(String var1) {
      return this.deserialize(var1);
   }
}
