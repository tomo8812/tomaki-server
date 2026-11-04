package org.sinytra.adapter.patch.resolver.injection;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.env.param.Parameter;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.mixin.MixinFlag;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.types.BytecodeFixerUpper;
import org.sinytra.adapter.util.MethodQualifier;

public class InheritedInjectionPointSubResolver implements SubResolver {
   @Nullable
   @Override
   public Configuration resolve(MixinContext context, Recipe recipe) {
      if (!recipe.hasInjectionPointValue("INVOKE")) {
         return null;
      }

      AtData at = recipe.clean().getAtData();
      MethodQualifier atTarget = at.getTarget().flatMap(MethodQualifier::parse).orElseThrow();
      if (atTarget == null) {
         return null;
      }

      TargetPair targetPair = recipe.getDirtyTarget();
      if (targetPair == null) {
         return null;
      }

      if (context.methods().hasInjectionTargetInsns(targetPair)) {
         return null;
      }

      String owner = atTarget.internalOwnerName();

      for (AbstractInsnNode insn : targetPair.methodNode().instructions) {
         if (insn instanceof MethodInsnNode minsn
            && minsn.name.equals(atTarget.name())
            && minsn.desc.equals(atTarget.desc())
            && !minsn.owner.equals(owner)
            && (
               context.environment().inheritanceHandler(context.dirtyLookup()).isClassInherited(minsn.owner, owner)
                  || this.isFixedField(minsn, context.patchContext())
            )) {
            MutableConfiguration config = MutableConfiguration.create();
            config.setAtData(at.withTarget(minsn));
            if (context.hasFlag(MixinFlag.AT_TARGET_SENSITIVE) && minsn.getOpcode() != 184) {
               MethodParameters params = recipe.clean().getParameters().copy();
               List<Parameter> callParams = params.get(MethodParameters.ParamGroup.METHOD_PARAMS);
               if (!callParams.isEmpty()) {
                  Parameter first = callParams.getFirst().extend().annotate("Lorg/spongepowered/asm/mixin/injection/Coerce;", b -> b.visible(false)).build();
                  callParams.set(0, first);
                  config.setParameters(params);
               }
            }

            return config;
         }
      }

      return null;
   }

   private boolean isFixedField(AbstractInsnNode insn, PatchContext context) {
      for (AbstractInsnNode prev = insn.getPrevious(); prev != null && !(prev instanceof LabelNode); prev = prev.getPrevious()) {
         if (prev instanceof FieldInsnNode finsn) {
            BytecodeFixerUpper bfu = context.environment().bytecodeFixerUpper();
            return bfu.getFieldTypeChange(finsn.owner, finsn.name) != null;
         }
      }

      return false;
   }
}
