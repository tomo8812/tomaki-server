package net.satisfy.farm_and_charm.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;

public class SaturationOverlayRenderer {
   public static void render(PoseStack poseStack, MultiBufferSource buffer, LivingEntity entity, int level, int foodCounter) {
      if (!entity.isBaby()) {
         EntityType<?> type = entity.getType();
         if (type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN) {
            Minecraft mc = Minecraft.getInstance();
            ItemRenderer itemRenderer = mc.getItemRenderer();
            double yOffset = entity.getBbHeight() + 0.6;
            if (entity.hasCustomName()) {
               yOffset += 0.3;
            }

            poseStack.pushPose();
            poseStack.translate(0.0, yOffset, 0.0);
            poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            poseStack.scale(0.4F, 0.4F, 0.4F);
            ItemStack icon = resolveIcon(entity);
            int[] thresholds = new int[]{5, 10, 15, 20};

            for (int i = 0; i < 3; i++) {
               poseStack.pushPose();
               poseStack.translate((i - 1) * 1.1, 0.0, 0.0);
               float alpha;
               if (level > 0) {
                  if (i < level) {
                     alpha = 0.9F;
                  } else if (i == level) {
                     float progress = (float)foodCounter / thresholds[level];
                     alpha = Mth.clamp(0.4F + 0.5F * progress, 0.4F, 0.9F);
                  } else {
                     alpha = 0.4F;
                  }
               } else {
                  float progress = getProgress(foodCounter, i, thresholds);
                  alpha = 0.4F + 0.5F * progress;
               }

               int light = Mth.floor(240.0F * alpha);
               itemRenderer.renderStatic(icon, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 1);
               poseStack.popPose();
            }

            poseStack.popPose();
         }
      }
   }

   private static float getProgress(int foodCounter, int i, int[] thresholds) {
      int start = switch (i) {
         case 1 -> thresholds[0];
         case 2 -> thresholds[0] + thresholds[1];
         default -> 0;
      };

      int end = switch (i) {
         case 0 -> thresholds[0];
         case 1 -> thresholds[0] + thresholds[1];
         case 2 -> thresholds[0] + thresholds[1] + thresholds[2];
         default -> 1;
      };
      return Mth.clamp((float)(foodCounter - start) / (end - start), 0.0F, 1.0F);
   }

   private static ItemStack resolveIcon(LivingEntity entity) {
      if (entity instanceof Cow) {
         return new ItemStack(Items.WHEAT);
      } else if (entity instanceof Pig) {
         return new ItemStack(Items.APPLE);
      } else if (entity instanceof Sheep) {
         return new ItemStack(Items.WHEAT);
      } else {
         return entity instanceof Chicken ? new ItemStack(Items.WHEAT_SEEDS) : new ItemStack((ItemLike)ObjectRegistry.HORSE_FODDER.get());
      }
   }

   public static void renderIfApplicable(PoseStack poseStack, MultiBufferSource buffer, LivingEntity entity) {
      if (entity instanceof SaturationTracker.SaturatedAnimal saturated) {
         SaturationTracker tracker = saturated.farm_and_charm$getSaturationTracker();
         render(poseStack, buffer, entity, tracker.level(), tracker.foodCounter());
      }
   }
}
