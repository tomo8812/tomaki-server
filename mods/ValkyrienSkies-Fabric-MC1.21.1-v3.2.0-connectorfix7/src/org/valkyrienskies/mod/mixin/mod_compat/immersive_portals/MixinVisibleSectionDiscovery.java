package org.valkyrienskies.mod.mixin.mod_compat.immersive_portals;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_846.class_851;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.mixinducks.client.render.LevelRendererVanillaDuck;
import qouteall.imm_ptl.core.render.ImmPtlViewArea;
import qouteall.imm_ptl.core.render.VisibleSectionDiscovery;

@Mixin(VisibleSectionDiscovery.class)
public class MixinVisibleSectionDiscovery {
   @Inject(method = "discoverVisibleSections", at = @At("RETURN"))
   private static void onDiscoverVisibleSections(
      class_638 world, ImmPtlViewArea builtChunks_, class_4184 camera, class_4604 frustum, ObjectArrayList<class_851> resultHolder_, CallbackInfo ci
   ) {
      if (class_310.method_1551().field_1769 instanceof LevelRendererVanillaDuck renderer) {
         renderer.vs$addShipVisibleChunks(frustum);
      }
   }
}
