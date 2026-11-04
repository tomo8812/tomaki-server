package de.maxhenkel.gravestone.corelib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class RendererProviders {
   private static final Minecraft minecraft = Minecraft.getInstance();

   public static Context createBlockEntityRendererContext() {
      return new Context(
         minecraft.getBlockEntityRenderDispatcher(),
         minecraft.getBlockRenderer(),
         minecraft.getItemRenderer(),
         minecraft.getEntityRenderDispatcher(),
         minecraft.getEntityModels(),
         minecraft.font
      );
   }

   public static net.minecraft.client.renderer.entity.EntityRendererProvider.Context createEntityRendererContext() {
      return new net.minecraft.client.renderer.entity.EntityRendererProvider.Context(
         minecraft.getEntityRenderDispatcher(),
         minecraft.getItemRenderer(),
         minecraft.getBlockRenderer(),
         minecraft.gameRenderer.itemInHandRenderer,
         minecraft.getResourceManager(),
         minecraft.getEntityModels(),
         minecraft.font
      );
   }
}
