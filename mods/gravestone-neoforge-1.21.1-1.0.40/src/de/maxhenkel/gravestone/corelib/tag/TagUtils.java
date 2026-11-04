package de.maxhenkel.gravestone.corelib.tag;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class TagUtils {
   public static Tag<Block> AIR_BLOCK_TAG = new SingleElementTag<>(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), Blocks.AIR);
   public static Tag<Item> AIR_ITEM_TAG = new SingleElementTag<>(BuiltInRegistries.ITEM.getKey(Items.AIR), Items.AIR);
   public static Tag<Fluid> AIR_FLUID_TAG = new SingleElementTag<>(BuiltInRegistries.FLUID.getKey(Fluids.EMPTY), Fluids.EMPTY);

   @Nullable
   public static Tag<Block> getBlock(String name, boolean nullIfNotExists) {
      if (name.startsWith("#")) {
         ResourceLocation id = ResourceLocation.tryParse(name.substring(1));
         if (id == null) {
            return nullIfNotExists ? null : AIR_BLOCK_TAG;
         } else {
            Optional<Named<Block>> tag = BuiltInRegistries.BLOCK.getTag(TagKey.create(Registries.BLOCK, id));
            if (tag.isEmpty()) {
               return nullIfNotExists ? null : AIR_BLOCK_TAG;
            } else {
               return new BlockTag(tag.get());
            }
         }
      } else {
         ResourceLocation resourceLocation = ResourceLocation.tryParse(name);
         if (resourceLocation == null) {
            return nullIfNotExists ? null : AIR_BLOCK_TAG;
         } else if (!BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
            return nullIfNotExists ? null : AIR_BLOCK_TAG;
         } else {
            return new SingleElementTag<>(resourceLocation, (Block)BuiltInRegistries.BLOCK.get(resourceLocation));
         }
      }
   }

   public static Tag<Block> getBlock(String name) {
      return getBlock(name, false);
   }

   public static Tag<Block> getBlockTag(ResourceLocation name, boolean nullIfNotExists) {
      Optional<Named<Block>> tag = BuiltInRegistries.BLOCK.getTag(BlockTags.create(name));
      if (tag.isEmpty()) {
         return nullIfNotExists ? null : AIR_BLOCK_TAG;
      } else {
         return new BlockTag(tag.get());
      }
   }

   public static Tag<Block> getBlockTag(ResourceLocation name) {
      return getBlockTag(name, false);
   }

   @Nullable
   public static Tag<Item> getItem(String name, boolean nullIfNotExists) {
      if (name.startsWith("#")) {
         ResourceLocation id = ResourceLocation.tryParse(name.substring(1));
         if (id == null) {
            return nullIfNotExists ? null : AIR_ITEM_TAG;
         } else {
            Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, id));
            if (tag.isEmpty()) {
               return nullIfNotExists ? null : AIR_ITEM_TAG;
            } else {
               return new ItemTag(tag.get());
            }
         }
      } else {
         ResourceLocation resourceLocation = ResourceLocation.tryParse(name);
         if (resourceLocation == null) {
            return nullIfNotExists ? null : AIR_ITEM_TAG;
         } else if (!BuiltInRegistries.ITEM.containsKey(resourceLocation)) {
            return nullIfNotExists ? null : AIR_ITEM_TAG;
         } else {
            return new SingleElementTag<>(resourceLocation, (Item)BuiltInRegistries.ITEM.get(resourceLocation));
         }
      }
   }

   public static Tag<Item> getItem(String name) {
      return getItem(name, false);
   }

   public static Tag<Item> getItemTag(ResourceLocation name, boolean nullIfNotExists) {
      Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(ItemTags.create(name));
      if (tag.isEmpty()) {
         return nullIfNotExists ? null : AIR_ITEM_TAG;
      } else {
         return new ItemTag(tag.get());
      }
   }

   public static Tag<Item> getItemTag(ResourceLocation name) {
      return getItemTag(name, false);
   }

   @Nullable
   public static Tag<Fluid> getFluid(String name, boolean nullIfNotExists) {
      if (name.startsWith("#")) {
         ResourceLocation id = ResourceLocation.tryParse(name.substring(1));
         if (id == null) {
            return nullIfNotExists ? null : AIR_FLUID_TAG;
         } else {
            Optional<Named<Fluid>> tag = BuiltInRegistries.FLUID.getTag(TagKey.create(Registries.FLUID, id));
            if (tag.isEmpty()) {
               return nullIfNotExists ? null : AIR_FLUID_TAG;
            } else {
               return new FluidTag(tag.get());
            }
         }
      } else {
         ResourceLocation resourceLocation = ResourceLocation.tryParse(name);
         if (resourceLocation == null) {
            return nullIfNotExists ? null : AIR_FLUID_TAG;
         } else if (!BuiltInRegistries.FLUID.containsKey(resourceLocation)) {
            return nullIfNotExists ? null : AIR_FLUID_TAG;
         } else {
            return new SingleElementTag<>(resourceLocation, (Fluid)BuiltInRegistries.FLUID.get(resourceLocation));
         }
      }
   }

   public static Tag<Fluid> getFluid(String name) {
      return getFluid(name, false);
   }

   public static Tag<Fluid> getFluidTag(ResourceLocation name, boolean nullIfNotExists) {
      Optional<Named<Fluid>> tag = BuiltInRegistries.FLUID.getTag(FluidTags.create(name));
      if (tag.isEmpty()) {
         return nullIfNotExists ? null : AIR_FLUID_TAG;
      } else {
         return new FluidTag(tag.get());
      }
   }

   public static Tag<Fluid> getFluidTag(ResourceLocation name) {
      return getFluidTag(name, false);
   }
}
