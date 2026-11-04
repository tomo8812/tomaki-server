package org.sinytra.adapter.patch.processor.extract;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchEnvironment;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.env.util.TypeConstants;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;
import org.sinytra.adapter.util.OpcodeUtil;

public class MirrorableExtractMixin {
   public static PatchResult apply(MixinContext context, Recipe recipe, String destinationClass, MethodInsnNode destinationMethodInvocation) {
      TargetPair dirtyTarget = recipe.getNewCleanTarget();
      if (dirtyTarget == null) {
         return PatchResult.PASS;
      }

      ClassNode classNode = context.classNode();
      MethodNode methodNode = context.methodNode();
      PatchEnvironment environment = context.environment();
      Type selfType = Type.getObjectType(dirtyTarget.classNode().name);
      Type[] params = Type.getArgumentTypes(destinationMethodInvocation.desc);
      int selfIndex = Stream.of(Stream.<Integer>iterate(0, i -> i < params.length, i -> i + 1).filter(i -> params[i].equals(selfType)).toList())
         .filter(list -> list.size() == 1)
         .map(List::getFirst)
         .findFirst()
         .orElse(-1);
      if (selfIndex == -1) {
         return PatchResult.PASS;
      }

      List<AbstractInsnNode> callInsns = MethodCallAnalyzer.getMethodCallSrcInsns(dirtyTarget.methodNode(), destinationMethodInvocation);
      if (callInsns != null && callInsns.size() > selfIndex) {
         AbstractInsnNode selfParamInsn = callInsns.get(selfIndex);
         if (selfParamInsn instanceof VarInsnNode varInsn && varInsn.getOpcode() == 25 && varInsn.var == 0) {
            ClassNode generatedTarget = environment.classGenerator().getOrGenerateMixinClass(classNode, destinationClass, null);
            environment.refmapHolder().copyEntries(classNode.name, generatedTarget.name);
            String name = methodNode.name + "$adapter$mirror$" + AdapterUtil.randomString(5);
            List<Type> originalParams = Parameters.getParameterTypes(methodNode.desc);
            List<Type> newParams = ImmutableList.builder().add(Type.getArgumentTypes(destinationMethodInvocation.desc)).add(TypeConstants.CI_TYPE).build();
            if (!new HashSet<>(newParams).containsAll(originalParams)) {
               return PatchResult.PASS;
            }

            String desc = Type.getMethodDescriptor(Type.VOID_TYPE, newParams.toArray(Type[]::new));
            context.methodAnnotation().setOrAppendNonNull("method", List.of(MethodQualifier.create(destinationMethodInvocation).asDescriptor()));
            MethodNode invokerMixinMethod = (MethodNode)generatedTarget.visitMethod(10, name, desc, null, null);
            invokerMixinMethod.visibleAnnotations = new ArrayList(methodNode.visibleAnnotations);
            methodNode.access = OpcodeUtil.setAccessVisibility(methodNode.access, 1);
            methodNode.visibleAnnotations.remove(context.methodAnnotation().unwrap());
            methodNode.visitAnnotation("Lorg/spongepowered/asm/mixin/Unique;", true);
            GeneratorAdapter gen = new GeneratorAdapter(invokerMixinMethod, invokerMixinMethod.access, invokerMixinMethod.name, invokerMixinMethod.desc);
            gen.newLabel();
            gen.loadArg(selfIndex);

            for (Type type : originalParams) {
               gen.loadArg(newParams.indexOf(type));
            }

            gen.invokeVirtual(selfType, new Method(methodNode.name, methodNode.desc));
            gen.newLabel();
            gen.returnValue();
            gen.newLabel();
            gen.endMethod();
            context.recordCtxAudit("Extract (mirror) mixin to target %s", destinationClass);
            return PatchResult.APPLY;
         } else {
            return PatchResult.PASS;
         }
      } else {
         return PatchResult.PASS;
      }
   }
}
