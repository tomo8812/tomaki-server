package de.maxhenkel.gravestone.corelib.tag;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemTag implements Tag<Item> {
   private final Named<Item> holderSet;

   public ItemTag(Named<Item> tagKey) {
      this.holderSet = tagKey;
   }

   @Override
   public ResourceLocation getName() {
      return this.holderSet.key().location();
   }

   public boolean contains(Item block) {
      return block.builtInRegistryHolder().is(this.holderSet.key());
   }

   @Override
   public List<Item> getAll() {
      return this.holderSet.stream().<Item>map(Holder::value).collect(Collectors.toList());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean contains(Object param1) {
      return this.contains((Item)var1);
   }
}
