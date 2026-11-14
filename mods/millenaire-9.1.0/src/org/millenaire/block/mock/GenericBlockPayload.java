package org.millenaire.block.mock;

import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public final class GenericBlockPayload {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<String, Optional<BlockState>> CACHE = new ConcurrentHashMap<>();
   private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

   private GenericBlockPayload() {
   }

   public static String blockIdOf(String payload) {
      int bracket = payload.indexOf(91);
      return bracket < 0 ? payload : payload.substring(0, bracket);
   }

   public static String serialize(BlockState state) {
      return BlockStateParser.serialize(state);
   }

   @Nullable
   public static BlockState resolve(@Nullable String payload, boolean fullCubeOnly) {
      if (payload != null && !payload.isEmpty()) {
         BlockState state = CACHE.computeIfAbsent(payload, p -> Optional.ofNullable(parse(p))).orElse(null);
         if (state == null) {
            return null;
         } else if (!isAccepted(state, fullCubeOnly)) {
            warnOnce(
               payload + "|refused",
               "Generic block payload '{}' is not an accepted block (air, mock, block entity, two-part block{}) — nothing placed",
               payload,
               fullCubeOnly ? " or not a full cube" : ""
            );
            return null;
         } else {
            return state;
         }
      } else {
         return null;
      }
   }

   public static boolean isAccepted(BlockState state, boolean fullCubeOnly) {
      Block block = state.getBlock();
      if (state.isAir() || block instanceof MockBlock || block instanceof EntityBlock) {
         return false;
      } else {
         return !(block instanceof DoorBlock)
               && !(block instanceof BedBlock)
               && !(block instanceof DoublePlantBlock)
               && !state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
               && !state.hasProperty(BlockStateProperties.BED_PART)
            ? !fullCubeOnly || state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)
            : false;
      }
   }

   @Nullable
   private static BlockState parse(String payload) {
      String id = blockIdOf(payload);
      ResourceLocation rl = ResourceLocation.tryParse(id);
      Block block = rl == null ? null : (Block)BuiltInRegistries.BLOCK.getOptional(rl).orElse(null);
      if (block == null) {
         warnOnce(payload, "Generic block payload '{}': unknown block (missing mod?) — nothing placed", payload);
         return null;
      }

      BlockState state = block.defaultBlockState();
      int bracket = payload.indexOf(91);
      if (bracket < 0) {
         return state;
      }

      int end = payload.endsWith("]") ? payload.length() - 1 : payload.length();

      for (String pair : payload.substring(bracket + 1, end).split(",")) {
         if (!pair.isBlank()) {
            int eq = pair.indexOf(61);
            String key = eq < 0 ? pair.trim() : pair.substring(0, eq).trim();
            String value = eq < 0 ? "" : pair.substring(eq + 1).trim();
            Property<?> property = block.getStateDefinition().getProperty(key);
            BlockState next = property == null ? null : withValue(state, property, value);
            if (next == null) {
               warnOnce(payload + "|" + pair, "Generic block payload '{}': ignoring '{}' (unknown property or value), default kept", payload, pair);
            } else {
               state = next;
            }
         }
      }

      return state;
   }

   @Nullable
   private static <T extends Comparable<T>> BlockState withValue(BlockState state, Property<T> property, String value) {
      return property.getValue(value).map(v -> (BlockState)state.setValue(property, v)).orElse(null);
   }

   private static void warnOnce(String key, String message, Object... args) {
      if (WARNED.add(key)) {
         LOGGER.warn(message, args);
      }
   }
}
