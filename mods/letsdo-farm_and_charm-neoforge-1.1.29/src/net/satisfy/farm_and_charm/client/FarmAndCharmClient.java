package net.satisfy.farm_and_charm.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.farm_and_charm.client.event.ClientEventHandler;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.client.gui.PetBowlEditGui;
import net.satisfy.farm_and_charm.client.gui.RoasterGui;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.client.gui.overlay.CraftingBowlInfoProvider;
import net.satisfy.farm_and_charm.client.gui.overlay.CuttingBoardInfoProvider;
import net.satisfy.farm_and_charm.client.gui.overlay.MincerInfoProvider;
import net.satisfy.farm_and_charm.client.gui.overlay.ScarecrowInfoProvider;
import net.satisfy.farm_and_charm.client.gui.overlay.SiloInfoProvider;
import net.satisfy.farm_and_charm.client.gui.overlay.StoveInfoProvider;
import net.satisfy.farm_and_charm.client.gui.overlay.WaterSprinklerInfoProvider;
import net.satisfy.farm_and_charm.client.model.CraftingBowlModel;
import net.satisfy.farm_and_charm.client.model.DungareesLeggingsModel;
import net.satisfy.farm_and_charm.client.model.MincerModel;
import net.satisfy.farm_and_charm.client.model.PlowCartModel;
import net.satisfy.farm_and_charm.client.model.ScarecrowModel;
import net.satisfy.farm_and_charm.client.model.SeederCartModel;
import net.satisfy.farm_and_charm.client.model.SupplyCartModel;
import net.satisfy.farm_and_charm.client.model.WaterSprinklerModel;
import net.satisfy.farm_and_charm.client.model.WellPumpModel;
import net.satisfy.farm_and_charm.client.renderer.block.ChickenNestRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.CraftingBowlRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.CuttingBoardRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.MincerRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.PetBowlBlockRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.ScarecrowRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.StoveBlockRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.SturdyLadderRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.TimberWellRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.ToolRackRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.WaterSprinklerRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.WindowSillRenderer;
import net.satisfy.farm_and_charm.client.renderer.entity.PlowCartRenderer;
import net.satisfy.farm_and_charm.client.renderer.entity.SeederCartRenderer;
import net.satisfy.farm_and_charm.client.renderer.entity.SupplyCartRenderer;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.ambient.FireflyAmbience;
import net.satisfy.foundation.client.armor.ArmorModels;
import net.satisfy.foundation.client.mimic.MimicModels;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.foundation.storage.StorageBlockEntityRenderer;
import net.satisfy.foundation.tooltip.InfoTooltip;

public class FarmAndCharmClient {
   private static boolean mimicModelsRegistered;

   public static void onInitializeClient() {
      RenderTypeRegistry.register(
         RenderType.cutout(),
         new Block[]{
            (Block)ObjectRegistry.CRAFTING_BOWL.get(),
            (Block)ObjectRegistry.CUTTING_BOARD.get(),
            (Block)ObjectRegistry.WATER_SPRINKLER.get(),
            (Block)ObjectRegistry.SCARECROW.get(),
            (Block)ObjectRegistry.STOVE.get(),
            (Block)ObjectRegistry.MINCER.get(),
            (Block)ObjectRegistry.WILD_RIBWORT.get(),
            (Block)ObjectRegistry.WILD_BARLEY.get(),
            (Block)ObjectRegistry.WILD_CARROTS.get(),
            (Block)ObjectRegistry.RIBWORT_TEA.get(),
            (Block)ObjectRegistry.NETTLE_TEA.get(),
            (Block)ObjectRegistry.STRAWBERRY_TEA.get(),
            (Block)ObjectRegistry.WILD_BEETROOTS.get(),
            (Block)ObjectRegistry.WILD_CORN.get(),
            (Block)ObjectRegistry.WILD_EMMER.get(),
            (Block)ObjectRegistry.WILD_LETTUCE.get(),
            (Block)ObjectRegistry.WILD_NETTLE.get(),
            (Block)ObjectRegistry.WILD_OAT.get(),
            (Block)ObjectRegistry.WILD_ONIONS.get(),
            (Block)ObjectRegistry.WILD_POTATOES.get(),
            (Block)ObjectRegistry.WILD_TOMATOES.get(),
            (Block)ObjectRegistry.WILD_STRAWBERRIES.get(),
            (Block)ObjectRegistry.STUFFED_RABBIT.get(),
            (Block)ObjectRegistry.STUFFED_CHICKEN.get(),
            (Block)ObjectRegistry.FARMERS_BREAKFAST.get(),
            (Block)ObjectRegistry.ROASTED_CORN_BLOCK.get(),
            (Block)ObjectRegistry.OAT_PANCAKE_BLOCK.get(),
            (Block)ObjectRegistry.CORN_CROP.get(),
            (Block)ObjectRegistry.OAT_CROP.get(),
            (Block)ObjectRegistry.BARLEY_CROP.get(),
            (Block)ObjectRegistry.LETTUCE_CROP.get(),
            (Block)ObjectRegistry.ONION_CROP.get(),
            (Block)ObjectRegistry.TOMATO_CROP.get(),
            (Block)ObjectRegistry.STRAWBERRY_CROP.get(),
            (Block)ObjectRegistry.COOKING_POT.get(),
            (Block)ObjectRegistry.ROASTER.get(),
            (Block)ObjectRegistry.TOMATO_CROP_BODY.get(),
            (Block)ObjectRegistry.CHICKEN_NEST.get(),
            (Block)ObjectRegistry.STURDY_LADDER.get(),
            (Block)ObjectRegistry.IRON_DIVIDER.get(),
            (Block)ObjectRegistry.CHICKEN_FENCE.get(),
            (Block)ObjectRegistry.CATTLEGRID.get(),
            (Block)ObjectRegistry.FEATHER_PILE.get(),
            (Block)ObjectRegistry.WHEAT_PILE.get()
         }
      );
      ColorHandlerRegistry.registerBlockColors(
         (state, world, pos, tintIndex) -> tintIndex == 1 && world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) : -1,
         new Block[]{(Block)ObjectRegistry.WATER_TROUGH.get(), (Block)ObjectRegistry.TIMBER_WELL.get()}
      );
      ClientStorageTypes.init();
      ClientEventHandler.init();
      FireflyAmbience.init(PlatformHelper::isFirefliesEnabled);
      BlockInfoOverlay.init();
      BlockInfoOverlay.registerProvider(new CuttingBoardInfoProvider());
      BlockInfoOverlay.registerProvider(new SiloInfoProvider());
      BlockInfoOverlay.registerProvider(new MincerInfoProvider());
      BlockInfoOverlay.registerProvider(new StoveInfoProvider());
      BlockInfoOverlay.registerProvider(new CraftingBowlInfoProvider());
      BlockInfoOverlay.registerProvider(new WaterSprinklerInfoProvider());
      BlockInfoOverlay.registerProvider(new ScarecrowInfoProvider());
      InfoTooltip.init();
      InfoTooltip.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get()).placeable().details(3).register();
      registerStorageTypeRenderers();
      registerBlockEntityRenderer();
      registerArmorModels();
      MenuRegistry.registerScreenFactory((MenuType)ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get(), CookingPotGui::new);
      MenuRegistry.registerScreenFactory((MenuType)ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), StoveGui::new);
      MenuRegistry.registerScreenFactory((MenuType)ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), RoasterGui::new);
   }

   public static void registerEntityRenderers() {
      EntityRendererRegistry.register(EntityTypeRegistry.ROTTEN_TOMATO, ThrownItemRenderer::new);
      EntityRendererRegistry.register(EntityTypeRegistry.SUPPLY_CART, SupplyCartRenderer::new);
      EntityRendererRegistry.register(EntityTypeRegistry.PLOW, PlowCartRenderer::new);
      EntityRendererRegistry.register(EntityTypeRegistry.SEEDER, SeederCartRenderer::new);
   }

   private static void registerMimicModels() {
      if (!mimicModelsRegistered) {
         mimicModelsRegistered = true;
         MimicModels.held(new Supplier[]{ObjectRegistry.ROPE_KNOT});
      }
   }

   public static void preInitClient() {
      registerMimicModels();
      registerEntityRenderers();
      registerEntityModelLayer();
   }

   public static void registerEntityModelLayer() {
      EntityModelLayerRegistry.register(WaterSprinklerModel.LAYER_LOCATION, WaterSprinklerModel::getTexturedModelData);
      EntityModelLayerRegistry.register(CraftingBowlModel.LAYER_LOCATION, CraftingBowlModel::getTexturedModelData);
      EntityModelLayerRegistry.register(MincerModel.LAYER_LOCATION, MincerModel::getTexturedModelData);
      EntityModelLayerRegistry.register(ScarecrowModel.LAYER_LOCATION, ScarecrowModel::getTexturedModelData);
      EntityModelLayerRegistry.register(WellPumpModel.LAYER_LOCATION, WellPumpModel::getTexturedModelData);
      EntityModelLayerRegistry.register(SupplyCartModel.LAYER_LOCATION, SupplyCartModel::createBodyLayer);
      EntityModelLayerRegistry.register(PlowCartModel.LAYER_LOCATION, PlowCartModel::createBodyLayer);
      EntityModelLayerRegistry.register(SeederCartModel.LAYER_LOCATION, SeederCartModel::createBodyLayer);
      EntityModelLayerRegistry.register(DungareesLeggingsModel.LAYER_LOCATION, DungareesLeggingsModel::createBodyLayer);
   }

   public static void registerStorageTypeRenderers() {
      StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.TOOL_RACK, new ToolRackRenderer());
      StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.WINDOW_SILL, new WindowSillRenderer());
      StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.CHICKEN_NEST, new ChickenNestRenderer());
   }

   public static void registerArmorModels() {
      ArmorModels.register(DungareesLeggingsModel.LAYER_LOCATION, DungareesLeggingsModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.DUNGAREES.get()});
   }

   public static void registerBlockEntityRenderer() {
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.STOVE_BLOCK_ENTITY.get(), StoveBlockRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), ScarecrowRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.TIMBER_WELL_BLOCK_ENTITY.get(), TimberWellRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.MINCER_BLOCK_ENTITY.get(), MincerRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.CRAFTING_BOWL_BLOCK_ENTITY.get(), CraftingBowlRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.CUTTING_BOARD_BLOCK_ENTITY.get(), CuttingBoardRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.SPRINKLER_BLOCK_ENTITY.get(), WaterSprinklerRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.STURDY_LADDER_BLOCK_ENTITY.get(), SturdyLadderRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.PET_BOWL_BLOCK_ENTITY.get(), context -> new PetBowlBlockRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
   }

   public static void openPetBowlScreen(PetBowlBlockEntity entity) {
      Minecraft.getInstance().setScreen(new PetBowlEditGui(entity));
   }
}
