package net.satisfy.candlelight.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.level.ItemLike;

public class TabRegistry {
   public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create("candlelight", Registries.CREATIVE_MODE_TAB);
   public static final RegistrySupplier<CreativeModeTab> CANDLELIGHT_TAB = TABS.register(
      "candlelight",
      () -> CreativeModeTab.builder(Row.BOTTOM, 0)
         .icon(() -> new ItemStack((ItemLike)ObjectRegistry.COOKING_POT.get()))
         .title(Component.translatable("creativetab.candlelight.tab_main"))
         .displayItems((parameters, output) -> {
            acceptMain(output);
            acceptKitchen(output);
            acceptFurniture(output);
         })
         .build()
   );

   public static void acceptMain(Output output) {
      output.accept((ItemLike)ObjectRegistry.COOKING_POT.get());
      output.accept((ItemLike)ObjectRegistry.COOKING_PAN.get());
      output.accept((ItemLike)ObjectRegistry.BOWL.get());
      output.accept((ItemLike)ObjectRegistry.PLATE.get());
      output.accept((ItemLike)ObjectRegistry.GLASS.get());
      output.accept((ItemLike)ObjectRegistry.WINE_GLASS.get());
      output.accept((ItemLike)ObjectRegistry.NAPKIN.get());
      output.accept((ItemLike)ObjectRegistry.CLOCHE.get());
      output.accept((ItemLike)ObjectRegistry.DINNER_BELL.get());
      output.accept((ItemLike)ObjectRegistry.TOMATO_SOUP.get());
      output.accept((ItemLike)ObjectRegistry.MUSHROOM_SOUP.get());
      output.accept((ItemLike)ObjectRegistry.BEETROOT_SALAD.get());
      output.accept((ItemLike)ObjectRegistry.PASTA_WITH_MOZZARELLA.get());
      output.accept((ItemLike)ObjectRegistry.BOLOGNESE.get());
      output.accept((ItemLike)ObjectRegistry.CHICKEN_TERIYAKI.get());
      output.accept((ItemLike)ObjectRegistry.SALAD.get());
      output.accept((ItemLike)ObjectRegistry.CHOCOLATE_MOUSSE.get());
      output.accept((ItemLike)ObjectRegistry.MOZZARELLA.get());
      output.accept((ItemLike)ObjectRegistry.BEEF_TARTARE.get());
      output.accept((ItemLike)ObjectRegistry.PASTA_WITH_BOLOGNESE.get());
      output.accept((ItemLike)ObjectRegistry.PASTA_WITH_LETTUCE.get());
      output.accept((ItemLike)ObjectRegistry.OMELET.get());
      output.accept((ItemLike)ObjectRegistry.FRESH_GARDEN_SALAD.get());
      output.accept((ItemLike)ObjectRegistry.HARVEST_PLATE.get());
      output.accept((ItemLike)ObjectRegistry.BEEF_WITH_MUSHROOM_IN_WINE_AND_POTATOES.get());
      output.accept((ItemLike)ObjectRegistry.TOMATO_MOZZARELLA_SALAD.get());
      output.accept((ItemLike)ObjectRegistry.PORK_RIBS.get());
      output.accept((ItemLike)ObjectRegistry.POTROAST.get());
      output.accept((ItemLike)ObjectRegistry.ROASTBEEF_WITH_GLAZED_CARROTS.get());
      output.accept((ItemLike)ObjectRegistry.ROASTED_LAMB_WITH_LETTUCE.get());
      output.accept((ItemLike)ObjectRegistry.TROPICAL_FISH_SUPREME.get());
      output.accept((ItemLike)ObjectRegistry.FILLET_STEAK.get());
      output.accept((ItemLike)ObjectRegistry.SALMON_ON_WHITE_WINE.get());
      output.accept((ItemLike)ObjectRegistry.CHICKEN_ALFREDO.get());
      output.accept((ItemLike)ObjectRegistry.CHICKEN_WITH_VEGETABLES.get());
      output.accept((ItemLike)ObjectRegistry.LASAGNE.get());
      output.accept((ItemLike)ObjectRegistry.BEEF_WELLINGTON.get());
      output.accept((ItemLike)ObjectRegistry.COOKING_HAT.get());
      output.accept((ItemLike)ObjectRegistry.CHEFS_JACKET.get());
      output.accept((ItemLike)ObjectRegistry.CHEFS_PANTS.get());
      output.accept((ItemLike)ObjectRegistry.CHEFS_BOOTS.get());
      output.accept((ItemLike)ObjectRegistry.FLOWER_CROWN.get());
      output.accept((ItemLike)ObjectRegistry.DRESS.get());
      output.accept((ItemLike)ObjectRegistry.NECKTIE.get());
      output.accept((ItemLike)ObjectRegistry.SHIRT.get());
      output.accept((ItemLike)ObjectRegistry.TROUSERS_AND_VEST.get());
      output.accept((ItemLike)ObjectRegistry.GOLD_RING.get());
      output.accept((ItemLike)ObjectRegistry.JEWELRY_BOX.get());
      output.accept((ItemLike)ObjectRegistry.PAINTING.get());
      output.accept((ItemLike)ObjectRegistry.HEART.get());
      output.accept((ItemLike)ObjectRegistry.ROSE.get());
      output.accept((ItemLike)ObjectRegistry.CHOCOLATE_BOX.get());
      output.accept((ItemLike)ObjectRegistry.TYPEWRITER_IRON.get());
      output.accept((ItemLike)ObjectRegistry.TYPEWRITER_GOLD.get());
      output.accept((ItemLike)ObjectRegistry.TABLE_SIGN_ITEM.get());
      output.accept((ItemLike)ObjectRegistry.NOTE_PAPER.get());
      output.accept((ItemLike)ObjectRegistry.NOTE_PAPER_WRITEABLE.get());
      output.accept((ItemLike)ObjectRegistry.LETTER_OPEN.get());
      output.accept((ItemLike)ObjectRegistry.LOVE_LETTER_OPEN.get());
      output.accept((ItemLike)ObjectRegistry.CANDLELIGHT_BANNER.get());
   }

   public static void acceptKitchen(Output output) {
      output.accept((ItemLike)ObjectRegistry.COBBLESTONE_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.COBBLESTONE_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.COBBLESTONE_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.SANDSTONE_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.SANDSTONE_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.SANDSTONE_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.STONE_BRICKS_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.STONE_BRICKS_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.STONE_BRICKS_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.DEEPSLATE_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.DEEPSLATE_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.DEEPSLATE_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.GRANITE_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.GRANITE_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.GRANITE_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.END_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.END_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.END_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.MUD_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.MUD_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.MUD_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.QUARTZ_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.QUARTZ_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.QUARTZ_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.RED_NETHER_BRICKS_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.RED_NETHER_BRICKS_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.RED_NETHER_BRICKS_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.BASALT_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.BASALT_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.BASALT_COUNTER.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_STOVE.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_KITCHEN_SINK.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_COUNTER.get());
   }

   public static void acceptFurniture(Output output) {
      output.accept((ItemLike)ObjectRegistry.FLOORBOARD.get());
      output.accept((ItemLike)ObjectRegistry.DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.CABINET.get());
      output.accept((ItemLike)ObjectRegistry.SIDEBOARD.get());
      output.accept((ItemLike)ObjectRegistry.CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.SOFA.get());
      output.accept((ItemLike)ObjectRegistry.TABLE.get());
      output.accept((ItemLike)ObjectRegistry.LAMP.get());
      output.accept((ItemLike)ObjectRegistry.SIDE_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.OAK_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.OAK_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.OAK_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.OAK_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.OAK_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.OAK_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.BIRCH_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.BIRCH_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.BIRCH_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.BIRCH_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.BIRCH_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.BIRCH_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.SPRUCE_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.SPRUCE_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.SPRUCE_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.SPRUCE_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.SPRUCE_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.SPRUCE_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.DARK_OAK_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.DARK_OAK_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.DARK_OAK_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.DARK_OAK_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.DARK_OAK_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.DARK_OAK_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.ACACIA_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.ACACIA_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.ACACIA_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.ACACIA_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.ACACIA_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.ACACIA_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.JUNGLE_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.JUNGLE_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.JUNGLE_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.JUNGLE_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.JUNGLE_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.JUNGLE_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.MANGROVE_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.MANGROVE_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.MANGROVE_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.MANGROVE_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.MANGROVE_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.MANGROVE_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.WARPED_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.WARPED_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.WARPED_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.WARPED_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.WARPED_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.WARPED_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.CRIMSON_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.CRIMSON_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.CRIMSON_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.CRIMSON_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.CRIMSON_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.CRIMSON_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.CHERRY_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.CHERRY_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.CHERRY_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.CHERRY_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.CHERRY_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.CHERRY_BIG_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_CABINET.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_DRAWER.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_TABLE.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_CHAIR.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_SHELF.get());
      output.accept((ItemLike)ObjectRegistry.BAMBOO_BIG_TABLE.get());
   }

   public static void init() {
      TABS.register();
   }
}
