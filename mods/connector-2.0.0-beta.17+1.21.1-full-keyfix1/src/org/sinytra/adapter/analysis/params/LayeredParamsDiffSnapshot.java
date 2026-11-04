package org.sinytra.adapter.analysis.params;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CheckReturnValue;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.sinytra.adapter.transform.param.InjectParameterTransform;
import org.sinytra.adapter.transform.param.InlineParameterTransformer;
import org.sinytra.adapter.transform.param.MoveParametersTransformer;
import org.sinytra.adapter.transform.param.ParameterTransformer;
import org.sinytra.adapter.transform.param.RemoveParameterTransformer;
import org.sinytra.adapter.transform.param.ReplaceParametersTransformer;
import org.sinytra.adapter.transform.param.SubstituteParameterTransformer;
import org.sinytra.adapter.transform.param.SwapParametersTransformer;
import org.sinytra.adapter.transform.param.TransformParameters;

public record LayeredParamsDiffSnapshot(List<LayeredParamsDiffSnapshot.ParamModification> modifications) implements ParamsDiffSnapshot {
   public static LayeredParamsDiffSnapshot EMPTY = new LayeredParamsDiffSnapshot(List.of());

   @Override
   public int modificationsCount() {
      return this.modifications.size();
   }

   @Override
   public boolean isEmpty() {
      return this.modifications.isEmpty();
   }

   @Override
   public List<Pair<Integer, Type>> insertions() {
      return this.modifications
         .stream()
         .map(p -> p instanceof LayeredParamsDiffSnapshot.InsertParam param ? param : null)
         .filter(Objects::nonNull)
         .map(p -> Pair.of(p.index, p.type))
         .toList();
   }

   @Override
   public List<Pair<Integer, Type>> replacements() {
      return this.modifications
         .stream()
         .map(p -> p instanceof LayeredParamsDiffSnapshot.ReplaceParam param ? param : null)
         .filter(Objects::nonNull)
         .map(p -> Pair.of(p.index, p.type))
         .toList();
   }

   @Override
   public List<Pair<Integer, Integer>> swaps() {
      return this.modifications
         .stream()
         .map(p -> p instanceof LayeredParamsDiffSnapshot.SwapParam param ? param : null)
         .filter(Objects::nonNull)
         .map(p -> Pair.of(p.from(), p.to()))
         .toList();
   }

   @Override
   public List<Pair<Integer, Integer>> substitutes() {
      return this.modifications
         .stream()
         .map(p -> p instanceof LayeredParamsDiffSnapshot.SubstituteParam param ? param : null)
         .filter(Objects::nonNull)
         .map(p -> Pair.of(p.target(), p.substitute()))
         .toList();
   }

   @Override
   public List<Pair<Integer, Integer>> moves() {
      return this.modifications
         .stream()
         .map(p -> p instanceof LayeredParamsDiffSnapshot.MoveParam param ? param : null)
         .filter(Objects::nonNull)
         .map(p -> Pair.of(p.from(), p.to()))
         .toList();
   }

   @Override
   public List<Pair<Integer, Consumer<InstructionAdapter>>> inlines() {
      return this.modifications
         .stream()
         .map(p -> p instanceof LayeredParamsDiffSnapshot.InlineParam param ? param : null)
         .filter(Objects::nonNull)
         .map(p -> Pair.of(p.target(), p.adapter()))
         .toList();
   }

   @Override
   public List<Integer> removals() {
      return this.modifications
         .stream()
         .map(p -> p instanceof LayeredParamsDiffSnapshot.RemoveParam param ? param.index : null)
         .filter(Objects::nonNull)
         .toList();
   }

   public LayeredParamsDiffSnapshot offset(int offset) {
      return new LayeredParamsDiffSnapshot(this.modifications.stream().map(p -> p.offset(offset)).toList());
   }

   public LayeredParamsDiffSnapshot offset(int offset, int limit) {
      return new LayeredParamsDiffSnapshot(this.modifications.stream().filter(p -> p.satisfiesIndexLimit(limit)).map(p -> p.offset(offset)).toList());
   }

   @Override
   public TransformParameters asParameterTransformer(boolean withOffset, Set<ParamsDiffSnapshot.Flags> flags) {
      List<ParameterTransformer> transformers = this.modifications.stream().map(paramModification -> paramModification.asParameterTransformer(flags)).toList();
      return TransformParameters.builder().transform(transformers).withOffset(withOffset).build();
   }

   public static LayeredParamsDiffSnapshot.Builder builder() {
      return new LayeredParamsDiffSnapshot.Builder();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public ParamsDiffSnapshot offset(int param1, int nullx) {
      return this.offset(var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public ParamsDiffSnapshot offset(int param1) {
      return this.offset(var1);
   }

   public static class Builder implements ParamsDiffSnapshotBuilder {
      private final com.google.common.collect.ImmutableList.Builder<LayeredParamsDiffSnapshot.ParamModification> modifications = ImmutableList.builder();
      private final List<Integer> removals = new ArrayList<>();

      private Builder() {
      }

      public LayeredParamsDiffSnapshot.Builder insert(int index, Type type) {
         this.modifications.add(new LayeredParamsDiffSnapshot.InsertParam(index, type));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder insertions(List<Pair<Integer, Type>> insertions) {
         insertions.forEach(p -> this.insert((Integer)p.getFirst(), (Type)p.getSecond()));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder replace(int index, Type type) {
         this.modifications.add(new LayeredParamsDiffSnapshot.ReplaceParam(index, type));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder replacements(List<Pair<Integer, Type>> replacements) {
         replacements.forEach(p -> this.replace((Integer)p.getFirst(), (Type)p.getSecond()));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder swap(int from, int to) {
         this.modifications.add(new LayeredParamsDiffSnapshot.SwapParam(from, to));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder swaps(List<Pair<Integer, Integer>> swaps) {
         swaps.forEach(p -> this.swap((Integer)p.getFirst(), (Integer)p.getSecond()));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder substitute(int target, int substitute) {
         this.modifications.add(new LayeredParamsDiffSnapshot.SubstituteParam(target, substitute));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder substitutes(List<Pair<Integer, Integer>> substitutes) {
         substitutes.forEach(p -> this.substitute((Integer)p.getFirst(), (Integer)p.getSecond()));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder move(int from, int to) {
         this.modifications.add(new LayeredParamsDiffSnapshot.MoveParam(from, to));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder moves(List<Pair<Integer, Integer>> moves) {
         moves.forEach(p -> this.move((Integer)p.getFirst(), (Integer)p.getSecond()));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder remove(int index) {
         this.modifications.add(new LayeredParamsDiffSnapshot.RemoveParam(index));
         this.removals.add(index);
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder removals(List<Integer> removals) {
         removals.forEach(this::remove);
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder inline(int target, Consumer<InstructionAdapter> adapter) {
         this.modifications.add(new LayeredParamsDiffSnapshot.InlineParam(target, adapter));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder inlines(List<Pair<Integer, Consumer<InstructionAdapter>>> inlines) {
         inlines.forEach(p -> this.inline((Integer)p.getFirst(), (Consumer<InstructionAdapter>)p.getSecond()));
         return this;
      }

      public LayeredParamsDiffSnapshot.Builder merge(ParamsDiffSnapshot diff) {
         return this.merge(diff, 0);
      }

      public LayeredParamsDiffSnapshot.Builder merge(ParamsDiffSnapshot diff, int indexOffset) {
         diff.insertions().forEach(p -> this.insert((Integer)p.getFirst() + indexOffset, (Type)p.getSecond()));
         diff.replacements().forEach(p -> this.replace((Integer)p.getFirst() + indexOffset, (Type)p.getSecond()));
         diff.swaps().forEach(p -> this.swap((Integer)p.getFirst() + indexOffset, (Integer)p.getSecond() + indexOffset));
         diff.substitutes().forEach(p -> this.substitute((Integer)p.getFirst() + indexOffset, (Integer)p.getSecond() + indexOffset));
         diff.removals().forEach(p -> this.remove(p + indexOffset));
         diff.moves().forEach(p -> this.move((Integer)p.getFirst() + indexOffset, (Integer)p.getSecond() + indexOffset));
         diff.inlines().forEach(p -> this.inline((Integer)p.getFirst() + indexOffset, (Consumer<InstructionAdapter>)p.getSecond()));
         return this;
      }

      @Override
      public List<Integer> getRemovals() {
         return List.copyOf(this.removals);
      }

      @CheckReturnValue
      public LayeredParamsDiffSnapshot build() {
         return new LayeredParamsDiffSnapshot(this.modifications.build());
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder merge(ParamsDiffSnapshot param1, int nullx) {
         return this.merge(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder merge(ParamsDiffSnapshot param1) {
         return this.merge(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder inlines(List param1) {
         return this.inlines(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder inline(int param1, Consumer nullx) {
         return this.inline(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder removals(List param1) {
         return this.removals(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder remove(int param1) {
         return this.remove(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder moves(List param1) {
         return this.moves(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder move(int param1, int nullx) {
         return this.move(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder substitutes(List param1) {
         return this.substitutes(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder substitute(int param1, int nullx) {
         return this.substitute(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder swaps(List param1) {
         return this.swaps(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder swap(int param1, int nullx) {
         return this.swap(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder replacements(List param1) {
         return this.replacements(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder replace(int param1, Type nullx) {
         return this.replace(var1, nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder insertions(List param1) {
         return this.insertions(var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public ParamsDiffSnapshotBuilder insert(int param1, Type nullx) {
         return this.insert(var1, nullx);
      }
   }

   public record InlineParam(int target, Consumer<InstructionAdapter> adapter) implements LayeredParamsDiffSnapshot.ParamModification {
      @Override
      public LayeredParamsDiffSnapshot.ParamModification offset(int offset) {
         return new LayeredParamsDiffSnapshot.InlineParam(this.target + offset, this.adapter);
      }

      @Override
      public boolean satisfiesIndexLimit(int index) {
         return this.target < index;
      }

      @Override
      public ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> flags) {
         return new InlineParameterTransformer(this.target, this.adapter);
      }
   }

   public record InsertParam(int index, Type type) implements LayeredParamsDiffSnapshot.ParamModification {
      @Override
      public LayeredParamsDiffSnapshot.ParamModification offset(int offset) {
         return new LayeredParamsDiffSnapshot.InsertParam(this.index + offset, this.type);
      }

      @Override
      public boolean satisfiesIndexLimit(int index) {
         return this.index < index;
      }

      @Override
      public ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> flags) {
         return new InjectParameterTransform(this.index, this.type);
      }
   }

   public record MoveParam(int from, int to) implements LayeredParamsDiffSnapshot.ParamModification {
      @Override
      public LayeredParamsDiffSnapshot.ParamModification offset(int offset) {
         return new LayeredParamsDiffSnapshot.MoveParam(this.from + offset, this.to + offset);
      }

      @Override
      public boolean satisfiesIndexLimit(int index) {
         return this.from < index;
      }

      @Override
      public ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> flags) {
         return new MoveParametersTransformer(this.from, this.to);
      }
   }

   public sealed interface ParamModification
      permits LayeredParamsDiffSnapshot.InsertParam,
      LayeredParamsDiffSnapshot.ReplaceParam,
      LayeredParamsDiffSnapshot.SwapParam,
      LayeredParamsDiffSnapshot.MoveParam,
      LayeredParamsDiffSnapshot.RemoveParam,
      LayeredParamsDiffSnapshot.InlineParam,
      LayeredParamsDiffSnapshot.SubstituteParam {
      LayeredParamsDiffSnapshot.ParamModification offset(int var1);

      boolean satisfiesIndexLimit(int var1);

      ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> var1);
   }

   public record RemoveParam(int index) implements LayeredParamsDiffSnapshot.ParamModification {
      @Override
      public LayeredParamsDiffSnapshot.ParamModification offset(int offset) {
         return new LayeredParamsDiffSnapshot.RemoveParam(this.index + offset);
      }

      @Override
      public boolean satisfiesIndexLimit(int index) {
         return this.index < index;
      }

      @Override
      public ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> flags) {
         return new RemoveParameterTransformer(this.index, flags.contains(ParamsDiffSnapshot.Flags.REMOVED_VAR_GRAVE));
      }
   }

   public record ReplaceParam(int index, Type type) implements LayeredParamsDiffSnapshot.ParamModification {
      @Override
      public LayeredParamsDiffSnapshot.ParamModification offset(int offset) {
         return new LayeredParamsDiffSnapshot.ReplaceParam(this.index + offset, this.type);
      }

      @Override
      public boolean satisfiesIndexLimit(int index) {
         return this.index < index;
      }

      @Override
      public ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> flags) {
         return new ReplaceParametersTransformer(this.index, this.type);
      }
   }

   public record SubstituteParam(int target, int substitute) implements LayeredParamsDiffSnapshot.ParamModification {
      @Override
      public LayeredParamsDiffSnapshot.ParamModification offset(int offset) {
         return new LayeredParamsDiffSnapshot.SubstituteParam(this.target + offset, this.substitute + offset);
      }

      @Override
      public boolean satisfiesIndexLimit(int index) {
         return this.target < index;
      }

      @Override
      public ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> flags) {
         return new SubstituteParameterTransformer(this.target, this.substitute);
      }
   }

   public record SwapParam(int from, int to) implements LayeredParamsDiffSnapshot.ParamModification {
      @Override
      public LayeredParamsDiffSnapshot.ParamModification offset(int offset) {
         return new LayeredParamsDiffSnapshot.SwapParam(this.from + offset, this.to + offset);
      }

      @Override
      public boolean satisfiesIndexLimit(int index) {
         return this.from < index;
      }

      @Override
      public ParameterTransformer asParameterTransformer(Set<ParamsDiffSnapshot.Flags> flags) {
         return new SwapParametersTransformer(this.from, this.to);
      }
   }
}
