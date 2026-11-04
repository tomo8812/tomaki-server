package org.sinytra.adapter.types;

import com.mojang.datafixers.util.Pair;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.MethodHelper;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.transform.MethodTransformer;
import org.sinytra.adapter.util.AdapterUtil;

public class FieldAccessorTypeTransformer implements MethodTransformer {
   private static final String PREFIX = "adapter$";
   private static final int PRIORITY_MAX = 9999;

   @Override
   public PatchResult apply(MixinContext context, Configuration config) {
      BytecodeFixerUpper bfu = context.environment().bytecodeFixerUpper();
      if (bfu != null && context.targetTypes().size() == 1) {
         ClassNode classNode = context.classNode();
         MethodNode methodNode = context.methodNode();
         String fieldFqn = AdapterUtil.getAccessorTargetFieldName(classNode.name, methodNode, context.methodAnnotation(), context.environment()).orElse(null);
         if (fieldFqn == null) {
            return PatchResult.PASS;
         }

         String fieldName = getFieldName(fieldFqn);
         Type owner = context.targetTypes().getFirst();
         Pair<Type, Type> updatedTypes = bfu.getFieldTypeChange(owner.getInternalName(), fieldName);
         if (updatedTypes != null) {
            TypeAdapter typeAdapter = bfu.getTypeAdapter((Type)updatedTypes.getSecond(), (Type)updatedTypes.getFirst());
            if (typeAdapter != null) {
               String targetMethod = this.addRedirectAcceptorField(owner, methodNode, fieldName, typeAdapter, bfu.getGenerator());
               methodNode.visibleAnnotations.remove(context.methodAnnotation().unwrap());
               AnnotationVisitor invokerAnn = methodNode.visitAnnotation("Lorg/spongepowered/asm/mixin/gen/Invoker;", true);
               invokerAnn.visit("value", targetMethod);
               return PatchResult.APPLY;
            }
         }

         return PatchResult.PASS;
      } else {
         return PatchResult.PASS;
      }
   }

   private String addRedirectAcceptorField(Type owner, MethodNode methodNode, String field, TypeAdapter adapter, BytecodeFixerJarGenerator generator) {
      ClassNode node = this.getOrCreateMixinClass(owner, generator);
      String methodName = "adapter$" + field;
      Type to = adapter.to();
      String methodDesc = Type.getMethodDescriptor(to, new Type[0]);
      if (node.methods.stream().noneMatch(m -> m.name.equals(methodName))) {
         boolean isStatic = MethodHelper.isStatic(methodNode);
         MethodNode method = (MethodNode)node.visitMethod(1 | (isStatic ? 8 : 0), methodName, methodDesc, null, null);
         AnnotationVisitor annotationVisitor = method.visitAnnotation("Lorg/spongepowered/asm/mixin/Unique;", true);
         annotationVisitor.visitEnd();
         method.visitCode();
         if (!isStatic) {
            method.visitVarInsn(25, 0);
         }

         method.visitFieldInsn(isStatic ? 178 : 180, owner.getInternalName(), field, adapter.from().getDescriptor());
         adapter.apply(method.instructions, method.instructions.getLast());
         method.visitInsn(getReturnOpcode(to));
         method.visitEnd();
         method.visitEnd();
      }

      return methodName + methodDesc;
   }

   private ClassNode getOrCreateMixinClass(Type targetClass, BytecodeFixerJarGenerator generator) {
      String className = targetClass.getInternalName().replace('/', '_');
      return generator.getOrCreateClass(className, s -> this.generateFieldAdapterMixin(s, targetClass));
   }

   private ClassNode generateFieldAdapterMixin(String className, Type targetClass) {
      ClassNode node = new ClassNode();
      node.visit(52, 33, className, null, "java/lang/Object", null);
      AnnotationVisitor mixinAnnotation = node.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;", false);
      AnnotationVisitor valueVisitor = mixinAnnotation.visitArray("value");
      valueVisitor.visit(null, targetClass);
      valueVisitor.visitEnd();
      mixinAnnotation.visit("priority", 9999);
      mixinAnnotation.visitEnd();
      return node;
   }

   private static int getReturnOpcode(Type type) {
      return switch (type.getSort()) {
         case 0 -> 177;
         case 1, 2, 3, 4, 5 -> 172;
         case 6 -> 174;
         case 7 -> 173;
         case 8 -> 175;
         case 9, 10 -> 176;
         default -> throw new UnsupportedOperationException();
      };
   }

   private static String getFieldName(String desc) {
      int descIndex = desc.indexOf(58);
      return descIndex == -1 ? desc : desc.substring(0, descIndex);
   }
}
