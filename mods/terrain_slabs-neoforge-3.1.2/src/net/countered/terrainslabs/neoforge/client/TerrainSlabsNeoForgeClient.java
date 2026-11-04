package net.countered.terrainslabs.neoforge.client;

import net.countered.terrainslabs.registries.ModBlocksRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class TerrainSlabsNeoForgeClient {
   @SubscribeEvent
   public static void init(FMLClientSetupEvent event) {
      registerRenderLayers();
      registerBlockColorProviders();
      registerItemColorProviders();
   }

   public static void registerRenderLayers() {
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocksRegistry.ICE_SLAB.get(), RenderType.translucent());
      ItemBlockRenderTypes.setRenderLayer((Block)ModBlocksRegistry.GRASS_SLAB.get(), RenderType.cutoutMipped());
   }

   public static void registerBlockColorProviders() {
      Minecraft.getInstance()
         .getBlockColors()
         .register(
            (state, world, pos, tintIndex) -> world != null && pos != null
               ? Minecraft.getInstance().getBlockColors().getColor(Blocks.GRASS_BLOCK.defaultBlockState(), world, pos, tintIndex)
               : GrassColor.getDefaultColor(),
            new Block[]{(Block)ModBlocksRegistry.GRASS_SLAB.get()}
         );
   }

   public static void registerItemColorProviders() {
      Minecraft.getInstance()
         .getItemColors()
         .register(
            (stack, tintIndex) -> Minecraft.getInstance().getBlockColors().getColor(Blocks.GRASS_BLOCK.defaultBlockState(), null, null, tintIndex),
            new ItemLike[]{(ItemLike)ModBlocksRegistry.GRASS_SLAB.get()}
         );
   }
}
