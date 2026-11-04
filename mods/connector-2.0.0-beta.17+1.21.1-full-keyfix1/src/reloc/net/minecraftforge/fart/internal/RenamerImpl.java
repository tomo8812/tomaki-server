package reloc.net.minecraftforge.fart.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import reloc.net.minecraftforge.fart.api.ClassProvider;
import reloc.net.minecraftforge.fart.api.Renamer;
import reloc.net.minecraftforge.fart.api.Transformer;

class RenamerImpl implements Renamer {
   static final int MAX_ASM_VERSION = 589824;
   private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
   private final List<Path> libraries;
   private final List<Transformer> transformers;
   private final SortedClassProvider sortedClassProvider;
   private final List<ClassProvider> classProviders;
   private final int threads;
   private final Consumer<String> logger;
   private final Consumer<String> debug;
   private final List<String> ignoreJarPathPrefix;
   private boolean setup = false;
   private ClassProvider libraryClasses;

   RenamerImpl(
      List<Path> libraries,
      List<Transformer> transformers,
      SortedClassProvider sortedClassProvider,
      List<ClassProvider> classProviders,
      int threads,
      Consumer<String> logger,
      Consumer<String> debug,
      List<String> ignoreJarPathPrefix
   ) {
      this.libraries = libraries;
      this.transformers = transformers;
      this.sortedClassProvider = sortedClassProvider;
      this.classProviders = Collections.unmodifiableList(classProviders);
      this.threads = threads;
      this.logger = logger;
      this.debug = debug;
      this.ignoreJarPathPrefix = ignoreJarPathPrefix;
   }

   private void setup() {
      if (!this.setup) {
         this.setup = true;
         ClassProvider.Builder libraryClassesBuilder = ClassProvider.builder().shouldCacheAll(true);
         this.logger.accept("Adding Libraries to Inheritance");
         this.libraries.forEach(libraryClassesBuilder::addLibrary);
         this.libraryClasses = libraryClassesBuilder.build();
      }
   }

   @Override
   public void run(File input, File output) {
      if (!this.setup) {
         this.setup();
      }

      input = Objects.requireNonNull(input).getAbsoluteFile();
      output = Objects.requireNonNull(output).getAbsoluteFile();
      if (!input.exists()) {
         throw new IllegalArgumentException("Input file not found: " + input.getAbsolutePath());
      }

      this.logger.accept("Reading Input: " + input.getAbsolutePath());
      List<Transformer.Entry> oldEntries = new ArrayList<>();
      List<Transformer.Entry> excludedEntries = new ArrayList<>();

      try (ZipFile in = new ZipFile(input)) {
         Util.forZip(in, ex -> {
            if (!ex.isDirectory()) {
               String namex = ex.getName();
               byte[] data = Util.toByteArray(in.getInputStream(ex));
               if (namex.endsWith(".class")) {
                  oldEntries.add(Transformer.ClassEntry.create(namex, ex.getTime(), data));
               } else if (namex.equals("META-INF/MANIFEST.MF")) {
                  oldEntries.add(Transformer.ManifestEntry.create(ex.getTime(), data));
               } else if (this.ignoreJarPathPrefix.stream().anyMatch(namex::startsWith)) {
                  excludedEntries.add(Transformer.ResourceEntry.create(namex, ex.getTime(), data));
               } else {
                  oldEntries.add(Transformer.ResourceEntry.create(namex, ex.getTime(), data));
               }
            }
         });
      } catch (IOException e) {
         throw new RuntimeException("Could not parse input: " + input.getAbsolutePath(), e);
      }

      this.sortedClassProvider.clearCache();
      ArrayList<ClassProvider> classProviders = new ArrayList<>(this.classProviders);
      classProviders.add(0, this.libraryClasses);
      this.sortedClassProvider.classProviders = classProviders;
      AsyncHelper async = new AsyncHelper(this.threads);

      try {
         List<Transformer.ClassEntry> ourClasses = oldEntries.stream()
            .filter(ex -> ex instanceof Transformer.ClassEntry && !ex.getName().startsWith("META-INF/"))
            .map(Transformer.ClassEntry.class::cast)
            .collect(Collectors.toList());
         this.logger.accept("Adding input to inheritance map");
         ClassProvider.Builder inputClassesBuilder = ClassProvider.builder();
         async.consumeAll(
            ourClasses,
            Transformer.ClassEntry::getClassName,
            c -> inputClassesBuilder.addClass(c.getName().substring(0, c.getName().length() - 6), c.getData())
         );
         classProviders.add(0, inputClassesBuilder.build());
         this.logger.accept("Processing entries");
         List<Transformer.Entry> newEntries = async.invokeAll(oldEntries, Transformer.Entry::getName, this::processEntry);
         newEntries.addAll(excludedEntries);
         this.logger.accept("Adding extras");
         this.transformers.stream().forEach(t -> newEntries.addAll(t.getExtras()));
         Set<String> seen = new HashSet<>();
         String dupes = newEntries.stream().map(Transformer.Entry::getName).filter(n -> !seen.add(n)).sorted().collect(Collectors.joining(", "));
         if (!dupes.isEmpty()) {
            throw new IllegalStateException("Duplicate entries detected: " + dupes);
         }

         this.logger.accept("Sorting");
         Collections.sort(newEntries, this::compare);
         if (!output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
         }

         seen.clear();
         this.logger.accept("Writing Output: " + output.getAbsolutePath());

         try (
            FileOutputStream fos = new FileOutputStream(output);
            ZipOutputStream zos = new ZipOutputStream(fos);
         ) {
            for (Transformer.Entry e : newEntries) {
               String name = e.getName();
               int idx = name.lastIndexOf(47);
               if (idx != -1) {
                  this.addDirectory(zos, seen, name.substring(0, idx));
               }

               this.logger.accept("  " + name);
               ZipEntry entry = new ZipEntry(name);
               entry.setTime(e.getTime());
               zos.putNextEntry(entry);
               zos.write(e.getData());
               zos.closeEntry();
            }
         } catch (IOException e) {
            throw new RuntimeException("Could not write output to file: " + output.getAbsolutePath(), e);
         }
      } finally {
         async.shutdown();
      }
   }

   private void addDirectory(ZipOutputStream zos, Set<String> seen, String path) throws IOException {
      if (seen.add(path)) {
         int idx = path.lastIndexOf(47);
         if (idx != -1) {
            this.addDirectory(zos, seen, path.substring(0, idx));
         }

         this.logger.accept("  " + path + '/');
         ZipEntry dir = new ZipEntry(path + '/');
         dir.setTime(946684800L);
         zos.putNextEntry(dir);
         zos.closeEntry();
      }
   }

   private Transformer.Entry processEntry(Transformer.Entry start) {
      Transformer.Entry entry = start;

      for (Transformer transformer : this.transformers) {
         entry = entry.process(transformer);
         if (entry == null) {
            return null;
         }
      }

      return entry;
   }

   private int compare(Transformer.Entry o1, Transformer.Entry o2) {
      if ("META-INF/MANIFEST.MF".equals(o1.getName())) {
         return "META-INF/MANIFEST.MF".equals(o2.getName()) ? 0 : -1;
      } else if ("META-INF/MANIFEST.MF".equals(o2.getName())) {
         return "META-INF/MANIFEST.MF".equals(o1.getName()) ? 0 : 1;
      } else {
         return o1.getName().compareTo(o2.getName());
      }
   }

   @Override
   public void close() throws IOException {
      this.sortedClassProvider.close();
   }
}
