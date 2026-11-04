package de.maxhenkel.sound_physics_remastered.configbuilder.custom.serializer;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import java.util.UUID;

public class UUIDSerializer implements ValueSerializer<UUID> {
   public static final UUIDSerializer INSTANCE = new UUIDSerializer();

   public UUID deserialize(String str) {
      return UUID.fromString(str);
   }

   public String serialize(UUID val) {
      return val.toString();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public String serialize(Object var1) {
      return this.serialize((UUID)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object deserialize(String var1) {
      return this.deserialize(var1);
   }
}
