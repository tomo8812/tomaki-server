package de.maxhenkel.gravestone.corelib.client.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.gravestone.corelib.math.Rotation;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3d;

public class OBJModelOptions<T> {
   private ResourceLocation texture;
   private Vector3d offset;
   private Rotation rotation;
   private OBJModelOptions.RenderListener<T> onRender;

   public OBJModelOptions(ResourceLocation texture, Vector3d offset, Rotation rotation, OBJModelOptions.RenderListener<T> onRender) {
      this.texture = texture;
      this.offset = offset;
      this.rotation = rotation;
      this.onRender = onRender;
   }

   public OBJModelOptions(ResourceLocation texture, Vector3d offset, Rotation rotation) {
      this(texture, offset, rotation, null);
   }

   public OBJModelOptions(ResourceLocation texture, Vector3d offset) {
      this(texture, offset, null, null);
   }

   public OBJModelOptions(ResourceLocation texture, Vector3d offset, OBJModelOptions.RenderListener<T> onRender) {
      this(texture, offset, null, onRender);
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public void setTexture(ResourceLocation texture) {
      this.texture = texture;
   }

   public Vector3d getOffset() {
      return this.offset;
   }

   public OBJModelOptions<T> setOffset(Vector3d offset) {
      this.offset = offset;
      return this;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public OBJModelOptions<T> setRotation(Rotation rotation) {
      this.rotation = rotation;
      return this;
   }

   public OBJModelOptions.RenderListener<T> getOnRender() {
      return this.onRender;
   }

   public interface RenderListener<T> {
      void onRender(T var1, PoseStack var2, float var3);
   }
}
