package org.sinytra.adapter.patch.resolver.special;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.sinytra.adapter.analysis.InstructionMatcher;
import org.sinytra.adapter.analysis.method.MethodInsnMatcher;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.Configurations;
import org.sinytra.adapter.patch.resolver.Resolver;

public record ResolverSyntheticInstanceof(boolean skipInsnComparison) implements Resolver {
   @Override
   public Resolver.ResolutionResult resolve(MixinContext context, Recipe recipe) {
      if (!recipe.hasInjectionPointValue("INVOKE")) {
         return Resolver.ResolutionResult.pass();
      }

      TargetPair cleanTarget = recipe.getCleanTarget();
      TargetPair dirtyTarget = recipe.getDirtyTarget();
      AbstractInsnNode targetInsn = context.methods().findInjectionTargetInsn(cleanTarget);
      if (targetInsn == null) {
         return Resolver.ResolutionResult.pass();
      }

      List<AbstractInsnNode> labelInsns = findLabelInsns(targetInsn);
      AbstractInsnNode jumpInsn = labelInsns.getLast();
      if (!(jumpInsn instanceof JumpInsnNode)) {
         return Resolver.ResolutionResult.pass();
      }

      InstructionMatcher cleanMatcher = MethodInsnMatcher.findForwardInstructions(targetInsn);
      int firstOp = cleanMatcher.after().getFirst().getOpcode();
      InsnList dirtyInsns = dirtyTarget.methodNode().instructions;

      for (AbstractInsnNode insn : dirtyInsns) {
         if (insn.getOpcode() == firstOp) {
            AbstractInsnNode nextLabel = findInsnAfterLabel(insn);
            InstructionMatcher dirtyMatcher = MethodInsnMatcher.findForwardInstructions(nextLabel);
            if (cleanMatcher.test(dirtyMatcher)) {
               if (this.skipInsnComparison) {
                  Type handlerReturnType = Type.getReturnType(context.methodNode().desc);
                  if (!handlerReturnType.equals(Type.BOOLEAN_TYPE)) {
                     return Resolver.ResolutionResult.pass();
                  }

                  TypeInsnNode instanceOfInsn = (TypeInsnNode)findLabelInsns(insn).stream().filter(i -> i.getOpcode() == 193).findFirst().orElse(null);
                  if (instanceOfInsn == null) {
                     return Resolver.ResolutionResult.pass();
                  }

                  int ordinal = getInstanceofOrdinal(dirtyInsns, instanceOfInsn);
                  Configuration config = recipe.dirty()
                     .copyClean()
                     .setMixinType("Lorg/sinytra/adapter/runtime/inject/ModifyInstanceofValue;")
                     .inheritTargetClass()
                     .inheritTargetMethod()
                     .setAtData(AtData.builder("sinytra:INSTANCEOF").target(instanceOfInsn.desc).ordinal(ordinal != 0 ? ordinal : null).build())
                     .inheritParameters()
                     .inheritReturnType();
                  return Resolver.ResolutionResult.replace(config);
               }

               List<AbstractInsnNode> dirtyLabelInsns = findLabelInsns(insn);
               List<AbstractInsnNode> modLabelInsns = new ArrayList<>();

               for (AbstractInsnNode ins : context.methodNode().instructions) {
                  if (!(ins instanceof LineNumberNode) && !(ins instanceof FrameNode)) {
                     modLabelInsns.add(ins.clone(Map.of()));
                  }
               }

               modLabelInsns.removeFirst();
               modLabelInsns.removeLast();
               dirtyLabelInsns.removeLast();
               modLabelInsns.removeLast();
               InstructionMatcher finalCleanMatcher = new InstructionMatcher(null, dirtyLabelInsns, List.of());
               InstructionMatcher finalDirtyMatcher = new InstructionMatcher(null, modLabelInsns, List.of());
               if (finalCleanMatcher.test(finalDirtyMatcher, 1)) {
                  return Resolver.ResolutionResult.replace(Configurations.DELETE);
               }
            }
         }
      }

      return Resolver.ResolutionResult.pass();
   }

   private static AbstractInsnNode findInsnAfterLabel(AbstractInsnNode insn) {
      AbstractInsnNode next = insn.getNext();

      while (next != null && !(next instanceof LabelNode)) {
         next = next.getNext();
      }

      return next;
   }

   private static List<AbstractInsnNode> findLabelInsns(AbstractInsnNode insn) {
      List<AbstractInsnNode> list = new ArrayList<>();
      if (!(insn instanceof LabelNode)) {
         for (AbstractInsnNode prev = insn.getPrevious(); prev != null && !(prev instanceof LabelNode); prev = prev.getPrevious()) {
            if (!(prev instanceof FrameNode) && !(prev instanceof LineNumberNode)) {
               list.add(prev);
            }
         }
      }

      for (AbstractInsnNode next = insn.getNext(); next != null && !(next instanceof LabelNode); next = next.getNext()) {
         if (!(next instanceof FrameNode) && !(next instanceof LineNumberNode)) {
            list.add(next);
         }
      }

      return list;
   }

   private static int getInstanceofOrdinal(InsnList insns, AbstractInsnNode insn) {
      List<AbstractInsnNode> instanceOfInsns = new ArrayList<>();

      for (AbstractInsnNode node : insns) {
         if (node.getOpcode() == 193) {
            instanceOfInsns.add(insn);
         }
      }

      return instanceOfInsns.indexOf(insn);
   }
}
