package dev.diorama.waypoint;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public final class WorldProjection {
   private static final Matrix4f WORLD = new Matrix4f();
   private static @Nullable Vec3 camera;
   private static float focal = 1.0F;

   private WorldProjection() {
   }

   public static void capture(Matrix4fc projection, Matrix4fc viewRotation, Vec3 cameraPos) {
      WORLD.set(projection).mul(viewRotation);
      focal = Math.abs(projection.m11());
      camera = cameraPos;
   }

   static boolean clip(double dx, double dy, double dz, Vector4f into) {
      if (camera == null) {
         return false;
      }

      WORLD.transform(into.set((float)dx, (float)dy, (float)dz, 1.0F));
      return true;
   }

   static float focal() {
      return focal;
   }

   public static float @Nullable [] project(double x, double y, double z, float width, float height) {
      Vec3 cam = camera;
      if (cam == null) {
         return null;
      }

      Vector4f clip = WORLD.transform(new Vector4f((float)(x - cam.x), (float)(y - cam.y), (float)(z - cam.z), 1.0F));
      if (clip.w <= 0.05F) {
         return null;
      }

      float ndcX = clip.x / clip.w;
      float ndcY = clip.y / clip.w;
      return new float[]{(ndcX + 1.0F) / 2.0F * width, (1.0F - ndcY) / 2.0F * height};
   }

   public static @Nullable Vec3 camera() {
      return camera;
   }
}
