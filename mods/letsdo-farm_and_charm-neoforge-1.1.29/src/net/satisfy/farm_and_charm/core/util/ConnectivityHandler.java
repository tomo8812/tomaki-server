package net.satisfy.farm_and_charm.core.util;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class ConnectivityHandler {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ConnectivityHandler.class.desiredAssertionStatus();

   public static <T extends BlockEntity & IMultiBlockEntityContainer> void formMulti(T be) {
      ConnectivityHandler.SearchCache<T> cache = new ConnectivityHandler.SearchCache<>();
      Deque<T> frontier = new ArrayDeque<>();
      frontier.add(be);
      formMulti(be.getType(), be.getLevel(), cache, frontier);
   }

   private static <T extends BlockEntity & IMultiBlockEntityContainer> void formMulti(
      BlockEntityType<?> type, BlockGetter level, ConnectivityHandler.SearchCache<T> cache, Deque<T> frontier
   ) {
      PriorityQueue<Pair<Integer, T>> creationQueue = makeCreationQueue();
      Set<BlockPos> visited = new HashSet<>();
      T first = (T)frontier.peekFirst();
      assert first != null;
      Axis mainAxis = first.getMainConnectionAxis();
      int minX = mainAxis == Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      int minY = mainAxis != Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      int minZ = mainAxis == Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE;

      for (T be : frontier) {
         BlockPos pos = be.getBlockPos();
         minX = Math.min(pos.getX(), minX);
         minY = Math.min(pos.getY(), minY);
         minZ = Math.min(pos.getZ(), minZ);
      }

      if (mainAxis == Axis.Y) {
         minX -= first.getMaxWidth();
      }

      if (mainAxis != Axis.Y) {
         minY -= first.getMaxWidth();
      }

      if (mainAxis == Axis.Y) {
         minZ -= first.getMaxWidth();
      }

      while (!frontier.isEmpty()) {
         T part = (T)frontier.pollFirst();
         BlockPos partPos = part.getBlockPos();
         if (!visited.contains(partPos)) {
            visited.add(partPos);
            int amount = tryToFormNewMulti(part, cache, true);
            if (amount > 1) {
               creationQueue.add(Pair.of(amount, part));
            }

            for (Axis axis : Axis.values()) {
               Direction dir = Direction.get(AxisDirection.NEGATIVE, axis);
               BlockPos next = partPos.relative(dir);
               if (next.getX() > minX && next.getY() > minY && next.getZ() > minZ && !visited.contains(next)) {
                  T nextBe = partAt(type, level, next);
                  if (nextBe != null && !nextBe.isRemoved()) {
                     frontier.addLast(nextBe);
                  }
               }
            }
         }
      }

      visited.clear();

      while (!creationQueue.isEmpty()) {
         Pair<Integer, T> next = creationQueue.poll();
         T toCreate = (T)next.getSecond();
         if (!visited.contains(toCreate.getBlockPos())) {
            visited.add(toCreate.getBlockPos());
            tryToFormNewMulti(toCreate, cache, false);
         }
      }
   }

   private static <T extends BlockEntity & IMultiBlockEntityContainer> int tryToFormNewMulti(T be, ConnectivityHandler.SearchCache<T> cache, boolean simulate) {
      int bestWidth = 1;
      int bestAmount = -1;
      if (!be.isController()) {
         return 0;
      }

      int radius = be.getMaxWidth();

      for (int w = 1; w <= radius; w++) {
         int amount = tryToFormNewMultiOfWidth(be, w, cache, true);
         if (amount >= bestAmount) {
            bestWidth = w;
            bestAmount = amount;
         }
      }

      if (!simulate) {
         int beWidth = be.getWidth();
         if (beWidth == bestWidth && beWidth * beWidth * be.getHeight() == bestAmount) {
            return bestAmount;
         }

         splitMultiAndInvalidate(be, cache);
         tryToFormNewMultiOfWidth(be, bestWidth, cache, false);
         be.preventConnectivityUpdate();
         be.setWidth(bestWidth);
         be.setHeight(bestAmount / bestWidth / bestWidth);
         be.notifyMultiUpdated();
      }

      return bestAmount;
   }

   private static <T extends BlockEntity & IMultiBlockEntityContainer> int tryToFormNewMultiOfWidth(
      T be, int width, ConnectivityHandler.SearchCache<T> cache, boolean simulate
   ) {
      int amount = 0;
      int height = 0;
      BlockEntityType<?> type = be.getType();
      Level level = be.getLevel();
      if (level == null) {
         return 0;
      }

      BlockPos origin = be.getBlockPos();
      Axis axis = be.getMainConnectionAxis();

      label151:
      for (int yOffset = 0; yOffset < be.getMaxLength(axis, width); yOffset++) {
         for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
               BlockPos pos = switch (axis) {
                  case X -> origin.offset(yOffset, xOffset, zOffset);
                  case Y -> origin.offset(xOffset, yOffset, zOffset);
                  case Z -> origin.offset(xOffset, zOffset, yOffset);
                  default -> throw new MatchException(null, null);
               };
               Optional<T> part = cache.getOrCache(type, level, pos);
               if (part.isEmpty()) {
                  break label151;
               }

               T controller = (T)part.get();
               int otherWidth = controller.getWidth();
               if (otherWidth > width || otherWidth == width && controller.getHeight() == be.getMaxLength(axis, width)) {
                  break label151;
               }

               Axis conAxis = controller.getMainConnectionAxis();
               if (axis != conAxis) {
                  break label151;
               }

               BlockPos conPos = controller.getBlockPos();
               if (!conPos.equals(origin)
                  && (
                     axis == Axis.Y
                        ? conPos.getX() < origin.getX()
                           || conPos.getZ() < origin.getZ()
                           || conPos.getX() + otherWidth > origin.getX() + width
                           || conPos.getZ() + otherWidth > origin.getZ() + width
                        : axis == Axis.Z && conPos.getX() < origin.getX()
                           || conPos.getY() < origin.getY()
                           || axis == Axis.X && conPos.getZ() < origin.getZ()
                           || axis == Axis.Z && conPos.getX() + otherWidth > origin.getX() + width
                           || conPos.getY() + otherWidth > origin.getY() + width
                           || axis == Axis.X && conPos.getZ() + otherWidth > origin.getZ() + width
                  )) {
                  break label151;
               }
            }
         }

         amount += width * width;
         height++;
      }

      if (simulate) {
         return amount;
      }

      Object extraData = be.getExtraData();

      for (int yOffset = 0; yOffset < height; yOffset++) {
         for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
               BlockPos pos = switch (axis) {
                  case X -> origin.offset(yOffset, xOffset, zOffset);
                  case Y -> origin.offset(xOffset, yOffset, zOffset);
                  case Z -> origin.offset(xOffset, zOffset, yOffset);
                  default -> throw new MatchException(null, null);
               };
               T part = partAt(type, level, pos);
               if (part != null && part != be) {
                  extraData = be.modifyExtraData(extraData);
                  splitMultiAndInvalidate(part, cache);
                  part.setController(origin);
                  part.preventConnectivityUpdate();
                  cache.put(pos, be);
                  part.setHeight(height);
                  part.setWidth(width);
                  part.notifyMultiUpdated();
               }
            }
         }
      }

      be.setExtraData(extraData);
      be.notifyMultiUpdated();
      return amount;
   }

   public static <T extends BlockEntity & IMultiBlockEntityContainer> void splitMulti(T be) {
      splitMultiAndInvalidate(be, null);
   }

   private static <T extends BlockEntity & IMultiBlockEntityContainer> void splitMultiAndInvalidate(T be, @Nullable ConnectivityHandler.SearchCache<T> cache) {
      Level level = be.getLevel();
      if (level != null) {
         be = be.getControllerBE();
         if (be != null) {
            int height = be.getHeight();
            int width = be.getWidth();
            if (width != 1 || height != 1) {
               BlockPos origin = be.getBlockPos();
               Axis axis = be.getMainConnectionAxis();

               for (int yOffset = 0; yOffset < height; yOffset++) {
                  for (int xOffset = 0; xOffset < width; xOffset++) {
                     for (int zOffset = 0; zOffset < width; zOffset++) {
                        BlockPos pos = switch (axis) {
                           case X -> origin.offset(yOffset, xOffset, zOffset);
                           case Y -> origin.offset(xOffset, yOffset, zOffset);
                           case Z -> origin.offset(xOffset, zOffset, yOffset);
                           default -> throw new MatchException(null, null);
                        };
                        T partAt = partAt(be.getType(), level, pos);
                        if (partAt != null && partAt.getController().equals(origin)) {
                           T controllerBE = partAt.getControllerBE();
                           partAt.setExtraData(controllerBE == null ? null : controllerBE.getExtraData());
                           partAt.removeController(true);
                           if (cache != null) {
                              cache.put(pos, partAt);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static <T extends BlockEntity & IMultiBlockEntityContainer> PriorityQueue<Pair<Integer, T>> makeCreationQueue() {
      return new PriorityQueue<>((one, two) -> Integer.compare((Integer)two.getFirst(), (Integer)one.getFirst()));
   }

   @Nullable
   public static <T extends BlockEntity & IMultiBlockEntityContainer> T partAt(BlockEntityType<?> type, BlockGetter level, BlockPos pos) {
      BlockEntity be = level.getBlockEntity(pos);
      return be != null && be.getType() == type && !be.isRemoved() ? checked(be) : null;
   }

   public static <T extends BlockEntity & IMultiBlockEntityContainer> boolean isConnected(BlockGetter level, BlockPos pos, BlockPos other) {
      T one = checked(level.getBlockEntity(pos));
      T two = checked(level.getBlockEntity(other));
      return one != null && two != null ? one.getController().equals(two.getController()) : false;
   }

   @Nullable
   private static <T extends BlockEntity & IMultiBlockEntityContainer> T checked(BlockEntity be) {
      return (T)(be instanceof IMultiBlockEntityContainer ? be : null);
   }

   private static class SearchCache<T extends BlockEntity & IMultiBlockEntityContainer> {
      Map<BlockPos, Optional<T>> controllerMap = new HashMap<>();

      public SearchCache() {
      }

      void put(BlockPos pos, T target) {
         this.controllerMap.put(pos, Optional.of(target));
      }

      void putEmpty(BlockPos pos) {
         this.controllerMap.put(pos, Optional.empty());
      }

      boolean hasVisited(BlockPos pos) {
         return this.controllerMap.containsKey(pos);
      }

      Optional<T> getOrCache(BlockEntityType<?> type, BlockGetter level, BlockPos pos) {
         if (this.hasVisited(pos)) {
            return this.controllerMap.get(pos);
         } else {
            T partAt = ConnectivityHandler.partAt(type, level, pos);
            if (partAt == null) {
               this.putEmpty(pos);
               return Optional.empty();
            } else {
               T controller = ConnectivityHandler.checked(level.getBlockEntity(partAt.getController()));
               if (controller == null) {
                  this.putEmpty(pos);
                  return Optional.empty();
               } else {
                  this.put(pos, controller);
                  return Optional.of(controller);
               }
            }
         }
      }
   }
}
