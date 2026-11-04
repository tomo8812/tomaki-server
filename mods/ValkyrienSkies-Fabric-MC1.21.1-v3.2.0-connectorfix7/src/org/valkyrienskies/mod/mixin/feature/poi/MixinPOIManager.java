package org.valkyrienskies.mod.mixin.feature.poi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_4153;
import net.minecraft.class_4156;
import net.minecraft.class_4158;
import net.minecraft.class_4153.class_4155;
import org.joml.Vector4ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.common.world.POIChunkSearcher;
import org.valkyrienskies.mod.mixinducks.world.OfLevel;

@Mixin(class_4153.class)
public abstract class MixinPOIManager implements OfLevel {
   @Unique
   private class_1937 valkyrienskies$sLevel;

   @Shadow
   public abstract Stream<class_4156> method_19123(Predicate<class_4158> var1, class_1923 var2, class_4155 var3);

   @Overwrite
   public Stream<class_4156> method_22383(Predicate<class_4158> predicate, class_2338 blockPos, int i, class_4155 occupancy) {
      int j = Math.floorDiv(i, 16) + 1;
      class_238 aABB = new class_238(blockPos).method_1014(i + 1.0);
      Stream<class_1923> chunkRange = class_1923.method_19280(new class_1923(blockPos), j);
      if (this.valkyrienskies$sLevel instanceof class_3218 sLevel) {
         for (LoadedServerShip ship : VSGameUtilsKt.getShipObjectWorld(sLevel)
            .getLoadedShips()
            .getIntersecting(VectorConversionsMCKt.toJOML(aABB), VSGameUtilsKt.getDimensionId(sLevel))) {
            Vector4ic chunkRangeBounds = POIChunkSearcher.INSTANCE.shipChunkBounds(ship.getActiveChunksSet());
            if (chunkRangeBounds != null) {
               chunkRange = Stream.concat(
                  chunkRange,
                  class_1923.method_19281(
                     new class_1923(chunkRangeBounds.x(), chunkRangeBounds.y()), new class_1923(chunkRangeBounds.z(), chunkRangeBounds.w())
                  )
               );
            }
         }
      }

      return chunkRange.<class_4156>flatMap(chunkPos -> this.method_19123(predicate, chunkPos, occupancy))
         .filter(
            poiRecord -> {
               class_2338 blockPos2 = poiRecord.method_19141();
               class_243 vecPos = VSGameUtilsKt.toWorldCoordinates(
                  this.valkyrienskies$sLevel, new class_243(blockPos2.method_10263(), blockPos2.method_10264(), blockPos2.method_10260())
               );
               return Math.abs(vecPos.method_10216() - blockPos.method_10263()) <= i && Math.abs(vecPos.method_10215() - blockPos.method_10260()) <= i;
            }
         );
   }

   @WrapOperation(
      method = "getInRange",
      at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;")
   )
   private Stream<class_4156> onGetInRange(
      Stream<class_4156> instance,
      Predicate<class_4158> predicate,
      Operation<Stream<class_4156>> original,
      @Local(argsOnly = true) class_2338 arg,
      @Local(argsOnly = true) int i
   ) {
      int k = i * i;
      return instance.filter(
         poiRecord -> POIChunkSearcher.INSTANCE.getWorldPos(poiRecord, this.valkyrienskies$sLevel).method_1025(class_243.method_24954(arg)) <= k
      );
   }

   @Override
   public class_1937 getLevel() {
      return this.valkyrienskies$sLevel;
   }

   @Override
   public void setLevel(class_1937 level) {
      this.valkyrienskies$sLevel = level;
   }
}
