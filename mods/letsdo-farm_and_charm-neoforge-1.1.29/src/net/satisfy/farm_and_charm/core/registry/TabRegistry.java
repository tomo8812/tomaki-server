package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.level.ItemLike;

public class TabRegistry {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create("farm_and_charm", Registries.CREATIVE_MODE_TAB);
   public static final RegistrySupplier<CreativeModeTab> FARM_AND_CHARM_TAB = CREATIVE_MODE_TABS.register(
      "farm_and_charm",
      () -> CreativeModeTab.builder(Row.TOP, 0)
         .icon(() -> new ItemStack((ItemLike)ObjectRegistry.SUPPLY_CART.get()))
         .title(Component.translatable("creativetab.farm_and_charm.tab"))
         .displayItems((parameters, output) -> {
            output.accept((ItemLike)ObjectRegistry.KERNELS.get());
            output.accept((ItemLike)ObjectRegistry.CORN.get());
            output.accept((ItemLike)ObjectRegistry.OAT_SEEDS.get());
            output.accept((ItemLike)ObjectRegistry.OAT.get());
            output.accept((ItemLike)ObjectRegistry.BARLEY_SEEDS.get());
            output.accept((ItemLike)ObjectRegistry.BARLEY.get());
            output.accept((ItemLike)ObjectRegistry.LETTUCE_CROP.get());
            output.accept((ItemLike)ObjectRegistry.LETTUCE.get());
            output.accept((ItemLike)ObjectRegistry.ONION.get());
            output.accept((ItemLike)ObjectRegistry.TOMATO_SEEDS.get());
            output.accept((ItemLike)ObjectRegistry.TOMATO.get());
            output.accept((ItemLike)ObjectRegistry.ROTTEN_TOMATO.get());
            output.accept((ItemLike)ObjectRegistry.STRAWBERRY_SEEDS.get());
            output.accept((ItemLike)ObjectRegistry.STRAWBERRY.get());
            output.accept((ItemLike)ObjectRegistry.WILD_NETTLE.get());
            output.accept((ItemLike)ObjectRegistry.WILD_RIBWORT.get());
            output.accept((ItemLike)ObjectRegistry.WILD_EMMER.get());
            output.accept((ItemLike)ObjectRegistry.WILD_BEETROOTS.get());
            output.accept((ItemLike)ObjectRegistry.WILD_POTATOES.get());
            output.accept((ItemLike)ObjectRegistry.WILD_CARROTS.get());
            output.accept((ItemLike)ObjectRegistry.WILD_BARLEY.get());
            output.accept((ItemLike)ObjectRegistry.WILD_CORN.get());
            output.accept((ItemLike)ObjectRegistry.WILD_OAT.get());
            output.accept((ItemLike)ObjectRegistry.WILD_LETTUCE.get());
            output.accept((ItemLike)ObjectRegistry.WILD_ONIONS.get());
            output.accept((ItemLike)ObjectRegistry.WILD_TOMATOES.get());
            output.accept((ItemLike)ObjectRegistry.WILD_STRAWBERRIES.get());
            output.accept((ItemLike)ObjectRegistry.LETTUCE_BAG.get());
            output.accept((ItemLike)ObjectRegistry.TOMATO_BAG.get());
            output.accept((ItemLike)ObjectRegistry.CARROT_BAG.get());
            output.accept((ItemLike)ObjectRegistry.POTATO_BAG.get());
            output.accept((ItemLike)ObjectRegistry.ONION_BAG.get());
            output.accept((ItemLike)ObjectRegistry.BEETROOT_BAG.get());
            output.accept((ItemLike)ObjectRegistry.CORN_BAG.get());
            output.accept((ItemLike)ObjectRegistry.STRAWBERRY_BAG.get());
            output.accept((ItemLike)ObjectRegistry.FLOUR_BAG.get());
            output.accept((ItemLike)ObjectRegistry.OAT_BALL.get());
            output.accept((ItemLike)ObjectRegistry.BARLEY_BALL.get());
            output.accept((ItemLike)ObjectRegistry.FERTILIZED_SOIL_BLOCK.get());
            output.accept((ItemLike)ObjectRegistry.FERTILIZED_FARM_BLOCK.get());
            output.accept((ItemLike)ObjectRegistry.PACKED_DIRT.get());
            output.accept((ItemLike)ObjectRegistry.TRAMPLED_PACKED_DIRT.get());
            output.accept((ItemLike)ObjectRegistry.STABLE_FLOOR.get());
            output.accept((ItemLike)ObjectRegistry.STRAW_STABLE_FLOOR.get());
            output.accept((ItemLike)ObjectRegistry.TRAMPLED_STABLE_FLOOR.get());
            output.accept((ItemLike)ObjectRegistry.STOVE.get());
            output.accept((ItemLike)ObjectRegistry.WATER_TROUGH.get());
            output.accept((ItemLike)ObjectRegistry.TIMBER_WELL.get());
            output.accept((ItemLike)ObjectRegistry.FEEDING_TROUGH.get());
            output.accept((ItemLike)ObjectRegistry.WATER_SPRINKLER.get());
            output.accept((ItemLike)ObjectRegistry.CATTLEGRID.get());
            output.accept((ItemLike)ObjectRegistry.IRON_DIVIDER.get());
            output.accept((ItemLike)ObjectRegistry.CHICKEN_FENCE.get());
            output.accept((ItemLike)ObjectRegistry.CHICKEN_COOP_ITEM.get());
            output.accept((ItemLike)ObjectRegistry.SILO_WOOD.get());
            output.accept((ItemLike)ObjectRegistry.SILO_COPPER.get());
            output.accept((ItemLike)ObjectRegistry.PITCHFORK.get());
            output.accept((ItemLike)ObjectRegistry.IRON_CLEAVER.get());
            output.accept((ItemLike)ObjectRegistry.DIAMOND_CLEAVER.get());
            output.accept((ItemLike)ObjectRegistry.NETHERITE_CLEAVER.get());
            output.accept((ItemLike)ObjectRegistry.SUPPLY_CART.get());
            output.accept((ItemLike)ObjectRegistry.PLOW.get());
            output.accept((ItemLike)ObjectRegistry.SEEDER.get());
            output.accept((ItemLike)ObjectRegistry.WINDOW_SILL.get());
            output.accept((ItemLike)ObjectRegistry.TOOL_RACK.get());
            output.accept((ItemLike)ObjectRegistry.CRAFTING_BOWL.get());
            output.accept((ItemLike)ObjectRegistry.CUTTING_BOARD.get());
            output.accept((ItemLike)ObjectRegistry.MINCER.get());
            output.accept((ItemLike)ObjectRegistry.COOKING_POT.get());
            output.accept((ItemLike)ObjectRegistry.ROASTER.get());
            output.accept((ItemLike)ObjectRegistry.BUTTER.get());
            output.accept((ItemLike)ObjectRegistry.YEAST.get());
            output.accept((ItemLike)ObjectRegistry.FLOUR.get());
            output.accept((ItemLike)ObjectRegistry.DOUGH.get());
            output.accept((ItemLike)ObjectRegistry.RAW_PASTA.get());
            output.accept((ItemLike)ObjectRegistry.MINCED_BEEF.get());
            output.accept((ItemLike)ObjectRegistry.LAMB_HAM.get());
            output.accept((ItemLike)ObjectRegistry.CHICKEN_PARTS.get());
            output.accept((ItemLike)ObjectRegistry.BACON.get());
            output.accept((ItemLike)ObjectRegistry.FARMER_SALAD.get());
            output.accept((ItemLike)ObjectRegistry.OATMEAL_WITH_STRAWBERRIES.get());
            output.accept((ItemLike)ObjectRegistry.SIMPLE_TOMATO_SOUP.get());
            output.accept((ItemLike)ObjectRegistry.BARLEY_SOUP.get());
            output.accept((ItemLike)ObjectRegistry.ONION_SOUP.get());
            output.accept((ItemLike)ObjectRegistry.POTATO_SOUP.get());
            output.accept((ItemLike)ObjectRegistry.GOULASH.get());
            output.accept((ItemLike)ObjectRegistry.PASTA_WITH_ONION_SAUCE.get());
            output.accept((ItemLike)ObjectRegistry.CORN_GRITS.get());
            output.accept((ItemLike)ObjectRegistry.BARLEY_PATTIES_WITH_POTATOES.get());
            output.accept((ItemLike)ObjectRegistry.BEEF_PATTY_WITH_VEGETABLES.get());
            output.accept((ItemLike)ObjectRegistry.SAUSAGE_WITH_OAT_PATTY.get());
            output.accept((ItemLike)ObjectRegistry.ROASTED_CHICKEN.get());
            output.accept((ItemLike)ObjectRegistry.ROASTED_CORN.get());
            output.accept((ItemLike)ObjectRegistry.COOKED_SALMON.get());
            output.accept((ItemLike)ObjectRegistry.COOKED_COD.get());
            output.accept((ItemLike)ObjectRegistry.CHICKEN_WRAPPED_IN_BACON.get());
            output.accept((ItemLike)ObjectRegistry.LAMB_WITH_CORN.get());
            output.accept((ItemLike)ObjectRegistry.BACON_WITH_EGGS.get());
            output.accept((ItemLike)ObjectRegistry.OAT_PANCAKE.get());
            output.accept((ItemLike)ObjectRegistry.FARMERS_BREAKFAST.get());
            output.accept((ItemLike)ObjectRegistry.BAKED_LAMB_HAM.get());
            output.accept((ItemLike)ObjectRegistry.POTATO_WITH_ROAST_MEAT.get());
            output.accept((ItemLike)ObjectRegistry.STUFFED_CHICKEN.get());
            output.accept((ItemLike)ObjectRegistry.STUFFED_RABBIT.get());
            output.accept((ItemLike)ObjectRegistry.FARMERS_BREAD.get());
            output.accept((ItemLike)ObjectRegistry.GRANDMOTHERS_STRAWBERRY_CAKE.get());
            output.accept((ItemLike)ObjectRegistry.STRAWBERRY_TEA.get());
            output.accept((ItemLike)ObjectRegistry.NETTLE_TEA.get());
            output.accept((ItemLike)ObjectRegistry.RIBWORT_TEA.get());
            output.accept((ItemLike)ObjectRegistry.STRAWBERRY_TEA_CUP.get());
            output.accept((ItemLike)ObjectRegistry.NETTLE_TEA_CUP.get());
            output.accept((ItemLike)ObjectRegistry.RIBWORT_TEA_CUP.get());
            output.accept((ItemLike)ObjectRegistry.CAT_FOOD.get());
            output.accept((ItemLike)ObjectRegistry.DOG_FOOD.get());
            output.accept((ItemLike)ObjectRegistry.CHICKEN_FEED.get());
            output.accept((ItemLike)ObjectRegistry.HORSE_FODDER.get());
            output.accept((ItemLike)ObjectRegistry.FERTILIZER.get());
            output.accept((ItemLike)ObjectRegistry.COMPOST.get());
            output.accept((ItemLike)ObjectRegistry.ROPE.get());
            output.accept((ItemLike)ObjectRegistry.PET_BOWL.get());
            output.accept((ItemLike)ObjectRegistry.DOG_FOOD_BAG.get());
            output.accept((ItemLike)ObjectRegistry.CAT_FOOD_BAG.get());
            output.accept((ItemLike)ObjectRegistry.CHICKEN_NEST.get());
            output.accept((ItemLike)ObjectRegistry.STURDY_LADDER.get());
            output.accept((ItemLike)ObjectRegistry.DUNGAREES.get());
            output.accept((ItemLike)ObjectRegistry.SCARECROW.get());
         })
         .build()
   );

   public static void init() {
      CREATIVE_MODE_TABS.register();
   }
}
