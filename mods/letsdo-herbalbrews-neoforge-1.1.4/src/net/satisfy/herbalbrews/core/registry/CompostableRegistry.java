package net.satisfy.herbalbrews.core.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;

public class CompostableRegistry {
   public static void registerCompostable() {
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.TEA_LEAF_CRATE.get()).asItem(), 2.5F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.GREEN_TEA_LEAF_BLOCK.get()).asItem(), 0.8F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.BLACK_TEA_LEAF_BLOCK.get()).asItem(), 0.8F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.MIXED_TEA_LEAF_BLOCK.get()).asItem(), 0.8F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.OOLONG_TEA_LEAF_BLOCK.get()).asItem(), 0.8F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.DRIED_OUT_GREEN_TEA_LEAF_BLOCK.get()).asItem(), 0.8F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ObjectRegistry.DRIED_GREEN_TEA.get(), 0.2F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ObjectRegistry.GREEN_TEA_LEAF.get(), 0.2F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ObjectRegistry.YERBA_MATE_LEAF.get(), 0.3F);
      ComposterBlock.COMPOSTABLES.put(((Item)ObjectRegistry.COFFEE_BEANS.get()).asItem(), 0.3F);
      ComposterBlock.COMPOSTABLES.put(((Item)ObjectRegistry.TEA_BLOSSOM.get()).asItem(), 0.3F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ObjectRegistry.ROOIBOS_LEAF.get(), 0.3F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.WILD_COFFEE_PLANT.get()).asItem(), 0.3F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.WILD_ROOIBOS_PLANT.get()).asItem(), 0.3F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.WILD_YERBA_MATE_PLANT.get()).asItem(), 0.3F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.HIBISCUS.get()).asItem(), 0.3F);
      ComposterBlock.COMPOSTABLES.put(((Block)ObjectRegistry.LAVENDER.get()).asItem(), 0.3F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ObjectRegistry.LAVENDER_BLOSSOM.get(), 0.3F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ObjectRegistry.DRIED_OOLONG_TEA.get(), 0.5F);
      ComposterBlock.COMPOSTABLES.put((ItemLike)ObjectRegistry.DRIED_BLACK_TEA.get(), 0.5F);
   }
}
