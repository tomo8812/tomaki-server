package de.maxhenkel.gravestone.corelib.tag;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public class FluidTag implements Tag<Fluid> {
   private final Named<Fluid> holderSet;

   public FluidTag(Named<Fluid> tagKey) {
      this.holderSet = tagKey;
   }

   @Override
   public ResourceLocation getName() {
      return this.holderSet.key().location();
   }

   public boolean contains(Fluid block) {
      return block.builtInRegistryHolder().is(this.holderSet.key());
   }

   @Override
   public List<Fluid> getAll() {
      return this.holderSet.stream().<Fluid>map(Holder::value).collect(Collectors.toList());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean contains(Object param1) {
      return this.contains((Fluid)var1);
   }
}
