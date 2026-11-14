package dev.diorama.render;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import dev.diorama.Diorama;
import dev.diorama.compat.Cobblemon;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

final class CobblemonPortraits {
   private static final int DETAIL = 4;
   private static final int BOX = 21;
   private static final int PER_FRAME = 2;
   private static final int REFRESH_TICKS = 40;
   private static final Map<Entity, CobblemonPortraits.Seen> SEEN = new MapMaker().weakKeys().makeMap();
   private static final Map<Cobblemon.Subject, Optional<XaeroRadarIcons.Sprite>> PORTRAITS = new HashMap<>();
   private static final ArrayDeque<Cobblemon.Subject> WAITING = new ArrayDeque<>();
   private static boolean scheduled;
   private static boolean warned;

   private CobblemonPortraits() {
   }

   static XaeroRadarIcons.@Nullable Sprite of(Entity entity) {
      Cobblemon.Subject subject = subject(entity);
      if (subject == null) {
         return null;
      }

      Optional<XaeroRadarIcons.Sprite> portrait = PORTRAITS.get(subject);
      if (portrait != null) {
         return portrait.orElse(null);
      }

      if (!WAITING.contains(subject)) {
         WAITING.add(subject);
      }

      if (!scheduled) {
         scheduled = true;
         Minecraft mc = Minecraft.getInstance();
         mc.tell(CobblemonPortraits::drawWaiting);
      }

      return null;
   }

   static boolean shiny(Entity entity) {
      Cobblemon.Subject subject = subject(entity);
      return subject != null && subject.shiny();
   }

   private static Cobblemon.@Nullable Subject subject(Entity entity) {
      if (!Cobblemon.has(entity)) {
         return null;
      }

      CobblemonPortraits.Seen seen = SEEN.get(entity);
      int age = entity.tickCount;
      if (seen == null || age - seen.age() >= 40 || age < seen.age()) {
         seen = new CobblemonPortraits.Seen(Cobblemon.subject(entity), age);
         SEEN.put(entity, seen);
      }

      return seen.subject();
   }

   static void clear() {
      for (Optional<XaeroRadarIcons.Sprite> portrait : PORTRAITS.values()) {
         portrait.ifPresent(XaeroRadarIcons.Sprite::close);
      }

      PORTRAITS.clear();
      WAITING.clear();
   }

   private static void drawWaiting() {
      scheduled = false;

      for (int i = 0; i < 2 && !WAITING.isEmpty(); i++) {
         Cobblemon.Subject subject = WAITING.poll();
         XaeroRadarIcons.Sprite sprite = null;

         try {
            sprite = draw(subject);
         } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            if (!warned) {
               warned = true;
               Diorama.LOGGER.warn("Couldn't draw Cobblemon's portrait of {}; it will be a dot on the radar", subject.id(), e);
            }
         }

         PORTRAITS.put(subject, Optional.ofNullable(sprite));
      }
   }

   private static ResourceLocation name(Cobblemon.Subject subject) {
      StringBuilder path = new StringBuilder(subject.id().getPath());
      char separator = '/';

      for (String aspect : subject.aspects()) {
         path.append(separator).append(aspect.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_"));
         separator = '.';
      }

      ResourceLocation name = ResourceLocation.tryBuild(subject.id().getNamespace(), path.toString());
      return name == null ? subject.id() : name;
   }

   private static XaeroRadarIcons.@Nullable Sprite draw(Cobblemon.Subject subject) throws ReflectiveOperationException {
      Minecraft mc = Minecraft.getInstance();
      int size = 84;
      TextureTarget target = new TextureTarget(size, size, true, Minecraft.ON_OSX);
      Matrix4f projection = RenderSystem.getProjectionMatrix();
      VertexSorting sorting = RenderSystem.getVertexSorting();
      float fogStart = RenderSystem.getShaderFogStart();

      XaeroRadarIcons.Sprite var9;
      try {
         target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         target.clear(Minecraft.ON_OSX);
         target.bindWrite(true);
         RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, 21.0F, 21.0F, 0.0F, -1000.0F, 1000.0F), VertexSorting.ORTHOGRAPHIC_Z);
         RenderSystem.setShaderFogStart(Float.MAX_VALUE);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         pushModelView();

         boolean drawn;
         try {
            drawn = Cobblemon.drawPortrait(subject, new PoseStack());
         } finally {
            mc.renderBuffers().bufferSource().endBatch();
            popModelView();
         }

         if (!drawn) {
            return null;
         }

         NativeImage image = new NativeImage(size, size, false);

         try {
            RenderSystem.bindTexture(target.getColorTextureId());
            image.downloadTexture(0, false);
            image.flipY();
            var9 = XaeroRadarIcons.Sprite.cropped(name(subject), image, true, false);
         } catch (Throwable var21) {
            try {
               image.close();
            } catch (Throwable var20) {
               var21.addSuppressed(var20);
            }

            throw var21;
         }

         image.close();
      } finally {
         Lighting.setupFor3DItems();
         RenderSystem.setShaderFogStart(fogStart);
         RenderSystem.setProjectionMatrix(projection, sorting);
         target.destroyBuffers();
         mc.getMainRenderTarget().bindWrite(true);
      }

      return var9;
   }

   private static void pushModelView() {
      RenderSystem.getModelViewStack().pushMatrix().identity();
      RenderSystem.applyModelViewMatrix();
   }

   private static void popModelView() {
      RenderSystem.getModelViewStack().popMatrix();
      RenderSystem.applyModelViewMatrix();
   }

   private record Seen(Cobblemon.@Nullable Subject subject, int age) {
   }
}
