package dev.diorama.render.gpu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferUploader;

final class GlState {
   private GlState() {
   }

   static void bindFramebuffer(int target, int framebuffer) {
      GlStateManager._glBindFramebuffer(target, framebuffer);
   }

   static void activeTexture(int unit) {
      GlStateManager._activeTexture(33984 + unit);
   }

   static void bindTexture(int texture) {
      GlStateManager._bindTexture(texture);
   }

   static void blend(boolean on) {
      if (on) {
         GlStateManager._enableBlend();
      } else {
         GlStateManager._disableBlend();
      }
   }

   static void blendFuncSeparate(int srcColor, int dstColor, int srcAlpha, int dstAlpha) {
      GlStateManager._blendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
   }

   static void depthTest(boolean on) {
      if (on) {
         GlStateManager._enableDepthTest();
      } else {
         GlStateManager._disableDepthTest();
      }
   }

   static void depthFunc(int func) {
      GlStateManager._depthFunc(func);
   }

   static void depthMask(boolean write) {
      GlStateManager._depthMask(write);
   }

   static void colorMaskAll() {
      GlStateManager._colorMask(true, true, true, true);
   }

   static void noCullNoScissor() {
      GlStateManager._disableCull();
      GlStateManager._disableScissorTest();
   }

   static void cull(boolean on) {
      if (on) {
         GlStateManager._enableCull();
      } else {
         GlStateManager._disableCull();
      }
   }

   static void scissor(boolean on) {
      if (on) {
         GlStateManager._enableScissorTest();
      } else {
         GlStateManager._disableScissorTest();
      }
   }

   static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      GlStateManager._colorMask(red, green, blue, alpha);
   }

   static void restoreBlend(boolean first, boolean second) {
      if (first) {
         GlStateManager._enableBlend();
      } else {
         GlStateManager._disableBlend();
      }
   }

   static void viewport(int width, int height) {
      GlStateManager._viewport(0, 0, width, height);
   }

   static void viewportAt(int x, int y, int width, int height) {
      GlStateManager._viewport(x, y, width, height);
   }

   static void useProgram(int program) {
      GlStateManager._glUseProgram(program);
   }

   static void bindVertexArray(int vao) {
      GlStateManager._glBindVertexArray(vao);
   }

   static void forgetBuffers() {
      BufferUploader.invalidate();
   }
}
