package org.sinytra.adapter.patch.resolver.target;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.InstructionMatcher;
import org.sinytra.adapter.analysis.method.MethodAnalyzer;
import org.sinytra.adapter.analysis.method.MethodInsnMatcher;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.util.MethodQualifier;

public class SplitTargetMethodSubResolver implements SubResolver {
   @Override
   public Configuration resolve(MixinContext context, Recipe recipe) {
      TargetPair cleanTarget = recipe.getCleanTarget();
      TargetPair dirtyTarget = context.methods().findOwnMethodPair(context.dirtyLookup(), recipe.clean().getTargetMethod());
      if (cleanTarget != null && dirtyTarget != null) {
         List<SplitTargetMethodSubResolver.CandidateMethod> candidates = disambiguate(locateCandidates(context, cleanTarget, dirtyTarget), context, cleanTarget);
         if (candidates.size() == 1) {
            MethodNode method = candidates.getFirst().method();
            context.recordCtxAudit("Adjusting split method target to %s", MethodQualifier.create(method).asDescriptor());
            if (recipe.clean().isCancellable()) {
               SplitMethodCancellationHelper.handle(context, recipe, method);
            }

            return MutableConfiguration.create().setTargetMethod(method);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static List<SplitTargetMethodSubResolver.CandidateMethod> locateCandidates(MixinContext context, TargetPair cleanTarget, TargetPair dirtyTarget) {
      MethodNode cleanTargetMethod = cleanTarget.methodNode();
      ClassNode dirtyTargetClass = dirtyTarget.classNode();
      MethodNode dirtyTargetMethod = dirtyTarget.methodNode();
      if (!MethodAnalyzer.isDirtyDeprecatedMethod(cleanTargetMethod, dirtyTargetMethod)) {
         return tryFindPartialCandidates(cleanTargetMethod, dirtyTargetClass, dirtyTargetMethod, context);
      } else {
         List<MethodNode> invocations = MethodAnalyzer.getOwnMethodCalls(dirtyTarget);
         if (invocations == null) {
            return List.of();
         } else {
            List<SplitTargetMethodSubResolver.CandidateMethod> candidates = findInsnsCalls(
               invocations.stream().map(m -> new TargetPair(dirtyTargetClass, m)).toList(), context
            );
            if (candidates.isEmpty()) {
               List<TargetPair> nestedLambdas = invocations.stream()
                  .flatMap(m -> MethodAnalyzer.findLambdasInMethod(dirtyTarget.classNode(), m, null).stream())
                  .flatMap(s -> MethodQualifier.parse(s).stream())
                  .map(s -> context.methods().findOwnMethodPair(context.dirtyLookup(), s))
                  .filter(Objects::nonNull)
                  .toList();
               return findInsnsCalls(nestedLambdas, context);
            } else {
               return candidates;
            }
         }
      }
   }

   private static List<SplitTargetMethodSubResolver.CandidateMethod> tryFindPartialCandidates(
      MethodNode cleanTargetMethod, ClassNode dirtyTargetClass, MethodNode dirtyTargetMethod, MixinContext context
   ) {
      Multimap<String, MethodInsnNode> cleanMethodCalls = MethodAnalyzer.getMethodCalls(cleanTargetMethod);
      Multimap<String, MethodInsnNode> dirtyMethodCalls = MethodAnalyzer.getMethodCalls(dirtyTargetMethod);
      List<TargetPair> dirtyOnlyCalls = dirtyMethodCalls.entries()
         .stream()
         .filter(e -> !cleanMethodCalls.containsKey(e.getKey()) && ((MethodInsnNode)e.getValue()).owner.equals(dirtyTargetClass.name))
         .map(Entry::getValue)
         .map(i -> context.methods().findOwnMethodPair(context.dirtyLookup(), MethodQualifier.create(i)))
         .filter(Objects::nonNull)
         .toList();
      return findInsnsCalls(dirtyOnlyCalls, context);
   }

   private static List<SplitTargetMethodSubResolver.CandidateMethod> disambiguate(
      List<SplitTargetMethodSubResolver.CandidateMethod> candidates, MixinContext context, TargetPair cleanTarget
   ) {
      if (candidates.size() <= 1) {
         return candidates;
      }

      List<AbstractInsnNode> cleanInsns = context.methods().findInjectionTargetInsns(cleanTarget);
      if (cleanInsns.size() != 1) {
         return candidates;
      }

      InstructionMatcher cleanMatcher = MethodInsnMatcher.findSurroundingInstructions(cleanInsns.getFirst(), 5);
      List<SplitTargetMethodSubResolver.CandidateMethod> matchingCandidates = candidates.stream()
         .filter(method -> method.insns().size() == 1)
         .filter(method -> {
            InstructionMatcher matcher = MethodInsnMatcher.findSurroundingInstructions(method.insns().getFirst(), 5);
            return cleanMatcher.test(matcher, 1);
         })
         .toList();
      return matchingCandidates.size() == 1 ? matchingCandidates : candidates;
   }

   private static List<SplitTargetMethodSubResolver.CandidateMethod> findInsnsCalls(List<TargetPair> methods, MixinContext context) {
      return methods.stream().map(pair -> {
         List<AbstractInsnNode> insns = context.methods().findInjectionTargetInsns(pair);
         return !insns.isEmpty() ? new SplitTargetMethodSubResolver.CandidateMethod(pair.methodNode(), insns) : null;
      }).filter(Objects::nonNull).toList();
   }

   private record CandidateMethod(MethodNode method, List<AbstractInsnNode> insns) {
   }
}
