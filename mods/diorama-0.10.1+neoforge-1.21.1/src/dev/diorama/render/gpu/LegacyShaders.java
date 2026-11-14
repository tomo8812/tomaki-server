package dev.diorama.render.gpu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LegacyShaders {
   private static final Logger LOGGER = LoggerFactory.getLogger("diorama");
   private static ShaderInstance fogShader;
   private static ShaderInstance fogLayerShader;
   private static boolean fogLayerFailed;
   private static ShaderInstance tileShader;
   private static ShaderInstance tileNightShader;
   private static final ShaderStateShard TILE_NIGHT_SHADER = new ShaderStateShard(() -> tileNightShader);
   private static final ShaderStateShard TEXTURED_SHADER = new ShaderStateShard(GameRenderer::getPositionTexColorShader);
   private static final ShaderStateShard FOG_SHADER = new ShaderStateShard(() -> fogShader);
   private static final ShaderStateShard FOG_LAYER_SHADER = new ShaderStateShard(() -> fogLayerShader);
   private static final ShaderStateShard TILE_SHADER = new ShaderStateShard(() -> tileShader);
   private static Object fogReload;
   private static Object tileReload;
   private static Object tileNightReload;
   private static boolean fogFailed;
   private static boolean tileFailed;
   private static boolean tileNightFailed;
   static final RenderType TEXTURED = create("diorama_textured", DefaultVertexFormat.POSITION_TEX_COLOR, TEXTURED_SHADER);
   static final RenderType SOLID = create("diorama_solid", DefaultVertexFormat.POSITION_COLOR, RenderStateShard.POSITION_COLOR_SHADER);
   static final RenderType FOG = create("diorama_fog", DefaultVertexFormat.POSITION_TEX_COLOR, FOG_SHADER);
   private static final RenderType FOG_LAYER = create("diorama_fog_layer", DefaultVertexFormat.POSITION_TEX_COLOR, FOG_LAYER_SHADER);
   private static final RenderType TILE = create("diorama_tile", DefaultVertexFormat.POSITION_TEX_COLOR, TILE_SHADER);
   private static final RenderType TILE_NIGHT = create("diorama_tile_night", DefaultVertexFormat.POSITION_TEX_COLOR, TILE_NIGHT_SHADER);

   private static RenderType create(String name, VertexFormat format, ShaderStateShard shader) {
      return RenderType.create(
         name,
         format,
         Mode.QUADS,
         1536,
         false,
         false,
         CompositeState.builder()
            .setShaderState(shader)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .createCompositeState(false)
      );
   }

   static boolean fogReady() {
      ShaderInstance reload = GameRenderer.getPositionTexColorShader();
      if (reload != fogReload) {
         fogReload = reload;
         fogFailed = false;
         if (fogShader != null) {
            fogShader.close();
            fogShader = null;
         }

         if (fogLayerShader != null) {
            fogLayerShader.close();
            fogLayerShader = null;
         }

         fogLayerFailed = false;
      }

      if (fogShader == null && !fogFailed) {
         try {
            fogShader = new ShaderInstance(Minecraft.getInstance().getResourceManager(), "diorama_fog", DefaultVertexFormat.POSITION_TEX_COLOR);
         } catch (Exception e) {
            fogFailed = true;
            LOGGER.error("Diorama's fog shader didn't compile; the map draws without fog", e);
         }
      }

      return fogShader != null;
   }

   static boolean fogLayerReady() {
      if (fogLayerShader == null && fogShader != null && !fogLayerFailed) {
         try {
            fogLayerShader = new ShaderInstance(Minecraft.getInstance().getResourceManager(), "diorama_fog_layer", DefaultVertexFormat.POSITION_TEX_COLOR);
         } catch (Exception e) {
            fogLayerFailed = true;
            LOGGER.error("Diorama's fog layer shader didn't compile; the map draws the fog directly", e);
         }
      }

      return fogLayerShader != null;
   }

   static void drawFogLayer(MeshData mesh, int framebuffer, int size) {
      boolean depth = GL11.glIsEnabled(2929);
      FOG_LAYER.setupRenderState();
      RenderSystem.disableBlend();
      RenderSystem.disableDepthTest();
      GlStateManager._glBindFramebuffer(36160, framebuffer);
      GlStateManager._viewport(0, 0, size, size);
      BufferUploader.drawWithShader(mesh);
      FOG_LAYER.clearRenderState();
      Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
      if (depth) {
         RenderSystem.enableDepthTest();
      }
   }

   static boolean tileReady() {
      ShaderInstance reload = GameRenderer.getPositionTexColorShader();
      if (reload != tileReload) {
         tileReload = reload;
         tileFailed = false;
         if (tileShader != null) {
            tileShader.close();
            tileShader = null;
         }
      }

      if (tileShader == null && !tileFailed) {
         try {
            tileShader = new ShaderInstance(Minecraft.getInstance().getResourceManager(), "diorama_tile", DefaultVertexFormat.POSITION_TEX_COLOR);
         } catch (Exception e) {
            tileFailed = true;
            LOGGER.error("Diorama's tile shader didn't compile; the map draws a little softer", e);
         }
      }

      return tileShader != null;
   }

   static boolean tileNightReady() {
      ShaderInstance reload = GameRenderer.getPositionTexColorShader();
      if (reload != tileNightReload) {
         tileNightReload = reload;
         tileNightFailed = false;
         if (tileNightShader != null) {
            tileNightShader.close();
            tileNightShader = null;
         }
      }

      if (tileNightShader == null && !tileNightFailed) {
         try {
            tileNightShader = new ShaderInstance(Minecraft.getInstance().getResourceManager(), "diorama_tile_night", DefaultVertexFormat.POSITION_TEX_COLOR);
         } catch (Exception e) {
            tileNightFailed = true;
            LOGGER.error("Diorama's night tile shader didn't compile; the map draws night without block light", e);
         }
      }

      return tileNightShader != null;
   }

   static void draw(RenderType type, MeshData mesh) {
      boolean depth = GL11.glIsEnabled(2929);
      RenderSystem.disableDepthTest();
      type.draw(mesh);
      if (depth) {
         RenderSystem.enableDepthTest();
      }
   }

   static void drawFog(MeshData mesh) {
      draw(FOG, mesh);
   }

   static void drawTile(MeshData mesh) {
      draw(TILE, mesh);
   }

   static void drawTileNight(MeshData mesh) {
      draw(TILE_NIGHT, mesh);
   }

   private LegacyShaders() {
   }
}
