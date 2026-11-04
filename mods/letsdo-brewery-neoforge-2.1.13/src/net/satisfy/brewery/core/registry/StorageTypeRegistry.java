package net.satisfy.brewery.core.registry;

import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.brewery.Brewery;

public class StorageTypeRegistry {
   public static final ResourceLocation BEVERAGE = Brewery.identifier("beverage");

   public static Set<Block> registerBlocks(Set<Block> blocks) {
      blocks.add((Block)ObjectRegistry.BEER_MUG.get());
      blocks.addAll(
         List.of(
            (Block)ObjectRegistry.BEER_BARLEY.get(),
            (Block)ObjectRegistry.BEER_HALEY.get(),
            (Block)ObjectRegistry.BEER_HOPS.get(),
            (Block)ObjectRegistry.BEER_NETTLE.get(),
            (Block)ObjectRegistry.WHISKEY_CARRASCONLABEL.get(),
            (Block)ObjectRegistry.WHISKEY_CRISTELWALKER.get(),
            (Block)ObjectRegistry.WHISKEY_JOJANNIK.get(),
            (Block)ObjectRegistry.WHISKEY_MAGGOALLAN.get(),
            (Block)ObjectRegistry.BEER_WHEAT.get(),
            (Block)ObjectRegistry.WHISKEY_LILITUSINGLEMALT.get(),
            (Block)ObjectRegistry.BEER_OAT.get(),
            (Block)ObjectRegistry.WHISKEY_AK.get(),
            (Block)ObjectRegistry.WHISKEY_HIGHLAND_HEARTH.get(),
            (Block)ObjectRegistry.WHISKEY_JAMESONS_MALT.get(),
            (Block)ObjectRegistry.WHISKEY_SMOKEY_REVERIE.get()
         )
      );
      return blocks;
   }
}
