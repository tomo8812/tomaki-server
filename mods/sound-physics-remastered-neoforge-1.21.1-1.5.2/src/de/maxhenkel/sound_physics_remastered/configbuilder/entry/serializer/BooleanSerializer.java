package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class BooleanSerializer implements ValueSerializer<Boolean> {
   public static final BooleanSerializer INSTANCE = new BooleanSerializer();

   @Nullable
   public Boolean deserialize(String str) {
      return Boolean.valueOf(str);
   }

   @Nullable
   public String serialize(Boolean val) {
      return String.valueOf(val);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public String serialize(Object var1) {
      return this.serialize((Boolean)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object deserialize(String var1) {
      return this.deserialize(var1);
   }
}
