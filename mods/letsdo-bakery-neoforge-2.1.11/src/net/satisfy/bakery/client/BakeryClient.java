package net.satisfy.bakery.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.bakery.client.gui.StreetSignEditGui;
import net.satisfy.bakery.client.gui.hud.SugarRushStacksHud;
import net.satisfy.bakery.client.gui.overlay.BakerStationInfoProvider;
import net.satisfy.bakery.client.gui.overlay.DisplayInfoProvider;
import net.satisfy.bakery.client.gui.overlay.JamPotInfoProvider;
import net.satisfy.bakery.client.renderer.block.BakerStationRenderer;
import net.satisfy.bakery.client.renderer.block.BlankCakeRenderer;
import net.satisfy.bakery.client.renderer.block.BreadBoxRenderer;
import net.satisfy.bakery.client.renderer.block.CakeCandleRenderer;
import net.satisfy.bakery.client.renderer.block.CakeDisplayRenderer;
import net.satisfy.bakery.client.renderer.block.CakeStandRenderer;
import net.satisfy.bakery.client.renderer.block.CupcakeDisplayRenderer;
import net.satisfy.bakery.client.renderer.block.JamPotRenderer;
import net.satisfy.bakery.client.renderer.block.StreetSignBlockRenderer;
import net.satisfy.bakery.client.renderer.block.TrayRenderer;
import net.satisfy.bakery.client.renderer.block.WallDisplayRenderer;
import net.satisfy.bakery.core.block.TrayBlock;
import net.satisfy.bakery.core.block.entity.StreetSignBlockEntity;
import net.satisfy.bakery.core.recipe.Filling;
import net.satisfy.bakery.core.registry.DataComponentRegistry;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.banner.CompletionistBannerRenderer;
import net.satisfy.foundation.block.ClothColor;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.foundation.storage.StorageBlockEntityRenderer;
import net.satisfy.foundation.storage.StorageTypeRenderer;

@OnlyIn(Dist.CLIENT)
public class BakeryClient {
   public static void initClient() {
      RenderTypeRegistry.register(
         RenderType.cutout(),
         new Block[]{
            (Block)ObjectRegistry.CAKE_STAND.get(),
            (Block)ObjectRegistry.IRON_TABLE.get(),
            (Block)ObjectRegistry.IRON_CHAIR.get(),
            (Block)ObjectRegistry.JAR.get(),
            (Block)ObjectRegistry.SWEETBERRY_JAM.get(),
            (Block)ObjectRegistry.CHOCOLATE_JAM.get(),
            (Block)ObjectRegistry.STRAWBERRY_JAM.get(),
            (Block)ObjectRegistry.GLOWBERRY_JAM.get(),
            (Block)ObjectRegistry.APPLE_JAM.get(),
            (Block)ObjectRegistry.CAKE_DISPLAY.get(),
            (Block)ObjectRegistry.SMALL_COOKING_POT.get(),
            (Block)ObjectRegistry.IRON_BENCH.get(),
            (Block)ObjectRegistry.BAKER_STATION.get(),
            (Block)ObjectRegistry.TRAY.get()
         }
      );
      registerStorageType();
      registerBlockEntityRenderer();
      BlockInfoOverlay.init();
      BlockInfoOverlay.registerProvider(new BakerStationInfoProvider());
      BlockInfoOverlay.registerProvider(new JamPotInfoProvider());
      BlockInfoOverlay.registerProvider(new DisplayInfoProvider());
      ClientGuiEvent.RENDER_HUD.register(SugarRushStacksHud::render);
      ColorHandlerRegistry.registerBlockColors(
         (state, level, pos, tintIndex) -> tintIndex == 0 ? ((ClothColor)state.getValue(TrayBlock.COLOR)).tint() : -1,
         new Block[]{(Block)ObjectRegistry.TRAY.get()}
      );
      ColorHandlerRegistry.registerItemColors(
         (stack, tintIndex) -> {
            if (tintIndex != 1) {
               return -1;
            }

            ClothColor color = (ClothColor)((BlockItemStateProperties)stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY))
               .get(TrayBlock.COLOR);
            return color != null ? color.tint() : -1;
         },
         new ItemLike[]{(ItemLike)ObjectRegistry.TRAY.get()}
      );
      ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
         if (tintIndex != 1) {
            return -1;
         }

         Filling filling = (Filling)stack.get((DataComponentType)DataComponentRegistry.FILLING.get());
         int fallback = stack.is((Item)ObjectRegistry.CORNET.get()) ? 16117990 : 13120060;
         return 0xFF000000 | (filling != null ? filling.color() : fallback);
      }, new ItemLike[]{(ItemLike)ObjectRegistry.CORNET.get(), (ItemLike)ObjectRegistry.JAM_ROLL.get()});
      ClientTooltipEvent.ITEM
         .register(
            (dev.architectury.event.events.client.ClientTooltipEvent.Item)(stack, lines, context, flag) -> {
               if (stack.is((Item)ObjectRegistry.CORNET_SHELL.get()) || stack.is((Item)ObjectRegistry.SPONGE_SHEET.get())) {
                  lines.add(
                     Math.min(1, lines.size()), Component.translatable("tooltip.bakery.unfilled").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10526880)))
                  );
               }

               Filling filling = (Filling)stack.get((DataComponentType)DataComponentRegistry.FILLING.get());
               if (filling != null) {
                  lines.add(
                     Math.min(1, lines.size()),
                     Component.translatable("tooltip.bakery.filling", new Object[]{filling.sourceItem().getDescription()})
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10526880)))
                  );
               }

               String cook = (String)stack.get((DataComponentType)DataComponentRegistry.PERFECT_JAM.get());
               if (cook != null && !cook.isEmpty()) {
                  int index = lines.size();

                  for (int line = 0; line < lines.size(); line++) {
                     if (((Component)lines.get(line)).getContents() instanceof TranslatableContents contents
                        && contents.getKey().equals("tooltip.foundation.canbeplaced")) {
                        index = line + 1;
                     }
                  }

                  lines.add(index, Component.empty());
                  lines.add(
                     index + 1,
                     Component.translatable("tooltip.bakery.jam_cook", new Object[]{cook})
                        .withStyle(Style.EMPTY.withItalic(true).withColor(TextColor.fromRgb(14919328)))
                  );
               }
            }
         );
      RenderTypeRegistry.register(RenderType.translucent(), new Block[]{(Block)ObjectRegistry.CAKE_STAND.get()});
   }

   public static void openStreetSignScreen(StreetSignBlockEntity entity) {
      Minecraft.getInstance().setScreen(new StreetSignEditGui(entity));
   }

   public static void preInitClient() {
      registerEntityModelLayer();
   }

   public static void registerStorageType(ResourceLocation location, StorageTypeRenderer renderer) {
      StorageBlockEntityRenderer.registerStorageType(location, renderer);
   }

   public static void registerStorageType() {
      registerStorageType(StorageTypeRegistry.CAKE_STAND, new CakeStandRenderer());
      registerStorageType(StorageTypeRegistry.TRAY, new TrayRenderer());
      registerStorageType(StorageTypeRegistry.BREADBOX, new BreadBoxRenderer());
      registerStorageType(StorageTypeRegistry.CAKE_DISPLAY, new CakeDisplayRenderer());
      registerStorageType(StorageTypeRegistry.CUPCAKE_DISPLAY, new CupcakeDisplayRenderer());
      registerStorageType(StorageTypeRegistry.WALL_DISPLAY, new WallDisplayRenderer());
   }

   public static void registerBlockEntityRenderer() {
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BAKERY_BANNER.get(), CompletionistBannerRenderer::new);
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.STREET_SIGN_BLOCK_ENTITY.get(), context -> new StreetSignBlockRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BLANK_CAKE_BLOCK_ENTITY.get(), context -> new BlankCakeRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.CAKE_CANDLE_BLOCK_ENTITY.get(), context -> new CakeCandleRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.BAKER_STATION_BLOCK_ENTITY.get(), context -> new BakerStationRenderer());
      BlockEntityRendererRegistry.register((BlockEntityType)EntityTypeRegistry.SMALL_COOKING_POT_BLOCK_ENTITY.get(), JamPotRenderer::new);
   }

   public static void registerEntityModelLayer() {
   }
}
