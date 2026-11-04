package org.sinytra.adapter.patch.resolver.special;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.env.MockMixinRuntime;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.resolver.Resolver;
import org.sinytra.adapter.util.MethodQualifier;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator.Context;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.Target;

public class ModifyVarAtReturnResolver implements Resolver {
   @Override
   public Resolver.ResolutionResult resolve(MixinContext context, Recipe recipe) {
      AtData at = recipe.clean().getAtData();
      if (!"RETURN".equals(at.getValue())) {
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

      int ordinal = at.getOrdinal().orElse(-1);
      Pair<AbstractInsnNode, Integer> cleanTargetPair = getTargetPair(recipe.clean(), context, cleanTarget, ordinal);
      if (cleanTargetPair != null && cleanTargetPair.getFirst() instanceof VarInsnNode cleanVarInsn && cleanVarInsn.var == (Integer)cleanTargetPair.getSecond()
         )
       {
         Pair<AbstractInsnNode, Integer> dirtyTargetPair = getTargetPair(recipe.clean(), context, dirtyTarget, ordinal);
         if (dirtyTargetPair != null && dirtyTargetPair.getFirst() instanceof MethodInsnNode dirtyMinsn) {
            List<AbstractInsnNode> args = MethodCallAnalyzer.getMethodCallSrcInsns(dirtyTarget.methodNode(), dirtyMinsn);
            if (args == null) {
               return Resolver.ResolutionResult.pass();
            }

            for (int i = 0; i < args.size(); i++) {
               AbstractInsnNode insn = args.get(i);
               if (insn instanceof VarInsnNode varInsn && varInsn.var == (Integer)cleanTargetPair.getSecond()) {
                  Configuration config = recipe.dirty()
                     .copyClean()
                     .setMixinType("Lorg/spongepowered/asm/mixin/injection/ModifyArg;")
                     .inheritTargetClass()
                     .inheritTargetMethod()
                     .setAtData(AtData.create("INVOKE", dirtyMinsn))
                     .inheritParameters()
                     .inheritReturnType()
                     .setProperty(MixinKeys.INDEX, i);
                  String qualifier = MethodQualifier.create(dirtyMinsn).asDescriptor();
                  context.recordCtxAudit("Redirect RETURN variable modifier to parameter %s of method call to %s", i, qualifier);
                  return Resolver.ResolutionResult.replace(config);
               }
            }

            return Resolver.ResolutionResult.pass();
         } else {
            return Resolver.ResolutionResult.pass();
         }
      } else {
         return Resolver.ResolutionResult.pass();
      }
   }

   private static Pair<AbstractInsnNode, Integer> getTargetPair(Configuration clean, MixinContext context, TargetPair injectionTarget, int ordinal) {
      List<AbstractInsnNode> targetInsns = context.methods().findInjectionTargetInsns(injectionTarget);
      if (targetInsns.isEmpty()) {
         return null;
      }

      int index = ordinal == -1 ? targetInsns.size() - 1 : ordinal;
      if (index >= targetInsns.size()) {
         return null;
      }

      AbstractInsnNode targetInsn = targetInsns.get(index);
      LocalVariableDiscriminator discriminator = LocalVariableDiscriminator.parse(context.methodAnnotation().unwrap());
      InjectionInfo injectionInfo = MockMixinRuntime.forInjectionInfo(context.classNode().name, injectionTarget.classNode().name, context.environment());
      Type returnType = clean.getReturnType();
      Target target = MockMixinRuntime.createMixinTarget(injectionTarget);
      Context ctx = new Context(injectionInfo, returnType, discriminator.isArgsOnly(), target, targetInsn);
      int local = discriminator.findLocal(ctx);
      return Pair.of(targetInsn, local);
   }
}
