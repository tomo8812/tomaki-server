package net.satisfy.farm_and_charm.core.registry;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class ArmorMaterialRegistry {
   public static final Holder<ArmorMaterial> CLOTH = register("cloth", (EnumMap<Type, Integer>)Util.make(new EnumMap(Type.class), m -> {
      m.put(Type.BOOTS, 1);
      m.put(Type.LEGGINGS, 1);
      m.put(Type.CHESTPLATE, 2);
      m.put(Type.HELMET, 1);
   }), () -> Ingredient.of(ItemTags.WOOL));
   public static final Holder<ArmorMaterial> JEWELRY = register("jewelry", (EnumMap<Type, Integer>)Util.make(new EnumMap(Type.class), m -> {
      m.put(Type.BOOTS, 1);
      m.put(Type.LEGGINGS, 1);
      m.put(Type.CHESTPLATE, 1);
      m.put(Type.HELMET, 1);
   }), () -> Ingredient.of(new ItemLike[]{Items.GOLD_INGOT}));
   private static final Map<String, Holder<ArmorMaterial>> CACHE = new HashMap<>();

   public static Holder<ArmorMaterial> withTextureNoOverlay(Holder<ArmorMaterial> base, ResourceLocation texturePng) {
      return withLayer(base, toLayerPrefix(texturePng));
   }

   private static ResourceLocation toLayerPrefix(ResourceLocation png) {
      String p = png.getPath();
      int slash = p.lastIndexOf(47);
      String name = slash >= 0 ? p.substring(slash + 1) : p;
      int dot = name.lastIndexOf(46);
      if (dot >= 0) {
         name = name.substring(0, dot);
      }

      return ResourceLocation.fromNamespaceAndPath(png.getNamespace(), name);
   }

   private static Holder<ArmorMaterial> register(String id, EnumMap<Type, Integer> map, Supplier<Ingredient> repair) {
      EnumMap<Type, Integer> copy = new EnumMap<>(Type.class);

      for (Type t : Type.values()) {
         Integer v = map.get(t);
         if (v == null) {
            v = 0;
         }

         copy.put(t, v);
      }

      List<Layer> layers = List.of();
      return Registry.registerForHolder(
         BuiltInRegistries.ARMOR_MATERIAL,
         ResourceLocation.fromNamespaceAndPath("farm_and_charm", id),
         new ArmorMaterial(copy, 15, SoundEvents.ARMOR_EQUIP_LEATHER, repair, layers, 0.0F, 0.0F)
      );
   }

   private static Holder<ArmorMaterial> withLayer(Holder<ArmorMaterial> base, ResourceLocation layerPrefix) {
      String key = BuiltInRegistries.ARMOR_MATERIAL.getKey((ArmorMaterial)base.value()) + "|" + layerPrefix;
      Holder<ArmorMaterial> cached = CACHE.get(key);
      if (cached != null) {
         return cached;
      }

      EnumMap<Type, Integer> def = new EnumMap<>(Type.class);

      for (Type t : Type.values()) {
         def.put(t, ((ArmorMaterial)base.value()).getDefense(t));
      }

      List<Layer> layers = List.of(new Layer(layerPrefix, "", true));
      Holder<ArmorMaterial> res = Registry.registerForHolder(
         BuiltInRegistries.ARMOR_MATERIAL,
         ResourceLocation.fromNamespaceAndPath("farm_and_charm", "mat_" + layerPrefix.getNamespace() + "_" + layerPrefix.getPath().replace('/', '_')),
         new ArmorMaterial(
            def,
            ((ArmorMaterial)base.value()).enchantmentValue(),
            ((ArmorMaterial)base.value()).equipSound(),
            ((ArmorMaterial)base.value()).repairIngredient(),
            layers,
            ((ArmorMaterial)base.value()).toughness(),
            ((ArmorMaterial)base.value()).knockbackResistance()
         )
      );
      CACHE.put(key, res);
      return res;
   }

   private ArmorMaterialRegistry() {
   }
}
