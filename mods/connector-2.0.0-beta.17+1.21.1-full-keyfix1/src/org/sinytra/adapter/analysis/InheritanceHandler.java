package org.sinytra.adapter.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.sinytra.adapter.util.provider.ClassLookup;

public class InheritanceHandler {
   private final ClassLookup classProvider;
   private final Map<String, Collection<String>> parentCache = new HashMap<>();

   public InheritanceHandler(ClassLookup classProvider) {
      this.classProvider = classProvider;
   }

   public boolean isClassInherited(String child, String parent) {
      if (child.equals(parent)) {
         return true;
      }

      ClassNode childNode = this.classProvider.getClass(child).orElse(null);
      ClassNode parentNode = this.classProvider.getClass(parent).orElse(null);
      return childNode != null && parentNode != null && this.getClassParents(child).contains(parent);
   }

   public Collection<String> getClassParents(String name) {
      Collection<String> parents = this.parentCache.get(name);
      if (parents == null) {
         parents = this.computeClassParents(name);
         this.parentCache.put(name, parents);
      }

      return parents;
   }

   private Collection<String> computeClassParents(String name) {
      ClassNode node = this.classProvider.getClass(name).orElse(null);
      Set<String> parents = new HashSet<>();
      if (node != null) {
         if (node.superName != null) {
            parents.add(node.superName);
            if (!node.superName.equals("java/lang/Object")) {
               parents.addAll(this.getClassParents(node.superName));
            }
         }

         if (node.interfaces != null) {
            parents.addAll(node.interfaces);

            for (String itf : node.interfaces) {
               parents.addAll(this.getClassParents(itf));
            }
         }
      }

      return parents;
   }
}
