package fuzs.puzzleslib.api.client.renderer.v1.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface NoDataSpecialModelRenderer extends SpecialModelRenderer<Void> {
   @Nullable
   default Void extractArgument(ItemStack stack) {
      return null;
   }

   default void render(@Nullable Void argument, PoseStack poseStack, MultiBufferSource bufferSource, int lightCoords, int overlayCoords, boolean hasFoil) {
      this.render(poseStack, bufferSource, lightCoords, overlayCoords, hasFoil);
   }

   void render(PoseStack var1, MultiBufferSource var2, int var3, int var4, boolean var5);

   // $VF: synthetic method
   // $VF: bridge method
   @Nullable
   @Override
   default Object extractArgument(ItemStack param1) {
      return this.extractArgument(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default void render(@Nullable Object param1, PoseStack nullx, MultiBufferSource nullxx, int nullxxx, int nullxxxx, boolean nullxxxxx) {
      this.render((Void)var1, nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
   }

   interface Unbaked extends SpecialModelRenderer.Unbaked<Void> {
      @Override
      MapCodec<? extends NoDataSpecialModelRenderer.Unbaked> type();
   }
}
