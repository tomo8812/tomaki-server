package org.valkyrienskies.mod.mixin.mod_compat.tis3d;

import li.cil.tis3d.client.renderer.block.entity.CasingBlockEntityRenderer;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Pseudo
@Mixin(CasingBlockEntityRenderer.class)
public abstract class MixinCasingTileEntityRender {
   @ModifyVariable(remap = false, method = "isBackFace", at = @At("STORE"), ordinal = 0)
   private class_243 vs$isBackFace(class_243 original, class_2338 position) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(class_310.method_1551().field_1687, position);
      return ship != null ? VectorConversionsMCKt.toMinecraft(ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(original))) : original;
   }
}
