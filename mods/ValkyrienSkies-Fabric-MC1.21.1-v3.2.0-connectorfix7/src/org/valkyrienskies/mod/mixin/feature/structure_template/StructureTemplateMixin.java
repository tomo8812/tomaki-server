package org.valkyrienskies.mod.mixin.feature.structure_template;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3499;
import net.minecraft.class_3499.class_3501;
import net.minecraft.class_3499.class_3502;
import net.minecraft.class_3499.class_5162;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.assembly.ICopyableBlock;
import org.valkyrienskies.mod.util.StructureTemplateFillFromVoxelSet;

@Mixin(class_3499.class)
public abstract class StructureTemplateMixin implements StructureTemplateFillFromVoxelSet {
   @Shadow
   private class_2382 field_15587;
   @Final
   @Shadow
   private List<class_5162> field_15586;
   @Final
   @Shadow
   private List<class_3502> field_15589;

   @Shadow
   private static List<class_3501> method_28055(List<class_3501> basicBlocks, List<class_3501> blocksWithEntities, List<class_3501> specialBlocks) {
      return null;
   }

   @Shadow
   private static void method_28054(class_3501 blockInfo, List<class_3501> basicBlocks, List<class_3501> blocksWithEntities, List<class_3501> specialBlocks) {
   }

   @Unique
   @Override
   public void vs$fillFromVoxelSet(
      @NotNull class_1937 level,
      @NotNull Iterable<class_2338> voxels,
      @NotNull List<ServerShip> shipsBeingCopied,
      @NotNull Map<Long, Vector3d> centerPositions,
      @NotNull class_2338 min,
      @NotNull class_2338 max
   ) {
      List<class_3501> basicBlocks = Lists.newArrayList();
      List<class_3501> blocksWithEntities = Lists.newArrayList();
      List<class_3501> specialBlocks = Lists.newArrayList();

      for (class_2338 currentWorldPos : voxels) {
         class_2338 relativePos = currentWorldPos.method_10059(min);
         class_2680 blockState = level.method_8320(currentWorldPos);
         class_2586 blockEntity = level.method_8321(currentWorldPos);
         class_2248 block = blockState.method_26204();
         class_2487 customTag = null;
         if (block instanceof ICopyableBlock) {
            customTag = ((ICopyableBlock)block).onCopy((class_3218)level, currentWorldPos, blockState, blockEntity, shipsBeingCopied, centerPositions);
         }

         class_3501 blockInfo;
         if (customTag != null) {
            blockInfo = new class_3501(relativePos, blockState, customTag);
         } else if (blockEntity != null) {
            blockInfo = new class_3501(relativePos, blockState, blockEntity.method_38243(level.method_30349()));
         } else {
            blockInfo = new class_3501(relativePos, blockState, null);
         }

         method_28054(blockInfo, basicBlocks, blocksWithEntities, specialBlocks);
      }

      this.field_15587 = new class_2382(
         max.method_10263() - min.method_10263() + 1, max.method_10264() - min.method_10264() + 1, max.method_10260() - min.method_10260() + 1
      );
      List<class_3501> finalBlockList = method_28055(basicBlocks, blocksWithEntities, specialBlocks);
      this.field_15589.clear();
      this.field_15586.clear();
      this.field_15586.add(PaletteInvoker.invokeInit(finalBlockList));
   }
}
