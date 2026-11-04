package reloc.net.minecraftforge.fart.internal;

import java.util.function.Function;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import reloc.net.minecraftforge.fart.api.Transformer;

abstract class OptionalChangeTransformer implements Transformer {
   protected final Function<ClassVisitor, OptionalChangeTransformer.ClassFixer> fixerFactory;

   protected OptionalChangeTransformer(Function<ClassVisitor, OptionalChangeTransformer.ClassFixer> fixerFactory) {
      this.fixerFactory = fixerFactory;
   }

   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry entry) {
      ClassReader reader = new ClassReader(entry.getData());
      ClassWriter writer = new ClassWriter(reader, 0);
      OptionalChangeTransformer.ClassFixer fixer = this.fixerFactory.apply(writer);
      reader.accept(fixer, 0);
      return !fixer.madeChange() ? entry : Transformer.ClassEntry.create(entry.getName(), entry.getTime(), writer.toByteArray());
   }

   protected abstract static class ClassFixer extends ClassVisitor {
      protected boolean madeChange = false;

      protected ClassFixer(ClassVisitor parent) {
         this(589824, parent);
      }

      protected ClassFixer(int api, ClassVisitor classVisitor) {
         super(api, classVisitor);
      }

      public boolean madeChange() {
         return this.madeChange;
      }
   }
}
