package net.satisfy.bakery.core.registry;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.bakery.Bakery;

public class StorageTypeRegistry {
   public static final ResourceLocation CAKE_STAND = Bakery.identifier("cake_stand");
   public static final ResourceLocation TRAY = Bakery.identifier("tray");
   public static final ResourceLocation BREADBOX = Bakery.identifier("breadbox");
   public static final ResourceLocation CAKE_DISPLAY = Bakery.identifier("cake_display");
   public static final ResourceLocation CUPCAKE_DISPLAY = Bakery.identifier("cupcake_display");
   public static final ResourceLocation WALL_DISPLAY = Bakery.identifier("wall_display");

   public static Set<Block> registerBlocks(Set<Block> blocks) {
      blocks.add((Block)ObjectRegistry.CAKE_STAND.get());
      blocks.add((Block)ObjectRegistry.TRAY.get());
      blocks.add((Block)ObjectRegistry.BREADBOX.get());
      blocks.add((Block)ObjectRegistry.CAKE_DISPLAY.get());
      blocks.add((Block)ObjectRegistry.CUPCAKE_DISPLAY.get());
      blocks.add((Block)ObjectRegistry.WALL_DISPLAY.get());
      return blocks;
   }
}
