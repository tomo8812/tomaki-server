package dev.diorama.render.gpu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.diorama.render.NightLook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

final class ImmediateCanvas implements MapCanvas {
   private final GuiGraphics graphics;

   ImmediateCanvas(GuiGraphics graphics) {
      this.graphics = graphics;
   }

   @Override
   public void quad(MapTexture texture, Sampling sampling, float[] corners, float[] uvs, int argb) {
      BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_TEX_COLOR);
      Matrix4f pose = this.graphics.pose().last().pose();

      for (int i = 0; i < corners.length / 2; i++) {
         mesh.addVertex(pose, corners[i * 2], corners[i * 2 + 1], 0.0F).setUv(uvs[i * 2], uvs[i * 2 + 1]).setColor(argb);
      }

      ((LegacyTexture)texture).bind(0, sampling);
      if (sampling == Sampling.TILE_AREA && LegacyShaders.tileReady()) {
         LegacyShaders.drawTile(mesh.buildOrThrow());
      } else {
         LegacyShaders.draw(LegacyShaders.TEXTURED, mesh.buildOrThrow());
      }

      unbind(0);
   }

   @Override
   public void nightTile(MapTexture tile, Sampling sampling, MapTexture light, float[] corners, float[] uvs, int params) {
      if (!LegacyShaders.tileNightReady()) {
         this.quad(tile, sampling, corners, uvs, NightLook.tintOf(params));
      } else {
         BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_TEX_COLOR);
         Matrix4f pose = this.graphics.pose().last().pose();

         for (int i = 0; i < corners.length / 2; i++) {
            mesh.addVertex(pose, corners[i * 2], corners[i * 2 + 1], 0.0F).setUv(uvs[i * 2], uvs[i * 2 + 1]).setColor(params);
         }

         ((LegacyTexture)tile).bind(0, sampling);
         ((LegacyTexture)light).bind(1, Sampling.TILE_SMOOTH);
         LegacyShaders.drawTileNight(mesh.buildOrThrow());
         unbind(0);
         unbind(1);
      }
   }

   @Override
   public void solid(float[] corners, int argb) {
      BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_COLOR);
      Matrix4f pose = this.graphics.pose().last().pose();

      for (int i = 0; i < corners.length / 2; i++) {
         mesh.addVertex(pose, corners[i * 2], corners[i * 2 + 1], 0.0F).setColor(argb);
      }

      LegacyShaders.draw(LegacyShaders.SOLID, mesh.buildOrThrow());
   }

   @Override
   public void blit(ResourceLocation texture, int x, int y, int u, int v, int w, int h, int texW, int texH, int argb) {
      this.gameRect(texture, x, y, x + w, y + h, (float)u / texW, (float)v / texH, (float)(u + w) / texW, (float)(v + h) / texH, argb);
   }

   @Override
   public void solids(float[] xyz, int[] colors, int quads) {
      BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_COLOR);

      for (int i = 0; i < quads * 4; i++) {
         mesh.addVertex(xyz[i * 3], xyz[i * 3 + 1], xyz[i * 3 + 2]).setColor(colors[i / 4]);
      }

      LegacyShaders.draw(LegacyShaders.SOLID, mesh.buildOrThrow());
   }

   @Override
   public void blits(ResourceLocation texture, float[] xyz, float[] uvs, int[] colors, int quads) {
      BufferBuilder mesh = this.mesh(xyz, uvs, colors, quads);
      RenderSystem.setShaderTexture(0, texture);
      LegacyShaders.draw(LegacyShaders.TEXTURED, mesh.buildOrThrow());
      unbind(0);
   }

   @Override
   public void rects(MapTexture texture, Sampling sampling, float[] xyz, float[] uvs, int[] colors, int quads) {
      BufferBuilder mesh = this.mesh(xyz, uvs, colors, quads);
      ((LegacyTexture)texture).bind(0, sampling);
      LegacyShaders.draw(LegacyShaders.TEXTURED, mesh.buildOrThrow());
      unbind(0);
   }

   private BufferBuilder mesh(float[] xyz, float[] uvs, int[] colors, int quads) {
      BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_TEX_COLOR);

      for (int i = 0; i < quads * 4; i++) {
         mesh.addVertex(xyz[i * 3], xyz[i * 3 + 1], xyz[i * 3 + 2]).setUv(uvs[i * 2], uvs[i * 2 + 1]).setColor(colors[i / 4]);
      }

      return mesh;
   }

   @Override
   public void playerFace(AbstractClientPlayer player, int x, int y, int size, int argb) {
      ResourceLocation skin = player.getSkin().texture();
      this.gameRect(skin, x, y, x + size, y + size, 0.125F, 0.125F, 0.25F, 0.25F, argb);
      this.gameRect(skin, x, y, x + size, y + size, 0.625F, 0.125F, 0.75F, 0.25F, argb);
   }

   @Override
   public void fog(MapTexture mask, MapTexture noise, MapTexture detail, float[] corners, float[] uvs, int x, int y, int width, int height, int params) {
      if (LegacyShaders.fogReady()) {
         BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_TEX_COLOR);
         Matrix4f pose = this.graphics.pose().last().pose();

         for (int i = 0; i < corners.length / 2; i++) {
            mesh.addVertex(pose, corners[i * 2], corners[i * 2 + 1], 0.0F).setUv(uvs[i * 2], uvs[i * 2 + 1]).setColor(params);
         }

         ((LegacyTexture)mask).bind(0, Sampling.CLAMP_LINEAR);
         ((LegacyTexture)noise).bind(1, Sampling.REPEAT_LINEAR);
         ((LegacyTexture)detail).bind(2, Sampling.CLAMP_LINEAR);
         LegacyShaders.drawFog(mesh.buildOrThrow());
         unbind(0);
         unbind(1);
         unbind(2);
      }
   }

   boolean fogLayer(
      MapTexture target,
      int size,
      MapTexture mask,
      MapTexture noise,
      MapTexture detail,
      int width,
      int height,
      float u0,
      float v0,
      float u1,
      float v1,
      int params
   ) {
      if (LegacyShaders.fogReady() && LegacyShaders.fogLayerReady()) {
         BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_TEX_COLOR);
         mesh.addVertex(0.0F, 0.0F, 0.0F).setUv(u0, v0).setColor(params);
         mesh.addVertex(width, 0.0F, 0.0F).setUv(u1, v0).setColor(params);
         mesh.addVertex(width, height, 0.0F).setUv(u1, v1).setColor(params);
         mesh.addVertex(0.0F, height, 0.0F).setUv(u0, v1).setColor(params);
         Matrix4f ortho = new Matrix4f().setOrtho(0.0F, size, 0.0F, size, 100.0F, 30000.0F);
         RenderSystem.backupProjectionMatrix();
         RenderSystem.setProjectionMatrix(ortho, VertexSorting.ORTHOGRAPHIC_Z);
         ((LegacyTexture)mask).bind(0, Sampling.CLAMP_LINEAR);
         ((LegacyTexture)noise).bind(1, Sampling.REPEAT_LINEAR);
         ((LegacyTexture)detail).bind(2, Sampling.CLAMP_LINEAR);
         boolean scissor = GL11.glIsEnabled(3089);
         GL11.glDisable(3089);
         int framebuffer = ((GlIdTexture)target).framebuffer();
         Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
         LegacyShaders.drawFogLayer(mesh.buildOrThrow(), framebuffer, size);
         if (scissor) {
            GL11.glEnable(3089);
         }

         unbind(0);
         unbind(1);
         unbind(2);
         RenderSystem.restoreProjectionMatrix();
         return true;
      } else {
         return false;
      }
   }

   private void gameRect(ResourceLocation texture, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int argb) {
      BufferBuilder mesh = this.begin(DefaultVertexFormat.POSITION_TEX_COLOR);
      this.rect(mesh, x0, y0, x1, y1, u0, v0, u1, v1, argb);
      RenderSystem.setShaderTexture(0, texture);
      LegacyShaders.draw(LegacyShaders.TEXTURED, mesh.buildOrThrow());
      unbind(0);
   }

   private void rect(BufferBuilder mesh, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int argb) {
      Matrix4f pose = this.graphics.pose().last().pose();
      mesh.addVertex(pose, x0, y0, 0.0F).setUv(u0, v0).setColor(argb);
      mesh.addVertex(pose, x0, y1, 0.0F).setUv(u0, v1).setColor(argb);
      mesh.addVertex(pose, x1, y1, 0.0F).setUv(u1, v1).setColor(argb);
      mesh.addVertex(pose, x1, y0, 0.0F).setUv(u1, v0).setColor(argb);
   }

   private BufferBuilder begin(VertexFormat format) {
      this.graphics.flush();
      return Tesselator.getInstance().begin(Mode.QUADS, format);
   }

   private static void unbind(int slot) {
      RenderSystem.setShaderTexture(slot, 0);
   }
}
