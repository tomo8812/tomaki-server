package de.maxhenkel.gravestone.corelib.client.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.maxhenkel.gravestone.corelib.client.RenderUtils;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class OBJModel {
   private ResourceLocation model;
   private OBJModel.OBJModelData data;

   public OBJModel(ResourceLocation model) {
      this.model = model;
   }

   @OnlyIn(Dist.CLIENT)
   private void load() {
      if (this.data == null) {
         this.data = OBJLoader.load(this.model);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void render(ResourceLocation texture, PoseStack matrixStack, MultiBufferSource buffer, int light) {
      this.load();
      matrixStack.pushPose();
      VertexConsumer builder = buffer.getBuffer(OBJRenderUtils.ENTITY_CUTOUT_TRIANGLES.apply(texture));

      for (int i = 0; i < this.data.faces.size(); i++) {
         int[][] face = this.data.faces.get(i);
         RenderUtils.vertex(
            builder,
            matrixStack,
            this.data.positions.get(face[0][0]),
            this.data.texCoords.get(face[0][1]),
            this.data.normals.get(face[0][2]),
            light,
            OverlayTexture.NO_OVERLAY
         );
         RenderUtils.vertex(
            builder,
            matrixStack,
            this.data.positions.get(face[1][0]),
            this.data.texCoords.get(face[1][1]),
            this.data.normals.get(face[1][2]),
            light,
            OverlayTexture.NO_OVERLAY
         );
         RenderUtils.vertex(
            builder,
            matrixStack,
            this.data.positions.get(face[2][0]),
            this.data.texCoords.get(face[2][1]),
            this.data.normals.get(face[2][2]),
            light,
            OverlayTexture.NO_OVERLAY
         );
      }

      matrixStack.popPose();
   }

   static class OBJModelData {
      private List<Vector3f> positions;
      private List<Vec2> texCoords;
      private List<Vector3f> normals;
      private List<int[][]> faces;

      public OBJModelData(List<Vector3f> positions, List<Vec2> texCoords, List<Vector3f> normals, List<int[][]> faces) {
         this.positions = positions;
         this.texCoords = texCoords;
         this.normals = normals;
         this.faces = faces;
      }

      public List<Vector3f> getPositions() {
         return this.positions;
      }

      public List<Vec2> getTexCoords() {
         return this.texCoords;
      }

      public List<Vector3f> getNormals() {
         return this.normals;
      }

      public List<int[][]> getFaces() {
         return this.faces;
      }
   }
}
