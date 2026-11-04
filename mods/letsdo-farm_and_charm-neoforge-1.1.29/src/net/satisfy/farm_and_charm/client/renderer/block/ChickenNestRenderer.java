package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.storage.StorageTypeRenderer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ChickenNestRenderer implements StorageTypeRenderer {
   private static final ChickenNestRenderer.TransformData[] TRANSFORMS = new ChickenNestRenderer.TransformData[]{
      new ChickenNestRenderer.TransformData(new Vector3f(-0.2F, 0.3F, -0.2F), 45.0F, 65.0F),
      new ChickenNestRenderer.TransformData(new Vector3f(0.2F, 0.3F, -0.2F), 20.0F, 60.0F)
   };

   public void render(StorageBlockEntity entity, PoseStack matrixStack, MultiBufferSource bufferSource, NonNullList<ItemStack> itemStacks) {
      if (!itemStacks.isEmpty()) {
         matrixStack.pushPose();
         this.setupInitialTransform(matrixStack);
         Random random = new Random(entity.getBlockPos().hashCode());

         for (int index = 0; index < itemStacks.size(); index++) {
            ItemStack stack = (ItemStack)itemStacks.get(index);
            if (!stack.isEmpty()) {
               matrixStack.pushPose();
               ChickenNestRenderer.TransformData transform = this.getTransform(index);
               this.applyItemTransform(matrixStack, transform);
               this.renderItems(matrixStack, bufferSource, entity, stack, random);
               matrixStack.popPose();
            }
         }

         matrixStack.popPose();
      }
   }

   private void setupInitialTransform(PoseStack matrixStack) {
      matrixStack.scale(0.5F, 0.5F, 0.5F);
      matrixStack.translate(0.1F, 0.0F, 0.3F);
   }

   private ChickenNestRenderer.TransformData getTransform(int index) {
      return index < TRANSFORMS.length ? TRANSFORMS[index] : new ChickenNestRenderer.TransformData(new Vector3f(0.0F, 0.3F, 0.0F), 0.0F, 0.0F);
   }

   private void applyItemTransform(PoseStack matrixStack, ChickenNestRenderer.TransformData data) {
      matrixStack.translate(data.position.x, data.position.y, data.position.z);
      Quaternionf rotation = new Quaternionf().rotateY((float)Math.toRadians(data.rotYDeg)).rotateX((float)Math.toRadians(data.rotXDeg));
      matrixStack.mulPose(rotation);
   }

   private void renderItems(PoseStack matrixStack, MultiBufferSource bufferSource, StorageBlockEntity entity, ItemStack stack, Random random) {
      for (int i = 0; i <= stack.getCount() / 8; i++) {
         matrixStack.pushPose();
         Vector3f offset = this.offsetRandomly(random);
         matrixStack.translate(offset.x, offset.y, offset.z);
         ClientUtil.renderItem(stack, matrixStack, bufferSource, entity);
         matrixStack.popPose();
      }
   }

   private Vector3f offsetRandomly(Random random) {
      return new Vector3f((random.nextFloat() - 0.5F) * 0.125F, (random.nextFloat() - 0.5F) * 0.125F, (random.nextFloat() - 0.5F) * 0.125F);
   }

   private record TransformData(Vector3f position, float rotYDeg, float rotXDeg) {
   }
}
