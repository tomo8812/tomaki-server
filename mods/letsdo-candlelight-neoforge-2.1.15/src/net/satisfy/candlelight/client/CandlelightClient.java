package net.satisfy.candlelight.client;

import dev.architectury.platform.Platform;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.candlelight.client.compat.AccessoriesClientCompat;
import net.satisfy.candlelight.client.gui.LetterGui;
import net.satisfy.candlelight.client.gui.TableSignEditScreen;
import net.satisfy.candlelight.client.model.CookingBootsModel;
import net.satisfy.candlelight.client.model.CookingChestplateModel;
import net.satisfy.candlelight.client.model.CookingHatModel;
import net.satisfy.candlelight.client.model.CookingLeggingsModel;
import net.satisfy.candlelight.client.model.DinnerBellModel;
import net.satisfy.candlelight.client.model.DressChestplateModel;
import net.satisfy.candlelight.client.model.FlowerCrownModel;
import net.satisfy.candlelight.client.model.SuitLeggingsModel;
import net.satisfy.candlelight.client.model.TieModel;
import net.satisfy.candlelight.client.model.TypewriterModel;
import net.satisfy.candlelight.client.overlay.SideTableInfoProvider;
import net.satisfy.candlelight.client.overlay.TableSetInfoProvider;
import net.satisfy.candlelight.client.overlay.TableSignInfoProvider;
import net.satisfy.candlelight.client.renderer.block.DinnerBellRenderer;
import net.satisfy.candlelight.client.renderer.block.JewelryRenderer;
import net.satisfy.candlelight.client.renderer.block.SideTableRenderer;
import net.satisfy.candlelight.client.renderer.block.TableSetRenderer;
import net.satisfy.candlelight.client.renderer.block.TypewriterRenderer;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.core.registry.ScreenHandlerTypeRegistry;
import net.satisfy.candlelight.core.registry.StorageTypeRegistry;
import net.satisfy.candlelight.core.registry.TabRegistry;
import net.satisfy.foundation.banner.CompletionistBannerRenderer;
import net.satisfy.foundation.client.armor.ArmorColors;
import net.satisfy.foundation.client.armor.ArmorModels;
import net.satisfy.foundation.client.creative.CreativeSideTabs;
import net.satisfy.foundation.client.creative.CreativeSideTabs.SideTab;
import net.satisfy.foundation.client.render.WallDecorationRenderer;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.foundation.storage.StorageBlockEntityRenderer;
import net.satisfy.foundation.storage.StorageTypeRenderer;
import net.satisfy.foundation.storage.WallShelfRenderer;

@OnlyIn(Dist.CLIENT)
public class CandlelightClient {
   public static void initClient() {
      registerArmorModels();
      RenderTypeRegistry.register(
         RenderType.cutout(),
         new Block[]{
            (Block)ObjectRegistry.TABLE.get(),
            (Block)ObjectRegistry.ROSE.get(),
            (Block)ObjectRegistry.POTTED_ROSE.get(),
            (Block)ObjectRegistry.GLASS_BLOCK.get(),
            (Block)ObjectRegistry.OAK_CHAIR.get(),
            (Block)ObjectRegistry.DARK_OAK_CHAIR.get(),
            (Block)ObjectRegistry.SPRUCE_CHAIR.get(),
            (Block)ObjectRegistry.WARPED_CHAIR.get(),
            (Block)ObjectRegistry.BIRCH_CHAIR.get(),
            (Block)ObjectRegistry.MANGROVE_CHAIR.get(),
            (Block)ObjectRegistry.ACACIA_CHAIR.get(),
            (Block)ObjectRegistry.CRIMSON_CHAIR.get(),
            (Block)ObjectRegistry.JUNGLE_CHAIR.get(),
            (Block)ObjectRegistry.OAK_TABLE.get(),
            (Block)ObjectRegistry.ACACIA_TABLE.get(),
            (Block)ObjectRegistry.DARK_OAK_TABLE.get(),
            (Block)ObjectRegistry.BIRCH_TABLE.get(),
            (Block)ObjectRegistry.SPRUCE_TABLE.get(),
            (Block)ObjectRegistry.JUNGLE_TABLE.get(),
            (Block)ObjectRegistry.MANGROVE_TABLE.get(),
            (Block)ObjectRegistry.WARPED_TABLE.get(),
            (Block)ObjectRegistry.CRIMSON_TABLE.get(),
            (Block)ObjectRegistry.CHAIR.get(),
            (Block)ObjectRegistry.TABLE.get(),
            (Block)ObjectRegistry.BAMBOO_CHAIR.get(),
            (Block)ObjectRegistry.BAMBOO_TABLE.get(),
            (Block)ObjectRegistry.CHERRY_TABLE.get(),
            (Block)ObjectRegistry.CHERRY_CHAIR.get(),
            (Block)ObjectRegistry.WINE_GLASS_BLOCK.get(),
            (Block)ObjectRegistry.COOKING_POT.get(),
            (Block)ObjectRegistry.COOKING_PAN.get(),
            (Block)ObjectRegistry.RED_NETHER_BRICKS_STOVE.get(),
            (Block)ObjectRegistry.QUARTZ_STOVE.get(),
            (Block)ObjectRegistry.MUD_STOVE.get(),
            (Block)ObjectRegistry.END_STOVE.get(),
            (Block)ObjectRegistry.GRANITE_STOVE.get(),
            (Block)ObjectRegistry.DEEPSLATE_STOVE.get(),
            (Block)ObjectRegistry.SANDSTONE_STOVE.get(),
            (Block)ObjectRegistry.STONE_BRICKS_STOVE.get(),
            (Block)ObjectRegistry.COBBLESTONE_STOVE.get(),
            (Block)ObjectRegistry.BAMBOO_STOVE.get()
         }
      );
      RenderTypeRegistry.register(
         RenderType.translucent(),
         new Block[]{(Block)ObjectRegistry.WINE_GLASS_BLOCK.get(), (Block)ObjectRegistry.TABLE_SET.get(), (Block)ObjectRegistry.GLASS_BLOCK.get()}
      );
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.CANDLELIGHT_BANNER_ENTITY.get(), CompletionistBannerRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.STORAGE_BLOCK_ENTITY.get(), context -> new StorageBlockEntityRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.SIDE_TABLE_BLOCK_ENTITY.get(), SideTableRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.TABLE_SET_BLOCK_ENTITY.get(), context -> new StorageBlockEntityRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.DINNER_BELL_BLOCK_ENTITY.get(), DinnerBellRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.TYPE_WRITER_BLOCK_ENTITY.get(), TypewriterRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.WALL_DECORATION.get(), WallDecorationRenderer::new);
      MenuRegistry.registerScreenFactory((MenuType)ScreenHandlerTypeRegistry.LETTER_SCREEN_HANDLER.get(), LetterGui::new);
      registerStorageType();
      ArmorColors.register(new Item[]{(Item)ObjectRegistry.DRESS.get(), (Item)ObjectRegistry.TROUSERS_AND_VEST.get()});
      DrinkColors.init();
      CreativeSideTabs.register(
         TabRegistry.CANDLELIGHT_TAB.getKey(),
         new SideTab[]{
            SideTab.of(Component.translatable("creativetab.candlelight.side.food"), (ItemLike)ObjectRegistry.BEEF_WELLINGTON.get(), TabRegistry::acceptMain),
            SideTab.of(
               Component.translatable("creativetab.candlelight.side.kitchen_blocks"),
               (ItemLike)ObjectRegistry.COBBLESTONE_STOVE.get(),
               TabRegistry::acceptKitchen
            ),
            SideTab.of(Component.translatable("creativetab.candlelight.side.furniture"), (ItemLike)ObjectRegistry.OAK_CHAIR.get(), TabRegistry::acceptFurniture)
         }
      );
      BlockInfoOverlay.init();
      BlockInfoOverlay.registerProvider(new TableSetInfoProvider());
      BlockInfoOverlay.registerProvider(new TableSignInfoProvider());
      BlockInfoOverlay.registerProvider(new SideTableInfoProvider());
      if (Platform.isModLoaded("accessories")) {
         AccessoriesClientCompat.init();
      }
   }

   public static void openTableSignScreen(InteractionHand hand, String text) {
      Minecraft.getInstance().setScreen(new TableSignEditScreen(hand, text));
   }

   public static void registerStorageType(ResourceLocation location, StorageTypeRenderer renderer) {
      StorageBlockEntityRenderer.registerStorageType(location, renderer);
   }

   public static void registerStorageType() {
      registerStorageType(StorageTypeRegistry.SHELF, new WallShelfRenderer());
      registerStorageType(StorageTypeRegistry.TABLE_SET, new TableSetRenderer());
      registerStorageType(StorageTypeRegistry.JEWELRY_BOX, new JewelryRenderer());
   }

   public static void registerArmorModels() {
      ArmorModels.register(CookingHatModel.LAYER_LOCATION, CookingHatModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.COOKING_HAT.get()});
      ArmorModels.register(FlowerCrownModel.LAYER_LOCATION, FlowerCrownModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.FLOWER_CROWN.get()});
      ArmorModels.register(TieModel.LAYER_LOCATION, TieModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.NECKTIE.get()});
      ArmorModels.register(
         CookingChestplateModel.LAYER_LOCATION,
         CookingChestplateModel::new,
         new ItemLike[]{(ItemLike)ObjectRegistry.CHEFS_JACKET.get(), (ItemLike)ObjectRegistry.FORMAL_SHIRT.get(), (ItemLike)ObjectRegistry.SHIRT.get()}
      );
      ArmorModels.register(DressChestplateModel.LAYER_LOCATION, DressChestplateModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.DRESS.get()});
      ArmorModels.register(CookingLeggingsModel.LAYER_LOCATION, CookingLeggingsModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.CHEFS_PANTS.get()});
      ArmorModels.register(SuitLeggingsModel.LAYER_LOCATION, SuitLeggingsModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.TROUSERS_AND_VEST.get()});
      ArmorModels.register(CookingBootsModel.LAYER_LOCATION, CookingBootsModel::new, new ItemLike[]{(ItemLike)ObjectRegistry.CHEFS_BOOTS.get()});
   }

   public static void preInitClient() {
      registerEntityModelLayers();
   }

   public static void registerEntityModelLayers() {
      EntityModelLayerRegistry.register(TypewriterModel.LAYER_LOCATION, TypewriterModel::getTexturedModelData);
      EntityModelLayerRegistry.register(DinnerBellModel.LAYER_LOCATION, DinnerBellModel::getTexturedModelData);
      EntityModelLayerRegistry.register(FlowerCrownModel.LAYER_LOCATION, FlowerCrownModel::createBodyLayer);
      EntityModelLayerRegistry.register(TieModel.LAYER_LOCATION, TieModel::createBodyLayer);
      EntityModelLayerRegistry.register(DressChestplateModel.LAYER_LOCATION, DressChestplateModel::createBodyLayer);
      EntityModelLayerRegistry.register(CookingHatModel.LAYER_LOCATION, CookingHatModel::createBodyLayer);
      EntityModelLayerRegistry.register(CookingChestplateModel.LAYER_LOCATION, CookingChestplateModel::createBodyLayer);
      EntityModelLayerRegistry.register(CookingLeggingsModel.LAYER_LOCATION, CookingLeggingsModel::createBodyLayer);
      EntityModelLayerRegistry.register(CookingBootsModel.LAYER_LOCATION, CookingBootsModel::createBodyLayer);
   }
}
