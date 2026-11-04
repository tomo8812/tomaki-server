package org.sinytra.adapter.transform.preprocess;

import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.locals.LocalVarAnalyzer;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.env.ctx.AuditTrail;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.transform.MethodTransformer;
import org.sinytra.adapter.util.AdapterUtil;

public class LocalCaptureUpgradeTransformer implements MethodTransformer {
   @Override
   public PatchResult apply(MixinContext context, Configuration config) {
      TargetPair cleanTarget = context.methods().findOwnMethodPair(context.cleanLookup(), config.getTargetMethod());
      if (cleanTarget == null) {
         return PatchResult.PASS;
      }

      TargetPair dirtyTarget = context.methods().findOwnMethodPair(context.dirtyLookup(), config.getTargetMethod());
      if (dirtyTarget == null) {
         return PatchResult.PASS;
      }

      if (!config.hasProperty(MixinKeys.LOCALS)) {
         return PatchResult.PASS;
      }

      MethodNode methodNode = context.methodNode();
      Type[] paramTypes = Type.getArgumentTypes(methodNode.desc);
      List<Pair<AnnotationNode, Type>> localAnnotations = AdapterUtil.getAnnotatedParameters(
         methodNode, paramTypes, "Lcom/llamalad7/mixinextras/sugar/Local;", Pair::of
      );
      if (!localAnnotations.isEmpty()) {
         return PatchResult.PASS;
      }

      LocalVarAnalyzer.CapturedLocalsInfo info = LocalVarAnalyzer.getCapturedLocals(context, dirtyTarget);
      if (info != null && !info.diff().isEmpty()) {
         LocalVarAnalyzer.CapturedLocalsTransform transform = LocalVarAnalyzer.analyzeCapturedLocals(info.capturedLocals(), methodNode);
         LocalVariableLookup cleanLookup = new LocalVariableLookup(cleanTarget.methodNode());
         LocalVariableLookup dirtyLookup = new LocalVariableLookup(dirtyTarget.methodNode());
         LocalVariableLookup lookup = info.capturedLocals().lvt();
         Map<Integer, Integer> parameterToOrdinal = new HashMap<>();
         Set<Integer> usedOrdinals = new HashSet<>();

         for (LocalVariableNode node : transform.usedLocalNodes()) {
            Type expected = Type.getType(node.desc);
            boolean addOrdinal = true;
            List<LocalVariableNode> cleanLocals = cleanLookup.getForType(expected);
            List<LocalVariableNode> dirtyLocals = dirtyLookup.getForType(expected);
            if (cleanLocals.size() != dirtyLocals.size()) {
               long matching = info.availableTypes().stream().filter(expected::equals).count();
               if (matching != 1L) {
                  return PatchResult.PASS;
               }

               addOrdinal = false;
            }

            int localOrdinal = lookup.getTypedOrdinal(node).orElse(-1);
            if (localOrdinal == -1) {
               return PatchResult.PASS;
            }

            int paramOrdinal = lookup.getParameterOrdinal(node);
            usedOrdinals.add(paramOrdinal);
            if (addOrdinal && cleanLocals.size() > 1) {
               parameterToOrdinal.put(paramOrdinal, localOrdinal);
            }
         }

         Type[] args = Type.getArgumentTypes(methodNode.desc);
         int start = info.capturedLocals().paramLocalStart();

         for (int i = start; i < args.length; i++) {
            if (usedOrdinals.contains(i)) {
               AnnotationVisitor visitor = methodNode.visitParameterAnnotation(i, "Lcom/llamalad7/mixinextras/sugar/Local;", false);
               if (parameterToOrdinal.containsKey(i)) {
                  visitor.visit("ordinal", parameterToOrdinal.get(i));
               }
            }
         }

         PatchResult result = transform.remover().isEmpty() ? PatchResult.APPLY : transform.remover().apply(context);
         if (result == PatchResult.PASS) {
            return PatchResult.PASS;
         }

         context.recordCtxAudit("Upgrade captured locals");
         context.environment().auditTrail().recordResult(context, config, AuditTrail.Match.FULL);
         return result;
      } else {
         return PatchResult.PASS;
      }
   }
}
