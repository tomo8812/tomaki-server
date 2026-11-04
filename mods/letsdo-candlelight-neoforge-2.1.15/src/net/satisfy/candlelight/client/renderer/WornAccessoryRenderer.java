package net.satisfy.candlelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.foundation.client.armor.ArmorModels;

public final class WornAccessoryRenderer {
   private WornAccessoryRenderer() {
   }

   public static boolean canRender(ItemStack stack) {
      return stack.is((Item)ObjectRegistry.FLOWER_CROWN.get()) || stack.is((Item)ObjectRegistry.NECKTIE.get());
   }

   public static void render(ItemStack stack, HumanoidModel<?> parent, PoseStack poseStack, MultiBufferSource buffers, int light) {
      ArmorModels.render(poseStack, buffers, stack, EquipmentSlot.HEAD, light, parent);
   }
}
