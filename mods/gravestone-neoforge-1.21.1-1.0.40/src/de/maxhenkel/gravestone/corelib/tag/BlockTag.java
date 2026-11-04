package de.maxhenkel.gravestone.corelib.tag;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockTag implements Tag<Block> {
   private final Named<Block> holderSet;

   public BlockTag(Named<Block> tagKey) {
      this.holderSet = tagKey;
   }

   @Override
   public ResourceLocation getName() {
      return this.holderSet.key().location();
   }

   public boolean contains(Block block) {
      return block.builtInRegistryHolder().is(this.holderSet.key());
   }

   @Override
   public List<Block> getAll() {
      return this.holderSet.stream().<Block>map(Holder::value).collect(Collectors.toList());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean contains(Object param1) {
      return this.contains((Block)var1);
   }
}
