package org.sinytra.connector.transformer.transform;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.analysis.selector.AnnotationValueHandle;
import org.sinytra.adapter.util.MethodQualifier;
import org.sinytra.connector.transformer.jar.IntermediateMapping;
import org.spongepowered.asm.mixin.gen.AccessorInfo.AccessorName;
import reloc.net.minecraftforge.fart.api.ClassProvider;
import reloc.net.minecraftforge.fart.internal.ClassProviderImpl;
import reloc.net.minecraftforge.fart.internal.EnhancedClassRemapper;
import reloc.net.minecraftforge.fart.internal.EnhancedRemapper;
import reloc.net.minecraftforge.fart.internal.RenamingTransformer;
import reloc.net.minecraftforge.srgutils.IMappingFile;

public final class OptimizedRenamingTransformer extends RenamingTransformer {
   private static final String CLASS_DESC_PATTERN = "^L[a-zA-Z0-9/$_]+;$";
   private static final String FQN_CLASS_NAME_PATTERN = "^(?:[a-zA-Z0-9$_]+\\.)*[a-zA-Z0-9$_]+$";
   private static final String INTERNAL_CLASS_NAME_PATTERN = "^(?:[a-zA-Z0-9$_]+/)*[a-zA-Z0-9$_]+$";
   private static final Pattern FIELD_QUALIFIER_PATTERN = Pattern.compile(
      "^(?<owner>L[\\\\\\w/$]+;)?(?<name>\\w+)(?::(?<desc>\\[*[ZCBSIFJD]|\\[*L[a-zA-Z0-9/_$]+;))?$"
   );
   private static final String ACCESSOR_METHOD_PATTERN = "^.*(Method_|Field_|Comp_).*$";
   private static final Pattern REGEX_DESC_PATTERN = Pattern.compile("^.*(net\\\\/minecraft\\\\/class_\\d{4}).*$");
   private final boolean remapRefs;

   public OptimizedRenamingTransformer(EnhancedRemapper remapper, boolean collectAbstractParams, boolean remapRefs) {
      super(remapper, collectAbstractParams);
      this.remapRefs = remapRefs;
   }

   @Override
   protected void postProcess(ClassNode node) {
      super.postProcess(node);
      OptimizedRenamingTransformer.PostProcessRemapper postProcessRemapper = new OptimizedRenamingTransformer.PostProcessRemapper(
         ((OptimizedRenamingTransformer.MixinAwareEnhancedRemapper)this.remapper).flatMappings, this.remapper
      );
      if (node.visibleAnnotations != null) {
         for (AnnotationNode annotation : node.visibleAnnotations) {
            postProcessRemapper.mapAnnotationValues(annotation.values);
         }
      }

      if (node.invisibleAnnotations != null) {
         for (AnnotationNode annotation : node.invisibleAnnotations) {
            postProcessRemapper.mapAnnotationValues(annotation.values);
         }
      }

      for (MethodNode method : node.methods) {
         if (method.visibleAnnotations != null) {
            for (AnnotationNode annotation : method.visibleAnnotations) {
               this.processMixinAnnotation(annotation, postProcessRemapper);
            }
         }

         for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof LdcInsnNode ldc) {
               ldc.cst = postProcessRemapper.mapValue(ldc.cst);
            }

            if (insn instanceof InvokeDynamicInsnNode indy) {
               for (int i = 0; i < indy.bsmArgs.length; i++) {
                  indy.bsmArgs[i] = postProcessRemapper.mapValue(indy.bsmArgs[i]);
                  indy.bsm = (Handle)postProcessRemapper.mapValue(indy.bsm);
               }
            }
         }

         this.avoidAmbigousMappingRecursion(node, method);
      }

      for (FieldNode field : node.fields) {
         field.value = postProcessRemapper.mapValue(field.value);
      }
   }

   private void avoidAmbigousMappingRecursion(ClassNode classNode, MethodNode method) {
      int parentMethods = this.countAmbigousOverridenMethods(classNode, method);
      if (parentMethods > 1) {
         for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode minsn
               && minsn.getOpcode() == 182
               && minsn.owner.equals(classNode.name)
               && minsn.name.equals(method.name)
               && minsn.desc.equals(method.desc)) {
               List<AbstractInsnNode> insns = MethodCallAnalyzer.getMethodCallInsns(method, minsn);
               if (insns != null && !insns.isEmpty() && insns.getFirst() instanceof VarInsnNode varInsn && varInsn.var == 0) {
                  method.instructions.set(minsn, new MethodInsnNode(183, classNode.superName, minsn.name, minsn.desc, minsn.itf));
               }
            }
         }
      }

      if (parentMethods > 0) {
         int i = 1;

         for (MethodNode m : classNode.methods) {
            if (m != method && m.name.equals(method.name) && m.desc.equals(method.desc)) {
               m.name = m.name + "$connector_disabled$" + i;
            }
         }
      }
   }

   private int countAmbigousOverridenMethods(ClassNode classNode, MethodNode method) {
      return classNode.superName != null
         ? this.remapper
            .getClass(classNode.name)
            .map(
               c -> (int)c.getMethods()
                  .stream()
                  .flatMap(Optional::stream)
                  .filter(
                     m -> !m.getName().equals(m.getMapped())
                        && m.getMapped().equals(method.name)
                        && method.desc.equals(this.remapper.mapMethodDesc(m.getDescriptor()))
                        && (m.getAccess() & 10) == 0
                  )
                  .count()
            )
            .orElse(0)
         : 0;
   }

   private void processMixinAnnotation(AnnotationNode annotation, OptimizedRenamingTransformer.PostProcessRemapper postProcessRemapper) {
      AnnotationHandle handle = new AnnotationHandle(annotation);
      handle.getValue("method")
         .map(AnnotationValueHandle::get)
         .filter(list -> list.size() == 1 && ((String)list.getFirst()).startsWith("desc="))
         .ifPresent(h -> {
            String method = (String)h.getFirst();
            Matcher matcher = REGEX_DESC_PATTERN.matcher(method);
            if (matcher.matches()) {
               String className = matcher.group(1);
               String mapped = ((OptimizedRenamingTransformer.MixinAwareEnhancedRemapper)this.remapper).flatMappings.map(className.replace("\\/", "/"));
               if (mapped != null) {
                  String mappedClassName = mapped.replace("/", "\\/");
                  String mappedMethod = method.replace(className, mappedClassName);
                  h.set(0, mappedMethod);
               }
            }
         });
      if (this.remapRefs || handle.getValue("remap").map(h -> !(Boolean)h.get()).orElse(false)) {
         postProcessRemapper.mapAnnotationValues(annotation.values);
      }
   }

   public static final class IntermediaryClassProvider implements ClassProvider {
      private final ClassProvider upstream;
      private final IMappingFile forwardMapping;
      private final EnhancedRemapper remapper;
      private final Map<String, Optional<ClassProvider.IClassInfo>> classCache = new ConcurrentHashMap<>();

      public IntermediaryClassProvider(ClassProvider upstream, IMappingFile forwardMapping, IMappingFile reverseMapping, Consumer<String> log) {
         this.upstream = upstream;
         this.forwardMapping = forwardMapping;
         this.remapper = new EnhancedRemapper(upstream, reverseMapping, log);
      }

      @Override
      public Optional<? extends ClassProvider.IClassInfo> getClass(String s) {
         return this.classCache.computeIfAbsent(s, this::computeClassInfo).or(() -> this.upstream.getClass(s));
      }

      @Override
      public Optional<byte[]> getClassBytes(String cls) {
         return this.upstream.getClassBytes(this.forwardMapping.remapClass(cls));
      }

      private Optional<ClassProvider.IClassInfo> computeClassInfo(String cls) {
         return this.getClassBytes(cls).map(data -> {
            ClassReader reader = new ClassReader(data);
            ClassWriter writer = new ClassWriter(0);
            ClassRemapper remapper = new EnhancedClassRemapper(writer, this.remapper, null);
            OptimizedRenamingTransformer.MixinTargetAnalyzer analyzer = new OptimizedRenamingTransformer.MixinTargetAnalyzer(589824, remapper);
            reader.accept(analyzer, 1);
            analyzer.targets.remove(cls);
            byte[] remapped = writer.toByteArray();
            ClassProvider.IClassInfo info = new ClassProviderImpl.ClassInfo(remapped);
            return !analyzer.targets.isEmpty() ? new OptimizedRenamingTransformer.MixinClassInfo(info, analyzer.targets) : info;
         });
      }

      @Override
      public void close() throws IOException {
         this.upstream.close();
      }
   }

   private static class MixinAnnotationVisitor extends AnnotationVisitor {
      private final Set<String> targets;
      private final String attributeName;

      public MixinAnnotationVisitor(int api, AnnotationVisitor annotationVisitor, Set<String> targets, String attributeName) {
         super(api, annotationVisitor);
         this.targets = targets;
         this.attributeName = attributeName;
      }

      public void visit(String name, Object value) {
         super.visit(name, value);
         if ("value".equals(this.attributeName) && value instanceof Type type) {
            this.targets.add(type.getInternalName());
         }
      }

      public AnnotationVisitor visitArray(String name) {
         return new OptimizedRenamingTransformer.MixinAnnotationVisitor(this.api, super.visitArray(name), this.targets, name);
      }
   }

   public static class MixinAwareEnhancedRemapper extends EnhancedRemapper {
      private final IntermediateMapping flatMappings;

      public MixinAwareEnhancedRemapper(ClassProvider classProvider, IMappingFile map, IntermediateMapping flatMappings, Consumer<String> log) {
         super(classProvider, map, log);
         this.flatMappings = flatMappings;
      }

      @Override
      public String map(String key) {
         String fastMapped = this.flatMappings.map(key);
         return fastMapped != null ? fastMapped : super.map(key);
      }

      @Override
      public String mapFieldName(String owner, String name, String descriptor) {
         String fastMapped = this.flatMappings.mapField(name, descriptor);
         return fastMapped != null ? fastMapped : this.classProvider.getClass(owner).map(cls -> {
            if (cls instanceof OptimizedRenamingTransformer.MixinClassInfo mcls) {
               for (String parent : mcls.computedParents()) {
                  String mapped = super.mapFieldName(parent, name, descriptor);
                  if (!name.equals(mapped)) {
                     return mapped;
                  }
               }
            }

            return null;
         }).orElseGet(() -> super.mapFieldName(owner, name, descriptor));
      }

      @Override
      public String mapMethodName(String owner, String name, String descriptor) {
         String fastMapped = this.flatMappings.mapMethod(name, descriptor);
         return fastMapped != null ? fastMapped : this.classProvider.getClass(owner).map(cls -> {
            if (cls instanceof OptimizedRenamingTransformer.MixinClassInfo && !name.startsWith("lambda$")) {
               int interfacePrefix = name.indexOf("$");
               if (interfacePrefix > -1 && name.lastIndexOf("$") == interfacePrefix) {
                  String actualName = name.substring(interfacePrefix + 1);
                  String fastMappedLambda = this.flatMappings.mapMethod(actualName, descriptor);
                  String mapped = fastMappedLambda != null ? fastMappedLambda : this.mapMethodName(owner, actualName, descriptor);
                  return name.substring(0, interfacePrefix + 1) + mapped;
               }

               if (name.matches("^.*(Method_|Field_|Comp_).*$")) {
                  AccessorName accessorName = AccessorName.of(name);
                  if (accessorName != null) {
                     String mapped = this.flatMappings.mapMethod(accessorName.name, descriptor);
                     if (mapped != null) {
                        return accessorName.prefix + mapped.substring(0, 1).toUpperCase() + mapped.substring(1);
                     }
                  }
               }
            }

            return null;
         }).orElseGet(() -> super.mapMethodName(owner, name, descriptor));
      }

      @Override
      public String mapPackageName(String name) {
         return name;
      }
   }

   private record MixinClassInfo(ClassProvider.IClassInfo wrapped, Set<String> computedParents) implements ClassProvider.IClassInfo {
      @Override
      public Collection<String> getInterfaces() {
         return Stream.concat(this.wrapped.getInterfaces().stream(), this.computedParents.stream()).toList();
      }

      @Override
      public int getAccess() {
         return this.wrapped.getAccess();
      }

      @Override
      public String getName() {
         return this.wrapped.getName();
      }

      @Nullable
      @Override
      public String getSuper() {
         return this.wrapped.getSuper();
      }

      @Override
      public Collection<? extends ClassProvider.IFieldInfo> getFields() {
         return this.wrapped.getFields();
      }

      @Override
      public Optional<? extends ClassProvider.IFieldInfo> getField(String name) {
         return this.wrapped.getField(name);
      }

      @Override
      public Collection<? extends ClassProvider.IMethodInfo> getMethods() {
         return this.wrapped.getMethods();
      }

      @Override
      public Optional<? extends ClassProvider.IMethodInfo> getMethod(String name, String desc) {
         return this.wrapped.getMethod(name, desc);
      }
   }

   private static class MixinTargetAnalyzer extends ClassVisitor {
      private final Set<String> targets = new HashSet<>();

      public MixinTargetAnalyzer(int api, ClassVisitor classVisitor) {
         super(api, classVisitor);
      }

      public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
         return new OptimizedRenamingTransformer.MixinAnnotationVisitor(this.api, super.visitAnnotation(descriptor, visible), this.targets, null);
      }
   }

   private record PostProcessRemapper(IntermediateMapping flatMappings, Remapper remapper) {
      public void mapAnnotationValues(List values) {
         if (values != null) {
            for (int i = 1; i < values.size(); i += 2) {
               values.set(i, this.mapAnnotationValue(values.get(i)));
            }
         }
      }

      public Object mapAnnotationValue(Object obj) {
         if (obj instanceof AnnotationNode annotation) {
            this.mapAnnotationValues(annotation.values);
         } else {
            if (!(obj instanceof List list)) {
               return this.mapValue(obj);
            }

            list.replaceAll(this::mapAnnotationValue);
         }

         return obj;
      }

      public Object mapValue(Object value) {
         if (value instanceof String str) {
            if (str.matches("^L[a-zA-Z0-9/$_]+;$")) {
               String mapped = this.flatMappings.map(str.substring(1, str.length() - 1));
               if (mapped != null) {
                  return "L" + mapped + ";";
               }
            } else if (str.matches("^(?:[a-zA-Z0-9$_]+\\.)*[a-zA-Z0-9$_]+$")) {
               String mapped = this.flatMappings.map(str.replace('.', '/'));
               if (mapped != null) {
                  return mapped.replace('/', '.');
               }
            } else if (str.matches("^(?:[a-zA-Z0-9$_]+/)*[a-zA-Z0-9$_]+$")) {
               String mapped = this.flatMappings.map(str);
               if (mapped != null) {
                  return mapped;
               }
            }

            MethodQualifier qualifier = MethodQualifier.parse(str).orElse(null);
            if (qualifier != null && qualifier.desc() != null) {
               String owner = qualifier.owner() != null ? this.remapper.mapDesc(qualifier.owner()) : "";
               String name = qualifier.name() != null ? this.flatMappings.mapMethodOrDefault(qualifier.name(), qualifier.desc()) : "";
               String desc = this.remapper.mapMethodDesc(qualifier.desc());
               return owner + name + desc;
            }

            Matcher fieldMatcher = OptimizedRenamingTransformer.FIELD_QUALIFIER_PATTERN.matcher(str);
            if (fieldMatcher.matches()) {
               String owner = fieldMatcher.group("owner");
               String name = fieldMatcher.group("name");
               String desc = fieldMatcher.group("desc");
               if (owner != null || name != null && (name.startsWith("field_") || name.startsWith("comp_"))) {
                  String mappedOwner = owner != null ? this.remapper.mapDesc(owner) : "";
                  String mappedName = name != null ? this.flatMappings.mapField(name, desc != null ? desc : "") : "";
                  return mappedOwner + mappedName + (desc != null ? ":" + this.remapper.mapDesc(desc) : "");
               }
            }

            String mapped = this.flatMappings.map(str);
            if (mapped != null) {
               return mapped;
            }
         }

         return this.remapper.mapValue(value);
      }
   }
}
