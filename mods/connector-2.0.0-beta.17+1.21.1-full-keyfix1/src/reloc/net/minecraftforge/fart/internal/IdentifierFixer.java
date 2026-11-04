package reloc.net.minecraftforge.fart.internal;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import reloc.net.minecraftforge.fart.api.IdentifierFixerConfig;
import reloc.net.minecraftforge.fart.api.Transformer;

public final class IdentifierFixer extends OptionalChangeTransformer {
   public IdentifierFixer(IdentifierFixerConfig config) {
      super(parent -> new IdentifierFixer.Fixer(config, parent));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry var1) {
      return super.process(var1);
   }

   private static class Fixer extends OptionalChangeTransformer.ClassFixer {
      private final IdentifierFixerConfig config;

      public Fixer(IdentifierFixerConfig config, ClassVisitor parent) {
         super(parent);
         this.config = config;
      }

      public final MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
         MethodVisitor parent = super.visitMethod(access, name, descriptor, signature, exceptions);
         return new MethodVisitor(589824, parent) {
            private final Map<Integer, Integer> seen = new HashMap<>();

            public void visitLocalVariable(String pname, String pdescriptor, String psignature, Label start, Label end, int index) {
               String newName = this.fixName(pname, index);
               super.visitLocalVariable(newName, pdescriptor, psignature, start, end, index);
            }

            private String fixName(String name, int index) {
               boolean valid = true;
               if (name.isEmpty()) {
                  valid = false;
               } else if (Fixer.this.config == IdentifierFixerConfig.SNOWMEN) {
                  if (9731 == name.charAt(0)) {
                     valid = false;
                  }
               } else if (!name.isEmpty() && Character.isJavaIdentifierStart(name.charAt(0))) {
                  for (int x = 1; x < name.length(); x++) {
                     if (!Character.isJavaIdentifierPart(name.charAt(x))) {
                        valid = false;
                        break;
                     }
                  }
               } else {
                  valid = false;
               }

               if (valid) {
                  return name;
               }

               Fixer.this.madeChange = true;
               int version = this.seen.computeIfAbsent(index, k -> 0) + 1;
               this.seen.put(index, version);
               return "lvt_" + index + '_' + version + '_';
            }
         };
      }
   }
}
