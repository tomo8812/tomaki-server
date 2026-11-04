package reloc.net.minecraftforge.fart.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import reloc.net.minecraftforge.fart.api.ClassProvider;
import reloc.net.minecraftforge.fart.api.Renamer;
import reloc.net.minecraftforge.fart.api.Transformer;
import reloc.net.minecraftforge.srgutils.IMappingFile;

public class RenamerBuilder implements Renamer.Builder {
   private final List<Path> libraries = new ArrayList<>();
   private final List<ClassProvider> classProviders = new ArrayList<>();
   private final List<Transformer.Factory> transformerFactories = new ArrayList<>();
   private int threads = Runtime.getRuntime().availableProcessors();
   private boolean withJvmClasspath = false;
   private Consumer<String> logger = System.out::println;
   private Consumer<String> debug = s -> {};
   private boolean collectAbstractParams = true;
   private final List<String> ignoreJarPathPrefix = new ArrayList<>();

   @Override
   public Renamer.Builder lib(File value) {
      this.libraries.add(value.toPath());
      return this;
   }

   @Override
   public Renamer.Builder lib(Path value) {
      this.libraries.add(value);
      return this;
   }

   @Override
   public Renamer.Builder map(File value) {
      try {
         this.add(Transformer.renamerFactory(IMappingFile.load(value), this.collectAbstractParams));
         return this;
      } catch (IOException e) {
         throw new RuntimeException("Could not map file: " + value.getAbsolutePath(), e);
      }
   }

   @Override
   public Renamer.Builder addClassProvider(ClassProvider classProvider) {
      this.classProviders.add(classProvider);
      return this;
   }

   @Override
   public Renamer.Builder withJvmClasspath() {
      this.withJvmClasspath = true;
      return this;
   }

   @Override
   public Renamer.Builder add(Transformer value) {
      this.transformerFactories.add(Transformer.Factory.always(Objects.requireNonNull(value, "value")));
      return this;
   }

   @Override
   public Renamer.Builder add(Transformer.Factory factory) {
      this.transformerFactories.add(Objects.requireNonNull(factory, "factory"));
      return this;
   }

   @Override
   public Renamer.Builder threads(int value) {
      this.threads = value;
      return this;
   }

   @Override
   public Renamer.Builder logger(Consumer<String> out) {
      this.logger = Objects.requireNonNull(out, "out");
      return this;
   }

   @Override
   public Renamer.Builder debug(Consumer<String> debug) {
      this.debug = Objects.requireNonNull(debug, "debug");
      return this;
   }

   @Override
   public Renamer.Builder setCollectAbstractParams(boolean collectAbstractParams) {
      this.collectAbstractParams = collectAbstractParams;
      return this;
   }

   @Override
   public Renamer.Builder ignoreJarPathPrefix(String... prefixes) {
      this.ignoreJarPathPrefix.addAll(Arrays.asList(prefixes));
      return this;
   }

   @Override
   public Renamer build() {
      List<ClassProvider> classProviders = new ArrayList<>(this.classProviders);
      if (this.withJvmClasspath) {
         classProviders.add(ClassProvider.fromJvmClasspath());
      }

      final SortedClassProvider sortedClassProvider = new SortedClassProvider(classProviders, this.logger);
      Transformer.Context ctx = new Transformer.Context() {
         @Override
         public Consumer<String> getLog() {
            return RenamerBuilder.this.logger;
         }

         @Override
         public Consumer<String> getDebug() {
            return RenamerBuilder.this.debug;
         }

         @Override
         public ClassProvider getClassProvider() {
            return sortedClassProvider;
         }
      };
      List<Transformer> transformers = new ArrayList<>(this.transformerFactories.size());

      for (Transformer.Factory factory : this.transformerFactories) {
         transformers.add(Objects.requireNonNull(factory.create(ctx), "output of " + factory));
      }

      return new RenamerImpl(this.libraries, transformers, sortedClassProvider, classProviders, this.threads, this.logger, this.debug, this.ignoreJarPathPrefix);
   }
}
