package org.sinytra.adapter.env.param;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.sinytra.adapter.analysis.params.LayeredParamsDiffSnapshot;

public class ParamDiffResolver {
   public static ParamDiffResolver.ParamEvalResult resolve(List<Type> input, LayeredParamsDiffSnapshot diff) {
      List<ParamDiffResolver.PositionedParam> state = new ArrayList<>();

      for (int i = 0; i < input.size(); i++) {
         Type type = input.get(i);
         state.add(new ParamDiffResolver.PositionedParam(i, type));
      }

      for (LayeredParamsDiffSnapshot.ParamModification modification : diff.modifications()) {
         switch (modification) {
            case LayeredParamsDiffSnapshot.InsertParam(int index, Type type):
               state.add(index, new ParamDiffResolver.PositionedParam(-1, type));
               break;
            case LayeredParamsDiffSnapshot.ReplaceParam(int index, Type type):
               state.get(index).type = type;
               break;
            case LayeredParamsDiffSnapshot.SwapParam(int from, int to):
               ParamDiffResolver.PositionedParam clean = state.get(from);
               ParamDiffResolver.PositionedParam dirty = state.get(to);
               state.set(from, dirty);
               state.set(to, clean);
               break;
            case LayeredParamsDiffSnapshot.MoveParam(int from, int to):
               state.add(to, state.remove(from));
               break;
            case LayeredParamsDiffSnapshot.RemoveParam(int index):
               state.remove(index);
               break;
            case LayeredParamsDiffSnapshot.InlineParam inline:
               state.remove(inline.target());
               break;
            case LayeredParamsDiffSnapshot.SubstituteParam substitute:
               state.remove(substitute.target());
               break;
            case null:
            default:
               throw new RuntimeException("Unknown parameter modification: " + modification);
         }
      }

      List<ParamDiffResolver.ParamState> result = new ArrayList<>(state.size());

      for (int i = 0; i < state.size(); i++) {
         ParamDiffResolver.PositionedParam param = state.get(i);
         result.add(new ParamDiffResolver.ParamState(param.type, param.cleanPos, i));
      }

      return new ParamDiffResolver.ParamEvalResult(input, result);
   }

   public static class ParamEvalResult {
      private final List<Type> original;
      private final Map<Integer, ParamDiffResolver.ParamState> params;

      public ParamEvalResult(List<Type> original, List<ParamDiffResolver.ParamState> params) {
         this.original = new ReferenceArrayList(original);
         this.params = params.stream().filter(s -> s.cleanIndex != -1).collect(Collectors.toUnmodifiableMap(p -> p.cleanIndex, Function.identity()));
      }

      @Nullable
      public ParamDiffResolver.ParamState getUpdated(Type param) {
         int oldIndex = this.original.indexOf(param);
         return oldIndex == -1 ? null : this.params.get(oldIndex);
      }
   }

   public record ParamState(Type type, int cleanIndex, int dirtyIndex) {
   }

   private static class PositionedParam {
      private final int cleanPos;
      private Type type;

      public PositionedParam(int cleanPos, Type type) {
         this.cleanPos = cleanPos;
         this.type = type;
      }
   }
}
