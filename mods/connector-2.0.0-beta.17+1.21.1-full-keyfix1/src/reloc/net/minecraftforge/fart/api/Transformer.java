package reloc.net.minecraftforge.fart.api;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;
import reloc.net.minecraftforge.fart.internal.EntryImpl;
import reloc.net.minecraftforge.fart.internal.FFLineFixer;
import reloc.net.minecraftforge.fart.internal.IdentifierFixer;
import reloc.net.minecraftforge.fart.internal.ParameterAnnotationFixer;
import reloc.net.minecraftforge.fart.internal.RecordFixer;
import reloc.net.minecraftforge.fart.internal.RenamingTransformer;
import reloc.net.minecraftforge.fart.internal.SignatureStripperTransformer;
import reloc.net.minecraftforge.fart.internal.SourceFixer;
import reloc.net.minecraftforge.srgutils.IMappingFile;

public interface Transformer {
   default Transformer.ClassEntry process(Transformer.ClassEntry entry) {
      return entry;
   }

   default Transformer.ManifestEntry process(Transformer.ManifestEntry entry) {
      return entry;
   }

   default Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
      return entry;
   }

   default Collection<? extends Transformer.Entry> getExtras() {
      return Collections.emptyList();
   }

   static Transformer.Factory renamerFactory(IMappingFile map, boolean collectAbstractParams) {
      return ctx -> new RenamingTransformer(ctx.getClassProvider(), map, ctx.getLog(), collectAbstractParams);
   }

   @Deprecated
   static Transformer.Factory renamerFactory(IMappingFile map) {
      return renamerFactory(map, true);
   }

   static Transformer.Factory identifierFixerFactory(IdentifierFixerConfig config) {
      return ctx -> new IdentifierFixer(config);
   }

   static Transformer.Factory parameterAnnotationFixerFactory() {
      return ctx -> ParameterAnnotationFixer.INSTANCE;
   }

   static Transformer.Factory fernFlowerLineFixerFactory(File sourceJar) {
      return ctx -> new FFLineFixer(ctx.getDebug(), sourceJar);
   }

   static Transformer.Factory recordFixerFactory() {
      return ctx -> RecordFixer.INSTANCE;
   }

   static Transformer.Factory sourceFixerFactory(SourceFixerConfig config) {
      return ctx -> new SourceFixer(config);
   }

   static Transformer.Factory signatureStripperFactory(SignatureStripperConfig config) {
      return ctx -> new SignatureStripperTransformer(ctx.getLog(), config);
   }

   interface ClassEntry extends Transformer.Entry {
      static Transformer.ClassEntry create(String name, long time, byte[] data) {
         return new EntryImpl.ClassEntry(name, time, data);
      }

      static Transformer.ClassEntry create(String cls, long time, byte[] data, int version) {
         return create("META-INF/versions/" + version + '/' + cls + ".class", time, data);
      }

      String getClassName();

      boolean isMultiRelease();

      int getVersion();
   }

   interface Context {
      Consumer<String> getLog();

      Consumer<String> getDebug();

      ClassProvider getClassProvider();
   }

   interface Entry {
      long STABLE_TIMESTAMP = 946684800L;

      long getTime();

      String getName();

      byte[] getData();

      Transformer.Entry process(Transformer var1);
   }

   interface Factory {
      static Transformer.Factory always(Transformer transformer) {
         Objects.requireNonNull(transformer, "transformer");
         return ctx -> transformer;
      }

      Transformer create(Transformer.Context var1);
   }

   interface ManifestEntry extends Transformer.Entry {
      static Transformer.ManifestEntry create(long time, byte[] data) {
         return new EntryImpl.ManifestEntry(time, data);
      }
   }

   interface ResourceEntry extends Transformer.Entry {
      static Transformer.ResourceEntry create(String name, long time, byte[] data) {
         return new EntryImpl.ResourceEntry(name, time, data);
      }
   }
}
