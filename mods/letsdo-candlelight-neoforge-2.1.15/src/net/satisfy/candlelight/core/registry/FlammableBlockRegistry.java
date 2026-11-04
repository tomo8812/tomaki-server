package net.satisfy.candlelight.core.registry;

import java.util.function.Supplier;
import net.satisfy.foundation.flammable.FoundationFlammables;

public class FlammableBlockRegistry {
   public static void init() {
      FoundationFlammables.plant(new Supplier[]{ObjectRegistry.ROSE});
      FoundationFlammables.wool(new Supplier[]{ObjectRegistry.NOTE_PAPER_BLOCK});
      FoundationFlammables.wood(
         new Supplier[]{
            ObjectRegistry.BAMBOO_STOVE,
            ObjectRegistry.BAMBOO_KITCHEN_SINK,
            ObjectRegistry.BAMBOO_COUNTER,
            ObjectRegistry.FLOORBOARD,
            ObjectRegistry.CABINET,
            ObjectRegistry.DRAWER,
            ObjectRegistry.CHAIR,
            ObjectRegistry.TABLE,
            ObjectRegistry.SIDEBOARD,
            ObjectRegistry.SIDE_TABLE,
            ObjectRegistry.LAMP,
            ObjectRegistry.SOFA,
            ObjectRegistry.OAK_CABINET,
            ObjectRegistry.OAK_DRAWER,
            ObjectRegistry.OAK_CHAIR,
            ObjectRegistry.OAK_TABLE,
            ObjectRegistry.OAK_SHELF,
            ObjectRegistry.OAK_BIG_TABLE,
            ObjectRegistry.SPRUCE_CABINET,
            ObjectRegistry.SPRUCE_DRAWER,
            ObjectRegistry.SPRUCE_CHAIR,
            ObjectRegistry.SPRUCE_TABLE,
            ObjectRegistry.SPRUCE_SHELF,
            ObjectRegistry.SPRUCE_BIG_TABLE,
            ObjectRegistry.BIRCH_CABINET,
            ObjectRegistry.BIRCH_DRAWER,
            ObjectRegistry.BIRCH_CHAIR,
            ObjectRegistry.BIRCH_TABLE,
            ObjectRegistry.BIRCH_SHELF,
            ObjectRegistry.BIRCH_BIG_TABLE,
            ObjectRegistry.JUNGLE_CABINET,
            ObjectRegistry.JUNGLE_DRAWER,
            ObjectRegistry.JUNGLE_CHAIR,
            ObjectRegistry.JUNGLE_TABLE,
            ObjectRegistry.JUNGLE_SHELF,
            ObjectRegistry.JUNGLE_BIG_TABLE,
            ObjectRegistry.ACACIA_CABINET,
            ObjectRegistry.ACACIA_DRAWER,
            ObjectRegistry.ACACIA_CHAIR,
            ObjectRegistry.ACACIA_TABLE,
            ObjectRegistry.ACACIA_SHELF,
            ObjectRegistry.ACACIA_BIG_TABLE,
            ObjectRegistry.DARK_OAK_CABINET,
            ObjectRegistry.DARK_OAK_DRAWER,
            ObjectRegistry.DARK_OAK_CHAIR,
            ObjectRegistry.DARK_OAK_TABLE,
            ObjectRegistry.DARK_OAK_SHELF,
            ObjectRegistry.DARK_OAK_BIG_TABLE,
            ObjectRegistry.MANGROVE_CABINET,
            ObjectRegistry.MANGROVE_DRAWER,
            ObjectRegistry.MANGROVE_CHAIR,
            ObjectRegistry.MANGROVE_TABLE,
            ObjectRegistry.MANGROVE_SHELF,
            ObjectRegistry.MANGROVE_BIG_TABLE,
            ObjectRegistry.CHERRY_CABINET,
            ObjectRegistry.CHERRY_DRAWER,
            ObjectRegistry.CHERRY_CHAIR,
            ObjectRegistry.CHERRY_TABLE,
            ObjectRegistry.CHERRY_SHELF,
            ObjectRegistry.CHERRY_BIG_TABLE,
            ObjectRegistry.BAMBOO_CABINET,
            ObjectRegistry.BAMBOO_DRAWER,
            ObjectRegistry.BAMBOO_CHAIR,
            ObjectRegistry.BAMBOO_TABLE,
            ObjectRegistry.BAMBOO_SHELF,
            ObjectRegistry.BAMBOO_BIG_TABLE
         }
      );
   }
}
