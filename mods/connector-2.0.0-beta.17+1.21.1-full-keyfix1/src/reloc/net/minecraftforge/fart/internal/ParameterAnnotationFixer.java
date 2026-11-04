package reloc.net.minecraftforge.fart.internal;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import reloc.net.minecraftforge.fart.api.Transformer;

public final class ParameterAnnotationFixer extends OptionalChangeTransformer {
   public static final ParameterAnnotationFixer INSTANCE = new ParameterAnnotationFixer();

   private ParameterAnnotationFixer() {
      super(ParameterAnnotationFixer.Fixer::new);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry var1) {
      return super.process(var1);
   }

   private static class AnnotationHolder {
      final int parameter;
      final String descriptor;
      final boolean visible;
      final AnnotationNode node;

      AnnotationHolder(int parameter, String descriptor, boolean visible, AnnotationNode node) {
         this.parameter = parameter;
         this.descriptor = descriptor;
         this.visible = visible;
         this.node = node;
      }
   }

   private static class Fixer extends OptionalChangeTransformer.ClassFixer {
      private String name;
      private boolean isEnum;
      private String outerName;

      public Fixer(ClassVisitor parent) {
         super(parent);
      }

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.name = name;
         if ((access & 16384) != 0) {
            this.isEnum = true;
         }

         super.visit(version, access, name, signature, superName, interfaces);
      }

      public void visitInnerClass(String name, String outerName, String innerName, int access) {
         if ((access & 520) == 0 && this.name.equals(name) && innerName != null) {
            if (outerName == null) {
               int idx = name.lastIndexOf(36);
               if (idx != -1) {
                  this.outerName = name.substring(0, idx);
               }
            } else {
               this.outerName = outerName;
            }
         }

         super.visitInnerClass(name, outerName, innerName, access);
      }

      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
         MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
         if (!name.equals("<init>")) {
            return methodVisitor;
         }

         Type[] syntheticParams = null;
         if (this.isEnum) {
            syntheticParams = new Type[]{Type.getObjectType("java/lang/String"), Type.INT_TYPE};
         } else if (this.outerName != null) {
            syntheticParams = new Type[]{Type.getObjectType(this.outerName)};
         }

         if (syntheticParams == null) {
            return methodVisitor;
         }

         Type[] argumentTypes = Type.getArgumentTypes(descriptor);
         return beginsWith(argumentTypes, syntheticParams)
            ? new ParameterAnnotationFixer.Fixer.MethodFixer(argumentTypes.length, syntheticParams.length, methodVisitor)
            : methodVisitor;
      }

      private static boolean beginsWith(Type[] values, Type[] prefix) {
         if (values.length < prefix.length) {
            return false;
         }

         for (int i = 0; i < prefix.length; i++) {
            if (!values[i].equals(prefix[i])) {
               return false;
            }
         }

         return true;
      }

      private class MethodFixer extends MethodVisitor {
         private final int argumentsLength;
         private final int numSynthetic;
         private final ParameterAnnotationFixer.AnnotationHolder[] annotations;
         private int parameterAnnotationCount;
         private boolean visibleParamAnnotations;
         private boolean hasParamAnnotation;

         MethodFixer(int argumentsLength, int numSynthetic, MethodVisitor methodVisitor) {
            super(589824, methodVisitor);
            this.argumentsLength = argumentsLength;
            this.numSynthetic = numSynthetic;
            this.annotations = new ParameterAnnotationFixer.AnnotationHolder[argumentsLength];
         }

         public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
            this.parameterAnnotationCount = parameterCount;
            this.visibleParamAnnotations = visible;
         }

         public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            this.hasParamAnnotation = true;
            AnnotationNode node = new AnnotationNode(589824, descriptor);
            this.annotations[parameter] = new ParameterAnnotationFixer.AnnotationHolder(parameter, descriptor, visible, node);
            return node;
         }

         public void visitEnd() {
            int offset = 0;
            if (this.hasParamAnnotation && this.parameterAnnotationCount == this.argumentsLength) {
               offset = this.numSynthetic;
            }

            if (offset != 0) {
               Fixer.this.madeChange = true;
            }

            super.visitAnnotableParameterCount(this.parameterAnnotationCount - offset, this.visibleParamAnnotations);

            for (ParameterAnnotationFixer.AnnotationHolder holder : this.annotations) {
               if (holder != null) {
                  int parameter = holder.parameter - offset;
                  if (parameter >= 0) {
                     holder.node.accept(super.visitParameterAnnotation(parameter, holder.descriptor, holder.visible));
                  }
               }
            }

            super.visitEnd();
         }
      }
   }
}
