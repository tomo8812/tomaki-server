package org.sinytra.adapter.analysis.params;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.GeneratedVariables;

@Deprecated
public record ParametersDiff(
   int originalCount,
   List<Pair<Integer, Type>> insertions,
   List<Pair<Integer, Type>> replacements,
   List<Pair<Integer, Integer>> swaps,
   List<Integer> removals,
   List<Pair<Integer, Integer>> moves
) {
   public LayeredParamsDiffSnapshot toSnapshot() {
      return LayeredParamsDiffSnapshot.builder()
         .insertions(this.insertions)
         .replacements(this.replacements)
         .swaps(this.swaps)
         .removals(this.removals)
         .moves(this.moves)
         .build();
   }

   public static ParametersDiff compareTypeParameters(Type[] parameterTypes, Type[] newParameterTypes) {
      List<ParametersDiff.MethodParameter> cleanParameters = Stream.of(parameterTypes).map(type -> new ParametersDiff.MethodParameter(null, type)).toList();
      List<ParametersDiff.MethodParameter> dirtyParameters = Stream.of(newParameterTypes).map(type -> new ParametersDiff.MethodParameter(null, type)).toList();
      return compareParameters(cleanParameters, dirtyParameters, false);
   }

   public static ParametersDiff compareParameters(
      List<ParametersDiff.MethodParameter> cleanParameters, List<ParametersDiff.MethodParameter> dirtyParameters, boolean lvtIndexes
   ) {
      List<Pair<Integer, Type>> insertions = new ArrayList<>();
      List<Pair<Integer, Type>> replacements = new ArrayList<>();
      List<Pair<Integer, Integer>> swaps = new ArrayList<>();
      List<Integer> removals = new ArrayList<>();
      int i = 0;
      int j = 0;
      int lvtIndex = 0;

      label94:
      while (j < dirtyParameters.size()) {
         boolean skipJIncr = false;
         if (i < cleanParameters.size()) {
            ParametersDiff.MethodParameter cleanParam = cleanParameters.get(i);
            ParametersDiff.MethodParameter dirtyParam = dirtyParameters.get(j);
            boolean sameType = cleanParam.type.equals(dirtyParam.type);
            if (!sameType || !cleanParam.matchName(dirtyParam)) {
               boolean handled = false;
               boolean removing = false;
               if (i + 1 < cleanParameters.size() && j + 1 < dirtyParameters.size()) {
                  ParametersDiff.MethodParameter nextCleanParam = cleanParameters.get(i + 1);
                  ParametersDiff.MethodParameter nextDirtyParam = dirtyParameters.get(j + 1);
                  if (nextCleanParam.type.equals(dirtyParam.type) && nextDirtyParam.equals(cleanParam)) {
                     swaps.add(Pair.of(j, j + 1));
                     i++;
                     lvtIndex++;
                     j++;
                     handled = true;
                  }

                  if (nextCleanParam.equals(dirtyParam) && (j + 2 >= cleanParameters.size() || cleanParameters.get(j + 2).equals(nextDirtyParam))) {
                     removing = true;
                  }
               }

               if (!handled) {
                  for (int k = j + 1; k < dirtyParameters.size(); k++) {
                     ParametersDiff.MethodParameter dirtyParamAhead = dirtyParameters.get(k);
                     if (cleanParam.type.equals(dirtyParamAhead.type) && (sameType || cleanParam.matchName(dirtyParamAhead))) {
                        while (j < k) {
                           insertions.add(Pair.of(lvtIndex, dirtyParameters.get(j).type));
                           j++;
                           lvtIndex++;
                        }
                        continue label94;
                     }
                  }

                  if (removing) {
                     removals.add(j);
                     skipJIncr = true;
                  } else if (!cleanParam.type.equals(dirtyParam.type)) {
                     replacements.add(Pair.of(lvtIndex, dirtyParam.type));
                  }
               }
            }

            i++;
            lvtIndex += lvtIndexes ? AdapterUtil.getLVTOffsetForType(dirtyParam.type) : 1;
         } else {
            Type type = dirtyParameters.get(j).type;
            insertions.add(Pair.of(lvtIndex, type));
            lvtIndex += lvtIndexes ? AdapterUtil.getLVTOffsetForType(type) : 1;
         }

         if (!skipJIncr) {
            j++;
         }
      }

      if (j - i + removals.size() != insertions.size()) {
         throw new IllegalStateException("Unexpected difference in params size");
      } else {
         return new ParametersDiff(i, insertions, replacements, swaps, removals, List.of());
      }
   }

   public boolean isEmpty() {
      return this.insertions.isEmpty() && this.replacements.isEmpty() && this.swaps.isEmpty() && this.removals.isEmpty() && this.moves.isEmpty();
   }

   public record MethodParameter(Type type, boolean isGeneratedType) {
      public MethodParameter(@Nullable String name, Type type) {
         this(type, name != null && GeneratedVariables.isGeneratedVariableName(name, type));
      }

      public boolean matchName(ParametersDiff.MethodParameter other) {
         return this.isGeneratedType == other.isGeneratedType;
      }
   }
}
