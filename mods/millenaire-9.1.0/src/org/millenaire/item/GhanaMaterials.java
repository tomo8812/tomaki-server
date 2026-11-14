package org.millenaire.item;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class GhanaMaterials {
   public static final int GHANA_ARMOR_DURABILITY_MULTIPLIER = 7;
   public static final Holder<ArmorMaterial> GHANA_ARMOR = Registry.registerForHolder(
      BuiltInRegistries.ARMOR_MATERIAL,
      ResourceLocation.fromNamespaceAndPath("millenaire", "ghana"),
      new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 3);
            map.put(Type.HELMET, 1);
         }),
         10,
         SoundEvents.ARMOR_EQUIP_GENERIC,
         () -> Ingredient.of(new ItemLike[]{Items.IRON_INGOT}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("millenaire", "ghana"))),
         0.0F,
         0.0F
      )
   );

   private GhanaMaterials() {
   }
}
