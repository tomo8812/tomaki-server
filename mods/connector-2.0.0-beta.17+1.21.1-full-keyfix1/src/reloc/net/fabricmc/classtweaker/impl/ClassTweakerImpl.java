package reloc.net.fabricmc.classtweaker.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.objectweb.asm.ClassVisitor;
import reloc.net.fabricmc.classtweaker.api.AccessWidener;
import reloc.net.fabricmc.classtweaker.api.ClassTweaker;
import reloc.net.fabricmc.classtweaker.api.EnumExtension;
import reloc.net.fabricmc.classtweaker.api.InjectedInterface;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;
import reloc.net.fabricmc.classtweaker.classvisitor.AccessWidenerClassVisitor;
import reloc.net.fabricmc.classtweaker.classvisitor.EnumExtensionClassVisitor;
import reloc.net.fabricmc.classtweaker.classvisitor.InterfaceInjectionClassVisitor;

public final class ClassTweakerImpl implements ClassTweaker, ClassTweakerVisitor {
   String namespace;
   final Map<String, AccessWidenerImpl> accessWideners = new HashMap<>();
   final Map<String, List<InjectedInterfaceImpl>> injectedInterfaces = new HashMap<>();
   final Map<String, List<EnumExtensionImpl>> enumExtensions = new HashMap<>();
   final Set<String> targetClasses = new LinkedHashSet<>();
   final Set<String> classes = new LinkedHashSet<>();

   @Override
   public void visitHeader(String namespace) {
      if (this.namespace != null && !this.namespace.equals(namespace)) {
         throw new RuntimeException(String.format("Namespace mismatch, expected %s got %s", this.namespace, namespace));
      }

      this.namespace = namespace;
   }

   @Override
   public AccessWidenerVisitor visitAccessWidener(String owner) {
      AccessWidenerImpl accessWidener = this.accessWideners.get(owner);
      if (accessWidener == null) {
         accessWidener = new AccessWidenerImpl(owner);
         this.accessWideners.put(owner, accessWidener);
         this.addTargets(owner);
      }

      return accessWidener;
   }

   @Override
   public void visitInjectedInterface(String owner, String iface, boolean transitive) {
      List<InjectedInterfaceImpl> injectedInterfaces = this.injectedInterfaces.computeIfAbsent(owner, s -> new ArrayList<>());
      InjectedInterfaceImpl injectedInterface = new InjectedInterfaceImpl(iface);
      injectedInterfaces.add(injectedInterface);
      this.addTargets(owner);
   }

   @Override
   public void visitEnumExtension(String owner, String addedConstant, boolean transitive) {
      List<EnumExtensionImpl> enumExtensions = this.enumExtensions.computeIfAbsent(owner, s -> new ArrayList<>());
      EnumExtensionImpl enumExtension = new EnumExtensionImpl(addedConstant);
      enumExtensions.add(enumExtension);
      this.addTargets(owner);
   }

   private void addTargets(String clazz) {
      this.classes.add(clazz);
      this.targetClasses.add(clazz);

      while (clazz.contains("$")) {
         clazz = clazz.substring(0, clazz.lastIndexOf("$"));
         this.targetClasses.add(clazz);
      }
   }

   @Override
   public ClassVisitor createClassVisitor(int api, @Nullable ClassVisitor classVisitor, @Nullable BiConsumer<String, byte[]> generatedClassConsumer) {
      if (!this.accessWideners.isEmpty()) {
         classVisitor = new AccessWidenerClassVisitor(api, classVisitor, this);
      }

      if (!this.injectedInterfaces.isEmpty()) {
         classVisitor = new InterfaceInjectionClassVisitor(api, classVisitor, this);
      }

      if (!this.enumExtensions.isEmpty()) {
         classVisitor = new EnumExtensionClassVisitor(api, classVisitor, this);
      }

      return classVisitor;
   }

   @Override
   public Set<String> getTargets() {
      return Collections.unmodifiableSet(this.targetClasses);
   }

   @VisibleForTesting
   public Set<String> getClasses() {
      return Collections.unmodifiableSet(this.classes);
   }

   @Override
   public AccessWidener getAccessWidener(String className) {
      AccessWidenerImpl accessWidener = this.accessWideners.get(className);
      return accessWidener == null ? AccessWidenerImpl.DEFAULT : accessWidener;
   }

   @Override
   public Map<String, AccessWidener> getAllAccessWideners() {
      return Collections.unmodifiableMap(this.accessWideners);
   }

   @Override
   public List<InjectedInterface> getInjectedInterfaces(String className) {
      return Collections.unmodifiableList(this.injectedInterfaces.getOrDefault(className, Collections.emptyList()));
   }

   @Override
   public Map<String, List<InjectedInterface>> getAllInjectedInterfaces() {
      return mapValues(this.injectedInterfaces, Collections::unmodifiableList);
   }

   @Override
   public List<EnumExtension> getEnumExtensions(String className) {
      return Collections.unmodifiableList(this.enumExtensions.getOrDefault(className, Collections.emptyList()));
   }

   @Override
   public Map<String, List<EnumExtension>> getAllEnumExtensions() {
      return mapValues(this.enumExtensions, Collections::unmodifiableList);
   }

   @Override
   public String getNamespace() {
      return this.namespace;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.namespace, this.accessWideners, this.targetClasses, this.classes);
   }

   private static <K, V1, V2> Map<K, V2> mapValues(Map<K, V1> map, Function<V1, V2> function) {
      return map.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> function.apply(entry.getValue())));
   }
}
