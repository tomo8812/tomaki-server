package org.embeddedt.modernfix.common.mixin.perf.attribute_supplier_dedup;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttributeSupplier.class)
public class AttributeSupplierMixin {
   @Shadow
   @Final
   @Mutable
   private Map<Holder<Attribute>, AttributeInstance> instances;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void useCompactJavaMap(Map<Holder<Attribute>, AttributeInstance> instances, CallbackInfo ci) {
      this.instances = new Object2ObjectOpenHashMap(this.instances);
   }
}
