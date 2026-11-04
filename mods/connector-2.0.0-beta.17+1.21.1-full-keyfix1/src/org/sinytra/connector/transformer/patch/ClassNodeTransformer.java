package org.sinytra.connector.transformer.patch;

import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.sinytra.adapter.env.ctx.PatchResult;
import reloc.net.minecraftforge.fart.api.Transformer;

public class ClassNodeTransformer implements Transformer {
   private final List<ClassNodeTransformer.ClassProcessor> processors;

   public ClassNodeTransformer(ClassNodeTransformer.ClassProcessor... processors) {
      this.processors = List.of(processors);
   }

   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry entry) {
      PatchResult patchResult = PatchResult.PASS;
      ClassReader reader = new ClassReader(entry.getData());
      ClassNode node = new ClassNode();
      reader.accept(node, 0);

      for (ClassNodeTransformer.ClassProcessor processor : this.processors) {
         patchResult = patchResult.or(processor.process(node));
      }

      if (patchResult != PatchResult.PASS) {
         ClassWriter writer = new ClassWriter(1 | (patchResult == PatchResult.COMPUTE_FRAMES ? 2 : 0));
         node.accept(writer);
         return Transformer.ClassEntry.create(entry.getName(), entry.getTime(), writer.toByteArray());
      } else {
         return entry;
      }
   }

   @Override
   public Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
      for (ClassNodeTransformer.ClassProcessor processor : this.processors) {
         entry = processor.process(entry);
      }

      return entry;
   }

   public interface ClassProcessor {
      PatchResult process(ClassNode var1);

      default Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
         return entry;
      }
   }
}
