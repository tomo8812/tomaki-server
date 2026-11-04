package net.satisfy.brewery.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.brewery.client.gui.overlay.BigBarrelInfoProvider;
import net.satisfy.brewery.client.gui.overlay.BrewingstationInfoProvider;
import net.satisfy.brewery.client.model.BeerElementalModel;
import net.satisfy.brewery.client.model.BrewfestBootsModel;
import net.satisfy.brewery.client.model.BrewfestChestplateModel;
import net.satisfy.brewery.client.model.BrewfestHatModel;
import net.satisfy.brewery.client.model.BrewfestLeggingsModel;
import net.satisfy.brewery.client.renderer.block.BeerMugRenderer;
import net.satisfy.brewery.client.renderer.block.BrewTimerRenderer;
import net.satisfy.brewery.client.renderer.block.BrewWhistleRenderer;
import net.satisfy.brewery.client.renderer.block.BrewingstationRenderer;
import net.satisfy.brewery.client.renderer.entity.BeerElementalAttackRenderer;
import net.satisfy.brewery.client.renderer.entity.BeerElementalRenderer;
import net.satisfy.brewery.core.block.PatternedWoolBlock;
import net.satisfy.brewery.core.registry.EntityTypeRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.banner.CompletionistBannerRenderer;
import net.satisfy.foundation.client.armor.ArmorModels;
import net.satisfy.foundation.client.render.WallDecorationRenderer;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.foundation.storage.BottleGroupRenderer;
import net.satisfy.foundation.storage.StorageBlockEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class BreweryClient {
   public static void onInitializeClient() {
      registerArmorModels();
      RenderTypeRegistry.register(
         RenderType.cutout(),
         new Block[]{
            (Block)ObjectRegistry.WILD_HOPS.get(),
            (Block)ObjectRegistry.BEER_MUG.get(),
            (Block)ObjectRegistry.BEER_WHEAT.get(),
            (Block)ObjectRegistry.BEER_HOPS.get(),
            (Block)ObjectRegistry.BEER_BARLEY.get(),
            (Block)ObjectRegistry.BEER_HALEY.get(),
            (Block)ObjectRegistry.BEER_OAT.get(),
            (Block)ObjectRegistry.BEER_NETTLE.get(),
            (Block)ObjectRegistry.HOPS_CROP_BODY.get(),
            (Block)ObjectRegistry.HOPS_CROP.get(),
            (Block)ObjectRegistry.WHISKEY_MAGGOALLAN.get(),
            (Block)ObjectRegistry.WHISKEY_CARRASCONLABEL.get(),
            (Block)ObjectRegistry.WHISKEY_LILITUSINGLEMALT.get(),
            (Block)ObjectRegistry.WHISKEY_JOJANNIK.get(),
            (Block)ObjectRegistry.WHISKEY_MAGGOALLAN.get(),
            (Block)ObjectRegistry.WHISKEY_CRISTELWALKER.get(),
            (Block)ObjectRegistry.WHISKEY_AK.get(),
            (Block)ObjectRegistry.WHISKEY_HIGHLAND_HEARTH.get(),
            (Block)ObjectRegistry.WHISKEY_JAMESONS_MALT.get(),
            (Block)ObjectRegistry.WHISKEY_SMOKEY_REVERIE.get(),
            (Block)ObjectRegistry.BREWERY_BANNER.get(),
            (Block)ObjectRegistry.BREWERY_WALL_BANNER.get(),
            (Block)ObjectRegistry.PATTERNED_WOOL.get(),
            (Block)ObjectRegistry.PATTERNED_CARPET_BLOCK.get(),
            (Block)ObjectRegistry.TABLE.get()
         }
      );
      ColorHandlerRegistry.registerBlockColors(
         (state, world, pos, tintIndex) -> tintIndex == 0 ? PatternedWoolBlock.getTint((DyeColor)state.getValue(PatternedWoolBlock.COLOR)) : -1,
         new Supplier[]{ObjectRegistry.PATTERNED_WOOL, ObjectRegistry.PATTERNED_CARPET_BLOCK, ObjectRegistry.TABLE}
      );
      ColorHandlerRegistry.registerItemColors(
         (stack, tintIndex) -> tintIndex != 0 ? -1 : PatternedWoolBlock.getTint(PatternedWoolBlock.getColor(stack)),
         new Supplier[]{ObjectRegistry.PATTERNED_WOOL, ObjectRegistry.PATTERNED_CARPET_BLOCK, ObjectRegistry.PATTERNED_CARPET}
      );
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BREWERY_BANNER.get(), CompletionistBannerRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BEER_MUG_BLOCK_ENTITY.get(), BeerMugRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BREW_WHISTLE_BLOCK_ENTITY.get(), BrewWhistleRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BREW_TIMER_BLOCK_ENTITY.get(), BrewTimerRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BREWINGSTATION_BLOCK_ENTITY.get(), BrewingstationRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.WALL_DECORATION.get(), WallDecorationRenderer::new);
      StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.BEVERAGE, new BottleGroupRenderer());
   }

   public static void preInitClient() {
      registerEntityRenderers();
      registerBlockInfo();
      registerEntityModelLayers();
   }

   private static void registerBlockInfo() {
      BlockInfoOverlay.init();
      BlockInfoOverlay.registerProvider(new BrewingstationInfoProvider());
      BlockInfoOverlay.registerProvider(new BigBarrelInfoProvider());
   }

   private static void registerEntityRenderers() {
      EntityRendererRegistry.register(EntityTypeRegistry.BEER_ELEMENTAL, BeerElementalRenderer::new);
      EntityRendererRegistry.register(EntityTypeRegistry.BEER_ELEMENTAL_ATTACK, BeerElementalAttackRenderer::new);
      EntityRendererRegistry.register(EntityTypeRegistry.DARK_BREW, ThrownItemRenderer::new);
   }

   public static void registerArmorModels() {
      ArmorModels.register(
         BrewfestHatModel.LAYER_LOCATION,
         BrewfestHatModel::new,
         new ItemLike[]{(ItemLike)ObjectRegistry.BREWFEST_HAT.get(), (ItemLike)ObjectRegistry.BREWFEST_HAT_RED.get()}
      );
      ArmorModels.register(
         BrewfestChestplateModel.LAYER_LOCATION,
         BrewfestChestplateModel::new,
         new ItemLike[]{(ItemLike)ObjectRegistry.BREWFEST_REGALIA.get(), (ItemLike)ObjectRegistry.BREWFEST_BLOUSE.get()}
      );
      ArmorModels.register(
         BrewfestLeggingsModel.LAYER_LOCATION,
         BrewfestLeggingsModel::new,
         new ItemLike[]{(ItemLike)ObjectRegistry.BREWFEST_TROUSERS.get(), (ItemLike)ObjectRegistry.BREWFEST_DRESS.get()}
      );
      ArmorModels.register(
         BrewfestBootsModel.LAYER_LOCATION,
         BrewfestBootsModel::new,
         new ItemLike[]{(ItemLike)ObjectRegistry.BREWFEST_BOOTS.get(), (ItemLike)ObjectRegistry.BREWFEST_SHOES.get()}
      );
   }

   public static void registerEntityModelLayers() {
      EntityModelLayerRegistry.register(BrewfestHatModel.LAYER_LOCATION, BrewfestHatModel::createBodyLayer);
      EntityModelLayerRegistry.register(BrewfestChestplateModel.LAYER_LOCATION, BrewfestChestplateModel::createBodyLayer);
      EntityModelLayerRegistry.register(BrewfestLeggingsModel.LAYER_LOCATION, BrewfestLeggingsModel::createBodyLayer);
      EntityModelLayerRegistry.register(BrewfestBootsModel.LAYER_LOCATION, BrewfestBootsModel::createBodyLayer);
      EntityModelLayerRegistry.register(BeerElementalModel.BEER_ELEMENTAL_MODEL_LAYER, BeerElementalModel::createBodyLayer);
   }
}
