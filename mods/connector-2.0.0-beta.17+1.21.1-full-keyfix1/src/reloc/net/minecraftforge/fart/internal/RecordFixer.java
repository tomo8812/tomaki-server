package reloc.net.minecraftforge.fart.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import reloc.net.minecraftforge.fart.api.Transformer;

public class RecordFixer extends OptionalChangeTransformer {
   public static final RecordFixer INSTANCE = new RecordFixer();

   private RecordFixer() {
      super(RecordFixer.Fixer::new);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry var1) {
      return super.process(var1);
   }

   private static class Fixer extends OptionalChangeTransformer.ClassFixer {
      private Map<String, RecordFixer.Fixer.Entry> components;
      private boolean isRecord;
      private boolean hasRecordComponents;

      public Fixer(ClassVisitor parent) {
         super(parent);
      }

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.isRecord = "java/lang/Record".equals(superName);
         super.visit(version, access, name, signature, superName, interfaces);
      }

      public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
         this.hasRecordComponents = true;
         return super.visitRecordComponent(name, descriptor, signature);
      }

      public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
         if (this.isRecord && (access & 24) == 16) {
            int newAccess = access & -6 | 2;
            if (newAccess != access) {
               this.madeChange = true;
               access = newAccess;
            }

            if (this.components == null) {
               this.components = new LinkedHashMap<>();
            }

            this.components.put(name + descriptor, new RecordFixer.Fixer.Entry(name, descriptor, signature));
         }

         return super.visitField(access, name, descriptor, signature, value);
      }

      public void visitEnd() {
         if (this.isRecord && !this.hasRecordComponents && this.components != null) {
            for (RecordFixer.Fixer.Entry entry : this.components.values()) {
               this.visitRecordComponent(entry.name, entry.descriptor, entry.signature);
               this.madeChange = true;
            }
         }
      }

      private static class Entry {
         private final String name;
         private final String descriptor;
         private final String signature;

         private Entry(String name, String descriptor, String signature) {
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
         }

         @Override
         public String toString() {
            return "[Name: " + this.name + ", Desc: " + this.descriptor + ", Sig: " + this.signature + "]";
         }
      }
   }
}
