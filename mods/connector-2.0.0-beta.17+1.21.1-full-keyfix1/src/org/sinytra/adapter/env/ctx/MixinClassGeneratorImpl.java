package org.sinytra.adapter.env.ctx;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;

public class MixinClassGeneratorImpl implements MixinClassGenerator {
   private final Map<String, MixinClassGenerator.GeneratedClass> generatedMixinClasses = new ConcurrentHashMap<>();

   @Nullable
   @Override
   public Map<String, MixinClassGenerator.GeneratedClass> getGeneratedMixinClasses() {
      return this.generatedMixinClasses;
   }

   @Override
   public ClassNode getOrGenerateMixinClass(ClassNode original, String targetClass, @Nullable String parent) {
      int lastSeparator = original.name.lastIndexOf(47);
      String pkg = original.name.substring(0, lastSeparator + 1);
      String[] parts = targetClass.split("/");
      String className = pkg + "adapter_generated_" + parts[parts.length - 1];
      MixinClassGenerator.GeneratedClass generatedClass;
      synchronized (this.generatedMixinClasses) {
         generatedClass = this.generatedMixinClasses.computeIfAbsent(className, s -> {
            ClassNode node = this.doGenerateMixinClass(s, targetClass, parent);
            return new MixinClassGenerator.GeneratedClass(original.name, node.name, node);
         });
      }

      return generatedClass.node();
   }

   private ClassNode doGenerateMixinClass(String className, String targetClass, @Nullable String parent) {
      ClassNode targetNode;
      try {
         targetNode = MixinService.getService().getBytecodeProvider().getClassNode(targetClass);
      } catch (ClassNotFoundException | IOException e) {
         throw new IllegalArgumentException("Target class " + targetClass + " not found", e);
      }

      ClassNode node = new ClassNode();
      boolean itf = (targetNode.access & 512) == 512;
      int flags = itf ? 512 : 16;
      node.visit(52, 1025 | flags, className, null, parent != null ? parent : "java/lang/Object", null);
      AnnotationVisitor mixinAnn = node.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;", false);
      AnnotationVisitor valueArray = mixinAnn.visitArray("value");
      valueArray.visit(null, Type.getType(Type.getObjectType(targetClass).getDescriptor()));
      valueArray.visitEnd();
      mixinAnn.visitEnd();
      return node;
   }
}
