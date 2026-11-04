package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class StringSerializer implements ValueSerializer<String> {
   public static final StringSerializer INSTANCE = new StringSerializer();

   @Nullable
   public String deserialize(String str) {
      return str;
   }

   @Nullable
   public String serialize(String val) {
      return val;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public String serialize(Object var1) {
      return this.serialize((String)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   public Object deserialize(String var1) {
      return this.deserialize(var1);
   }
}
