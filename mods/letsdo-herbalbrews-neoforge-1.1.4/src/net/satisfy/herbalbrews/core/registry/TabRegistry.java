package net.satisfy.herbalbrews.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.level.ItemLike;

public class TabRegistry {
   public static final DeferredRegister<CreativeModeTab> HERBALBREWS_TABS = DeferredRegister.create("herbalbrews", Registries.CREATIVE_MODE_TAB);
   public static final RegistrySupplier<CreativeModeTab> HERBALBREWS_TAB = HERBALBREWS_TABS.register(
      "herbalbrews",
      () -> CreativeModeTab.builder(Row.TOP, 1)
         .icon(() -> new ItemStack((ItemLike)ObjectRegistry.TEA_KETTLE.get()))
         .title(Component.translatable("creative_tab.herbalbrews"))
         .displayItems((parameters, out) -> {
            out.accept((ItemLike)ObjectRegistry.STOVE.get());
            out.accept((ItemLike)ObjectRegistry.TEA_LEAF_CRATE.get());
            out.accept((ItemLike)ObjectRegistry.GREEN_TEA_LEAF_BLOCK.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_GREEN_TEA_LEAF_BLOCK.get());
            out.accept((ItemLike)ObjectRegistry.BLACK_TEA_LEAF_BLOCK.get());
            out.accept((ItemLike)ObjectRegistry.MIXED_TEA_LEAF_BLOCK.get());
            out.accept((ItemLike)ObjectRegistry.OOLONG_TEA_LEAF_BLOCK.get());
            out.accept((ItemLike)ObjectRegistry.WITCH_HAT.get());
            out.accept((ItemLike)ObjectRegistry.TOP_HAT.get());
            out.accept((ItemLike)ObjectRegistry.WILD_COFFEE_PLANT.get());
            out.accept((ItemLike)ObjectRegistry.WILD_YERBA_MATE_PLANT.get());
            out.accept((ItemLike)ObjectRegistry.WILD_ROOIBOS_PLANT.get());
            out.accept((ItemLike)ObjectRegistry.HIBISCUS.get());
            out.accept((ItemLike)ObjectRegistry.LAVENDER.get());
            out.accept((ItemLike)ObjectRegistry.TEA_BLOSSOM.get());
            out.accept((ItemLike)ObjectRegistry.GREEN_TEA_LEAF.get());
            out.accept((ItemLike)ObjectRegistry.YERBA_MATE_LEAF.get());
            out.accept((ItemLike)ObjectRegistry.ROOIBOS_LEAF.get());
            out.accept((ItemLike)ObjectRegistry.COFFEE_BEANS.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_GREEN_TEA.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_BLACK_TEA.get());
            out.accept((ItemLike)ObjectRegistry.DRIED_OOLONG_TEA.get());
            out.accept((ItemLike)ObjectRegistry.LAVENDER_BLOSSOM.get());
            out.accept((ItemLike)ObjectRegistry.TEA_KETTLE.get());
            out.accept((ItemLike)ObjectRegistry.COPPER_TEA_KETTLE.get());
            out.accept((ItemLike)ObjectRegistry.CAULDRON.get());
            out.accept((ItemLike)ObjectRegistry.JUG.get());
            out.accept((ItemLike)ObjectRegistry.GREEN_TEA.get());
            out.accept((ItemLike)ObjectRegistry.BLACK_TEA.get());
            out.accept((ItemLike)ObjectRegistry.HIBISCUS_TEA.get());
            out.accept((ItemLike)ObjectRegistry.LAVENDER_TEA.get());
            out.accept((ItemLike)ObjectRegistry.COFFEE.get());
            out.accept((ItemLike)ObjectRegistry.MILK_COFFEE.get());
            out.accept((ItemLike)ObjectRegistry.ROOIBOS_TEA.get());
            out.accept((ItemLike)ObjectRegistry.OOLONG_TEA.get());
            out.accept((ItemLike)ObjectRegistry.YERBA_MATE_TEA.get());
            out.accept((ItemLike)ObjectRegistry.FLASK.get());
            out.accept((ItemLike)ObjectRegistry.HERBAL_INFUSION.get());
            out.accept((ItemLike)ObjectRegistry.HERBALBREWS_BANNER.get());
         })
         .build()
   );

   public static void init() {
      HERBALBREWS_TABS.register();
   }
}
