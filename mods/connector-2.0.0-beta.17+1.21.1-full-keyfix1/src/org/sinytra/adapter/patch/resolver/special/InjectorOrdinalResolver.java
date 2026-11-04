package org.sinytra.adapter.patch.resolver.special;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.InstructionMatcher;
import org.sinytra.adapter.analysis.locals.LocalVarAnalyzer;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.analysis.method.MethodAnalyzer;
import org.sinytra.adapter.analysis.method.MethodInsnMatcher;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.PropertyContainer;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.mixin.MixinFlag;
import org.sinytra.adapter.patch.resolver.Resolver;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.GeneratedVariables;
import org.sinytra.adapter.util.MethodQualifier;
import org.sinytra.adapter.util.SingleValueHandle;

public class InjectorOrdinalResolver implements Resolver {
   private static final Map<String, InjectorOrdinalResolver.OffsetUpdateHandler> OFFSET_HANDLERS = Map.of(
      "INVOKE", InjectorOrdinalResolver.InvokeOffsetHandler.INSTANCE, "RETURN", InjectorOrdinalResolver.ReturnOffsetHandler.INSTANCE
   );

   @Override
   public Resolver.ResolutionResult resolve(MixinContext context, Recipe recipe) {
      Type returnType = recipe.clean().getReturnType();
      if (returnType == null) {
         return Resolver.ResolutionResult.pass();
      }

      List<InjectorOrdinalResolver.HandlerInstance<?, ?>> offsetHandlers = getOffsetHandlers(context, recipe, returnType);
      if (offsetHandlers.isEmpty()) {
         return Resolver.ResolutionResult.pass();
      }

      TargetPair cleanTarget = recipe.getCleanTarget();
      if (cleanTarget == null) {
         return Resolver.ResolutionResult.pass();
      }

      TargetPair dirtyTarget = recipe.getDirtyTarget();
      if (dirtyTarget == null) {
         return Resolver.ResolutionResult.pass();
      }

      boolean applied = false;
      MutableConfiguration merged = MutableConfiguration.create();

      for (InjectorOrdinalResolver.HandlerInstance<?, ?> instance : offsetHandlers) {
         Configuration config = instance.apply(context, cleanTarget, dirtyTarget);
         if (config != null) {
            applied = true;
            merged.mergeFrom(config);
         }
      }

      return applied ? Resolver.ResolutionResult.success(merged) : Resolver.ResolutionResult.pass();
   }

   private static List<InjectorOrdinalResolver.HandlerInstance<?, ?>> getOffsetHandlers(MixinContext context, Recipe recipe, Type returnType) {
      Configuration cleanConfig = recipe.clean();
      AtData at = cleanConfig.getAtData();
      List<InjectorOrdinalResolver.HandlerInstance<?, ?>> handlers = new ArrayList<>();
      at.getProperty(AtData.Keys.ORDINAL)
         .ifPresent(
            ordinal -> {
               String target = at.getTarget().orElse(null);
               InjectorOrdinalResolver.OffsetUpdateHandler handler = OFFSET_HANDLERS.get(at.getValue());
               if (handler != null && (!handler.requiresTarget() || target != null)) {
                  InjectorOrdinalResolver.OffsetUpdateHandler.Context handleContext = new InjectorOrdinalResolver.OffsetUpdateHandler.Context(target, ordinal);
                  InjectorOrdinalResolver.HandlerInstance<?, ?> instance = new InjectorOrdinalResolver.HandlerInstance<>(
                     handler, handleContext, i -> MutableConfiguration.create().setAtData(at.withOrdinal(i))
                  );
                  handlers.add(instance);
               }
            }
         );
      if (context.hasFlag(MixinFlag.TARGETS_VARIABLE)) {
         LocalVariableLookup cleanTable = recipe.cleanLocalsTable();
         if (cleanTable != null) {
            cleanConfig.getProperty(MixinKeys.ORDINAL)
               .flatMap(
                  ordinal -> cleanTable.getByTypedOrdinal(returnType, ordinal)
                     .flatMap(lvn -> cleanTable.getTypedOrdinal(lvn).map(o -> new InjectorOrdinalResolver.LocalVar(lvn, o, true)))
                     .map(
                        local -> new InjectorOrdinalResolver.HandlerInstance<>(
                           InjectorOrdinalResolver.ModifyVariableOffsetHandler.INSTANCE,
                           local,
                           pair -> MutableConfiguration.create()
                              .setProperty(MixinKeys.ORDINAL, ((InjectorOrdinalResolver.LocalVar)pair.getFirst()).ordinal())
                              .mergeFrom((PropertyContainer)pair.getSecond())
                        )
                     )
               )
               .or(
                  () -> cleanConfig.getProperty(MixinKeys.INDEX)
                     .flatMap(
                        i -> Optional.ofNullable(cleanTable.getByIndexOrNull(i))
                           .flatMap(lvn -> cleanTable.getTypedOrdinal(lvn).map(o -> new InjectorOrdinalResolver.LocalVar(lvn, o, false)))
                           .map(
                              local -> new InjectorOrdinalResolver.HandlerInstance<>(
                                 InjectorOrdinalResolver.ModifyVariableOffsetHandler.INSTANCE,
                                 local,
                                 pair -> MutableConfiguration.create()
                                    .setProperty(MixinKeys.INDEX, ((InjectorOrdinalResolver.LocalVar)pair.getFirst()).lvn().index)
                                    .mergeFrom((PropertyContainer)pair.getSecond())
                              )
                           )
                     )
               )
               .ifPresent(handlers::add);
         }
      }

      return handlers;
   }

   private record HandlerInstance<T, U>(InjectorOrdinalResolver.UpdateHandler<T, U> handler, T context, Function<U, @Nullable Configuration> applicator) {
      @Nullable
      public Configuration apply(MixinContext mixinContext, TargetPair cleanTarget, TargetPair dirtyTarget) {
         Optional<U> updatedValue = this.handler.apply(mixinContext, cleanTarget, dirtyTarget, this.context);
         if (updatedValue.isPresent()) {
            U value = updatedValue.get();
            mixinContext.recordCtxAudit("Update injection point ordinal from %s to %s", this.context, value);
            return this.applicator.apply(value);
         } else {
            return null;
         }
      }
   }

   private static class InvokeOffsetHandler implements InjectorOrdinalResolver.OffsetUpdateHandler {
      public static final InjectorOrdinalResolver.InvokeOffsetHandler INSTANCE = new InjectorOrdinalResolver.InvokeOffsetHandler();

      @Override
      public boolean requiresTarget() {
         return true;
      }

      public Optional<Integer> apply(
         MixinContext mixinContext, TargetPair cleanTarget, TargetPair dirtyTarget, InjectorOrdinalResolver.OffsetUpdateHandler.Context context
      ) {
         String target = context.target();
         int ordinal = context.ordinal();
         Multimap<String, MethodInsnNode> cleanCallsMap = MethodAnalyzer.getMethodCalls(cleanTarget.methodNode());
         Multimap<String, MethodInsnNode> dirtyCallsMap = MethodAnalyzer.getMethodCalls(dirtyTarget.methodNode());
         PatchContext patchContext = mixinContext.patchContext();
         String cleanValue = patchContext.remap(target);
         Collection<? extends AbstractInsnNode> cleanCalls = cleanCallsMap.get(cleanValue);
         String dirtyValue = patchContext.remap(target);
         Collection<? extends AbstractInsnNode> dirtyCalls = dirtyCallsMap.get(dirtyValue);
         if (cleanCalls.size() != dirtyCalls.size()) {
            List<InstructionMatcher> cleanMatchers = cleanCalls.stream().map(MethodInsnMatcher::findSurroundingInstructions).toList();
            List<InstructionMatcher> dirtyMatchers = dirtyCalls.stream().map(MethodInsnMatcher::findSurroundingInstructions).toList();
            if (ordinal >= 0 && ordinal < cleanMatchers.size()) {
               InstructionMatcher original = cleanMatchers.get(ordinal);
               List<InstructionMatcher> matches = dirtyMatchers.stream().filter(original::test).toList();
               if (matches.size() == 1) {
                  return Optional.of(dirtyMatchers.indexOf(matches.getFirst()));
               }
            }
         }

         return Optional.empty();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Optional apply(MixinContext param1, TargetPair nullx, TargetPair nullxx, Object nullxxx) {
         return this.apply(var1, nullx, nullxx, (InjectorOrdinalResolver.OffsetUpdateHandler.Context)nullxxx);
      }
   }

   private record LocalVar(LocalVariableNode lvn, int ordinal, boolean relative) {
      public LocalVar(LocalVariableNode lvn, int ordinal) {
         this(lvn, ordinal, false);
      }
   }

   private static class ModifyVariableOffsetHandler
      implements InjectorOrdinalResolver.UpdateHandler<InjectorOrdinalResolver.LocalVar, Pair<InjectorOrdinalResolver.LocalVar, Configuration>> {
      private static final InjectorOrdinalResolver.ModifyVariableOffsetHandler INSTANCE = new InjectorOrdinalResolver.ModifyVariableOffsetHandler();

      public Optional<Pair<InjectorOrdinalResolver.LocalVar, @Nullable Configuration>> apply(
         MixinContext mixinContext, TargetPair cleanTarget, TargetPair dirtyTarget, InjectorOrdinalResolver.LocalVar local
      ) {
         MethodNode methodNode = mixinContext.methodNode();
         Type[] args = Type.getArgumentTypes(methodNode.desc);
         if (args.length < 1) {
            return Optional.empty();
         } else {
            Type targetType = args[0];
            if (targetType != Type.BOOLEAN_TYPE && targetType != Type.INT_TYPE && targetType != Type.FLOAT_TYPE) {
               return Optional.empty();
            } else {
               return mixinContext.methodAnnotation().getValue("slice").isPresent() && local.relative()
                  ? Optional.empty()
                  : tryFindUpdatedIndex(targetType, cleanTarget, dirtyTarget, local)
                     .map(var -> Pair.of(var, null))
                     .or(() -> tryFindSyntheticVariableIndex(mixinContext, methodNode, cleanTarget, dirtyTarget, local));
            }
         }
      }

      private static Optional<Pair<InjectorOrdinalResolver.LocalVar, @Nullable Configuration>> tryFindSyntheticVariableIndex(
         MixinContext mixinContext, MethodNode methodNode, TargetPair cleanTarget, TargetPair dirtyTarget, InjectorOrdinalResolver.LocalVar local
      ) {
         int ordinal = local.ordinal();
         Type variableType = Type.getReturnType(methodNode.desc);
         LocalVariableLookup cleanTable = new LocalVariableLookup(cleanTarget.methodNode());
         LocalVariableLookup dirtyTable = new LocalVariableLookup(dirtyTarget.methodNode());
         if (cleanTable.getForType(variableType).size() == dirtyTable.getForType(variableType).size()) {
            List<LocalVariableNode> available = dirtyTable.getForType(variableType);
            if (available.size() > ordinal) {
               int variableIndex = available.get(ordinal).index;
               AbstractInsnNode cleanInsn = mixinContext.methods().findInjectionTargetInsn(cleanTarget);
               AbstractInsnNode dirtyInsn = mixinContext.methods().findInjectionTargetInsn(dirtyTarget);
               if (cleanInsn != null && dirtyInsn != null) {
                  for (AbstractInsnNode insn = cleanInsn; insn != null && !(insn instanceof LabelNode); insn = insn.getNext()) {
                     SingleValueHandle<Integer> handle = AdapterUtil.handleLocalVarInsnValue(insn);
                     if (handle != null && handle.get() == variableIndex) {
                        List<SingleValueHandle<Integer>> dirtyVars = getUsedVariablesInLabel(dirtyInsn, insn.getOpcode());
                        if (dirtyVars.size() == 1) {
                           int dirtyIndex = dirtyVars.getFirst().get();
                           if (dirtyIndex != variableIndex) {
                              LocalVariableNode lvn = dirtyTable.getByIndex(dirtyIndex);
                              return dirtyTable.getTypedOrdinal(lvn)
                                 .map(
                                    o -> Pair.of(
                                       new InjectorOrdinalResolver.LocalVar(lvn, o), MutableConfiguration.create().setProperty(MixinKeys.ARGS_ONLY, false)
                                    )
                                 );
                           }
                        }
                        break;
                     }
                  }
               }
            }
         }

         return Optional.empty();
      }

      private static List<SingleValueHandle<Integer>> getUsedVariablesInLabel(AbstractInsnNode start, int opcode) {
         List<SingleValueHandle<Integer>> list = new ArrayList<>();

         for (AbstractInsnNode insn = start; insn != null && !(insn instanceof LabelNode); insn = insn.getNext()) {
            if (insn.getOpcode() == opcode) {
               SingleValueHandle<Integer> handle = AdapterUtil.handleLocalVarInsnValue(insn);
               if (handle != null) {
                  list.add(handle);
               }
            }
         }

         return list;
      }

      private static Optional<InjectorOrdinalResolver.LocalVar> tryFindUpdatedIndex(
         Type targetType, TargetPair cleanTarget, TargetPair dirtyTarget, InjectorOrdinalResolver.LocalVar local
      ) {
         int ordinal = local.ordinal();
         List<LocalVariableNode> cleanLocals = cleanTarget.methodNode()
            .localVariables
            .stream()
            .filter(l -> Type.getType(l.desc) == targetType)
            .sorted(Comparator.comparingInt(l -> l.index))
            .toList();
         if (cleanLocals.size() <= ordinal) {
            return Optional.empty();
         }

         LocalVariableNode cleanLocal = cleanLocals.get(ordinal);
         if (!GeneratedVariables.isGeneratedVariableName(cleanLocal.name, Type.getType(cleanLocal.desc))) {
            return Optional.empty();
         }

         LocalVariableLookup dirtyVarLookup = new LocalVariableLookup(dirtyTarget.methodNode());
         List<LocalVariableNode> dirtyLocals = dirtyVarLookup.getForType(targetType);
         if (cleanLocals.size() == dirtyLocals.size() && dirtyLocals.size() > ordinal) {
            LocalVariableNode dirtyLocal = dirtyLocals.get(ordinal);
            if (!local.relative() && dirtyLocal.index == local.lvn().index) {
               return Optional.empty();
            } else {
               OptionalInt dirtyNameOrdinal = GeneratedVariables.getGeneratedVariableOrdinal(dirtyLocal.name, Type.getType(dirtyLocal.desc));
               if (!dirtyNameOrdinal.isEmpty() && (!local.relative() || ordinal != dirtyNameOrdinal.getAsInt())) {
                  return cleanLocal.index != dirtyLocal.index && !local.relative()
                     ? Optional.of(new InjectorOrdinalResolver.LocalVar(dirtyLocal, dirtyLocals.indexOf(dirtyLocal)))
                     : Optional.empty();
               } else {
                  return Optional.empty();
               }
            }
         } else {
            return findReplacementLocal(cleanTarget.methodNode(), dirtyTarget.methodNode(), cleanLocal)
               .flatMap(var -> dirtyVarLookup.getTypedOrdinal(var).map(o -> new InjectorOrdinalResolver.LocalVar(var, o)));
         }
      }

      private static Optional<LocalVariableNode> findReplacementLocal(MethodNode cleanMethod, MethodNode dirtyMethod, LocalVariableNode desired) {
         InsnList desiredInitializerInsns = LocalVarAnalyzer.findInitializerInsns(cleanMethod, desired.index);
         List<LocalVariableNode> matches = dirtyMethod.localVariables.stream().filter(lvn -> desired.desc.equals(lvn.desc)).filter(lvn -> {
            InsnList insns = LocalVarAnalyzer.findInitializerInsns(dirtyMethod, lvn.index);
            return InstructionMatcher.test(desiredInitializerInsns, insns);
         }).toList();
         return matches.size() == 1 ? Optional.of(matches.getFirst()) : Optional.empty();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Optional/* @Nullable */ apply(MixinContext param1, TargetPair nullx, TargetPair nullxx, Object nullxxx) {
         return this.apply(var1, nullx, nullxx, (InjectorOrdinalResolver.LocalVar)nullxxx);
      }
   }

   private interface OffsetUpdateHandler extends InjectorOrdinalResolver.UpdateHandler<InjectorOrdinalResolver.OffsetUpdateHandler.Context, Integer> {
      default boolean requiresTarget() {
         return false;
      }

      record Context(@Nullable String target, int ordinal) {
      }
   }

   private static class ReturnOffsetHandler implements InjectorOrdinalResolver.OffsetUpdateHandler {
      public static final InjectorOrdinalResolver.OffsetUpdateHandler INSTANCE = new InjectorOrdinalResolver.ReturnOffsetHandler();
      private static final Set<Integer> RETURN_OPCODES = Set.of(177, 176, 172, 174, 175, 173);

      public Optional<Integer> apply(
         MixinContext mixinContext, TargetPair cleanTarget, TargetPair dirtyTarget, InjectorOrdinalResolver.OffsetUpdateHandler.Context context
      ) {
         int ordinal = context.ordinal();
         List<AbstractInsnNode> cleanReturnInsns = findReturnInsns(cleanTarget.methodNode());
         List<AbstractInsnNode> dirtyReturnInsns = findReturnInsns(dirtyTarget.methodNode());
         if (ordinal < cleanReturnInsns.size() && cleanReturnInsns.size() != dirtyReturnInsns.size()) {
            AbstractInsnNode cleanInsn = cleanReturnInsns.get(ordinal);
            InstructionMatcher original = new InstructionMatcher(cleanInsn, findReturnPrecedingInsns(cleanInsn), List.of());
            List<InstructionMatcher> dirtyMatchers = dirtyReturnInsns.stream()
               .map(i -> new InstructionMatcher(i, findReturnPrecedingInsns(i), List.of()))
               .toList();
            List<InstructionMatcher> matches = dirtyMatchers.stream().filter(m -> original.test(m, 1)).toList();
            if (matches.size() == 1) {
               return Optional.of(dirtyMatchers.indexOf(matches.getFirst()));
            }
         }

         return findByMethodCalls(ordinal, cleanTarget, dirtyTarget);
      }

      private static Optional<Integer> findByMethodCalls(int ordinal, TargetPair cleanTarget, TargetPair dirtyTarget) {
         List<Pair<InsnNode, List<String>>> cleanCalls = getBlockCalls(cleanTarget.methodNode());
         List<Pair<InsnNode, List<String>>> dirtyCalls = getBlockCalls(dirtyTarget.methodNode());
         if (ordinal >= cleanCalls.size()) {
            return Optional.empty();
         }

         List<String> cleanCall = (List<String>)cleanCalls.get(ordinal).getSecond();

         for (int i = 0; i < dirtyCalls.size(); i++) {
            Pair<InsnNode, List<String>> dirtyCall = dirtyCalls.get(i);
            boolean haveCommon = !Collections.disjoint(cleanCall, (Collection<?>)dirtyCall.getSecond());
            if (haveCommon) {
               return Optional.of(i);
            }
         }

         return Optional.empty();
      }

      private static List<Pair<InsnNode, List<String>>> getBlockCalls(MethodNode methodNode) {
         List<Pair<InsnNode, List<String>>> blockCalls = new ArrayList<>();
         List<String> minsns = new ArrayList<>();

         for (AbstractInsnNode insn : methodNode.instructions) {
            if (insn instanceof MethodInsnNode minsn) {
               minsns.add(MethodQualifier.create(minsn).asDescriptor());
            }

            if (RETURN_OPCODES.contains(insn.getOpcode())) {
               blockCalls.add(Pair.of((InsnNode)insn, minsns));
               minsns = new ArrayList<>();
            }
         }

         List<List<String>> values = blockCalls.stream().<List<String>>map(Pair::getSecond).toList();
         Map<String, Integer> counts = new HashMap<>();

         for (List<String> list : values) {
            for (String s : list) {
               counts.merge(s, 1, Integer::sum);
            }
         }

         for (List<String> list : values) {
            list.removeIf(sx -> counts.getOrDefault(sx, 0) > 1);
         }

         return blockCalls;
      }

      private static List<AbstractInsnNode> findReturnPrecedingInsns(AbstractInsnNode insn) {
         List<AbstractInsnNode> insns = new ArrayList<>();
         int maxSize = 6;

         for (AbstractInsnNode prev = insn.getPrevious();
            prev != null && insns.size() < maxSize && !RETURN_OPCODES.contains(prev.getOpcode());
            prev = prev.getPrevious()
         ) {
            if (!(prev instanceof FrameNode) && !(prev instanceof LineNumberNode) && !(prev instanceof LabelNode)) {
               insns.addFirst(prev);
            }
         }

         return insns;
      }

      private static List<AbstractInsnNode> findReturnInsns(MethodNode methodNode) {
         Builder<AbstractInsnNode> insns = ImmutableList.builder();

         for (AbstractInsnNode insn : methodNode.instructions) {
            if (RETURN_OPCODES.contains(insn.getOpcode())) {
               insns.add(insn);
            }
         }

         return insns.build();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Optional apply(MixinContext param1, TargetPair nullx, TargetPair nullxx, Object nullxxx) {
         return this.apply(var1, nullx, nullxx, (InjectorOrdinalResolver.OffsetUpdateHandler.Context)nullxxx);
      }
   }

   private interface UpdateHandler<T, U> {
      Optional<U> apply(MixinContext var1, TargetPair var2, TargetPair var3, T var4);
   }
}
