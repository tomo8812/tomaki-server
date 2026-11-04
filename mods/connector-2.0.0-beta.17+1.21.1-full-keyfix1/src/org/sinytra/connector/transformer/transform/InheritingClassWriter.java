package org.sinytra.connector.transformer.transform;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.sinytra.adapter.env.ctx.PatchEnvironment;

public class InheritingClassWriter extends ClassWriter {
   private final PatchEnvironment environment;

   public InheritingClassWriter(int flags, PatchEnvironment environment) {
      super(flags);
      this.environment = environment;
   }

   protected String getCommonSuperClass(String type1, String type2) {
      InheritingClassWriter.ClassInfo t1Info = this.getClassInfo(type1);
      InheritingClassWriter.ClassInfo t2Info = this.getClassInfo(type2);
      Set<String> t1Inh = this.getParents(t1Info);
      Set<String> t2Inh = this.getParents(t2Info);
      if (t2Inh.contains(type1)) {
         return type1;
      }

      if (t1Inh.contains(type2)) {
         return type2;
      }

      if (!t1Info.isInterface() && !t2Info.isInterface()) {
         for (String t1Parent : t1Inh) {
            if (t2Inh.contains(t1Parent)) {
               return t1Parent;
            }
         }

         return super.getCommonSuperClass(type1, type2);
      } else {
         return "java/lang/Object";
      }
   }

   private InheritingClassWriter.ClassInfo getClassInfo(String className) {
      Optional<ClassNode> asmNode = this.environment.cleanClassLookup().getClass(className);
      if (asmNode.isPresent()) {
         ClassNode node = asmNode.get();
         return new InheritingClassWriter.ClassInfo(Modifier.isInterface(node.access), node.superName, Objects.requireNonNullElse(node.interfaces, List.of()));
      }

      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      try {
         Class<?> clazz = Class.forName(className.replace('/', '.'), false, classLoader);
         return new InheritingClassWriter.ClassInfo(
            clazz.isInterface(),
            clazz.getSuperclass() == null ? null : Type.getInternalName(clazz.getSuperclass()),
            Arrays.stream(clazz.getInterfaces()).<String>map(Type::getInternalName).toList()
         );
      } catch (ClassNotFoundException e) {
         throw new TypeNotPresentException(className, e);
      }
   }

   private Set<String> getParents(InheritingClassWriter.ClassInfo info) {
      LinkedHashSet<String> parents = new LinkedHashSet<>();
      if (info.superClass() != null) {
         parents.add(info.superClass());
         parents.addAll(this.getParents(this.getClassInfo(info.superClass())));
      }

      for (String itf : info.interfaces()) {
         if (parents.add(itf)) {
            parents.addAll(this.getParents(this.getClassInfo(itf)));
         }
      }

      return parents;
   }

   private record ClassInfo(boolean isInterface, @Nullable String superClass, List<String> interfaces) {
   }
}
