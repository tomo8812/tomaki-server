package dev.emi.emi.runtime;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import dev.emi.emi.EmiPort;
import dev.emi.emi.config.EmiConfig;
import java.io.File;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class EmiScreenshotRecorder {
   private static final String SCREENSHOTS_DIRNAME = "screenshots";

   public static void saveScreenshot(String path, int width, int height, Runnable renderer) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> saveScreenshotInner(path, width, height, renderer));
      } else {
         saveScreenshotInner(path, width, height, renderer);
      }
   }

   private static void saveScreenshotInner(String path, int width, int height, Runnable renderer) {
      Minecraft client = Minecraft.getInstance();
      int scale;
      if (EmiConfig.recipeScreenshotScale < 1) {
         scale = EmiPort.getGuiScale(client);
      } else {
         scale = EmiConfig.recipeScreenshotScale;
      }

      RenderTarget framebuffer = new TextureTarget(width * scale, height * scale, true, Minecraft.ON_OSX);
      framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      framebuffer.clear(Minecraft.ON_OSX);
      framebuffer.bindWrite(true);
      Matrix4fStack view = RenderSystem.getModelViewStack();
      view.pushMatrix();
      view.identity();
      view.translate(-1.0F, 1.0F, 0.0F);
      view.scale(2.0F / width, -2.0F / height, -0.001F);
      view.translate(0.0F, 0.0F, 10.0F);
      EmiPort.applyModelViewMatrix();
      Matrix4f backupProj = RenderSystem.getProjectionMatrix();
      RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorting.ORTHOGRAPHIC_Z);
      renderer.run();
      RenderSystem.setProjectionMatrix(backupProj, VertexSorting.ORTHOGRAPHIC_Z);
      view.popMatrix();
      EmiPort.applyModelViewMatrix();
      framebuffer.unbindWrite();
      client.getMainRenderTarget().bindWrite(true);
      saveScreenshotInner(client.gameDirectory, path, framebuffer, message -> client.execute(() -> client.gui.getChat().addMessage(message)));
   }

   private static void saveScreenshotInner(File gameDirectory, String suggestedPath, RenderTarget framebuffer, Consumer<Component> messageReceiver) {
      NativeImage nativeImage = takeScreenshot(framebuffer);
      File screenshots = new File(gameDirectory, "screenshots");
      screenshots.mkdir();
      String filename = getScreenshotFilename(screenshots, suggestedPath);
      File file = new File(screenshots, filename);
      File parent = file.getParentFile();
      parent.mkdirs();
      Util.ioPool()
         .execute(
            () -> {
               try {
                  nativeImage.writeToFile(file);
                  Component text = EmiPort.literal(
                     filename, Style.EMPTY.withUnderlined(true).withClickEvent(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath()))
                  );
                  messageReceiver.accept(EmiPort.translatable("screenshot.success", text));
               } catch (Throwable e) {
                  EmiLog.error("Failed to write screenshot", e);
                  messageReceiver.accept(EmiPort.translatable("screenshot.failure", e.getMessage()));
               } finally {
                  nativeImage.close();
               }
            }
         );
   }

   private static NativeImage takeScreenshot(RenderTarget framebuffer) {
      int i = framebuffer.width;
      int j = framebuffer.height;
      NativeImage nativeImage = new NativeImage(i, j, false);
      RenderSystem.bindTexture(framebuffer.getColorTextureId());
      nativeImage.downloadTexture(0, false);
      nativeImage.flipY();
      return nativeImage;
   }

   private static String getScreenshotFilename(File directory, String path) {
      int i = 1;

      while (new File(directory, path + (i == 1 ? "" : "_" + i) + ".png").exists()) {
         i++;
      }

      return path + (i == 1 ? "" : "_" + i) + ".png";
   }
}
