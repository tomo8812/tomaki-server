package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class IntegerSerializer implements ValueSerializer<Integer> {
   public static final IntegerSerializer INSTANCE = new IntegerSerializer();

   @Nullable
   public Integer deserialize(String str) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   @Nullable
   public String serialize(Integer val) {
      return String.valueOf(val);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public String serialize(Object var1) {
      return this.serialize((Integer)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object deserialize(String var1) {
      return this.deserialize(var1);
   }
}
