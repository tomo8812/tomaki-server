package reloc.net.minecraftforge.fart.internal;

import org.objectweb.asm.ClassVisitor;
import reloc.net.minecraftforge.fart.api.SourceFixerConfig;
import reloc.net.minecraftforge.fart.api.Transformer;

public final class SourceFixer extends OptionalChangeTransformer {
   public SourceFixer(SourceFixerConfig config) {
      super(parent -> new SourceFixer.Fixer(config, parent));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry var1) {
      return super.process(var1);
   }

   private static class Fixer extends OptionalChangeTransformer.ClassFixer {
      private final SourceFixerConfig config;
      private String className = null;
      private boolean hadEntry = false;

      public Fixer(SourceFixerConfig config, ClassVisitor parent) {
         super(parent);
         this.config = config;
      }

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         super.visit(version, access, name, signature, superName, interfaces);
         this.className = name;
      }

      public void visitSource(String source, String debug) {
         super.visitSource(this.getSourceName(source), debug);
         this.hadEntry = true;
      }

      public void visitEnd() {
         if (!this.hadEntry) {
            super.visitSource(this.getSourceName(null), null);
         }

         super.visitEnd();
      }

      private String getSourceName(String existing) {
         String name = this.className;
         if (this.config == SourceFixerConfig.JAVA) {
            int idx = name.lastIndexOf(47);
            if (idx != -1) {
               name = name.substring(idx + 1);
            }

            idx = name.indexOf(36);
            if (idx != -1) {
               name = name.substring(0, idx);
            }

            name = name + ".java";
         }

         if (!name.equals(existing)) {
            this.madeChange = true;
         }

         return name;
      }
   }
}
