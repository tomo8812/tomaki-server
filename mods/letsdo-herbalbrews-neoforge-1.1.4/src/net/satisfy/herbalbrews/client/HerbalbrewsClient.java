package net.satisfy.herbalbrews.client;

import dev.architectury.platform.Platform;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.herbalbrews.client.gui.CauldronGui;
import net.satisfy.herbalbrews.client.gui.TeaKettleGui;
import net.satisfy.herbalbrews.client.model.TopHatModel;
import net.satisfy.herbalbrews.client.model.WitchHatModel;
import net.satisfy.herbalbrews.client.renderer.CompletionistBannerRenderer;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import net.satisfy.herbalbrews.core.registry.MenuTypeRegistry;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;

@OnlyIn(Dist.CLIENT)
public class HerbalbrewsClient {
   public static void onInitializeClient() {
      RenderTypeRegistry.register(
         RenderType.cutout(),
         new Block[]{
            (Block)ObjectRegistry.LAVENDER.get(),
            (Block)ObjectRegistry.WILD_COFFEE_PLANT.get(),
            (Block)ObjectRegistry.WILD_ROOIBOS_PLANT.get(),
            (Block)ObjectRegistry.WILD_YERBA_MATE_PLANT.get(),
            (Block)ObjectRegistry.HIBISCUS.get(),
            (Block)ObjectRegistry.HIBISCUS_TEA_BLOCK.get(),
            (Block)ObjectRegistry.MILK_COFFEE_BLOCK.get(),
            (Block)ObjectRegistry.POTTED_HIBISCUS.get(),
            (Block)ObjectRegistry.POTTED_LAVENDER.get(),
            (Block)ObjectRegistry.POTTED_WILD_COFFEE.get(),
            (Block)ObjectRegistry.POTTED_WILD_YERBA_MATE.get(),
            (Block)ObjectRegistry.COFFEE_PLANT.get(),
            (Block)ObjectRegistry.TEA_PLANT.get(),
            (Block)ObjectRegistry.YERBA_MATE_PLANT.get(),
            (Block)ObjectRegistry.ROOIBOS_PLANT.get(),
            (Block)ObjectRegistry.POTTED_WILD_ROOIBOS.get(),
            (Block)ObjectRegistry.COPPER_TEA_KETTLE.get(),
            (Block)ObjectRegistry.TEA_KETTLE.get(),
            (Block)ObjectRegistry.CAULDRON.get(),
            (Block)ObjectRegistry.JUG.get(),
            (Block)ObjectRegistry.GREEN_TEA_BLOCK.get(),
            (Block)ObjectRegistry.BLACK_TEA_BLOCK.get(),
            (Block)ObjectRegistry.LAVENDER_TEA_BLOCK.get(),
            (Block)ObjectRegistry.COFFEE_BLOCK.get(),
            (Block)ObjectRegistry.ROOIBOS_TEA_BLOCK.get(),
            (Block)ObjectRegistry.YERBA_MATE_TEA_BLOCK.get()
         }
      );
      if (Platform.isFabric()) {
         MenuRegistry.registerScreenFactory((MenuType)MenuTypeRegistry.TEA_KETTLE_SCREEN_HANDLER.get(), TeaKettleGui::new);
         MenuRegistry.registerScreenFactory((MenuType)MenuTypeRegistry.CAULDRON_SCREEN_HANDLER.get(), CauldronGui::new);
      }

      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.HERBALBREWS_BANNER.get(), CompletionistBannerRenderer::new);
   }

   public static void preInitClient() {
      EntityModelLayerRegistry.register(TopHatModel.LAYER_LOCATION, TopHatModel::createBodyLayer);
      EntityModelLayerRegistry.register(WitchHatModel.LAYER_LOCATION, WitchHatModel::createBodyLayer);
      EntityModelLayerRegistry.register(CompletionistBannerRenderer.LAYER_LOCATION, CompletionistBannerRenderer::createBodyLayer);
   }
}
