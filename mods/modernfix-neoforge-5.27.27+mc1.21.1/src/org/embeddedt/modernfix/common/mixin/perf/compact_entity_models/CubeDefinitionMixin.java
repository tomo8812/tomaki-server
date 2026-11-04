package org.embeddedt.modernfix.common.mixin.perf.compact_entity_models;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CubeDefinition.class)
@ClientOnlyMixin
public class CubeDefinitionMixin {
   @Unique
   private static final ConcurrentHashMap<List<Object>, Cube> MFIX_CUBE_CACHE = new ConcurrentHashMap<>();

   @WrapOperation(method = "bake", at = @At(value = "NEW", target = "(IIFFFFFFFFFZFFLjava/util/Set;)Lnet/minecraft/client/model/geom/ModelPart$Cube;"))
   private Cube modernfix$deduplicateCube(
      int texCoordU,
      int texCoordV,
      float originX,
      float originY,
      float originZ,
      float dimensionX,
      float dimensionY,
      float dimensionZ,
      float gtowX,
      float growY,
      float growZ,
      boolean mirror,
      float texScaleU,
      float texScaleV,
      Set visibleFaces,
      Operation<Cube> original
   ) {
      List<Object> cacheKey = List.of(
         texCoordU, texCoordV, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, gtowX, growY, growZ, mirror, texScaleU, texScaleV, visibleFaces
      );
      Cube cube = MFIX_CUBE_CACHE.get(cacheKey);
      if (cube == null) {
         cube = (Cube)original.call(cacheKey.toArray());
         MFIX_CUBE_CACHE.put(cacheKey, cube);
      }

      return cube;
   }
}
