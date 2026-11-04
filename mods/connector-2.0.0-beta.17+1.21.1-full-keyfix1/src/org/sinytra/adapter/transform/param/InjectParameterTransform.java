package org.sinytra.adapter.transform.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.sinytra.adapter.analysis.locals.LVTSnapshot;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.env.ctx.MethodHelper;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.patch.mixin.MixinFlag;

public record InjectParameterTransform(int index, Type type) implements ParameterTransformer {
   @Override
   public PatchResult apply(ClassNode classNode, MethodNode methodNode, MixinContext context, List<Type> parameters, int offset) {
      boolean isNonStatic = !MethodHelper.isStatic(methodNode);
      int index = this.index + offset;
      if (index >= parameters.size() + 1) {
         return PatchResult.PASS;
      } else {
         AnnotationHandle annotation = context.methodAnnotation();
         if (context.hasFlag(MixinFlag.TARGETS_VARIABLE)) {
            annotation.getValue("index").ifPresent(indexHandle -> {
               int indexValue = (Integer)indexHandle.get();
               if (indexValue >= index) {
                  indexHandle.set(indexValue + 1);
               }
            });
            return PatchResult.APPLY;
         } else if (annotation.matchesDesc("Lorg/spongepowered/asm/mixin/injection/ModifyArgs;")) {
            ModifyArgsOffsetUpgrader.upgradeAfterParamInsert(methodNode, this.index);
            return PatchResult.APPLY;
         } else {
            LocalVariableNode self = methodNode.localVariables.stream().filter(lvn -> lvn.index == 0).findFirst().orElseThrow();
            int lvtIndex = ParamTransformationUtil.calculateLVTIndex(parameters, isNonStatic, index);
            LVTSnapshot.with(
               methodNode,
               () -> {
                  ParameterNode newParameter = new ParameterNode("adapter_injected_" + index, 4096);
                  parameters.add(index, this.type);
                  methodNode.parameters.add(index, newParameter);
                  offsetParameters(methodNode, index);
                  methodNode.localVariables
                     .add(
                        index + (isNonStatic ? 1 : 0),
                        new LocalVariableNode(newParameter.name, this.type.getDescriptor(), null, self.start, self.end, lvtIndex)
                     );
               }
            );
            return PatchResult.APPLY;
         }
      }
   }

   public static void offsetParameters(MethodNode methodNode, int paramIndex) {
      if (methodNode.invisibleParameterAnnotations != null) {
         List<List<AnnotationNode>> annotations = new ArrayList<>(Arrays.asList(methodNode.invisibleParameterAnnotations));
         if (paramIndex < annotations.size()) {
            annotations.add(paramIndex, null);
            methodNode.invisibleParameterAnnotations = annotations.toArray(List[]::new);
            methodNode.invisibleAnnotableParameterCount = annotations.size();
         }
      }

      if (methodNode.invisibleTypeAnnotations != null) {
         List<TypeAnnotationNode> invisibleTypeAnnotations = methodNode.invisibleTypeAnnotations;

         for (int j = 0; j < invisibleTypeAnnotations.size(); j++) {
            TypeAnnotationNode typeAnnotation = invisibleTypeAnnotations.get(j);
            TypeReference ref = new TypeReference(typeAnnotation.typeRef);
            int typeIndex = ref.getFormalParameterIndex();
            if (ref.getSort() == 22 && typeIndex >= paramIndex) {
               invisibleTypeAnnotations.set(
                  j, new TypeAnnotationNode(TypeReference.newFormalParameterReference(typeIndex + 1).getValue(), typeAnnotation.typePath, typeAnnotation.desc)
               );
            }
         }
      }
   }
}
