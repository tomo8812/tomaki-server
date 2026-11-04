package org.sinytra.adapter.analysis.locals;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.MethodHelper;

public class LocalVariableLookup {
   private final List<LocalVariableNode> sortedLocals;
   private final boolean isNonStatic;
   private final int lastParamLVTIndex;
   private final Int2ObjectMap<LocalVariableNode> byIndex = new Int2ObjectOpenHashMap();
   private final Map<Type, List<LocalVariableNode>> byType = new HashMap<>();

   public LocalVariableLookup(MethodNode methodNode) {
      this.isNonStatic = !MethodHelper.isStatic(methodNode);
      this.sortedLocals = methodNode.localVariables.stream().sorted(Comparator.comparingInt(lvn -> lvn.index)).toList();

      for (LocalVariableNode node : this.sortedLocals) {
         this.byIndex.put(node.index, node);
      }

      int paramCount = Type.getArgumentTypes(methodNode.desc).length;
      this.lastParamLVTIndex = paramCount == 0 ? -1 : this.getByParameterOrdinal(paramCount - 1).index;
   }

   public LocalVariableNode getByOrdinal(int ordinal) {
      return this.sortedLocals.get(ordinal);
   }

   public LocalVariableNode getByParameterOrdinal(int ordinal) {
      return this.sortedLocals.get(this.isNonStatic ? ordinal + 1 : ordinal);
   }

   public LocalVariableNode getByIndex(int index) {
      return Objects.requireNonNull((LocalVariableNode)this.byIndex.get(index), "Missing local variable at index " + index);
   }

   @Nullable
   public LocalVariableNode getByIndexOrNull(int index) {
      return (LocalVariableNode)this.byIndex.get(index);
   }

   public int getOrdinal(LocalVariableNode node) {
      return this.sortedLocals.indexOf(node);
   }

   public int getParameterOrdinal(LocalVariableNode node) {
      return node.index > this.lastParamLVTIndex ? -1 : this.getOrdinal(node) - (this.isNonStatic ? 1 : 0);
   }

   public LocalVariableNode getLast() {
      return this.sortedLocals.getLast();
   }

   public List<LocalVariableNode> getForType(LocalVariableNode node) {
      return this.getForType(Type.getType(node.desc));
   }

   public List<LocalVariableNode> getForType(Type type) {
      return this.byType.computeIfAbsent(type, t -> this.sortedLocals.stream().filter(l -> Type.getType(l.desc).equals(type)).toList());
   }

   public Optional<LocalVariableNode> getByTypedOrdinal(Type type, int ordinal) {
      List<LocalVariableNode> available = this.getForType(type);
      return available.size() > ordinal ? Optional.of(available.get(ordinal)) : Optional.empty();
   }

   public Optional<Integer> getTypedOrdinal(LocalVariableNode node) {
      int ordinal = this.getForType(node).indexOf(node);
      return ordinal == -1 ? Optional.empty() : Optional.of(ordinal);
   }
}
