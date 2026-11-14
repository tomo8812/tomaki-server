package dev.diorama.render.gpu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.NativeImage.InternalGlFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.system.MemoryUtil;

final class GlIdTexture implements LegacyTexture {
   private static final int TEXTURE_2D = 3553;
   private final int id;
   private int framebuffer = -1;

   GlIdTexture(int size, int levels) {
      this.id = TextureUtil.generateTextureId();
      TextureUtil.prepareImage(InternalGlFormat.RGBA, this.id, levels - 1, size, size);
   }

   @Override
   public void write(int level, int x, int y, int edge, int[] abgr) {
      ByteBuffer buffer = MemoryUtil.memAlloc(edge * edge * 4).order(ByteOrder.LITTLE_ENDIAN);

      try {
         buffer.asIntBuffer().put(abgr, 0, edge * edge);
         GlStateManager._bindTexture(this.id);
         GlStateManager._pixelStore(3314, 0);
         GlStateManager._pixelStore(3316, 0);
         GlStateManager._pixelStore(3315, 0);
         GlStateManager._pixelStore(3317, 4);
         GlStateManager._texSubImage2D(3553, level, x, y, edge, edge, 6408, 5121, MemoryUtil.memAddress(buffer));
      } finally {
         MemoryUtil.memFree(buffer);
      }
   }

   @Override
   public void bind(int slot, Sampling sampling) {
      GlStateManager._bindTexture(this.id);
      GlStateManager._texParameter(3553, 10241, sampling.mipmapped() ? 9987 : 9729);
      GlStateManager._texParameter(3553, 10240, sampling.crisp() ? 9728 : 9729);
      int wrap = sampling.repeats() ? 10497 : 33071;
      GlStateManager._texParameter(3553, 10242, wrap);
      GlStateManager._texParameter(3553, 10243, wrap);
      RenderSystem.setShaderTexture(slot, this.id);
   }

   int framebuffer() {
      if (this.framebuffer < 0) {
         this.framebuffer = GlStateManager.glGenFramebuffers();
         GlStateManager._glBindFramebuffer(36160, this.framebuffer);
         GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.id, 0);
      }

      return this.framebuffer;
   }

   @Override
   public void close() {
      if (this.framebuffer >= 0) {
         GlStateManager._glDeleteFramebuffers(this.framebuffer);
      }

      TextureUtil.releaseTextureId(this.id);
   }
}
