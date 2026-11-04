package reloc.net.minecraftforge.fart.internal;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;

public class EnhancedClassRemapper extends ClassRemapper {
   private final EnhancedRemapper remapper;
   @Nullable
   private final RenamingTransformer transformer;
   private static final Handle META_FACTORY = new Handle(
      6,
      "java/lang/invoke/LambdaMetafactory",
      "metafactory",
      "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
      false
   );
   private static final Handle ALT_META_FACTORY = new Handle(
      6,
      "java/lang/invoke/LambdaMetafactory",
      "altMetafactory",
      "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;",
      false
   );

   public EnhancedClassRemapper(ClassVisitor classVisitor, EnhancedRemapper remapper, @Nullable RenamingTransformer transformer) {
      super(classVisitor, remapper);
      this.remapper = remapper;
      this.transformer = transformer;
   }

   public MethodVisitor visitMethod(int access, final String mname, final String mdescriptor, String msignature, String[] exceptions) {
      String remappedDescriptor = this.remapper.mapMethodDesc(mdescriptor);
      MethodVisitor methodVisitor = this.cv
         .visitMethod(
            access,
            this.remapper.mapMethodName(this.className, mname, mdescriptor),
            remappedDescriptor,
            this.remapper.mapSignature(msignature, false),
            exceptions == null ? null : this.remapper.mapTypes(exceptions)
         );
      if (methodVisitor == null) {
         return null;
      }

      if (this.transformer != null && (access & 1280) != 0) {
         this.renameAbstract(access, mname, mdescriptor);
      }

      return new MethodRemapper(methodVisitor, this.remapper) {
         public void visitLocalVariable(String pname, String pdescriptor, String psignature, Label start, Label end, int index) {
            super.visitLocalVariable(
               EnhancedClassRemapper.this.remapper.mapParameterName(EnhancedClassRemapper.this.className, mname, mdescriptor, index, pname),
               pdescriptor,
               psignature,
               start,
               end,
               index
            );
         }

         public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            if (!EnhancedClassRemapper.META_FACTORY.equals(bootstrapMethodHandle) && !EnhancedClassRemapper.ALT_META_FACTORY.equals(bootstrapMethodHandle)) {
               super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
            } else {
               String owner = Type.getReturnType(descriptor).getInternalName();
               String odesc = ((Type)bootstrapMethodArguments[0]).getDescriptor();
               Object[] remappedBootstrapMethodArguments = new Object[bootstrapMethodArguments.length];

               for (int i = 0; i < bootstrapMethodArguments.length; i++) {
                  remappedBootstrapMethodArguments[i] = this.remapper.mapValue(bootstrapMethodArguments[i]);
               }

               this.mv
                  .visitInvokeDynamicInsn(
                     this.remapper.mapMethodName(owner, name, odesc),
                     this.remapper.mapMethodDesc(descriptor),
                     (Handle)this.remapper.mapValue(bootstrapMethodHandle),
                     remappedBootstrapMethodArguments
                  );
            }
         }
      };
   }

   private void renameAbstract(int access, String name, String descriptor) {
      Type[] types = Type.getArgumentTypes(descriptor);
      if (types.length != 0) {
         List<String> names = new ArrayList<>();
         int i = (access & 8) == 0 ? 1 : 0;

         for (Type type : types) {
            names.add(this.remapper.mapParameterName(this.className, name, descriptor, i, "var" + i));
            i += type.getSize();
         }

         this.transformer
            .storeNames(
               this.remapper.mapType(this.className),
               this.remapper.mapMethodName(this.className, name, descriptor),
               this.remapper.mapMethodDesc(descriptor),
               names
            );
      }
   }
}
