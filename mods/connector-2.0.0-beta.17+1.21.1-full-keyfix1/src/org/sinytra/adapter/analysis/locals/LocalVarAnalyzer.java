package org.sinytra.adapter.analysis.locals;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.params.EnhancedParamsDiff;
import org.sinytra.adapter.analysis.params.ParamsDiffSnapshot;
import org.sinytra.adapter.env.ctx.LocalVariable;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.transform.param.TransformParameters;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.OpcodeUtil;

public final class LocalVarAnalyzer {
   @Nullable
   public static LocalVarAnalyzer.CapturedLocalsInfo getCapturedLocals(MixinContext context, TargetPair dirtyTarget) {
      AdapterUtil.CapturedLocals capturedLocals = AdapterUtil.getCapturedLocals(context, dirtyTarget);
      if (capturedLocals == null) {
         return null;
      }

      List<LocalVariable> available = context.methods().getTargetMethodLocals(capturedLocals.target());
      if (available == null) {
         return null;
      }

      List<Type> availableTypes = available.stream().map(LocalVariable::type).toList();
      ParamsDiffSnapshot diff = EnhancedParamsDiff.createLayered(capturedLocals.expected(), availableTypes);
      return new LocalVarAnalyzer.CapturedLocalsInfo(capturedLocals, diff, availableTypes);
   }

   public static InsnList findInitializerInsns(MethodNode methodNode, int index) {
      InsnList insns = new InsnList();

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof VarInsnNode varInsn && varInsn.var == index && OpcodeUtil.isStoreOpcode(varInsn.getOpcode())) {
            for (AbstractInsnNode prev = insn.getPrevious(); prev != null; prev = prev.getPrevious()) {
               if (prev instanceof LabelNode) {
                  return insns;
               }

               if (!(prev instanceof FrameNode) && !(prev instanceof LineNumberNode)) {
                  insns.insert(prev.clone(Map.of()));
               }
            }
         }
      }

      return insns;
   }

   public static LocalVarAnalyzer.CapturedLocalsTransform analyzeCapturedLocals(AdapterUtil.CapturedLocals capturedLocals, MethodNode methodNode) {
      int paramLocalStart = capturedLocals.paramLocalStart();
      LocalVariableLookup table = capturedLocals.lvt();
      Set<Integer> used = new HashSet<>();
      Set<LocalVariableNode> usedLocalNodes = new HashSet<>();

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof VarInsnNode varInsn) {
            LocalVariableNode node = table.getByIndexOrNull(varInsn.var);
            if (node != null) {
               int ordinal = table.getParameterOrdinal(node);
               if (ordinal != -1 && ordinal >= paramLocalStart && ordinal <= capturedLocals.paramLocalEnd()) {
                  used.add(ordinal);
                  usedLocalNodes.add(node);
               }
            }
         }
      }

      TransformParameters remover = TransformParameters.builder()
         .chain(
            b -> IntStream.range(paramLocalStart, capturedLocals.paramLocalEnd() + 1)
               .filter(i -> !used.contains(i))
               .boxed()
               .sorted(Collections.reverseOrder())
               .forEach(b::remove)
         )
         .build();
      return new LocalVarAnalyzer.CapturedLocalsTransform(used, remover, usedLocalNodes);
   }

   public static void findVariableInitializerInsns(
      MethodNode methodNode, boolean isStatic, int index, Int2ObjectMap<InsnList> varInsnLists, Int2IntMap usageCount
   ) {
      InsnList insns = new InsnList();

      label56:
      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof VarInsnNode varInsn && varInsn.var == index && OpcodeUtil.isStoreOpcode(varInsn.getOpcode())) {
            for (AbstractInsnNode prev = insn.getPrevious(); prev != null; prev = prev.getPrevious()) {
               if (prev instanceof LabelNode) {
                  break label56;
               }

               if (!(prev instanceof FrameNode) && !(prev instanceof LineNumberNode)) {
                  if (prev instanceof VarInsnNode vInsn && (isStatic || vInsn.var != 0) && OpcodeUtil.isLoadOpcode(vInsn.getOpcode())) {
                     if (!varInsnLists.containsKey(vInsn.var)) {
                        findVariableInitializerInsns(methodNode, isStatic, vInsn.var, varInsnLists, usageCount);
                     }

                     usageCount.compute(vInsn.var, (key, existing) -> existing == null ? 1 : existing + 1);
                  }

                  insns.insert(prev.clone(Map.of()));
               }
            }
         }
      }

      varInsnLists.put(index, insns);
   }

   private LocalVarAnalyzer() {
   }

   public record CapturedLocalsInfo(AdapterUtil.CapturedLocals capturedLocals, ParamsDiffSnapshot diff, List<Type> availableTypes) {
   }

   public record CapturedLocalsTransform(Collection<Integer> used, TransformParameters remover, Collection<LocalVariableNode> usedLocalNodes) {
      public LocalVarAnalyzer.CapturedLocalsUsage getUsage(AdapterUtil.CapturedLocals capturedLocals) {
         LocalVariableLookup targetTable = new LocalVariableLookup(capturedLocals.target().methodNode());
         Int2ObjectMap<InsnList> varInsnLists = new Int2ObjectOpenHashMap();
         Int2IntMap usageCount = new Int2IntOpenHashMap();
         this.used.forEach(ordinal -> {
            int index = targetTable.getByOrdinal(ordinal).index;
            LocalVarAnalyzer.findVariableInitializerInsns(capturedLocals.target().methodNode(), capturedLocals.isStatic(), index, varInsnLists, usageCount);
         });
         return new LocalVarAnalyzer.CapturedLocalsUsage(targetTable, usageCount, varInsnLists);
      }
   }

   public record CapturedLocalsUsage(LocalVariableLookup targetTable, Int2IntMap usageCount, Int2ObjectMap<InsnList> varInsnLists) {
   }
}
