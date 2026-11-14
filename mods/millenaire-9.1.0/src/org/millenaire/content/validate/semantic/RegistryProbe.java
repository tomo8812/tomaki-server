package org.millenaire.content.validate.semantic;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public final class RegistryProbe {
   public static final RegistryProbe INSTANCE = new RegistryProbe();

   private RegistryProbe() {
   }

   public boolean itemExists(String id) {
      ResourceLocation rl = ResourceLocation.tryParse(id);
      return rl != null && BuiltInRegistries.ITEM.containsKey(rl);
   }

   public boolean itemTagExists(String id) {
      String bare = id.startsWith("#") ? id.substring(1) : id;
      ResourceLocation rl = ResourceLocation.tryParse(bare);
      return rl == null ? false : BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, rl)).isPresent();
   }

   public boolean blockExists(String id) {
      ResourceLocation rl = ResourceLocation.tryParse(id);
      return rl != null && BuiltInRegistries.BLOCK.containsKey(rl);
   }

   public String invalidBlockProperty(String blockId, Map<String, String> properties) {
      ResourceLocation rl = ResourceLocation.tryParse(blockId);
      if (rl == null) {
         return null;
      }

      Block block = (Block)BuiltInRegistries.BLOCK.get(rl);
      StateDefinition<Block, ?> definition = block.getStateDefinition();

      for (Entry<String, String> e : properties.entrySet()) {
         Property<?> property = definition.getProperty(e.getKey());
         if (property == null) {
            return e.getKey() + "=" + e.getValue() + " (no such property)";
         }

         Optional<?> value = property.getValue(e.getValue());
         if (value.isEmpty()) {
            return e.getKey() + "=" + e.getValue() + " (no such value)";
         }
      }

      return null;
   }
}
