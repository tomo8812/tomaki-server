package reloc.net.minecraftforge.fart.internal;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;
import reloc.net.minecraftforge.fart.api.ClassProvider;
import reloc.net.minecraftforge.fart.api.Transformer;
import reloc.net.minecraftforge.srgutils.IMappingFile;

public class RenamingTransformer implements Transformer {
   private static final String ABSTRACT_FILE = "fernflower_abstract_parameter_names.txt";
   protected final EnhancedRemapper remapper;
   private final Set<String> abstractParams = ConcurrentHashMap.newKeySet();
   private final boolean collectAbstractParams;

   public RenamingTransformer(ClassProvider classProvider, IMappingFile map, Consumer<String> log) {
      this(classProvider, map, log, true);
   }

   public RenamingTransformer(ClassProvider classProvider, IMappingFile map, Consumer<String> log, boolean collectAbstractParams) {
      this(new EnhancedRemapper(classProvider, map, log), collectAbstractParams);
   }

   public RenamingTransformer(EnhancedRemapper remapper, boolean collectAbstractParams) {
      this.collectAbstractParams = collectAbstractParams;
      this.remapper = remapper;
   }

   protected void postProcess(ClassNode node) {
   }

   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry entry) {
      ClassReader reader = new ClassReader(entry.getData());
      ClassNode node = new ClassNode();
      ClassRemapper remapper = new EnhancedClassRemapper(node, this.remapper, this);
      reader.accept(remapper, 0);
      this.postProcess(node);
      ClassWriter writer = new ClassWriter(0);
      node.accept(writer);
      byte[] data = writer.toByteArray();
      String newName = this.remapper.map(entry.getClassName());
      return entry.isMultiRelease()
         ? Transformer.ClassEntry.create(newName, entry.getTime(), data, entry.getVersion())
         : Transformer.ClassEntry.create(newName + ".class", entry.getTime(), data);
   }

   @Override
   public Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
      return "fernflower_abstract_parameter_names.txt".equals(entry.getName()) ? null : entry;
   }

   @Override
   public Collection<? extends Transformer.Entry> getExtras() {
      if (!this.abstractParams.isEmpty() && this.collectAbstractParams) {
         byte[] data = this.abstractParams.stream().sorted().collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8);
         return Collections.singletonList(Transformer.ResourceEntry.create("fernflower_abstract_parameter_names.txt", 946684800L, data));
      } else {
         return Collections.emptyList();
      }
   }

   void storeNames(String className, String methodName, String methodDescriptor, Collection<String> paramNames) {
      this.abstractParams.add(className + ' ' + methodName + ' ' + methodDescriptor + ' ' + paramNames.stream().collect(Collectors.joining(" ")));
   }
}
