package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.renderer.v1.RenderTypeHelper;
import java.util.Objects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public final class BlockRenderTypesContextImpl implements RenderTypesContext<Block> {
   public void registerRenderType(Block block, RenderType renderType) {
      Objects.requireNonNull(block, "block is null");
      Objects.requireNonNull(renderType, "render type is null");
      RenderTypeHelper.registerRenderType(block, renderType);
   }

   public RenderType getRenderType(Block object) {
      return ClientAbstractions.INSTANCE.getRenderType(object);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public RenderType getRenderType(Object param1) {
      return this.getRenderType((Block)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void registerRenderType(Object param1, RenderType nullx) {
      this.registerRenderType((Block)var1, nullx);
   }
}
