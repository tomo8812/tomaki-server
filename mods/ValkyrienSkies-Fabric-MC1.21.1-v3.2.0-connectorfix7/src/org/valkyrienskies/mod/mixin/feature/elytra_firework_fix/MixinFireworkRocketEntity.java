package org.valkyrienskies.mod.mixin.feature.elytra_firework_fix;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.class_1309;
import net.minecraft.class_1671;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1671.class)
public abstract class MixinFireworkRocketEntity {
   private static final int VS_CLIENT_LIFETIME_CAP = 60;
   private static final double VS_DESYNC_DIST_SQ = 100.0;
   private static final int VS_KILL_LIST_MAX = 1024;
   private static final Set<Integer> vs$killedFireworkIds = Collections.synchronizedSet(new LinkedHashSet<>());
   @Shadow
   private int field_7613;
   @Shadow
   @Nullable
   private class_1309 field_7616;

   @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
   private void vs$capClientFirework(CallbackInfo ci) {
      class_1671 self = (class_1671)this;
      if (self.method_37908().method_8608()) {
         int id = self.method_5628();
         if (vs$killedFireworkIds.contains(id)) {
            ci.cancel();
            if (!self.method_31481()) {
               self.method_31472();
            }
         } else if (this.field_7616 != null) {
            class_1309 attached = this.field_7616;
            double dx = self.method_23317() - attached.method_23317();
            double dy = self.method_23318() - attached.method_23318();
            double dz = self.method_23321() - attached.method_23321();
            double distSq = dx * dx + dy * dy + dz * dz;
            boolean desync = distSq > 100.0;
            boolean overLife = this.field_7613 > 60;
            if (desync || overLife) {
               vs$rememberKilled(id);
               self.method_31472();
               ci.cancel();
            }
         }
      }
   }

   private static void vs$rememberKilled(int id) {
      synchronized (vs$killedFireworkIds) {
         vs$killedFireworkIds.add(id);

         while (vs$killedFireworkIds.size() > 1024) {
            Iterator<Integer> it = vs$killedFireworkIds.iterator();
            it.next();
            it.remove();
         }
      }
   }
}
