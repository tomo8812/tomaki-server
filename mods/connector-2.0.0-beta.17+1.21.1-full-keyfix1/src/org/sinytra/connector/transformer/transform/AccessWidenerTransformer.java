package org.sinytra.connector.transformer.transform;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.loader.impl.MappingResolverImpl;
import org.jetbrains.annotations.Nullable;
import org.sinytra.connector.transformer.jar.IntermediateMapping;
import reloc.net.fabricmc.classtweaker.api.ClassTweakerReader;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;
import reloc.net.minecraftforge.fart.api.Transformer;

public class AccessWidenerTransformer implements Transformer {
   private final String resource;
   private final MappingResolverImpl resolver;
   private final IntermediateMapping fastMapping;

   public AccessWidenerTransformer(String resource, MappingResolverImpl namedMappingFile, IntermediateMapping fastMapping) {
      this.resource = resource;
      this.resolver = namedMappingFile;
      this.fastMapping = fastMapping;
   }

   @Override
   public Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
      if (entry.getName().equals(this.resource)) {
         String content = this.mapAccessWidener(entry.getData());
         return Transformer.ResourceEntry.create("META-INF/accesstransformer.cfg", entry.getTime(), content.getBytes(StandardCharsets.UTF_8));
      } else {
         return entry;
      }
   }

   public String mapAccessWidener(byte[] content) {
      ClassTweakerReader.Header header = ClassTweakerReader.readHeader(content);
      String namespace = header.getNamespace();
      AccessWidenerTransformer.TransformingClassTweakerVisitor visitor = new AccessWidenerTransformer.TransformingClassTweakerVisitor(namespace);
      ClassTweakerReader.create(visitor).read(content);
      visitor.finish();
      return visitor.builder.toString();
   }

   public class TransformingClassTweakerVisitor implements ClassTweakerVisitor {
      private final String sourceNamespace;
      private final StringBuilder builder = new StringBuilder();
      private final Map<String, AccessWidenerVisitor.AccessType> classAccess = new HashMap<>();
      private final Map<String, Map<String, AccessWidenerVisitor.AccessType>> classFields = new HashMap<>();

      public TransformingClassTweakerVisitor(String sourceNamespace) {
         this.sourceNamespace = sourceNamespace;
         this.builder.append("# Access Transformer file converted by Connector\n");
      }

      @Nullable
      @Override
      public AccessWidenerVisitor visitAccessWidener(String owner) {
         return new AccessWidenerTransformer.TransformingClassTweakerVisitor.RemappingAccessWidenerVisitor(owner);
      }

      public void finish() {
         this.classAccess.forEach((name, access) -> {
            String modifier = switch (access) {
               case ACCESSIBLE -> "public";
               case EXTENDABLE -> "public-f";
               default -> throw new IllegalArgumentException("Invalid access type " + access + " for class");
            };
            String mappedName = AccessWidenerTransformer.this.resolver.mapClassName(this.sourceNamespace, name).replace('/', '.');
            this.builder.append(modifier).append(" ").append(mappedName).append("\n");
         });
         this.classFields
            .forEach(
               (owner, fields) -> fields.forEach(
                  (name, access) -> {
                     String modifier = switch (access) {
                        case ACCESSIBLE -> "public";
                        case MUTABLE -> "public-f";
                        default -> throw new IllegalArgumentException("Invalid access type " + access + " for field");
                     };
                     String mappedOwner = AccessWidenerTransformer.this.resolver.mapClassName(this.sourceNamespace, owner);
                     String mappedName = AccessWidenerTransformer.this.resolver.mapFieldName(this.sourceNamespace, owner, name, "");
                     this.builder
                        .append(modifier)
                        .append(" ")
                        .append(mappedOwner.replace('/', '.'))
                        .append(" ")
                        .append(mappedName)
                        .append(!name.equals(mappedName) ? " # " + mappedName : "")
                        .append("\n");
                  }
               )
            );
      }

      public class RemappingAccessWidenerVisitor implements AccessWidenerVisitor {
         private final String owner;

         public RemappingAccessWidenerVisitor(String owner) {
            this.owner = owner;
         }

         @Override
         public void visitClass(AccessWidenerVisitor.AccessType access, boolean transitive) {
            TransformingClassTweakerVisitor.this.classAccess
               .compute(
                  this.owner,
                  (value, existing) -> (AccessWidenerVisitor.AccessType)(existing != null && access.ordinal() <= existing.ordinal() ? existing : access)
               );
         }

         @Override
         public void visitMethod(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
            String modifier = switch (access) {
               case ACCESSIBLE -> "public";
               case EXTENDABLE -> "protected-f";
               default -> throw new IllegalArgumentException("Invalid access type " + access + " for method");
            };
            String mappedOwner = AccessWidenerTransformer.this.resolver.mapClassName(TransformingClassTweakerVisitor.this.sourceNamespace, this.owner);
            String mappedName = AccessWidenerTransformer.this.resolver
               .mapMethodName(TransformingClassTweakerVisitor.this.sourceNamespace, this.owner, name, descriptor);
            if (name.equals(mappedName)) {
               mappedName = AccessWidenerTransformer.this.fastMapping.mapMethodOrDefault(name, descriptor);
            }

            String mappedDescriptor = AccessWidenerTransformer.this.resolver.mapDescriptor(TransformingClassTweakerVisitor.this.sourceNamespace, descriptor);
            TransformingClassTweakerVisitor.this.builder
               .append(modifier)
               .append(" ")
               .append(mappedOwner.replace('/', '.'))
               .append(" ")
               .append(mappedName)
               .append(mappedDescriptor)
               .append(!name.equals(mappedName) ? " # " + mappedName : "")
               .append("\n");
            this.visitClass(access, false);
         }

         @Override
         public void visitField(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
            TransformingClassTweakerVisitor.this.classFields
               .computeIfAbsent(this.owner, n -> new HashMap<>())
               .compute(
                  name, (value, existing) -> (AccessWidenerVisitor.AccessType)(existing != null && access.ordinal() <= existing.ordinal() ? existing : access)
               );
            if (access != AccessWidenerVisitor.AccessType.MUTABLE) {
               this.visitClass(access, false);
            }
         }
      }
   }
}
