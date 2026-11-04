package org.sinytra.adapter.patch.resolver.injection;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.MethodInsnNode;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.env.ann.AtData;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;

public class OverloadedInjectionPointSubResolver implements SubResolver {
   @Nullable
   @Override
   public Configuration resolve(MixinContext context, Recipe recipe) {
      AtData at = recipe.clean().getAtData();
      if (at != null && (at.getValue().equals("INVOKE") || at.getValue().equals("INVOKE_ASSIGN"))) {
         TargetPair cleanPair = recipe.getCleanTarget();
         if (cleanPair == null) {
            return null;
         }

         TargetPair dirtyPair = recipe.getDirtyTarget();
         if (dirtyPair == null) {
            return null;
         }

         AtData tempAt = this.getInvokeAtData(at);
         if (context.methods().findInjectionTargetInsn(cleanPair, tempAt) instanceof MethodInsnNode cleanMinsn) {
            MethodQualifier cleanQualifier = MethodQualifier.create(cleanMinsn);
            TargetPair cleanTarget = context.methods().findMethodPair(context.cleanLookup(), cleanQualifier);
            if (cleanTarget == null) {
               return null;
            }

            TargetPair newCleanTarget = context.methods().findMethodPair(context.dirtyLookup(), cleanQualifier);
            if (newCleanTarget == null) {
               return null;
            }

            if (!AdapterUtil.isDeprecated(cleanTarget.methodNode()) && AdapterUtil.isDeprecated(newCleanTarget.methodNode())) {
               List<MethodInsnNode> cleanCallsInDirty = MethodCallAnalyzer.getMethodCallMinsns(dirtyPair.methodNode(), cleanQualifier);
               if (!cleanCallsInDirty.isEmpty()) {
                  return null;
               }

               MethodQualifier qualifier = cleanQualifier.ignoreDesc();
               List<MethodInsnNode> dirtyCalls = MethodCallAnalyzer.getMethodCallMinsns(dirtyPair.methodNode(), qualifier);
               if (dirtyCalls.isEmpty()) {
                  return null;
               }

               boolean allEqual = dirtyCalls.stream().map(m -> m.desc).distinct().limit(2L).count() <= 1L;
               return allEqual ? MutableConfiguration.create().setAtData(recipe.clean().getAtData().withTarget(dirtyCalls.getFirst())) : null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private AtData getInvokeAtData(AtData at) {
      return at.getValue().equals("INVOKE_ASSIGN") ? at.withValue("INVOKE") : at;
   }
}
