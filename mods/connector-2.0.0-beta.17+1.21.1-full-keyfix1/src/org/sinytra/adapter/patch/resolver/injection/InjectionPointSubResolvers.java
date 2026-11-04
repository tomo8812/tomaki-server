package org.sinytra.adapter.patch.resolver.injection;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.InstructionMatcher;
import org.sinytra.adapter.analysis.method.MethodAnalyzer;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.analysis.method.MethodInsnMatcher;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.env.util.WeighedDisambiguation;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.config.key.SpecialKeys;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.util.MethodQualifier;

public class InjectionPointSubResolvers {
   public static final SubResolver REPLACED_TYPE = (context, recipe) -> {
      TargetPair cleanPair = recipe.getCleanTarget();
      TargetPair dirtyTarget = recipe.getDirtyTarget();
      if (cleanPair != null && dirtyTarget != null) {
         List<AbstractInsnNode> insns = context.methods().findInjectionTargetInsns(cleanPair);
         if (!insns.isEmpty() && insns.getFirst() instanceof MethodInsnNode cleanInsn) {
            InstructionMatcher var11 = MethodInsnMatcher.findSurroundingInstructions(cleanInsn);
            Multimap<String, MethodInsnNode> dirtyCalls = MethodAnalyzer.getMethodCalls(dirtyTarget.methodNode());
            List dirtyMatchers = dirtyCalls.values().stream().map(MethodInsnMatcher::findSurroundingInstructions).toList();
            WeighedDisambiguation<MethodQualifier> magicBlackBox = WeighedDisambiguation.<MethodQualifier>builder()
               .match(() -> testMatchers(context, cleanInsn, var11, dirtyMatchers, false))
               .match(() -> testMatchers(context, cleanInsn, var11, dirtyMatchers, true))
               .match(() -> testOverloadedMethods(context, cleanInsn, cleanPair, dirtyTarget))
               .resultsEqual(MethodQualifier::equals)
               .build();
            MethodQualifier replacement = magicBlackBox.findBestMatch();
            return replacement != null ? MutableConfiguration.create().setAtData(recipe.clean().getAtData().withTarget(replacement)) : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   };
   public static final SubResolver EXTRACTED_CALL = (context, recipe) -> {
      if (context.isStatic()) {
         return null;
      }

      TargetPair cleanPair = recipe.getCleanTarget();
      if (cleanPair == null) {
         return null;
      }

      TargetPair dirtyTarget = recipe.getDirtyTarget();
      if (dirtyTarget == null) {
         return null;
      }

      Multimap<String, MethodInsnNode> cleanCalls = MethodAnalyzer.getMethodCalls(cleanPair.methodNode());
      Multimap<String, MethodInsnNode> dirtyCalls = MethodAnalyzer.getMethodCalls(dirtyTarget.methodNode());
      Set<String> dirtyOnly = new HashSet<>(dirtyCalls.keySet());
      dirtyOnly.removeAll(cleanCalls.keySet());

      for (String qualifier : dirtyOnly) {
         Collection<MethodInsnNode> calls = dirtyCalls.get(qualifier);
         if (calls.size() == 1) {
            MethodInsnNode minsn = calls.iterator().next();
            if (minsn.getOpcode() == 184 && !minsn.owner.equals(dirtyTarget.classNode().name)) {
               List<AbstractInsnNode> argInsns = MethodCallAnalyzer.getMethodCallSrcInsns(dirtyTarget.methodNode(), minsn);
               AbstractInsnNode self = argInsns.stream().filter(i -> i instanceof VarInsnNode vinsn && vinsn.var == 0).findFirst().orElse(null);
               if (self != null) {
                  int selfParamIndex = argInsns.indexOf(self);
                  MethodQualifier target = MethodQualifier.create(minsn);
                  TargetPair pair = context.methods().findMethodPair(context.dirtyLookup(), target);
                  if (pair != null && context.methods().hasInjectionTargetInsns(pair)) {
                     return recipe.dirty()
                        .copyClean()
                        .setTargetClass(minsn.owner)
                        .setTargetMethod(minsn)
                        .inheritAtData()
                        .setProperty(SpecialKeys.EXTRACT_ORIGIN_PARAM, selfParamIndex)
                        .inheritProperyIfAbsent(MixinKeys.INDEX)
                        .inheritProperyIfAbsent(MixinKeys.ORDINAL);
                  }
               }
            }
         }
      }

      return null;
   };

   private static List<MethodQualifier> testMatchers(
      MixinContext context, MethodInsnNode cleanInsn, InstructionMatcher cleanMatcher, List<InstructionMatcher> dirtyMatchers, boolean partial
   ) {
      return dirtyMatchers.stream().map(m -> {
         boolean before = cleanMatcher.testBefore(m);
         boolean after = cleanMatcher.testAfter(m);
         boolean match = partial ? before || after : before && after;
         MethodInsnNode dirtyInsn = (MethodInsnNode)m.insn();
         return match && matchesMethodCall(context, cleanInsn, dirtyInsn) ? dirtyInsn : null;
      }).filter(Objects::nonNull).map(MethodQualifier::create).toList();
   }

   private static List<MethodQualifier> testOverloadedMethods(MixinContext context, MethodInsnNode cleanInsn, TargetPair cleanPair, TargetPair dirtyPair) {
      ClassNode dirtyClass = context.dirtyLookup().getClass(cleanInsn.owner).orElse(null);
      if (dirtyClass == null) {
         return List.of();
      }

      List<Type> cleanParams = Parameters.getParameterTypes(cleanInsn.desc);
      List<MethodNode> methods = dirtyClass.methods.stream().filter(m -> {
         if (cleanPair.classNode().methods.stream().noneMatch(c -> c.name.equals(m.name) && c.desc.equals(m.desc)) && m.name.equals(cleanInsn.name)) {
            List<Type> dirtyParams = Parameters.getParameterTypes(m.desc);
            return dirtyParams.size() > cleanParams.size() && dirtyParams.subList(0, cleanParams.size()).equals(cleanParams);
         } else {
            return false;
         }
      }).filter(m -> MethodAnalyzer.containsMethodCall(dirtyPair.methodNode(), MethodQualifier.create(m))).toList();
      return methods.size() == 1 ? List.of(MethodQualifier.create(dirtyClass, methods.getFirst())) : List.of();
   }

   private static boolean matchesMethodCall(MixinContext context, MethodInsnNode cleanInsn, MethodInsnNode dirtyInsn) {
      return cleanInsn.owner.equals(dirtyInsn.owner) && cleanInsn.name.equals(dirtyInsn.name)
         || context.getTypeAdapter(Type.getObjectType(dirtyInsn.owner), Type.getObjectType(cleanInsn.owner)) != null
            && Type.getArgumentTypes(cleanInsn.desc).length == Type.getArgumentTypes(dirtyInsn.desc).length;
   }
}
