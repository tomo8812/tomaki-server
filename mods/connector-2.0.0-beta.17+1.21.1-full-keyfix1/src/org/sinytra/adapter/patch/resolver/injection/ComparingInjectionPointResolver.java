package org.sinytra.adapter.patch.resolver.injection;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.MethodLabelComparator;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ann.ConstantData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.env.param.Parameter;
import org.sinytra.adapter.env.util.TypeConstants;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.config.key.SpecialKeys;
import org.sinytra.adapter.patch.processor.wrapop.WrapOpSurgeon;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.util.MethodQualifier;

public abstract class ComparingInjectionPointResolver implements SubResolver {
   @Nullable
   protected AbstractInsnNode prepare(MixinContext context, Recipe recipe) {
      TargetPair dirtyTarget = recipe.getDirtyTarget();
      if (dirtyTarget == null) {
         return null;
      }

      TargetPair cleanTarget = recipe.getCleanTarget();
      return cleanTarget == null ? null : context.methods().findInjectionTargetInsn(cleanTarget);
   }

   public static class Inject extends ComparingInjectionPointResolver {
      @Nullable
      @Override
      public Configuration resolve(MixinContext context, Recipe recipe) {
         TargetPair cleanTarget = recipe.getCleanTarget();
         if (cleanTarget == null) {
            return null;
         }

         AbstractInsnNode cleanInsn = this.prepare(context, recipe);
         MethodLabelComparator.ComparisonResult comparisonResult = MethodLabelComparator.findPatchedLabels(cleanInsn, recipe);
         if (comparisonResult == null) {
            return null;
         }

         List<List<AbstractInsnNode>> hunkLabels = comparisonResult.patchedLabels();
         ClassNode cleanTargetClass = cleanTarget.classNode();

         for (List<AbstractInsnNode> insns : hunkLabels) {
            for (AbstractInsnNode insn : insns) {
               if (insn instanceof MethodInsnNode minsn && minsn.getOpcode() == 184 && !minsn.owner.equals(cleanTargetClass.name)) {
                  Configuration result = attemptExtractMixin(minsn, context, recipe.clean());
                  if (result != null) {
                     return result;
                  }
               }
            }
         }

         for (List<AbstractInsnNode> insns : hunkLabels) {
            for (AbstractInsnNode insn : insns) {
               if (insn instanceof MethodInsnNode minsn) {
                  return MutableConfiguration.create().setAtData(AtData.create("INVOKE", minsn));
               }
            }
         }

         return null;
      }

      private static Configuration attemptExtractMixin(MethodInsnNode minsn, MixinContext context, Configuration clean) {
         TargetPair target = context.methods().findMethodPair(context.dirtyLookup(), MethodQualifier.create(minsn));
         if (target == null) {
            return null;
         }

         List<AbstractInsnNode> newTargetInsns = context.methods().findInjectionTargetInsns(target, true);
         return newTargetInsns.isEmpty()
            ? null
            : MutableConfiguration.create()
               .setTargetClass(target.classNode().name)
               .setTargetMethod(minsn)
               .setAtData(clean.getAtData().withOrdinal(null))
               .setProperty(SpecialKeys.EXTRACT_TARGET, minsn);
      }
   }

   public static class WrapOperation extends ComparingInjectionPointResolver {
      @Nullable
      @Override
      public Configuration resolve(MixinContext context, Recipe recipe) {
         TargetPair dirtyTarget = recipe.getDirtyTarget();
         if (dirtyTarget == null) {
            return null;
         }

         AbstractInsnNode cleanInsn = this.prepare(context, recipe);
         if (cleanInsn instanceof MethodInsnNode minsn) {
            MethodLabelComparator.ComparisonResult comparisonResult = MethodLabelComparator.findPatchedLabels(cleanInsn, recipe);
            if (comparisonResult == null) {
               return null;
            }

            List<List<AbstractInsnNode>> hunkLabels = comparisonResult.patchedLabels();
            return handleWrapOperationToInstanceOf(recipe, minsn, comparisonResult.cleanLabel(), hunkLabels, context)
               .or(() -> handleWrapOperationAdaptedTarget(hunkLabels))
               .or(() -> handleWrapOperationNewInjectionPoint(minsn, comparisonResult.cleanLabel(), hunkLabels))
               .or(() -> handleTargetModification(hunkLabels, dirtyTarget, context))
               .orElse(null);
         } else {
            return null;
         }
      }

      private static Optional<Configuration> handleWrapOperationToInstanceOf(
         Recipe recipe, MethodInsnNode cleanInjectionInsn, List<AbstractInsnNode> cleanLabel, List<List<AbstractInsnNode>> hunkLabels, MixinContext context
      ) {
         if (hunkLabels.size() == 1 && cleanLabel.getLast() instanceof JumpInsnNode && !cleanLabel.stream().anyMatch(i -> i instanceof TypeInsnNode)) {
            List<AbstractInsnNode> dirtyLabel = hunkLabels.getFirst();
            if (!(dirtyLabel.getLast() instanceof JumpInsnNode)) {
               return Optional.empty();
            }

            List<TypeInsnNode> instanceOfCalls = dirtyLabel.stream().filter(TypeInsnNode.class::isInstance).map(TypeInsnNode.class::cast).toList();
            if (instanceOfCalls.size() != 1) {
               return Optional.empty();
            }

            TypeInsnNode instanceOfCall = instanceOfCalls.getFirst();
            MethodNode methodNode = context.methodNode();
            LocalVariableLookup mixinLocals = new LocalVariableLookup(methodNode);
            LocalVariableNode instanceLocal = mixinLocals.getByParameterOrdinal(0);
            Configuration clean = recipe.clean();
            List<Type> inheritedParams = clean.getParameters().getTypes(MethodParameters.ParamGroup.METHOD_PARAMS);
            Multimap<Integer, VarInsnNode> usedVars = WrapOpSurgeon.getUsedVars(mixinLocals, inheritedParams, context);
            MutableConfiguration config = recipe.dirty()
               .copyClean()
               .removeProperty(MixinKeys.TARGET_AT)
               .setProperty(MixinKeys.TARGET_CONSTANT, ConstantData.classValue(Type.getObjectType(instanceOfCall.desc)))
               .inheritParameters()
               .inheritReturnType();
            MethodParameters parameters = config.getParameters();
            Parameter newInstanceParam = Parameter.simple(TypeConstants.OBJECT_TYPE);
            parameters.set(MethodParameters.ParamGroup.METHOD_PARAMS, List.of(newInstanceParam));
            if (usedVars.containsKey(instanceLocal.index)) {
               TargetPair cleanTarget = recipe.getCleanTarget();
               LocalVariableLookup cleanLocals = recipe.cleanLocalsTable();
               List<AbstractInsnNode> originalCallArgs = MethodCallAnalyzer.getMethodCallSrcInsns(cleanTarget.methodNode(), cleanInjectionInsn);
               int cleanOrdinal = cleanLocals.getTypedOrdinal(cleanLocals.getByIndex(((VarInsnNode)originalCallArgs.getFirst()).var)).orElse(-1);
               if (cleanOrdinal == -1) {
                  return Optional.empty();
               }

               LocalVariableNode dirtyLocal = recipe.dirtyLocalsTable().getByTypedOrdinal(Type.getType(instanceLocal.desc), cleanOrdinal).orElse(null);
               if (dirtyLocal == null) {
                  return Optional.empty();
               }

               Parameter replacementInstanceParam = Parameter.builder(dirtyLocal.desc)
                  .annotate("Lcom/llamalad7/mixinextras/sugar/Local;", b -> b.put("ordinal", cleanOrdinal))
                  .build();
               parameters.add(MethodParameters.ParamGroup.LOCALS, replacementInstanceParam);
               parameters.mapParameter(newInstanceParam, replacementInstanceParam);
            }

            if (instanceOfCall.getPrevious() instanceof MethodInsnNode m) {
               String loadedType = Type.getReturnType(m.desc).getDescriptor();

               for (int paramVar : usedVars.keySet()) {
                  if (paramVar != instanceLocal.index) {
                     LocalVariableNode node = mixinLocals.getByIndex(paramVar);
                     if (!loadedType.equals(node.desc)) {
                        return Optional.empty();
                     }

                     int paramOrdinal = mixinLocals.getParameterOrdinal(node);
                     Parameter oldParam = clean.getParameters().get(MethodParameters.ParamGroup.METHOD_PARAMS).get(paramOrdinal);
                     parameters.mapParameter(oldParam, newInstanceParam);
                  }
               }
            }

            return Optional.of(config);
         } else {
            return Optional.empty();
         }
      }

      private static Optional<Configuration> handleWrapOperationAdaptedTarget(List<List<AbstractInsnNode>> hunkLabels) {
         if (hunkLabels.size() != 1) {
            return Optional.empty();
         }

         List<MethodInsnNode> methodCalls = hunkLabels.getFirst().stream().filter(MethodInsnNode.class::isInstance).map(MethodInsnNode.class::cast).toList();
         if (methodCalls.size() != 1) {
            return Optional.empty();
         }

         MethodInsnNode insn = methodCalls.getLast();
         return Optional.of(MutableConfiguration.create().setAtData(AtData.create("INVOKE", insn)));
      }

      private static Optional<Configuration> handleWrapOperationNewInjectionPoint(
         MethodInsnNode cleanInjectionInsn, List<AbstractInsnNode> cleanLabel, List<List<AbstractInsnNode>> hunkLabels
      ) {
         if (hunkLabels.size() != 1) {
            return Optional.empty();
         }

         Type cleanReturnType = Type.getReturnType(cleanInjectionInsn.desc);
         List<AbstractInsnNode> dirtyLabel = hunkLabels.getFirst();
         List<String> cleanMethodCalls = cleanLabel.stream()
            .filter(i -> i instanceof MethodInsnNode m && m.owner.equals(cleanInjectionInsn.owner) && Type.getReturnType(m.desc).equals(cleanReturnType))
            .map(i -> ((MethodInsnNode)i).name)
            .toList();
         List<MethodInsnNode> methodCalls = dirtyLabel.stream()
            .filter(
               i -> i instanceof MethodInsnNode m
                  && m.owner.equals(cleanInjectionInsn.owner)
                  && Type.getReturnType(m.desc).equals(cleanReturnType)
                  && !cleanMethodCalls.contains(m.name)
            )
            .map(MethodInsnNode.class::cast)
            .toList();
         if (methodCalls.size() != 1) {
            return Optional.empty();
         }

         MethodInsnNode dirtyMinsn = methodCalls.getFirst();
         return Optional.of(MutableConfiguration.create().setAtData(AtData.create("INVOKE", dirtyMinsn)));
      }

      private static Optional<Configuration> handleTargetModification(List<List<AbstractInsnNode>> hunkLabels, TargetPair dirtyTarget, MixinContext context) {
         ClassNode dirtyClass = dirtyTarget.classNode();
         return hunkLabels.stream()
            .flatMap(Collection::stream)
            .filter(insn -> insn instanceof MethodInsnNode minsn && minsn.owner.equals(dirtyClass.name))
            .map(MethodInsnNode.class::cast)
            .filter(minsn -> {
               TargetPair pair = context.methods().findMethodPair(context.dirtyLookup(), MethodQualifier.create(minsn));
               return pair != null && context.methods().hasInjectionTargetInsns(pair);
            })
            .map(minsn -> MutableConfiguration.create().setAtData(AtData.create("INVOKE", minsn)))
            .findFirst();
      }
   }
}
