package net.satisfy.brewery.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class TabRegistry {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create("brewery", Registries.CREATIVE_MODE_TAB);
   public static final RegistrySupplier<CreativeModeTab> BREWERY_TAB = CREATIVE_MODE_TABS.register(
      "brewery",
      () -> CreativeModeTab.builder(Row.TOP, 1)
         .icon(() -> new ItemStack((ItemLike)ObjectRegistry.BEER_WHEAT.get()))
         .title(Component.translatable("itemGroup.brewery.creative_tab"))
         .displayItems((parameters, out) -> {
            out.accept((ItemLike)ObjectRegistry.CABINET.get());
            out.accept((ItemLike)ObjectRegistry.DRAWER.get());
            out.accept((ItemLike)ObjectRegistry.BAR_COUNTER.get());
            out.accept((ItemLike)ObjectRegistry.SIDEBOARD.get());
            out.accept((ItemLike)ObjectRegistry.WALL_CABINET.get());
            out.accept((ItemLike)ObjectRegistry.TABLE.get());
            out.accept((ItemLike)ObjectRegistry.BENCH.get());
            out.accept((ItemLike)ObjectRegistry.PATTERNED_WOOL.get());
            out.accept((ItemLike)ObjectRegistry.PATTERNED_CARPET.get());
            out.accept((ItemLike)ObjectRegistry.WILD_HOPS.get());
            out.accept((ItemLike)ObjectRegistry.HOPS.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_WHEAT.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_BARLEY.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_CORN.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_OAT.get());
            out.accept((ItemLike)ObjectRegistry.WOODEN_BREWINGSTATION.get());
            out.accept((ItemLike)ObjectRegistry.COPPER_BREWINGSTATION.get());
            out.accept((ItemLike)ObjectRegistry.NETHERITE_BREWINGSTATION.get());
            out.accept((ItemLike)ObjectRegistry.DARK_BREW.get());
            out.accept((ItemLike)ObjectRegistry.BARREL_MAIN.get());
            out.accept(((Block)ObjectRegistry.BEER_MUG.get()).asItem());
            out.accept((ItemLike)ObjectRegistry.BEER_WHEAT.get());
            out.accept((ItemLike)ObjectRegistry.BEER_BARLEY.get());
            out.accept((ItemLike)ObjectRegistry.BEER_HOPS.get());
            out.accept((ItemLike)ObjectRegistry.BEER_NETTLE.get());
            out.accept((ItemLike)ObjectRegistry.BEER_OAT.get());
            out.accept((ItemLike)ObjectRegistry.BEER_HALEY.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_JOJANNIK.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_LILITUSINGLEMALT.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_CRISTELWALKER.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_MAGGOALLAN.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_CARRASCONLABEL.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_AK.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_HIGHLAND_HEARTH.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_SMOKEY_REVERIE.get());
            out.accept((ItemLike)ObjectRegistry.WHISKEY_JAMESONS_MALT.get());
            out.accept((ItemLike)ObjectRegistry.PORK_KNUCKLE.get());
            out.accept((ItemLike)ObjectRegistry.FRIED_CHICKEN.get());
            out.accept((ItemLike)ObjectRegistry.DUMPLINGS.get());
            out.accept((ItemLike)ObjectRegistry.HALF_CHICKEN.get());
            out.accept((ItemLike)ObjectRegistry.SAUSAGE.get());
            out.accept((ItemLike)ObjectRegistry.MASHED_POTATOES.get());
            out.accept((ItemLike)ObjectRegistry.POTATO_SALAD.get());
            out.accept((ItemLike)ObjectRegistry.PRETZEL.get());
            out.accept((ItemLike)ObjectRegistry.GINGERBREAD.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_HAT.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_HAT_RED.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_DRESS.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_REGALIA.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_TROUSERS.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_BLOUSE.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_BOOTS.get());
            out.accept((ItemLike)ObjectRegistry.BREWFEST_SHOES.get());
            out.accept((ItemLike)ObjectRegistry.BREATHALYZER.get());
            out.accept((ItemLike)ObjectRegistry.BREWERY_BANNER.get());
            out.accept((ItemLike)ObjectRegistry.BEER_ELEMENTAL_SPAWN_EGG.get());
         })
         .build()
   );

   public static void init() {
      CREATIVE_MODE_TABS.register();
   }
}
