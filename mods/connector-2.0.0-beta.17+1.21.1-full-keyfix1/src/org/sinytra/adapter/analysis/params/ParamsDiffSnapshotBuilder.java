package org.sinytra.adapter.analysis.params;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

@CanIgnoreReturnValue
public interface ParamsDiffSnapshotBuilder {
   ParamsDiffSnapshotBuilder insert(int var1, Type var2);

   ParamsDiffSnapshotBuilder insertions(List<Pair<Integer, Type>> var1);

   ParamsDiffSnapshotBuilder replace(int var1, Type var2);

   ParamsDiffSnapshotBuilder replacements(List<Pair<Integer, Type>> var1);

   ParamsDiffSnapshotBuilder swap(int var1, int var2);

   ParamsDiffSnapshotBuilder swaps(List<Pair<Integer, Integer>> var1);

   ParamsDiffSnapshotBuilder substitute(int var1, int var2);

   ParamsDiffSnapshotBuilder substitutes(List<Pair<Integer, Integer>> var1);

   ParamsDiffSnapshotBuilder move(int var1, int var2);

   ParamsDiffSnapshotBuilder moves(List<Pair<Integer, Integer>> var1);

   ParamsDiffSnapshotBuilder remove(int var1);

   ParamsDiffSnapshotBuilder removals(List<Integer> var1);

   ParamsDiffSnapshotBuilder inline(int var1, Consumer<InstructionAdapter> var2);

   ParamsDiffSnapshotBuilder inlines(List<Pair<Integer, Consumer<InstructionAdapter>>> var1);

   ParamsDiffSnapshotBuilder merge(ParamsDiffSnapshot var1);

   ParamsDiffSnapshotBuilder merge(ParamsDiffSnapshot var1, int var2);

   @CheckReturnValue
   List<Integer> getRemovals();
}
