package org.sinytra.adapter.patch.processor.wrapop;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.transform.param.ParamTransformationUtil;
import org.sinytra.adapter.types.BytecodeFixerUpper;
import org.sinytra.adapter.types.TypeAdapter;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.OpcodeUtil;

public class WrapOpSurgeon {
   public static boolean tryUpgrade(MixinContext context, Recipe recipe, List<Type> methodParams, MethodInsnNode cleanInsn, MethodInsnNode dirtyInsn) {
      MethodNode methodNode = context.methodNode();
      LocalVariableLookup mixinLocals = new LocalVariableLookup(methodNode);
      Multimap<Integer, VarInsnNode> usedVars = getUsedVars(mixinLocals, methodParams, context);
      Map<Integer, Pair<TypeAdapter, Consumer<InsnList>>> adapters = new HashMap<>();
      Iterator var9 = usedVars.keys().iterator();

      while (true) {
         if (!var9.hasNext()) {
            usedVars.forEach((i, insn) -> {
               Pair<TypeAdapter, Consumer<InsnList>> adapter = adapters.get(i);
               ((TypeAdapter)adapter.getFirst()).apply(methodNode.instructions, insn);
            });

            for (Pair<TypeAdapter, Consumer<InsnList>> pair : adapters.values()) {
               if (pair.getSecond() != null) {
                  ((Consumer)pair.getSecond()).accept(methodNode.instructions);
               }
            }

            return true;
         }

         Integer key = (Integer)var9.next();
         if (key != 0) {
            break;
         }

         Pair<TypeAdapter, Consumer<InsnList>> pair = findReplacementForInstance(cleanInsn, dirtyInsn, context, recipe);
         if (pair == null) {
            break;
         }

         adapters.put(key, pair);
      }

      return false;
   }

   @Nullable
   private static Pair<TypeAdapter, Consumer<InsnList>> findReplacementForInstance(
      MethodInsnNode cleanInsn, MethodInsnNode dirtyInsn, MixinContext context, Recipe recipe
   ) {
      MethodNode cleanTargetMethod = recipe.getCleanTarget().methodNode();
      List<AbstractInsnNode> receiverInsns = MethodCallAnalyzer.getMethodCallArgInsns(cleanTargetMethod, cleanInsn).getFirst();
      if (!receiverInsns.isEmpty() && receiverInsns.getFirst() instanceof VarInsnNode varInsn) {
         BytecodeFixerUpper bfu = context.patchContext().environment().bytecodeFixerUpper();
         if (bfu == null) {
            return null;
         }

         LocalVariableLookup cleanLookup = recipe.cleanLocalsTable();
         LocalVariableNode lvn = cleanLookup.getByIndex(varInsn.var);
         TypeAdapter typeAdapter = bfu.getTypeAdapter(Type.getObjectType(dirtyInsn.owner), Type.getType(lvn.desc));
         if (typeAdapter == null) {
            return null;
         }

         List<AbstractInsnNode> subList = receiverInsns.subList(1, receiverInsns.size());
         TypeAdapter adapter = typeAdapter.andThen((list, insn) -> {
            for (AbstractInsnNode next = insn.getNext(); next != null && !(next instanceof LabelNode); next = next.getNext()) {
               if (next instanceof MethodInsnNode minsn && minsn.owner.equals(dirtyInsn.owner)) {
                  List<AbstractInsnNode> args = MethodCallAnalyzer.getMethodCallInsns(context.methodNode(), minsn);
                  if (args.contains(insn)) {
                     minsn.owner = cleanInsn.owner;
                  }
               }
            }

            list.insert(insn, AdapterUtil.insnList(AdapterUtil.cloneInsns(subList)));
         });
         Consumer<InsnList> castCheck = subList.getFirst() instanceof TypeInsnNode typeInsn && typeInsn.getOpcode() == 192
            ? list -> {
               List<AbstractInsnNode> originalWOCall = new ArrayList<>(ParamTransformationUtil.findWrapOperationOriginalCallArgs(context.methodNode()));
               originalWOCall.add(originalWOCall.getLast().getNext());
               originalWOCall.add(originalWOCall.getLast().getNext());
               originalWOCall.add(originalWOCall.getLast().getNext());
               List<AbstractInsnNode> cloned = AdapterUtil.cloneInsns(originalWOCall);
               boolean hasLabel = list.getFirst() instanceof LabelNode;
               LabelNode label = hasLabel ? (LabelNode)list.getFirst() : new LabelNode();
               InsnList check = AdapterUtil.insnList(
                  new LabelNode(), new VarInsnNode(25, 0), new TypeInsnNode(193, typeInsn.desc), new JumpInsnNode(154, label), new LabelNode()
               );
               typeAdapter.apply(check, check.get(1));
               check.add(AdapterUtil.insnList(cloned));
               check.add(new InsnNode(OpcodeUtil.getReturnOpcode(context.methodNode())));
               if (!hasLabel) {
                  check.add(label);
               }

               list.insert(check);
            }
            : null;
         return Pair.of(adapter, castCheck);
      } else {
         return null;
      }
   }

   public static Multimap<Integer, VarInsnNode> getUsedVars(LocalVariableLookup mixinLocals, List<Type> methodParams, MixinContext context) {
      MethodNode methodNode = context.methodNode();
      List<Integer> paramVars = IntStream.range(0, methodParams.size()).mapToObj(mixinLocals::getByParameterOrdinal).map(l -> l.index).toList();
      List<AbstractInsnNode> originalOpCall = ParamTransformationUtil.findWrapOperationOriginalCallArgs(methodNode);
      Multimap<Integer, VarInsnNode> usedVars = HashMultimap.create();

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof VarInsnNode varInsn && !originalOpCall.contains(insn) && paramVars.contains(varInsn.var)) {
            usedVars.put(varInsn.var, varInsn);
         }
      }

      return usedVars;
   }
}
