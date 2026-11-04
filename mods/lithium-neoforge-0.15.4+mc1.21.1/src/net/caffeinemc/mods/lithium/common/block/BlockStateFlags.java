package net.caffeinemc.mods.lithium.common.block;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap.Entry;
import java.util.ArrayList;
import net.caffeinemc.mods.lithium.common.ai.pathing.BlockStatePathingCache;
import net.caffeinemc.mods.lithium.common.ai.pathing.PathNodeCache;
import net.caffeinemc.mods.lithium.common.entity.FluidCachingEntity;
import net.caffeinemc.mods.lithium.common.reflection.ReflectionUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.pathfinder.PathType;

public class BlockStateFlags {
   public static final boolean ENABLED = BlockCountingSection.class.isAssignableFrom(LevelChunkSection.class);
   public static final int NUM_LISTENING_FLAGS;
   public static final ListeningBlockStatePredicate[] LISTENING_FLAGS;
   public static final int LISTENING_MASK_OR;
   public static final ListeningBlockStatePredicate ANY;
   public static final int NUM_TRACKED_FLAGS;
   public static final TrackedBlockStatePredicate[] TRACKED_FLAGS;
   public static final TrackedBlockStatePredicate OVERSIZED_SHAPE;
   public static final TrackedBlockStatePredicate PATH_NOT_OPEN;
   public static final TrackedBlockStatePredicate WATER;
   public static final TrackedBlockStatePredicate LAVA;
   public static final TrackedBlockStatePredicate[] FLAGS;
   public static final TrackedBlockStatePredicate ENTITY_TOUCHABLE;

   static {
      Reference2BooleanArrayMap<ListeningBlockStatePredicate> listeningFlags = new Reference2BooleanArrayMap();
      ANY = new ListeningBlockStatePredicate(listeningFlags.size()) {
         public boolean test(BlockState operand) {
            return true;
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public boolean test(Object param1) {
            return this.test((BlockState)var1);
         }
      };
      listeningFlags.put(ANY, false);
      NUM_LISTENING_FLAGS = listeningFlags.size();
      int listenMaskOR = 0;
      int iteration = 0;
      ObjectIterator countingFlags = listeningFlags.reference2BooleanEntrySet().iterator();

      while (countingFlags.hasNext()) {
         Entry<ListeningBlockStatePredicate> entry = (Entry<ListeningBlockStatePredicate>)countingFlags.next();
         boolean listenOnlyXOR = entry.getBooleanValue();
         listenMaskOR |= listenOnlyXOR ? 0 : 1 << iteration;
      }

      LISTENING_MASK_OR = listenMaskOR;
      LISTENING_FLAGS = (ListeningBlockStatePredicate[])listeningFlags.keySet().toArray(new ListeningBlockStatePredicate[NUM_LISTENING_FLAGS]);
      ArrayList<TrackedBlockStatePredicate> countingFlagsx = new ArrayList<>(listeningFlags.keySet());
      OVERSIZED_SHAPE = new TrackedBlockStatePredicate(countingFlagsx.size()) {
         public boolean test(BlockState operand) {
            return operand.hasLargeCollisionShape();
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public boolean test(Object param1) {
            return this.test((BlockState)var1);
         }
      };
      countingFlagsx.add(OVERSIZED_SHAPE);
      if (FluidCachingEntity.class.isAssignableFrom(Entity.class)) {
         WATER = new TrackedBlockStatePredicate(countingFlagsx.size()) {
            public boolean test(BlockState operand) {
               return operand.getFluidState().getType().is(FluidTags.WATER);
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public boolean test(Object param1) {
               return this.test((BlockState)var1);
            }
         };
         countingFlagsx.add(WATER);
         LAVA = new TrackedBlockStatePredicate(countingFlagsx.size()) {
            public boolean test(BlockState operand) {
               return operand.getFluidState().getType().is(FluidTags.LAVA);
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public boolean test(Object param1) {
               return this.test((BlockState)var1);
            }
         };
         countingFlagsx.add(LAVA);
      } else {
         WATER = null;
         LAVA = null;
      }

      if (BlockStatePathingCache.class.isAssignableFrom(BlockStateBase.class)) {
         PATH_NOT_OPEN = new TrackedBlockStatePredicate(countingFlagsx.size()) {
            public boolean test(BlockState operand) {
               return PathNodeCache.getNeighborPathNodeType(operand) != PathType.OPEN;
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public boolean test(Object param1) {
               return this.test((BlockState)var1);
            }
         };
         countingFlagsx.add(PATH_NOT_OPEN);
      } else {
         PATH_NOT_OPEN = null;
      }

      NUM_TRACKED_FLAGS = countingFlagsx.size();
      TRACKED_FLAGS = countingFlagsx.toArray(new TrackedBlockStatePredicate[NUM_TRACKED_FLAGS]);
      ArrayList<TrackedBlockStatePredicate> flags = new ArrayList<>(countingFlagsx);
      ENTITY_TOUCHABLE = new TrackedBlockStatePredicate(countingFlagsx.size()) {
         public boolean test(BlockState operand) {
            return ReflectionUtil.isBlockStateEntityTouchable(operand) || operand.is(Blocks.LAVA) || operand.is(BlockTags.FIRE);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public boolean test(Object param1) {
            return this.test((BlockState)var1);
         }
      };
      flags.add(ENTITY_TOUCHABLE);
      FLAGS = flags.toArray(new TrackedBlockStatePredicate[0]);
   }
}
