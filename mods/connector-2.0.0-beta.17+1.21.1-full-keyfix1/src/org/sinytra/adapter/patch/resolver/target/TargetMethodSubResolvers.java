package org.sinytra.adapter.patch.resolver.target;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.method.MethodAnalyzer;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.resolver.Resolver;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.patch.resolver.injection.InjectionPointResolver;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;

public class TargetMethodSubResolvers {
   public static final SubResolver CHANGED_LAMBDA_INDEX = (context, recipe) -> {
      MethodQualifier cleanQualifier = recipe.clean().getTargetMethod();
      if (!MethodAnalyzer.isLambda(cleanQualifier)) {
         return null;
      }

      ClassNode targetClass = context.dirtyLookup().getClass(recipe.clean().getTargetClass()).orElse(null);
      if (targetClass == null) {
         return null;
      }

      List<MethodNode> candidateMethods = targetClass.methods.stream().filter(m -> MethodAnalyzer.isLambda(m) && m.desc.equals(cleanQualifier.desc())).toList();
      if (candidateMethods.size() != 1) {
         return null;
      }

      MethodNode newTarget = candidateMethods.getFirst();
      return MutableConfiguration.create().setTargetMethod(newTarget);
   };
   public static final SubResolver CHANGED_METHOD_PARAMS = (context, recipe) -> {
      Configuration overloaded = resolveOverloadedReplacement(context, recipe);
      if (overloaded != null) {
         return overloaded;
      } else {
         MethodQualifier cleanQualifier = recipe.clean().getTargetMethod();
         Pair<ClassNode, List<MethodNode>> candidates = context.methods().findOwnMethodsByName(context.dirtyLookup(), cleanQualifier);
         if (candidates == null) {
            return null;
         } else {
            Configuration resolved = resolveReplacementCandidate(context, recipe, (List<MethodNode>)candidates.getSecond());
            if (resolved == null) {
               return null;
            } else {
               return resolved.getTargetMethod() != null && !resolved.getTargetMethod().matches(cleanQualifier) ? resolved : null;
            }
         }
      }
   };
   public static final SubResolver MOVED_INTO_LAMBDA = (context, recipe) -> {
      MethodQualifier cleanQualifier = recipe.clean().getTargetMethod();
      TargetPair target = context.methods().findOwnMethodPair(context.dirtyLookup(), cleanQualifier);
      if (target == null) {
         return null;
      }

      for (AbstractInsnNode insn : target.methodNode().instructions) {
         if (insn instanceof InvokeDynamicInsnNode indy && indy.bsmArgs.length > 1 && indy.bsmArgs[1] instanceof Handle handle) {
            TargetPair lambda = MethodQualifier.parse(handle.getName()).map(q -> context.methods().findOwnMethodPair(context.dirtyLookup(), q)).orElse(null);
            if (lambda == null) {
               return null;
            }

            if (!context.methods().findInjectionTargetInsns(lambda).isEmpty()) {
               return MutableConfiguration.create().setTargetMethod(lambda.methodNode());
            }
         }
      }

      return null;
   };

   @Nullable
   private static Configuration resolveOverloadedReplacement(MixinContext context, Recipe recipe) {
      TargetPair dirtyTarget = recipe.getNewCleanTarget();
      if (dirtyTarget != null && AdapterUtil.isDeprecated(dirtyTarget.methodNode())) {
         for (MethodNode invocation : MethodAnalyzer.getOwnMethodCalls(dirtyTarget)) {
            if (context.methods().hasInjectionTargetInsns(new TargetPair(dirtyTarget.classNode(), invocation))) {
               return MutableConfiguration.create().setTargetMethod(invocation);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Nullable
   private static Configuration resolveReplacementCandidate(MixinContext context, Recipe recipe, List<MethodNode> methods) {
      if (methods.size() == 1) {
         return MutableConfiguration.create().setTargetMethod(methods.getFirst());
      } else {
         Resolver resolver = recipe.resolvers().get(InjectionPointResolver.class);
         List<Pair<MethodNode, Configuration>> valid = methods.stream()
            .sorted(Comparator.<MethodNode>comparingInt(m -> Parameters.getParameterTypes(m.desc).size()).reversed())
            .flatMap(m -> {
               Configuration dirtyCopy = recipe.dirty().copy().setTargetMethod(m);
               context.pushAudit(resolver);
               Resolver.ResolutionResult result = resolver.resolve(context, recipe.withDirtyConfig(dirtyCopy));
               context.popAudit();
               return result.maybePatch().stream().map(c -> Pair.of(m, c.copyClean().setTargetMethod(m)));
            })
            .toList();
         if (valid.size() == 1) {
            return (Configuration)valid.getFirst().getSecond();
         } else {
            List<MethodNode> nonDeprecated = valid.stream().<MethodNode>map(Pair::getFirst).filter(m -> !isDirtyDeprecatedMethod(context, m)).toList();
            if (nonDeprecated.size() == 1) {
               return MutableConfiguration.create().setTargetMethod(nonDeprecated.getFirst());
            } else {
               return !valid.isEmpty() ? (Configuration)valid.getFirst().getSecond() : null;
            }
         }
      }
   }

   public static boolean isDirtyDeprecatedMethod(MixinContext context, MethodNode dirty) {
      TargetPair pair = context.methods().findOwnMethodPair(context.cleanLookup(), MethodQualifier.create(dirty));
      return (pair == null || !AdapterUtil.isDeprecated(pair.methodNode())) && !AdapterUtil.isDeprecated(dirty);
   }
}
