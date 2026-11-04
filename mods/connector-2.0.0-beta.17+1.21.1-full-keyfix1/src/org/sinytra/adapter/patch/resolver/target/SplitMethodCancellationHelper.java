package org.sinytra.adapter.patch.resolver.target;

import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.method.MethodAnalyzer;
import org.sinytra.adapter.env.ctx.MixinClassGenerator;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.util.TypeConstants;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;

public final class SplitMethodCancellationHelper {
   public static void handle(MixinContext context, Recipe recipe, MethodNode newTarget) {
      TargetPair cleanTarget = recipe.getCleanTarget();
      if (cleanTarget != null) {
         TargetPair newCleanTarget = recipe.getNewCleanTarget();
         if (newCleanTarget != null) {
            ClassNode originalClassTarget = newCleanTarget.classNode();
            MethodNode originalMethodTarget = newCleanTarget.methodNode();
            if (MethodAnalyzer.isDirtyDeprecatedMethod(cleanTarget.methodNode(), originalMethodTarget)
               && Type.getReturnType(originalMethodTarget.desc) == Type.VOID_TYPE) {
               MixinClassGenerator generator = context.patchContext().environment().classGenerator();
               ClassNode generatedTarget = generator.getOrGenerateMixinClass(context.classNode(), originalClassTarget.name, null);
               List<MethodNode> invocations = MethodAnalyzer.getOwnMethodCalls(newCleanTarget);
               if (invocations != null) {
                  int index = invocations.indexOf(newTarget);
                  String fieldName = "adapter$canceller$" + originalMethodTarget.name + "$" + AdapterUtil.randomString(5);
                  FieldNode trackerField = (FieldNode)generatedTarget.visitField(2, fieldName, Type.BOOLEAN_TYPE.getDescriptor(), null, null);

                  for (int i = index + 1; i < invocations.size(); i++) {
                     generateCancellerMethod(generatedTarget, trackerField, originalClassTarget, invocations.get(i), context, i == invocations.size() - 1);
                  }

                  context.recordCtxAudit("Generate cancellation handler mixin");
               }
            }
         }
      }
   }

   private static void generateCancellerMethod(
      ClassNode generatedTarget, FieldNode trackerField, ClassNode originalClassTarget, MethodNode newTarget, MixinContext context, boolean reset
   ) {
      String name = context.methodNode().name + "$adapter$canceller$" + AdapterUtil.randomString(5);
      String desc = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[]{TypeConstants.CI_TYPE});
      MethodNode invokerMixinMethod = (MethodNode)generatedTarget.visitMethod(2 | (context.isStatic() ? 8 : 0), name, desc, null, null);
      AnnotationVisitor injectAnn = invokerMixinMethod.visitAnnotation("Lorg/spongepowered/asm/mixin/injection/Inject;", true);
      AnnotationVisitor methodValue = injectAnn.visitArray("method");
      methodValue.visit(null, newTarget.name + newTarget.desc);
      methodValue.visitEnd();
      methodValue = injectAnn.visitArray("at");
      AnnotationVisitor atAnn = methodValue.visitAnnotation(null, "Lorg/spongepowered/asm/mixin/injection/At;");
      atAnn.visit("value", "HEAD");
      atAnn.visitEnd();
      methodValue.visitEnd();
      injectAnn.visit("cancellable", Boolean.TRUE);
      injectAnn.visitEnd();
      GeneratorAdapter gen = new GeneratorAdapter(invokerMixinMethod, invokerMixinMethod.access, invokerMixinMethod.name, invokerMixinMethod.desc);
      Label endLabel = new Label();
      gen.newLabel();
      gen.loadThis();
      gen.getField(Type.getObjectType(generatedTarget.name), trackerField.name, Type.BOOLEAN_TYPE);
      gen.visitJumpInsn(153, endLabel);
      if (reset) {
         gen.newLabel();
         gen.loadThis();
         gen.visitInsn(3);
         gen.putField(Type.getObjectType(generatedTarget.name), trackerField.name, Type.BOOLEAN_TYPE);
      }

      gen.newLabel();
      gen.loadArg(0);
      gen.invokeVirtual(TypeConstants.CI_TYPE, new Method("cancel", "()V"));
      gen.visitLabel(endLabel);
      gen.returnValue();
      gen.newLabel();
      gen.endMethod();
      MethodQualifier qualifier = new MethodQualifier(TypeConstants.CI_TYPE.getDescriptor(), "cancel", "()V");
      InsnList methodInsns = context.methodNode().instructions;

      for (AbstractInsnNode insn : methodInsns) {
         if (insn instanceof MethodInsnNode minsn && qualifier.matches(minsn)) {
            InsnList list = new InsnList();
            list.add(new VarInsnNode(25, 0));
            list.add(new InsnNode(4));
            list.add(new FieldInsnNode(181, originalClassTarget.name, trackerField.name, trackerField.desc));
            methodInsns.insertBefore(minsn, list);
         }
      }
   }
}
