package org.sinytra.adapter.patch.processor.extract;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.locals.LocalVarAnalyzer;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.config.key.SpecialKeys;
import org.sinytra.adapter.patch.processor.Processor;
import org.sinytra.adapter.transform.param.TransformParameters;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.OpcodeUtil;

public class ExtractMixinProcessor implements Processor {
   @Override
   public TxResult process(MixinContext context, Configuration dirty, Recipe recipe) {
      if (recipe.clean().getTargetClass().equals(dirty.getTargetClass())) {
         return TxResult.PASS;
      }

      PatchResult result = apply(dirty.getTargetClass(), context, recipe);
      if (result == PatchResult.PASS && dirty.hasProperty(SpecialKeys.EXTRACT_TARGET)) {
         MethodInsnNode minsn = dirty.getProperty(SpecialKeys.EXTRACT_TARGET).orElseThrow();
         result = MirrorableExtractMixin.apply(context, recipe, dirty.getTargetClass(), minsn);
      }

      if (result == PatchResult.PASS && dirty.hasProperty(SpecialKeys.EXTRACT_ORIGIN_PARAM)) {
         return ProxyExtractMixinSub.INSTANCE.process(context, dirty, recipe);
      } else {
         return result == PatchResult.PASS ? TxResult.FAIL : TxResult.SUCCESS;
      }
   }

   public static PatchResult apply(String targetClassName, MixinContext context, Recipe recipe) {
      boolean isStatic = context.isStatic();
      ClassNode classNode = context.classNode();
      MethodNode methodNode = context.methodNode();
      TargetPair cleanTarget = recipe.getCleanTarget();
      String owner = Optional.ofNullable(cleanTarget).map(t -> t.classNode().name).orElse(targetClassName);
      boolean isInherited = context.environment().inheritanceHandler(context.dirtyLookup()).isClassInherited(targetClassName, owner);
      ExtractMixinProcessor.Candidates candidates = findCandidates(classNode, methodNode);
      if (!candidates.canMove(classNode, isInherited)) {
         return PatchResult.PASS;
      }

      ClassNode targetClass = context.environment().dirtyClassLookup().getClass(targetClassName).orElse(null);
      ClassNode generatedTarget = context.environment()
         .classGenerator()
         .getOrGenerateMixinClass(classNode, targetClassName, targetClass != null ? targetClass.superName : null);
      context.environment().refmapHolder().copyEntries(classNode.name, generatedTarget.name);

      for (MethodNode method : candidates.methods()) {
         generatedTarget.methods.add(method);
         updateOwnerRefereces(method, classNode, targetClassName);
         if (!isStatic && method.localVariables != null) {
            method.localVariables
               .stream()
               .filter(l -> l.index == 0)
               .findFirst()
               .ifPresent(lvn -> lvn.desc = Type.getObjectType(generatedTarget.name).getDescriptor());
         }
      }

      candidates.handleUpdates().forEach(c -> c.accept(generatedTarget));
      PatchResult result = PatchResult.PASS;
      boolean capturesLocals = recipe.clean().hasProperty(MixinKeys.LOCALS);
      if (capturesLocals) {
         result = result.or(recreateLocalVariables(methodNode, context, recipe, generatedTarget));
      }

      context.recordCtxAudit("Extract mixin to target %s", targetClassName);
      context.patchContext().postApply(() -> classNode.methods.removeAll(candidates.methods));
      return result.or(PatchResult.APPLY);
   }

   private static ExtractMixinProcessor.Candidates findCandidates(ClassNode classNode, MethodNode methodNode) {
      List<MethodNode> methods = new ArrayList<>();
      List<Consumer<ClassNode>> handleUpdates = new ArrayList<>();
      methods.add(methodNode);

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof InvokeDynamicInsnNode indy && indy.bsmArgs.length >= 3) {
            for (int i = 0; i < indy.bsmArgs.length; i++) {
               if (indy.bsmArgs[i] instanceof Handle handle
                  && handle.getOwner().equals(classNode.name)
                  && handle.getName().startsWith("lambda$" + methodNode.name)) {
                  int finalI = i;
                  classNode.methods
                     .stream()
                     .filter(m -> m.name.equals(handle.getName()) && m.desc.equals(handle.getDesc()))
                     .findFirst()
                     .ifPresent(
                        m -> {
                           methods.add(m);
                           handleUpdates.add(
                              t -> indy.bsmArgs[finalI] = new Handle(handle.getTag(), t.name, handle.getName(), handle.getDesc(), handle.isInterface())
                           );
                        }
                     );
               }
            }
         }
      }

      return new ExtractMixinProcessor.Candidates(methods, handleUpdates);
   }

   private static void updateOwnerRefereces(MethodNode methodNode, ClassNode originalClass, String targetClass) {
      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof MethodInsnNode minsn && minsn.owner.equals(originalClass.name)) {
            minsn.owner = targetClass;
         } else if (insn instanceof FieldInsnNode finsn && finsn.owner.equals(originalClass.name)) {
            finsn.owner = targetClass;
         }
      }
   }

   private static boolean isInheritedField(ClassNode cls, FieldInsnNode finsn, boolean isTargetInherited, List<Runnable> accessUpdates) {
      FieldNode field = cls.fields.stream().filter(f -> f.name.equals(finsn.name)).findFirst().orElse(null);
      if (field != null) {
         if (AdapterUtil.isShadowField(field)) {
            return true;
         }

         if (isTargetInherited) {
            accessUpdates.add(() -> field.access = fixAccess(field.access));
            return true;
         }
      }

      return false;
   }

   private static boolean isInheritedMethod(ClassNode cls, MethodInsnNode minsn, boolean isTargetInherited, List<Runnable> accessUpdates) {
      MethodNode method = cls.methods.stream().filter(m -> m.name.equals(minsn.name) && m.desc.equals(minsn.desc)).findFirst().orElse(null);
      if (method != null) {
         List<AnnotationNode> annotations = method.visibleAnnotations != null ? method.visibleAnnotations : List.of();
         if (AdapterUtil.hasAnnotation(annotations, "Lorg/spongepowered/asm/mixin/Shadow;")) {
            return true;
         }

         if (isTargetInherited) {
            accessUpdates.add(() -> method.access = fixAccess(method.access));
            return true;
         }
      }

      return false;
   }

   private static int fixAccess(int access) {
      int visibility = OpcodeUtil.getAccessVisibility(access);
      return visibility != 2 && visibility != 0 ? access : OpcodeUtil.setAccessVisibility(visibility, 4097);
   }

   private static PatchResult recreateLocalVariables(MethodNode methodNode, MixinContext context, Recipe recipe, ClassNode extractClass) {
      AdapterUtil.CapturedLocals capturedLocals = AdapterUtil.getCapturedLocals(context, recipe);
      if (capturedLocals == null) {
         return PatchResult.PASS;
      }

      LocalVariableLookup table = capturedLocals.lvt();
      int paramLocalStart = capturedLocals.paramLocalStart();
      LocalVarAnalyzer.CapturedLocalsTransform transform = LocalVarAnalyzer.analyzeCapturedLocals(capturedLocals, methodNode);
      LocalVarAnalyzer.CapturedLocalsUsage usage = transform.getUsage(capturedLocals);
      Collection<Integer> used = transform.used();
      LocalVariableLookup targetTable = usage.targetTable();
      Int2ObjectMap<InsnList> varInsnLists = usage.varInsnLists();
      Int2IntMap usageCount = usage.usageCount();
      PatchResult result = transform.remover().apply(context);
      if (result == PatchResult.PASS) {
         return PatchResult.PASS;
      }

      MethodNode copy = new MethodNode(2 | (capturedLocals.isStatic() ? 8 : 0), "adapter$bridge$" + methodNode.name, methodNode.desc, null, null);
      methodNode.accept(copy);
      copy.visibleAnnotations = null;
      copy.invisibleAnnotations = null;
      methodNode.instructions = new InsnList();
      TransformParameters cleanupPatch = TransformParameters.builder()
         .chain(b -> IntStream.range(paramLocalStart, paramLocalStart + used.size()).boxed().sorted(Collections.reverseOrder()).forEach(b::remove))
         .build();
      PatchResult cleanupResult = cleanupPatch.apply(context);
      if (cleanupResult == PatchResult.PASS) {
         return PatchResult.PASS;
      }

      InsnList replacementInsns = new InsnList();
      Type lastVar = Type.getType(table.getLast().desc);
      AtomicInteger nextAvailableIndex = new AtomicInteger(methodNode.localVariables.size() - 1 + AdapterUtil.getLVTOffsetForType(lastVar));
      usageCount.forEach((index, count) -> {
         if (count == 1) {
            int usages = 0;
            ObjectIterator var4x = varInsnLists.int2ObjectEntrySet().iterator();

            while (var4x.hasNext()) {
               Entry<InsnList> entry = (Entry<InsnList>)var4x.next();

               for (AbstractInsnNode insn : (InsnList)entry.getValue()) {
                  if (insn instanceof VarInsnNode varInsn && varInsn.var == index) {
                     InsnList varInitializers = (InsnList)varInsnLists.get(varInsn.var);
                     ((InsnList)entry.getValue()).insert(varInsn, varInitializers);
                     ((InsnList)entry.getValue()).remove(varInsn);
                     usages++;
                  }
               }
            }

            if (usages > 1) {
               throw new IllegalStateException("Expected only one reference to variable " + index);
            }
         }
      });
      LabelNode end = new LabelNode();
      Map<Integer, Integer> newIndices = new HashMap<>();
      usageCount.forEach((index, count) -> {
         if (count > 1) {
            LocalVariableNode node = targetTable.getByIndex(index);
            Type typex = Type.getType(node.desc);
            int newIndex = nextAvailableIndex.getAndAdd(AdapterUtil.getLVTOffsetForType(typex));
            LabelNode start = new LabelNode();
            methodNode.localVariables.add(new LocalVariableNode(node.name, node.desc, node.signature, start, end, newIndex));
            InsnList insns = (InsnList)varInsnLists.get(index);
            replacementInsns.add(insns);
            replacementInsns.add(new VarInsnNode(OpcodeUtil.getStoreOpcode(typex.getSort()), newIndex));
            replacementInsns.add(start);
            varInsnLists.remove(index);
            varInsnLists.forEach((varIndex, varInsns) -> {
               for (AbstractInsnNode insn : varInsns) {
                  if (insn instanceof VarInsnNode varInsn && varInsn.var == index) {
                     varInsn.var = newIndex;
                  }
               }
            });
            newIndices.put(index, newIndex);
         }
      });

      for (int i = 0; i < paramLocalStart + 1; i++) {
         Type type = Type.getType(table.getByIndex(i).desc);
         replacementInsns.add(AdapterUtil.loadType(type, i));
      }

      used.forEach(ordinal -> {
         LocalVariableNode node = targetTable.getByOrdinal(ordinal);
         InsnList insns = (InsnList)varInsnLists.get(node.index);
         if (insns != null) {
            replacementInsns.add(insns);
         } else {
            Type typex = Type.getType(node.desc);
            int newIndex = newIndices.getOrDefault(node.index, -1);
            if (newIndex == -1) {
               throw new IllegalArgumentException("Missing new index for var " + node.index);
            }

            replacementInsns.add(new VarInsnNode(OpcodeUtil.getLoadOpcode(typex.getSort()), newIndex));
         }
      });
      replacementInsns.add(new MethodInsnNode(capturedLocals.isStatic() ? 184 : 182, extractClass.name, copy.name, copy.desc));
      replacementInsns.add(new LabelNode());
      replacementInsns.add(new InsnNode(OpcodeUtil.getReturnOpcode(methodNode)));
      replacementInsns.add(end);
      methodNode.instructions = replacementInsns;
      extractClass.methods.add(copy);
      return PatchResult.COMPUTE_FRAMES;
   }

   record Candidates(List<MethodNode> methods, List<Consumer<ClassNode>> handleUpdates) {
      public boolean canMove(ClassNode classNode, boolean isInherited) {
         List<Runnable> accessFixes = new ArrayList<>();

         for (MethodNode methodNode : this.methods) {
            for (AbstractInsnNode insn : methodNode.instructions) {
               if (insn instanceof FieldInsnNode finsn
                     && finsn.owner.equals(classNode.name)
                     && !ExtractMixinProcessor.isInheritedField(classNode, finsn, isInherited, accessFixes)
                  || insn instanceof MethodInsnNode minsn
                     && minsn.owner.equals(classNode.name)
                     && !ExtractMixinProcessor.isInheritedMethod(classNode, minsn, isInherited, accessFixes)) {
                  return false;
               }
            }
         }

         accessFixes.forEach(Runnable::run);
         return true;
      }
   }
}
