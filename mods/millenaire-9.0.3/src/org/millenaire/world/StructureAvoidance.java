package org.millenaire.world;

import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.slf4j.Logger;

public final class StructureAvoidance {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final int SAFETY_MARGIN = 3;
   private static final List<ResourceKey<Structure>> AVOIDED_STRUCTURE_KEYS = List.of(
      BuiltinStructures.PILLAGER_OUTPOST,
      BuiltinStructures.DESERT_PYRAMID,
      BuiltinStructures.JUNGLE_TEMPLE,
      BuiltinStructures.SWAMP_HUT,
      BuiltinStructures.WOODLAND_MANSION,
      BuiltinStructures.IGLOO
   );

   private StructureAvoidance() {
   }

   public static boolean hasConflict(ServerLevel level, BlockPos center, int villageRadius) {
      int scanRadius = villageRadius + 3;
      int minX = center.getX() - scanRadius;
      int maxX = center.getX() + scanRadius;
      int minZ = center.getZ() - scanRadius;
      int maxZ = center.getZ() + scanRadius;
      Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
      Set<Structure> avoided = Collections.newSetFromMap(new IdentityHashMap<>());
      registry.getTag(StructureTags.VILLAGE).ifPresent(tag -> tag.forEach(h -> avoided.add((Structure)h.value())));

      for (ResourceKey<Structure> key : AVOIDED_STRUCTURE_KEYS) {
         Structure s = (Structure)registry.get(key);
         if (s != null) {
            avoided.add(s);
         }
      }

      StructureManager sm = level.structureManager();

      for (int cx = minX >> 4; cx <= maxX >> 4; cx++) {
         for (int cz = minZ >> 4; cz <= maxZ >> 4; cz++) {
            for (StructureStart start : sm.startsForStructure(new ChunkPos(cx, cz), avoided::contains)) {
               if (start.isValid() && anyPieceOverlaps(start, minX, minZ, maxX, maxZ)) {
                  LOGGER.debug(
                     "[Millenaire] Spawn rejected at {} : structure '{}' overlaps the area", center.toShortString(), registry.getKey(start.getStructure())
                  );
                  return true;
               }
            }
         }
      }

      return false;
   }

   static boolean anyPieceOverlaps(StructureStart start, int minX, int minZ, int maxX, int maxZ) {
      for (StructurePiece piece : start.getPieces()) {
         if (overlapsXZ(piece.getBoundingBox(), minX, minZ, maxX, maxZ)) {
            return true;
         }
      }

      return false;
   }

   static boolean overlapsXZ(BoundingBox box, int minX, int minZ, int maxX, int maxZ) {
      return box.intersects(minX, minZ, maxX, maxZ);
   }
}
