package org.sinytra.adapter.analysis.params;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.sinytra.adapter.transform.param.TransformParameters;

public interface ParamsDiffSnapshot {
   int modificationsCount();

   boolean isEmpty();

   List<Pair<Integer, Type>> insertions();

   List<Pair<Integer, Type>> replacements();

   List<Integer> removals();

   List<Pair<Integer, Integer>> swaps();

   List<Pair<Integer, Integer>> substitutes();

   List<Pair<Integer, Integer>> moves();

   List<Pair<Integer, Consumer<InstructionAdapter>>> inlines();

   ParamsDiffSnapshot offset(int var1);

   ParamsDiffSnapshot offset(int var1, int var2);

   TransformParameters asParameterTransformer(boolean var1, Set<ParamsDiffSnapshot.Flags> var2);

   enum Flags {
      REMOVED_VAR_GRAVE;

      // $VF: synthetic method
      private static ParamsDiffSnapshot.Flags[] $values() {
         return new ParamsDiffSnapshot.Flags[]{REMOVED_VAR_GRAVE};
      }
   }
}
