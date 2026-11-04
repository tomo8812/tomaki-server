package net.satisfy.candlelight.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.candlelight.core.block.SideTableBlock;
import net.satisfy.candlelight.core.block.entity.SideTableBlockEntity;

public class SideTableRenderer implements BlockEntityRenderer<SideTableBlockEntity> {
   private static final float TABLE_TOP = 0.625F;
   private static final float FLAT_STEP = 0.035F;
   private static final float BLOCK_STEP = 0.25F;
   private static final float BOOK_HEIGHT = 0.1875F;

   public SideTableRenderer(Context context) {
   }

   public void render(SideTableBlockEntity table, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
      ItemStack items = table.getItems();
      if (!items.isEmpty()) {
         Direction facing = (Direction)table.getBlockState().getValue(SideTableBlock.FACING);
         if (items.is(ItemTags.BOOKSHELF_BOOKS)) {
            renderBooks(table, items, facing, poseStack, buffer, light, overlay);
         } else {
            boolean block = Minecraft.getInstance().getItemRenderer().getModel(items, table.getLevel(), null, 0).isGui3d();
            ItemStack single = items.copyWithCount(1);
            int count = Math.min(items.getCount(), 4);

            for (int i = 0; i < count; i++) {
               poseStack.pushPose();
               poseStack.translate(0.5, 0.625, 0.5);
               poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() + i * 7.0F));
               if (block) {
                  poseStack.translate(0.0, 0.125 + i * 0.25F, 0.0);
                  poseStack.scale(0.5F, 0.5F, 0.5F);
               } else {
                  poseStack.translate(0.0, 0.02 + i * 0.035F, 0.0);
                  poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                  poseStack.scale(0.5F, 0.5F, 0.5F);
               }

               Minecraft.getInstance()
                  .getItemRenderer()
                  .renderStatic(
                     single,
                     ItemDisplayContext.FIXED,
                     light,
                     OverlayTexture.NO_OVERLAY,
                     poseStack,
                     buffer,
                     table.getLevel(),
                     (int)table.getBlockPos().asLong() + i
                  );
               poseStack.popPose();
            }
         }
      }
   }

   private static void renderBooks(
      SideTableBlockEntity table, ItemStack books, Direction facing, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay
   ) {
      int count = Math.min(books.getCount(), 2);
      long seed = table.getBlockPos().asLong();
      VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(buffer, Sheets.cutoutBlockSheet(), true, books.hasFoil());

      for (int i = 0; i < count; i++) {
         RandomSource random = RandomSource.create(seed * 31L + i);
         BakedModel model = SideTableBookModels.get(random.nextInt(SideTableBookModels.IDS.size()));
         if (model == null) {
            return;
         }

         poseStack.pushPose();
         poseStack.translate(0.5, i * 0.1875F, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() + (random.nextFloat() - 0.5F) * 30.0F));
         poseStack.translate(-0.5, 0.0, -0.5);
         Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), consumer, null, model, 1.0F, 1.0F, 1.0F, light, overlay);
         poseStack.popPose();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(BlockEntity param1, float nullx, PoseStack nullxx, MultiBufferSource nullxxx, int nullxxxx, int nullxxxxx) {
      this.render((SideTableBlockEntity)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }
}
