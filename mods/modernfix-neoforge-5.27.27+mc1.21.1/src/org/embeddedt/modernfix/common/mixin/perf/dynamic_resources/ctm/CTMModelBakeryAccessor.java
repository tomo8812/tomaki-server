package org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.ctm;

import java.util.Map;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelBakery.BakedCacheKey;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelBakery.class)
@ClientOnlyMixin
public interface CTMModelBakeryAccessor {
   @Accessor("bakedCache")
   Map<BakedCacheKey, BakedModel> mfix$getBakedCache();
}
