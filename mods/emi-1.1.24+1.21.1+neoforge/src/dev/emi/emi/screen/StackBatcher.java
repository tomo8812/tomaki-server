package dev.emi.emi.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiLog;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class StackBatcher {
   private static MethodHandle sodiumSpriteHandle;
   private final StackBatcher.BatcherVertexConsumerProvider imm;
   private final MultiBufferSource unlitFacade;
   private final Map<RenderType, VertexBuffer> buffers = new LinkedHashMap<>();
   private final Set<TextureAtlasSprite> spritesToUpdate = Sets.newHashSet();
   private boolean populated = false;
   private boolean dirty = false;
   private int x;
   private int y;
   private int z;
   public static final List<RenderType> EXTRA_RENDER_LAYERS;

   public static boolean isEnabled() {
      return EmiConfig.useBatchedRenderer;
   }

   public StackBatcher() {
      Map<RenderType, ByteBufferBuilder> buffers = new HashMap<>();
      this.assign(buffers, RenderType.solid());
      this.assign(buffers, RenderType.cutout());
      this.assign(buffers, RenderType.translucent());
      this.assign(buffers, Sheets.solidBlockSheet());
      this.assign(buffers, Sheets.cutoutBlockSheet());
      this.assign(buffers, Sheets.translucentCullBlockSheet());
      this.assign(buffers, RenderType.glint());
      this.assign(buffers, RenderType.entityGlint());

      for (RenderType layer : EXTRA_RENDER_LAYERS) {
         this.assign(buffers, layer);
      }

      this.imm = new StackBatcher.BatcherVertexConsumerProvider(new ByteBufferBuilder(256), buffers);
      this.unlitFacade = new StackBatcher.UnlitFacade(this.imm);
   }

   private void assign(Map<RenderType, ByteBufferBuilder> buffers, RenderType layer) {
      buffers.put(layer, new ByteBufferBuilder(layer.bufferSize()));
   }

   public boolean isPopulated() {
      return this.populated;
   }

   public void repopulate() {
      this.dirty = true;
   }

   public void begin(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      if (this.dirty) {
         this.populated = false;
         this.dirty = false;
         this.spritesToUpdate.clear();
      }
   }

   public void render(StackBatcher.Batchable batchable, GuiGraphics draw, int x, int y, float delta) {
      if (!this.populated) {
         try {
            batchable.renderForBatch(batchable.isSideLit() ? this.imm : this.unlitFacade, draw, x - this.x, y + this.y, this.z, delta);
         } catch (Throwable t) {
            if (EmiConfig.devMode) {
               EmiLog.error("Batchable threw exception during batched rendering. See log for info", t);
            }

            batchable.setUnbatchable();
         }
      }
   }

   public void render(EmiIngredient stack, GuiGraphics draw, int x, int y, float delta) {
      this.render(stack, draw, x, y, delta, -3);
   }

   public void render(EmiIngredient stack, GuiGraphics draw, int x, int y, float delta, int flags) {
      if (stack instanceof StackBatcher.Batchable b && !b.isUnbatchable() && isEnabled() && (flags & 1) != 0) {
         if (!this.populated) {
            try {
               b.renderForBatch(b.isSideLit() ? this.imm : this.unlitFacade, draw, x - this.x, y + this.y, this.z, delta);
               if (sodiumSpriteHandle != null && !stack.isEmpty()) {
                  ItemStack is = stack.getEmiStacks().get(0).getItemStack();
                  Minecraft client = Minecraft.getInstance();
                  BakedModel model = client.getItemRenderer().getItemModelShaper().getItemModel(is);
                  if (model != null) {
                     for (BakedQuad quad : EmiPort.getQuads(model)) {
                        if (quad != null) {
                           this.spritesToUpdate.add(quad.getSprite());
                        }
                     }
                  }
               }
            } catch (Throwable t) {
               if (EmiConfig.devMode) {
                  EmiLog.error("Stack threw exception during batched rendering. See log for info", t);
               }

               b.setUnbatchable();
            }
         }

         stack.render(draw, x, y, delta, flags & -2);
      } else {
         stack.render(draw, x, y, delta, flags);
      }
   }

   public void draw() {
      if (isEnabled()) {
         if (sodiumSpriteHandle != null) {
            try {
               for (TextureAtlasSprite sprite : this.spritesToUpdate) {
                  sodiumSpriteHandle.invoke((TextureAtlasSprite)sprite);
               }
            } catch (Throwable var4) {
            }
         }

         if (!this.populated) {
            this.bake();
            this.populated = true;
         }

         RenderSystem.enableDepthTest();
         Lighting.setupFor3DItems();
         Matrix4f mat = new Matrix4f(RenderSystem.getModelViewMatrix());
         mat.mul(new Matrix4f().translation(this.x, this.y, 0.0F));

         for (Entry<RenderType, VertexBuffer> en : this.buffers.entrySet()) {
            en.getKey().setupRenderState();
            EmiPort.setShader(en.getValue(), mat);
            en.getKey().clearRenderState();
         }

         BufferUploader.reset();
      }
   }

   private void bake() {
      this.imm.drawCurrentLayer();
      this.buffers.values().forEach(VertexBuffer::close);
      this.buffers.clear();

      for (Entry<RenderType, BufferBuilder> entry : this.imm.getPendingLayerBuffers().entrySet()) {
         this.bake(entry.getKey(), entry.getValue());
      }

      this.imm.getPendingLayerBuffers().clear();
   }

   public void bake(RenderType layer, BufferBuilder bldr) {
      MeshData builtBuffer = bldr.build();
      if (builtBuffer != null) {
         VertexBuffer vb = new VertexBuffer(Usage.DYNAMIC);
         vb.bind();
         vb.upload(builtBuffer);
         this.buffers.put(layer, vb);
      }
   }

   static {
      try {
         Class<?> clazz = null;

         try {
            clazz = Class.forName("me.jellysquid.mods.sodium.client.render.texture.SpriteUtil");
         } catch (Throwable var2) {
         }

         sodiumSpriteHandle = MethodHandles.lookup().findStatic(clazz, "markSpriteActive", MethodType.methodType(void.class, TextureAtlasSprite.class));
         if (sodiumSpriteHandle != null) {
            EmiLog.info("Discovered Sodium");
         }
      } catch (Throwable var3) {
      }

      EXTRA_RENDER_LAYERS = Lists.newArrayList();
   }

   public interface Batchable {
      boolean isSideLit();

      boolean isUnbatchable();

      void setUnbatchable();

      void renderForBatch(MultiBufferSource var1, GuiGraphics var2, int var3, int var4, int var5, float var6);
   }

   private static class BatcherVertexConsumerProvider implements MultiBufferSource {
      protected final ByteBufferBuilder fallbackBuffer;
      protected final Map<RenderType, ByteBufferBuilder> layerBuffers;
      protected final Map<RenderType, BufferBuilder> pending = new HashMap<>();
      protected RenderType currentLayer = null;

      protected BatcherVertexConsumerProvider(ByteBufferBuilder fallbackBuffer, Map<RenderType, ByteBufferBuilder> layerBuffers) {
         this.fallbackBuffer = fallbackBuffer;
         this.layerBuffers = layerBuffers;
      }

      public VertexConsumer getBuffer(RenderType renderLayer) {
         BufferBuilder bufferBuilder = this.pending.get(renderLayer);
         if (bufferBuilder == null) {
            ByteBufferBuilder allocator = this.layerBuffers.get(renderLayer);
            if (allocator != null) {
               bufferBuilder = new BufferBuilder(allocator, renderLayer.mode(), renderLayer.format());
            } else {
               if (this.currentLayer != null) {
                  this.draw(this.currentLayer);
               }

               bufferBuilder = new BufferBuilder(this.fallbackBuffer, renderLayer.mode(), renderLayer.format());
               this.currentLayer = renderLayer;
            }

            this.pending.put(renderLayer, bufferBuilder);
         }

         return bufferBuilder;
      }

      private ByteBufferBuilder getBufferInternal(RenderType layer) {
         return this.layerBuffers.getOrDefault(layer, this.fallbackBuffer);
      }

      public void drawCurrentLayer() {
         if (this.currentLayer != null) {
            RenderType renderLayer = this.currentLayer;
            if (!this.layerBuffers.containsKey(renderLayer)) {
               this.draw(renderLayer);
            }

            this.currentLayer = null;
         }
      }

      public void draw(RenderType layer) {
         ByteBufferBuilder bufferAllocator = this.getBufferInternal(layer);
         boolean isSameAsCurrentLayer = Objects.equals(this.currentLayer, layer);
         if (isSameAsCurrentLayer || bufferAllocator != this.fallbackBuffer) {
            BufferBuilder builder = this.pending.remove(layer);
            if (builder != null) {
               MeshData buffer = builder.build();
               if (buffer != null) {
                  buffer.sortQuads(bufferAllocator, VertexSorting.ORTHOGRAPHIC_Z);
                  layer.draw(buffer);
               }

               if (isSameAsCurrentLayer) {
                  this.currentLayer = null;
               }
            }
         }
      }

      public Map<RenderType, BufferBuilder> getPendingLayerBuffers() {
         return this.pending;
      }
   }

   public static class ClaimedCollection {
      private Set<StackBatcher> claimed = Sets.newHashSet();
      private List<StackBatcher> unclaimed = Lists.newArrayList();

      public StackBatcher claim() {
         synchronized (this) {
            StackBatcher batcher;
            if (this.unclaimed.isEmpty()) {
               batcher = new StackBatcher();
            } else {
               batcher = this.unclaimed.remove(this.unclaimed.size() - 1);
            }

            if (batcher == null) {
               batcher = new StackBatcher();
            }

            this.claimed.add(batcher);
            return batcher;
         }
      }

      public void unclaim(StackBatcher batcher) {
         synchronized (this) {
            this.claimed.remove(batcher);
            this.unclaimed.add(batcher);
         }
      }

      public void unclaimAll() {
         synchronized (this) {
            for (StackBatcher batcher : this.claimed) {
               this.unclaimed.add(batcher);
            }

            this.claimed.clear();
         }
      }
   }

   private static class UnlitFacade implements MultiBufferSource {
      private final MultiBufferSource delegate;
      private final IdentityHashMap<VertexConsumer, VertexConsumer> cache = new IdentityHashMap<>();

      public UnlitFacade(MultiBufferSource delegate) {
         this.delegate = delegate;
      }

      public VertexConsumer getBuffer(RenderType layer) {
         return this.cache.computeIfAbsent(this.delegate.getBuffer(layer), StackBatcher.UnlitFacade.Consumer::new);
      }

      private static final class Consumer implements VertexConsumer {
         private final VertexConsumer delegate;

         private Consumer(VertexConsumer delegate) {
            this.delegate = delegate;
         }

         public VertexConsumer setNormal(float x, float y, float z) {
            this.delegate.setNormal(0.0F, -1.0F, 0.0F);
            return this;
         }

         public VertexConsumer addVertex(float x, float y, float z) {
            this.delegate.addVertex(x, y, z);
            return this;
         }

         public VertexConsumer setUv(float u, float v) {
            this.delegate.setUv(u, v);
            return this;
         }

         public VertexConsumer setUv1(int u, int v) {
            this.delegate.setUv1(u, v);
            return this;
         }

         public VertexConsumer setUv2(int u, int v) {
            this.delegate.setUv2(u, v);
            return this;
         }

         public VertexConsumer setColor(int r, int g, int b, int a) {
            this.delegate.setColor(r, g, b, a);
            return this;
         }
      }
   }
}
