package org.sinytra.adapter.patch.resolver.injection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.InstructionMatcher;
import org.sinytra.adapter.analysis.method.MethodInsnMatcher;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.mixin.MixinFlag;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;

public class ArbitraryInjectionPointSubResolver implements SubResolver {
   @Nullable
   @Override
   public Configuration resolve(MixinContext context, Recipe recipe) {
      String injectionPointTarget = recipe.clean().getAtData().getTarget().orElse(null);
      TargetPair cleanTarget = recipe.getCleanTarget();
      TargetPair dirtyTarget = recipe.getDirtyTarget();
      AbstractInsnNode cleanInsn = context.methods().findInjectionTargetInsn(cleanTarget);
      if (cleanInsn == null) {
         return null;
      }

      MethodInsnNode targetMethodCall = this.resolveTargetCallInsn(dirtyTarget.methodNode(), cleanInsn, context, injectionPointTarget);
      if (targetMethodCall == null) {
         return null;
      }

      if (targetMethodCall.owner.equals(dirtyTarget.classNode().name)) {
         TargetPair target = context.methods().findOwnMethodPair(context.dirtyLookup(), MethodQualifier.create(targetMethodCall));
         if (target == null) {
            return null;
         }

         if (context.methods().hasInjectionTargetInsns(target)) {
            return MutableConfiguration.create().setTargetMethod(targetMethodCall);
         }
      }

      return MutableConfiguration.create().setAtData(AtData.create("INVOKE", targetMethodCall));
   }

   private MethodInsnNode resolveTargetCallInsn(
      MethodNode dirtyTargetMethod, AbstractInsnNode cleanInjectionInsn, MixinContext context, @Nullable String injectionPointTarget
   ) {
      AbstractInsnNode nextCallCandidate = findCandidates(
         MethodInsnMatcher.findBackwardsInstructions(cleanInjectionInsn).inverse(), dirtyTargetMethod, List::getLast
      );
      if (nextCallCandidate != null) {
         return findReplacementInjectionPoint(nextCallCandidate, AbstractInsnNode::getNext, context, injectionPointTarget);
      }

      AbstractInsnNode previousCallCandidate = findCandidates(MethodInsnMatcher.findForwardInstructions(cleanInjectionInsn), dirtyTargetMethod, List::getFirst);
      return previousCallCandidate != null
         ? findReplacementInjectionPoint(previousCallCandidate, AbstractInsnNode::getPrevious, context, injectionPointTarget)
         : null;
   }

   private static AbstractInsnNode findCandidates(
      InstructionMatcher cleanMatcher, MethodNode dirtyTargetMethod, Function<List<AbstractInsnNode>, AbstractInsnNode> selector
   ) {
      if (cleanMatcher.after().isEmpty()) {
         return null;
      }

      int firstOpcode = cleanMatcher.after().getFirst().getOpcode();
      List<AbstractInsnNode> candidates = new ArrayList<>();

      for (AbstractInsnNode insn : dirtyTargetMethod.instructions) {
         if (!(insn instanceof FrameNode) && !(insn instanceof LineNumberNode) && insn.getOpcode() == firstOpcode) {
            InstructionMatcher dirtyMatcher = MethodInsnMatcher.findForwardInstructionsDirect(insn);
            if (cleanMatcher.test(dirtyMatcher, 1)) {
               AbstractInsnNode lastInsn = selector.apply(dirtyMatcher.after());
               if (lastInsn != null) {
                  candidates.add(lastInsn);
               }
            }
         }
      }

      return !candidates.isEmpty() ? candidates.getFirst() : null;
   }

   private static MethodInsnNode findReplacementInjectionPoint(
      AbstractInsnNode lastInsn, UnaryOperator<AbstractInsnNode> flow, MixinContext context, @Nullable String injectionPointTarget
   ) {
      if (context.hasFlag(MixinFlag.RETURN_TYPE_SENSITIVE) && injectionPointTarget != null) {
         Type desiredReturnType = Type.getReturnType(injectionPointTarget);
         return (MethodInsnNode)AdapterUtil.iterateInsns(
            lastInsn, flow, v -> v instanceof MethodInsnNode minsn && Type.getReturnType(minsn.desc).equals(desiredReturnType)
         );
      } else {
         return (MethodInsnNode)AdapterUtil.iterateInsns(lastInsn, flow, v -> v instanceof MethodInsnNode);
      }
   }
}
