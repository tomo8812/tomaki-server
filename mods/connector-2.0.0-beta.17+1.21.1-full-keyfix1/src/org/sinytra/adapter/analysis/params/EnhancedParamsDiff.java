package org.sinytra.adapter.analysis.params;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.MethodHelper;
import org.sinytra.adapter.util.GeneratedVariables;
import org.slf4j.Logger;

public class EnhancedParamsDiff {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean DEBUG = Boolean.getBoolean("adapter.core.paramdiff.debug");

   public static LayeredParamsDiffSnapshot createLayered(List<Type> clean, List<Type> dirty) {
      LayeredParamsDiffSnapshot.Builder builder = LayeredParamsDiffSnapshot.builder();
      buildDiff(builder, clean, dirty);
      return builder.build();
   }

   public static LayeredParamsDiffSnapshot compareMethodParameters(MethodNode clean, MethodNode dirty) {
      if (clean.localVariables != null && dirty.localVariables != null) {
         int cleanParamCount = Type.getArgumentTypes(clean.desc).length;
         int dirtyParamCount = Type.getArgumentTypes(dirty.desc).length;
         boolean isCleanStatic = MethodHelper.isStatic(clean);
         boolean isDirtyStatic = MethodHelper.isStatic(dirty);
         List<EnhancedParamsDiff.LocalVariable> cleanParams = clean.localVariables
            .stream()
            .sorted(Comparator.comparingInt(lv -> lv.index))
            .filter(lv -> isCleanStatic || lv.index != 0)
            .limit(cleanParamCount)
            .map(EnhancedParamsDiff.LocalVariable::new)
            .toList();
         List<EnhancedParamsDiff.LocalVariable> dirtyParams = dirty.localVariables
            .stream()
            .sorted(Comparator.comparingInt(lv -> lv.index))
            .filter(lv -> isDirtyStatic || lv.index != 0)
            .limit(dirtyParamCount)
            .map(EnhancedParamsDiff.LocalVariable::new)
            .toList();
         boolean compareNames = cleanParams.stream().allMatch(t -> t.name() != null)
            && dirtyParams.stream().anyMatch(EnhancedParamsDiff.LocalVariable::isGenerated);
         LayeredParamsDiffSnapshot.Builder builder = LayeredParamsDiffSnapshot.builder();
         buildDiffWithContext(builder, createPositionedVariableList(cleanParams), createPositionedVariableList(dirtyParams), compareNames);
         return builder.build();
      } else {
         return LayeredParamsDiffSnapshot.EMPTY;
      }
   }

   private static void buildDiff(ParamsDiffSnapshotBuilder builder, List<Type> clean, List<Type> dirty) {
      buildDiffWithContext(builder, createPositionedList(clean), createPositionedList(dirty), false);
   }

   private static void buildDiffWithContext(
      ParamsDiffSnapshotBuilder builder,
      List<EnhancedParamsDiff.TypeWithContext> cleanQueue,
      List<EnhancedParamsDiff.TypeWithContext> dirtyQueue,
      boolean compareNames
   ) {
      int dirtySize = dirtyQueue.size();
      boolean sameSize = cleanQueue.size() == dirtyQueue.size();
      if (DEBUG) {
         LOGGER.info("Comparing types:\n{}", printTable(cleanQueue, dirtyQueue));
      }

      while (!cleanQueue.isEmpty()) {
         if (predictParameterMatch(builder, cleanQueue, dirtyQueue, compareNames, sameSize)) {
            cleanQueue.removeFirst();
            dirtyQueue.removeFirst();
         } else if (!tryReplacingParams(builder, cleanQueue, dirtyQueue)) {
            EnhancedParamsDiff.SwapResult swapResult = checkForSwaps(builder, cleanQueue, dirtyQueue);
            if (swapResult != null) {
               dirtyQueue.removeAll(swapResult.removeDirty());
               cleanQueue.clear();
               dirtyQueue.clear();
               break;
            }

            int dirtyTypeIndex = cleanQueue.size() == 1 ? dirtySize - 1 : lookAhead(dirtyQueue, cleanQueue.get(1));
            if (dirtyTypeIndex == -1) {
               if (cleanQueue.size() == dirtyQueue.size() && (dirtyTypeIndex = findClosestMatch(cleanQueue, dirtyQueue)) > 1) {
                  List<EnhancedParamsDiff.TypeWithContext> compareClean = extract(cleanQueue, dirtyTypeIndex);
                  List<EnhancedParamsDiff.TypeWithContext> compareDirty = extract(dirtyQueue, dirtyTypeIndex);
                  compare(builder, compareClean, compareDirty);
               } else {
                  if (cleanQueue.size() <= 1) {
                     throw new IllegalStateException("undefined behavior");
                  }

                  EnhancedParamsDiff.TypeWithContext type = cleanQueue.remove(1);
                  if (DEBUG) {
                     LOGGER.info("Removing type {}", type);
                  }

                  builder.remove(type.pos());
               }
            } else {
               boolean matchesName = !compareNames || cleanQueue.size() < 2 || dirtyQueue.size() < 2 || cleanQueue.get(1).matches(dirtyQueue.get(1));
               int offset = cleanQueue.get(0).sameType(dirtyQueue.get(0)) && matchesName ? 1 : 2;
               List<EnhancedParamsDiff.TypeWithContext> compareClean = extract(cleanQueue, matchesName ? 2 : 1);
               List<EnhancedParamsDiff.TypeWithContext> compareDirty = extract(dirtyQueue, dirtyTypeIndex + offset);
               compare(builder, compareClean, compareDirty);
            }
         }
      }

      if (!dirtyQueue.isEmpty()) {
         for (EnhancedParamsDiff.TypeWithContext type : dirtyQueue) {
            builder.insert(type.pos(), type.type());
         }
      }
   }

   private static boolean predictParameterMatch(
      ParamsDiffSnapshotBuilder builder,
      List<EnhancedParamsDiff.TypeWithContext> cleanQueue,
      List<EnhancedParamsDiff.TypeWithContext> dirtyQueue,
      boolean compareNames,
      boolean sameSize
   ) {
      if (sameSize && !cleanQueue.isEmpty() && !dirtyQueue.isEmpty()) {
         if (cleanQueue.get(0).sameType(dirtyQueue.get(0))) {
            return true;
         } else if (checkMovedParam(builder, cleanQueue, dirtyQueue, 0)) {
            return true;
         } else if (cleanQueue.size() > 2
            && dirtyQueue.size() > 2
            && cleanQueue.get(1).matches(dirtyQueue.get(0))
            && cleanQueue.get(2).matches(dirtyQueue.get(1))) {
            builder.remove(cleanQueue.getFirst().pos());
            cleanQueue.removeFirst();
            return true;
         } else {
            return false;
         }
      } else if (cleanQueue.size() > 1 && dirtyQueue.size() > 1) {
         if (cleanQueue.get(0).sameType(dirtyQueue.get(0))) {
            if (cleanQueue.get(1).matches(dirtyQueue.get(1))) {
               return true;
            }

            if (!compareNames) {
               return checkMovedParam(builder, cleanQueue, dirtyQueue, 1);
            }

            if (dirtyQueue.size() > 2 && cleanQueue.get(1).matches(dirtyQueue.get(2))) {
               builder.insert(dirtyQueue.get(1).pos(), dirtyQueue.get(1).type());
               cleanQueue.removeFirst();
               dirtyQueue.removeFirst();
               dirtyQueue.removeFirst();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean checkMovedParam(
      ParamsDiffSnapshotBuilder builder, List<EnhancedParamsDiff.TypeWithContext> cleanQueue, List<EnhancedParamsDiff.TypeWithContext> dirtyQueue, int index
   ) {
      EnhancedParamsDiff.TypeWithContext param = cleanQueue.get(index);
      EnhancedParamsDiff.TypeWithContext dirtyParam = dirtyQueue.get(index);
      Map<Type, Integer> cleanGroup = groupTypes(cleanQueue);
      Map<Type, Integer> dirtyGroup = groupTypes(dirtyQueue);
      if (cleanGroup.get(param.type()) == 1
         && dirtyGroup.getOrDefault(param.type(), 0) == 1
         && Objects.equals(cleanGroup.getOrDefault(dirtyParam.type(), 0), dirtyGroup.get(dirtyParam.type()))) {
         EnhancedParamsDiff.TypeWithContext newParam = dirtyQueue.stream().filter(t -> t.type().equals(param.type())).findFirst().orElseThrow();
         if (Math.abs(param.pos() - newParam.pos()) == 1) {
            builder.swap(param.pos(), newParam.pos());
         } else {
            builder.move(param.pos(), newParam.pos());
         }

         cleanQueue.remove(param);
         dirtyQueue.remove(newParam);
         return true;
      } else {
         return false;
      }
   }

   private static int findClosestMatch(List<EnhancedParamsDiff.TypeWithContext> cleanQueue, List<EnhancedParamsDiff.TypeWithContext> dirtyQueue) {
      for (EnhancedParamsDiff.TypeWithContext typeWithContext : cleanQueue) {
         int pos = lookAhead(dirtyQueue, typeWithContext);
         if (pos != -1) {
            return pos;
         }
      }

      return -1;
   }

   private static boolean tryReplacingParams(
      ParamsDiffSnapshotBuilder builder, List<EnhancedParamsDiff.TypeWithContext> cleanQueue, List<EnhancedParamsDiff.TypeWithContext> dirtyQueue
   ) {
      if (replaceType(builder, 0, cleanQueue, dirtyQueue)) {
         return true;
      } else if (cleanQueue.size() != 2 || dirtyQueue.size() != 2) {
         return false;
      } else if (cleanQueue.get(0).sameType(dirtyQueue.get(1)) && cleanQueue.get(1).sameType(dirtyQueue.get(0))) {
         builder.swap(dirtyQueue.get(0).pos(), dirtyQueue.get(1).pos());
         cleanQueue.clear();
         dirtyQueue.clear();
         return true;
      } else {
         return replaceType(builder, 1, cleanQueue, dirtyQueue);
      }
   }

   private static boolean replaceType(
      ParamsDiffSnapshotBuilder builder, int index, List<EnhancedParamsDiff.TypeWithContext> cleanQueue, List<EnhancedParamsDiff.TypeWithContext> dirtyQueue
   ) {
      if (index < cleanQueue.size() && index < dirtyQueue.size()) {
         EnhancedParamsDiff.TypeWithContext cleanType = cleanQueue.get(index);
         EnhancedParamsDiff.TypeWithContext dirtyType = dirtyQueue.get(index);
         int next = index + 1;
         if ((cleanQueue.size() == dirtyQueue.size() || dirtyQueue.size() <= next || !cleanType.sameType(dirtyQueue.get(next)))
            && !isPossiblyInjected(next, cleanQueue, dirtyQueue)) {
            if (!cleanType.sameType(dirtyType)) {
               if (DEBUG) {
                  LOGGER.info("Replacing {} with {}", cleanType, dirtyType);
               }

               cleanQueue.remove(index);
               dirtyQueue.remove(index);
               builder.replace(dirtyType.pos(), dirtyType.type());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean isPossiblyInjected(
      int index, List<EnhancedParamsDiff.TypeWithContext> cleanQueue, List<EnhancedParamsDiff.TypeWithContext> dirtyQueue
   ) {
      for (int i = index; i < cleanQueue.size() && i < dirtyQueue.size(); i++) {
         if (!cleanQueue.get(i).sameType(dirtyQueue.get(i))) {
            return true;
         }
      }

      return false;
   }

   private static <T> Comparator<T> maybeInverse(Comparator<T> comparator, boolean inverse) {
      return inverse ? comparator.reversed() : comparator;
   }

   private static List<Pair<Type, EnhancedParamsDiff.TypeWithContext>> sortMapDiff(
      Map<Type, Integer> map, List<EnhancedParamsDiff.TypeWithContext> context, boolean descending
   ) {
      return map.entrySet()
         .stream()
         .map(e -> {
            Type type = e.getKey();
            Integer count = e.getValue();
            if (count == 1) {
               EnhancedParamsDiff.TypeWithContext inserted = context.stream().filter(t -> t.type().equals(type)).findFirst().orElseThrow();
               return Pair.of(type, inserted);
            } else {
               return null;
            }
         })
         .filter(Objects::nonNull)
         .sorted(maybeInverse(Comparator.comparingInt(p -> ((EnhancedParamsDiff.TypeWithContext)p.getSecond()).pos()), descending))
         .toList();
   }

   @Nullable
   private static EnhancedParamsDiff.SwapResult checkForSwaps(
      ParamsDiffSnapshotBuilder builder, List<EnhancedParamsDiff.TypeWithContext> clean, List<EnhancedParamsDiff.TypeWithContext> dirty
   ) {
      Map<Type, Integer> cleanGroup = groupTypes(clean);
      Map<Type, Integer> dirtyGroup = groupTypes(dirty);
      MapDifference<Type, Integer> diff = Maps.difference(cleanGroup, dirtyGroup);
      List<EnhancedParamsDiff.TypeWithContext> rearrangeClean = new ArrayList<>(clean);
      List<EnhancedParamsDiff.TypeWithContext> rearrangeDirty = new ArrayList<>(dirty);
      List<EnhancedParamsDiff.TypeWithContext> removeDirty = new ArrayList<>();
      LayeredParamsDiffSnapshot.Builder tempDiff = LayeredParamsDiffSnapshot.builder();
      if (diff.entriesOnlyOnLeft().isEmpty() && !diff.entriesOnlyOnRight().isEmpty()) {
         for (Pair<Type, EnhancedParamsDiff.TypeWithContext> p : sortMapDiff(diff.entriesOnlyOnRight(), dirty, false)) {
            Type type = (Type)p.getFirst();
            EnhancedParamsDiff.TypeWithContext inserted = (EnhancedParamsDiff.TypeWithContext)p.getSecond();
            dirtyGroup.remove(type);
            rearrangeDirty.remove(inserted);
            removeDirty.add(inserted);
            int offset = inserted.pos() + (int)builder.getRemovals().stream().filter(i -> i < inserted.pos()).count();
            tempDiff.insert(offset, inserted.type());
         }
      } else if (!diff.entriesOnlyOnLeft().isEmpty() && diff.entriesOnlyOnRight().isEmpty()) {
         for (Pair<Type, EnhancedParamsDiff.TypeWithContext> p : sortMapDiff(diff.entriesOnlyOnLeft(), clean, true)) {
            Type type = (Type)p.getFirst();
            EnhancedParamsDiff.TypeWithContext inserted = (EnhancedParamsDiff.TypeWithContext)p.getSecond();
            int offset = inserted.pos() - (int)builder.getRemovals().stream().filter(i -> i < inserted.pos()).count();
            tempDiff.remove(offset);
            cleanGroup.remove(type);
            rearrangeClean.remove(inserted);
         }
      }

      if (!sameTypeCount(cleanGroup, dirtyGroup)) {
         return null;
      }

      if (DEBUG) {
         LOGGER.info("Checking for swaps in parameters:\n{}", printTable(rearrangeClean, rearrangeDirty));
      }

      while (!rearrangeClean.isEmpty() && rearrangeClean.getFirst().sameType(rearrangeDirty.getFirst())) {
         rearrangeClean.removeFirst();
         rearrangeDirty.removeFirst();
      }

      while (!rearrangeClean.isEmpty() && rearrangeClean.getLast().sameType(rearrangeDirty.getLast())) {
         rearrangeClean.removeLast();
         rearrangeDirty.removeLast();
      }

      if (!rearrangeClean.isEmpty() && !rearrangeDirty.isEmpty()) {
         builder.merge(tempDiff.build());
         rearrange(builder, rearrangeClean, rearrangeDirty);
         return new EnhancedParamsDiff.SwapResult(removeDirty);
      } else if (rearrangeClean.isEmpty() && rearrangeDirty.isEmpty()) {
         builder.merge(tempDiff.build());
         return new EnhancedParamsDiff.SwapResult(removeDirty);
      } else {
         return null;
      }
   }

   private static void rearrange(
      ParamsDiffSnapshotBuilder builder, List<EnhancedParamsDiff.TypeWithContext> clean, List<EnhancedParamsDiff.TypeWithContext> dirty
   ) {
      Map<Type, Integer> cleanGroup = groupTypes(clean);
      Map<Type, Integer> dirtyGroup = groupTypes(dirty);
      if (sameTypeCount(cleanGroup, dirtyGroup)) {
         if (DEBUG) {
            LOGGER.info("Rearranging parameters:\n{}", printTable(clean, dirty));
         }

         record Rearrangement(EnhancedParamsDiff.TypeWithContext cleanType, EnhancedParamsDiff.TypeWithContext dirtyType, int fromRelative, int toRelative) {
         }

         List<Rearrangement> rearrangements = new ArrayList<>();

         for (EnhancedParamsDiff.TypeWithContext cleanType : clean) {
            if (cleanGroup.get(cleanType.type()) == 1 && dirtyGroup.get(cleanType.type()) == 1) {
               EnhancedParamsDiff.TypeWithContext dirtyType = dirty.stream().filter(cleanType::sameType).findFirst().orElseThrow();
               rearrangements.add(new Rearrangement(cleanType, dirtyType, clean.indexOf(cleanType), dirty.indexOf(dirtyType)));
            }
         }

         rearrangements.sort(Comparator.comparingInt(Rearrangement::toRelative));
         List<EnhancedParamsDiff.TypeWithContext> rearrangeClean = new ArrayList<>(clean);
         List<EnhancedParamsDiff.TypeWithContext> rearrangeDirty = new ArrayList<>(dirty);

         for (Rearrangement rearrangement : rearrangements) {
            boolean same = true;

            for (int i = 0; i < rearrangeClean.size(); i++) {
               if (!rearrangeClean.get(i).sameType(rearrangeDirty.get(i))) {
                  same = false;
               }
            }

            if (same) {
               break;
            }

            if (DEBUG) {
               LOGGER.info("Moving param {} to index {}", rearrangement.cleanType(), rearrangement.dirtyType().pos());
            }

            int offsetOriginalPos = rearrangeDirty.get(rearrangement.fromRelative()).pos();
            int destPos = rearrangement.dirtyType().pos();
            if (Math.abs(offsetOriginalPos - destPos) == 1) {
               builder.swap(offsetOriginalPos, destPos);
            } else {
               builder.move(offsetOriginalPos, destPos);
            }

            rearrangeClean.add(rearrangement.toRelative(), rearrangeClean.remove(rearrangement.fromRelative()));
         }
      }
   }

   private static void compare(
      ParamsDiffSnapshotBuilder builder, List<EnhancedParamsDiff.TypeWithContext> clean, List<EnhancedParamsDiff.TypeWithContext> dirty
   ) {
      if (DEBUG) {
         LOGGER.info("Running comparison for:\n{}", printTable(clean, dirty));
      }

      List<ParametersDiff.MethodParameter> cleanTypes = clean.stream().map(t -> new ParametersDiff.MethodParameter(t.type(), t.isGenerated())).toList();
      List<ParametersDiff.MethodParameter> dirtyTypes = dirty.stream().map(t -> new ParametersDiff.MethodParameter(t.type(), t.isGenerated())).toList();
      ParametersDiff diff = ParametersDiff.compareParameters(cleanTypes, dirtyTypes, false);
      if (DEBUG) {
         LOGGER.info(
            "Comparison results:\n\tInserted: {}\n\tReplaced: {}\n\tSwapped:  {}\n\tRemoved:  {}",
            new Object[]{diff.insertions(), diff.removals(), diff.swaps(), diff.removals()}
         );
      }

      int indexOffset = !dirty.isEmpty() ? dirty.getFirst().pos() : 0;
      builder.merge(diff.toSnapshot(), indexOffset);
   }

   private static List<EnhancedParamsDiff.TypeWithContext> createPositionedList(List<Type> list) {
      List<EnhancedParamsDiff.TypeWithContext> ret = new ArrayList<>();

      for (int i = 0; i < list.size(); i++) {
         ret.add(new EnhancedParamsDiff.TypeWithContext(list.get(i), i));
      }

      return ret;
   }

   private static List<EnhancedParamsDiff.TypeWithContext> createPositionedVariableList(List<EnhancedParamsDiff.LocalVariable> list) {
      List<EnhancedParamsDiff.TypeWithContext> ret = new ArrayList<>();

      for (int i = 0; i < list.size(); i++) {
         EnhancedParamsDiff.LocalVariable ctx = list.get(i);
         ret.add(new EnhancedParamsDiff.TypeWithContext(ctx.name(), ctx.type(), i, ctx.isGenerated()));
      }

      return ret;
   }

   private static boolean sameTypeCount(Map<Type, Integer> cleanGroup, Map<Type, Integer> dirtyGroup) {
      if (cleanGroup.size() != dirtyGroup.size()) {
         return false;
      }

      for (Entry<Type, Integer> entry : cleanGroup.entrySet()) {
         Integer dirtyCount = dirtyGroup.get(entry.getKey());
         if (dirtyCount == null || dirtyCount != entry.getValue()) {
            return false;
         }
      }

      return true;
   }

   private static <T> List<T> extract(List<T> list, int amount) {
      List<T> res = new ArrayList<>();

      for (int i = 0; i < amount && !list.isEmpty(); i++) {
         res.add(list.removeFirst());
      }

      return res;
   }

   private static int lookAhead(List<EnhancedParamsDiff.TypeWithContext> types, EnhancedParamsDiff.TypeWithContext target) {
      for (int i = 0; i < types.size(); i++) {
         if (target.sameType(types.get(i))) {
            return i;
         }
      }

      return -1;
   }

   private static Map<Type, Integer> groupTypes(List<EnhancedParamsDiff.TypeWithContext> list) {
      Map<Type, Integer> map = new HashMap<>();

      for (EnhancedParamsDiff.TypeWithContext type : list) {
         map.compute(type.type(), (t, old) -> old == null ? 1 : old + 1);
      }

      return map;
   }

   private static String printTable(List<EnhancedParamsDiff.TypeWithContext> clean, List<EnhancedParamsDiff.TypeWithContext> dirty) {
      StringBuilder builder = new StringBuilder();
      builder.append("\t| %5s | %25s%s%26s | %25s%s%26s |\n".formatted("Index", "", "Clean", "", "", "Dirty", ""));
      builder.append("\t");
      builder.append("=".repeat(127));
      builder.append("\n");
      int max = Math.max(clean.size(), dirty.size());

      for (int i = 0; i < max; i++) {
         builder.append("\t| %-5s | %56s | %-56s |\n".formatted(i, i < clean.size() ? clean.get(i).type() : "", i < dirty.size() ? dirty.get(i).type() : ""));
      }

      builder.append("\t");
      builder.append("=".repeat(127));
      return builder.toString();
   }

   private record LocalVariable(@Nullable String name, Type type, boolean isGenerated) {
      public LocalVariable(LocalVariableNode lvn) {
         this(lvn.name, Type.getType(lvn.desc), lvn.name != null && GeneratedVariables.isGeneratedVariableName(lvn.name, Type.getType(lvn.desc)));
      }
   }

   private record SwapResult(List<EnhancedParamsDiff.TypeWithContext> removeDirty) {
   }

   private record TypeWithContext(@Nullable String name, Type type, int pos, boolean isGenerated) {
      public TypeWithContext(Type type, int pos) {
         this(null, type, pos, false);
      }

      public boolean sameType(EnhancedParamsDiff.TypeWithContext other) {
         return this.type().equals(other.type());
      }

      public boolean sameName(EnhancedParamsDiff.TypeWithContext other) {
         return this.name == null || other.name() == null || this.isGenerated == other.isGenerated();
      }

      public boolean matches(EnhancedParamsDiff.TypeWithContext other) {
         return this.sameType(other) && this.sameName(other);
      }
   }
}
