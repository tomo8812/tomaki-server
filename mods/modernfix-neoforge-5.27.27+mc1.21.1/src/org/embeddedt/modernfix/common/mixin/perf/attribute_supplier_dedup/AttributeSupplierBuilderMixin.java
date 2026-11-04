package org.embeddedt.modernfix.common.mixin.perf.attribute_supplier_dedup;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import org.embeddedt.modernfix.entity.AttributeInstanceTemplates;
import org.embeddedt.modernfix.neoforge.init.ModernFixForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Builder.class)
public class AttributeSupplierBuilderMixin {
   @Shadow
   @Final
   private Map<Holder<Attribute>, AttributeInstance> builder;

   @Inject(method = "build", at = @At(value = "NEW", target = "(Ljava/util/Map;)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;"))
   private void deduplicateInstances(CallbackInfoReturnable<AttributeSupplier> cir) {
      if (!ModernFixForge.registryEventsFired) {
         this.builder.replaceAll((a, i) -> AttributeInstanceTemplates.intern(i));
      }
   }
}
